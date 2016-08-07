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


//cohen 154
object ArrayMax extends DerivationScript {

    val name = "ArrayMax"

	def apply(): SynthTree = {
    	val logger= LoggerFactory.getLogger("progsynth.ArrayMax")

		//immutable variables
		val arr = VarArrayInt("arr") //[0..N)
		val N = VarInt("N") // N >= 1
		val immutableVars = arr :: N :: Nil

		//globalInvs
		val globalInvs = List(N >= c(1))

		//mutable variables
		val m = VarInt("m")
		val mutableVars = m :: Nil

		//other variables (new, dummies)
		val n = VarInt("n")
		val i = VarInt("i")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		//pre and post
		val pre = TermBool.TrueT
		val post = m eqeq MaxQTermInt(i, c(0) <= i && i < N, arr.select(i))

		logger.trace("Derivation: " + name)
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(name, immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, one, one <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((n, one) :: (m, arr.select(0)) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(m :: n :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c(1)) ::
				(prime(m), FnApp(MaxIntFn, List(m , arr.select(n)))) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		//.applyTactic2(new StepIntoConsequentTactic())
		//.applyTactic2(new StepIntoSubFormulaTactic(21))
		//.applyTactic2(new ReplaceFormulaTactic(TermBool.TrueT))
		//.applyTactic2(new StepIntoSubFormulaTactic(9))
		//.introAssignment((x, ConstInt("+Inf")))
		//.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.applyTactic2(new StartAsgnDerivationTactic(x :: n :: Nil))
		//.applyTactic2(new StepIntoPO())
		//.applyTactic2(new StepIntoConsequentTactic())
		//.applyTactic2(new InstantiateMetaTactic((prime(n), n + c(1)):: Nil))
		//.applyTactic2(new ReplaceSubFormulaTactic(10, (i < n) || (i eqeq n)))
	}
}
