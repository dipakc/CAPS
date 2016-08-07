package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

//displayId: Int, boundVar: Var
class SplitoutBoundVariableTV(displayId: Int, boundVar: Var) extends ClassTV(
{
	List(
		FieldTV("displayId", "displayId", IntegerTV(displayId)),
		FieldTV("boundVar", "boundVar", NewVarTV(boundVar)))
	})
{
	override val tvName = "SplitoutBoundVariableTV"
	
}
