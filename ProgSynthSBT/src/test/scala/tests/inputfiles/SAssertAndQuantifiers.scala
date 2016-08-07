package tests.inputfiles

import progsynth.spec.StaticAssertions._
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.types._
import progsynth.types.Types._
import progsynth.spec.StaticAssertions.@@
import progsynth.spec.StaticAssertions.@@._

object SAssertAndQuantifiers {
	def test(x: Int, y: Int): Int =
		sRequire(TrueF){
		var max = 0
		sAssert((i: Int) => ∀(i)∘(TrueF))
		x
	} sEnsuring ((max) => () =>TrueF)
}