package models.pprint

import progsynth.methodspecs.InterpretedFns._
import progsynth.types._
import progsynth.types.Types._

//import models.TermPPrint.pprint
/**
 * import models._
 * import PPrintTerm._
 *
 *
 */
/**
 * Whether to parenthesize a node in a tree depends on the following things
 * 1. Case type of the node (constructor)
 * 2. Case type of the parent (constructor)
 * 3. Position of the node in parent type signature
 * eg. in arrStore arr[index] = value. index should never be parenthesized since it is already parenthesized.
 * if arr is ident then it need not parenthesized whereas if it is ArrayStore then it needs to be parenthezied.
 * Note that simple bindings will not work here. ((arr[0]:=1)[0] := 1)
 * */
trait BindingPowers {
	//bindingPower
	val minBP = -1
	val maxBP = 100

	def getBP(any: Any): Int = {
		val formulaBase = 0
		val predBase = 6
		val termIntBase = 7
		val termBoolBase = 7
		val termArrayIntBase = 7
		val termArrayBoolBase = 7

		any match {
			case Forall(v, f) => formulaBase
			case Exists(v, f) => formulaBase
			case Not(f) => formulaBase
			case Iff(f1, f2) => formulaBase + 1
			case Impl(f1, f2) => formulaBase + 2
			case Or(f1, f2) => formulaBase + 3
			case And(f1, f2) => formulaBase + 4
			case Atom(pred) => formulaBase + 5
			case TrueF => formulaBase + 6
			case FalseF => formulaBase + 6
			case Unknown() => throw new RuntimeException("Unknown not handled in getFormulaBP")
			//////////////////////////////////
			case Pred("$less", _) => predBase
			case Pred("$greater", _) => predBase
			case Pred("$less$eq", _) => predBase
			case Pred("$greater$eq", _) => predBase
			case Pred("$eq$eq", _) => predBase
			case Pred("BoolPred", _) => predBase
			///////////////////////////////
			case FnAppInt(PlusIntFn, _) => termIntBase
			case FnAppInt(MinusIntFn, _) => termIntBase
			//case ArrStoreInt(_, _, _) => base
			case FnAppInt(TimesIntFn, _) => termIntBase + 1
			case FnAppInt(PercentIntFn, _) => termIntBase + 1
			case FnAppInt(DivIntFn, _) => termIntBase + 1
			case FnAppInt(UnaryMinusIntFn, _) => termIntBase + 2
			case ConstInt(_)
			| VarInt(_)
			| ArrSelectInt(_, _) => termIntBase + 3
			case ArrStoreArrayInt(_, _, _) => termArrayIntBase
			case VarArrayInt(_) => maxBP
			case ConstArrayInt(_) => maxBP
			/////////////////////////////////
			case FnAppBool(EqEqBoolFn, _) => termBoolBase
			case FnAppBool(ImplBoolFn, _) => termBoolBase + 1
			case FnAppBool(RImplBoolFn, _) => termBoolBase + 1
			case FnAppBool(OrBoolFn, _) => termBoolBase + 2
			case FnAppBool(AndBoolFn, _) => termBoolBase + 3
			case FnAppBool(NegBoolFn, _) => termBoolBase + 4
			case _: ConstBool
			| _: VarBool
			| _: ArrSelectBool => termBoolBase + 5
			//////////////////////////////////
			case ArrStoreArrayBool(_, _, _) => termArrayBoolBase
			case VarArrayBool(_) => maxBP
			case ConstArrayBool(_) => maxBP
			case _ =>
				throw new RuntimeException("No match found in getBP for : " + any.toString)
		}
	}
}


