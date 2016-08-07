package models

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.Assert
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.methodspecs.InterpretedFns._
import scala.util.parsing.combinator.Parsers
import mqprinter.MQPrinter._
import models.parseruntyped.PSCSParser
import progsynth.synthesisold.ProgContext
import models.mqprinter.MQPrinter
import org.slf4j.LoggerFactory //
import progsynth.logger.PSLogUtils._

@RunWith(classOf[JUnitRunner])
class MQPrinterParserTest extends FunSuite with ShouldMatchers {

	val logger= LoggerFactory.getLogger("progsynth.MQPrinterParserTest")

	/** Parser Class */
	class MQParser extends PSCSParser(true) {
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
	}

	val mqParser = new MQParser()
	val ctx = {
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
		new ProgContext(Nil, valList, Nil)
	}

	def mytest(in: Term): Unit = {
		mytest(in, ctx)
	}

	def mytest(in: Term, myctx: ProgContext): Unit = {
		val mqTxt = MQPrinter.mqprintTerm0(in)
		val res = mqParser.parseAll(mqParser.termCSP(myctx), mqTxt)
		withClue(s"Parse not successful \n term: $in \n mqprint: $mqTxt \n "){
			res.successful should equal(true)
		}
		withClue(s"Parse resulted in different output: \n in : $in \n mqtxt: $mqTxt \n out: ${res.get}"){
			res.get should equal (in)
		}
	}

	test("VarConst 1") { mytest(ConstInt("0")) }
	test("VarConst 2") { mytest(ConstInt("-5")) }
	test("VarConst 3") { mytest(VarInt("x")) }
	test("VarConst 4") { mytest(ConstBool("true")) }
	test("VarConst 5") { mytest(ConstBool("false")) }
	test("VarConst 6") { mytest(VarBool("p")) }
	test("VarConst 7") { mytest(ConstArrayBool("Array(true, false)"))}

	test("III 1") { mytest(FnAppInt(PlusIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 2"){ mytest(FnAppInt(MinusIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 3") { mytest(FnAppInt(TimesIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 4") { mytest(FnAppInt(DivIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 5") { mytest(FnAppInt(PercentIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 6") { mytest(FnAppInt(UnaryMinusIntFn, VarInt("x") :: Nil))}
	test("III 7") { mytest(FnAppInt(MaxIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}
	test("III 8") { mytest(FnAppInt(MinIntFn, ConstInt("1") :: ConstInt("2") :: Nil))}

///////////////////////////
	test("Array 1") { mytest(ConstArrayInt("Array(1, 2, 3)"))}
	test("Array 2") { mytest(ArrSelectInt(VarArrayInt("arr"), ConstInt("0")))}
	test("Array 3") { mytest(ArrStoreArrayInt(VarArrayInt("arr"), ConstInt("0"), ConstInt("2")))}
	///////////////////////////////////////////
	test("Forall 1") {
		val i = VarInt("i")
		val c0 = ConstInt("0")
		val barr = VarArrayBool("barr")
		mytest(ForallTermBool(i, c0 <= i, barr.select(i)))
	}
	test("Forall 2") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		mytest(ForallTermBool(i :: j :: Nil, TermBool.TrueT, TermBool.TrueT))
	}


	test("Term: MaxQuantifier") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		mytest(MaxQTermInt(i :: Nil, TermBool.TrueT, ConstInt("0")))
	}

	test("Term: MaxQuantifierWithMulipleDummies") {
		val i = VarInt("i")
		val j = VarInt("j")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		mytest(MaxQTermInt(i :: j :: Nil, TermBool.TrueT, ConstInt("0")))
	}

	test("Term: MaxSegSumSpec") {
		val i = VarInt("i")
		val p = VarInt("p")
		val q = VarInt("q")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val arr = VarArrayInt("arr")
		val ctxVars = List(N, arr)
		val myctx = new ProgContext(Nil, ctxVars, Nil)
		mytest(MaxQTermInt(p :: q :: Nil, (c0 <= p) && p <= q && q <= N, PlusQTermInt(i, p <= i && i < q, arr.select(i))), myctx)
	}

	test("Term: ExistsTermBool") {
		val i = VarInt("i")
		val c0 = ConstInt("0")
		val barr = VarArrayBool("barr")
		mytest(ExistsTermBool(i, c0 <= i, barr.select(i)))
	}

	test("Term: MaxSpec") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		mytest(((z eqeq x) || (z eqeq y)) && (z >= x) && (z >= y))
	}

	test("Term: Equiv") {
		val p = VarBool("p")
		val q = VarBool("q")
		mytest(p equiv q)
	}


	test("Term: Operator precedence") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		mytest(((x + y * z < x + y) && (x eqeq y)) equiv ((x < y) && (x + y < z)))
	}

	test("Term: FnAppBool") {
		val p = VarBool("p")
		val q = VarBool("q")
		val r = VarBool("r")
		mytest((p && q))
		mytest(p || q)
		mytest(p impl q)
		mytest(p equiv q)
		mytest(!q)
	}

	test("Term: Int * Int => Bool") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")

		mytest(x <= y)

		mytest(x >= y)

		mytest(x < y)

		mytest(x > y)

		mytest(x eqeq y)
	}

	test("Meta variable term") {
		val x = VarInt("x")
		val xprime = VarInt("x'")
		val y = VarInt("y")
		val z = VarInt("z")

		val ctxNew = ctx.addVals(xprime :: Nil)

		var input = """x'"""
		mytest(xprime, ctxNew)
	}

	test("Negation Binding Power 1") {
		val x = VarInt("x")
		val y = VarInt("y")
		var z = !(x < y)
		mytest(z)
	}

	test("Negation Binding Power 2") {
		val x = VarInt("x")
		val y = VarInt("y")
		var z = !(x eqeq y)
		mytest(z)
	}

	test("Negation Binding Power 3") {
		val x = VarInt("x")
		val y = VarInt("y")
		var z = !(x < y) && (x < y)
		mytest(z)
	}



}
