package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class ReplaceSubTermTV(subTermId: Int, newSubTerm: Term) extends ClassTV(
{
	List(
		FieldTV("subTermId", "subTermId", IntegerTV(subTermId)),
		FieldTV("newSubTerm", "newSubTerm", TermTV(newSubTerm)))
	})
{
	override val tvName = "ReplaceSubTermTV"
	
}
