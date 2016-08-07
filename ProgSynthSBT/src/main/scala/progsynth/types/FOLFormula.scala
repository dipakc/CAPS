package progsynth.types
import Types._
import scala.collection.mutable.LinkedHashMap

//TODO_refactor: FOLFormula type has been moved to package.scala due to compilation error.

//trait FOLFormulaTrait {
//	type FOLFormula = Formula[Pred]
//	val True = True1[Pred]()
//	val False = False1[Pred]()
//}


object FOLFormula extends FOLFormulaStaticUtils {

	//Moving this method to FOLFormulaStaticUtils causes the SBT builder to crash. (in eclipse)
	//TODO: Add this method in the Formula[Pred] class
	/**Returns scala code(String) that constructs the formula. */
	def toCode(formula: FOLFormula): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(formula, ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	//Moving method to FOLFormulaStaticUtils causes the SBT builder to crash. (in eclipse)
	//TODO: Add this method in the Formula[Pred] class
	/**Returns scala  code(String) that constructs the formula
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the formula is returned */
	def toCode(formula: FOLFormula, ctxMap: LinkedHashMap[String, String],
		introduceVar: Boolean = true): String = formula match {
		case True1() =>
			val codeRhs = <a>True1()</a>.text
			val seed = Some("true")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case False1() =>
			val codeRhs = <a>False1()</a>.text
			val seed = Some("false")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Atom(pred) =>
			val predCode = pred.toCode(ctxMap, false)
			val codeRhs = <a>Atom({ predCode })</a>.text
			val seed = Some("atm")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Not(f) =>
			val fCode = FOLFormula.toCode(f, ctxMap)
			//--------
			val codeRhs = <a>Not({fCode})</a>.text
			val seed = Some("not")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case And(f1, f2) =>
			val f1code = FOLFormula.toCode(f1, ctxMap)
			val f2code = FOLFormula.toCode(f2, ctxMap)
			//--------
			val codeRhs = <a>And({ f1code }, { f2code })</a>.text
			val seed = Some("and")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Or(f1, f2) =>
			val f1code = FOLFormula.toCode(f1, ctxMap)
			val f2code = FOLFormula.toCode(f2, ctxMap)
			//--------
			val codeRhs = <a>Or({ f1code }, { f2code })</a>.text
			val seed = Some("or")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Impl(f1, f2) =>
			val f1code = FOLFormula.toCode(f1, ctxMap)
			val f2code = FOLFormula.toCode(f2, ctxMap)
			//--------
			val codeRhs = <a>Impl({ f1code }, { f2code })</a>.text
			val seed = Some("impl")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Iff(f1, f2) =>
			val f1code = FOLFormula.toCode(f1, ctxMap)
			val f2code = FOLFormula.toCode(f2, ctxMap)
			//--------
			val codeRhs = <a>Iff({ f1code }, { f2code })</a>.text
			val seed = Some("iff")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Forall(v, f) =>
			val fCode = FOLFormula.toCode(f, ctxMap)
			//--------
			val codeRhs = <a>Forall("{v}", { fCode })</a>.text
			val seed = Some("forall")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Exists(v, f) =>
			val fCode = FOLFormula.toCode(f, ctxMap)
			//--------
			val codeRhs = <a>Exists("{v}", { fCode })</a>.text
			val seed = Some("exists")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
		case Unknown() =>
			val codeRhs = <a>Unknown[Pred]()</a>.text
			val seed = Some("unk")
			GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}
	//TODO: move to companion objects (overloaded apply methods)
}
