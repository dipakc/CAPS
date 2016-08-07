package progsynth.utils.folformulautils
import progsynth.types._
import progsynth.types.Types._
import progsynth.types.PSType._
object BoolToFormula {
	implicit def stringPair2Var(strtpe: (String, String)) = Var.mkVar(strtpe._1, getPSType(strtpe._2).get)
	implicit def boolean2Atom(b: Boolean) = Atom(Pred("=", List(("x", "Int"), ("x", "Int"))))
	implicit def booleanFn1Atom(f: (_) => Boolean) = Atom(Pred("=", List(("x", "Int"), ("x", "Int"))))
	implicit def booleanFn2Atom(f: (_, _) => Boolean) = Atom(Pred("=", List(("x", "Int"), ("x", "Int"))))
	implicit def booleanFn3Atom(f: (_, _, _) => Boolean) = Atom(Pred("=", List(("x", "Int"), ("x", "Int"))))
	implicit def booleanFn4Atom(f: (_, _, _, _) => Boolean) = Atom(Pred("=", List(("x", "Int"), ("x", "Int"))))
	//def getFormula(fun: (Any => Boolean): FOLFormula = True
	//def not(a: Formula[Pred]) = Not(a)
	def varat[T](variable: T, loc: Int): T = variable
}
