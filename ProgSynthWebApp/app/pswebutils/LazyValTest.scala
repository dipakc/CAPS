package pswebutils

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator.RegexParsers
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

object LazyValLogger {
	val logger= LoggerFactory.getLogger("progsynth.xxx")
}

object LazyValTest extends StandardTokenParsers with PackratParsers with App {
	lexical.delimiters ++= List("+", "*", "-", "/", "(", ")")
	def sum: Parser[Any] = log {
		product ~ "+" ~ sum | product
	}("sum")
	def product: Parser[Any] = log {
		primary ~ "*" ~ product | primary
	}("product")
	def primary: Parser[Any] = log {
		"(" ~ sum ~ ")" | numericLit
	}("primary")

	lazy val plus = "+"
	lazy val x: PackratParser[Any] = log {
		log(plus)("x::= PLUS ~ plus | plus") ~ log(plus)("x::= plus ~ PLUS | plus") | log(plus)("x::= plus ~ plus | PLUS")
	}("x")

	//val abc: ParseResult[Any] = sum(new lexical.Scanner("3*(1+2)"))
	val abc: ParseResult[Any] = x(new lexical.Scanner("+"))
}

object TestApp2 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	def plus0: Parser[Any] = { logger.trace("######"); "+" }
	def plus: PackratParser[Any] = plus0
	def x: PackratParser[Any] = log {
		log(plus)("x::= PLUS ~ plus | plus") ~ log(plus)("x::= plus ~ PLUS | plus") | log(plus)("x::= plus ~ plus | PLUS")
	}("x")

	//val abc: ParseResult[Any] = sum(new lexical.Scanner("3*(1+2)"))
	val res = parseAll(x, "+")

}

object TestApp3 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	lazy val lotsOfOnes: PackratParser[Any] = lotsOfOnes ~ "1" | "1"

	val res = parseAll(lotsOfOnes, "1111")
	logger.trace(res.toString)
}

object TestApp4 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	lazy val lotsOfOnes2: PackratParser[Any] = expr
	lazy val expr: PackratParser[Any] = lotsOfOnes2 ~ "1" | "1"
	val res = parseAll(lotsOfOnes2, "1111")
	logger.trace(res.toString)
}

object TestApp5 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	def plus: PackratParser[Any] = { log("+")("plusP") }
	def x: PackratParser[Any] = log {
		log(plus)("x::= PLUS ~ plus | plus") ~ log(plus)("x::= plus ~ PLUS | plus") | log(plus)("x::= plus ~ plus | PLUS")
	}("x")

	val res = parseAll(x, "+")
}

//Aim: left recursion with context

object TestApp6 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	def toPackrat(x: String): PackratParser[Any] = {
		val xp: Parser[Any] = x
		xp
	}

	var cnt = 0

	lazy val lotsOfOnes: PackratParser[Any] = lotsOfOnes ~ one | one
	lazy val one: PackratParser[Any] = toPackrat("1") ^? { case "1" => cnt = cnt + 1; cnt }
	lazy val test: PackratParser[Any] = lotsOfOnes ~ "2" | lotsOfOnes
	val res = parseAll(test, "1111")
	logger.trace(cnt.toString)
	logger.trace(res.toString)
}

object TestApp7 extends RegexParsers with PackratParsers with JavaTokenParsers with App {
	import LazyValLogger._
	def toPackrat(x: String): PackratParser[Any] = {
		val xp: Parser[Any] = x
		xp
	}

	var cnt = 0

	lazy val lotsOfOnes: PackratParser[Any] = lotsOfOnes ~ one | one
	lazy val one: PackratParser[Any] = toPackrat("1") ^? { case "1" => cnt = cnt + 1; cnt }
	lazy val test: PackratParser[Any] = lotsOfOnes ~ "2" | lotsOfOnes
	val res = parseAll(test, "1111")
	logger.trace(cnt.toString)
	logger.trace(res.toString)
}

import scala.util.parsing.combinator.JavaTokenParsers
object Arith extends JavaTokenParsers with App {
	import LazyValLogger._
	override def log[T](p: => Parser[T])(name: String): Parser[T] = Parser { in =>
		def prt(x: Input) = x.source.toString.drop(x.offset)

		logger.trace("trying " + name + " at " + prt(in))
		val r = p(in)
		logger.trace(name + " --> " + r + " next: " + prt(r.next))
		r
	}
	def expr: Parser[Any] = log(term ~ rep("+" ~ term | "-" ~ term))("expr")
	def term: Parser[Any] = factor ~ rep("*" ~ factor | "/" ~ factor)
	def factor: Parser[Any] = floatingPointNumber | "(" ~ expr ~ ")"
	logger.trace(parseAll(expr, "(3+7)*2").toString)
}

