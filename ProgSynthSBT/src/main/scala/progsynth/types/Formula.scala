package progsynth.types

object Formula extends FormulaStaticUtils

//case class FOLFormulaQ()

sealed abstract class Formula[T]	extends		FormulaApplyRec[T]
									with 		FormulaMonad[T]
									with 		FormulaOperators[T]
									with 		FormulaUtils[T]
									with        FormulaChilds[T]
									with 		FormulaFId[T]
									with 		FormulaManipulations[T]
									with 		PSProgTree
									with 		Product
									with 		CaseRewritable
									/*with 		FormulaToString[T]*/{
	def isUnknown() = false
	def unary_!() = Not(this)
}

case class True1[T]() extends Formula[T]
case class False1[T]() extends Formula[T]
case class Atom[T](a: T) extends Formula[T]
case class Not[T](f: Formula[T]) extends Formula[T]
case class And[T](f1: Formula[T], f2: Formula[T]) extends Formula[T]
case class Or[T](f1: Formula[T], f2: Formula[T]) extends Formula[T]
case class Impl[T](f1: Formula[T], f2: Formula[T]) extends Formula[T]
case class Iff[T](f1: Formula[T], f2: Formula[T]) extends Formula[T]
case class Forall[T](v: Var, f: Formula[T]) extends Formula[T]
case class Exists[T](v: Var, f: Formula[T]) extends Formula[T]
case class Unknown[T]() extends Formula[T] {
	val uid = Unknown.getId()
	override def isUnknown() = true
}

object Unknown extends UnknownIdCtr {
	var cnt: Int = 0
	def getId() = { cnt = cnt + 1; cnt }
}

object True1 extends True1IdCtr
object False1 extends False1IdCtr
object Atom extends AtomIdCtr
object Not extends NotIdCtr
object And extends AndIdCtr
object Or extends OrIdCtr
object Impl extends ImplIdCtr
object Iff extends IffIdCtr
object Forall extends ForallIdCtr
object Exists extends ExistsIdCtr

//TODO_refactor: remove Test object
//object Test {
//	import progsynth.utils.folformulautils.BoolToFormula._
//	//import Formula._
//	def forall(f: Any => Formula[Pred]): Formula[Pred] = f(0)
//	def not(f: Formula[Pred]) = Not(f)
//	def test() = {
//		//todo: Formula[Fol]. How to create new type/class for this
//		val aFolFormula: Formula[Pred] = Atom(Pred("pred", Nil))
//		val x = 4.0
//		val y = 6.0
//		val z = 8.0
//		//val i1 = And(x < y, y == z)
//		//val i2 = forall(w => x < y and  y == z or x == z)
//		//val i3 = And(i1, i2)
//		val max = 4
//		//val i4 = ((x <= y) ==> (max == y)) \/ ((x >= y) ==> (max == x))
//		//val i5 = ((x <= y) impl (max == y)) or ((x >= y) impl (max == x))
//		//val i6 = (x <= y ==> max == y) \/ (x >= y ==> max == x)
//
//		//Tip: Use scala parser in scala.
//		//Tip: Lazy evaluation
//		//Tip: Scala meta programming
//		//Tip: Use strings instead of variable names
//		// "String parsing" vs "scala objects" for formula
//		// Do I need to pass the formula library via the plugin in case of "scala objects for formula"
//		//This is what I want
//		//val i1 = not(x < y) and max = x
//		//sAssert(i1)
//		//OR
//		//sAssert(name = "i1", value = not(x < y) and max = x)
//		//Atom can be any boolean function in some particular package
//		//Atom can be an obect of a Pred class. There will implicit defs for converting known boolean functions to Pred.
//		//Problem: how to specify a function that accepts a variable but does accept expression.
//	}
//}
//
//// Formula[T] ::= True[T] | False[T] | Atom (a: T) |
////				  Not(f) | And(f1, f2) | Or(f1, f2) | Iff(f1, f2) | Impl(f1, f2) |
////				  Forall(v: String, f) | Exists(v:String, f)
////
//// Pred(r: String, ts: List[Term])
//// Term ::= Var(v: String, t: String) | FnApp(f:String, ts: List[Term])
