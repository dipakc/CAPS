package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPostTactic
import models.mqprinter.MQPrinter._

class RTVInPostTV(constant: Term, variable: Var, initValue: Term, bounds: TermBool) extends ClassTV(
{
	List(
		FieldTV("constant", "constant", TermTV(constant)),
		FieldTV("variable", "variable", NewVarTV(variable)),
		FieldTV("initValue", "initValue", TermTV(initValue)),
		FieldTV("bounds", "bounds", TermBoolTV(Some(mqprintTerm0(bounds)))))
	})
{
	override val tvName = "RTVInPostTV"
	
}
