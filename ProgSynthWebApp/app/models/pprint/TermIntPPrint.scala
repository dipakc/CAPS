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


trait TermIntPPrint { self: TermPPrint =>
	//parentBP is minBP when pprint is called at the outermost level
	def pprintTermInt0(term: TermInt) = pprintTermInt(term, minBP)

	def pprintTermInt(term: TermInt, parentBP: Int): String = {
		val selfBp = getBP(term)
		var retVal = term match {
			case const: ConstInt => const.name
			case variable: VarInt => variable.v
			case FnAppInt(bfn, t1 :: t2 :: Nil) =>
				<a>{pprintTerm(t1, selfBp)} {getSym(bfn)} {pprintTerm(t2, selfBp)}</a>.text
			case FnAppInt(UnaryMinusIntFn, t1 :: Nil) =>
				<a>-{pprintTerm(t1, selfBp)}</a>.text
			case FnAppInt(aFn, ts) =>
				<a>{aFn.name}({ts.map(pprintTerm(_, minBP)).mkString(", ")})</a>.text
			case ArrSelectInt(arr, index) =>
				<a>{pprintTermArrayInt(arr, selfBp)}[{pprintTermInt(index, selfBp)}]</a>.text
			case ForallTermBool(dummies, range, term) =>
				<a>(FORALL {dummies.map(_.v).mkString(", ")}: {pprintTermBool(range, minBP)}: {pprintTermBool(term, minBP) })</a>.text
			case ExistsTermBool(dummies, range, term) =>
				<a>(EXISTS {dummies.map(_.v).mkString(", ")}: {pprintTermBool(range, minBP)}: {pprintTermBool(term, minBP) })</a>.text
			case QTermInt(aFn, dummies, range, term) =>
				val argTpes = aFn.argTpes
				assert(argTpes.length == 2) // ensure that aFn is a binary function
				assert(argTpes.forall(_ == PSInt) )
				val tpe = aFn.tpe
				assert(tpe == PSInt)
				<a>({aFn.name.toUpperCase()} {dummies.map(_.v).mkString(", ")}: pprintTermBool(range, minBP): pprintTermInt(term, minBP))</a>.text

		}
		//Parenthesize only if parentBP is greater than selfBP
		parenIf(retVal, parentBP > selfBp)
	}

	def getSym(fn: Fn) = fn match {
		case UnaryMinusIntFn => "-"
		case TimesIntFn => "*"
		case DivIntFn => "/"
		case PercentIntFn => "%"
		case MinusIntFn => "-"
		case PlusIntFn => "+"
	}
}
