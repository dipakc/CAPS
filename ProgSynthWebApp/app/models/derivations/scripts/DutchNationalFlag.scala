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

//Top down displayid numbering
object DutchNationalFlag extends DerivationScript {
    val name = "DutchNationalFlag"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val i = VarInt("i")
		val r = VarInt("r")
		val w = VarInt("w")
		val b = VarInt("b")
		val tmp = VarInt("tmp")
		val y = VarInt("y")
		val X = VarInt("X")
		val Y = VarInt("Y")
		val a = VarArrayInt("a")
		val ARR = VarArrayInt("ARR")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")

		val synthTree =
		new SynthTree()
		.applyTacticBatch(new Init4Tactic(
			name = name,
			immutableVars = List(N),
			mutableVars = List(a, r, w),
			globalInvs = List(N >= c0),
			preF = TermBool.TrueT,
			postF = (
					ForallTermBool(i, 0 <= i < r, a.select(i) < 0) &&
					ForallTermBool(i, r <= i < w, a.select(i) eqeq 0) &&
					ForallTermBool(i, w <= i < N, a.select(i) > 0) &&
					0 <= r <= w <= N),
			macros = Nil
		))
		.applyTacticBatch( new RTVInPost2Tactic(40, b, c0, w <= b <= N)) //w in w <= i
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		.applyTacticBatch( new DeleteConjunctTactic(b eqeq w, b - w) )
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		.applyTacticBatch( new IntroAssignmentTactic((r, c0) :: (w, c0) :: (b, N)::Nil) )
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		.applyTacticBatch( new IntroIfTactic(List(a.select(w) < c0, a.select(w) eqeq c0, a.select(w) > c0)))
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroAssignmentEndTactic(List((r, r + c1), (w, w + c1))))
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroSwapTactic(a, w, r))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroAssignmentTactic(List((w, w + c1))))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroAssignmentEndTactic(List((b, b - c1))))
        .applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroSwapTactic(a, w, b-1))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())

		//val html = views.xhtml.showState(synthTree)
		synthTree
	}
}

