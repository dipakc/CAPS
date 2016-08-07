package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class RTVInPost2TV(displayId: Int, variable: Var, initValue: Term, bounds: TermBool) extends ClassTV(
{
	List(
		FieldTV("displayId", "displayId", IntegerTV(displayId)),
		FieldTV("variable", "variable", NewVarTV(variable)),
		FieldTV("initValue", "initValue", TermTV(initValue)),
		FieldTV("bounds", "bounds", TermBoolTV(Some(mqprintTerm0(bounds)))))
	})
{
	override val tvName = "RTVInPost2TV"
	
}
