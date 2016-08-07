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

//Top down displayid numbering
object ExistsTrue extends DerivationScript {
    val name = "ExistsTrue"

	def apply(): SynthTree = {
		import varObj._

		val name = "ExistsTrue"
		val mutableVars = List(r)
		val immutableVars = List(arr, N);
		val preF: TermBool = TermBool.TrueT
		val postF: TermBool = (r equiv ExistsTermBool(i, c0 <= i && i < N, arr.select(i)) )
		val globalInvs = List(N >= c0)

		val synthTree = new SynthTree()
		val synthTree1 =
		synthTree
		.applyTacticBatch(new Init4Tactic( name,  immutableVars, mutableVars, globalInvs, preF, postF, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("false")) :: (n, c0) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(List(r, n)))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30)) //consequent
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(10, (i < n) || (i eqeq n))) //i < n + 1
		.applyTacticBatch(new DistributivityTactic(6))//range
		.applyTacticBatch(new RangeSplitTactic(4)) // (equiv, 2)
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new OnePointTactic(17)) //(equiv, 2)(\/, 2)
		.applyTacticBatch(new ReplaceSubFormulaTactic(5, r)) ////(equiv, 2)(\/, 1)
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), r || arr.select(n)) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		//#############################
		return synthTree1
		synthTree1
		//#############################

    }
}
