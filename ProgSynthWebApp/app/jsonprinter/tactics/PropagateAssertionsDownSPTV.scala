package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class PropagateAssertionsDownSPTV(displayId1: Int, displayId2: Int) extends ClassTV(
{
	List(
		FieldTV("displayId1", "displayId1", IntegerTV(displayId1)),
		FieldTV("displayId2", "displayId2", IntegerTV(displayId2)))
	})
{
	override val tvName = "PropagateAssertionsDownSPTV"
	
}
