package models.parseruntyped

import progsynth.types._
import models.parseruntyped.SymbolMapper
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.RegexParsers
import progsynth.methodspecs.InterpretedFns._

trait TermTParsers extends SymbolMapper with PSWebParserUtils
				   with RegexParsers with PackratParsers with JavaTokenParsers {
	//------------------------------------------------------------------

	// Do not "|" to CFG parsers if they are inputs which can be recognized by both.
	// docs/Parsers.html#orcfg
	//lazy val termTP = termArrayTP | termBasicTP

	/**Basic means non-array */
	lazy val termBasicTP: Parser[TermT] =
		unitTermTP * maxMinP * timesP * plusP *
			intIntBoolOpr * //TODO: test the elevation of intIntBoolOpr
				level3 * level2 * level1 * level0 * level1  //TODO: why level1 is repeated

	lazy val unitTermTP: Parser[TermT] =
		countTermTP | qTermTP | arrSelectTP | constTP | varTP | parenP(termBasicTP) | unaryMinusP | notP

	lazy val countTermTP: Parser[CountTermT] = {
		parenP((sym("""\Count""") ~> rep1sep(varTP, ",")) ~ (sym(":") ~> opt(termBasicTP)) ~ prefixP(sym(":"))(termBasicTP)) ^? {
			case varList ~ rangeOpt ~ term => rangeOpt match {
				case Some(range) => CountTermT(varList, range, term)
				case None => CountTermT(varList, TrueT, term)
			}
		}
	}

	lazy val qTermTP: Parser[QTermT] = {
		parenP(quantTP ~ rep1sep(varTP, ",") ~ (sym(":") ~> opt(termBasicTP)) ~ prefixP(sym(":"))(termBasicTP)) ^? {
			case quantFn ~ varList ~ rangeOpt ~ term => rangeOpt match {
				case Some(range) => quantFn(varList, range, term)
				case None => quantFn(varList, TrueT, term)
			}
		}
	}

	lazy val quantTP: Parser[(List[VarT], TermT, TermT)=> QTermT] = forallTP | existsTP | bigSumTP | bigMaxTP

	lazy val forallTP: Parser[(List[VarT], TermT, TermT)=> QTermT] = sym("""\forall""") ^^^ {
		(dummies: List[VarT], range: TermT, term: TermT) => QTermT(AndBoolFn, dummies, range, term)
	}

	lazy val existsTP: Parser[(List[VarT], TermT, TermT)=> QTermT] = sym("""\exists""") ^^^ {
		(dummies: List[VarT], range: TermT, term: TermT) => QTermT(OrBoolFn, dummies, range, term)
	}

	lazy val bigSumTP: Parser[(List[VarT], TermT, TermT)=> QTermT] = sym("""\Sum""") ^^^ {
		(dummies: List[VarT], range: TermT, term: TermT) => QTermT(PlusIntFn, dummies, range, term)
	}

	lazy val bigMaxTP: Parser[(List[VarT], TermT, TermT)=> QTermT] = sym("""\Max""") ^^^ {
		(dummies: List[VarT], range: TermT, term: TermT) => QTermT(MaxIntFn, dummies, range, term)
	}

	lazy val arrSelectTP: Parser[ArrSelectT] = {
		termArrayTP ~ sqParenP(termBasicTP) ^? {
		case arrName ~ indexTerm => ArrSelectT(arrName, indexTerm)
		}
	}


	lazy val constBoolP: Parser[ConstT] =
		("false" ^^^ { ConstT("false", PBool) }
		| "true" ^^^ { ConstT("true", PBool)})

	lazy val constIntP: Parser[ConstT] = wholeNumber ^^ { s => ConstT(s, PInt) }

	lazy val constTP: Parser[ConstT] = constIntP | constBoolP

	lazy val varTP: Parser[VarT] =
		 metaVarTP | basicVarTP

	lazy val basicVarTP: Parser[VarT] =
	    ident ^^ { s => VarT(s)}

	lazy val metaVarTP: Parser[VarT] =
	    ident ~ "'" ^^ { p => VarT(p._1 + p._2)}

	lazy val unaryMinusP: Parser[TermT] =
		sym("-") ~> unitTermTP ^^ { aTerm: TermT =>
			FnAppT(UnaryMinusIntFn, aTerm::Nil)
		}

	lazy val notP: Parser[TermT] = sym("~") ~> unitTermTP ^^ { aTerm: TermT => //Changed symbol from ! to ~.
		FnAppT(NegBoolFn, aTerm::Nil)
	}

	lazy val maxMinP =
	    ( sym("max") ^^^ { (a: TermT, b: TermT) => FnAppT( MaxIntFn, a :: b :: Nil) }
	      | sym("min") ^^^ { (a: TermT, b: TermT) => FnAppT( MinIntFn, a :: b :: Nil) }
	    )

	lazy val timesP =
			( sym("*") ^^^ { (a: TermT, b: TermT) => FnAppT( TimesIntFn, a :: b :: Nil) }
			| sym("/") ^^^ { (a: TermT, b: TermT) => FnAppT( DivIntFn, a :: b :: Nil) }
			| sym("%") ^^^ { (a: TermT, b: TermT) => FnAppT( PercentIntFn, a :: b :: Nil) })

	lazy val plusP =
		( sym("+") ^^^ { (a: TermT, b: TermT) => FnAppT( PlusIntFn, a :: b :: Nil) }
		| sym("-") ^^^ { (a: TermT, b: TermT) => FnAppT( MinusIntFn, a :: b :: Nil) })

	lazy val level0: Parser[(TermT, TermT)=>TermT] =
		sym("""\equiv""") ^^^ { (a: TermT, b: TermT) => FnAppT( EquivBoolFn, a :: b :: Nil) }
	lazy val level1: Parser[(TermT, TermT)=>TermT]=
		sym("""\impl""") ^^^ { (a: TermT, b: TermT) => FnAppT( ImplBoolFn, a :: b :: Nil) } //TODO: implement RImplBoolFn
	lazy val level2: Parser[(TermT, TermT)=>TermT]=
		sym("""\/""") ^^^ { (a: TermT, b: TermT) => FnAppT( OrBoolFn, a :: b :: Nil) }
	lazy val level3: Parser[(TermT, TermT)=>TermT] =
		sym("""/\""") ^^^ { (a: TermT, b: TermT) => FnAppT( AndBoolFn, a :: b :: Nil) }

	lazy val intIntBoolOpr = (
		  sym("<=") ^^^ { (a: TermT, b: TermT) => FnAppT( LEBoolFn, a :: b :: Nil) }
		| sym("<") ^^^ { (a: TermT, b: TermT) => FnAppT( LTBoolFn, a :: b :: Nil) }
		| sym(">=") ^^^ { (a: TermT, b: TermT) => FnAppT( GEBoolFn, a :: b :: Nil) }
		| sym(">") ^^^ { (a: TermT, b: TermT) => FnAppT( GTBoolFn, a :: b :: Nil) }
		| sym("eqeq") ^^^ { (a: TermT, b: TermT) => FnAppT( EqEqBoolFn, a :: b :: Nil) }
	)

	//----------------------------------------------------------------------------------

	lazy val termArrayTP: PackratParser[TermArrayT] =
		arrStoreArrayTP | constArrayTP | varArrayTP | parenP(termArrayTP)

	//Why is arr store parenthesized? docs/Parsers.html/#PrefixParser
	lazy val arrStoreArrayTP: PackratParser[ArrStoreArrayT] = {
		parenP(termArrayTP ~ sqParenP(termBasicTP) ~ (sym(":=") ~> termBasicTP)) ^? {
			case arr ~ index ~ value => ArrStoreArrayT(arr, index, value)
		}
	}

	lazy val constArrayTP: Parser[ConstArrayT] = constArrayIntTP | constArrayBoolTP

	lazy val constArrayIntTP: Parser[ConstArrayT] =
		//when using inner parser like constTP, also add the paren parser
		("Array" <~ sym("(")) ~> repsep(constIntP | parenP(constIntP), ",") <~ sym(")")  ^^ {
			termList: List[ConstT] =>
				val argList = termList.map(_.name).mkString(", ")
				ConstArrayT("Array(" + argList + ")", PArrayInt)
		}

	lazy val constArrayBoolTP: Parser[ConstArrayT] =
		//when using inner parser like constTP, also add the paren parser
		("Array" <~ sym("(")) ~> repsep(constBoolP | parenP(constBoolP), ",") <~ sym(")")  ^^ {
			termList: List[ConstT] =>
				val argList = termList.map(_.name).mkString(", ")
				ConstArrayT("Array(" + argList + ")", PArrayBool)
		}

	lazy val varArrayTP: Parser[VarArrayT] =
		ident ^^ { s => VarArrayT (s) }
	//------------------------------------------------------------------------------------
	lazy val newVarTP: Parser[NewVarT] =
		(ident <~ ":") ~ ident  ^? { case name ~ tpe => NewVarT(name, tpe) }

}

