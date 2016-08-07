package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPostTactic
import models.mqprinter.MQPrinter._

class InsertVariableTV(aVar: Var, initVal: Term) extends ClassTV(
{
	List(
		FieldTV("aVar", "aVar", NewVarTV(aVar)),
		FieldTV("initVal", "initVal", TermTV(initVal)))
	})
{
	override val tvName = "InsertVariableTV"
	
}
