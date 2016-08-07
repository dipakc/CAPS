package models.parseruntyped

import progsynth.types._
import progsynth.types.Types._
import models.parseruntyped.SymbolMapper
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.RegexParsers
import progsynth.methodspecs.InterpretedFns._
//

trait PredTParsers extends TermTParsers {
	lazy val intOprP = (
		  sym("<=") ^^^ { (a: TermT, b: TermT) => PredT("$less$eq", a :: b :: Nil) }
		| sym("<") ^^^ { (a: TermT, b: TermT) => PredT("$less", a :: b :: Nil) }
		| sym(">=") ^^^ { (a: TermT, b: TermT) => PredT("$greater$eq", a :: b :: Nil) }
		| sym(">") ^^^ { (a: TermT, b: TermT) => PredT("$greater", a :: b :: Nil) }
		| sym("==") ^^^ { (a: TermT, b: TermT) => PredT("$eq$eq", a :: b :: Nil) }
	)

	lazy val predTP: Parser[List[PredT]] = intPredTP | boolPredTP

	lazy val intPredTP: Parser[List[PredT]] =  rep1(termBasicTP ~ intOprP) ~ termBasicTP ^? {
		case list ~ lastTerm =>
			var predTList: List[PredT] = list.iterator.sliding(2).withPartial(false).toList.map {
				case (term1 ~ opr1) :: (term2 ~ _) :: Nil => opr1(term1, term2)
			}
			val term ~ opr = list.last
			predTList = predTList ++ List(opr(term, lastTerm))
			predTList
	}

	lazy val boolPredTP: Parser[List[PredT]] =
		termBasicTP ^^ { termT => List(PredT("BoolPred", termT :: Nil))}
	//---------------------------------------------------------------------------------
}


