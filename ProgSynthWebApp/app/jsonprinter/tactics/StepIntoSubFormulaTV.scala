package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class StepIntoSubFormulaTV(subId: Int) extends ClassTV(
{
	List(
		FieldTV("subId", "subId", IntegerTV(subId)))
	})
{
	override val tvName = "StepIntoSubFormulaTV"
	
}
