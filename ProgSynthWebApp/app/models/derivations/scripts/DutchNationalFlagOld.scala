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

object DutchNationalFlagOld extends DerivationScript {
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
		val h = VarArrayInt("h")
		val ARR = VarArrayInt("ARR")
		val N = VarInt("N")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")

		val synthTree =
		new SynthTree()
		.applyTacticBatch(new Init4Tactic(
			name = name,
			immutableVars = List(N),
			mutableVars = List(h, r, w),
			globalInvs = List(N >= c0),
			preF = TermBool.TrueT,
			postF = (
					ForallTermBool(i, c0 <= i && i < r, h.select(i) <= c0) && //TODO: Should it be h[i] < 0
					ForallTermBool(i, r <= i && i < w, h.select(i) eqeq c0) &&
					ForallTermBool(i, w <= i && i < N, h.select(i) >= c0) &&
					c0 <= r && r <= w && w <= N),
			macros = Nil
		))
		.applyTacticBatch( new RTVInPost2Tactic(33, b, c0, w <= b && b <= N))
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		.applyTacticBatch( new DeleteConjunctTactic(b eqeq w, b - w) )
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		.applyTacticBatch( new IntroAssignmentTactic((r, c0) :: (w, c0) :: (b, N)::Nil) )
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1) )
		//.applyTactic2( new StartIfDerivationTactic(List(r, w, b, h)) )
		//.applyTactic2( new StartGCmdDerivationTactic())
		//.applyTactic2( new GuessGuardTactic(h.select(w) <= c0))
		//.applyTactic2( new InstantiateMetaTactic((prime(r), r + c1) :: (prime(w), w + c1) :: (prime(b), b):: Nil))
		//.applyTactic2( new StepIntoSubFormulaTactic(149))
		//.applyTactic2( new SimplifyAutoTactic())
		.applyTacticBatch( new IntroIfTactic(List(h.select(w) <= c0, h.select(w) eqeq c0, h.select(w) >= c0)))
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new StepIntoBATactic(List(h, r, w, b)))
		.applyTacticBatch( new InstantiateMetaTactic(
				(prime(r), r + c1) ::
				(prime(w), w + c1) ::
				(prime(b), b) ::
				(prime(h), h) :: Nil
				))
		//.applyTacticBatch( new AssumePreTactic(Nil, TermBool.TrueT))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroSwapTactic(h, w, r))/*16*/
		.applyTacticBatch( new StepOutTactic())/*17*/
		.applyTacticBatch( new StepOutTactic())/*18*/
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new StepIntoBATactic(List(h, r, w, b)))
		.applyTacticBatch( new InstantiateMetaTactic(
				(prime(r), r) ::
				(prime(w), w + c1) ::
				(prime(b), b) ::
				(prime(h), h) :: Nil
				))
		.applyTacticBatch( new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new StepIntoBATactic(List(h, r, w, b)))
		.applyTacticBatch( new InstantiateMetaTactic(
				(prime(r), r) ::
				(prime(w), w) ::
				(prime(b), b - c1) ::
				(prime(h), h) :: Nil
				))
		.applyTacticBatch( new StepOutTactic())
		//.applyTacticBatch( new AssumePreTactic(Nil, TermBool.TrueT))
		.applyTacticBatch( new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch( new IntroSwapTactic(h, w, b - c1))
		.applyTacticBatch( new StepOutTactic())
		.applyTacticBatch( new StepOutTactic())

		//val html = views.xhtml.showState(synthTree)
		synthTree
	}
}

