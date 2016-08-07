package tests.formulas
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class FormulaAndTermUtilsTest extends FunSuite with ShouldMatchers {
	val trueF = True1()
	val falseF = False1()
	val xVar = VarInt("x")
	val yVar = VarInt("y")
	val zVar = VarInt("z")
	val plusFn = Fn("$plus", List(PSInt, PSInt), PSInt)
	val timesFn = Fn("$times", List(PSInt, PSInt), PSInt)
	val fiveConst = ConstInt("5")
	val sixConst = ConstInt("6")
	val aTermXTimesY = FnAppInt(timesFn, List(xVar, yVar)) //x + y
	val aTermXTimesYPlus5 = FnAppInt(plusFn, List(aTermXTimesY, fiveConst))
	val aTermComplex = FnAppInt(plusFn, List(aTermXTimesY, aTermXTimesYPlus5))
	val fSimple1 = Atom(Pred("$eq$eq", List(aTermXTimesYPlus5, zVar)))
	val fSimple2 = Atom(Pred("$less", List(aTermXTimesYPlus5, zVar)))
	val fSimple3 = Atom(Pred("$more", List(aTermXTimesYPlus5, aTermComplex)))
	val fAnd = And(fSimple1, fSimple2)
	val fOr = Or(fSimple1, fSimple2)
	var fOrAnd = Or(And(fSimple1, fSimple2), fSimple3)
	var fAndOr = And(Or(fSimple1, fSimple2), fSimple3)
	var fImpl = Impl(fOrAnd, fAndOr)
	var fIff = Iff(fOrAnd, fAndOr)
	var fNot = Not(fIff)

	test("mapTerms1") {
		val input1 = fOrAnd
		//writeln0((input1)
		//Or(And(Atom(Pred($eq$eq,List(FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5)))))))))
		val output1 = input1.mapTerms { term =>
			term match {
				case FnAppInt(Fn(x, a, b), c) => FnAppInt(Fn("myfn", a, b), c)
				case _ => term
			}
		}
		PSDbg.writeln0(output1.toString)
		output1.toString should equal("Or(And(Atom(Pred($eq$eq,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5)))))))))")
		//Or(And(Atom(Pred($eq$eq,List(FnApp(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnApp(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnApp(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), FnApp(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), FnApp(Fn($plus,List(PSInt, PSInt),PSInt),List(FnApp(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5)))))))))
	}

	test("mapTerms2") {
		val input = fOrAnd
		//writeln0((input)
		val output = input.mapTerms { term =>
			def fun(t1: Term): Term = {
				t1 match {
					case FnAppInt(Fn(x, a, b), c) => FnAppInt(Fn("myfn", a, b), c map fun)
					case _ => t1
				}
			}
			fun(term)
		}
		//writeln0((output)
		output.toString should equal("Or(And(Atom(Pred($eq$eq,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn(myfn,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5)))))))))")
	}

	test("mapTerms3") {
		val input = fOrAnd
		//writeln0((input)
		def fun(t1: Term): Term = t1 match {
			case VarInt("x") => VarInt("myvar")
			case _ => t1
		}
		val output = input.mapTerms { term =>
			fun(term)
		}
		//writeln0((output)
		output.toString should equal("Or(And(Atom(Pred($eq$eq,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), ConstInt(5)))))))))")
	}

	test("mapSubTerms1") {
		val input = fOrAnd
		//writeln0((input)
		val output = input.mapSubTerms { case VarInt("x") => VarInt("myvar") }
		//writeln0((output)
		output.toString should equal("Or(And(Atom(Pred($eq$eq,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(myvar), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(myvar), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(myvar), VarInt(y))), ConstInt(5))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(myvar), VarInt(y))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(myvar), VarInt(y))), ConstInt(5)))))))))")
	}

	test("replaceVar") {
		val input = fOrAnd
		//writeln0((input)
		val output = input.replaceVar(VarInt("x"), aTermXTimesY)
		//writeln0((output)
		output.toString should equal("Or(And(Atom(Pred($eq$eq,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), VarInt(y))), ConstInt(5))), VarInt(z)))),Atom(Pred($less,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), VarInt(y))), ConstInt(5))), VarInt(z))))),Atom(Pred($more,List(FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), VarInt(y))), ConstInt(5))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), VarInt(y))), FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(FnAppInt(Fn($times,List(PSInt, PSInt),PSInt),List(VarInt(x), VarInt(y))), VarInt(y))), ConstInt(5)))))))))")
	}
	//TODO: test Pred and Term utils function

	test("simplify1") {
		val input = fOrAnd
		val output = input.simplify()
		val expOutput = fOrAnd
		withClue("simplify output does not match expected result") {
			output should equal(expOutput)
		}
	}

	test("simplify2") {
		val input = And(fOrAnd, And(fOrAnd, And(True1(), True1())))
		val output = input.simplify()
		val expOutput = And(fOrAnd, fOrAnd)
		withClue("simplify output does not match expected result") {
			output should equal(expOutput)
		}
	}
}
