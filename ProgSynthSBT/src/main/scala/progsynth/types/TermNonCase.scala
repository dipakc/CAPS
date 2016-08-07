package progsynth.types

import progsynth.methodspecs.InterpretedFns
import org.kiama.rewriting.Rewritable
import org.kiama.rewriting.Rewriter
/**
 * == Term ==
 */
/**This file is kept for reference. Just in case we want to implement Term classes and non-case classes.*/
/*
sealed abstract class Term extends TermOperators
	with TermUtils with TermChilds with TermPPrinter with TermDSL with PSProgTree with Rewritable //
	{
	def getType(): PSType
}
//////////////////////////////////////////////////////////////////////////
class Var(val v: String, val t: PSType) extends Term { //TODO: rename t to tpe and v as name with Equals { with Equals {
	if (t == "") throw new RuntimeException();
	override def getType() = t

	def canEqual(other: Any) = {
		other.isInstanceOf[progsynth.types.Var]
	}

	override def equals(other: Any) = {
		other match {
			case that: progsynth.types.Var =>
				/*Var.super.equals(that) && */that.canEqual(Var.this) &&
				v == that.v && t == that.t
			case _ => false
		}
	}

	override def hashCode() = {
		val prime = 41
		List(/*Var.super.hashCode, */v.hashCode, t.hashCode)
		.reduceLeft { prime * _ + _ }
	}

	override def arity = 2
	override def deconstruct: Seq[Rewriter.Term] = v :: t :: Nil
	override def reconstruct(arr: Array[Any]): Var = arr match {
		case Array(v: String, t: PSType) => new Var(v, t)
	}

	override def toString() = <a>Var({v.toString},{t.toString})</a>.text
}

object Var {
	def unapply(aVar: Var): Option[(String, PSType)] = {
		val retTup = (aVar.v, aVar.t)
		Some(retTup)
	}

	def apply(v: String, t: PSType): Var = {
		new Var(v, t)
	}
}
//////////////////////////////////////////////////////////////////////////
class FnApp(val f: Fn, val ts: List[Term]) extends Term { //TODO: rename f as fn
	override def getType() = f.tpe

	def canEqual(other: Any) = {
		other.isInstanceOf[progsynth.types.FnApp]
	}
	
	override def equals(other: Any) = {
		other match {
			case that: progsynth.types.FnApp =>
				/*FnApp.super.equals(that) && */that.canEqual(FnApp.this) &&
				f == that.f && ts == that.ts
			case _ => false
		}
	}

	override def hashCode() = {
		val prime = 41
		List(/*FnApp.super.hashCode, */f.hashCode, ts.hashCode)
		.reduceLeft { prime * _ + _ }
	}

	override def arity = 2
	override def deconstruct: Seq[Rewriter.Term] = f :: ts :: Nil
	override def reconstruct(arr: Array[Any]): FnApp = arr match {
		case Array(f: Fn, ts: List[Term]) => new FnApp(f, ts)
	}
	
	override def toString() = <a>FnApp({f.toString},{ts.toString})</a>.text
}

object FnApp {
	def unapply(aFnApp: FnApp): Option[(Fn, List[Term])] = {
		val retTup = (aFnApp.f, aFnApp.ts)
		Some(retTup)
	}

	def apply(f: Fn, ts: List[Term]): FnApp = {
		new FnApp(f, ts)
	}
}
//////////////////////////////////////////////////////////////////////////
/**Should ArrSelect and Store be normal functions */
/** Constructor for array Select: arr(x) */
class ArrSelect(val arr: Term, val index: Term) extends Term {
	if (arr.getType() != PSArrayInt && index.getType == PSInt)
		throw new RuntimeException("Select constructor used with non array parameter")
	override def getType() = PSInt //Only Int Arrays are supported

	def canEqual(other: Any) = {
		other.isInstanceOf[progsynth.types.ArrSelect]
	}
	
	override def equals(other: Any) = {
		other match {
			case that: progsynth.types.ArrSelect =>
				/*ArrSelect.super.equals(that) && */that.canEqual(ArrSelect.this) &&
				arr == that.arr && index == that.index
			case _ => false
		}
	}
	
	override def hashCode() = {
		val prime = 41
		val hashCodes = List(/*ArrSelect.super.hashCode, */arr.hashCode, index.hashCode)
		hashCodes reduceLeft { prime * _ + _ }
	}

	override def arity = 2
	override def deconstruct: Seq[Rewriter.Term] = arr :: index :: Nil
	override def reconstruct(arr: Array[Any]): ArrSelect = arr match {
		case Array(arr: Term, index: Term) => new ArrSelect(arr, index)
	}
	
	override def toString() = <a>ArrSelect({arr.toString},{index.toString})</a>.text
}

object ArrSelect {
	def unapply(arrSelect: ArrSelect): Option[(Term, Term)] = {
		val retTup = (arrSelect.arr, arrSelect.index)
		Some(retTup)
	}

	def apply(arr: Term, index: Term): ArrSelect = {
		new ArrSelect(arr, index)
	}
}
//////////////////////////////////////////////////////////////////////////

/** Constructor for array Store: Store(arr, index, value) */
class ArrStore(val arr: Term, val index: Term, val value: Term) extends Term {
	if (arr.getType() != PSArrayInt && index.getType == PSInt && value.getType == PSInt)
		throw new RuntimeException("Select constructor used with non array parameter")
	override def getType() = PSArrayInt //Only Int Arrays are supported

	def canEqual(other: Any) = {
		other.isInstanceOf[progsynth.types.ArrStore]
	}
	
	override def equals(other: Any) = {
		other match {
			case that: progsynth.types.ArrStore =>
				/*ArrStore.super.equals(that) && */that.canEqual(ArrStore.this) &&
				arr == that.arr && index == that.index && value == that.value
			case _ => false
		}
	}

	override def hashCode() = {
		val prime = 41
		val hashCodes = List(/*ArrStore.super.hashCode, */arr.hashCode, index.hashCode, value.hashCode)
		hashCodes reduceLeft { prime * _ + _ }
	}

	override def arity = 2
	override def deconstruct: Seq[Rewriter.Term] = arr :: index :: value :: Nil
	override def reconstruct(arr: Array[Any]): ArrStore = arr match {
		case Array(arr: Term, index: Term, value: Term) => new ArrStore(arr, index, value)
	}
	override def toString() = <a>ArrStore({arr.toString},{index.toString},{value.toString})</a>.text
}

object ArrStore {
	def unapply(arrStore: ArrStore): Option[(Term, Term, Term)] = {
		val retTup = (arrStore.arr, arrStore.index, arrStore.value)
		Some(retTup)
	}

	def apply(arr: Term, index: Term, value: Term): ArrStore = {
		new ArrStore(arr, index, value)
	}
}

//////////////////////////////////////////////////////////////////////////
class Const(val name: String, val tpe: PSType) extends Term {
	override def getType() = tpe

	def canEqual(other: Any) = {
		other.isInstanceOf[progsynth.types.Const]
	}

	override def equals(other: Any) = {
		other match {
			case that: progsynth.types.Const =>
				/*Const.super.equals(that) && */that.canEqual(Const.this) &&
				name == that.name && tpe == that.tpe
			case _ => false
		}
	}

	override def hashCode() = {
		val prime = 41
		List(/*Const.super.hashCode, */name.hashCode, tpe.hashCode)
		.reduceLeft { prime * _ + _ }
	}

	override def arity = 2
	override def deconstruct: Seq[Rewriter.Term] = name :: tpe :: Nil
	override def reconstruct(arr: Array[Any]): Const = arr match {
		case Array(name: String, tpe: PSType) => new Const(name, tpe)
	}
	override def toString() = <a>Const({name.toString},{tpe.toString})</a>.text
}

object Const {
	def unapply(aConst: Const): Option[(String, PSType)] = {
		val retTup = (aConst.name, aConst.tpe)
		Some(retTup)
	}

	def apply(name: String, tpe: PSType): Const = {
		new Const(name, tpe)
	}
}
//////////////////////////////////////////////////////////////////////////
/**
 * == DSL for term operators. ==
 *
 * Functionality overlaps with [[progsynth.types.TermDSL]]
 */
trait TermOperators { this: Term =>
	def t_<(that: Term) = Atom(Pred("$less", this :: that :: Nil))
	def t_>(that: Term) = Atom(Pred("$greater", this :: that :: Nil))
	def t_==(that: Term) = Atom(Pred("$eq$eq", this :: that :: Nil))
	def t_<=(that: Term) = Atom(Pred("$less$eq", this :: that :: Nil))
	def t_>=(that: Term) = Atom(Pred("$greater$eq", this :: that :: Nil))
	def t_!=(that: Term) = Not(Atom(Pred("$eq$eq", this :: that :: Nil)))
}

/**
 * == DSL for term operators and atoms. ==
 *
 * Functionality overlaps with [[progsynth.types.TermOperators]]
 */

trait TermDSL { self: Term =>
	def <(that: Term): FOLFormula = Atom(Pred("$less", self :: that :: Nil))
	def >(that: Term): FOLFormula = Atom(Pred("$greater", self :: that :: Nil))
	def eqeq(that: Term): FOLFormula = Atom(Pred("$eq$eq", self :: that :: Nil)) //== is already defined for Var
	def <=(that: Term): FOLFormula = Atom(Pred("$less$eq", self :: that :: Nil))
	def >=(that: Term): FOLFormula = Atom(Pred("$greater$eq", self :: that :: Nil))
	def neq(that: Term): FOLFormula = Not(Atom(Pred("$eq$eq", self :: that :: Nil))) //!= is already defined for Var

	def +(that: Term): Term = FnApp(InterpretedFns.plusInt(), List(self, that))
	def -(that: Term): Term = FnApp(InterpretedFns.minusInt(), List(self, that))
	def *(that: Term): Term = FnApp(InterpretedFns.timesInt(), List(self, that))
	def %(that: Term): Term = FnApp(InterpretedFns.percentInt(), List(self, that))
	def /(that: Term): Term = FnApp(InterpretedFns.divInt(), List(self, that))
	def unary_-(): Term = FnApp(InterpretedFns.unaryMinusInt(), List(self))
	def select(index: Term): Term = ArrSelect(self, index)
	def store(index: Term, value: Term): Term = ArrStore(self, index, value)
}

object TermTestObj {
	def main(args: Array[String]) {
		val arr = VarArrayInt("arr")
		val c2 = ConstInt("2")
		val c100 = ConstInt("100")
		//Store(arr, 2, 100)
		val t = arr.store(c2, c100)
		val areEqual = (t == ArrStore(arr, c2, c100))
		println(areEqual)
	}
}
*/