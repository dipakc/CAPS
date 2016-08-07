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

object Max extends DerivationScript {
    val name = "Max"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val immutableVars = x :: y :: Nil
		val mutableVars = z :: Nil
		val pre = TermBool.TrueT
		val post = ((z eqeq x) || (z eqeq y) ) && (z >= x) && (z >= y)
////
		val synthTree = new SynthTree()

		synthTree
		.applyTacticBatch(new Init4Tactic("MaxProg", mutableVars, immutableVars, Nil, pre, post, Nil))
//		.applyTacticBatch(new StartIfDerivationTactic(z :: Nil))
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(z), x):: Nil))
//		.applyTacticBatch(new VerifiedTransformationTactic(x >= y, EquivBoolFn))
//		.applyTacticBatch(new GuessGuardTactic(x >= y))
//		.applyTacticBatch(new VerifiedTransformationTactic(TermBool.TrueT, EquivBoolFn))
//		.stepOut
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(z), y):: Nil))
//		.applyTacticBatch(new VerifiedTransformationTactic(x <= y, EquivBoolFn))
//		.applyTacticBatch(new GuessGuardTactic(x <= y))
//		.applyTacticBatch(new VerifiedTransformationTactic(TermBool.TrueT, EquivBoolFn))
//		.stepOut
//		.stepOut
//
//		//.applyTactic2(new StepIntoPO())
//		//.applyTactic2(new InstantiateMetaTactic((prime(z), x) :: Nil))
//		//.applyTactic2(new VerifiedTransformationTactic(x >= y, EquivBoolFn))
//		//.applyTactic2(new MagicTactic(Nil, TermBool.TrueT))
//		//.stepOut
//		//.stepOut
////test
    }
}

object sumOfArray extends DerivationScript {
    val name = "sumOfArray"

	def apply(): SynthTree = {
		null
	}
}

object expo extends DerivationScript {
    val name = "expo"

	def apply(): SynthTree = {
		null
    }
}


object fib extends DerivationScript {
    val name = "fib"

	def apply(): SynthTree = {
		null
    }
}

object boundedLinearSearch extends DerivationScript {
    val name = "boundedLinearSearch"

	def apply(): SynthTree = {
		null
    }
}


object bubbleSort extends DerivationScript {
    val name = "bubbleSort"

	def apply(): SynthTree = {
        null
    }
}


object insertionSort extends DerivationScript {
    val name = "insertionSort"

	def apply(): SynthTree = {
        null
    }
}

object mergeSort extends DerivationScript {
    val name = "mergeSort"

	def apply(): SynthTree = {
        null
    }
}

object quickSort extends DerivationScript {
    val name = "quickSort"

	def apply(): SynthTree = {
        null
    }
}

object bresenhamLineDrawing extends DerivationScript {
    val name = "bresenhamLineDrawing"

	def apply(): SynthTree = {
        null
    }
}
