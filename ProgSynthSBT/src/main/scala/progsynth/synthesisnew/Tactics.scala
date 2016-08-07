package progsynth.synthesisnew
import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import SynthUtils._
import PSTacticsHelper._
import scala.util.{Try, Success, Failure}

abstract class TacticError
case class TacticNotApplicable(msg: String) extends TacticError
case class TacticApplicationFailed(msg: String) extends TacticError
case class TacticException(msg: String) extends TacticError
case class TacticInputParseError(msg: String) extends TacticError

/** A abstract class to be implemented by all the tactics */
abstract class Tactic {
	/** Given a SynthNode, returns a new node resulted from the application of this tactic */
	def getNextNode(node: SynthNode, idOpt: Option[Int]): Either[ TacticError, SynthNode]

	/** Returns and html element describing the action of the tactic application */
	def getHint(): Elem

	/** Returns the relation between a SynthNode and the new SynthNode created by application of this tactic.
	 *  For program nodes, it returns None (program refinement relationship is implicit) */
	def getRelation(): Option[Fn]

	def tName: String

	def printException(ex: Throwable) = {
		println("Exception in " + this.toString + " tactic " + "\n"
        + "MSG:" + ex.getMessage() + "\n" +
        "Stack Trace:" + "\n" +
        ex.getStackTrace().map{"    " + _.toString()}.mkString("\n") )
	}
}


/** init tactic for directly specifying a hoare triple */
abstract class InitTacticHoareAbs extends Tactic {

	def initNodeAndFrame(rootFrame: Frame): (CalcStep, Frame)

	/** If the current node is a root node, call the initNodeAndFrame function to set the node and frame.
	 *  Exceptions thrown by 'initNodeAndFrame' application are caught and captured as TacticApplicationFailed error */
	def getNextNode(node: SynthNode, idOpt: Option[Int]): Either[ TacticError, SynthNode] = {
		try{
			if (node.isRoot) {
				val (nextNodeObj, frame) = initNodeAndFrame(node.frame)
				val synthNode = new SynthNode(tactic = this, nodeObject = nextNodeObj,
											childs = Nil, parent = node, frame = frame,
											synthTree = node.synthTree, idOpt)
				Right(synthNode)
			} else {
				Left(TacticNotApplicable("Tactic not applicable. Check the applicability conditions"))
			}
		} catch {
			case ex: Throwable =>
				printException(ex)
				Left(TacticApplicationFailed(ex.getMessage()))
		}
	}
}

abstract class FunTactic() extends Tactic {

	/** Abstract function.
	 *  This partial function needs to be implemented by all the tactics implementing FunTactic.
	 *  'fun' is called from  the getNextNode method to get the next SynthNode */
	//def fun: (Any, Frame) ==> Any

	def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep]

	/** Call fun on the input node to get the next node.
	 *  Exceptions thrown by 'fun' application are caught and captured as TacticApplicationFailed error */
	def getNextNode(node: SynthNode, idOpt: Option[Int]): Either[ TacticError, SynthNode] = {
		try{
			val nextNodeObjT = fun(node.nodeObj, node.frame)

			nextNodeObjT match {
			    case Success(nextNodeObj) =>
			        val newSynthNode = new SynthNode(
        				tactic = this,
        				nodeObject = nextNodeObj,
        				childs = Nil,
        				parent = node,
        				frame = node.frame, synthTree = node.synthTree, idOpt)

        			Right(newSynthNode)
			    case Failure(e) => throw e
			}
		} catch {
			case ex: Throwable =>
				printException(ex)
				Left(TacticApplicationFailed(ex.getMessage()))
		}
	}
}

abstract class PrgFunTactic() extends FunTactic {

    private def optToTry[T](xOpt: Option[T], msg: String) = xOpt match {
		case Some(x) => Success(x)
		case None => Failure(new Exception(msg))
	}

    def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn]

    def replaceSubProgDisplayId2(aProg: ProgramAnn, focusId: Int, innerProg: ProgramAnn): Option[ProgramAnn] = {
        for {
            newProg <- replaceSubProgDisplayId(aProg, focusId, innerProg)
            newProg2 <- newProg.setDisplayIdAll()
        } yield {
            newProg2.asInstanceOf[ProgramAnn]
        }
    }

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = {
	    nodeObj match {
	        case CalcProgStep(bProg, Some(focusId2)) =>
	            for{
	                f_bProg <- optToTry(extractSubProgDisplayId(bProg, focusId2), "ERROR1") //TODO: Fix error msg
	                f_cProg <- progFun(f_bProg, frame)
	                cProg <- optToTry(replaceSubProgDisplayId2(bProg, focusId2, f_cProg), "ERROR2") //TODO: Fix error msg
	            } yield {
	                CalcProgStep(cProg, Some(focusId2))
	            }
	        case CalcProgStep(bProg, None) =>
	            for{
	                f_bProg <- Success(bProg)
	                f_cProg <- progFun(f_bProg, frame)
	                cProg <- Success(f_cProg)
	            } yield {
	                CalcProgStep(cProg, None)
	            }
	    }
	}
}

abstract class StepInTactic() extends Tactic {
	/** Abstract function.
	 *  This partial function needs to be implemented by all the tactics implementing StepInTactic.
	 *  'funIn' is called from  the getNextNode method to get the next SynthNode.
	 *  'funIn' function also creates a new frame.
	 *  (outerObj, outerFrame) |-> (innerObj, innerFrame) */
	//def funIn: (Any, Frame) ==> (Any, Frame)
	/*Returns inner object and inner frame*/ //TODO: Update doc comment
	def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)]

	/** Abstract function.
	 *  This partial function needs to be implemented by all the tactics implementing StepInTactic.
	 *  'funOut' is called by the StepOutTactic's getNextNode method to get the next SynthNode
	 *  ((oldOuterObj, outerFrame) |-> newInnerObj) |-> newOuterObj) */
	//def funOut: (Any, Frame) ==> (Any ==> Any)
	//TODO: Update doc comment
	/* Returns new outer object */
	def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep]

	/** Call fun on the input node to get the next node.
	 *  Exceptions thrown by 'fun' application are caught and captured as TacticApplicationFailed error */
	def getNextNode(node: SynthNode, idOpt: Option[Int]): Either[ TacticError, SynthNode] = {
		funIn(node.nodeObj, node.frame) match {
		    case Success((nextNodeObj, frame)) =>
				val synthNode = new SynthNode(tactic = this, nodeObject = nextNodeObj,
											childs = Nil, parent = node, frame = frame,
											synthTree = node.synthTree, idOpt)
				Right(synthNode)
		    case Failure(e) =>
		        printException(e)
		        Left(TacticApplicationFailed(e.getMessage))
		}
	}
}

abstract class PrgStepInTactic() extends StepInTactic {
    def getInnerProg(pa: ProgramAnn): Option[ProgramAnn]

	def prgFunIn(outerFocusedProg: ProgramAnn, outerFrame: Frame): Try[(ProgramAnn, Frame)]

	def prgFunOut(oldFocusedProg: ProgramAnn, oldFrame: Frame)(newInnerProg: ProgramAnn): Try[ProgramAnn]

	private def optToTry[T](xOpt: Option[T], msg: String) = xOpt match {
		case Some(x) => Success(x)
		case None => Failure(new Exception(msg))
	}

    def replaceSubProgDisplayId2(aProg: ProgramAnn, focusId: Int, innerProg: ProgramAnn): Option[ProgramAnn] = {
        for {
            newProg <- replaceSubProgDisplayId(aProg, focusId, innerProg)
            newProg2 <- newProg.setDisplayIdAll()
        } yield {
            newProg2.asInstanceOf[ProgramAnn]
        }
    }

	override def funIn(nodeObj: CalcStep, frame: Frame): Try[(CalcStep, Frame)] = (nodeObj, frame) match {
        case (CalcProgStep(aProg, Some(focusId1)), frame1: ProgramFrame) => {
            for {
                f_aProg <- optToTry(extractSubProgDisplayId(aProg, focusId1), "ERROR1")
                (f_bProg, frame2) <- prgFunIn(f_aProg, frame1)
                focusId2 = f_bProg.displayId
                bProg = aProg
            } yield {
                (CalcProgStep(bProg, Some(focusId2)), frame2)
            }
		}
        case (CalcProgStep(aProg, None), frame1: ProgramFrame) => {
            for {
                f_aProg <- Success(aProg)
                (f_bProg, frame2) <- prgFunIn(f_aProg, frame1)
                focusId2 = f_bProg.displayId
                bProg = aProg
            } yield {
                (CalcProgStep(bProg, Some(f_bProg.displayId)), frame2)
            }
		}
        case _ =>
            Failure(new RuntimeException("Tactic not applicable"))
    }

	override def funOut(oldObj: CalcStep, oldFrame: Frame)(newObj: CalcStep): Try[CalcStep] = (oldObj, oldFrame) match {
	    case (CalcProgStep(aProg, Some(focusId1)), frame1) =>
	        newObj match {
	            case (CalcProgStep(cProg, Some(focusId2))) =>
            	    for {
            	        f_aProg <- optToTry(extractSubProgDisplayId(aProg, focusId1), "ERROR")
            	        f_cProg <- optToTry(extractSubProgDisplayId(cProg, focusId2), "ERROR")
            	        f_dProg <- prgFunOut(f_aProg, frame1)(f_cProg)
            	        dProg <- optToTry(replaceSubProgDisplayId2(aProg, focusId1, f_dProg), "ERROR")
            	    } yield {
            	        CalcProgStep(dProg, Some(focusId1))
            	    }
            	case (CalcProgStep(newProg, None)) =>
            	    //This case should never occur
            	    throw new RuntimeException("Stepout failed.")
	        }
	    case (CalcProgStep(aProg, None), frame1) =>
	        newObj match {
	            case (CalcProgStep(cProg, Some(focusId2))) =>
            	    for {
            	        f_aProg <- Success(aProg)
            	        f_cProg <- optToTry(extractSubProgDisplayId(cProg, focusId2), "ERROR")
            	        f_dProg <- prgFunOut(f_aProg, frame1)(f_cProg)
            	        dProg <- Success(f_dProg)
            	    } yield {
            	        CalcProgStep(dProg, None)
            	    }
            	case (CalcProgStep(newProg, None)) =>
            	    //This case should never occur
            	    throw new RuntimeException("Stepout failed.")
	        }
	    case _ =>
	        Failure(new RuntimeException("ERROR"))
	}
}

case class StepOutTactic() extends Tactic {

    val tName = "StepOut"

	/** Searches for the matching stepin node and calls the funOut of that node to get the newOuter Node*/
	def getNextNode(node: SynthNode, idOpt: Option[Int]): Either[ TacticError, SynthNode] = {
		try{
			//Find the matching stepin node
			var cnode = node
			var SOStack: List[Tactic] = Nil
			var SINodeOpt: Option[SynthNode] = None
			while (cnode.parent != null && SINodeOpt.isEmpty) {
				cnode.tactic match {
					case _: StepOutTactic =>
						SOStack ::= cnode.tactic
					case _: StepInTactic if !SOStack.isEmpty =>
						SOStack = SOStack.tail
					case _: StepInTactic if SOStack.isEmpty =>
						SINodeOpt = Some(cnode)
					case _ =>
				}
				cnode = cnode.parent
			}
			//Call the funOut function on the stepin node to get the new outerObj
			val res = SINodeOpt match {
				case Some(siNode) =>
					val siTactic: StepInTactic = siNode.tactic.asInstanceOf[StepInTactic]
					val outerNodeObj = siNode.parent.nodeObj
					val outerFrm = siNode.parent.frame
					val newObjT = siTactic.funOut(outerNodeObj, outerFrm)(node.nodeObj)
					newObjT match {
					    case Success(newObj) =>
    					    val sn = new SynthNode(
    					    		tactic = this,
    					    		nodeObject = newObj,
    					    		childs = Nil,
    					    		parent = node,
    					    		frame = siNode.parent.frame,
    					    		synthTree = node.synthTree, idOpt)
    					    Right(sn)
					    case Failure(e) => throw e
					}
				case None =>
					Left(TacticApplicationFailed("Tactic Application Failed"))
			}
			res
		} catch {
			case ex: Throwable =>
				printException(ex)
				Left(TacticApplicationFailed(ex.getMessage()))
		}
	}
	def getHint(): Elem = <div>StepOut</div>
	def getRelation() = None
}
