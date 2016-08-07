package progsynth

import debug.PSDbg
import types.Types.FOLFormula
import types.FOLFormulaRich

object ProgSynth {
    def println1(arg: Any) = PSDbg.writeln1(arg)
    def println0(arg: Any) = PSDbg.writeln0(arg)
    implicit def toRichFormula(aFOLFormula: FOLFormula): FOLFormulaRich = new FOLFormulaRich(aFOLFormula)

    trait Counter {
		var cnt = 0
	    def getCnt = { cnt = cnt + 1; cnt }
		def resetCnt(newCnt: Int = 0) = {cnt = newCnt}
    }
}