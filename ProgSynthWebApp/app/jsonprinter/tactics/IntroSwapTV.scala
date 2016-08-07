package jsonprinter
package tactics

import progsynth.types.TermBool
import models.pprint.TermPPrintObj
import progsynth.types.Var
import progsynth.types.Term
import progsynth.synthesisnew.RTVInPost2Tactic
import models.mqprinter.MQPrinter._
import progsynth.types.TermInt

object IntroSwapTV{
	def apply(array: Var, index1: TermInt, index2: TermInt): IntroSwapTV = {
		new IntroSwapTV(mqprintTerm0(array), mqprintTerm0(index1), mqprintTerm0(index2))
	}
}

//IntroSwapTactic( array: Var, index1: TermInt, index2: TermInt)
class IntroSwapTV(array: String, index1: String, index2: String) extends ClassTV(
{
	List(
		FieldTV("array", "array", VarTV(array)),
		FieldTV("index1", "index1", TermTV(index1)),//TODO: Should we implement TermIntTV
		FieldTV("index2", "index2", TermTV(index2)))//TODO: Should we implement TermIntTV
	})
{
	override val tvName = "IntroSwapTV"
	
}
