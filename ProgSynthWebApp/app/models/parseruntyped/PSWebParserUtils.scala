package models.parseruntyped

import scala.util.parsing.combinator.Parsers
import models.parseruntyped.SymbolMapper
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.JavaTokenParsers

trait PSWebParserUtils extends SymbolMapper /*with RegexParsers */with JavaTokenParsers{

	/**Given:  p: Parser[T] and f: T => Option[U]
	* Result: parser of type Parser[U]
	* Succeeds when p succeeds and f returns non-None on the result of p. */
	def strengthen[T, U](p: Parser[T], f: T => Option[U], failureMsg: String): Parser[U] = {
		p >> { r1 =>
			val r2Opt = f(r1)
			r2Opt match {
				case Some(r2) => success(r2)
				case None =>  failure(failureMsg + r1)
			}
		}
	}

	def sqParenP[T](p: Parser[T]): Parser[T] = sym("[") ~> p <~ sym("]")

	def parenP[T](p: Parser[T]): Parser[T] = sym("(") ~> p <~ sym(")")

	def prefixP[T](prefix: String)(p: Parser[T]) =  prefix ~> p
}