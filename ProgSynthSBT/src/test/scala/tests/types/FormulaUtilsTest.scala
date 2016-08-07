package tests.types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import tests.testutils.PSPropertyChecks
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import progsynth.types.FormulaGen
import progsynth.types.And
import progsynth.types._
import progsynth.types.Types._
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class FormulaUtilsTest extends PSPropertyChecks with ShouldMatchers with FunSuite {
	ignore("collectSubF1") {
		forAll(FormulaGen.folFormulaGenHt(4, 4)) { f =>
			val numTopOrs = f.collectSubFTop{case Or(a, b) => true}.size
			val f2 = f.mapF{case Or(a, b) => And(a, b)}
			val numTopAnds = f2.collectSubFTop{case And(a, b) => true}.size
			PSDbg.writeln0(f)
			PSDbg.writeln0(f2)
			PSDbg.writeln0(numTopOrs + " " + numTopAnds)
			numTopOrs should equal (numTopAnds)
		}
	}
}