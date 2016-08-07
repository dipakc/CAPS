package tests.synthesis
import progsynth.spec.StaticAssertions._
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.types._
import progsynth.types.Types._

object MaxTestExtractCtxObj {
	def getMax(x: Int, y: Int): Int =
		sRequire(TrueF){
		var max = 0
		sAssert(TrueF)
		if (x < y) {
			sAssert(x < y)
			max = y
		} sEnsuring (r => And((x < y),(max == y)))
		else {
			sAssert(Not(x < y))
			max = x
		} sEnsuring (r => And(Not(x < y),(max == x)))

		sAssert(Or((And((x < y),(max == y))),And(Not(x < y),(max == x))))
		max
	} sEnsuring (max => And(((x <= y) impl (max == y)), ((x >= y) impl (max == x))))
}
