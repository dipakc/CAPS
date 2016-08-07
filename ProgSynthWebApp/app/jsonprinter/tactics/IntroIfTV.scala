package jsonprinter
package tactics

import progsynth.types.TermBool
import progsynth.types.Term
import progsynth.types.Var
import models.mqprinter.MQPrinter.mqprintTerm0
import jsonprinter.VarTV

object IntroIfTV {
	
	def apply(guards: List[TermBool]) = {
		new IntroIfTV(guards.map(g => (mqprintTerm0(g))))
	}
}

class IntroIfTV(guards: List[String]) extends ClassTV(
	{
		val metaTV:TV  = TermBoolTV.getEmpty
		
		val items: List[TV] =
			guards.map(t => new TermBoolTV(Some(t)))
		
		List(FieldTV("guards", "guards",
				ListTV(metaTV, items)))
	})
{
	override val tvName = "IntroIfTV"
	
}
