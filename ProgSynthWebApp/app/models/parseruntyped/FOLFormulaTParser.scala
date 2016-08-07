package models.parseruntyped

import models.parseruntyped.SymbolMapper
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.RegexParsers
import progsynth.methodspecs.InterpretedFns._
import Types.FOLFormulaT

trait FOLFormulaTParsers extends PredTParsers {
	lazy val negTP: Parser[FOLFormulaT] =
		prefixP(sym("""~"""))(folFormulaTP) ^? {
			case formula => NotT(formula)
		}

	lazy val existsFTP: Parser[List[FOLFormulaT => FOLFormulaT]] =
		sym("""\exists""") ~> rep1(varTP) ^^ { varList =>
			varList map { bVar =>
				{ formula: FOLFormulaT => ExistsT(bVar, formula)}
			}
	}

	lazy val forallFTP: Parser[List[FOLFormulaT => FOLFormulaT]] =
		sym("""\forall""") ~> rep1(varTP) ^^ { varList =>
			varList map { bVar =>
				{ formula: FOLFormulaT => ForallT(bVar, formula) }
			}
	}

	lazy val quantifiedFTP: Parser[FOLFormulaT] =
		rep1(forallFTP | existsFTP) ~ prefixP(sym(":"))(folFormulaTP) ^? {
			case qfnLists ~ bodyF =>
				qfnLists.flatten.foldRight(bodyF) { (fn, newBody) => fn(newBody) }
		}

	lazy val unitFOLFormulaTP: Parser[FOLFormulaT] =
		quantifiedFTP | predFTP | negTP | parenP(folFormulaTP)

	lazy val predFTP = predTP ^^ {preds =>
		val x: List[FOLFormulaT] = preds.map(p => AtomT(p))
		x.reduceLeft((a, b) => AndT(a, b))
	}

	lazy val levelfT0 =
		sym("""\equiv""") ^^^ { (a: FOLFormulaT, b: FOLFormulaT) => IffT(a, b)}

	lazy val levelfT1 =
		sym("""\impl""") ^^^ { (a: FOLFormulaT, b: FOLFormulaT) => ImplT(a, b)}

	lazy val levelfT2 =
		sym("""\/""") ^^^ { (a: FOLFormulaT, b: FOLFormulaT) => OrT(a, b)}

	lazy val levelfT3 =
		sym("""/\""") ^^^ { (a: FOLFormulaT, b: FOLFormulaT) => AndT(a, b)}

	lazy val folFormulaTP: Parser[FOLFormulaT] =
		unitFOLFormulaTP * levelfT3 * levelfT2 * levelfT1 * levelfT0

}


