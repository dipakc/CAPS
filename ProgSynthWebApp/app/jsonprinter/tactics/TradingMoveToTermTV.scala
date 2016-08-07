package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class TradingMoveToTermTV(displayId: Int, termToBeMovedId: Int) extends ClassTV(
{
	List(
		FieldTV("displayId", "displayId", IntegerTV(displayId)),
		FieldTV("termToBeMovedId", "termToBeMovedId", IntegerTV(termToBeMovedId)))
	})
{
	override val tvName = "TradingMoveToTermTV"
	
}
