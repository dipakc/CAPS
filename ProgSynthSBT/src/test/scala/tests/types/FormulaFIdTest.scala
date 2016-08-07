package tests.types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import tests.testutils.PSPropertyChecks
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import progsynth.types._
import progsynth.types.Types._
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class FormulaFIdTest extends PSPropertyChecks with ShouldMatchers with FunSuite {
	test("setFIdAll1") {
		forAll(FormulaGen.folFormulaGenHt(4, 4)) { formula: FOLFormula =>
			PSDbg.writeln0(formula)
			val newF = formula.setFIdAll()
			newF.existsSubF{ _.fid == -1} should equal (false)
		}
	}
	
//	test("termGen"){
//		forAll(TermGen.fnAppGenSz(2, PSInt)) { term: Term =>
//		//forAll(TermGen.varGen(PSUnit)) { term: Term =>
//			PSDbg.writeln0(term)
//		}
//	}
}
