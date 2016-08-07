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

object allTrue extends DerivationScript {
    val name = "Exe1_k62_allTrue"

	def apply(): SynthTree = {
		import varObj._

		val name = "Exe1_k62_allTrue"
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
//				//.applyTacticBatch(new StartIfDerivationTactic(r :: n :: Nil))
//				//.applyTacticBatch(new StartGCmdDerivationTactic())
//				//.applyTacticBatch(new StepIntoConsequentTactic())
//				//.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
//				//.applyTacticBatch(new SimplifyAutoTactic())
//				//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (i < n) || (i eqeq n )))
//				//.applyTacticBatch(new DistributivityTactic(13))
//				//.applyTacticBatch(new RangeSplitTactic(21))
//				//.applyTacticBatch(new ReplaceSubFormulaTactic(21, i eqeq n))
//				//.applyTacticBatch(new OnePointTactic(21))
//				//.applyTacticBatch(new ReplaceSubFormulaTactic(13, r))
//				//.applyTacticBatch(new InstantiateMetaTactic((prime(r), r && arr.select(n)) :: Nil))
//				//.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//				//.stepOut
//				//.applyTacticBatch(new SimplifyTactic())
//				//.stepOut
//				//.stepOut
//				//.stepOut
//				//.stepOut
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO)
		.applyTacticBatch(new StepIntoSubFormulaTactic(49))
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(10, (i < n) || (i eqeq n))) //Parse error
		.applyTacticBatch(new DistributivityTactic(13))
		.applyTacticBatch(new RangeSplitTactic(21))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new OnePointTactic(21))
		.applyTacticBatch(new ReplaceSubFormulaTactic(13, r))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), r && arr.select(n)) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOutAll
		//#############################
		return synthTree1
		synthTree1
		//#############################
    }
}
