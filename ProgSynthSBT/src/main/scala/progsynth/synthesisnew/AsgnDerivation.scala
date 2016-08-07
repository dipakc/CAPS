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
import SynthUtils._

/** Output of the StartAsgnDerivationTactic tactic.
 *  Does not hold the derivation steps information.
 *  */
//printer implemented in progsynth.printers.AsgnDerivationPrinter2
class AsgnDerivation(
	val unkProg: UnknownProg,
	val varList: List[Var],
	val derivedTerms: List[(Var, Option[Term])]) extends CalcStep //primed variables
{
	/** Get the underlying initial formula corresponding to the proof obligation.
	 *  pre => wp.S.post
	 *  This  formula will contain the meta-variables. */
	def getFormula(): TermBool = {
		//TODO: ensure that quantifiers (dummies) are handled properly
		unkProg.pre.term.impl(unkProg.post.term.replaceVarsSim(varList, primedVars))
	}

	val primedVars = MetaVarUtilities.primedVars(varList)
	/**  x :=   x'
	 *   y := y' */
	/** Required for displaying the unknown program with the metavariable assignments */
	val dummyAsgnProg =
		mkAssignments(
				unkProg.pre,
				varList.zip{primedVars map (ExprProg(_))},
				unkProg.post)
}


/** Output of the StartIfDerivationTactic tactic.
 *  The StartIfDerivationTactic will usually be followed by the StartGCmdTactic (Stepin Tactic)
 *  Does not hold the derivation steps information.
 *  This class will supersede the AsgnDerivation.
 *  */
//printer implemented in progsynth.printers.IfDerivationPrinter
class IfDerivation(
	val unkProg: UnknownProg, //To hold the specification
	val varList: List[Var], //Variables to be modified. //unprimed
	val guardedCmds: List[(TermBool,List[(Var, Option[Term])]) ], // Grd, metavariables(primed), and expressions.
	val freshVars: List[Var],//fresh variables introduced during the derivation
	val assumedPres: List[TermBool] //assumed preconditions
	) extends CalcStep
{
	/** Get the underlying initial formula corresponding to the proof obligation.
	 *  pre => wp.S.post
	 *  This  formula will contain the meta-variables. */
	def getFormula(): TermBool = {
		//TODO: ensure that quantifiers (dummies) are handled properly
		unkProg.pre.term.impl(unkProg.post.term.replaceVarsSim(varList, primedVars))
	}

	val primedVars = MetaVarUtilities.primedVars(varList)

}

object MetaVarUtilities {
	def primedVars(varList: List[Var]) = varList map mkPrimedVar
	def unprimedVars(varList: List[Var]) = varList map mkUnprimedVar

	/** make a primed var.
	 *  > mkPrimedVar(VarInt("x"))
	 *  VarInt("x'")
	 */
	def mkPrimedVar(aVar: Var) = Var.mkVar(aVar.v + "'", aVar.getType)

	/** make a unprimed var from primed var
	 *  > mkPrimedVar(VarInt("x'"))
	 *  VarInt("x")
	 */
	def mkUnprimedVar(aVar: Var) = {
		val newVarStr = aVar.v replace("'", "")
		Var.mkVar(newVarStr, aVar.getType)
	}
}

/** Class to store the object associated with each calculational proof step.
 *  The underlying core object is of type TermBool
 *  CalcProofStep is derived from PSProgTree in order to be able to assign displayids to all the subnodes
 *  derivedTerms has primved variables.
 *  */
case class CalcProofStep(val coreObj: Term, val guard: TermBool, val derivedTerms: List[(Var, Option[Term])],
		freshVariables: List[Var], assumedPreList: List[TermBool], metaVars: List[Var]) extends PSProgTree with CaseRewritable with CalcStep {

}
