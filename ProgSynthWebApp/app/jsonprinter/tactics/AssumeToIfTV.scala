package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class AssumeToIfTV(displayId: Int) extends ClassTV(
{
	List(
		FieldTV("displayId", "displayId", IntegerTV(displayId)))
	})
{
	override val tvName = "AssumeToIfTV"
	
}
