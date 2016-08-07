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

trait TermArrayBoolPPrint { self: TermPPrint =>
	
	def pprintTermArrayBool0(term: TermArrayBool): String =
		pprintTermArrayBool(term, minBP)
		
	def pprintTermArrayBool(term: TermArrayBool, parentBP: Int): String = {
		val selfBp = getBP(term)
		val retVal = term match {
			case ConstArrayBool(name) => name
			case VarArrayBool(v) => v
			case ArrStoreArrayBool(arr, index, value) =>
				val arrStr = pprintTermArrayBool(arr, selfBp)
				val indexStr = pprintTermInt0(index)
				val valueStr = pprintTermBool(value, selfBp)
				<a>{arrStr}[{indexStr}] := {valueStr}</a>.text
			case FnAppArrayBool(bfn, ts) =>
				throw new RuntimeException("case FnAppArrayInt not implemented in pprintTermArrayInt")
		}
		parenIf(retVal, parentBP > selfBp)
	}
}
