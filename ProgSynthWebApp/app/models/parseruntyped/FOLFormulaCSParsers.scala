package models.parseruntyped

import progsynth.utils.PSUtils
import progsynth.synthesisold.ProgContext
import progsynth.types._
import progsynth.types.Types._
import Types.FOLFormulaT

trait FOLFormulaCSParsers extends FOLFormulaTParsers with FOLFormulaValidator with PSUtils with PSWebParserUtils {
	def folFormulaCSP(implicit progCtx: ProgContext):Parser[FOLFormula] =
		strengthen(folFormulaTP, getFOLFormula, "Can not parse term. Failure in type checking: ")
}

trait FOLFormulaValidator extends PredValidator {
	def getFOLFormula(result: FOLFormulaT)(implicit progCtx: ProgContext): Option[FOLFormula] = result match {
		case True1T() => Some(True1[Pred]())
		case False1T() => Some(False1[Pred]())
		case AtomT(predT) => getPred(predT) map (Atom(_))
		case NotT(ft) => getFOLFormula(ft) map (Not(_))
		case AndT(ft1, ft2) =>
			allmap(List(ft1, ft2), getFOLFormula)
				.map{case List(f1, f2) => And(f1, f2)}
		case OrT(ft1, ft2) =>
			allmap(List(ft1, ft2), getFOLFormula)
				.map{case List(f1, f2) => Or(f1, f2)}
		case ImplT(ft1, ft2) =>
			allmap(List(ft1, ft2), getFOLFormula)
				.map{case List(f1, f2) => Impl(f1, f2)}
		case IffT(ft1, ft2) =>
			allmap(List(ft1, ft2), getFOLFormula)
				.map{case List(f1, f2) => Iff(f1, f2)}
		case ForallT(vt, ft) =>
			val vOpt = progCtx.getVar(vt.v, PSInt)(false)
			if(vOpt.isDefined) {/*show warning*/}
			/** Only Integer dummies supported.
			 *  A new dummy variable is created even when a variable of same name exists in ctx.vals or ctx.vars
			 *  */
			val newV = VarInt(vt.v)
			val bodyFOpt: Option[FOLFormula] = getFOLFormula(ft)(progCtx.addDummies(List(newV)))
			bodyFOpt.map(bodyF => Forall(newV, bodyF))
		case ExistsT(vt, ft) =>
			val vOpt = progCtx.getVar(vt.v, PSInt)(false)
			if(vOpt.isDefined) {/*show warning*/}
			/** Only Integer dummies supported.
			 *  A new dummy variable is created even when a variable of same name exists in ctx.vals or ctx.vars
			 *  */
			val newV = VarInt(vt.v)
			val bodyFOpt: Option[FOLFormula] = getFOLFormula(ft)(progCtx.addDummies(List(newV)))
			bodyFOpt.map(bodyF => Exists(newV, bodyF))

		case UnknownT() => Some(Unknown())
	}
}