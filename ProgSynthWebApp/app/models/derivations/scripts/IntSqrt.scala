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

//Kalde pg. 55
//Top down displayid numbering
object IntSqrt extends DerivationScript {
    val name = "IntSqrt"
	def apply(): SynthTree = {
		val N = VarInt("N")
		val x = VarInt("x")
		val immutableVars = N :: Nil
		val mutableVars = x :: Nil

		val pre = TermBool.TrueT
		val post = x * x <= N && (x + c(1) ) * (x + c(1)) > N
		val globalInv = List(N >= c(0))

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("IntSqrt", immutableVars, mutableVars, globalInv, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic((x + c(1) ) * (x + c(1)) > N, N - x * x))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((x, c(0)))))
		.applyTacticBatch(new StepOutTactic())
		//.applyTactic2(new StepIntoProgIdTactic(81))//Step into the while prog.
		//.applyTactic2(new StrengthenInvariantTactic( (c(0) <= x)  :: Nil))//TODO: Why to strengthen?: variant PO.
		//TODO: investigate: is 0 <= x required?
		.stepIntoUnknownProgIdx(1)
		.introAssignment((x, x + c(1)))
		.stepOutAll
	}
}
