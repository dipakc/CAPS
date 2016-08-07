package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var


class AssumePreTV(freshVariables: List[Var], assumedPre: TermBool) extends ClassTV(
	{
		List(
			FieldTV("freshVariables", "freshVariables",
					ListTV(NewVarTV.getEmpty(),
							freshVariables.map(iv =>
								NewVarTV(Some(StringTV(iv.v)), Some(PSTypeTV(Some(iv.getType.getCleanName))))))),
			FieldTV("assumedPre", "assumedPre",
					TermBoolTV(assumedPre)))
	})
{
	override val tvName = "AssumePreTV"
	
}
