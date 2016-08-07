package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Term
import models.mqprinter.MQPrinter.mqprintTerm0


object ReplaceFormulaTV{
	def apply(newFormula: TermBool) = {
		new ReplaceFormulaTV(mqprintTerm0(newFormula))
	}
}

class ReplaceFormulaTV(newFormula: String) extends ClassTV(
	{
		List(
			FieldTV("newFormula", "newFormula",
					TermBoolTV(Some(newFormula))))
	})
{
	override val tvName = "ReplaceFormulaTV"
	
}
