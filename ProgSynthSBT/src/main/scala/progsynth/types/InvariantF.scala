package progsynth.types

import progsynth.methodspecs.InterpretedFns._
import progsynth.ProgSynth._

import Types._
import scalaz.Lens
import scala.collection.mutable.LinkedHashMap

case class InvariantF(loc: Option[Int], formula: FOLFormula, rvVar: Option[Var]) extends InvariantUtils with PSProgTree with CaseRewritable {
	//override def toString = prettyPrint(this)
	def updateRvVar(newVar: Var): InvariantF = {
		if (this.formula.getFreeVars contains newVar)
			this.copy(rvVar = Some(newVar))
		else
			this
	}

	def setRvVar(aVar: Var): InvariantF = {
		this.copy(rvVar = Some(aVar))
	}

	def removeRvVar: InvariantF = {
		this.copy(rvVar = None)
	}

	def removeRedundantRvVar(): InvariantF = {
		if (this.formula.getFreeVars contains rvVar)
			this.copy(rvVar = None)
		else
			this
	}

	//Equivalent to InvariantF.formula.mod(self.pre, And(_, grdcmd.guard))
	def modifyFormula(fun: FOLFormula => FOLFormula): InvariantF = {
		this.copy(formula = fun(formula))
	}

	@deprecated("use containsUnknown instead", "")
	def known(inv: InvariantF) = !inv.formula.existsSubF(_.isUnknown)
	@deprecated("use containsUnknown instead", "")
	def unknown(inv: InvariantF) = inv.formula.existsSubF(_.isUnknown)

	def containsUnknown = this.formula.existsSubF(_.isUnknown)

	def addConjunct(aFormula: FOLFormula) = InvariantF.formula.set(this, And(this.formula, aFormula))

}

object InvariantF extends InvariantLensed {
}

trait InvariantLensed {
	def loc: Lens[InvariantF, Option[Int]] = Lens(_.loc, (p, n) => p.copy(loc = n))
	def rvVar: Lens[InvariantF, Option[Var]] = Lens(_.rvVar, (p, n) => p.copy(rvVar = n))
	def formula: Lens[InvariantF, FOLFormula] = Lens(_.formula, (p, n) => p.copy(formula = n))
	class InvariantW[A](l: Lens[A, InvariantF]) {
		def loc: Lens[A, Option[Int]] = l andThen InvariantF.loc
		def rvVar: Lens[A, Option[Var]] = l andThen InvariantF.rvVar
		def formula: Lens[A, FOLFormula] = l andThen InvariantF.formula
	}
	implicit def lens2InvariantW[A](l: Lens[A, InvariantF]): InvariantW[A] = new InvariantW(l)
}

trait InvariantUtils { self: InvariantF =>
	def substituteRvVarInF(aTerm: Term): InvariantF = {
		self match {
			case InvariantF(_, aFormula, Some(aVar)) => {
				val newFormula = aFormula.replaceVar(aVar, aTerm)
				val newInv = self.copy(formula = newFormula)
				newInv.removeRedundantRv()
			}
			case InvariantF(_, _, None) => self
		}
	}

	def removeRedundantRv() = {
		rvVar match {
			case Some(aVar) =>
				if (!self.formula.existsSubTerm(_ == aVar)) {
					self.copy(rvVar = None)
				} else self
			case None => self
		}
	}

	/**Returns scala code(String) that constructs the InvariantF. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the InvariantF
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the InvariantF is returned */
	def toCode(ctxMap: LinkedHashMap[String, String], introduceVar: Boolean = true): String = {
		//InvariantF(loc: Option[Int], formula: FOLFormula, rvVar: Option[Var])
		val formulaCode = FOLFormula.toCode(formula, ctxMap)
		val rvVarCode = rvVar match {
			case Some(rvVarTerm) => <a>Some({rvVarTerm.toCode(ctxMap)})</a>.text
			case None => "None"
		}

		val codeRhs = <a>InvariantF({ loc }, { formulaCode }, { rvVarCode })</a>.text
		val seed = Some("inv")
		GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}
}
