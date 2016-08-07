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

//Direct assignment.
//Top down displayid numbering
object IntDiv extends DerivationScript {
    val name = "IntDiv"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val y = VarInt("y")
		val q = VarInt("q")
		val r = VarInt("r")
		val immutableVars = x :: y :: Nil
		val mutableVars = q :: r :: Nil
		val globalInvs = (x >= c(0)) :: (y > c(0)) :: Nil
		val pre = TermBool.TrueT
		val post = c(0) <= r && r < y && (q * y + r eqeq x)

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("IntDiv", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic(r < y, r))//
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List( (q, c(0)), (r, x) )))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((r, r - y),(q, q + c(1)))))
		.applyTacticBatch(new StepOutTactic())
    }
}

object IntDiv2 extends DerivationScript {
    val name = "IntDiv2"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val y = VarInt("y")
		val q = VarInt("q")
		val r = VarInt("r")
		val immutableVars = x :: y :: Nil
		val mutableVars = q :: r :: Nil
		val globalInvs = (x >= c(0)) :: (y > c(0)) :: Nil
		val pre = TermBool.TrueT
		val post = c(0) <= r && r < y && (q * y + r eqeq x)

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("IntDiv", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic(r < y, r))//
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List( (q, c(0)), (r, x) )))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		//.introAssignment((r, r - y),(q, q + c(1))) //TODO: derive
		.applyTacticBatch(new StepIntoBATactic(r :: q :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(27))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), r - y) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] - c(1)) * y + r eqeq x))
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] - c(1)) * y + r eqeq x))
		.applyTacticBatch(new InstantiateMetaTactic((prime(q), q + c(1)) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.stepOut
		.applyTacticBatch(new SimplifyAutoTactic())
		.stepOutAll
    }
}

//Cohen. 128
object IntDiv3 extends DerivationScript {
    val name = "IntDiv3"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val y = VarInt("y")
		val q = VarInt("q")
		val r = VarInt("r")
		val immutableVars = x :: y :: Nil
		val mutableVars = q :: r :: Nil

		val pre = x >= c(0) && y > c(0)
		val post = c(0) <= r && r < y && (q * y + r eqeq x)

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("IntDiv", immutableVars, mutableVars, Nil, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic(r < y, r))//
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: q :: Nil))
		.applyTacticBatch(new StepIntoPO())
		//.applyTacticBatch(new ExpandAllMacrosTactic())
		//.applyTacticBatch(new StepIntoConsequentTactic())
		.applyTacticBatch(new StepIntoSubFormulaTactic(18))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), x):: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] * y) eqeq c(0)))
		.applyTacticBatch(new InstantiateMetaTactic((prime(q), c(0)):: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut
		//.introAssignment((q, c(0)),(r, x))
		.stepOut
		.stepOut
		.stepIntoUnknownProgIdx(1)
		//.applyTacticBatch(new StartAsgnDerivationTactic(r :: q :: Nil))
		//.applyTacticBatch(new StepIntoPO())
		//.applyTacticBatch(new StepIntoConsequentTactic())
		//.introAssignment((r, r - y),(q, q + c(1))) //TODO: derive
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: q :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new StepIntoSubFormulaTactic(27))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), r - y) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] - c(1)) * y + r eqeq x))
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] - c(1)) * y + r eqeq x))
		.applyTacticBatch(new InstantiateMetaTactic((prime(q), q + c(1)) :: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.stepOut
		.applyTacticBatch(new SimplifyAutoTactic())
		.stepOutAll

    }
}

object intDivMacroTest extends DerivationScript {
    val name = "intDivMacroTest"

	def apply(): SynthTree = {
	    def macroToFn(aMacro: Macro): Fn = {
	       aMacro match {
	             case Macro(name, vars, retTpe, term) =>
	                 Fn(name, vars.map(_.getType), retTpe)
	       }
	    }

		val x = VarInt("x")
		val y = VarInt("y")
		val q = VarInt("q")
		val r = VarInt("r")
		val immutableVars = x :: y :: Nil
		val mutableVars = q :: r :: Nil
		val macro1  = Macro("xybounds", List(x, y), PSBool, x >= c(0) && y > c(0))
		val xyboundsFn = macroToFn(macro1)
		val pre = FnApp(xyboundsFn, List(x, y)).asInstanceOf[TermBool]
		val post = c(0) <= r && r < y && (q * y + r eqeq x)
		val synthTree = new SynthTree()
		synthTree
		.addGlobalMacro(macro1)
		.applyTacticBatch(new Init4Tactic("IntDiv", immutableVars, mutableVars, Nil, pre, post, Nil))
		//.applyTacticBatch(new ExpandAllMacrosTactic())
		.applyTacticBatch(new DeleteConjunctTactic(r < y, r))//
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: q :: Nil))
		.applyTacticBatch(new StepIntoPO())
		//.applyTacticBatch(new ExpandGlobalMacrosTactic(synthTree.getGlobalMacros()))
		//.applyTacticBatch(new UseGlobalMacroTactic(5, synthTree.getGlobalMacros()))
		.applyTacticBatch(new StepIntoConsequentTactic())
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), x):: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic((prime(q).asInstanceOf[VarInt] * y) eqeq c(0)))
		//.applyTacticBatch(new InstantiateMetaTactic((prime(q), c(0)):: Nil))
		//.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		//.stepOut
		//.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		//.stepOut
		//.stepOut
		//.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.introAssignment((r, r - y),(q, q + c(1))) //TODO: derive
		//.stepOutAll
	}
}


object IntDivStepIntoTest extends DerivationScript {
    val name = "IntDivStepIntoTest"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val y = VarInt("y")
		val q = VarInt("q")
		val r = VarInt("r")
		val immutableVars = x :: y :: Nil
		val mutableVars = q :: r :: Nil
		val globalInvs = (x >= c(0)) :: (y > c(0)) :: Nil
		val pre = TermBool.TrueT
		val post = c(0) <= r && r < y && (q * y + r eqeq x)

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("IntDiv", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic(r < y, r))//
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(r :: q :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(9))
    }
}
