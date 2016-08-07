package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz.{ Forall => SForall, Const => SConst, _ }
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
import scala.util.Try

/*
case class StepInPostFormulaTactic() extends StepInTactic {
	override val tName = "StepInPostFormula"
	//extractPostFormula
    def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)]	= Try {
	    (outerObj, outerFrame) match {
    		case (pa: ProgramAnn, frame: Frame) =>
    			val post = pa.post.term
    			val newFrame = new ProgramFrame(Nil, Some(frame), Nil, Nil, Nil)
    			CalcProgStep((post, newFrame), None)
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

    def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep]	= Try {
        (oldOuterObj, outerFrame) match {
    		case (pa: ProgramAnn, _) =>
    			newInnerObj match {
    				case newPostF: TermBool =>
    					//pa.copyPost(pa.post.copy(term = newPostF))
    					CalcProgStep(pa.withNewParams(post = pa.post.copy(term = newPostF)), None)
    				case _ =>
    				    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
        }
	}

	def getHint(): Elem =
		<div>
			<div>StepInPostFormula</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
}
*/
/**
 * UnknownProg Tactic
 * Replace a UnknownProg with AssignmentProof object.
 * Create IfProg if the final proofstep formula is a program expr.
 * TODO: does not work with the AssumePreTactic.
 */
case class StartAsgnDerivationTactic(lhsVars: List[Var]) extends StepInTactic {
	override val tName = "StartAsgnDerivation"

	def getHint(): Elem = <div>
		<div>StartAsgnDerivation</div>
		<div>{ PSTacticsHelper.docLink(tName) }</div>
	</div>
	def getRelation() = None

	override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (pa: UnknownProg, frame) => {
    			val metaVariables = lhsVars.map { _.rename(name => name + "'") }
    			val derivedTerms: List[(Var, Option[Term])] =
    				metaVariables.map { (_, None) }
    			val ad = new AsgnDerivation(pa, lhsVars, derivedTerms)
    			val newFrame = ProgramFrame.addVals(ProgramFrame.mkEmptyFrame(parent = Some(frame)), metaVariables)

    			(ad, newFrame)
    		}
	    }
	}

	/**
	 * Create assignment program from the derived meta-variables in the AsgnDerivation
	 * All meta-variables must be derived
	 */
	def makeAssignmentProg(pa: UnknownProg, asgnDerivation: AsgnDerivation): Option[Assignment] = {
		val varTermOList = asgnDerivation.derivedTerms
		val allMetavariablesDerived = asgnDerivation.derivedTerms.forall { case (aVar, termO) => termO.isDefined }
		if (!allMetavariablesDerived)
			None
		else {
			val varTermList = asgnDerivation.derivedTerms map {
				case (aVar, termO) => (Var.mkVar(aVar.v.replace("'", ""), aVar.getType), termO.get)
			}
			Some(mkAssignmentTerms(pa.pre, varTermList, pa.post))
		}
	}

    override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
        (oldOuterObj, outerFrame) match {
    		case (CalcProgStep(pa: UnknownProg, focusIdOpt), _) =>
			    newInnerObj match {
    				case asgnDerivation: AsgnDerivation => makeAssignmentProg(pa, asgnDerivation) match {
    					case Some(asgnProg) => CalcProgStep(asgnProg, None) //set proper focusIdOpt
    					case None => throw new RuntimeException("StartAsgnDerivationTactic stepout failed. Unable to create assignment program")
    				}
			    }
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
        }
	}
}


/**
 * Applicable to UnknownProg
 * - Creates a CalcProof node with PO of the program with meta variables.
 * - On stepout, may create assignment, composition, or if depending on the accumulated values
 * - On stepout, the created program will be merged with the context program
 * if they are of the same type (for example, if inside if)
 *
 */
case class StepIntoBATactic2(lhsVars: List[Var]) extends StepInTactic {

	override val tName = "StepIntoBA"

	def getHint(): Elem = {
		import StepIntoBATacticDoc.dName
		<div>
			<div class="tacticName">{ tName }</div>
			{
				PSTacticsHelper.paramTable(
					Tuple2(dName("lhsVars"), lhsVars.map(aVar => aVar.v).mkString(", "))
						:: Nil)
			}
			{ docLink(tName) }
		</div>
	}

	def getRelation() = None

	val primedVars = MetaVarUtilities.primedVars(lhsVars)

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (unkProg: UnknownProg, frame: ProgramFrame) => {

    			val primedVars = MetaVarUtilities.primedVars(lhsVars)

    			def getBA(): TermBool = {
    				//TODO: ensure that quantifiers (dummies) are handled properly
    				unkProg.pre.term.impl(unkProg.post.term.replaceVarsSim(lhsVars, primedVars))
    			}

    			val po = getBA()

    			//TODO: do we need to add the metavariables to context as done in StartAsgnDerivationTactic funin
    			//TODO: Check relation
    			val newFrame = FormulaFrame.mkEmptyFrame(parent = Some(frame), relation = RImplBoolFn)

    			val calcProofStep = CalcProofStep(
    				coreObj = po,
    				guard = TermBool.TrueT,
    				derivedTerms = primedVars.map { (_, None) }, //TODO: why do we need to pass this.
    				freshVariables = Nil,
    				assumedPreList = Nil,
    				metaVars = primedVars)

    			(calcProofStep, newFrame)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
    		case (CalcProgStep(unkProg: UnknownProg, None), outerFrame) =>
    			newInnerObj match { //case finalStep @ CalcProofStep(TermBool.TrueT, grd, derivedTerms, fv, apres) =>
    				//	makeProg2(unkProg, grd, derivedTerms, fv, apres, outerFrame, false)
    				case finalStep @ CalcProofStep(TermBool.TrueT, _grd, derivedTerms, fvs, apres, _metaVars) =>
    					CalcProgStep(makeProgWithPre(unkProg, derivedTerms, fvs, apres), None) //TODO: set proper focusId
    				//case finalStep @ CalcProofStep(_, grd, derivedTerms, fv, apres) =>
    				//	makeProg2(unkProg, grd, derivedTerms, fv, apres, outerFrame, true)
    				case _ =>
    				    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	/**
	 * Create an output prog from the derived meta-variables
	 * All the meta-variables must be derived
	 */
	def makeProg2(unkProg: UnknownProg, grd: TermBool, derivedTerms: List[(Var, Option[Term])],
		fvs: List[Var], apres: List[TermBool], outerFrame: Frame, useWP: Boolean): ProgramAnn =
		{
			val pre = unkProg.pre
			val post = unkProg.post
			val assumedPre = TermBool.mkConjunct(apres)

			val lhsRhsPairs = {
				val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)

				definedDerivedTerms map {
					case (primedVar, termO) =>
						(MetaVarUtilities.mkUnprimedVar(primedVar), termO.get)
				}
			}

			val cmd = {
				CompUtil(None, pre.addConjunct(grd))
					.compose(fpre => {
						val progs = fvs.map { fv =>
							val rhs = getRhs(fv)
							val newPost = fpre.addConjunct(fv eqeq rhs)
							mkVarDefProg(fpre, fv, rhs, newPost)
						}
						progs match {
							case Nil => None
							case p :: Nil => Some(p)
							case _ => Some(mkComposition(progs(0).pre, progs, progs.last.post))
						}
					}).compose(fpre =>
						if (useWP) {
							Some(mkUnknownProg(
								pre = fpre,
								upid = 0,
								post = post.term.replaceVarsSim(lhsRhsPairs.toMap).inv))
						} else if (!apres.isEmpty)
							Some(mkUnknownProg(
							pre = fpre,
							upid = 0,
							post = pre.addConjunct(grd).addConjunct(assumedPre)))
						else
							None).compose(fpre =>
						Some(mkAssignmentTerms(
							pre = fpre,
							lhsRhsPairs = lhsRhsPairs,
							post = post)))
					.compOpt.get
			}

			//G FV Theta S
			if (grd == TermBool.TrueT)
				cmd
			else {
				val ifProg = mkIfProg1(
					pre = pre,
					grdcmd = {
						GuardedCmd(
							guard = grd,
							cmd = cmd)
					},
					post = post)

				val frameSummary = outerFrame.getSummary
				val gInvF = TermBool.mkConjunct(frameSummary.getGlobalInvs())
				val macros = frameSummary.getMacros()
				ProgramAnnPOProver.setGrdsCompleteFlag(ifProg, gInvF, macros)

				ifProg
			}
		}

	def getRhs(fv: Var) = fv match {
		//TODO: Initialize with any
		case fv: VarInt => ConstInt("0")
		case fv: VarBool => ConstBool("true")
		case fv: VarArrayInt => ConstArrayInt("Array()")
		case fv: VarArrayBool => ConstArrayBool("Array()")
		case _ => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
	}

	/**
	 * Create an output prog from the derived meta-variables
	 * TODO: All the meta-variables must be derived
	 */
	def makeProgWithPre(unkProg: UnknownProg, derivedTerms: List[(Var, Option[Term])],
		fvs: List[Var], apres: List[TermBool]): ProgramAnn =
		{
			val ret = mkAssignmentTerms(
				pre = unkProg.pre,
				lhsRhsPairs = {
					val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)

					definedDerivedTerms map {
						case (primedVar, termO) =>
							(MetaVarUtilities.mkUnprimedVar(primedVar), termO.get)
					}
				},
				post = unkProg.post)
			//ret.withNewParams(assumedPres = apres, assumedFVs = fvs)
			//TODO: Propage assumedPres and assumedFVs to program mode.
			ret
		}
}

/**
 * Applicable to UnknownProg
 *
 */
case class StepIntoBATactic(lhsVars: List[Var]) extends StepInTactic {

	override val tName = "StepIntoBA"

	def getHint(): Elem = {
		import StepIntoBATacticDoc.dName
		<div>
			<div class="tacticName">{ tName }</div>
			{
				PSTacticsHelper.paramTable(
					Tuple2(dName("lhsVars"), lhsVars.map(aVar => aVar.v).mkString(", "))
						:: Nil)
			}
			{ docLink(tName) }
		</div>
	}

	def getRelation() = None

	val primedVars = MetaVarUtilities.primedVars(lhsVars)

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {

	    val CalcProgStepWithInner(_prog, _focusIdOpt, unkProgOpt) = outerObj

	    (outerObj, outerFrame) match {
    		case (CalcProgStepWithInner(_prog, _focusIdOpt, Some(unkProg: UnknownProg)), frame: ProgramFrame) => {
    			val primedVars = MetaVarUtilities.primedVars(lhsVars)

    			def getBA(): TermBool = {
    				//TODO: ensure that quantifiers (dummies) are handled properly
    				unkProg.pre.term.impl(unkProg.post.term.replaceVarsSim(lhsVars, primedVars))
    			}

    			//TODO: do we need to add the metavariables to context as done in StartAsgnDerivationTactic funin
    			//TODO: Check relation
    			val newFrame = FormulaFrame.mkEmptyFrame(parent = Some(frame), relation = RImplBoolFn)

    			val calcProofStep = CalcProofStep(
    				coreObj = getBA(),
    				guard = TermBool.TrueT,
    				derivedTerms = primedVars.map { (_, None) }, //TODO: why do we need to pass this.
    				freshVariables = Nil,
    				assumedPreList = Nil,
    				metaVars = primedVars)

    			(calcProofStep, newFrame)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

    override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
    		case (CalcProgStepWithInner(aProg, focusIdOpt, Some(f_a_unkProg: UnknownProg)), frame: ProgramFrame) =>
    			newInnerObj match {
    				case finalStep @ CalcProofStep(TermBool.TrueT, _grd, derivedTerms, fvs, apres, _metaVars) =>

    				    val f_dProg = makeProgWithPre(f_a_unkProg, derivedTerms, fvs, apres)

    					val dProgOpt: Option[ProgramAnn] = replaceSubProgDisplayIdOpt(aProg, focusIdOpt, f_dProg)
    					dProgOpt match {
    					    case Some(dProg) => CalcProgStep(dProg, focusIdOpt)
    					    case None => throw new RuntimeException("Tactic application failed.")
    					}
    				case _ =>
    				    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
    }

	/**
	 * Create an output prog from the derived meta-variables
	 * All the meta-variables must be derived
	 */
	def makeProg2(unkProg: UnknownProg, grd: TermBool, derivedTerms: List[(Var, Option[Term])],
		fvs: List[Var], apres: List[TermBool], outerFrame: Frame, useWP: Boolean): ProgramAnn =
		{
			val pre = unkProg.pre
			val post = unkProg.post
			val assumedPre = TermBool.mkConjunct(apres)

			val lhsRhsPairs = {
				val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)

				definedDerivedTerms map {
					case (primedVar, termO) =>
						(MetaVarUtilities.mkUnprimedVar(primedVar), termO.get)
				}
			}

			val cmd = {
				CompUtil(None, pre.addConjunct(grd))
					.compose(fpre => {
						val progs = fvs.map { fv =>
							val rhs = getRhs(fv)
							val newPost = fpre.addConjunct(fv eqeq rhs)
							mkVarDefProg(fpre, fv, rhs, newPost)
						}
						progs match {
							case Nil => None
							case p :: Nil => Some(p)
							case _ => Some(mkComposition(progs(0).pre, progs, progs.last.post))
						}
					}).compose(fpre =>
						if (useWP) {
							Some(mkUnknownProg(
								pre = fpre,
								upid = 0,
								post = post.term.replaceVarsSim(lhsRhsPairs.toMap).inv))
						} else if (!apres.isEmpty)
							Some(mkUnknownProg(
							pre = fpre,
							upid = 0,
							post = pre.addConjunct(grd).addConjunct(assumedPre)))
						else
							None).compose(fpre =>
						Some(mkAssignmentTerms(
							pre = fpre,
							lhsRhsPairs = lhsRhsPairs,
							post = post)))
					.compOpt.get
			}

			//G FV Theta S
			if (grd == TermBool.TrueT)
				cmd
			else {
				val ifProg = mkIfProg1(
					pre = pre,
					grdcmd = {
						GuardedCmd(
							guard = grd,
							cmd = cmd)
					},
					post = post)

				val frameSummary = outerFrame.getSummary
				val gInvF = TermBool.mkConjunct(frameSummary.getGlobalInvs())
				val macros = frameSummary.getMacros()
				ProgramAnnPOProver.setGrdsCompleteFlag(ifProg, gInvF, macros)

				ifProg
			}
		}

	def getRhs(fv: Var) = fv match {
		//TODO: Initialize with any
		case fv: VarInt => ConstInt("0")
		case fv: VarBool => ConstBool("true")
		case fv: VarArrayInt => ConstArrayInt("Array()")
		case fv: VarArrayBool => ConstArrayBool("Array()")
		case _ => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
	}

	def buildComposition(prgs: List[ProgramAnn]) = prgs match {
		case prg :: Nil => prg
		case Nil => throw new RuntimeException("list should not be empty")
		case _ => Composition(prgs, prgs.head.pre, prgs.last.post)
	}

	/**
	 * Create an output prog from the derived meta-variables
	 */
	def makeProgWithPre(unkProg: UnknownProg, derivedTerms: List[(Var, Option[Term])],
		fvs: List[Var], apres: List[TermBool]): ProgramAnn =
		{
			val varDefs = fvs map { fv =>
				VarDefProg(fv, None, unkProg.pre, unkProg.pre)
			}

			var iPre = unkProg.pre
			val assumeProgs = apres map { apre =>
				val ap = AssumeProg(apre, iPre, iPre.addConjunct(apre))
				iPre = iPre.addConjunct(apre)
				ap
			}

			val asgn = mkAssignmentTerms(
				pre = iPre,
				lhsRhsPairs = {
					val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)

					definedDerivedTerms map {
						case (primedVar, termO) =>
							(MetaVarUtilities.mkUnprimedVar(primedVar), termO.get)
					}
				},
				post = unkProg.post)
			buildComposition(varDefs ++ assumeProgs :+ asgn)
		}
}

/**
 * AsgnDerivation Tactic
 * Start Guarded Command Derivation Tactic. Creates a CalcProof node with PO of the program with meta variables.
 */
case class StartGCmdDerivationTactic() extends StepInTactic {
	override val tName = "StartGCmdDerivation"

	def getHint(): Elem = {
		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">StartGCmdDerivation</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}
	def getRelation() = None

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (ifDerivation: IfDerivation, frame: ProgramFrame) => {
    			val po = ifDerivation.getFormula.simplify
    			val newFrame = new FormulaFrame(Nil, parent = Some(frame), dummies = Nil, axioms = Nil, conjectures = Nil, relation = EquivBoolFn)
    			val derivedTerms: List[(Var, Option[Term])] = ifDerivation.primedVars.map { primedVar => (primedVar, None) }
    			(CalcProofStep(po, TermBool.TrueT, derivedTerms, ifDerivation.freshVars, ifDerivation.assumedPres, Nil), newFrame)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
        (oldOuterObj, outerFrame) match {
    		case (ifDerivation: IfDerivation, _) =>
    			newInnerObj match {
    				case finalStep @ CalcProofStep(TermBool.TrueT, grd, derivedTerms, fv, apres, Nil) =>
    					val updatedGuardedCmds = ifDerivation.guardedCmds ++ List((grd, derivedTerms))
    					new IfDerivation(ifDerivation.unkProg, ifDerivation.varList, updatedGuardedCmds, fv, apres)
            		case _ =>
            		    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
       		    throw new RuntimeException("Tactic not applicable")
        }
	}
}

/**
 * AsgnDerivation Tactic
 * Step into PO of the program with meta variables.
 * Deprecated. Use StepIntoBA
 */
case class StepIntoPO() extends StepInTactic {
	override val tName = "StepIntoPO"

	def getHint(): Elem =
		<div>
			<div>StepIntoPO</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (asgnDerivation: AsgnDerivation, frame: ProgramFrame) => {
    			val po = asgnDerivation.getFormula.simplify
    			val newFrame = FormulaFrame.mkEmptyFrame(parent = Some(frame), relation = RImplBoolFn)
    			(CalcProofStep(po, TermBool.TrueT, asgnDerivation.derivedTerms, Nil, Nil, Nil), newFrame)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
            case (asgnDerivation2: AsgnDerivation, _) =>
    			newInnerObj match {
    				case finalStep @ CalcProofStep(TermBool.TrueT, TermBool.TrueT, derivedTerms, fv, apres, _metaVars) =>
    					//TODO: handle fv and apres
    					new AsgnDerivation(asgnDerivation2.unkProg, asgnDerivation2.varList, derivedTerms)
                    case _ =>
            		    throw new RuntimeException("Tactic not applicable")
    			}
            case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Applicaable to IfProg
 * - Creates a CalcProof node with PO of the if program with meta variables.
 * - On stepout, adds a guarded command to the IfProg.
 */
case class StepIntoIFBATactic(lhsVars: List[Var]) extends StepInTactic {

	override val tName = "StepIntoIFBA"

	def getHint(): Elem = {
		import StepIntoIFBATacticDoc.dName
		<div>
			<div class="tacticName">{ tName }</div>
			{
				PSTacticsHelper.paramTable(
					Tuple2(dName("lhsVars"), lhsVars.map(aVar => aVar.v).mkString(", "))
						:: Nil)
			}
			{ docLink(tName) }
		</div>
	}

	def getRelation() = None

	val primedVars = MetaVarUtilities.primedVars(lhsVars)

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (oIfProg: IfProg, frame: ProgramFrame) => {

    			val primedVars = MetaVarUtilities.primedVars(lhsVars)

    			def getBA(): TermBool = {
    				//TODO: ensure that quantifiers (dummies) are handled properly
    				oIfProg.pre.term.impl(oIfProg.post.term.replaceVarsSim(lhsVars, primedVars))
    			}

    			val po = getBA()

    			//TODO: do we need to add the metavariables to context as done in StartAsgnDerivationTactic funin
    			//TODO: Check relation
    			val newFrame = FormulaFrame.mkEmptyFrame(parent = Some(frame), relation = RImplBoolFn)

    			val calcProofStep = CalcProofStep(
    				coreObj = po,
    				guard = TermBool.TrueT,
    				derivedTerms = primedVars.map { (_, None) }, //TODO: why do we need to pass this.
    				freshVariables = Nil,
    				assumedPreList = Nil,
    				metaVars = primedVars)

    			(calcProofStep, newFrame)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
    		case (CalcProgStep(oIfProg: IfProg, focusIdOpt), outerFrame) =>
    			newInnerObj match {
    				case finalStep @ CalcProofStep(TermBool.TrueT, grd, derivedTerms, fv, apres, _metaVars) =>
    					CalcProgStep(makeProg2(oIfProg, grd, derivedTerms, fv, apres, outerFrame, useWP = false), focusIdOpt) //TODO: Set proper focusIdOpt
    				case finalStep @ CalcProofStep(_, grd, derivedTerms, fv, apres, _metaVars) =>
    					CalcProgStep(makeProg2(oIfProg, grd, derivedTerms, fv, apres, outerFrame, useWP = true), focusIdOpt)  //TODO: Set proper focusIdOpt
    				case _ =>
    				    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	/**
	 * Create an output prog from the derived meta-variables
	 * All the meta-variables must be derived
	 */
	def makeProg2(oIfProg: IfProg, grd: TermBool, derivedTerms: List[(Var, Option[Term])],
		fvs: List[Var], apres: List[TermBool], outerFrame: Frame, useWP: Boolean): ProgramAnn =
		{
			val pre = oIfProg.pre
			val post = oIfProg.post
			val assumedPre = TermBool.mkConjunct(apres)

			val lhsRhsPairs = {
				val definedDerivedTerms = derivedTerms.filter(_._2.isDefined)

				definedDerivedTerms map {
					case (primedVar, termO) =>
						(MetaVarUtilities.mkUnprimedVar(primedVar), termO.get)
				}
			}

			val cmd = {
				CompUtil(None, pre.addConjunct(grd))
					.compose(fpre => {
						val progs = fvs.map { fv =>
							val rhs = getRhs(fv)
							val newPost = fpre.addConjunct(fv eqeq rhs)
							mkVarDefProg(fpre, fv, rhs, newPost)
						}
						progs match {
							case Nil => None
							case p :: Nil => Some(p)
							case _ => Some(mkComposition(progs(0).pre, progs, progs.last.post))
						}
					}).compose(fpre =>
						if (useWP) {
							Some(mkUnknownProg(
								pre = fpre,
								upid = 0,
								post = post.term.replaceVarsSim(lhsRhsPairs.toMap).inv))
						} else if (!apres.isEmpty)
							Some(mkUnknownProg(
							pre = fpre,
							upid = 0,
							post = pre.addConjunct(grd).addConjunct(assumedPre)))
						else
							None).compose(fpre =>
						Some(mkAssignmentTerms(
							pre = fpre,
							lhsRhsPairs = lhsRhsPairs,
							post = post)))
					.compOpt.get
			}

			//G FV Theta S
			if (grd == TermBool.TrueT)
				cmd
			else {
				val ifProg = mkIfProg(
					pre = pre,
					grdcmds = {
						oIfProg.grdcmds ++
							List(GuardedCmd(
								guard = grd,
								cmd = cmd))
					},
					post = post)

				val frameSummary = outerFrame.getSummary
				val gInvF = TermBool.mkConjunct(frameSummary.getGlobalInvs())
				val macros = frameSummary.getMacros()
				ProgramAnnPOProver.setGrdsCompleteFlag(ifProg, gInvF, macros)

				ifProg
			}
		}

	def getRhs(fv: Var) = fv match {
		//TODO: Initialize with any
		case fv: VarInt => ConstInt("0")
		case fv: VarBool => ConstBool("true")
		case fv: VarArrayInt => ConstArrayInt("Array()")
		case fv: VarArrayBool => ConstArrayBool("Array()")
		case _ => throw new RuntimeException("StartIfDerivationTactic stepout failed. Unable to create if program")
	}

}
