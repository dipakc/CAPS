package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._

class CollapseCompositionsTV() extends ClassTV(Nil)
{
	override val tvName = "CollapseCompositionsTV"
	
}
