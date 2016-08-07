package models.pprint

import progsynth.types._
import progsynth.types.Types._
//import models.parser.FOLFormulaParser
import progsynth.synthesisold.ProgContext

object TermPPrintObj extends TermPPrint

trait TermPPrint extends PPrintUtils with TermIntPPrint with TermArrayIntPPrint
	with TermBoolPPrint with TermArrayBoolPPrint with BindingPowers {
	// docs/ProgSynthWebAppDoc.html#termprinters

	//parentBP: parent binding power.
	def pprintTerm(term: Term, parentBP: Int): String = term match {
		case termInt: TermInt => pprintTermInt(termInt, parentBP)
		case termArrayInt: TermArrayInt => pprintTermArrayInt(termArrayInt, parentBP)
		case termBool: TermBool => pprintTermBool(termBool, parentBP)
		case termArrayBool: TermArrayBool => pprintTermArrayBool(termArrayBool, parentBP)
	}

	def pprintTerm0(term: Term): String = pprintTerm(term, minBP);
}
//