package progsynth.utils

import org.slf4j.LoggerFactory
import progsynth.types.TermBool
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
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.provers._
import PartialFunction._
import XHTMLPrinters2.termToHtml
import progsynth.synthesisnew.Macro

case class POProofStatus(po: TermBool, mps: MultiProverStatus)

class SimplifyAuto() {
	val logger = LoggerFactory.getLogger("progsynth.SimplifyAuto")

	private var ppsList: List[POProofStatus] = Nil //TODO: eliminate var

	def getPPSList(): List[POProofStatus]  = ppsList
	
	/**
	 * Steps into subformulas in bottom up fashion.
	 *  Simplifies the formula (with the help of Z3) to true/false if possible.
	 *  variables param: list of variables in context (mutable variables, immutable variables, and
	 *  dummies)
	 */
	//TODO: Can the variables param be removed.
	def simplifyAuto(axms: TermBool, rel: Fn, f: TermBool, filter: TermBool => Boolean = _ => true)
	(implicit variables: List[Var], macros: List[Macro]): TermBool =
	{
		logger.trace(beginSection("SimplifyAuto"))
		logger.trace("axms: ")
		logger.trace(multiline(XHTMLPrinters2.termToHtml(axms).toString))
		logger.trace("rel: ")
		logger.trace(rel.toString)
		logger.trace("f: ")
		logger.trace(multiline(XHTMLPrinters2.termToHtml(f).toString))
		logger.trace("variables: ")
		logger.trace(inlineList(variables flatMap { aVar => XHTMLPrinters2.termToHtml(aVar) }))
		logger.trace("macros: ")
		logger.trace(macros.toString)
		val retVal = f match {
			case NotTermBool(fin) =>
				val finNew = simplifyAuto(axms, inv(rel), fin)
				val fnew = NotTermBool(finNew).simplify
				semSimplify(axms, rel, fnew, filter)
			case AndTermBool(f1, f2) =>
				val f1new = simplifyAuto(axms &&& f2, rel, f1)
				val f2new = simplifyAuto(axms &&& f1new, rel, f2)
				val fnew = AndTermBool(f1new, f2new).simplify
				semSimplify(axms, rel, fnew, filter)
			case OrTermBool(f1, f2) =>
				val f1new = simplifyAuto(axms &&& !f2, rel, f1)
				val f2new = simplifyAuto(axms &&& !f1new, rel, f2)
				val fnew = OrTermBool(f1new, f2new).simplify
				semSimplify(axms, rel, fnew, filter)
			case ImplTermBool(f1, f2) =>
				//step into f2
				val f2new = simplifyAuto(axms &&& f1, rel, f2)
				//step into f1
				val f1new = simplifyAuto(axms &&& !f2new, inv(rel), f1)
				val fnew = ImplTermBool(f1new, f2new).simplify
				semSimplify(axms, rel, fnew, filter)
			case RImplTermBool(f1, f2) =>
				//step into f1
				val f1new = simplifyAuto(axms &&& f2, rel, f1)
				//step into f2
				val f2new = simplifyAuto(axms &&& !f1new, inv(rel), f2)
				val fnew = RImplTermBool(f1new, f2new).simplify
				semSimplify(axms, rel, fnew, filter)
			case EqEqEqTermBool(f1, f2) =>
				val f1new = simplifyAuto(axms, EquivBoolFn, f1) //step into f1
				val f2new = simplifyAuto(axms, EquivBoolFn, f2) //step into f2
				val fnew = EqEqEqTermBool(f1new, f2new).simplify
				semSimplify(axms, rel, fnew, filter)
			case ForallTermBool(dummies, f1, f2) =>
				val commonVars = variables.intersect(dummies)
				if (commonVars.isEmpty) {
					val variablesUpd = variables ++ dummies
					//step into f2
					val f2new = simplifyAuto(axms &&& f1, rel, f2)(variablesUpd, macros)
					//step into f1
					val f1new =
						simplifyAuto(axms &&& !f2new, inv(rel), f1)
					(variablesUpd, macros)
					val fnew = ForallTermBool(dummies, f1new, f2new).simplify
					semSimplify(axms, rel, fnew, filter)
				} else {
					//TODO: log message. variable clash. commonVars.
					f
				}
			case ExistsTermBool(dummies, f1, f2) =>
				val commonVars = variables.intersect(dummies)
				if (commonVars.isEmpty) {
					val variablesUpd = variables ++ dummies
					//step into f2
					val f2new = simplifyAuto(axms &&& f1, rel, f2)(variablesUpd, macros)
					//step into f1
					val f1new = simplifyAuto(axms &&& f2new, rel, f1)(variablesUpd, macros)
					val fnew = ExistsTermBool(dummies, f1new, f2new).simplify
					semSimplify(axms, rel, fnew, filter)
				} else {
					//TODO: log message. variable clash. commonVars.
					f
				}
			case atom => semSimplify(axms, rel, atom, filter)
		}
		logger.trace(endSection("SimplifyAuto"))
		retVal
	}

	/**
	 * Reduce the formula to true or false if possible
	 *  Also update the POProofStatus field.
	 */
	private def semSimplify(axms: TermBool, relation: Fn, formula: TermBool, filter: TermBool => Boolean )(implicit macros: List[Macro] //TODO: Handle macros
	): TermBool =
		{
			logger.trace("semSimplify called")
			import TermBool._

			def getPO(newFormula: TermBool) = {
				//axms.impl(FnApp(relation, List(formula, newFormula)))
				axms.impl(FnApp(EquivBoolFn, List(formula, newFormula))) //Use EquivBoolFn instead of relation.
			}

			def reduceTo(newFormula: TermBool): Option[TermBool] = {
				logger.trace("reduceToCalled")
				val po = getPO(newFormula)
				val pm = new PSProverMgr()
				val mps = pm.expandMacrosAndProve2(po, macros)
				mps.finalStatus match {
					case PSProofValid() =>
						logger.trace("Added po to ppsList")
						ppsList ::= POProofStatus(po, mps)
						logger.trace("ppsList length" + ppsList.length)
						Some(newFormula)
					case _ =>
						logger.trace("Added po to ppsList")
						ppsList ::= POProofStatus(po, mps) //TODO: do not added failing POs.
						logger.trace("ppsList length" + ppsList.length)
						None
				}
			}
			if(filter(formula))
				reduceTo(TrueT).orElse { reduceTo(FalseT) }.getOrElse { formula }
			else
				formula
		}


	private def inv(aFn: Fn): Fn = aFn match { //TODO: DRY
		case EquivBoolFn => EquivBoolFn
		case ImplBoolFn => RImplBoolFn
		case RImplBoolFn => ImplBoolFn
		case _ => throw new RuntimeException("unhandled relation in invertRelation")
	}

}
