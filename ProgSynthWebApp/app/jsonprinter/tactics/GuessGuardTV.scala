package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Term
import models.mqprinter.MQPrinter.mqprintTerm0


object GuessGuardTV{
	def apply(guard: TermBool) = {
		new GuessGuardTV(mqprintTerm0(guard))
	}
}

class GuessGuardTV(guard: String) extends ClassTV(
	{
		List(
			FieldTV("guard", "guard",
					TermBoolTV(Some(guard))))
	})
{
	override val tvName = "GuessGuardTV"
	
}
