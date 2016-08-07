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

trait TermArrayIntPPrint { self: TermPPrint =>

	def pprintTermArrayInt0(term: TermArrayInt): String =
		pprintTermArrayInt(term, minBP)
		
	def pprintTermArrayInt(term: TermArrayInt, parentBP: Int): String = {
		val selfBp = getBP(term)
		val retVal = term match {
			case ConstArrayInt(name) => name
			case VarArrayInt(v) => v
			case ArrStoreArrayInt(arr, index, value) =>
				val arrStr = arr match {
					case _: ArrStore => paren(pprintTermArrayInt(arr, selfBp)) //paren needed since ArrStore can not have two different bindings.
					case _ => pprintTermArrayInt(arr, selfBp)
				}

				val indexStr = pprintTermInt0(index)
				val valueStr = pprintTermInt(value, selfBp)
				<a>{arrStr}[{indexStr}] := {valueStr}</a>.text
			case FnAppArrayInt(bfn, ts) =>
				throw new RuntimeException("case FnAppArrayInt not implemented in pprintTermArrayInt")
		}
		parenIf(retVal, parentBP > selfBp)
	}
}

