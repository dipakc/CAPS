package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj

class StepIntoUnknownProgIdxTV(idx: Int) extends ClassTV(
	{
		List(FieldTV("idx", "idx", IntegerTV(idx)))
	})
{
	override val tvName = "StepIntoUnknownProgIdxTV"
	
}
