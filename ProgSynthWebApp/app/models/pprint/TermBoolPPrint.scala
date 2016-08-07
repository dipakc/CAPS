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

trait TermBoolPPrint { self: TermPPrint =>

	def pprintTermBool0(term: TermBool): String = pprintTermBool(term, minBP)

	def pprintTermBool(term: TermBool, parentBP: Int): String = {
		val selfBp = getBP(term)
		var retVal = term match {
			case ConstBool(name) => name
			case VarBool(v) => v
			case FnAppBool(bfn, t1 :: t2 :: Nil) =>
				<a>{pprintTerm(t1, selfBp)} {getBoolFnSym(bfn)} {pprintTerm(t2, selfBp)}</a>.text
			case FnAppBool(NegBoolFn, t1 :: Nil) =>
				<a>{getBoolFnSym(NegBoolFn)}{pprintTerm(t1, selfBp)}</a>.text
			case FnAppBool(aFn, ts) =>
				<a>{aFn.name}({ts.map(pprintTerm(_, minBP)).mkString(", ")})</a>.text
			case ArrSelectBool(arr, index) =>
				<a>{pprintTermArrayBool(arr, selfBp)}[{pprintTermInt(index, selfBp)}]</a>.text
			case QTermBool(aFn, dummies, range, term) =>
				val argTpes = aFn.argTpes
				assert(argTpes.length == 2) // ensure that aFn is a binary function
				assert(argTpes.forall(_ == PSBool) )
				val tpe = aFn.tpe
				assert(tpe == PSBool)
				<a>({aFn.name.toUpperCase()} {dummies.map(_.v).mkString(", ")}: pprintTermBool(range, minBP): pprintTermBool(term, minBP))</a>.text
		}
		//Parenthesize only if parentBP is greater than selfBP
		parenIf(retVal, parentBP > selfBp)
	}

	def getBoolFnSym(fn: Fn) = fn match {
		case NegBoolFn => "!"
		case AndBoolFn => "&&"
		case OrBoolFn => "||"
		case ImplBoolFn => "==>"
		case RImplBoolFn => "<=="
		case EqEqBoolFn => """==="""
	}
}

