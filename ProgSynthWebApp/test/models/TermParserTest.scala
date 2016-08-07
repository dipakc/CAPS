package models

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.Assert
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.methodspecs.InterpretedFns._
import progsynth.synthesisold.ProgContext
import models.pprint.TermPPrint
import progsynth.types.TermGen
import scalaz._
import Scalaz._
import models.parseruntyped.PSCSParser
import scala.util.parsing.combinator.Parsers
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

@RunWith(classOf[JUnitRunner])
class TermParserTest extends FunSuite with ShouldMatchers {

	val logger = LoggerFactory.getLogger("progsynth.TermParserTest")

	//
	/**delete whitespace utility function */
	def delWS(s: String) = "\\s".r.replaceAllIn(s, "")

	/** Prepare context */
	val valList = (
			   VarInt("x")
			:: VarInt("x56")
			:: VarInt("y")
			:: VarInt("z")
			:: VarInt("w")
			:: VarArrayInt("arr")
			:: VarArrayInt("arr2")
			:: VarArrayInt("arr3")
			:: VarBool("p")
			:: VarBool("q")
			:: VarBool("r")
			:: VarBool("s")
			:: VarInt("N")
			:: VarArrayBool("barr")
			:: VarArrayBool("barr2")
			:: VarArrayBool("barr3")
			:: Nil)

	val ctx = new ProgContext(Nil, valList, Nil)

	/** Parser Class */
	class testParser(isMathQuill: Boolean) extends PSCSParser(isMathQuill) {
		def testParserForSuccess[T](parser: Parser[T], input: String, expOutput: T): Unit = {
			val res = parseAll(parser, input)
			if(!res.successful)
				logger.trace("failed for input = " + input )
			if(res.successful && res.get != expOutput)
				logger.trace("different output for input = " + input + "; actual output: " + res.get)
			res.successful should equal (true)
			logger.trace("##############*****")
			logger.trace(res.toString)
			res.get should equal (expOutput)
		}

		def testParserForFailure[T](parser: Parser[T], input: String): Unit = {
			val res = parseAll(parser, input)
			res.successful should equal (false)
			if(res.successful) logger.trace("invalid input parsed input = " + input )
		}

		def printParserResult[T](parser: Parser[T], input: String): Unit = {
			val res = parseAll(parser, input)
			println(res);
		}

	}

	/** Parser Instances */
	val mqParser = new testParser(isMathQuill= true)
	val txtParser = new testParser(isMathQuill= false)

	/** Test Functions */
	test("Term: VarInt, ConstInt") {
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "0", ConstInt("0"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "5", ConstInt("5"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "-5", ConstInt("-5"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "x", VarInt("x"))
		mqParser.testParserForFailure(mqParser.termCSP(ctx), "x-")
	}

	test("Term: VarBool, ConstBool") {
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "true", ConstBool("true"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "false", ConstBool("false"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), "p", VarBool("p"))
	}

	test("Term: VarArrayBool, ConstArrayBool") {
		//mqParser.testParserForSuccess(mqParser.termCSP(ctx), "barr", VarArrayBool("barr"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), """Array\left(true, false\right)""", ConstArrayBool("Array(true, false)"))
	}

	test("Term: Int * Int -> Int Fnapp"){
		var input = "1 + 2"
		var eoutput = FnAppInt(PlusIntFn, ConstInt("1") :: ConstInt("2") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = "1 - 2"
		eoutput = FnAppInt(MinusIntFn, ConstInt("1") :: ConstInt("2") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """1 \ast 2"""
		eoutput = FnAppInt(TimesIntFn, ConstInt("1") :: ConstInt("2") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """1 \slash 2"""
		eoutput = FnAppInt(DivIntFn, ConstInt("1") :: ConstInt("2") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """1 \% 2"""
		eoutput = FnAppInt(PercentIntFn, ConstInt("1") :: ConstInt("2") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = "-x"
		eoutput = FnAppInt(UnaryMinusIntFn, VarInt("x") :: Nil)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: ConstArrayInt, VarArrayInt"){
		val input =  """Array\left(1, 2, 3\right)"""
		val eoutput = ConstArrayInt("Array(1, 2, 3)")
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		val input2 =  "arr"
		val eoutput2 = VarArrayInt("arr")
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input2, eoutput2)
	}

	test("Term: ArrSelectInt") {
		var input =  """arr\left[0\right]"""
		var eoutput = ArrSelectInt(VarArrayInt("arr"), ConstInt("0"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: ArrStoreArrayInt") {
		var input =  """\left(arr\left[0\right] := 2\right)"""
		var eoutput = ArrStoreArrayInt(VarArrayInt("arr"), ConstInt("0"), ConstInt("2"))
		mqParser.testParserForSuccess(mqParser.termArrayCSP(ctx), input, eoutput)
	}

	test("Term: ForallTermBool") {
		val i = VarInt("i")
		val c0 = ConstInt("0")
		val barr = VarArrayBool("barr")
		val input = """\left(\forall i:0\le i:barr\left[i\right]\right)"""
		val eoutput = ForallTermBool(i, c0 <= i, barr.select(i))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: ForallTermBoolMultipleDummies") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		//val input = """$\left(\forall i j:0\le i\wedge i\le j\wedge j<N:arr\left[i\right]\le arr\left[j\right]\right)$"""
		val input = """\left(\forall i,j:true:true\right)"""
		val eoutput = ForallTermBool(i :: j :: Nil, TermBool.TrueT, TermBool.TrueT)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: MaxQuantifier") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		val input = """\left(\Max i:true:0\right)"""
		val eoutput = MaxQTermInt(i :: Nil, TermBool.TrueT, ConstInt("0"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: MaxQuantifierWithMulipleDummies") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		val input = """\left(\Max i,j:true:0\right)"""
		val eoutput = MaxQTermInt(i :: j :: Nil, TermBool.TrueT, ConstInt("0"))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	//r=\left(\Maxp\:q:0\le p\wedge p\le q\wedge q\le N:\left(\Sum i:p\le i\wedge i<q:arr\left[i\right]\right)\right)
	test("Term: MaxSegSumSpec") {
		val i = VarInt("i")
		val p = VarInt("p")
		val q = VarInt("q")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		val ctxVars = List(N, arr)
		val myctx = new ProgContext(Nil, ctxVars, Nil)
		val input = """\left(\Max p,q:0\le p\wedge p\le q\wedge q\le N:\left(\Sum i:p\le i\wedge i<q:arr\left[i\right]\right)\right)"""
		val eoutput = MaxQTermInt(p :: q :: Nil, (c0 <= p) && p <= q && q <= N, PlusQTermInt(i, p <= i && i < q, arr.select(i)))
		mqParser.testParserForSuccess(mqParser.termCSP(myctx), input, eoutput)
	}

	test("Term: ExistsTermBool") {
		val i = VarInt("i")
		val c0 = ConstInt("0")
		val barr = VarArrayBool("barr")
		val input = """\left(\exists i:0\le i:barr\left[i\right]\right)"""
		val eoutput = ExistsTermBool(i, c0 <= i, barr.select(i))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: MaxSpec") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val input = """\left(z=x\vee z=y\right)\wedge z\ge x\wedge z\ge y"""
		val eoutput = ((z eqeq x) || (z eqeq y)) && (z >= x) && (z >= y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: Equiv") {
		val p = VarBool("p")
		val q = VarBool("q")
		val input = """p\equiv q"""
		val eoutput = (p equiv q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: Equiv2") {
		val p = VarBool("p")
		val x = VarInt("x")
		val input = """p\equiv x"""
		val eoutput = (p equiv x)
		mqParser.testParserForFailure(mqParser.termCSP(ctx), input)
	}

	test("Term: Not in Context variable") {
		val p = VarBool("p")
		val qqq = VarBool("qqq")
		val input = """p\equiv qqq"""
		val eoutput = (p equiv qqq)
		mqParser.testParserForFailure(mqParser.termCSP(ctx), input)
	}

	test("Term: Operator precedence") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val input = """x+y\ast z<x+y\wedge x=y\equiv x<y\wedge x+y<z"""
		val eoutput = ((x + y * z < x + y) && (x eqeq y)) equiv ((x < y) && (x + y < z))
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: FnAppBool") {
		val p = VarBool("p")
		val q = VarBool("q")
		val r = VarBool("r")
		var input = """p \wedge q"""
		var eoutput = (p && q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """p \vee q"""
		eoutput = (p || q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """p \Rightarrow q"""
		eoutput = (p impl q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """p \equiv q"""
		eoutput = (p equiv q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """\neg q"""
		eoutput = ( !q)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Term: Int * Int => Bool") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")

		var input = """x \le y"""
		var eoutput = (x <= y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """x \ge y"""
		eoutput = (x >= y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """x < y"""
		eoutput = (x < y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """x > y"""
		eoutput = (x > y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)

		input = """x = y"""
		eoutput = (x eqeq y)
		mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	}

	test("Meta variable term") {
		val x = VarInt("x")
		val xprime = VarInt("x'")
		val y = VarInt("y")
		val z = VarInt("z")

		val ctxNew = ctx.addVals(xprime :: Nil)

		var input = """x'"""
		var eoutput = xprime

		mqParser.testParserForSuccess(mqParser.termCSP(ctxNew), input, eoutput)
	}

	test("Chaining") {
	    val x = VarInt("x")
	    val y = VarInt("y")
	    val z = VarInt("z")

	    var input = """x \le y \le z"""
	    var eoutput = x <= y && y <= z
	    mqParser.testParserForSuccess(mqParser.termCSP(ctx), input, eoutput)
	    //mqParser.printParserResult(mqParser.termCSP(ctx), input)
	}
}
/*
//ConstInt
0
5
-5

//VarInt
x54
y24

//FnAppInt
1 + 2
1 - 2
1 * 2
1 / 2
1 % 2
- x

//ArrSelectInt
arr[0]

//ArrStoreArrayInt
arr[0] := 2

//TermInt
4 + -(5 * 6 + 7)
4/5
-(4 % 5) + 5
-(4 % x) + y
x + arr[1]

//ConstBool
true
false
//VarBool
p
q123

//FnAppBool
p && q
p || q
p ==> q
p == q
!p

//ArrSelectBool
barr[0]

//ArrStoreArrBool
barr[0] := true

//TermBool
(x && y ) || false
(x == y ) ==> false

*/

//Following code might be useful for implementing tests with generators.
//import org.scalatest.prop.PropertyChecks
//class TermParserTest extends FunSuite with ShouldMatchers with PropertyChecks{
//
/*
	/* Will not work if input contains redundant parenthesis.
	 * Redundant spaces are handled)*/
	def testIntForSuccess2(input: String) = {
		val parseResult = tp.parseAll(tp.termIntP, input)
		withClue(""){
			parseResult.successful should equal (true)
			logger.trace("input: " + delWS(input))
			logger.trace("output: " + delWS(pprint(parseResult.get)))
			delWS(pprint(parseResult.get)) should equal (delWS(input))
		}
	}


	def testBoolForSuccess2(input: String) = {
		val res = tp.parseAll(tp.termBoolP, input)
		withClue("input: " + delWS(input) + "\n" + "output: " + delWS(pprint(res.get))){
			res.successful should equal (true)
			(res.get |> pprint |> delWS) should equal (input |> delWS)
		}
	}


	test("TermInt") {
		testIntForSuccess2( """4 + -(5 * 6 + 7)""")
		testIntForSuccess2( "4 + -(5 * 6) + 7")
		testIntForSuccess2( "4 % -(5 * 6 + 7)")
		testIntForSuccess2( "(arr[0] := 2)[0] % -(5 * 6 + 7)")
	}

	test("BoolTerms") {
		testBoolForSuccess2("true")
		testBoolForSuccess2("false")
		testBoolForSuccess2("p")
		testBoolForSuccess2("q")
		testBoolForSuccess2("p && q")
		testBoolForSuccess2("p || q")
		testBoolForSuccess2("p ==> q")
		testBoolForSuccess2("p == q")
		testBoolForSuccess2("!p")
		testBoolForSuccess2("barr[0]")
		testBoolForSuccess2("barr[x + 1]")
		testBoolForSuccess2("(barr[x + y] := true)[0]")
		testBoolForSuccess2("p && q || false")
		testBoolForSuccess2("(q == r) ==> false")
	}

*/