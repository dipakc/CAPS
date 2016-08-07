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
object ArraySum extends DerivationScript {

    val name = "ArraySum"

	def apply(): SynthTree = {
    	val logger= LoggerFactory.getLogger("progsynth.ArraySum")

		val zero = ConstInt("0")
		val one = ConstInt("1")

    	//immutable variables
		val arr = VarArrayInt("arr") //[0..N)
		val N = VarInt("N") // N >= 0
		val immutableVars = arr :: N :: Nil

		//globalInvs
		val globalInvs = List(N >= zero)

		//mutable variables
		val s = VarInt("s")
		val mutableVars = s :: Nil

		//other variables (new, dummies)
		val n = VarInt("n")
		val i = VarInt("i")

		//pre and post
		val pre = TermBool.TrueT
		val post = s eqeq PlusQTermInt(i, c(0) <= i && i < N, arr.select(i))

		logger.trace("Derivation: " + name)
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(name, immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, zero, zero <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((s, zero) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(s :: n :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + one) ::
				(prime(s), FnApp(PlusIntFn, List(s , arr.select(n)))) :: Nil))
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
