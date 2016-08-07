package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz.{ Forall => SForall, Const => SConst, Success => SSuccess, Failure => SFailure, _ }
import Scalaz._
import org.kiama.rewriting.Rewriter.{ Term => KTerm, _ }
import progsynth.proofobligations.POGenerator
import progsynth.proofobligations.StrongestPost
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import progsynth.printers.XHTMLPrinters2
import SynthUtils._
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.provers._
import PartialFunction._
import TacticDocRepo._
import XHTMLPrinters2.termToHtml
import PSTacticsHelper._
import progsynth.utils.PSErrorCodes._
import scala.util.{Success, Failure, Try}

/*
class AddMacrosTactic(macros: List[Macro]) extends StepInTactic {
	//Create a new frame with added macros
	override def funIn: (Any, Frame) ==> (Any, NonRootFrame) = {
		/** Why is addMacroTactic not enabled for RootFrame?
		 *  refer addMacros scaladoc in NonRootFrame class */
		case (nodeObj, frame: NonRootFrame) =>
			val newFrame = NonRootFrame.addMacros(frame, macros)
			(nodeObj, newFrame)
	}

	//Expand the macros
	override def funOut: (Any, Frame) ==> (Any ==> Any) = {
		case (pa: ProgramAnn, _) =>
			{ case newPostF: TermBool => pa.copyPost(pa.post.copy(term = newPostF)) }
	}

	override def toString = "AddMacros"
	def getHint(): Elem =
		<div>
			<div>AddMacros</div>
			<div>{PSTacticsHelper.docLink(tName)}</div>
		</div>

	def getRelation() = None
}
*/
//This is a comment. And


case class RetValTactic(initTerm: Option[Term]) extends PrgFunTactic {

    override val tName = "RetVal"

    override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = (nodeObj, frame) match {
		case (pa: UnknownProg, _) => Try {
			//TODO: move this tactic to synthesisnew package
			val rvt = new progsynth.synthesisold.RetValTactic(initTerm: Option[Term])
			val result = rvt.applyTactic(pa, None)
			result match {
				case res0 :: Nil => res0.resultProg match {
					case Some(res) => res
					case None => throw new RuntimeException("RetValTactic application failed")
				}
				case _ => throw new RuntimeException("RetValTactic application failed")
			}
		}
		case _ =>
		    Failure(throw new RuntimeException("Tactic not applicable"))
	}

	def getHint(): Elem =
		<div>
			<div>RetVal</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None
}

/** Step into the unknown prog of given id*/
case class StepIntoUnknownProgIdTactic(id: java.lang.Integer) extends PrgStepInTactic {

    override val tName = "StepIntoUnknownProgId"

    def getHint(): Elem =
		<div>
			<div>StepIntoUnknownProgId</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
	//extractUnknwonProg

	def getInnerProg(pa: ProgramAnn): Option[ProgramAnn] = {
		var unkProg: UnknownProg = null
		val queryStr = rule { case up @ UnknownProgC(id) => unkProg = up; up }

		oncetd(log(queryStr, ""))(pa)
		if(unkProg != null) Some(unkProg) else None
	}

	/*Returns innerFocusedObj and frame */
	override def prgFunIn(outerFocusedProg: ProgramAnn, outerFrame: Frame): Try[(ProgramAnn, Frame)] = Try {
	    (outerFocusedProg, outerFrame) match {
    		case (pa: ProgramAnn, frame: ProgramFrame) => {
    		    val unkProgOpt = getInnerProg(pa)
    		    unkProgOpt match {
    		        case Some(unkProg) =>
            			val newFrameO = FrameFinder.mkInnerProgFrame(pa, frame)(unkProg)
            			newFrameO match {
            				case Some(newFrame) => (unkProg, newFrame)
            				case None => throw new RuntimeException("Exception in StepIntoUnknownProgIdTactic")
            			}
    		        case None =>
    		            throw new RuntimeException("Unable to find the unknown program")
    		    }
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	//buildFromNewProgram
	override def prgFunOut(oldFocusedProg: ProgramAnn, oldFrame: Frame)(newInnerProg: ProgramAnn): Try[ProgramAnn] = Try {
		PSDbg.writeln0("called")
		val newpa = oncetd(rule { case UnknownProgC(id) => newInnerProg })(oldFocusedProg)
		newpa.get.asInstanceOf[ProgramAnn]
    }


}

/** Step into Prog with given id*/
case class StepIntoProgIdTactic(id: java.lang.Integer) extends PrgStepInTactic {
	override val tName = "StepIntoProgId"
	def getHint(): Elem =
		<div>
			<div>StepIntoProgId</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	//extractUnknwonProg
	def getInnerProg(pa: ProgramAnn): Option[ProgramAnn] = {
			var subProgOpt: Option[ProgramAnn] = None
			val queryStr = rule { case sub: ProgramAnn if sub.id == id => subProgOpt = Some(sub); sub }
			oncetd(queryStr)(pa) //Run for its side effect.
			subProgOpt
	}

	override def prgFunIn(outerFocusedProg: ProgramAnn, outerFrame: Frame): Try[(ProgramAnn, Frame)] = Try {
	    (outerFocusedProg, outerFrame) match {
    		case (pa: ProgramAnn, frame: ProgramFrame) => {
    		    val subProgOpt = getInnerProg(pa)

    		    subProgOpt match {
    		        case Some(subProg) =>
            			val newFrameO = FrameFinder.mkInnerProgFrame(pa, frame)(subProg)
            			newFrameO match {
            				case Some(newFrame) => (subProg, newFrame)
            				case None => throw new RuntimeException("Exception in StepIntoProgIdTactic application")
            			}
            		case None =>
            		    throw new RuntimeException("StepIntoProgIdTactic application failed. subprogram with given id not found")
    		    }
    		}
        	case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	//buildFromNewProgram
	override def prgFunOut(oldFocusedProg: ProgramAnn, oldFrame: Frame)(newInnerProg: ProgramAnn): Try[ProgramAnn] = Try {
		PSDbg.writeln0("called")
		val newOuterOpt = oncetd(rule { case sub: ProgramAnn if sub.id == id => newInnerProg })(oldFocusedProg)
		newOuterOpt match {
			case Some(newOuter) => newOuter.asInstanceOf[ProgramAnn]
			case None => throw new RuntimeException("Exception in StepIntoProgIdTactic stepout function")
		}
	}
}

/** Step into Prog with given display id*/
case class StepIntoSubProgTactic(displayId: java.lang.Integer) extends PrgStepInTactic {
	override val tName = "StepIntoSubProg"
	def getHint(): Elem =
		<div>
			<div>StepIntoSubProg</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	//extractSubProg
    def getInnerProg(pa: ProgramAnn): Option[ProgramAnn] = {
		var subProgOpt: Option[ProgramAnn] = None
		val queryStr = rule { case sub: ProgramAnn if sub.displayId == displayId => subProgOpt = Some(sub); sub }
		//oncetd(log(queryStr, ""))(pa)
		oncetd(queryStr)(pa) //Run for its side effect.
		subProgOpt
	}

	override def prgFunIn(outerFocusedProg: ProgramAnn, outerFrame: Frame): Try[(ProgramAnn, Frame)] = Try {
	    (outerFocusedProg, outerFrame) match {
    		case (pa: ProgramAnn, frame: ProgramFrame) => {
    			val subProgOpt = getInnerProg(pa)
    			subProgOpt match {
    			    case Some(subProg) =>
            			val newFrameO = FrameFinder.mkInnerProgFrame(pa, frame)(subProg)
            			newFrameO match {
            				case Some(newFrame) => (subProg, newFrame)
            				case None => throw new RuntimeException("Exception in StepIntoProgIdTactic application")
            			}
    			    case None =>
    			        throw new RuntimeException("StepIntoProgIdTactic application failed. subprogram with given id not found")
    			}
    		}
        	case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	//buildFromNewProgram
	override def prgFunOut(oldFocusedProg: ProgramAnn, oldFrame: Frame)(newInnerProg: ProgramAnn): Try[ProgramAnn] = Try {
    	PSDbg.writeln0("called")
    	val newOuterOpt = oncetd(rule { case sub: ProgramAnn if sub.displayId == displayId => newInnerProg })(oldFocusedProg)
    	newOuterOpt match {
    		case Some(newOuter) => newOuter.asInstanceOf[ProgramAnn]
    		case None => throw new RuntimeException("Exception in StepIntoProgIdTactic stepout function")
    	}
	}
}

/** Step into the unknown prog of given id*/
case class StepIntoUnknownProgIdxTactic(val idx: java.lang.Integer) extends PrgStepInTactic {
	override val tName = "StepIntoUnknownProgIdx"
	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml
		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">StepIntoUnknownProgIdx</th></tr>
				<tr><th>idx</th><td>{ idx }</td></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None
	//extractUnknwonProg

	def getInnerProg(pa: ProgramAnn): Option[ProgramAnn] = {
		var unkProg: UnknownProg = null
		var cnt = 0

		val aStrat = strategy {
			case up @ UnknownProgC(_) =>
				cnt += 1;
				if (cnt == idx) {
					unkProg = up
					Some(up)
				} else None
		}
		oncetd(aStrat)(pa) //This strategy is run for the side effect.

		if (unkProg != null) Some(unkProg) else None
	}

	override def prgFunIn(outerFocusedProg: ProgramAnn, outerFrame: Frame): Try[(ProgramAnn, Frame)] = Try {
	    (outerFocusedProg, outerFrame) match {
    		case (pa: ProgramAnn, frame: Frame) => {
    		    val unkProgOpt = getInnerProg(pa)
    		    unkProgOpt match {
    		        case Some(unkProg) =>
        		        val newFrameO = FrameFinder.mkInnerProgFrame(pa, frame)(unkProg)
        		        newFrameO match {
            		        case Some(newFrame) => (unkProg, newFrame)
            		        case None => throw new RuntimeException("Exception in StepIntoUnknownProgIdTactic")
        		        }
    		        case None =>
    		            throw new RuntimeException("Unknown Program with the given idx not found.")
    		    }
    		}
        	case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	//buildFromNewProgram
	override def prgFunOut(oldFocusedProg: ProgramAnn, oldFrame: Frame)(newInnerProg: ProgramAnn): Try[ProgramAnn] = Try {
		var cnt = 0
		val aStrat = strategy {
			case up @ UnknownProgC(_) =>
				cnt += 1;
				if (cnt == idx) {
					Some(newInnerProg)
				} else None
		}
		val res = oncetd(aStrat)(oldFocusedProg)
		res.get.asInstanceOf[ProgramAnn]
	}
}

/**
 * Unknown Program Tactic
 * Replaces a constant 'const' by new variable 'variable'.
 * The introduced variable in initialized to 'initValue'
 * The specified bounds are conjuncted with the post-condition.
 */
case class RCVInPostTactic(const0: Var, variable0: Var, initValue0: Term, bounds: TermBool) extends PrgFunTactic {
	override val tName = "RCVInPost"
	def getHint(): Elem =
		<div>
			<div>RCVInPost</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

	val const = const0.asInstanceOf[VarInt] //TODO: Remove hardcoded Int
	val variable = variable0.asInstanceOf[VarInt]
	val initValue = initValue0.asInstanceOf[TermInt]

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val initProg: ProgramAnn = mkVarDefProg(
    				pre = unkProg.pre,
    				lhs = variable,
    				rhs = mkExprProg(unkProg.pre, initValue, unkProg.pre.addConjunct(variable eqeq initValue).setRvVar(variable)),
    				post = unkProg.pre.addConjunct(variable eqeq initValue))

    			val newPost = unkProg.post.modifyTermBool(_.replaceVar(const, variable) && bounds && (variable eqeq const))
    			val newUnk: ProgramAnn = mkUnknownProg(pre = initProg.post, unkProg.id, post = newPost)
    			val compProg = mkComposition(
    				pre = unkProg.pre,
    				programs = initProg :: newUnk :: Nil,
    				post = unkProg.post)
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(compProg)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(compProg, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(compProg, frameSummary.getMacros())
    			compProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Unknown Program Tactic
 * Replaces a term 'const' by new variable 'variable'.
 * The introduced variable in initialized to 'initValue'
 * The specified bounds are conjuncted with the post-condition.
 */
case class RTVInPostTactic(constant: Term, variable: Var, initValue: Term, bounds: TermBool) extends PrgFunTactic {
	override val tName = "RTVInPost"
	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml
		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">RTVInPost</th></tr>
				<tr><th align="left">constant</th><td>{ termToHtml(constant) }</td></tr>
				<tr><th align="left">variable</th><td>{ termToHtml(variable) }</td></tr>
				<tr><th align="left">initValue</th><td>{ termToHtml(initValue) }</td></tr>
				<tr><th align="left">bounds</th><td>{ termToHtml(bounds) }</td></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	//TODO: check that const variable and initValue are of same type.
	val const2 = constant
	val variable2 = variable
	val initValue2 = initValue

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val initProg: ProgramAnn = mkVarDefProg(
    				pre = unkProg.pre,
    				lhs = variable2,
    				rhs = mkExprProg(unkProg.pre, initValue2, unkProg.pre.addConjunct(variable2 eqeq initValue2).setRvVar(variable2)),
    				post = unkProg.pre.addConjunct(variable2 eqeq initValue2))

    			val newPost = unkProg.post.modifyTermBool(x => (x.mapSubTerms { case `const2` => variable2 }.asInstanceOf[TermBool]) && bounds && (variable2 eqeq const2))
    			val newUnk: ProgramAnn = mkUnknownProg(pre = initProg.post, unkProg.id, post = newPost)
    			val compProg = mkComposition(
    				pre = unkProg.pre,
    				programs = initProg :: newUnk :: Nil,
    				post = unkProg.post)
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(compProg)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(compProg, frameSummary.getMacros())
    			//ProgramAnnPOProver.provePOs(compProg, frameSummary.getMacros())
    			compProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * WhileStrInvSP Program Tactic
 * Propagates assumed precondition forward at the end of the while body (thetaStar).
 * Strengthens the invariant with thetaStar.
 * Adds an unknown program at start of the body. {thetaStar}UnkProg{theta}
 * Currently implemented only for a single guard.
 */
case class WhileStrInvSPTactic() extends PrgFunTactic {

	override val tName = "WhileStrInvSP"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml

		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">WhileStrInvSP</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	object WhileProgSingleEx {
		def unapply(w: ProgramAnn): Option[(Option[TermBool], GuardedCmd, InvariantT, InvariantT)] = w match {
			case WhileProgSingle(a, b, c, d) => Some(a, b, c, d)
			case _ => throw new Exception("Current node is not a while program with a single guarded command.")
		}
	}

	object CompositionCollapsed{
		//Extract subprograms of a nested composition
		def unapply(comp: Composition): Option[List[ProgramAnn]] = {
			val CompositionC(stmts) = comp
			val newStmts = stmts flatMap {
				case CompositionCollapsed(substmts) => substmts
				case stmt => List(stmt)
			}
			Some(newStmts)
		}
	}

	object WhileExtractor {
		def apply(inv: InvariantT, grd: TermBool, decls: List[ProgramAnn],
			bodyPre: InvariantT, theta: TermBool, sProg: ProgramAnn): WhileProg =
		{
			throw new Exception("WhileExtractor apply not implemented")
		}

		def unapply(wp: WhileProg): Option[(InvariantT, TermBool, List[ProgramAnn], InvariantT, TermBool, ProgramAnn)] = {
			wp match {
				case WhileProgSingleEx(Some(inv), GuardedCmd(grd, c), _, _) => c match {
					case CompositionCollapsed(stmts) =>

						val decls = stmts.takeWhile(stmt => stmt match {
							case VarDefProgC(_, None) => true
							case ValDefProgC(_, None) => true
							case _ => false
						})

						val rstmts = stmts.drop(decls.length)

						rstmts match {
							case AssumeProgC(theta) :: bdyStms =>
								val sProg = buildComposition(bdyStms)
								Some((inv.inv, grd, decls, c.pre, theta, sProg))
							case s :: Nil =>
								throw new Exception("The first statement in the loop body is not an 'assume' statment.")
						}
					case _ =>
						throw new Exception("The of the while loop is not a composition statement.")
				}
			}
		}

	}

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (whileProg @ WhileExtractor(inv: InvariantT, grd: TermBool, decls: List[ProgramAnn],
    			bodyPre: InvariantT, theta: TermBool, sProg: ProgramAnn), frame) =>
    			{
    				val thetaStar: TermBool = StrongestPost.computeStrongestPost(sProg, theta)(simplify = true, weakened = true).get

    				val outVarDecls: List[ProgramAnn] = {
    					//fv theta* intersect vars in decls
    					val fvs = thetaStar.getFreeVars
    					decls.filter { decl =>
    						decl match {
    							case ValDefProgC(lhs, _) if fvs contains lhs => true
    							case VarDefProgC(lhs, _) if fvs contains lhs => true
    							case _ => false
    						}
    					}
    				}

    				val establishInv: List[ProgramAnn] =
    					List(AssumeProg(thetaStar, whileProg.pre, whileProg.pre.addConjunct(thetaStar)))

    				val whileProgNew: WhileProg =
    					WhileProgSingle(
    						loopInv = whileProg.loopInv.map(_ && thetaStar),
    						grdcmd = GuardedCmd(
    							guard = grd,
    							cmd = {
    								val inVarDecls = decls.filterNot(outVarDecls.contains(_))
    								val unkProg = UnknownProg(0,
    									pre = bodyPre.addConjunct(thetaStar),
    									post = sProg.pre)
    								val newSProg = sProg.withNewParams(post = sProg.post.addConjunct(thetaStar))
    								collapseOuterMostComposition(
    										buildComposition(inVarDecls :+ unkProg :+ newSProg))
    							}),
    						pre = whileProg.pre.addConjunct(thetaStar),
    						post = whileProg.post)

    				buildComposition(outVarDecls ++ establishInv :+ whileProgNew)
    			}
    		case _ => throw new Exception("Current node is not a while program in desired form.")
	    }
	}
}

case class PropagateAssumeUpTactic(displayId: java.lang.Integer) extends PrgFunTactic {

	override val tName = "PropagateAssumeUp"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml

		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">{tName}</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	// reads displayId from context
	object CompExt {
		//preProgs, aProg, assumeProg, postProgs
		def unapply(compProg: Composition): Option[(List[ProgramAnn], ProgramAnn, AssumeProg, List[ProgramAnn])] = {
			val (l1, l2) = compProg.programs.span { prg =>
				prg match {
					case asp @ AssumeProg(_, _, _) if asp.displayId == displayId => false
					case _ => true
				}
			}

			val (preProgs, aProg) = (l1.init, l1.last)
			val (assumeProg, postProgs) = (l2.head, l2.tail)

			if (!preProgs.isEmpty)
				Some((preProgs, aProg, assumeProg.asInstanceOf[AssumeProg], postProgs))
			else
				None
		}
	}

	// reads displayId from context
	object IfProgExt {
		//grdCmds1, grdCmds2, pre, post
		def unapply(ifProg: IfProg): Option[(List[GuardedCmd], GuardedCmd, List[GuardedCmd], InvariantT, InvariantT)] = {
			val (l1, l2) = ifProg.grdcmds.span { grdCmd =>
				grdCmd match {
					case GuardedCmd(grd, CompositionC((asp @ AssumeProg(_, _, _)) :: rest)) if asp.displayId == displayId => false
					case _ => true
				}
			}
			if (!l2.isEmpty)
				Some((l1, l2.head, l2.tail, ifProg.pre, ifProg.post))
			else
				None
		}
	}

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (pa: ProgramAnn, frame) =>

    			val queryStr = strategy {
    				case SkipProg(alpha, beta) :: AssumeProgC(theta) :: rest =>
    					Some(AssumeProg(theta, alpha, alpha.and(theta))
    						:: SkipProg(alpha.and(theta), beta.and(theta))
    						:: rest)

    				case AssignmentExpr(varTerms, alpha, beta) :: AssumeProgC(theta) :: rest =>
    					val wp: TermBool = theta.replaceVarsSim(varTerms.toMap)
    					Some(AssumeProg(wp, alpha, alpha.and(wp))
    						:: AssignmentExpr(varTerms, alpha.and(wp), beta.and(theta))
    						:: rest)

    				case UnknownProg(id, alpha, beta) :: AssumeProgC(theta) :: rest =>
    					Some(AssumeProg(theta, alpha, alpha.and(theta))
    						:: UnknownProg(id, alpha.and(theta), beta.and(theta))
    						:: rest)

    				case AssumeProg(psi, alpha, alphapsi) :: AssumeProgC(theta) :: rest =>
    					Some(AssumeProg(theta, alpha, alpha.and(theta))
    						:: AssumeProg(psi, alpha.and(theta), alpha.and(psi).and(theta))
    						:: rest)

    				case Composition(prgs, alpha, beta) :: AssumeProgC(theta) :: rest =>
    					Some(Composition(prgs :+ AssumeProg(theta, beta, beta.and(theta)), alpha, beta.and(theta)))

    				case IfProg(grdcmds, alpha, beta) :: AssumeProgC(theta) :: rest =>
    					Some(IfProg(
    						grdcmds.map(_.mapCmd(cmd => cmd.appendProg(AssumeProg(theta, cmd.post, cmd.post.and(theta))))),
    						alpha,
    						beta.and(theta)) :: Nil)
    				case WhileProgSingle(Some(inv), GuardedCmd(grd, cmd), alpha, beta) :: AssumeProgC(theta) :: rest =>
    					Some(AssumeProg(theta, alpha, alpha.and(theta))
    						:: WhileProgSingle(
    							Some(inv && theta),
    							GuardedCmd(grd,
    								Composition(
    									cmd,
    									AssumeProg(theta, cmd.post, cmd.post.and(theta)),
    									cmd.pre,
    									cmd.post.and(theta))),
    							alpha.and(theta), beta.and(theta))
    							:: rest)
    				case WhileProgSingle(Some(inv), GuardedCmd(grd, cmd @ CompositionC(AssumeProgC(theta) :: body)), alpha, beta) =>
    					Some(buildComposition(AssumeProg(grd impl theta, alpha, alpha.and(grd impl theta))
    						:: WhileProgSingle(
    							Some(inv && grd impl theta),
    							GuardedCmd(grd,
    								buildComposition(
    									body
    										:+ AssumeProg(grd impl theta, cmd.post, cmd.post.and(grd impl theta)))),
    							alpha.and(grd impl theta), beta)
    							:: Nil))

    				case IfProgExt(grdcmdsPre,
    						grdcmd @ GuardedCmd(grd, CompositionC(AssumeProgC(theta):: programs)),
    						grdcmdsPost,
    						alpha, beta) =>
    					{
    						Some(buildComposition(
    							AssumeProg(theta, alpha, alpha.and(theta)),
    							IfProg(
    								grdcmdsPre.map(_.strengthenCmdPre(theta)),
    								GuardedCmd(grd, buildComposition(programs)),
    								grdcmdsPost.map(_.strengthenCmdPre(theta)),
    								alpha.and(theta),
    								beta)))
    					}
    			}

    			val res = oncetd(queryStr)(pa)
    			res match {
    				case Some(modifiedCompo) => modifiedCompo.asInstanceOf[ProgramAnn]
    				case None => throw new RuntimeException("PropagateAssumeUp tactic application failed.")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}



////Propagates the assumed predicate of the assume program with displayId1 to the post of the subprogram ( displayId2)
//class PropagateAssertionsDownWSPTactic(displayId1: java.lang.Integer, displayId2: java.lang.Integer) extends FunTactic {
//
//	val logger= LoggerFactory.getLogger("progsynth.PropagateAssertionsDownWSPTactic")
//
//	override val tName = "PropagateAssertionsDownWSP"
//
//	override def toString() = tName
//
//	def getHint(): Elem = {
//		import XHTMLPrinters2.termToHtml
//
//		<div>
//			<table border='1' class='tablestyle'>
//				<tr><th colspan="2">{tName}</th></tr>
//			</table>
//			<div>{ PSTacticsHelper.docLink(tName) }</div>
//		</div>
//	}
//
//	def getRelation() = None
//
//	override def fun: (Any, Frame) ==> Any = {
//		case (pa: ProgramAnn, frame) =>
//
//			val queryStr = strategy {
//				case prg: ProgramAnn =>
//					propagateDown(prg, displayId1, displayId2)
//			}
//
//			val res = oncetd(queryStr)(pa)
//			res match {
//				case Some(modifiedProg) => modifiedProg
//				case None => throw new RuntimeException("PropagateAssertionsDownWSP tactic application failed.")
//			}
//	}
//
//	def propagateDown(prg: ProgramAnn): Option[ProgramAnn] =
//		propagateDown(prg, prg.displayId, prg.displayId)
//
//	def propagateDown(prg: ProgramAnn, a: Int, b: Int): Option[ProgramAnn] = {
//
//		logger.trace(beginSection("PropagateDown"))
//
//		logger.trace("prg.displayId: " + prg.displayId + "a: " + a + " b: " + b )
//
//		val perfectMatch = prg.displayId == a && prg.displayId == b
//
//		val retVal:Option[ProgramAnn] = (prg match {
//
//			case _: WhileProg if (prg.displayId == a || prg.displayId == b) =>
//				None
//
//			case WhileProgSingle(invOpt, GuardedCmd(grd, cmd), pre, post)
//			if hasSubProg(cmd, a) && hasSubProg(cmd, b) =>
//				propagateDown(cmd, a, b)
//
//			case ifp @ IfProgC(grdcmds) if perfectMatch =>
//				val newGCs = for {
//					GuardedCmd(grd, cmd) <- grdcmds
//					cmd2 = cmd.setPre(ifp.pre.and(grd))
//					cmd3 <- propagateDown(cmd2)
//				} yield
//					GuardedCmd(grd, cmd3)
//
//				if(newGCs.length == grdcmds.length) {
//					val post = TermBool.mkDisjunct(newGCs.map(_.cmd.post.term))
//					Some(IfProg(newGCs, ifp.pre, post.inv).setDisplayIdPA(ifp.displayId))
//				} else
//					None
//
//
//			case ifp @ IfProgC(grdcmds) if ifp.displayId == a =>
//				val (gcs1, theGCOpt, gcs2) = span1(grdcmds, (gc: GuardedCmd) => hasSubProg(gc.cmd, b))
//
//				for {
//					GuardedCmd(grd, cmdb) <- theGCOpt
//					cmdb2 = cmdb.setPre(ifp.pre.and(grd))
//					cmdb3 <- propagateDown(cmdb2, cmdb2.displayId, b)
//					newGrdCmd = GuardedCmd(grd, cmdb3)
//				} yield {
//					IfProg(gcs1 ++ (newGrdCmd :: gcs2), ifp.pre, ifp.post)
//					.setDisplayIdPA(ifp.displayId)
//				}
//
//			case ifp @ IfProgC(grdcmds) =>
//				val (gcs1, theGCOpt, gcs2) =
//					span1(grdcmds, (gc: GuardedCmd) => hasSubProg(gc.cmd, a) && hasSubProg(gc.cmd, b))
//
//				for {
//					GuardedCmd(grd, cmdab) <- theGCOpt
//					cmdabNew <- propagateDown(cmdab, a, b)
//					newGrdCmd = GuardedCmd(grd, cmdabNew)
//				} yield {
//					IfProg(gcs1 ++ (newGrdCmd :: gcs2), ifp.pre, ifp.post)
//					.setDisplayIdPA(ifp.displayId)
//				}
//
//			case cmp @ CompositionC(prgs) if hasSubProg(cmp, a) && hasSubProg(cmp, b) =>
//				var iPre = cmp.pre
//				var done = false
//				var start = cmp.displayId == a
//
//				val newPrgOpts = for(prg <- prgs) yield {
//
//					if(!start && hasSubProg(prg, a)){
//						start = true
//					}
//
//					if(start && !done){
//						if (hasSubProg(prg, b))	done = true
//
//						val prg2 = prg.setPre(iPre)
//
//						propagateDown(prg2, prg2.displayId, if (done) b else prg2.displayId) match {
//							case Some(prg3) =>
//								iPre = prg3.post
//								Some(prg3)
//							case None => None
//						}
//					} else
//						Some(prg)
//				}
//
//				lo2ol(newPrgOpts).map(prgs => buildComposition(prgs).setDisplayIdPA(cmp.displayId))
//
//			case skp @ SkipProg(pre, post) if perfectMatch=>
//				Some(skp.withNewParams(post = pre))
//
//			case unk @ UnknownProg(_, pre, post) if perfectMatch =>
//				None
//
//			case asm @ AssumeProg(theta, pre, _) if perfectMatch =>
//				Some(asm.withNewParams(post = pre.and(theta)))
//
//			case asgn @ Assignment(_, pre, _) if perfectMatch =>
//				(StrongestPost.computeStrongestPost(asgn, pre.term, simplify = true) map { spF =>
//					asgn.withNewParams(post = spF.inv)
//				}).toOption
//
//			case vd @ VarDefProgC(_, None) if perfectMatch => //TODO: implement for assignment
//				 Some(vd.withNewParams(post = vd.pre))
//
//			case vd @ ValDefProgC(_, None) if perfectMatch =>
//				 Some(vd.withNewParams(post = vd.pre))
//
//			case _ => None
//		})
//		retVal.map{ r =>
//			logger.trace(endSection("PropagateDown"))
//			r
//		}
//	}
//}


/**
 * Strengthens the post of the program with strongest postcondition.
 * Updates the pre and post of all the intermediate programs.
 */
case class StrengthenPostSPTactic(displayId: java.lang.Integer) extends PrgFunTactic {

	override val tName = "StrengthenPostSP"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml

		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">{tName}</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {

    		case (pa: ProgramAnn, frame) =>

    			val queryStr = strategy {
    				case prg: ProgramAnn if prg.displayId == displayId=>
    					propagateDownSP(prg)
    			}

    			val res = oncetd(queryStr)(pa)
    			res match {
    				case Some(modifiedProg) => modifiedProg.asInstanceOf[ProgramAnn]
    				case None => throw new RuntimeException("StrengthenPostSP tactic application failed.")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	def propagateDownSP(prg: ProgramAnn): Option[ProgramAnn] = {
		//import scalaz._; import Scalaz._

		prg match {

			case ifp @ IfProgC(grdcmds)=>
				val newGCs = for {
					GuardedCmd(grd, cmd) <- grdcmds
					cmd2 = cmd.setPre(ifp.pre.and(grd))
					cmd3 <- propagateDownSP(cmd2)
				} yield
					GuardedCmd(grd, cmd3)

				if(newGCs.length == grdcmds.length) {
					val post = TermBool.mkDisjunct(newGCs.map(_.cmd.post.term))
					Some(IfProg(newGCs, ifp.pre, post.inv))
				} else
					None


			case cmp @ CompositionC(prgs) =>
				var iPre = cmp.pre
				val newPrgs = for {
					prg <- prgs
					prg2 = prg.setPre(iPre)
					prg3 <- propagateDownSP(prg2)
					iPre = prg3.post
				} yield
					prg3
				if(newPrgs.length == prgs.length)
					Some(buildComposition(newPrgs))
				else
					None

			case asgn @ Assignment(_, pre, _) =>
				val x = (StrongestPost.computeStrongestPost(asgn, pre.term)(simplify = true, weakened = false) map { spF =>
					asgn.withNewParams(post = spF.inv)
				})
				x.toOption

			case vd @ VarDefProgC(_, None)=> //TODO: implement for assignment
				 Some(vd.withNewParams(post = vd.pre))

			case vd @ ValDefProgC(_, None) =>
				 Some(vd.withNewParams(post = vd.pre))

			case skp @ SkipProg(pre, post) =>
				Some(skp.withNewParams(post = pre))

			case unk @ UnknownProg(_, pre, post) =>
				None

			case asm @ AssumeProg(theta, pre, _) =>
				Some(asm.withNewParams(post = pre.and(theta)))

			case _ => None
		}
	}
}

case class AssumeToIfTactic(displayId: java.lang.Integer) extends PrgFunTactic {

	override val tName = "AssumeToIf"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml

		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">AssumeToIf</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	def notSimple(t: TermBool) = t match {
		case NotTermBool(x) => x
		case _ => !t
	}

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (pa: ProgramAnn, frame) =>

    			val queryStr = strategy {
    				case (ap @ AssumeProgC(theta)) :: (rest: List[ProgramAnn]) if !rest.isEmpty && ap.displayId == displayId =>
    					Some(List(
    				        IfProg(
    			                pre = ap.pre,
    							post = rest.last.post,
    							grdcmds = List(
    								GuardedCmd(theta, buildComposition(rest)),
                                    GuardedCmd(notSimple(theta),
    									UnknownProg(0, pre = ap.pre.and(!theta), post = rest.last.post))))))
    			}

    			val res = oncetd(queryStr)(pa)
    			res match {
    				case Some(ifProg) => ifProg.asInstanceOf[ProgramAnn]
    				case None => throw new RuntimeException("AssumeToIf tactic application failed.")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

}

/**
 * Unknown Program Tactic
 * Introduce a while loop
 */
case class DeleteConjunctTactic(val conjunct: TermBool, val variant: Term) extends PrgFunTactic {
	override val tName = "DeleteConjunct"

	def getHint(): Elem = {
		import DeleteConjunctTacticDoc.dName
		import XHTMLPrinters2.termToHtml
		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">DeleteConjunct</th></tr>
				<tr><th align="left">{ dName("conjunct") }</th><td>{ termToHtml(conjunct) }</td></tr>
				<tr><th align="left">{ dName("variant") }</th><td>{ termToHtml(variant) }</td></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val pre = unkProg.pre
    			val post = unkProg.post
    			// Remove conjunct from post
    			//TODO: should throw exception if the conjuct is not there in the post
    			//TODO: if the post is a single term which is equal to conjuct then true should be returned.
    			val loopInv = post.term match {
    				case AndTermBool(t1, t2) if List(t1, t2) contains conjunct =>
    					if (t1 == conjunct) t2 else t1
    				case AndNTermBool(ts) if ts contains conjunct =>
    					AndNTermBool(ts.filterNot(t => t == conjunct)) //TODO: is the first case redundant.
    				case _ => null //TODO: handle this case
    			}

    			// Replace conjunct with negation of conjunct
    			val insideLoop: TermBool = loopInv && conjunct.unary_!

    			val composition = mkComposition(
    				pre = pre,
    				programs = List(
    					mkUnknownProg(pre, unkProg.id, loopInv.inv),
    					mkWhileProg(
    						pre = loopInv.inv,
    						loopInv = Some(loopInv),
    						grdcmds = List(GuardedCmd(conjunct.unary_!,
    							mkUnknownProg(insideLoop.inv, unkProg.id + 1, loopInv.inv))), //TODO: decide id.
    						post = post)),
    				post = post)
    			val frameSummary = frame.getSummary
    			//POGenerator.populatePOs(composition)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(composition, frameSummary.getMacros())
    			//ProgramAnnPOProver.provePOs(composition, frameSummary.getMacros())
    			composition
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Unknown Program Tactic
 * Introduce a while loop
 */
case class IntroWhileTactic(loopInvF: TermBool, guardF: TermBool) extends PrgFunTactic {
	override val tName = "IntroWhile"

	def getHint(): Elem =
		<div>
			<div>IntroWhileTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val pre = unkProg.pre
    			val post = unkProg.post

    			val insideLoop: TermBool = loopInvF && guardF

    			val composition = mkComposition(
    				pre = pre,
    				programs = List(
    					mkUnknownProg(pre, unkProg.id, loopInvF.inv),
    					mkWhileProg(
    						pre = loopInvF.inv,
    						loopInv = Some(loopInvF),
    						grdcmds = List(GuardedCmd(guardF,
    							mkUnknownProg(insideLoop.inv, unkProg.id + 1, loopInvF.inv))), //TODO: decide id.
    						post = post)),
    				post = post)
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(composition)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(composition, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(composition, frameSummary.getMacros())
    			composition
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class IntroCompositionTactic(midInv: TermBool) extends PrgFunTactic {
	override val tName = "IntroComposition"

	def getHint(): Elem =
		<div>
			<div>IntroCompositionTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val pre = unkProg.pre
    			val post = unkProg.post

    			val unk1 = mkUnknownProg(pre, unkProg.id + 1, midInv.inv)
    			val unk2 = mkUnknownProg(midInv.inv, unkProg.id + 2, post)

    			val composition = mkComposition(
    				pre = pre,
    				programs = unk1 :: unk2 :: Nil,
    				post = post)
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(composition)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(composition, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(composition, frameSummary.getMacros())
    			composition
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**Assume a program directly. Useful for verification */
case class AssumeProgTactic(aProg: ProgramAnn) extends PrgFunTactic {
	override val tName = "AssumeProg"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml
		<div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

    override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = (nodeObj, frame) match {
		case (unkProg: UnknownProg, frame) =>
			val retVal = aProg
			retVal.inferAnn //todo: enabled temporarily. disable when done.
			val frameSummary = frame.getSummary
			POGenerator.populatePOs(retVal)(frameSummary.getGlobalInvs())
			//POZ3Prover.provePOs(retVal, frameSummary.getMacros())
			ProgramAnnPOProver.provePOs(retVal, frameSummary.getMacros())
			Success(retVal)
		case _ =>
		    Failure(new RuntimeException("Tactic not applicable"))

	}
}

/**
 * UnknownProgram Tactic
 * Assume skipProg as the desired unknown program
 */
case class AssumeSkipTactic() extends PrgFunTactic {
	override val tName = "AssumeSkip"

	def getHint(): Elem =
		<div>
			<div>AssumeSkipTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val resProg = mkSkipProg(pre = unkProg.pre, post = unkProg.post)
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(resProg)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(resProg, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(resProg, frameSummary.getMacros())
    			resProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * AnnProgram Tactic
 * Replace AnnProgram by composition of variable declaration and the input AnnProgram
 */
case class InsertVariableTactic(aVar: Var, initVal: Term) extends PrgFunTactic {
	override val tName = "InsertVariable"

	def getHint(): Elem =
		<div>
			<div>InsertVariable</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (prog: ProgramAnn, _) =>
    			val midInv = prog.pre.addConjunct(aVar eqeq initVal)
    			val preInv = prog.pre
    			val postInv = prog.post // Decided not to add (aVar eqeq initVal) in the post
    			//TODO: ensure that the variable and init value are of same type.
    			//TODO: check that the variable is not already there in the Frame
    			val varDefProg = mkVarDefProg(preInv, aVar, initVal, midInv)

    			//Modify pre of the input program
    			val dupProg = prog.cloneobj.asInstanceOf[ProgramAnn]
    			dupProg.withNewParams(pre = midInv)

    			val resProg = mkComposition2(preInv, varDefProg, dupProg, postInv)
    			resProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


/**
 * StrengthenInvariantTactic
 */
case class StrengthenInvariantTactic(newInvs: List[TermBool]) extends PrgFunTactic {
	override val tName = "StrengthenInvariant"

	def getHint: Elem =
		<div>
			<div>StrengthenInvariantTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
		//TODO: should we allow multiple grdcmds
    		case (wp @ WhileProgC(loopInv, GuardedCmd(guard, up @ UnknownProgC(_)) :: Nil), frm: ProgramFrame) => {
    			val newInv: Option[TermBool] = loopInv.map(li => AndNTermBool(li :: newInvs))
    			val newUnknownProg = up.copy()
    			val newUnknownProg2 = newUnknownProg
    				.withNewParams(pre = up.pre.modifyTermBool(term => AndNTermBool(term :: newInvs)))
    				.withNewParams(post = up.post.modifyTermBool(term => AndNTermBool(term :: newInvs)))
    			val newWhileProg = wp.copy(loopInv = newInv, grdcmds = GuardedCmd(guard, newUnknownProg2) :: Nil)
    			val frmSummary = frm.getSummary
    			POGenerator.populatePOs(newWhileProg)(frmSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(newWhileProg, frmSummary.getMacros())
    			ProgramAnnPOProver.provePOs(newWhileProg, frmSummary.getMacros())
    			newWhileProg
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Introduces an If program construct.
 * - Applicable to UnkownProg node
 * - Takes list of guards as input. The list can be incomplete, or even empty.
 * - All the guards must be valid program expressions
 * - A guard can be added later by applying AddGuardTactic
 * - If the guards do cover all the cases the if program is tagged as "incomplete"
 * - An unknownProg is created for each supplied guard.
 */
case class IntroIfTactic(guards: List[TermBool]) extends PrgFunTactic {
	override val tName = "IntroIf"

	def getHint(): Elem = {
		import IntroIfTacticDoc.dName
		<div>
			<div class="tacticName">{ tName }</div>
			{
				PSTacticsHelper.paramTable(
					Tuple2(dName("guards"), oneColTable(guards, termToHtml _))
						:: Nil)
			}
			{ docLink(tName) }
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val pre = unkProg.pre
    			val post = unkProg.post

    			var upid = unkProg.upid + 1
    			def getUpId() = { upid += 1; upid }

    			val ifProg = mkIfProgCL(
    				pre = pre)(
    					grdcmds = guards.map { grd =>
    						mkGrdCmdC(
    							grd = grd)(
    								cmd = mkUnknownProgC(
    									pre = pre.addConjunct(grd))(
    										upid = getUpId //TODO: set proper id
    										)(
    											post = post))
    					})(
    						post = post)

    			val frameSummary = frame.getSummary
    			val gInvF = TermBool.mkConjunct(frameSummary.getGlobalInvs())
    			val macros = frameSummary.getMacros()
    			ProgramAnnPOProver.setGrdsCompleteFlag(ifProg, gInvF, macros)
    			println("ifProg grdsComplete: " + ifProg.grdsComplete.toString)
    			ifProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


///**
// * UnknownProg Tactic
// * StartIfDerivationTactic on stepin creates a IfDerivation object which acts as accumulator for
// * derived guarded commands.
// * On stepout, it creates a If program with the derived guarded commands.
// *
// */
//case class StartIfDerivationTactic(lhsVars: List[Var]) extends StepInTactic {
//	override val tName = "StartIfDerivation"
//
//	def getHint(): Elem = {
//		import XHTMLPrinters2.termToHtml
//		<div>
//			<table border='1' class='tablestyle'>
//				<tr><th colspan="2">StartIfDerivation</th></tr>
//				<tr><th align='left'>lhsVars</th><td>{ lhsVars.map(av => av.v).mkString(", ") }</td></tr>
//			</table>
//			<div>{ PSTacticsHelper.docLink(tName) }</div>
//		</div>
//	}
//
//	def getRelation() = None
//
//	override def funIn: (Any, Frame) ==> (Any, NonRootFrame) = {
//		case (pa: UnknownProg, frame) => {
//			val ifDerivation = new IfDerivation(pa, lhsVars, Nil, Nil, Nil)
//			val newFrame = new ProgramFrame(Nil, parent = frame, varList = Nil, valList = Nil, Nil)
//			(ifDerivation, newFrame)
//		}
//	}
//
//	/**
//	 * Create an IfProg from the derived meta-variables in the IfDerivation.
//	 * All the meta-variables must be derived
//	 */
//	def makeProg(pa: UnknownProg, ifDerivation: IfDerivation, outerFrame: Frame): Option[ProgramAnn] = {
//		val initProgOpt: Option[ProgramAnn] = {
//			def getRhs(fv: Var) = fv match {
//				//TODO: Initialize with any
//				case fv: VarInt => ConstInt("0")
//				case fv: VarBool => ConstBool("true")
//				case fv: VarArrayInt => ConstArrayInt("Array()")
//				case fv: VarArrayBool => ConstArrayBool("Array()")
//				case _ => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
//			}
//			val progs = ifDerivation.freshVars.map { fv =>
//				val rhs = getRhs(fv)
//				val newPost = pa.pre.addConjunct(fv eqeq rhs)
//				mkVarDefProg(pa.pre, fv, rhs, newPost)
//			}
//
//			progs match {
//				case Nil => None
//				case p :: Nil => Some(p)
//				case _ => Some(mkComposition(progs(0).pre, progs, progs.last.post))
//			}
//		}
//
//		val preProgOpt: Option[ProgramAnn] = {
//			if (!ifDerivation.assumedPres.isEmpty) {
//				val newPost = pa.pre.addConjunct(TermBool.mkConjunct(ifDerivation.assumedPres))
//				//TODO: fix ID. There might be another unknown prog with id = pa.upid + 1
//				Some(mkUnknownProg(pa.pre, pa.upid + 1, newPost))
//			} else {
//				None
//			}
//		}
//
//		val ifProgOpt: Option[IfProg] = {
//			val coverage = TermBool.mkConjunct(ifDerivation.guardedCmds map (_._1))
//			//TODO: ensure that coverage is "true"
//			val grdcmds = ifDerivation.guardedCmds map {
//				case (grd, derivedTerms) =>
//					val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)
//					val lhsRhsPairs = definedDerivedTerms map { case (primedVar, termO) => (MetaVarUtilities.mkUnprimedVar(primedVar), termO.get) }
//					val cmd = mkAssignmentTerms(pa.pre.addConjunct(grd), lhsRhsPairs, pa.post)
//					GuardedCmd(grd, cmd)
//			}
//			val ifProg = mkIfProg(pa.pre, grdcmds, pa.post)
//			Some(ifProg)
//		}
//
//		val resProg = {
//			(initProgOpt, preProgOpt, ifProgOpt) match {
//				case (Some(initProg), Some(preProg), Some(ifProg)) =>
//					mkComposition3(initProg.pre, initProg, preProg, ifProg, ifProg.post)
//				case (Some(initProg), None, Some(ifProg)) =>
//					mkComposition2(initProg.pre, initProg, ifProg, ifProg.post)
//				case (None, Some(preProg), Some(ifProg)) =>
//					mkComposition2(preProg.pre, preProg, ifProg, ifProg.post)
//				case (None, None, Some(ifProg)) =>
//					ifProg
//				case (None, None, None) => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
//			}
//		}
//		val outerFrameSummary = outerFrame.getSummary
//		POGenerator.populatePOs(resProg)(outerFrameSummary.getGlobalInvs())
//		//POZ3Prover.provePOs(resProg, outerFrameSummary.getMacros())
//		ProgramAnnPOProver.provePOs(resProg, outerFrameSummary.getMacros())
//
//		Some(resProg)
//	}
//
//	override def funOut: (Any, Frame) ==> (Any ==> Any) = {
//		case (pa: UnknownProg, outerFrame) => {
//			case ifDerivation: IfDerivation => makeProg(pa, ifDerivation, outerFrame) match {
//				case Some(ifProg) => ifProg
//				case None => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
//			}
//		}
//	}
//}

case class IntroSwapTactic( array: Var, index1: TermInt, index2: TermInt) extends PrgFunTactic {
	override val tName = "IntroSwap"

	def getHint(): Elem = {
	    import IntroSwapTacticDoc.dName
		<div>
	    	<div class="tacticName">{tName}</div>
	    	{
		    	PSTacticsHelper.paramTable(
		    	    Tuple2(dName("array"), termToHtml(array)) ::
		    	    Tuple2(dName("index1"), termToHtml(index1)) ::
		    	    Tuple2(dName("index2"), termToHtml(index2)) ::
		    	    Nil
		    	)
			}
			{docLink(tName)}
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>

    		    val swapDef = {
    				def old(aVar: VarArrayInt):VarArrayInt = aVar.addPrefix("_old_").asInstanceOf[VarArrayInt]

    				val x = VarInt("x")
    				val y = VarInt("y")
    				val arr = VarArrayInt("arr")
    				val oarr = old(arr)
    				//---------------------------
    				mkProcedureDef(
    					pre = (TermBool.TrueT).inv,
    					name = "arraySwap",
    					fParams = List(arr, x, y),
    					body = null,
    					ghostVars = Nil,
    					frameVars = List(arr),
    					post = (arr eqeq oarr.store(x, oarr.select(y)).store(y, oarr.select(x))).inv)
    			}
    		    val retVal = mkProcedureCall(
    		    	pre = unkProg.pre,
    		    	procDef = swapDef,
    		    	params = List(array, index1, index2),
    		    	post = unkProg.post)

    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(retVal)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(retVal, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(retVal, frameSummary.getMacros())
    			if (retVal.allPOsAreValid())
    				retVal
    			else
    			    throw new RuntimeException("Proof obligation for array swap can not be discharged.")
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


case class IntroAssignmentTactic(val lhsRhsTuples:List[(Var, Term)]) extends PrgFunTactic {
	override val tName = "IntroAssignment"

	def getHint(): Elem = {
	    import IntroAssignmentTacticDoc.dName
		import XHTMLPrinters2.termToHtml
		import XHTMLPrinters2.getArrDisplayLhsRhs

		val lhsRhsTuplesDisplay = lhsRhsTuples.map(t => getArrDisplayLhsRhs(t._1, t._2))

		<div>
	    	<div class="tacticName">{tName}</div>
	    	{
		    	PSTacticsHelper.paramTable(
		    	    Tuple2(dName("lhsRhsTuples"), twoColTable(lhsRhsTuplesDisplay, termToHtml _, termToHtml _))
		    	    :: Nil
		    	)
			}
			{docLink(tName)}
		</div>
	}

	def getRelation() = None


	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: ProgramAnn, frame) if unkProg.isInstanceOf[UnknownProg] ||  unkProg.isInstanceOf[AssumeProg]=>
    		    val tmp = 15
                val retVal = mkAssignmentTerms(unkProg.pre, lhsRhsTuples, unkProg.post)
    			//retVal.inferAnn
    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(retVal)(frameSummary.getGlobalInvs())
    			ProgramAnnPOProver.provePOs(retVal, frameSummary.getMacros())

    			if (retVal.allPOsAreValid())
    				retVal
    			else
    			    throw new RuntimeException("Proof obligation for the assignment can not be discharged.")
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * UnknownProgram Tactic
 * Insert given assignement at the end
 */
case class IntroAssignmentEndTactic(lhsRhsTuples:List[(Var, Term)]) extends PrgFunTactic {
	override val tName = "IntroAssignmentEnd"

	def getHint(): Elem = {
	    import IntroAssignmentTacticDoc.dName
		import XHTMLPrinters2.termToHtml
		import XHTMLPrinters2.getArrDisplayLhsRhs

		val lhsRhsTuplesDisplay = lhsRhsTuples.map(t => getArrDisplayLhsRhs(t._1, t._2))

		<div>
	    	<div class="tacticName">{tName}</div>
	    	{
		    	PSTacticsHelper.paramTable(
		    	    Tuple2(dName("lhsRhsTuples"), twoColTable(lhsRhsTuplesDisplay, termToHtml _, termToHtml _))
		    	    :: Nil
		    	)
			}
			{docLink(tName)}
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: ProgramAnn, frame) if unkProg.isInstanceOf[UnknownProg] ||  unkProg.isInstanceOf[AssumeProg]=>
    			val (inv1, inv3) = (unkProg.pre, unkProg.post)
    			// {preI} unkProg {postI}
    			// {preI} unkNew {weakestPreI} lhs = rhs {postI}
    			val inv2 = inv3.term.replaceVarsSim(lhsRhsTuples.toMap).inv //TODO: should be replace Free Var
    			val unkNew = mkUnknownProg(inv1, 0, inv2) //TODO: set proper id
    			val asgnProg = mkAssignmentTerms(inv2, lhsRhsTuples, inv3)
    			mkComposition(inv1, List(unkNew, asgnProg), inv3)
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


/**
 * Unknown Program Tactic
 * Replaces specific terms in the post condition by a fresh variable'.
 * The introduced variable in initialized to 'initValue'
 * The specified bounds are conjuncted with the post-condition.
 */
case class RTVInPost2Tactic(displayId: Int, variable: Var, initValue: Term, bounds: TermBool) extends PrgFunTactic {
	override val tName = "RTVInPost2"

	def getHint(): Elem ={
	    import RTVInPost2TacticDoc.dName
		<div>
	    	<div class="tacticName">{tName}</div>
	    	{
		    	PSTacticsHelper.paramTable(
		    	    Tuple2(dName("displayId"), displayId)
		    	    :: Tuple2(dName("variable"), termToHtml(variable) )
		    	    :: Tuple2(dName("initValue"), termToHtml(initValue) )
		    	    :: Tuple2(dName("bounds"), termToHtml(bounds) )
		    	    :: Nil
		    	)
			}
			{docLink(tName)}
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (unkProg: UnknownProg, frame) =>
    			val compProg = {
    				if(variable.getType != initValue.getType)
    					PSError(ErrVariableInitValueTypeMismatch())

    				val initProg: ProgramAnn = mkVarDefProg(
    					pre = unkProg.pre,
    					lhs = variable,
    					rhs = mkExprProg(unkProg.pre, initValue, unkProg.pre.addConjunct(variable eqeq initValue).setRvVar(variable)),
    					post = unkProg.pre.addConjunct(variable eqeq initValue))

    				val newUnk: ProgramAnn = {
    					var subTerm: Option[Term] = None

    					val replacedF = (unkProg.post.term.mapSubTerms {
    						case term if term.displayId == displayId && term.getType == variable.getType =>
    							subTerm = Some(term)
    							variable
    					}).asInstanceOf[TermBool]

    					if(subTerm.isEmpty)
    						PSError("Sub-term not found. Or the term and variable types do not match")

    					val newPostF = replacedF && bounds && (variable eqeq subTerm.get)

    					val newPostInv = InvariantT(unkProg.post.loc, newPostF, unkProg.post.rvVar)

    					mkUnknownProg(pre = initProg.post, unkProg.id, post = newPostInv)
    				}

    				mkComposition(
    					pre = unkProg.pre,
    					programs = initProg :: newUnk :: Nil,
    					post = unkProg.post)
    			}

    			val frameSummary = frame.getSummary
    			POGenerator.populatePOs(compProg)(frameSummary.getGlobalInvs())
    			//POZ3Prover.provePOs(compProg, frameSummary.getMacros())
    			ProgramAnnPOProver.provePOs(compProg, frameSummary.getMacros())
    			compProg
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


/**
 * Collapse Composition Tactic.
 * Applicable to any program with atleast one nested composition.
 * Collapse all the nested compositions. Works bottomup.
 */
case class CollapseCompositionsTactic() extends PrgFunTactic {
	override val tName = "CollapseCompositions"

	def getHint(): Elem =
		<div>
			<div>{tName}</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

    override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (pa: ProgramAnn, _) =>

    			val collapseStrategy = strategy {
    				case NestedComposition(stmts)=>
    					Some(mkComposition(pa.pre, stmts, pa.post))
    				case _ =>
    					None
    			}

    			val res = everywherebu(collapseStrategy)(pa)

    			res match {
    				case Some(modifiedProg) => modifiedProg.asInstanceOf[ProgramAnn]
    				case None =>
    					throw new RuntimeException("CollapseCompositionTactic application failed.")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

