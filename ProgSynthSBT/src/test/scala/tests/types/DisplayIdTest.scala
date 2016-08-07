package tests.types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import tests.testutils.PSPropertyChecks
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import progsynth.types._
import progsynth.types.Types._
import progsynth.dsl.QQ._
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class DisplayIdTest extends PSPropertyChecks with ShouldMatchers with FunSuite {
//	test("test1") {
//		forAll(FormulaGen.folFormulaGenHt(4, 4)) { formula: FOLFormula =>
//			PSDbg.writeln0(formula)
//			formula.setDisplayIdAll
//			//val newF = newFO.get.asInstanceOf[FOLFormula]
//			//PSDbg.writeln0(progsynth.types.DisplayIdPrinter.toIdString(newF))
//			//newF.existsSubF { _.fid == -1 } should equal(false)
//			//TODO
//		}
//	}
//
//	test("test2") {
//		val f = Impl(Forall(VarInt("v3"), True1()), False1())
//		f.setDisplayIdAll
//		//val newF = newFO.get
//		//PSDbg.writeln0(progsynth.types.DisplayIdPrinter.toIdString(newF))
//		//TODO
//	}
//
//	test("no two ids are same") {
//		val x = VarInt("x")
//		val y = VarInt("y")
//		val f = ▶ ∀ x ∘ (y feqeq x)
//		f.setDisplayIdAll
//		//val newF = newFO.get.asInstanceOf[FOLFormula]
//		//PSDbg.writeln0(progsynth.types.DisplayIdPrinter.toIdString(newF))
//		//TODO
//	}
//
//	test("setting id does not change the programAnn") {
//	}
//
//	test("There does exist a node with id set to -1") {
//
//	}

	test("boolean term") {
		val x = VarBool("x")
		val y = VarBool("y")
		val f = x || y && x
		val prog = mkAssignmentTerms(f.inv(), Nil, f.inv())
		val progNew = prog.setDisplayIdAll.get.asInstanceOf[DisplayId]

		//println(newF.displayId)
		println(progsynth.types.DisplayIdPrinter.toIdString(progNew))
	}
}