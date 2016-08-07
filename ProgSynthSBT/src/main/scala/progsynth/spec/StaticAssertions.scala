package progsynth.spec
import scala.annotation.elidable.ASSERTION
import scala.annotation.elidable

import progsynth.types._
import progsynth.types.Types._

object StaticAssertions extends AssertDefs with RequireDefs with LoopInvDefs
						with EnsuringDefs with EnsuringDefsTuple with QuantifierDSL with PredicateDSL

trait AssertDefs {
	@elidable(ASSERTION)
	def sAssert(assertion: FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1](fun: Function1[T1, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	def sAssert[T1, T2](fun: Function2[T1, T2, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3](fun: Function3[T1, T2, T3, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	@deprecated("", "")
	def sAssert(loc: Int)(formula: FOLFormula) { formula }
}

trait RequireDefs {
	@elidable(ASSERTION)
	def sRequire[T](formula: FOLFormula)(arg: T):T = {arg}

	@elidable(ASSERTION)
	def sRequire[T, T1](fun: Function1[T1, FOLFormula])(arg: T):T = {arg}

	@elidable(ASSERTION)
	def sRequire[T, T1, T2](fun: Function2[T1, T2, FOLFormula])(arg: T):T = {arg}

	@elidable(ASSERTION)
	def sRequire[T, T1, T2, T3](fun: Function3[T1, T2, T3, FOLFormula])(arg: T):T = {arg}

	@elidable(ASSERTION)
	@deprecated("", "")
	def sRequire[T](loc: Int)(formula: FOLFormula)(arg: T):T = {arg}
}

trait LoopInvDefs {
	@elidable(ASSERTION)
	def sLoopInv(assertion: FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1](fun: Function1[T1, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2](fun: Function2[T1, T2, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3](fun: Function3[T1, T2, T3, FOLFormula]) { TrueF}

	@elidable(ASSERTION)
	@deprecated("", "")
	def sLoopInv(loc: Int)(formula: FOLFormula) { formula }
}

trait EnsuringDefs {
	implicit def any2Ensuring[A](x: A): Ensuring[A] = new Ensuring(x)
	final class Ensuring[A](val x: A) {
		def sEnsuring(fun: (A) => Any): A = { x }
	}
}

trait EnsuringDefsTuple {
	implicit def any2EnsuringTuple2[A, B](x: (A, B)): EnsuringTuple2[A, B] = new EnsuringTuple2(x)

	final class EnsuringTuple2[A, B](val x: (A, B)) {
		def sEnsuring(fun: (A, B) => Any): (A, B) = { x }
	}
}

trait QuantifierDSL /*extends sAssertDSL*/{
	object @@ {
		def ∀[T](x: T) = @@
		def ∃[T](x: T) = @@
		def ∘(x: FOLFormula): FOLFormula = True1()
		//def apply(x: Boolean) = Boolean
		//def ##(x: Boolean) = true
	}
}

trait PredicateDSL {
	/** == Usage: ==
	   {{{
	   object myPreds extends PredicateDefsTrait {
		//...
		//arr[m] is the minimum of the of arr[p..q]
		def minElem(m: Int, arr: Array[Int], p: Int, q: Int) = definePredicate { (i: Int) =>
			∀(i)∘(p <= i && i < q impl arr(m) <= arr(i))
		}
		//...
		}}}
	*/
	def definePredicate[T](arg: T): Boolean = true
}

/** Object defining predicates should extend this trait.*/
trait PredicateDefsTrait {

}
/*
trait sAssertDSL {
	@elidable(ASSERTION)
	def sAssert[T](assertion: T =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2](assertion: (T1, T2) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3](assertion: (T1, T2, T3) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4](assertion: (T1, T2, T3, T4) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5](assertion: (T1, T2, T3, T4, T5) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6](assertion: (T1, T2, T3, T4, T5, T6) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7](assertion: (T1, T2, T3, T4, T5, T6, T7) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8](assertion: (T1, T2, T3, T4, T5, T6, T7, T8) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sAssert[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) =>FOLFormula) { assertion}


	@elidable(ASSERTION)
	def sLoopInv[T](assertion: T =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2](assertion: (T1, T2) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3](assertion: (T1, T2, T3) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4](assertion: (T1, T2, T3, T4) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5](assertion: (T1, T2, T3, T4, T5) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6](assertion: (T1, T2, T3, T4, T5, T6) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7](assertion: (T1, T2, T3, T4, T5, T6, T7) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8](assertion: (T1, T2, T3, T4, T5, T6, T7, T8) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) =>FOLFormula) { assertion}

	@elidable(ASSERTION)
	def sLoopInv[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22](assertion: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) =>FOLFormula) { assertion}
}
*/
