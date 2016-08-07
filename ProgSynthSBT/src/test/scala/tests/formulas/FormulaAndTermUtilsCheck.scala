package tests.formulas
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import org.scalacheck.{Properties, Gen, Arbitrary, Shrink}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.prop.PropertyChecks
import progsynth.types.TermGen._
import progsynth.types.FormulaGen._
import tests.testutils.PSPropertyChecks
import progsynth.debug.PSDbg._
@RunWith(classOf[JUnitRunner])
class FormulaAndTermUtilsCheck extends PSPropertyChecks with ShouldMatchers with FunSuite {

	ignore("replaceVarTerm1") {
		forAll(termGenHt(4)) { term =>
			val boolList =
				term.getVars map { aVar =>
					var n = VarInt("n")
					term == term.replaceVar(aVar, n).replaceVar(n, aVar)
				}
			assert( boolList.foldLeft(true) { (f, bool) => (f && bool) } )
		}
	}

	ignore("replaceVarTerm2") {
		forAll(termGenHt(4)) { term =>
			var v1 = VarInt("v1")
			var t1 = VarInt("t1")
			val termNew = term.replaceVar(v1, t1)
			assert(!(termNew.getVars contains v1) &&
				(!(term.getVars contains v1) || (termNew.getVars contains t1)))
		}
	}

	ignore("existsSubTermTerm1") {
		//forAll(termGenHt(4)) { term =>
		forAllWithShrink (termAndPathGen(4), shrinkTermPath){ case (term, path) =>
			val subTerm = path.last
			writeln0("term: " + term)
			writeln0("subTerm: " + subTerm)
			withClue{ "term: " + term + "\n" + "subterm: " + subTerm}{
				assert(term.existsSubTerm(_ == subTerm))
			}
		}
	}

	ignore("Term should not contain v4") {
		//forAll(termGenHt(4)) { term =>
		forAll { term: Term =>
			writeln0("term: " + term)
			assert(! (term.getVars contains VarInt("v4")))
		}
	}

	test("replaceVarformula1") {
		forAll(folFormulaGenHt(4, 4)) { formula:FOLFormula =>
			val boolList =
				formula.getFreeVars() map { aVar =>
					var n = Var.mkVar("nnnn", aVar.getType)
					formula == formula.replaceVar(aVar, n).replaceVar(n, aVar)
				}
			assert(boolList.foldLeft(true) { (f, bool) => (f && bool) })
		}
	}
}


