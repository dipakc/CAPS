package progsynth.dsl
import progsynth.types._
import progsynth.types.Types._

import progsynth.methodspecs.InterpretedFns

class FOLFormulaDSL(aFOLFormula: FOLFormula) {
	def f_/\(f2: FOLFormula) = And(aFOLFormula, f2)
	def f_\/(f2: FOLFormula) = Or(aFOLFormula, f2)
	def f_==>(f2: FOLFormula) = Impl(aFOLFormula, f2)
	def f_=== (f2: FOLFormula) = Iff(aFOLFormula, f2)
	//def ∧ = /\ _
	//def ∨ = \/ _
	//def → = ==> _
	//def ⇔ = === _
	//def ≡ = === _
}

class Quantifier
case class ForallQuantifier(v: Var) extends Quantifier
case class ExistsQuantifier(v: Var) extends Quantifier

class QQ {
	var qlist: List[Quantifier] = Nil
	def forall(aVar: Var): QQ = {qlist = qlist ::: List(ForallQuantifier(aVar) ); this}
	def exists(aVar: Var): QQ = {qlist = qlist ::: List(ExistsQuantifier(aVar) ); this}
	def ff (f: FOLFormula):FOLFormula = qlist match {
		case Nil => f
		case ForallQuantifier(aVar) :: qs =>
			qlist = qs;
			Forall(aVar, ff(f))
		case ExistsQuantifier(aVar) :: qs =>
			qlist = qs;
			Exists(aVar, ff(f))
	}

	def ∀ = forall _
	def ∃ = exists _
	def ∘ = ff _
}

object QQ {
	//def apply() = new QQ()
	def @@ = new QQ()
	def ▶ = @@
	def ¬ (f: FOLFormula)= Not(f)
}

/** DSL for internal use. Not to be used while specifying the programs. */
object FOLFormulaDSLApp {
	import QQ._

	val x = VarInt("x")
	val y = VarInt("y")

	val f: FOLFormula = (x f_< y)
	val f2: FOLFormula = (x f_< y) && (x f_< y)
	val f3: FOLFormula = (x f_< y) f_\/ (x f_< y)
	val f4: FOLFormula = (x f_< y) f_==> (x f_< y)

	val f5: FOLFormula = @@ forall(x) exists(y) ff(f)
	val f5_1: FOLFormula = ▶ ∀(x)∘(f)
	val f6: FOLFormula = ▶ ∀(x) ∃(y) ∘(▶ ∀(x) ∘(f))
	val f7: FOLFormula = ▶ ∀x ∃y ∘(▶ ∀x ∘(f))
	val f8: FOLFormula = ▶ ∀x ∃y ∘(▶ ∀x ∘(¬(f)))

}
