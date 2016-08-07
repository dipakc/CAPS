package progsynth.types

import progsynth.methodspecs.InterpretedFns._
import progsynth.ProgSynth._

import Types._
import scalaz.Lens
import scala.collection.mutable.LinkedHashMap

case class InvariantT(loc: Option[Int], term: TermBool, rvVar: Option[Var])
	extends Invariant2Utils with PSProgTree with CaseRewritable {
	//override def toString = prettyPrint(this)
	def updateRvVar(newVar: Var): InvariantT = {
		if (this.term.getFreeVars contains newVar)
			this.copy(rvVar = Some(newVar))
		else
			this
	}

	def setRvVar(aVar: Var): InvariantT = {
		this.copy(rvVar = Some(aVar))
	}

	def removeRvVar: InvariantT = {
		this.copy(rvVar = None)
	}

	def removeRedundantRvVar(): InvariantT = {
		if (this.term.getFreeVars contains rvVar)
			this.copy(rvVar = None)
		else
			this
	}

	def modifyTermBool(fun: TermBool => TermBool): InvariantT = {
		this.copy(term = fun(term))
	}

	def containsUnknown = this.term.existsSubTerm(_.isUnknown)

	/** does not modify self*/
	def addConjunct(aTerm: TermBool) =
		InvariantT.term.set(this, (this.term && aTerm).simplify)

	def and(aTerm: TermBool) =
		InvariantT.term.set(this, (this.term && aTerm).simplify)
}

object InvariantT extends Invariant2Lensed {
}

trait Invariant2Lensed {
	def loc: Lens[InvariantT, Option[Int]] = Lens(_.loc, (p, n) => p.copy(loc = n))
	def rvVar: Lens[InvariantT, Option[Var]] = Lens(_.rvVar, (p, n) => p.copy(rvVar = n))
	def term: Lens[InvariantT, TermBool] = Lens(_.term, (p, n) => p.copy(term = n))
	class Invariant2W[A](l: Lens[A, InvariantT]) {
		def loc: Lens[A, Option[Int]] = l andThen InvariantT.loc
		def rvVar: Lens[A, Option[Var]] = l andThen InvariantT.rvVar
		def term: Lens[A, TermBool] = l andThen InvariantT.term
	}
	implicit def lens2Invariant2W[A](l: Lens[A, InvariantT]): Invariant2W[A] = new Invariant2W(l)
}

trait Invariant2Utils { self: InvariantT =>
	def substituteRvVar(aTerm: Term): InvariantT = {
		self match {
			case InvariantT(_, curTerm, Some(aVar)) => {
				val newTerm = curTerm.replaceVar(aVar, aTerm).asInstanceOf[TermBool] //TODO: avoid type casting.
				val newInv = self.copy(term = newTerm)
				newInv.removeRedundantRv()
			}
			case InvariantT(_, _, None) => self
		}
	}

	def removeRedundantRv() = {
		rvVar match {
			case Some(aVar) =>
				if (!self.term.existsSubTerm(_ == aVar)) {
					self.copy(rvVar = None)
				} else self
			case None => self
		}
	}

	/*
	/**Returns scala code(String) that constructs the InvariantT. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the InvariantT
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the InvariantT is returned */
	def toCode(ctxMap: LinkedHashMap[String, String], introduceVar: Boolean = true): String = {
		//InvariantT(loc: Option[Int], formula: TermBool, rvVar: Option[Var])
		val formulaCode = TermBool.toCode(formula, ctxMap)
		val rvVarCode = rvVar match {
			case Some(rvVarTerm) => <a>Some({rvVarTerm.toCode(ctxMap)})</a>.text
			case None => "None"
		}

		val codeRhs = <a>InvariantT({ loc }, { formulaCode }, { rvVarCode })</a>.text
		val seed = Some("inv")
		GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}
	*/
}