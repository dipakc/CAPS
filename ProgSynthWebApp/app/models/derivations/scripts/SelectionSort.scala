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
import progsynth.proofobligations.POGenerator
import progsynth.provers.ProgramAnnPOProver
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._



object SelectionSort extends DerivationScript {

    val name = "SelectionSort"

	def apply(): SynthTree = {
    	val logger= LoggerFactory.getLogger("progsynth.SelectionSort")

		//immutable variables
		val N = VarInt("N") // N >= 0
		val immutableVars = N :: Nil

		//globalInvs
		val globalInvs = List(N >= c(0))

		//mutable variables
		val arr = VarArrayInt("arr") //[0..N)
		val mutableVars = arr :: Nil

		//other variables (new, dummies)
		val n = VarInt("n")
		val i = VarInt("i")
		val a = VarInt("a")
		val j = VarInt("j")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		//pre and post
		val pre = TermBool.TrueT
		val post = ForallTermBool(i, c(0) <= i && i < N,
		                ForallTermBool(j, i <= j < N, arr.select(i) <= arr.select(j)))

		logger.trace("Derivation: " + name)
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(name, immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPost2Tactic(13, n, zero, zero <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((n, zero) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentEndTactic((n, n + 1)::Nil))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(arr :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(41))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(8, (i < n) || (i eqeq n)))
		.applyTacticBatch(new DistributivityTactic(4))
		.applyTacticBatch(new RangeSplitTactic(2))
		.applyTacticBatch(new ReplaceSubFormulaTactic(30, i eqeq n))
		.applyTacticBatch(new OnePointTactic(28))
		.applyTacticBatch(new AssumePreTactic(a::Nil, n <= a < N && ForallTermBool(j, a <= j < N, arr.select(a) <= arr.select(j))))
	}
}
