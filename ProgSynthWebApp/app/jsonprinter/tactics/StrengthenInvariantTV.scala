package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Term
import progsynth.types.Var
import models.mqprinter.MQPrinter.mqprintTerm0
import jsonprinter.VarTV

object StrengthenInvariantTV {
	
	def apply(newInvs: List[TermBool]) = {
		new StrengthenInvariantTV(newInvs.map(g => (mqprintTerm0(g))))
	}
}

class StrengthenInvariantTV(newInvs: List[String]) extends ClassTV(
	{
		val metaTV:TV  = TermBoolTV.getEmpty
		
		val items: List[TV] =
			newInvs.map(t => new TermBoolTV(Some(t)))
		
		List(FieldTV("newInvs", "newInvs",
				ListTV(metaTV, items)))
	})
{
	override val tvName = "StrengthenInvariantTV"
	
}
