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
import scala.util.{Try, Success => TSuccess, Failure => TFailure}
/*
class AddMacrosTactic(macros: List[Macro]) extends StepInTactic {
	//Create a new frame with added macros
	override def funIn: (CalcStep, Frame) ==> (CalcStep, NonRootFrame) = {
		/** Why is addMacroTactic not enabled for RootFrame?
		 *  refer addMacros scaladoc in NonRootFrame class */
		case (nodeObj, frame: NonRootFrame) =>
			val newFrame = NonRootFrame.addMacros(frame, macros)
			(nodeObj, newFrame)
	}

	//Expand the macros
	override def funOut: (CalcStep, Frame) ==> (CalcStep ==> CalcStep) = {
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

/*
/**
 * Formula Tactic
 * replace an oldF subformula with the newF
 * TODO: use z3 to validate
 */
case class ReplaceByEquivTactic(oldF: TermBool, newF: TermBool) extends FunTactic {
	override val tName = "ReplaceByEquiv"

	def getHint(): Elem =
		<div>
			<div>ReplaceByEquiv</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None
	//i < n + 1
	//i < n \/ i = n
	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (f: TermBool, _) =>
    			val replace = rule {
    				case cf: TermBool if (cf == oldF) => newF
    				case a => a
    			}
    			val res = bottomup(replace)(f)
    			res.get
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}
*/
/**
 * CalcProofStep Tactic
 *  Replaces the primed variables with the given terms
 */
case class InstantiateMetaTactic(primedVarTermList: List[(Var, Term)]) extends FunTactic {
	override val tName = "InstantiateMeta"
	val primedVarList = primedVarTermList map { case (v, _) => v }
	val termList = primedVarTermList map { case (_, t) => t }
	val substMap: Map[Var, Term] = (primedVarList zip termList).toMap

	def getHint(): Elem =
		<div>
			<div>Instantiated Metavariables: </div>
			<div class="spacediv"></div>
			{
				val divList = primedVarList.map(pv => <div>{ XHTMLPrinters2.termToHtml(pv) }</div>)
				divList.head ++ (divList.tail map { div => <div>, </div> ++ div })
			}
			<div class="spacediv"></div>
			<div> = </div>
			<div class="spacediv"></div>
			{
				val termDivList = termList.map(tv => <div>{ XHTMLPrinters2.termToHtml(tv) }</div>)
				termDivList.head ++ (termDivList.tail map { div => <div>, </div> ++ div })
			}
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	val IsPrimeR = """.*'""".r

	def isPrime(aVar: Var) = aVar.v match {
		case IsPrimeR() => true
		case _ => false
	}

	def primeTest() = primedVarList.forall { isPrime }

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f, grd, derivedTerms, freshVariables, assumedPre, metaVars), frm: FormulaFrame) =>
    			if (!primeTest)
    				throw new RuntimeException("Variables are not primed.")

    			val freeVarsInAssumptions = frm.getSummary.formulaFrameSummary.getAxiomsAndConjectures.getFreeVars
    			val primeFreeVars = primedVarList.intersect(freeVarsInAssumptions)
    			if (!primeFreeVars.isEmpty)
    				throw new RuntimeException("""Can not instantiate metavariables appearing as
    		        	free variables in context assumptions.""")

    			val replace = rule {
    				case aVar: Var if substMap contains aVar =>
    					PSDbg.writeln0(<a>{ aVar } is substituted by { substMap.get(aVar).get }</a>.text)
    					substMap.get(aVar).get
    				case a => a
    			}
    			val res = bottomup(replace)(f)
    			val newTB: Term = res.get.asInstanceOf[Term] //Was TermBool
    			val newDerivedTerms = derivedTerms.map {
    				case (aVar, termO) =>
    					if (substMap contains aVar) {
    						(aVar, substMap.get(aVar))
    					} else
    						(aVar, termO)
    			}
    			val newMetaVars = metaVars diff primedVarList
    			CalcProofStep(newTB, grd, newDerivedTerms, freshVariables, assumedPre, newMetaVars)
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * CalcProofStep Tactic
 *  Guesses guard. conjoing the guard with already existing guard.
 *  Deprecated
 */
case class GuessGuardTactic(guard: TermBool) extends FunTactic {
	override val tName = "GuessGuard"

	def getHint(): Elem =
		<div>
			<div>Guessing Guard value: </div>
			<div class="spacediv"></div>
			{ XHTMLPrinters2.termToHtml(guard) }
			<div class="spacediv"></div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

    override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
        (nodeObj, frame) match {
            case (CalcProofStep(f, oldGrd, derivedTerms, fv, apre, metaVars), _) =>
                CalcProofStep(f, (oldGrd && guard).simplify, derivedTerms, fv, apre, metaVars)
            case _ =>
                throw new RuntimeException("Tactic not applicable")
        }
    }
}

/**
 * AsgnDerivStep Tactic
 * Steps into the consequent of an implication.
 * The antecedent is added to the axioms.
 * TODO: Stepout is not proper. usecase stepIntoConsequentIssue derivation
 * Workaround: Use StepIntoSubformula Tactic
 */
case class StepIntoConsequentTactic() extends StepInTactic {
	override val tName = "StepIntoConsequent"

	def getHint(): Elem =
		<div>
			<div>Step into consequent of the implication.</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (CalcProofStep(ImplTermBool(tb1, tb2), grd, derivedTerms, fv, apre, metaVars), frame: FormulaFrame) => {
    			val prog = CalcProofStep(tb2, grd, derivedTerms, fv, apre, metaVars)
    			val newFrame = new FormulaFrame(parent = Some(frame), relation = frame.relation, axioms = tb1 :: Nil,
    				macros = Nil, dummies = Nil, conjectures = Nil)
    			(prog, newFrame)
    		}
            case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
    		case (step @ CalcProofStep(coreObj: TermBool, _, _, _, _, _), _) =>
    		    newInnerObj match {
        			case step2 @ CalcProofStep(coreObj2: TermBool, guard2, derivedTerms2, freshVariables2, assumedPreList2, metaVars2) =>
        				CalcProofStep(ImplTermBool(coreObj, coreObj2), guard2,
        					derivedTerms2, freshVariables2, assumedPreList2, metaVars2)
        			case _ =>
        			    throw new RuntimeException("Tactic not applicable")
    		    }
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Applies to CalcProofStep.
 * Steps into a desired subformula of the formula of the CalcProofStep
 * Context is updated automatically. (assumptions, dummies, and relation are updated)
 */
case class StepIntoSubFormulaTactic(subId: Int) extends StepInTactic with StepIntoSubTerm2 {
	override val tName = "StepIntoSubFormula"

	def getHint(): Elem =
		<div>
			<div>Step into subformula with id { subId }</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

    override def funIn(outerObj: CalcStep, outerFrame: Frame): Try[(CalcStep, Frame)] = Try {
	    (outerObj, outerFrame) match {
    		case (cps @ CalcProofStep(f_g: Term, grd, derivedTerms, fv, apre, metaVars), frame: FormulaFrame) => {
    			/**
    			 * Create  a new subframe with appropriate parent.
    			 * updateSubFrame does not modify the parent
    			 */
    			val subFrame = FormulaFrame.mkEmptyFrame(parent = Some(frame), relation = frame.relation)

    			getSubTermSubFrame(f_g, subFrame)(subId) match {
    				case None => throw new RuntimeException("StepIntoSubFormulaTactic: Unable to create subFrame. Possible reason: subformula not found.")
    				case Some((subF, subFrame)) =>
    					(CalcProofStep(subF, grd, derivedTerms, fv, apre, metaVars), subFrame)
    			}
    		}
            case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	override def funOut(oldOuterObj: CalcStep, outerFrame: Frame)(newInnerObj: CalcStep): Try[CalcStep] = Try {
	    (oldOuterObj, outerFrame) match {
    		case (oldOuter: CalcProofStep, _) =>
    			newInnerObj match {
    				case newInner: CalcProofStep =>
    					val aRule = rule {
    						case x: Term if x.displayId == subId => newInner.coreObj
    					}
    					val newFOpt = oncetd(aRule)(oldOuter.coreObj)

    					newFOpt match {
    						case Some(newF) =>
    							CalcProofStep(newF.asInstanceOf[Term], newInner.guard, newInner.derivedTerms,
    									newInner.freshVariables, newInner.assumedPreList, newInner.metaVars)
    						case None => throw new RuntimeException("Stepout Tactic Failed. Unable to replace the inner term.")
    					}
    				case _ =>
    				    throw new RuntimeException("Tactic not applicable")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}
/*
class TradingMoveToTermTactic1(displayId: Int, termToBeMovedId: Int) extends FunTactic {
	override val tName = "TradingMoveToTerm1"

	//def getHint(): Elem = <div>EmptyRange</div>
	def getHint: Elem =
		<div>
			<div>TradingMoveToTerm</div>
			<div>{PSTacticsHelper.docLink("TradingMoveToTermTactic1")}</div>
		</div>
	def getRelation() = None
	override def fun: (CalcStep, Frame) ==> CalcStep = {
		case (CalcProofStep(f: TermBool, grd, derivedTerms), _) => {
			val replaceRule = strategy {
				case cf: TermBool if (cf.displayId == displayId) => cf match {
					case ForallTermBool( dummies, AndNTermBool(fs), term) if fs.map(_.displayId).contains(termToBeMovedId) =>
						val termToBeMoved = fs.filter(_.displayId == termToBeMovedId).head
						val newFs = fs.filterNot(_ == termToBeMoved)
						Some(ForallTermBool( dummies, AndNTermBool(newFs), ImplTermBool(termToBeMoved, term)))
					case ExistsTermBool(dummies, AndNTermBool(fs), term) if fs.map(_.displayId).contains(termToBeMovedId) =>
						val termToBeMoved = fs.filter(_.displayId == termToBeMovedId).head
						val newFs = fs.filterNot(_ == termToBeMoved)
						Some(ExistsTermBool( dummies, AndNTermBool(newFs), AndTermBool(termToBeMoved, term)))
				}
				case _ => None
			}
			val res = oncetd(replaceRule)(f)
			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
			val modifiedFSimplified = modifiedF.simplify
			CalcProofStep(modifiedFSimplified, grd, derivedTerms)
		}
	}
}
*/

/**
 * AsgnDerivStep Tactic
 * Replaces a subformula with an equivalent formula
 */
case class ReplaceSubFormulaTactic(oldSubFId: Int, newSubF: TermBool) extends FunTactic {
	override val tName = "ReplaceSubFormula"

	def getHint(): Elem =
		<div class="SepChildsBySpace">
			<div>Replace subformula with id { oldSubFId } by formula &nbsp;{ XHTMLPrinters2.termToHtml(newSubF) }</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: TermBool, grd, derivedTerms, fv, aPres, metaVars), frm: FormulaFrame) => {

    			val globalInvs = TermBool.mkConjunct(frm.getSummary.progFrameSummary.globalInvs)

    			val assumptions = globalInvs && frm.getSummaryAxiomAndConjectures && grd && TermBool.mkConjunct(aPres)

    			val macros = frm.getSummary.getMacros()

    			//Extractor for extracting term with the given displayId
    			val SubTerm = getSubTermExtractor(oldSubFId)

    			// TODO: Context should not be used since some of the assumptions might not
    			// hold inside the oldSubF because of the dummy scoping.

    			import PSTacticsHelper.verify
    			var found = false
    			val replaceRule = strategy {
    				case SubTerm(cf: TermBool) =>
    					found = true
    					if (verify(assumptions, cf, EquivBoolFn, newSubF, macros))
    						Some(newSubF)
    					else
    						throw new Exception("Unable to discharge the proof obligation")
    				case _ => None
    			}

    			val resOpt = oncetd(replaceRule)(f)

    			val modifiedF: TermBool = resOpt match {
    				case Some(res: TermBool) => res
    				case _ if (! found) => throw new Exception(s"Subterm with id {$oldSubFId} not found")
    				case _ => throw new Exception(s"Unable to apply the tactic.")
    			}

    			cps.copy(coreObj = modifiedF)
    		}
    		case _ => throw new Exception(s"Current node is not a formula step.")
	    }
	}
}

/**
 * AsgnDerivStep Tactic
 * Replaces a subterm with an equivalent term
 */
case class ReplaceSubTermTactic(subTermId: Int, newSubTerm: Term) extends FunTactic {
	override val tName = "ReplaceSubTerm"

	def getHint(): Elem =
		<div class="SepChildsBySpace">
			<div>Replace subterm with id { subTermId } by term &nbsp;{ XHTMLPrinters2.termToHtml(newSubTerm) }</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = Some(EqEqBoolFn) //TODO: Can this be input to the tactic

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: Term, grd, derivedTerms, fv, aPres, metaVars), frm: FormulaFrame) => {

    			def isTheTerm(term: Term) =
    				(term.displayId == subTermId) &&
    				PSTacticsHelper.verifyRelation(term, EqEqBoolFn, newSubTerm)(cps, frm)

    			val replaceRule = strategy {
    				case subTerm: Term if isTheTerm(subTerm) => Some(newSubTerm)
    				// TODO: Context should not be used since some of the assumptions might not
    				// hold inside the oldSubF because of the dummy scoping. Ensure no dummy repetition.
    				case _ => None
    			}
    			val res = oncetd(replaceRule)(f)
    			val modifiedTerm: Term  = res match {
    				case Some(mt: Term) => mt
    				case _ => throw new RuntimeException("Failure in replacing the subterm")
    			}
    			cps.copy(coreObj = modifiedTerm)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class DistributivityTactic(displayId: java.lang.Integer) extends FunTactic {
	override val tName = "Distributivity"

	//def getHint(): Elem = <div>Distributivity</div>
	var hint = <div> Distributivity (term {displayId}). </div>
	def getHint: Elem =
		<div>
			{ hint }
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = Some(EquivBoolFn)
	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), _) => {
    			val replaceRule = strategy {
    				case cf: TermBool if (cf.displayId == displayId) => cf match {
    					case AndTermBool(f1, OrTermBool(f2, f3)) =>
    						hint = <div>{ hint }<div> { XHTMLPrinters2.mkAndDiv } <div> distributes over </div> { XHTMLPrinters2.mkOrDiv } </div></div>
    						Some(OrTermBool(AndTermBool(f1, f2), AndTermBool(f1, f3)))
    					case OrTermBool(f1, AndTermBool(f2, f3)) =>
    						hint = <div>{ hint }<div> { XHTMLPrinters2.mkOrDiv } <div> distributes over </div> { XHTMLPrinters2.mkAndDiv } </div></div>
    						Some(AndTermBool(OrTermBool(f1, f2), OrTermBool(f1, f3)))
    				}
    				case _ => None
    			}
    			val res = oncetd(replaceRule)(f)
    			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
    			CalcProofStep(modifiedF, grd, derivedTerms, fv, apres, metaVars)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class QDistributivityTactic(displayId: Int) extends FunTactic {

	override val tName = "QDistributivity"

	def getHint: Elem =
		<div>
			<div>{tName} (term {displayId})</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = None

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), _) => {

    			val replaceStrategy = strategy {

    				case SubTerm(cf @ ForallTermBool(dummies, range, OrNTermBool(ts))) if ts.length >= 2 =>

    					val (zs, others) = ts.partition(_.isFreeOf(dummies))

    					if (zs.length >= 1 && others.length >= 1) {
    						val zTerm = OrNTermBool(zs)
    						val otherTerm = ForallTermBool(dummies, range, OrNTermBool(others))
    						Some(OrTermBool(zTerm, otherTerm))
    					} else {
    						if (zs.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms have bound variables")
    						else if (others.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms are bound variable free")
    						None
    					}

    				case SubTerm(cf @ ExistsTermBool(dummies, range, AndNTermBool(ts))) if ts.length >= 2 =>

    					val (zs, others) = ts.partition(_.isFreeOf(dummies))

    					if (zs.length >= 1 && others.length >= 1) {
    						val zTerm = AndNTermBool(zs)
    						val otherTerm = ExistsTermBool(dummies, range, AndNTermBool(others))
    						Some(AndTermBool(zTerm, otherTerm))
    					} else {
    						if (zs.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms have bound variables")
    						else if (others.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms are bound variable free")
    						None
    					}
    				case SubTerm(cf @ MaxQTermInt(dummies, range, PlusNTermInt(ts))) if ts.length >= 2 =>

    					val (zs, others) = ts.partition(_.isFreeOf(dummies))

    					if (zs.length >= 1 && others.length >= 1) {
    						val zTerm = PlusNTermInt(zs)
    						val otherTerm = MaxQTermInt(dummies, range, PlusNTermInt(others))
    						Some(PlusTermInt(zTerm, otherTerm))
    					} else {
    						if (zs.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms have bound variables")
    						else if (others.isEmpty)
    							throw new Exception("QDistributivityTactic application failed. All terms are bound variable free")
    						None
    					}
    			}

    			val res = oncetd(replaceStrategy)(f)

    			res match {
    				case Some(modifiedF) => CalcProofStep(modifiedF.asInstanceOf[TermBool], grd, derivedTerms, fv, apres, metaVars)
    				case None => throw new RuntimeException("QDistributivityTactic application failed.")
    			}
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class TestFailureTactic() extends FunTactic {
	override val tName = "TestFailure"

	def getHint: Elem =
		<div>
			<div>TestFailureTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), _) =>
    			throw new RuntimeException("TestFailureTactic Application Failed")
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class RangeSplitTactic(displayId: Int) extends FunTactic {

	override val tName = "RangeSplit"

	def getHint: Elem =
		<div>
			<div>RangeSplit (term {displayId})</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	object QTermEx {
		def unapply(qTerm: QTerm): Option[(Fn, List[Var], TermBool, Term)] = qTerm match {
			case QTerm(opr, dummies, range, term) => Some((opr, dummies, range, term))
			case _ => throw new Exception("The subterm is not a quantified term")
		}
	}

	object OrTermBoolEx {
		def unapply(tb: TermBool): Option[(TermBool, TermBool)] = tb match {
			case OrTermBool(t1, t2) => Some(t1, t2)
			case _ => throw new Exception("Range of the subterm is not a disjunction.")
		}
	}

	def isIdempotent(opr: Fn): Try[Boolean] = opr match {
		case AndBoolFn | OrBoolFn | MaxIntFn | MinIntFn => TSuccess(true)
		case _ => TFailure(new Exception(s"operator {opr.name} is not idempotent"))
	}

	def rangeDisjoint(r1: TermBool, r2: TermBool)(cps: CalcProofStep, frm: FormulaFrame): Try[Boolean] = {
		val po: TermBool = !(r1 && r2)
		if (verify2(po)(cps, frm))
			TSuccess(true)
		else
			TFailure(new Exception(s"Ranges are not disjoint"))
	}

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(term: Term, grd, derivedTerms, fv, apres, metaVars), frm: FormulaFrame) => {

    			var found = false
    			val replaceRule = strategy {
    				case SubTerm(cf: Term) =>
    					found = true
    					cf match {
    						case QTermEx(opr, dummies, OrTermBoolEx(r1, r2), term) =>
    							val proviso = isIdempotent(opr).orElse(rangeDisjoint(r1, r2)(cps, frm))
    							proviso match {
    								case TSuccess(retVal) => retVal
    								case TFailure(e) => throw e
    							}
    							val t1 = QTerm.mkQTerm(opr, dummies, r1, term)
    							val t2 = QTerm.mkQTerm(opr, dummies, r2, term)
    							Some(FnApp(opr, List(t1, t2)))
    					}
    				case _ => None
    			}

    			val resOpt = oncetd(replaceRule)(term)

    			val modifiedTerm: Term = resOpt match {
    				case Some(res: Term) => res
    				case _ if (! found) => throw new Exception(s"Subterm with id {$displayId} not found")
    				case _ => throw new Exception(s"Unable to apply the Range split tactic.")
    			}

    			cps.copy(coreObj = modifiedTerm)
    		}
    		case _ => throw new Exception(s"Current node is not a proof step node.")
	    }
	}
}


case class SimplifyTactic() extends FunTactic {
	override val tName = "Simplify"

	def getHint(): Elem =
		<div>
			<div>Simplify</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = Some(EquivBoolFn)
	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), _) => {
    			CalcProofStep(f.simplify, grd, derivedTerms, fv, apres, metaVars)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class SimplifyAutoTactic() extends FunTactic {
	override val tName = "SimplifyAuto"

	val logger = LoggerFactory.getLogger("progsynth.SimplifyAutoTactic")

	private var ppsList: List[POProofStatus] = Nil

	def getHint(): Elem = {
		val logger = LoggerFactory.getLogger("progsynth.SimplifyAutoTactic")
		logger.trace("SimplifyAutoTactic.getHint called")

		val ppsDivs = for (POProofStatus(po, mps) <- ppsList) yield {
			<div class="pps">
				<div class="PO">{ XHTMLPrinters2.termToHtml(po) }</div>
				{ mps.toHtml }
			</div>
		}

		<div>
			<div>SimplifyAuto</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
			{
				XHTMLPrinters2.mkMinimizableWithTitle(<div>ProofInfo</div>)(
					<div class="PPSList">{ ppsDivs }</div>)(true)
			}
		</div>
	}

	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: TermBool, grd, derTerms, fVars, apres, metaVars), frm: FormulaFrame) => {
    			logger.trace("BeginSection(SimplifyAutoTactic.fun)")
    			val globalInvs = TermBool.mkConjunct(frm.getSummary.progFrameSummary.globalInvs)
    			val assumptions = (globalInvs && frm.getSummaryAxiomAndConjectures && grd && TermBool.mkConjunct(apres)).simplify()
    			val frmRelation = frm.getSummary.formulaFrameSummary.relation
    			val allVars = frm.getSummary.getAllVariables
    			val macros = frm.getSummary.getMacros
    			val saObj = new SimplifyAuto()
    			val newf = saObj.simplifyAuto(assumptions, frmRelation, f)(allVars, macros)
    			ppsList = saObj.getPPSList()
    			val rv = cps.copy(coreObj = newf)
    			logger.trace("EndSection(SimplifyAutoTactic.fun)")
    			rv
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	private def binFnApp(f1: Term, relation: Fn, f2: Term): FnApp = {
		FnApp(relation, List(f1, f2))
	}
}

//
//class OnePointTactic2(displayId: Int) extends FunTactic {

//	def getHint: Elem = <div>OnePoint</div>
//	def getRelation() = Some(EquivBoolFn)
//	override def fun: (CalcStep, Frame) ==> CalcStep = {
//		case (CalcProofStep(f: TermBool), _) => {
//			val replaceRule = strategy {
//				case cf: TermBool if (cf.displayId == displayId) => cf match {
//					case QTerm(aFn, d :: Nil, EqEqTermBool(v: Var, z: Term), term) if (d == v)  && !(z.getFreeVars contains d) =>
//						term.replaceVar(v, z) //TODO: term(v replaced by z) should be defined.
//				}
//			}
//			val res = oncetd(replaceRule)(f)
//			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
//			CalcProofStep(modifiedF)
//		}
//	}
//}


case class TradingMoveToTermTactic(displayId: Int, termToBeMovedId: Int) extends FunTactic {
	override val tName = "TradingMoveToTerm"

	//def getHint(): Elem = <div>EmptyRange</div>
	def getHint: Elem =
		<div>
			<div>{tName} (term {displayId}, termToBeMoved {termToBeMovedId})</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), _) => {
    			val replaceRule = strategy {
    				case cf: TermBool if (cf.displayId == displayId) => cf match {
    					case ForallTermBool(dummies, AndNTermBool(fs), term) if fs.map(_.displayId).contains(termToBeMovedId) =>
    						val termToBeMoved = fs.filter(_.displayId == termToBeMovedId).head
    						val newFs = fs.filterNot(_ == termToBeMoved)
    						Some(ForallTermBool(dummies, AndNTermBool(newFs), ImplTermBool(termToBeMoved, term)))
    					case ExistsTermBool(dummies, AndNTermBool(fs), term) if fs.map(_.displayId).contains(termToBeMovedId) =>
    						val termToBeMoved = fs.filter(_.displayId == termToBeMovedId).head
    						val newFs = fs.filterNot(_ == termToBeMoved)
    						Some(ExistsTermBool(dummies, AndNTermBool(newFs), AndTermBool(termToBeMoved, term)))
    				}
    				case _ => None
    			}
    			val res = oncetd(replaceRule)(f)
    			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
    			val modifiedFSimplified = modifiedF.simplify
    			CalcProofStep(modifiedFSimplified, grd, derivedTerms, fv, apres, metaVars)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}


case class MergeBoundVariablesTactic(displayId: Int) extends FunTactic {
	override val tName = "MergeBoundVariables"

	def getHint: Elem =
		<div>
			<div>{tName}</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EqEqBoolFn)
	//TODO: depending on the type of termObj relation should change to EqEq or Equiv

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(t: Term, _, _, _, _, _), _) => {
    			val replaceRule = strategy {
    				case SubTerm(QTerm(opr1, dummies1, range1,
    									QTerm(opr2, dummies2, range2, term))) if opr1 == opr2 =>
    					Some(QTerm.mkQTerm(opr1, dummies1 ++ dummies2, range1 && range2, term))
    				case _ => None
    			}
    			val resOpt = oncetd(replaceRule)(t)
    			val modifiedF: TermBool = resOpt match {
    				case Some(res) => res.asInstanceOf[TermBool]
    				case None => throw new Exception("Unable to merge bound variables")
    			}
    			val modifiedFSimplified = modifiedF.simplify
    			cps.copy(coreObj = modifiedFSimplified)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**TODO: document*/
case class UseAssumptionsTactic(subFormulaId: Int, newSubF: TermBool) extends FunTactic {
	override val tName = "UseAssumptions"

	def getHint: Elem =
		<div>
			<div>UseAssumptions</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = Some(EquivBoolFn)

	def getGlobalInv(frm: FormulaFrame): TermBool = {
		frm.getSummary.progFrameSummary.getConjunctionOfGlobalInvs
	}
	def getPO(cf: TermBool, grd: TermBool, assumedPre: List[TermBool], frm: FormulaFrame): TermBool = {
		//assumptions /\ globalInv => cf \equiv newSubF
		val po: TermBool = {
			val assumptions: TermBool = {
				frm.getSummaryAxiomAndConjectures &&
					grd &&
					TermBool.mkConjunct(assumedPre)
			}
			//TODO: should not use assumptions because of dummy conflict
			val globalInv: TermBool = getGlobalInv(frm)
			AndTermBool(assumptions, globalInv).impl(EqEqEqTermBool(cf, newSubF))
		}
		po
	}
	/*
	def isValid(po: TermBool, macros: List[Macro]): Boolean = {
		val z3ResOpt = Z3Prover.expandMacroAndProve(po, macros)
		z3ResOpt match {
			case Some(z3Res) if z3Res.isValid =>
				PSDbg.writeln1("valid")
				true
			case Some(z3Res) if !z3Res.isValid =>
				PSDbg.writeln1("not valid")
				false
			case None =>
				PSDbg.writeln1("Can not be be verified by Z3")
				false
		}
	}
	*/
	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), frm: FormulaFrame) => {
    			val macros = frm.getSummary.getMacros()
    			val replaceStrategy = strategy {
    				case cf: TermBool if (cf.displayId == subFormulaId) &&
    					//isValid(getPO(cf, grd, apres, frm), macros) =>
    					PSTacticsHelper.verify(getPO(cf, grd, apres, frm), macros) =>
    					Some(newSubF)
    				case _ =>
    					None
    			}
    			val res = oncetd(replaceStrategy)(f)
    			res match {
    				case Some(modifiedF) => CalcProofStep(modifiedF.asInstanceOf[TermBool].simplify, grd,
    					derivedTerms, fv, apres, metaVars)
    				case None => throw new RuntimeException("UseAssumptions application failed.")
    			}
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

trait VerifiedTransformationBase extends FunTactic {
	def newFormula: TermBool
	def relation: Fn

	def getRelation() = Some(relation)

	/** Verify that (assumptions => (f Fn newFormula)) */
	def verifyTransformation(f: TermBool, grd: TermBool, assumedPre: List[TermBool], frm: FormulaFrame): Boolean = {
		val globalInvs = TermBool.mkConjunct(frm.getSummary.progFrameSummary.globalInvs)
		val frameAssumptions = frm.getSummaryAxiomAndConjectures
		val accumulatedAssumptions = grd && TermBool.mkConjunct(assumedPre)
		val macros = frm.getSummary.getMacros()
		PSTacticsHelper.verify(globalInvs && frameAssumptions && accumulatedAssumptions, f, relation, newFormula, macros)
	}

	def isValidFn(aFn: Fn) = aFn match {
		case AndBoolFn | OrBoolFn | NegBoolFn | ImplBoolFn | RImplBoolFn | EquivBoolFn => true
		case _ => false
	}

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: TermBool, grd, _, _, apres, metaVars), frm: FormulaFrame) => {
    			if (! isValidFn(relation))
    				throw new Exception(s"Relation {relation.name} is not supported.")
    			if (!verifyTransformation(f, grd, apres, frm))
    				throw new Exception(s"Failed to verify the transformation.")
    			cps.copy(coreObj = newFormula)
    		}
    		case _ =>
    			throw new Exception("current node is not a Formula Node.")
    	}
	}


}
/**
 * Under the current assumptions, the current formula *Fn* the *newFormula*
 */
case class VerifiedTransformationTactic(newFormula: TermBool, relation: Fn)  extends VerifiedTransformationBase {

	override val tName = "VerifiedTransformation"

	def getHint: Elem =
		<div>
			<div>{tName}</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

}

/*
//TODO: remove if redundant
case class ReplaceFormulaTactic2(newf: TermBool) extends FunTactic {
	override val tName = "ReplaceFormula2"

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    	    case (f: TermBool, _) => newf
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	def getHint(): Elem =
		<div>
			<div>ReplaceFormula</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None
}
*/

case class ReplaceFormulaTactic(newFormula: TermBool) extends VerifiedTransformationBase {
	val relation = EquivBoolFn
	override val tName = "ReplaceFormula"
	override def getHint: Elem =
		<div class="SepChildsBySpace">
			<div>Replace formula</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
}

/**
 * * Expands the macros application in the specified subformula.
 *  * The subformula is recursively expanded.
 *  * If there are not macro applications in the subformula, then the same formula is returned.
 */
case class ExpandMacroTactic(displayId: Int) extends FunTactic {

    override val tName = "ExpandMacro"

	def getHint: Elem =
		<div>
			<div>ExpandMacroTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), frm) => {
    			val macros = frm.getSummary.getMacros()
    			val replaceRule = strategy {
    				case cf: TermBool if (cf.displayId == displayId) =>
    					val expandedF = new MacroExpander(macros).expand(cf).asInstanceOf[TermBool]
    					Some(expandedF)
    				case _ => None
    			}
    			val res = oncetd(replaceRule)(f)
    			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
    			CalcProofStep(modifiedF, grd, derivedTerms, fv, apres, metaVars)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * * Expands the macros applications in formula under consideration.
 *  * The formula is recursively expanded.
 *  * If there are no macro applications in the formula, then the same formula is returned.
 */
case class ExpandAllMacrosTactic() extends FunTactic {

    override val tName = "ExpandAllMacros"

	def getHint: Elem =
		<div>
			<div>ExpandAllMacrosTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

    def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), frm) =>
    			val macros = frm.getSummary.getMacros
    			val expandedF = new MacroExpander(macros).expand(f).asInstanceOf[TermBool]
    			CalcProofStep(expandedF, grd, derivedTerms, fv, apres, metaVars)
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}
/*
class ExpandGlobalMacrosTactic(globalMacros: List[Macro]) extends FunTactic {
	override val tName = "ExpandGlobalMacros"
	def getHint: Elem =
		<div>
			<div>ExpandGlobalMacroTactic</div>
			<div>{PSTacticsHelper.docLink(tName)}</div>
		</div>
	def getRelation() = Some(EquivBoolFn)
	override def fun: (CalcStep, Frame) ==> CalcStep = {
		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres), frm) => {
			val expandedF = new MacroExpander(globalMacros).expand(f).asInstanceOf[TermBool]
			CalcProofStep(expandedF, grd, derivedTerms, fv, apres)
		}
	}
}

//TODO: Implement this
class UseGlobalMacroTactic(displayId: Int, globalMacros: List[Macro]) extends FunTactic {
	override val tName = "UseGlobalMacro"
	def getHint: Elem =
		<div>
			<div>UseGlobalMacroTactic</div>
			<div>{PSTacticsHelper.docLink(tName)}</div>
		</div>
	def getRelation() = Some(EquivBoolFn)
	override def fun: (CalcStep, Frame) ==> CalcStep = {
		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres), frm) => {
			val replaceRule = strategy {
				case cf: TermBool if (cf.displayId == displayId) =>
					val expandedF = new MacroExpander(globalMacros).expand(cf).asInstanceOf[TermBool]
					Some(expandedF)
				case _ => None
			}
			val res = oncetd(replaceRule)(f)
			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
			CalcProofStep(modifiedF, grd, derivedTerms, fv, apres)
		}
	}
}

class ExpandGlobalMacroTactic(displayId: Int, globalMacros: List[Macro]) extends FunTactic {
	def getHint: Elem =
		<div>
			<div>ExpandGlobalMacroTactic</div>
			<div>{PSTacticsHelper.docLink(tName)}</div>
		</div>
	def getRelation() = Some(EquivBoolFn)
	override def fun: (CalcStep, Frame) ==> CalcStep = {
		case (CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres), frm) => {
			val replaceRule = strategy {
				case cf: TermBool if (cf.displayId == displayId) =>
					val expandedF = new MacroExpander(globalMacros).expand(cf).asInstanceOf[TermBool]
					Some(expandedF)
				case _ => None
			}
			val res = oncetd(replaceRule)(f)
			val modifiedF: TermBool = res.get.asInstanceOf[TermBool]
			CalcProofStep(modifiedF, grd, derivedTerms, fv, apres)
		}
	}
}
*/

/**Formula tactic. */
case class Magic2Tactic(vars: List[Var], newF: TermBool) extends FunTactic {
	override val tName = "Magic2"
	override def toString() = "Magic2Tactic"
	def getHint: Elem =
		<div>
			<div>MagicTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: TermBool, grd, derivedTerms, fv, apres, metaVars), frm: FormulaFrame) =>
    			CalcProofStep(newF, grd, derivedTerms, fv, apres, metaVars)
    		case _ =>
    			CalcProofStep(newF, TermBool.TrueT, Nil, Nil, Nil, Nil)
	    }
	}
}

case class MagicTactic(vars: List[Var], newF: TermBool) extends FunTactic {
	override val tName = "Magic"
	override def toString() = "MagicTactic"
	def getHint: Elem =
		<div>
			<div>MagicTactic</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	def getRelation() = None

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (_, _) =>
    			CalcProgStep(mkUnknownProg(TermBool.TrueT.inv(), 1, newF.inv()), None)
	    }
	}
}


/**
 * Assume precondition that might involve fresh variables.
 * An unknownprog is created on stepout to establish the precondition.
 */
case class AssumePreTactic(freshVariables: List[Var], assumedPre: TermBool) extends FunTactic {
	override val tName = "AssumePre"

	override def toString() = "AssumePre"

	def getHint: Elem =
		<div>
			<div>
				<div>AssumePreTactic: Introduce fresh variables: { freshVariables.map(avar => avar.v + ": " + avar.getType).mkString(", ") }.</div>
				<div>Assume precondition:&nbsp;</div><div>{ XHTMLPrinters2.termToHtml(assumedPre) }</div>
			</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f, grd, derivedTerms, cFreshVars, cAssumedPreList, metaVars), frm: FormulaFrame) =>
    			CalcProofStep(f, grd, derivedTerms, cFreshVars ++ freshVariables, cAssumedPreList ::: List(assumedPre), metaVars)
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

/**
 * Assume precondition "fresh variables" equiv "subformula". An unknownprog should be created on stepout
 * to establish the precondition.
 */
case class AssumePreEquivTactic(freshVariable: VarBool, subFormulaId: Int) extends FunTactic {
	override val tName = "AssumePreEquiv"
	override def toString() = "AssumePreEquiv"
	//def getHint(): Elem = <div>EmptyRange</div>
	def getHint: Elem =
		<div>
			<div>
				<div>AssumePreEquivTactic: Introduce fresh variable: { freshVariable.v + ": " + freshVariable.getType }.</div>
				<div>Assume precondition:&nbsp;</div><div>{ freshVariable.v } equiv formula({ subFormulaId })</div>
			</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {

    		case (cps @ CalcProofStep(f, grd, derivedTerms, cFreshVars, cAssumedPreList, metaVars), frm: FormulaFrame) => {
    			var subFormula: TermBool = null
    			val queryRule = rule {
    				case cf: TermBool if (cf.displayId == subFormulaId) =>
    					subFormula = cf;
    					cf
    			}

    			oncetd(queryRule)(f) //Run for side-effect
    			if (subFormula == null) {
    				throw new RuntimeException("AssumePreEquivTactic application failed. " +
    					"subformula with given id not found")
    			}

    			val assumedPre = freshVariable equiv subFormula
    			CalcProofStep(f, grd, derivedTerms, cFreshVars ++ List(freshVariable), cAssumedPreList ::: List(assumedPre), metaVars)
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class SplitoutBoundVariableTactic(displayId: Int, boundVar: Var) extends FunTactic {
	override val tName = "SplitoutBoundVariable" //TODO: make the tactic name consistent with the class name

	override def toString() = tName

	def getHint: Elem =
		<div>
			<div>{tName}: Splitout variable {boundVar.v} from term with id {displayId} </div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EqEqBoolFn)
	//TODO: depending on the type of termObj relation should change to EqEq or Equiv

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	//Split range into two parts. First part contains conjucts that are free of bound variable.
	def splitRange(range: TermBool, dummies: List[Var]): Try[(TermBool, TermBool)] = range match {
		case AndNTermBool(rangeCs) =>
			val vars2 = dummies.filter(_ != boundVar)
			if(!vars2.isEmpty){
				val (range1Cs, range2Cs) = rangeCs.partition(_.isFreeOf(vars2))
				val range1 = TermBool.mkConjunct(range1Cs)
				val range2 = TermBool.mkConjunct(range2Cs)
				TSuccess((range1, range2))
			} else
				TFailure(new Exception("The quantifier has a single bound variable!"))
		case _ =>
			TFailure(new Exception("Unable to split range into conjuncts"))
	}

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(t: Term, _, _, _, _, _), _) => {
    			tacticFun(t) match {
    				case TSuccess(newTerm) =>
    					cps.copy(coreObj = newTerm)
    				case TFailure(e) =>
    					throw e
    			}
    		}
    		case _ => throw new Exception("Current node not a calproof step with term node.")
	    }
	}

	def tacticFun(t: Term): Try[Term] = Try{
		var found = false

		val replaceRule = strategy {
			case SubTerm(qt) =>
				found = true
				qt match {
					case QTerm(opr, dummies, range, term) =>
						qtTransformFun(opr, dummies, range, term)
					case _ =>
						throw new Exception("Subterm is not a quantified term.")
				}
			case _ => None
		}
		val resOpt = oncetd(replaceRule)(t)

		val modifiedF: Term = resOpt match {
			case Some(res: TermBool) => res.simplify
			case Some(res: Term) => res
			case _ if !found => throw new Exception(s"subTerm with displayid $displayId not found")
			case _ => throw new Exception("Unable to splitout the bound variable")
		}
		modifiedF
	}

	def qtTransformFun(opr: Fn, dummies: List[Var], range: TermBool, term: Term): Some[QTerm] = {
		(splitRange(range, dummies) map { case (range1, range2) =>
			val restVars = dummies.filter(_ != boundVar)
			Some(QTerm.mkQTerm(opr, List(boundVar), range1,
					QTerm.mkQTerm(opr, restVars, range2, term)))
		}).get
	}

}


case class OnePointTactic(displayId: Int) extends FunTactic {
	override val tName = "OnePoint"

	override def toString() = tName

	def getHint(): Elem =
		<div>
			<div> OnePoint (term {tName})</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn)

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(f: Term, grd, derivedTerms, fv, apres, metaVars), _) => {

    			var found = false

    			val replaceRule = strategy {
    				case SubTerm(cf: Term) => {
    					found = true
    					cf match {
    						case QTerm(aFn, dummies, range, term) => {
    							range match {
    								case EqEqTermBool(v: Var, z: Term) =>
    									dummies match {
    										case d :: Nil =>
    											if (d != v)
    												throw new Exception("Variable and bound variable are different")
    											if (!z.isFreeOf(d))
    												throw new Exception("The term ${z} contains free occurences of the bound variable.")
    										case _ =>
    											throw new Exception("There should be single bound variable.")
    									}

    									//getHint = <div> {XHTMLPrinters2.mkAndDiv} <div> distributes over </div> {XHTMLPrinters2.mkOrDiv} </div>
    									Some(term.replaceVar(v, z)) //TODO: term(v replaced by z) should be defined.
    								case _ => throw new Exception("Range formula not in the required form.")
    							}
    						}
    					}
    				}
    				case _ => None
    			}

    			val resOpt = oncetd(replaceRule)(f)

    			val newTerm = resOpt match {
    				case Some(res) => res
    				case _ if !found =>
    					throw new Exception(s"subTerm with displayid $displayId not found")
    				case None =>
    					throw new Exception("Unable to apply onepoint tactic.")
    			}
    			cps.copy(coreObj = newTerm.asInstanceOf[Term])
    		}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}
}

case class EmptyRangeTactic(displayId: Int) extends FunTactic {
	import PSTacticsHelper.verify

	override val tName = "EmptyRange"

	override def toString() = tName

	def getHint: Elem =
		<div>
			<div>{tName} (term {displayId})</div>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>

	def getRelation() = Some(EquivBoolFn) //TODO: Fix relation

	//Extractor for extracting term with the given displayId
	val SubTerm = getSubTermExtractor(displayId)

	override def fun(nodeObj: CalcStep, frame: Frame): Try[CalcStep] = Try {
	    (nodeObj, frame) match {
    		case (cps @ CalcProofStep(term: Term, grd, derivedTerms, fv, apres, metaVars), frm) => {

    			val macros = frm.getSummary.getMacros()

    			var found = false
    			val replaceRule = strategy {
    				case SubTerm(cf: Term) =>
    					found = true
    					cf match {
    						//TODO: what if Z3 prover call fails
    						//TODO: provide direct API for sat and unsat
    						case QTerm(aFn, _, range, _) =>
    							if (verify(NotTermBool(range), macros)) //Are assumptions used.
    								getUnit(aFn)
    							else
    								throw new Exception( "Unable to prove empty range.")
    						case _ =>
    							throw new Exception(s"The term with id $displayId is not a quantified term.")
    					}
    				case _ => None
    			}

    			val resOpt = oncetd(replaceRule)(term)

    			val newTerm = resOpt match {
    				case Some(res: TermBool) => res.simplify
    				case Some(res: Term) => res
    				case _ if !found => throw new Exception( s"Subterm with id $displayId not found.")
    				case _ => throw new Exception( s"Failed to apply EmptyRange tactic.")
    			}
    			cps.copy(coreObj = newTerm)
    		}
    		case _ => throw new Exception("Current node does not correspond to a proof step.")
	    }
	}
}


/*
/** CalcProofStep Tactic
 *  Replaces the primed variables with the given terms
 *  Same purpose as InstantiateMetaTactic2. But want to keep InstantiateMetaTactic for backward compatibility.
 *  InstantiateMetaTactic assumes that the derivedVariables list in CalcProof is already populated with the values of meta-variables.
 */
class InstantiateMetaTactic2(primedVarTermList: List[(Var, Term)]) extends FunTactic {
	override val tName = "InstantiateMeta2"
	val primedVarList = primedVarTermList map {case (v, _) => v}
	val termList = primedVarTermList map {case (_, t) => t}
	val substMap: Map[Var, Term] = (primedVarList zip termList).toMap

	override def toString() = "InstantiateMeta2"
	def getHint(): Elem =
		<div>
			<div>Guessing expression values: </div>
			<div class="spacediv"></div>
			{ primedVarList.map(termToHtml(_))}
			<div class="spacediv"></div>
			<div> = </div>
			<div class="spacediv"></div>
			{ termList.map(termToHtml(_)) }
			<div>{PSTacticsHelper.docLink(tName)}</div>
		</div>
	def getRelation() = Some(EquivBoolFn)

	override def fun: (CalcStep, Frame) ==> CalcStep = {
		case (CalcProofStep(f, grd, derivedTerms), _) =>
			val replace = rule {
				case aVar: Var if substMap contains aVar=>
					PSDbg.writeln0(<a>{aVar} is substituted by {substMap.get(aVar).get}</a>.text)
					substMap.get(aVar).get
				case a => a
			}
			val res = bottomup(replace)(f)
			val newTB: TermBool = res.get.asInstanceOf[TermBool]
			val newDerivedTerms = derivedTerms.map{ case (aVar, termO) =>
				if(substMap contains aVar) {
					(aVar, substMap.get(aVar))
				}
				else
					(aVar, termO)
			}
			CalcProofStep(newTB, grd, newDerivedTerms)
	}
}
*/
