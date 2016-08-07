package tests.synthesis

import progsynth.spec.StaticAssertions._
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.types._
import progsynth.types.Types._
import progsynth.spec.StaticAssertions.@@
import progsynth.spec.StaticAssertions.@@._
import progsynth.spec.UnknownFragment._

object ExistsPositiveElementSrc1 extends App {

	def existsPositiveElement1(arr: Array[Int], N: Int): Int =
		sRequire(N > 0){
		var r = 0
		sAssert(N > 0 && r == 0)
		UnknownFragment[Unit](1)
		sAssert((i: Int) => r == 1 iff ∃(i)∘(0 <= i && i < N && arr(i) > 0 ))
		r
	} sEnsuring{ (r) => (i: Int) =>
	r == 1 iff ∃(i)∘(0 <= i && i < N && arr(i) > 0 ) }

//	def existsPositiveElement2(arr: Array[Int], N: Int): Int =
//	sRequire(N > 0){
//		var r = 0
//		var n = 0
//		sLoopInv((i: Int) => (r == 1 iff ∃(i)∘(0 <= i && i < n && arr(i) > 0 )))
//		while(n != N){
//			sAssert((i: Int) => (r == 1 iff ∃(i)∘(0 <= i && i < n && arr(i) > 0 )))
//			UnknownFragment[Unit](2)
//			sAssert((i: Int) => (r == 1 iff ∃(i)∘(0 <= i && i < n+1 && arr(i) > 0 )))
//			n = n + 1
//		}
//		sAssert((i: Int) => (r == 1 iff ∃(i)∘(0 <= i && i < n && arr(i) > 0 )) && n == N)
//		r
//	} sEnsuring{ (r) => (i: Int) =>
//			r == 1 iff ∃(i)∘(0 <= i && i < N && arr(i) > 0 ) }

}