package models.pprint

import progsynth.methodspecs.InterpretedFns._
import progsynth.types._
import progsynth.types.Types._
//import models.TermPPrint.pprint

trait FOLFormulaPPrint extends TermPPrint with PredPPrint{
	def pprintFOLFormula0(formula: FOLFormula): String =
		pprintFOLFormula(formula, minBP)

	def pprintFOLFormula(formula: FOLFormula, parentBP: Int): String = {
		val ppf = pprintFOLFormula _
		val selfBp = getBP(formula)
		val retVal = formula match {
			case Atom(pred) => pprintPred(pred, selfBp)
			case And(f1, f2) =>
				<a>{ppf(f1, selfBp)} /\ {ppf(f2, selfBp)}</a>.text
			case Or(f1, f2) =>
				<a>{ppf(f1, selfBp)} \/ {ppf(f2, selfBp)}</a>.text
			case Impl(f1, f2) =>
				<a>{ppf(f1, selfBp)} \impl {ppf(f2, selfBp)}</a>.text
			case Iff(f1, f2) =>
				<a>{ppf(f1, selfBp)} \equiv {ppf(f2, selfBp)}</a>.text
			case QuantN(qs, vs, f) =>
				val quantStr = (qs zip vs) map {case (quant, aVar) => quant + " " + aVar.v}
				<a>{quantStr.mkString(" ")}: {ppf(f, selfBp)}</a>.text
			case TrueF => "true"
			case FalseF => "false"
		}
		parenIf(retVal, parentBP > selfBp)
	}
}

trait PredPPrint extends IntPredPPrint with BoolPredPPrint { self: FOLFormulaPPrint =>
	def pprintPred(pred: Pred, parentBP: Int): String = {
		def intPred = List("$eq$eq", "$less$eq", "$greater$eq", "$less", "$greater") contains pred.r
		def boolPred = pred.r == "BoolPred"

		if(intPred)
			pprintIntPred(pred, parentBP)
		else if (boolPred)
			pprintBoolPred(pred, parentBP)
		else
			throw new RuntimeException("pprintPred: " + pred.toString)
	}
	def pprintPred0(pred: Pred) = pprintPred(pred, minBP)
}

trait IntPredPPrint { self: FOLFormulaPPrint =>
	def pprintIntPred(pred: Pred, parentBP: Int): String = {
		val selfBp = getBP(pred)
		val retVal = pred match {
			case Pred("$eq$eq", t1 :: t2 :: Nil) =>
				pprintTerm(t1, selfBp) + " == "  + pprintTerm(t2, selfBp)
			case Pred("$less$eq", t1 :: t2 :: Nil) =>
				pprintTerm(t1, selfBp) + " <= "  + pprintTerm(t2, selfBp)
			case Pred("$greater$eq", t1 :: t2 :: Nil) =>
				pprintTerm(t1, selfBp) + " >= "  + pprintTerm(t2, selfBp)
			case Pred("$less", t1 :: t2 :: Nil) =>
				pprintTerm(t1, selfBp) + " < "  + pprintTerm(t2, selfBp)
			case Pred("$greater", t1 :: t2 :: Nil) =>
				pprintTerm(t1, selfBp) + " > "  + pprintTerm(t2, selfBp)
		}
		parenIf(retVal, parentBP > selfBp)
	}
}

trait BoolPredPPrint { self: FOLFormulaPPrint =>
	def pprintBoolPred(pred: Pred, parentBP: Int): String = {
		val selfBp = getBP(pred)
		val retVal = pred match {
			case Pred("BoolPred", List(t: TermBool) ) => pprintTermBool(t, selfBp)
		}
		parenIf(retVal, parentBP > selfBp)
	}
}
