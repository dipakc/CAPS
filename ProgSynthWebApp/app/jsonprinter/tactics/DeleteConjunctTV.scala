package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Term
import models.mqprinter.MQPrinter.mqprintTerm0


object DeleteConjunctTV{
	def apply(conjunct: TermBool, variant: Term) = {
		new DeleteConjunctTV(mqprintTerm0(conjunct), mqprintTerm0(variant))
	}
}

class DeleteConjunctTV(conjunct: String, variant: String) extends ClassTV(
	{
		List(
			FieldTV("conjunct", "conjunct",
					TermBoolTV(Some(conjunct))),
			FieldTV("variant", "variant",
					TermTV(Some(variant)))
		)
	})
{
	override val tvName = "DeleteConjunctTV"
	
}
