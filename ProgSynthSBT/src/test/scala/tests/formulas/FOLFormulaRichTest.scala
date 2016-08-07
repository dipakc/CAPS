package tests.formulas
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.dsl.QQ._

@RunWith(classOf[JUnitRunner])
class FOLFormulaRichTest extends FunSuite with ShouldMatchers {
	trait Setup {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val p = VarInt("p")
		val q = VarInt("q")
		val r = VarInt("r")
		val s = VarInt("s")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val funFn = Fn("fun", List(PSInt, PSInt), PSInt)
		val arr = VarArrayInt("arr")
		def fun(t1: Term, t2: Term): Term = FnAppInt(funFn, List(t1, t2))
	}

	test("FOLFormulaRich.getFreeVars_1") {
		new Setup {
			var f: FOLFormula = ▶ ∀ x ∘ (y feqeq x)
			f.getFreeVars().toSet should equal (List(y).toSet)
		}
	}

	test("FOLFormulaRich.getFreeVars_2") {
		new Setup {
			val f: FOLFormula = ▶ ∀ x ∘ (fun(x, y) feqeq x)
			f.getFreeVars().toSet should equal (List(y).toSet)
		}
	}

	test("FOLFormulaRich.getFreeVars_3") {
		new Setup {
			val f:FOLFormula = ((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y)
			f.getFreeVars().toSet should equal (List(arr, x, y).toSet)
		}
	}

	test("FOLFormulaRich.getFreeVars_4") {
		new Setup {
			val f = ▶ ∀ x ∘ (((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y))
			f.getFreeVars().toSet should equal (List(arr, y).toSet)
		}
	}
	test("FOLFormulaRich.getFreeVars_5") {
		new Setup {
			val f = ▶ ∀ x ∀ y∘ (((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y))
			f.getFreeVars().toSet should equal (List(arr).toSet)
		}
	}
	test("FOLFormulaRich.getFreeVars_6") {
		new Setup {
			val f = ▶ ∃x ∀y∘ (((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y))
			f.getFreeVars().toSet should equal (List(arr).toSet)
		}
	}
	test("FOLFormulaRich.getFreeVars_7") {
		new Setup {
			val f = ▶ ∀y∘(((arr.select(x) feqeq x) f_/\ ▶ ∃x ∘(arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y))
			f.getFreeVars().toSet should equal (List(arr, x).toSet)
		}
	}
	test("FOLFormulaRich.getFreeVars_8") {
		new Setup {
			val f = ▶ ∀y∘(((arr.select(y) feqeq y) f_/\ ▶ ∃x ∘(arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y))
			f.getFreeVars().toSet should equal (List(arr, x).toSet)
		}
	}
	test("FOLFormulaRich.getFreeVars_9") {
		new Setup {
			val f = (fun(q, y) feqeq z) f_\/ (r f_< s) f_=== (p f_< c1) f_/\ TrueF f_\/ FalseF
			f.getFreeVars().toSet should equal (List(q, y, z, r, s, p).toSet)
		}
	}

	test("FOLFormulaRich.collectItemsWithContext_1") {
		new Setup {
			//(Node, ctx) -> retValue
			def getAndWhoseParentIsOr: PartialFunction[(Any, FOLFormula), FOLFormula] = {
				case ( item: And[Pred], parent) =>
					//writeln0((parent)
					item
			}
			//(Node, OldCtx) -> newCtx
			def getNewParent: PartialFunction[(Any, FOLFormula), FOLFormula] = {
				case (item: FOLFormula, _ )=>
					item
			}

			def collectAndWhoseParentIsOr(f:FOLFormula): List[FOLFormula] = {
				( f.collectItemsWithContext(getAndWhoseParentIsOr, getNewParent)(TrueF)) distinct
			}
			val f = (x f_< c0) f_\/ ((x f_> y) f_/\ (y f_< z))
			val expectedSet = Set(And(Atom(Pred("$greater",List(x, y))),Atom(Pred("$less",List(y, z)))))
			collectAndWhoseParentIsOr(f).toSet should equal (expectedSet)
		}
	}

	test("FOLFormulaRich.collectItemsWithContext_2") {
		new Setup {
			def collectPredicateWhoseAncestorIsAnd(f: FOLFormula): List[Pred] = {
				def isItemToBeCollected:  PartialFunction[(Any, Boolean), Pred] =
					{case (p: Pred, true ) => p}
				def setAncestorFlag : PartialFunction[(Any, Boolean), Boolean] =
					{case (itm: And[Pred], _ ) => true }
				( f.collectItemsWithContext(isItemToBeCollected, setAncestorFlag)(false)) distinct
			}
			val f = (x f_< c0) f_\/ ((x f_> y) f_/\ (y f_< z))
			val expectedSet = Set(Pred("$greater",List(x, y)), Pred("$less",List(y, z)))
			collectPredicateWhoseAncestorIsAnd(f).toSet should equal (expectedSet)
		}
	}

	test("FOLFormulaRich.collectItemsWithContext_3") {
		new Setup {
			def collectPredicateWhoseAncestorIsAnd(f: FOLFormula): List[Pred] = {
				def isItemToBeCollected:  PartialFunction[(Any, Boolean), Pred] =
					{case (p: Pred, true ) => p}
				def setAncestorFlag : PartialFunction[(Any, Boolean), Boolean] =
					{case (itm: And[Pred], _ ) => true }
				( f.collectItemsWithContext(isItemToBeCollected, setAncestorFlag)(false)) distinct
			}
			val f = (x f_< c0) f_/\ ((x f_> y) f_\/ (y f_< z))
			val expectedSet = Set(Pred("$less",List(VarInt("x"), ConstInt("0"))), Pred("$greater",List(x, y)), Pred("$less",List(y, z)))
			collectPredicateWhoseAncestorIsAnd(f).toSet should equal (expectedSet)
		}
	}


}