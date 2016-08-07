package models.derivations.scripts

import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth._
import progsynth._
import progsynth.ProgSynth._
import progsynth.methodspecs.InterpretedFns._
import scala.util._
import models._
import derivations.DerivationUtils._
import progsynth.testobjects.DerivationScript

//Fixed
object simplifyAutoIssue extends DerivationScript {
    val name = "allTrue"

	def apply(): SynthTree = {
		import varObj._

		val name = "allTrue"
		val mutableVars = List(r)
		val immutableVars = List(arr, N);
		val preF: TermBool = TermBool.TrueT
		val postF: TermBool = (r equiv ForallTermBool(i, c0 <= i && i < N, arr.select(i)) )
		val globalInvs = List(N >= c0)

		val synthTree = new SynthTree()
		val synthTree1 =
		synthTree
		.applyTacticBatch(new Init4Tactic( name,  immutableVars, mutableVars, globalInvs, preF, postF, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO)
		.applyTacticBatch(new StepIntoConsequentTactic())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())

		//.stepOutAll //TODO: Not Implemented
		//#############################
		return synthTree1
		synthTree1
		//#############################
    }
}
