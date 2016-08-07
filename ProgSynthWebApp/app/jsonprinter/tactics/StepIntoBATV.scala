package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var

class StepIntoBATV(lhsVars: List[Var]) extends ClassTV(
	{
		List(
			FieldTV("lhsVars", "lhsVars",
					ListTV(
							VarTV.getEmpty(),
							lhsVars.map(iv => VarTV(iv.v)))))
	})
{
	override val tvName = "StepIntoBATV"
	
}
