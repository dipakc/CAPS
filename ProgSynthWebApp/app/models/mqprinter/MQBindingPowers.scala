package models.mqprinter

import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns._

/**
 * - Binding power of all the atomic constructs in MAXBP
 * - Binding power of all the constructs whose concrete representation is surrounded by brackets have MAXBP binding power
 * - Atomic Constructs(maxBP) - III(intBP) - IIB(predBP) - BBB(boolBP)- minBP
*/
object MQBindingPowers {
	//bindingPower
	val maxBP = 40
	val intBP = 30
	val predBP = 20
	val boolBP = 10
	val minBP = 0

	def getBP(t: Term): Int = {
		t match {
			case Var(_)
			| Const(_)
			| ArrSelect(_, _)
			| ArrStore(_, _, _)
			| QTerm(_, _, _, _)  => maxBP
			case FnApp(fn, ts ) => getBP(fn: Fn)
		}
	}

	def getBP(fn: Fn): Int = fn match {
		case PowIntFn
		| MinIntFn
		| MaxIntFn => maxBP
		case UnaryMinusIntFn => intBP
		case TimesIntFn
		| DivIntFn
		| PercentIntFn => intBP - 1
		case PlusIntFn
		| MinusIntFn => intBP - 2
		/////////////
		case GTBoolFn
		| LTBoolFn
		| GEBoolFn
		| LEBoolFn
		| EqEqBoolFn => predBP
		/////////////
		case NegBoolFn => predBP + 1// older Value boolBP. Changed so that !(x < y) is always brackeed. This is how parsing is designed.
		case AndBoolFn => boolBP - 1
		case OrBoolFn => boolBP - 2
		case ImplBoolFn
		| RImplBoolFn => boolBP - 3
		case EquivBoolFn => boolBP - 4
	}
}
