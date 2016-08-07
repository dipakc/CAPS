package tests.inputfiles
import progsynth.spec.StaticAssertions._
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.types._
import progsynth.types.Types._

object WhileLoopExample {

	def testMethod() = {
		var x = 0
		val y = 0
		var z = 0
		val w1 = {
			sLoopInv(TrueF)
			while (x < y) { z }
		}

		val w2 = {
			x = x + 1
			sLoopInv(TrueF)
			while (x < y) {	z }
			y
		}
	}

}
