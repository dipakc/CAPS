package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz._
import scalaz.Scalaz._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
//import progsynth.printers.XHTMLPrinters
import SynthUtils._

/** Tactics functions related to progsynth */
	trait PSTacticFuns { self: SynthTree =>

		def initTactic(name: String, params: List[Var], retVar: Var, preF: TermBool, postF: TermBool) = {
			applyTactic2(new InitTactic(name, params, retVar, preF, postF))
		}

		def initTactic2(initP: ProgramAnn) = {
			applyTactic2(new InitTactic2(initP))
		}

		def initTactic3(name: String, params: List[Var], retVar: Var, preF: TermBool, postF: TermBool, globalInvs: List[TermBool]) = {
			applyTactic2(new InitTactic3(name, params, retVar, preF, postF, globalInvs))
		}

		def stepOut() = {
			applyTactic2(new StepOutTactic())
		}

//		def stepInPostFormula = {
//			applyTactic2(new StepInPostFormulaTactic())
//		}

		def replaceFormula(newf: TermBool) = {
			applyTactic2(new ReplaceFormulaTactic(newf: TermBool))
		}

		def retValTactic(initTerm: Option[Term]) = {
			applyTactic2(new RetValTactic(initTerm))
		}

		def stepIntoUnknownProgId(id: java.lang.Integer) = {
			applyTactic2(new StepIntoUnknownProgIdTactic(id))
		}

		def stepIntoUnknownProgIdx(idx: java.lang.Integer) = {
			applyTactic2(new StepIntoUnknownProgIdxTactic(idx))
		}

//		def stepIntoProg(constructType: String) = {
//			applyTactic2(new StepIntoProgTactic(constructType))
//		}


		def rcvInPost(const0: Var, variable0: Var, initValue0: Term, bounds: TermBool) = {
			applyTactic2(new RCVInPostTactic(const0, variable0, initValue0, bounds))
		}

		def rtvInPost(const0: Term, variable0: Var, initValue0: Term, bounds: TermBool) = {
			applyTactic2(new RTVInPostTactic(const0, variable0, initValue0, bounds))
		}


		def deleteConjunct(conjunct: TermBool, variant: Term) = {
			applyTactic2(new DeleteConjunctTactic(conjunct, variant))
		}

		def introWhile(loopInvF: TermBool, guardF: TermBool) = {
			applyTactic2(new IntroWhileTactic(loopInvF, guardF))
		}

		def introAssignment(lhsRhsTuples:List[(Var, Term)]) = {
			applyTactic2(new IntroAssignmentTactic(lhsRhsTuples))
		}

		def introAssignment(lhsRhsTuples:(Var, Term)*) = {
			applyTactic2(new IntroAssignmentTactic(lhsRhsTuples.toList))
		}

		def assumeSkip() = {
			applyTactic2(new AssumeSkipTactic())
		}

		def insertVariable(aVar: Var, initVal: Term) = {
			applyTactic2(new InsertVariableTactic(aVar: Var, initVal: Term))
		}

		def introAssignmentEnd(lhs: Var, rhs: Term) = {
			applyTactic2(new IntroAssignmentEndTactic(List((lhs, rhs))))
		}

		/*
		def replaceByEquiv(oldF: TermBool, newF: TermBool) = {
			applyTactic2(new ReplaceByEquivTactic(oldF, newF))
		}
		*/

		private def ___AsgnDerivation___ = null

	}