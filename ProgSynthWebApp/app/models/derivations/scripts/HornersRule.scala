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

object HornersRule extends DerivationScript {
    val name = "HornersRule"

	def apply(): SynthTree = {

		val c = VarArrayInt("c")
		val N = VarInt("N")
		val r = VarInt("r")
		val x = VarInt("x")
		val i = VarInt("i")
		val n = VarInt("n")
		val y = VarInt("y")

		val immutableVars = c :: N :: x :: Nil
		val mutableVars = r :: Nil
		val globalInvs = (N >= 0) :: Nil

		val pre = TermBool.TrueT
		// r = ( MAX p, q : 0 <= p <= q <= N: sum.p.q)

		val post =  r eqeq PlusQTermInt(
				dummies = List(i),
				range =  0 <= i < N,
				term = c.select(i) * x.pow(i))

		val macros = Nil
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("HornersRule", immutableVars, mutableVars, globalInvs, pre, post, macros))
		.applyTacticBatch(new RTVInPostTactic(N, n, 0, 0 <= n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(r :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(6)) //Consequent
		.applyTacticBatch(new ReplaceSubFormulaTactic(21, TermBool.TrueT)) //0 <= n <= N
		.applyTacticBatch(new SimplifyTactic())
		.applyTacticBatch(new ReplaceSubTermTactic(12, 0)) //n
		.applyTacticBatch(new EmptyRangeTactic(4)) //SUM
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), ConstInt("0")) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentEndTactic(List((n, n + 1))))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(r :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(34)) //consequent
		.applyTacticBatch(new StepIntoSubFormulaTactic(23)) // 0 <= n <= N
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new SimplifyTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(10, (i < n) || (i eqeq n)))
		.applyTacticBatch(new DistributivityTactic(6))
		.applyTacticBatch(new RangeSplitTactic(4))
		.applyTacticBatch(new ReplaceSubTermTactic(5, r))
		.applyTacticBatch(new ReplaceSubTermTactic(5, r))
		.applyTacticBatch(new ReplaceSubFormulaTactic(8, i eqeq n))
		.applyTacticBatch(new OnePointTactic(6))
		.applyTacticBatch(new AssumePreTactic( y::Nil, y eqeq pow(x, n)))
		.applyTacticBatch(new ReplaceSubTermTactic( 10, y))
		.applyTacticBatch(new InstantiateMetaTactic( (prime(r), r + c.select(n) * y)::Nil ))
		.applyTacticBatch(new ReplaceFormulaTactic( TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic( TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoSubProgTactic(87))
    }
}

