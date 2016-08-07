package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var

class ReplaceSubFormulaTV(oldSubFId: Int, newSubF: TermBool) extends ClassTV(
	{
		List(
			FieldTV("oldSubFId", "oldSubFId", IntegerTV(oldSubFId)),
			FieldTV("newSubF", "newSubF", TermBoolTV(newSubF)))
	})
{
	override val tvName = "ReplaceSubformulaTV" //TODO: Rename as ReplaceSubFormula
	
}
