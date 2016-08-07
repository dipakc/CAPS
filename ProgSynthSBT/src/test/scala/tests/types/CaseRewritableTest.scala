package tests.types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import progsynth.types._
import progsynth.types.Types._
import tests.testutils.PSPropertyChecks
import org.kiama.rewriting.Rewriter._

@RunWith(classOf[JUnitRunner])
class CaseRewritableTest  extends PSPropertyChecks with ShouldMatchers with FunSuite {
	test("formula") {
		forAll(FormulaGen.folFormulaGenHt(4, 4)) { formula: FOLFormula =>
			val f2 = formula.reconstruct(formula.deconstruct.toArray)
			formula should equal (f2)
		}
	}

	test("programAnn") {
		forAll(ProgramAnnGen.programAnnGenHt(4)) { pa: ProgramAnn =>
			val pa2 = pa.reconstruct(pa.deconstruct.toArray)
			(pa == pa2) should equal (true)
		}
	}

	test("maxProg") {
		import tests.types.SampleObjects.maxProgram
		val mp2 = topdown (id) (maxProgram)
		mp2.isDefined should equal (true)
		maxProgram == (mp2.get.asInstanceOf[ProgramAnn]) should equal (true)

		val strategy = rule {
			case inv: InvariantT if inv.rvVar.isDefined => inv.removeRvVar
			case x => x
		}
		val mp3 = bottomup (strategy) (maxProgram)
		maxProgram == (mp3.get.asInstanceOf[ProgramAnn]) should equal (false)

		val invCount = count {case inv: InvariantT => 1} (maxProgram)
		invCount should equal (18)

		val invRvVarCount = count {case inv: InvariantT if inv.rvVar.isDefined => 1} (maxProgram)
		invRvVarCount should equal (2)

		val strategy2 = rule {
			case VarInt("y") => VarInt("z")
			case x => x
		}

		val mp4 = bottomup (strategy2) (maxProgram)
		mp4.get.asInstanceOf[ProgramAnn] == maxProgram should equal (false)
	}

}
