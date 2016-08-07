package progsynth.types

import Types._
import org.scalacheck.Gen
import progsynth.types.TermGen._
import org.scalacheck.Shrink
import progsynth.utils.{PSUtils=>psu}
import progsynth.debug.PSDbg._

object FormulaGen {

	def predGenHt(pht: Int) = {
		for {
			r <- Gen.oneOf("eqeq", "lesseq", "greatereq", "less", "greater") //TODO: try standard predicates
			arity <- Gen.choose(0, 4)
			ts <- Gen.listOfN(arity, termGenHt(pht))
		} yield Pred(r, ts)
	}

	def folFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		ht match {
			case _ if ht > 0 =>
				Gen.frequency(
					(1, leafFormulaGen),
					(2, atomFormulaGenHt(pht)),
					(2, notFormulaGenHt(ht - 1, pht)),
					(2, andFormulaGenHt(ht - 1, pht)),
					(2, orFormulaGenHt(ht - 1, pht)),
					(2, implFormulaGenHt(ht - 1, pht)),
					(2, iffFormulaGenHt(ht - 1, pht)),
					(2, existsFormulaGenHt(ht - 1, pht)),
					(2, forallFormulaGenHt(ht - 1, pht)))
			case _ if ht <= 0 =>
				Gen.frequency(
					(1, leafFormulaGen),
					(3, atomFormulaGenHt(pht)))

		}
	}

	lazy val trueGen = Gen.value(True1[Pred]())
	lazy val falseGen = Gen.value(False1[Pred]())
	lazy val unkGen = for (i <- Gen.choose(0, 20)) yield Unknown[Pred]()
	lazy val leafFormulaGen = Gen.oneOf(trueGen, falseGen, unkGen)

	def atomFormulaGenHt(pht: Int): Gen[FOLFormula] = {
		for {
			pred <- predGenHt(pht)
		} yield {
			Atom(pred)
		}
	}

	def notFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for { f <- folFormulaGenHt(ht - 1, pht) }
			yield Not(f)
	}

	def andFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f1 <- folFormulaGenHt(ht - 1, pht);
			f2 <- folFormulaGenHt(ht - 1, pht)
		} yield {
			And(f1, f2)
		}
	}

	def orFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f1 <- folFormulaGenHt(ht - 1, pht);
			f2 <- folFormulaGenHt(ht - 1, pht)
		} yield {
			Or(f1, f2)
		}
	}

	def implFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f1 <- folFormulaGenHt(ht - 1, pht);
			f2 <- folFormulaGenHt(ht - 1, pht)
		} yield {
			Impl(f1, f2)
		}
	}

	def iffFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f1 <- folFormulaGenHt(ht - 1, pht);
			f2 <- folFormulaGenHt(ht - 1, pht)
		} yield {
			Iff(f1, f2)
		}
	}

	def forallFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f <- folFormulaGenHt(ht - 1, pht);
			name <- varGen
		} yield {
			Forall(name, f)
		}
	}

	def existsFormulaGenHt(ht: Int, pht: Int): Gen[FOLFormula] = {
		for {
			f <- folFormulaGenHt(ht - 1, pht);
			name <- varGen
		} yield {
			Exists(name, f)
		}
	}

	lazy val dummyVarGen = Gen.oneOf("v1", "v2", "v3", "v4", "t1", "t2")

	/** Shrinks a formula*/
	def shrinkFormulaImpl(implicit termShr: Shrink[Term]): Shrink[FOLFormula] = {
		writeln0("formula shrink called")
		def shrinkFormula(f: FOLFormula): Stream[FOLFormula] = {
			f match {
				case Atom(Pred(r, ts)) =>
					//TODO: shrink the terms
					val a: List[List[Term]] = ts map (term => (termShr.shrink(term)).toList)
					val tss: List[List[Term]] = psu.crossProd(a)
					val c: List[FOLFormula] = tss map { ts2 =>
						Atom(Pred(r, ts2))
					}
					val b: Stream[FOLFormula] = Stream(True1[Pred](), False1[Pred]())
					b append c
				case _ =>
					val a: Stream[FOLFormula] = Stream(f.childs:_*)
					val b: Stream[FOLFormula] = {
							for (cf <- f.childs.toStream)
								yield shrinkFormula(cf)
						}.flatten
					a append b
			}
		}
		Shrink { shrinkFormula _ }
	}

	/** Shrinks a formula but do not shrink the term */
	def shrinkFormulaImpl2(implicit termShr: Shrink[Term]): Shrink[FOLFormula] = {
		writeln0("formula shrink called")
		def shrinkFormula(f: FOLFormula): Stream[FOLFormula] = {
			f match {
				case Atom(Pred(r, ts)) =>
					val b: Stream[FOLFormula] = Stream(True1[Pred](), False1[Pred]())
					b
				case _ =>
					val a: Stream[FOLFormula] = Stream(f.childs:_*)
					val b: Stream[FOLFormula] = {
							for (cf <- f.childs.toStream)
								yield shrinkFormula(cf)
						}.flatten
					a append b
			}
		}
		Shrink { shrinkFormula _ }
	}

}
