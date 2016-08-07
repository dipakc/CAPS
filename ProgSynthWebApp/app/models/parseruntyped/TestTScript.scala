package models.parseruntyped
import progsynth.types._
import progsynth.types.Types._
import progsynth.synthesisold.ProgContext
import models.pprint.FOLFormulaPPrint
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._


object TestTScript extends TermCSParsers with PredCSParsers with FOLFormulaCSParsers
	with FOLFormulaPPrint  with App
{
	override val logger = LoggerFactory.getLogger("progsynth.TestTScript")

	override val isMQ = false
	val (x, y, z, w) = (VarInt("x"), VarInt("y"), VarInt("z"), VarInt("w"))
	val (arr, arr2, arr3) = (VarArrayInt("arr"), VarArrayInt("arr2"), VarArrayInt("arr3"))
	val (p, q, r, s) = (VarBool("p"), VarBool("q"), VarBool("r"), VarBool("s"))
	val (barr, barr2, barr3) = (VarArrayBool("barr"), VarArrayBool("bar2"), VarArrayBool("barr3"))


	implicit var progCtx = new ProgContext(varList = Nil,
				valList = List(x, y, z, w, arr, arr2, arr3, p, q, r, s, barr, barr2, barr3), Nil)
	//---------------------------------------------------------------------------------------------
	def termTtest(input: String)(implicit progCtx: ProgContext) {
		val res: ParseResult[TermT] = parseAll(termBasicTP, input)
		res match {
			case Success(result, next) =>
				val termO = getBasicTerm(result);
				termO match {
					case Some(term) => logger.trace(pprintTerm0(term))
					case None => logger.trace("failed to get Term")
				}

			case Failure(msg, next) => logger.trace(msg)
			case Error(msg, next) => logger.trace(msg)
		}
		logger.trace("res: " + res)
	}

	def termTest(input: String)(implicit progCtx: ProgContext) {
		val res: ParseResult[Term] = parseAll(termCSP, input)
		res match {
			case Success(result, next) =>
				logger.trace(pprintTerm0(result))
			case Failure(msg, next) => logger.trace(msg)
			case Error(msg, next) => logger.trace(msg)
		}
		logger.trace("res: " + res)
	}

	def predTest(input: String)(implicit progCtx: ProgContext) {
		val res: ParseResult[List[Pred]] = parseAll(predCSP, input)
		res match {
			case Success(result, next) =>
				for(pred <- result){
					logger.trace(pprintPred0(pred))
				}
			case Failure(msg, next) => logger.trace(msg)
			case Error(msg, next) => logger.trace(msg)
		}
		logger.trace("res: " + res)
	}

	def folFormulaTest(input: String)(implicit progCtx: ProgContext) {
		val res: ParseResult[FOLFormula] = parseAll(folFormulaCSP, input)
		res match {
			case Success(result, next) =>
				logger.trace(pprintFOLFormula0(result))
			case Failure(msg, next) => logger.trace(msg)
			case Error(msg, next) => logger.trace(msg)
		}
		logger.trace("res: " + res)
	}

	//---------------------------------------------------------------------------------------------

	termTtest("x + y")
	termTtest("(x + y) / 4 * z")
	termTtest("""p \/ (q ==> r)""")
	//termTest("x + y || z")
	//predTest("x < y <= p")
	//folFormulaTest("""x < y /\ (\forall i : i < y) /\ m < x""")
	//termTest("z < x")

}

object SimpleParseTests extends App {
    import scala.util.parsing.combinator._

    class AAA extends RegexParsers {

        def p1: Parser[String]    = ("a" | "ab") ^^ { _.toString }
        def ap: Parser[String] = "a" ^^ { _.toString }
        def p2: Parser[String] = (p2 ~ ap) ^? { case x ~ y => x + y } | ap
        def p3: Parser[String] = (ap ~ p3) ^? { case x ~ y => x + y } | ap
    }



    class BBB extends RegexParsers with PackratParsers {
        lazy val p1: PackratParser[String]    = ("a" | "ab") ^^ { _.toString }
        lazy val ap: PackratParser[String] = "a" ^^ { _.toString }
        lazy val p2: PackratParser[String] = (p2 ~ ap) ^? { case x ~ y => x + y } | ap
        lazy val p3: PackratParser[String] = (ap ~ p3) ^? { case x ~ y => x + y } | ap

    }

//    val aaa = new AAA();
//    println(aaa.parseAll(aaa.p1, """a"""));//OK
//    println(aaa.parseAll(aaa.p1, """ab"""));// FAIL
//    //println(aaa.parseAll(aaa.p2, """aaa"""));// FAIL: Stack overflow
//    println(aaa.parseAll(aaa.p3, """aaa"""))//OK
//
//    val bbb = new BBB();
//    println(bbb.parseAll(bbb.p1, """a"""));//OK
//    println(bbb.parseAll(bbb.p1, """ab"""));// FAIL
//    println(bbb.parseAll(bbb.p2, """aaa"""));//OK
//    println(bbb.parseAll(bbb.p3, """aaa"""))//OK

    class CCC extends RegexParsers {

        def a: Parser[String]    = "a" ^^ { _.toString }
        def aa: Parser[String]    = "aa" ^^ { _.toString }
        def aSa: Parser[String] = (a ~> S) <~ a ^^ { 'a' + _ + 'a' }
        def S: Parser[String] = (aSa | aa) ^^ { _.toString }
    }

    //aSa / aa
    val ccc = new CCC();
    println(ccc.parseAll(ccc.a, """a"""));
    println(ccc.parseAll(ccc.aa, """aa"""));
    println(ccc.parseAll(ccc.S, """aaaaaaaaaa"""));


}
