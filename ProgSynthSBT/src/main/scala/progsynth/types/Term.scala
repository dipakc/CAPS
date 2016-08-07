package progsynth.types
import Types._
import progsynth.methodspecs.InterpretedFns

/** == Term ==
 * <img src="..\..\..\..\..\src\main\resources\doc_resources\TermClassHierarchy.png" alt="TermClassHierarchy.png" />
 *
 *   */
sealed abstract class Term /*extends TermOperators*/
	extends TermDSL with TermUtils with TermChilds with TermPPrinter with PSProgTree with Product with CaseRewritable//
{
	def getType(): PSType
	def defaultValue: Term
}

object Term extends TermUtilsStatic

trait TermUnit extends Term with TermDSLUnit {
	def defaultValue() = ConstUnit("()")
	def getType() = PSUnit
}
trait TermBool extends Term with TermDSLBool with TermBoolUtils{
	def defaultValue() = ConstBool("true")
	def getType() = PSBool
}

object TermBool extends TermUtilsBoolStatic

trait TermInt extends Term with TermDSLInt {
	def defaultValue() = ConstInt("0")
	def getType() = PSInt
}
trait TermReal extends Term with TermDSLReal {
	def defaultValue() = ConstReal("0.0")
	def getType() = PSReal
}
trait TermArrayBool extends Term with TermDSLArrayBool {
	def defaultValue() = ConstArrayBool("Array()")
	def getType() = PSArrayBool
}
trait TermArrayInt extends Term with TermDSLArrayInt {
	def defaultValue() = ConstArrayInt("Array()")
	def getType() = PSArrayInt
}
trait TermArrayReal extends Term with TermDSLArrayReal {
	def defaultValue() = ConstArrayReal("Array()")
	def getType() = PSArrayReal
}

//----------------------------------------------------------------------------------
trait Var extends Term { //TODO: rename t to tpe and v as name
	val v: String

	/**returns the renamed variable. Keeps the type of the variable same*/
	def rename(fn: String => String) = {
	    Var.mkVar(fn(this.v), this.getType)
	}

	def addPrefix(prefix: String) = {
	  	this.rename(prefix + _)
	}
}

object Var {
	def unapply(aVar: Var) : Option[String] = Some(aVar.v)

	def mkVar(v: String, tpe: PSType): Var = tpe match {
		case PSUnit => VarUnit(v)
		case PSBool => VarBool(v)
		case PSInt => VarInt(v)
		case PSReal => VarReal(v)
		case PSArrayBool => VarArrayBool(v)
		case PSArrayInt => VarArrayInt(v)
		case PSArrayReal => VarArrayReal(v)
		case PSAny => throw new RuntimeException("MkVar of PSAny type not supported")
	}
}

case class VarBool(v: String) extends Var with TermBool
case class VarInt(v: String) extends  Var with TermInt {
	def getInt: Int = v.toInt
}

case class VarUnit(v: String) extends  Var with TermUnit
case class VarReal(v: String) extends  Var with TermReal
case class VarArrayInt(v: String) extends  Var with TermArrayInt
case class VarArrayBool(v: String) extends  Var with TermArrayBool
case class VarArrayReal(v: String) extends  Var with TermArrayReal
//----------------------------------------------------------------------------------

trait Const extends Term {
	val name: String
}

object Const {
	def unapply(aConst: Const): Option[String] = Some(aConst.name)

	def mkConst(name: String, tpe: PSType) = tpe match {
		case PSUnit => ConstUnit(name)
		case PSBool => ConstBool(name)
		case PSInt => ConstInt(name)
		case PSReal => ConstReal(name)
		case PSArrayBool => ConstArrayBool(name)
		case PSArrayInt => ConstArrayInt(name)
		case PSArrayReal => ConstArrayReal(name)
		case PSAny => throw new RuntimeException("mkConst for PSAny failed")
	}
}

case class ConstBool(name: String) extends Const with TermBool

case class ConstInt(name: String) extends Const with TermInt

case class ConstUnit(name: String) extends Const with TermUnit

case class ConstReal(name: String) extends Const with TermReal

case class ConstArrayInt(name: String) extends Const with TermArrayInt

case class ConstArrayBool(name: String) extends Const with TermArrayBool

case class ConstArrayReal(name: String) extends Const with TermArrayReal

//----------------------------------------------------------------------------------

trait FnApp extends Term {
	val f: Fn //TODO: rename f as fn
	val ts: List[Term]
}
object FnApp {
	def unapply(aFnApp: FnApp): Option[(Fn, List[Term])] = {
		Some((aFnApp.f, aFnApp.ts))
	}

	def apply(f: Fn, ts: List[Term]) = mkFnApp(f, ts, f.tpe)

	def mkFnApp(f: Fn, ts: List[Term], tpe: PSType) = tpe match {
		case PSUnit => FnAppUnit(f, ts)
		case PSBool => FnAppBool(f, ts)
		case PSInt => FnAppInt(f, ts)
		case PSReal => FnAppReal(f, ts)
		case PSArrayBool => FnAppArrayBool(f, ts)
		case PSArrayInt => FnAppArrayInt(f, ts)
		case PSArrayReal => FnAppArrayReal(f, ts)
		case PSAny => throw new RuntimeException("mkFnApp for PSAny failed")
	}

}

case class FnAppUnit(f: Fn, ts: List[Term]) extends  FnApp with TermUnit

case class FnAppBool(f: Fn, ts: List[Term]) extends FnApp with TermBool

case class FnAppInt(f: Fn, ts: List[Term]) extends  FnApp with TermInt

case class FnAppReal(f: Fn, ts: List[Term]) extends  FnApp with TermReal

case class FnAppArrayBool(f: Fn, ts: List[Term]) extends  FnApp with TermArrayBool

case class FnAppArrayInt(f: Fn, ts: List[Term]) extends  FnApp with TermArrayInt

case class FnAppArrayReal(f: Fn, ts: List[Term]) extends  FnApp with TermArrayReal


//----------------------------------------------------------------------------------

/**Should ArrSelect and Store be normal functions */
/** Constructor for array Select: arr(x) */
trait ArrSelect extends Term {
	val arr: Term
	val index: Term
}

object ArrSelect {
	def unapply(anArrSelect: ArrSelect): Option[(Term, Term)] = {
		Some((anArrSelect.arr, anArrSelect.index))
	}

	def mkArrSelect(arr: Term, index: Term, tpe: PSType) = (arr, index, tpe) match {
		case (arr1: TermArrayBool, index1: TermInt, PSBool) => ArrSelectBool(arr1, index1)
		case (arr1: TermArrayInt, index1: TermInt, PSInt) => ArrSelectInt(arr1, index1)
		case (arr1: TermArrayReal, index1: TermInt, PSReal) => ArrSelectReal(arr1, index1)
		case (arr1: TermBool, _, _) => throw new RuntimeException("unhandled case TermBool in mkArrSelect")
		case (arr1: TermInt, _, _) => throw new RuntimeException("unhandled case TermInt in mkArrSelect")
		case (arr1: TermReal, _, _) => throw new RuntimeException("unhandled case TermReal in mkArrSelect")
		case (arr1: TermUnit, _, _) => throw new RuntimeException("unhandled case TermUnit in mkArrSelect")
		case (a, b, c) =>  throw new RuntimeException("unhandled case in mkArrStore: " + List(a, b, c).map(_.toString).mkString(" ") )
	}
}

case class ArrSelectBool(arr: TermArrayBool, index: TermInt) extends ArrSelect with TermBool

case class ArrSelectInt(arr: TermArrayInt, index: TermInt) extends ArrSelect with TermInt

case class ArrSelectReal(arr: TermArrayReal, index: Term) extends ArrSelect with TermReal

//----------------------------------------------------------------------------------
/** Constructor for array Store: Store(arr, index, value) */
trait ArrStore extends Term {
	val arr: Term
	val index: Term
	val value: Term
}

object ArrStore {
	def unapply(anArrStore: ArrStore): Option[(Term, Term, Term)] = {
		Some((anArrStore.arr, anArrStore.index, anArrStore.value))
	}

	def mkArrStore(arr: Term, index: Term, value: Term, tpe: PSType): ArrStore = (arr, index, value, tpe) match {
		case (arr1: TermArrayBool, index1: TermInt, value1: TermBool, PSArrayBool) => ArrStoreArrayBool(arr1, index1, value1)
		case (arr1: TermArrayInt, index1: TermInt, value1: TermInt, PSArrayInt) => ArrStoreArrayInt(arr1, index1, value1)
		case (arr1: TermArrayReal, index1: TermInt, value1: TermReal, PSArrayReal) => ArrStoreArrayReal(arr1, index1, value1)
		case (arr1: TermBool, _, _, _) => throw new RuntimeException("unhandled case TermBool in mkArrSelect")
		case (arr1: TermInt, _, _, _) => throw new RuntimeException("unhandled case TermInt in mkArrSelect")
		case (arr1: TermReal, _, _, _) => throw new RuntimeException("unhandled case TermReal in mkArrSelect")
		case (arr1: TermUnit, _, _, _) => throw new RuntimeException("unhandled case TermUnit in mkArrSelect")
		case (a, b, c, d) =>  throw new RuntimeException("unhandled case in mkArrStore: " + List(a, b, c, d).map(_.toString).mkString(" ") )
	}

}

case class ArrStoreArrayBool(arr: TermArrayBool, index: TermInt, value: TermBool) extends ArrStore with TermArrayBool

case class ArrStoreArrayInt(arr: TermArrayInt, index: TermInt, value: TermInt) extends ArrStore with TermArrayInt

case class ArrStoreArrayReal(arr: TermArrayReal, index: TermInt, value: TermReal) extends ArrStore with TermArrayReal
//----------------------------------------------------------------------------------
//Quantifiers

trait QTerm extends Term {
	val opr: Fn
	val dummies: List[Var]
	val range: TermBool
	val term: Term
}

object QTerm {
	def unapply(qTerm: QTerm): Option[(Fn, List[Var], TermBool, Term)] = {
		Some(qTerm.opr, qTerm.dummies, qTerm.range, qTerm.term)
	}

	//TODO: why not convert this function to apply.
	def mkQTerm(opr: Fn, dummies: List[Var], range: Term, term: Term): QTerm = (opr, range, term) match {
		case (Fn(_, List(PSInt, PSInt), PSInt), range: TermBool, termInt: TermInt) =>
			QTermInt(opr, dummies, range, termInt)
		case (Fn(_, List(PSBool, PSBool), PSBool), range: TermBool, termBool: TermBool) =>
			QTermBool(opr, dummies, range, termBool)
		case (Fn(_, List(PSReal, PSReal), PSReal), range: TermBool, termReal: TermReal) =>
			QTermReal(opr, dummies, range, termReal)
		case _ =>
			throw new RuntimeException("unhandled case in mkArrStore: " + List(opr, dummies, range, term).map(_.toString).mkString(" ") )
	}

}

case class QTermBool(opr: Fn, dummies: List[Var], range: TermBool, term: TermBool) extends QTerm with TermBool

case class QTermInt(opr: Fn, dummies: List[Var], range: TermBool, term: TermInt) extends QTerm with TermInt

case class QTermReal(opr: Fn, dummies: List[Var], range: TermBool, term: TermReal) extends QTerm with TermReal


case class CountInt(dummies: List[Var], range: TermBool, term: TermBool) //not extended from TermInt

//object CountInt {
//    def unapply(qterm: QTermInt): Option[(List[Var], TermBool, TermBool)] = qterm match {
//        case QTermInt(PlusIntFn, dummies, range, FnAppTermInt(ITEFn, t, 1, 0)) => Some((dummies, range, t))
//        case _ => None
//    }
//}
//----------------------------------------------------------------------------------
trait UnkTerm extends Term {

}

object UnkTerm {
	def unapply(unkTerm: UnkTerm): Option[Int] = {
		Some(0)
	}

	def mkUnkTerm(tpe: PSType): UnkTerm = tpe match {
		case PSInt => UnkTermInt()
		case PSBool => UnkTermBool()
		case PSReal => UnkTermReal()
		case PSUnit => UnkTermUnit()
		case PSArrayInt => UnkTermArrayInt()
		case PSArrayBool => UnkTermArrayBool()
		case PSArrayReal => UnkTermArrayReal()
		case _ =>
			throw new RuntimeException("UnkTerm mkUnkTerm")
	}

	def mkUnkTermBool() = UnkTermBool()

}


case class UnkTermBool() extends UnkTerm with TermBool
case class UnkTermInt() extends UnkTerm with TermInt
case class UnkTermReal() extends UnkTerm with TermReal
case class UnkTermUnit() extends UnkTerm with TermUnit
case class UnkTermArrayInt() extends UnkTerm with TermArrayInt
case class UnkTermArrayReal() extends UnkTerm with TermArrayReal
case class UnkTermArrayBool() extends UnkTerm with TermArrayBool

//----------------------------------------------------------------------------------

 /** == DSL for term operators. ==
  *
  * Functionality overlaps with [[progsynth.types.TermDSL]]
  */
//trait TermOperators { this: Term with TermInt =>
//	def t_<(that: Term) = Atom(Pred("$less", this :: that :: Nil))
//	def t_>(that: Term) = Atom(Pred("$greater", this :: that :: Nil))
//	def t_==(that: Term) = Atom(Pred("$eq$eq", this :: that :: Nil))
//	def t_<=(that: Term) = Atom(Pred("$less$eq", this :: that :: Nil))
//	def t_>=(that: Term) = Atom(Pred("$greater$eq", this :: that :: Nil))
//	def t_!=(that: Term) = Not(Atom(Pred("$eq$eq", this :: that :: Nil)))
//}

/** == DSL for term operators and atoms. ==
  *
  * Functionality overlaps with [[progsynth.types.TermOperators]]
  */
trait TermDSL {self: Term =>
	//TODO: ensure that actual types 'this'  and 'that' are the same
	//== is already defined for Var
	def feqeq(that: Term): FOLFormula = Atom(Pred("$eq$eq", self :: that :: Nil))
	//!= is already defined for Var
	def fneq(that: Term): FOLFormula = Not(Atom(Pred("$eq$eq", self :: that :: Nil)))

	def eqeq(that: Term): TermBool = FnAppBool(InterpretedFns.EqEqBoolFn, List(self, that))

	//!= is already defined for Var
	def neqeq(that: Term): TermBool = FnAppBool(InterpretedFns.NegBoolFn, FnAppBool(InterpretedFns.EqEqBoolFn, List(self, that)) :: Nil)

}

trait TermDSLInt { self: TermInt =>
	import InterpretedFns._

	def f_<(that: TermInt): FOLFormula = Atom(Pred("$less", self :: that :: Nil))
	def f_>(that: TermInt): FOLFormula = Atom(Pred("$greater", self :: that :: Nil))
	def f_<=(that: TermInt): FOLFormula = Atom(Pred("$less$eq", self :: that :: Nil))
	def f_>=(that: TermInt): FOLFormula = Atom(Pred("$greater$eq", self :: that :: Nil))

	def <(that: TermInt): TermBool = FnAppBool(LTBoolFn, List(self, that))
	def >(that: TermInt): TermBool = FnAppBool(GTBoolFn, List(self, that))
	def <=(that: TermInt): TermBool = FnAppBool(LEBoolFn, List(self, that))
	def >=(that: TermInt): TermBool = FnAppBool(GEBoolFn, List(self, that))


	def + (that: TermInt): TermInt = FnAppInt(PlusIntFn, List(self, that))
	def - (that: TermInt): TermInt = FnAppInt(MinusIntFn, List(self, that))
	def * (that: TermInt): TermInt = FnAppInt(TimesIntFn, List(self, that))
	def % (that: TermInt): TermInt = FnAppInt(PercentIntFn, List(self, that))
	def / (that: TermInt): TermInt = FnAppInt(DivIntFn, List(self, that))
	def unary_-(): TermInt = FnAppInt(UnaryMinusIntFn, List(self))
	def min (that: TermInt): TermInt = FnAppInt(MinIntFn, List(self, that))
	def max (that: TermInt): TermInt = FnAppInt(MaxIntFn, List(self, that))
	def pow (that: TermInt): TermInt = FnAppInt(PowIntFn, List(self, that))
}
trait TermDSLUnit { self: TermUnit =>

}

trait TermDSLBool { self: TermBool =>
	def fm(): FOLFormula = {
		Atom(Pred("BoolPred", this :: Nil))
	}

	//Scala precedence :
	// !
	// &&
	// ||
	// impl equiv nequiv
	def && (that: TermBool): TermBool = FnAppBool(InterpretedFns.AndBoolFn, List(self, that))
	def &&& (that: TermBool): TermBool = (this && that).simplify
	def || (that: TermBool): TermBool = FnAppBool(InterpretedFns.OrBoolFn, List(self, that))
	def ||| (that: TermBool): TermBool = (this || that).simplify
	def unary_!(): TermBool = FnAppBool(InterpretedFns.NegBoolFn, List(self))
	def impl (that: Term): TermBool = FnAppBool(InterpretedFns.ImplBoolFn, List(self, that))
	def rimpl (that: Term): TermBool = FnAppBool(InterpretedFns.RImplBoolFn, List(self, that))
	def equiv (that: Term): TermBool =  FnAppBool(InterpretedFns.EquivBoolFn, List(self, that))
	def nequiv(that: Term): TermBool =  !FnAppBool(InterpretedFns.EquivBoolFn, List(self, that))

	import InterpretedFns._
	private val oprList = List(LTBoolFn, GTBoolFn, LEBoolFn, GEBoolFn)

	def chainfn(z: TermInt, foo: (TermInt, TermInt) => TermBool ) = self match {
		case prevTerm @ AndNTermBool(conjuncts) =>
			conjuncts.last match {
				case lastTerm @ FnAppBool(opr, List(x, y: TermInt)) if oprList contains opr=>
					prevTerm && foo(y, z)
				case _ =>  throw new RuntimeException("Chaining operator failed")
			}
		case _ =>
			throw new RuntimeException("Chaining operator failed")
	}

	//To Allow chaining
	def <(z: TermInt): TermBool = chainfn(z, (x, y) => x < y)
	def >(z: TermInt): TermBool = chainfn(z, (x, y) => x > y)
	def <=(z: TermInt): TermBool = chainfn(z, (x, y) => x <= y)
	def >=(z: TermInt): TermBool = chainfn(z, (x, y) => x >= y)
}

trait TermDSLReal { self: TermReal =>

}

trait TermDSLArrayInt { self: TermArrayInt =>
	def select(index: TermInt): TermInt =
		ArrSelectInt(self, index)
	def apply(index: TermInt): TermInt =
		ArrSelectInt(self, index)
	def store(index: TermInt, value: TermInt): TermArrayInt =
		ArrStoreArrayInt(self, index, value)
}

trait TermDSLArrayBool { self: TermArrayBool =>
	def select(index: TermInt): TermBool =
		ArrSelectBool(self, index)
	def apply(index: TermInt): TermBool =
		ArrSelectBool(self, index)
	def store(index: TermInt, value: TermBool): TermArrayBool =
		ArrStoreArrayBool(self, index, value)

}

trait TermDSLArrayReal{ self: TermArrayReal =>

}

