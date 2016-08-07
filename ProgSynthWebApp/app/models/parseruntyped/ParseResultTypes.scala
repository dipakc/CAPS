package models.parseruntyped

import progsynth.types.Fn

abstract class TermT {}

/**
 * Type detected by CFG parser.
 * As of now, only constants and arrayconst types are detected by CFG parser.
 */
abstract class PType
case object PInt extends PType
case object PReal extends PType
case object PBool extends PType
case object PUnit extends PType
case object PArrayInt extends PType
case object PArrayBool extends PType
case object PArrayReal extends PType
case object PUnk extends PType

case class VarT(v: String) extends TermT
case class ConstT(name: String, tpe: PType = PUnk) extends TermT
case class FnAppT(f: Fn, ts: List[TermT]) extends TermT
case class ArrSelectT(arr: TermArrayT, index: TermT) extends TermT
case class QTermT(opr: Fn, dummies: List[VarT], range: TermT, term: TermT) extends TermT
case class CountTermT(dummies: List[VarT], range: TermT, term: TermT) extends TermT

case class NewVarT(v: String, tpe: String) extends TermT

trait TermArrayT extends TermT
object TrueT extends TermT
object FalseT extends TermT

case class VarArrayT(v: String) extends TermArrayT
case class ConstArrayT(name: String, tpe: PType = PUnk) extends TermArrayT
case class FnAppArrayT(f: Fn, ts: List[TermT]) extends TermArrayT
case class ArrStoreArrayT(arr: TermArrayT, index: TermT, value: TermT) extends TermArrayT

case class PredT(r: String, ts: List[TermT])

abstract class FormulaT[T]{}

object Types {
	type FOLFormulaT = FormulaT[PredT]
}

case class True1T[T]() extends FormulaT[T]
case class False1T[T]() extends FormulaT[T]
case class AtomT[T](a: T) extends FormulaT[T]
case class NotT[T](f: FormulaT[T]) extends FormulaT[T]
case class AndT[T](f1: FormulaT[T], f2: FormulaT[T]) extends FormulaT[T]
case class OrT[T](f1: FormulaT[T], f2: FormulaT[T]) extends FormulaT[T]
case class ImplT[T](f1: FormulaT[T], f2: FormulaT[T]) extends FormulaT[T]
case class IffT[T](f1: FormulaT[T], f2: FormulaT[T]) extends FormulaT[T]
case class ForallT[T](v: VarT, f: FormulaT[T]) extends FormulaT[T]
case class ExistsT[T](v: VarT, f: FormulaT[T]) extends FormulaT[T]
case class UnknownT[T]() extends FormulaT[T]
