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

//Kalde pg. 98
//Top down displayid numbering
object BinarySearch extends DerivationScript {
    val name = "BinarySearch"

	def apply(): SynthTree = {
		//immutable variables
		val f = VarArrayInt("f") //[0..N]
		//Note: The array is no-empty.
		//Note: N is a valid index
		val N = VarInt("N")
		val A = VarInt("A")
		val immutableVars = f :: N :: A :: Nil

		//globalInvs
		val globalInvs = List(N >= c(1), f.select(c(0)) <= A, A < f.select(N))

		//mutable variables
		val x = VarInt("x")
		val mutableVars = x :: Nil

		//new variables
		val y = VarInt("y")
		val h = VarInt("h")

		//pre and post
		val pre = TermBool.TrueT
		val post = f.select(x) <= A && A < f.select(x + c(1)) && c(0) <= x && x < N

		//TODO: Complete the derivation
		//derivation start
		val synthTree = new SynthTree()

		synthTree
		.applyTacticBatch(new Init4Tactic("binarySearch", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(x + c(1), y, N, x < y && y <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(y eqeq x+c(1), y - x))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((x, c(0)))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(x :: y :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(37))
		.applyTacticBatch(new InstantiateMetaTactic((prime(y), y):: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new AssumePreTactic(List(h), x <= h && h < y))
		.applyTacticBatch(new InstantiateMetaTactic((prime(x), h):: Nil))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new AssumePreTactic(Nil, f.select(h) <= A))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new AssumeToIfTactic(283)) //second assume prog
		.applyTacticBatch(new StepIntoSubProgTactic(197))//assume prog
		.applyTacticBatch(new IntroAssignmentTactic(List((h, (x + y)/2 ))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((y, h ))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
	}
}


object BbinarySearch2 extends DerivationScript {
    val name = "BinarySearch2"

	def apply(): SynthTree = {
		//immutable variables
		val f = VarArrayInt("f") //[0..N]
		//Note: The array is no-empty.
		//Note: N is a valid index
		val N = VarInt("N")
		val A = VarInt("A")
		val immutableVars = f :: N :: A :: Nil

		//globalInvs
		val globalInvs = List(N >= c(1), f.select(c(0)) <= A, A < f.select(N))

		//mutable variables
		val x = VarInt("x")
		val mutableVars = x :: Nil

		//new variables
		val y = VarInt("y")
		val h = VarInt("h")

		//pre and post
		val pre = TermBool.TrueT
		val post = f.select(x) <= A && A < f.select(x + c(1)) && c(0) <= x && x < N

		//derivation start
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("binarySearch", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(x + c(1), y, N, x < y && y <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(y eqeq x+c(1), y - x))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((x, c(0)))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(List(x, y)))
		.applyTacticBatch(new StepIntoSubFormulaTactic(61))
		.applyTacticBatch(new InstantiateMetaTactic((prime(y), y):: Nil))//13
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new AssumePreTactic(List(h), x <= h && h < y))
		.applyTacticBatch(new InstantiateMetaTactic((prime(x), h):: Nil))
		.applyTacticBatch(new AssumePreTactic(Nil, f.select(h) <= A))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new AssumeToIfTactic(256))
		.applyTacticBatch(new StepIntoSubProgTactic(158))
		.applyTacticBatch(new IntroAssignmentTactic(List((h, (x + y) / c(2)))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((y, h))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StartIfDerivationTactic(x :: y :: Nil))
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		.applyTacticBatch(new StepIntoConsequentTactic())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(y), y):: Nil))//13
//		.applyTacticBatch(new ReplaceSubFormulaTactic(10, TermBool.TrueT))//14
//		.applyTacticBatch(new ReplaceSubFormulaTactic(19, TermBool.TrueT))//15
//		.applyTacticBatch(new StepIntoSubFormulaTactic(12))//16
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))//17
//		.applyTacticBatch(new StepOutTactic())//18
//		.applyTacticBatch(new SimplifyTactic())//19
//		.applyTacticBatch(new AssumePreTactic(List(h), x <= h && h < y))
//		.applyTacticBatch(new InstantiateMetaTactic((prime(x), h):: Nil))
//		.applyTacticBatch(new ReplaceFormulaTactic(f.select(h) <= A))
//		.applyTacticBatch(new GuessGuardTactic(f.select(h) <= A))
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new SimplifyTactic())
//		.applyTacticBatch(new StepOutTactic())
//		/////////////////////////
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		.applyTacticBatch(new StepIntoConsequentTactic())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(x), x):: Nil))
//		.applyTacticBatch(new ReplaceSubFormulaTactic(5, TermBool.TrueT))
//		.applyTacticBatch(new ReplaceSubFormulaTactic(8, TermBool.TrueT))
//		.applyTacticBatch(new ReplaceSubFormulaTactic(8, TermBool.TrueT))
//		.applyTacticBatch(new InstantiateMetaTactic((prime(y), h):: Nil))
//		.applyTacticBatch(new AssumePreTactic(Nil, x < h ))
//		.applyTacticBatch(new ReplaceSubFormulaTactic(12, TermBool.TrueT))
//		.applyTacticBatch(new GuessGuardTactic(A < f.select(h)))
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
//		.introAssignment((h, (x + y )/ c(2)))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StepOutTactic())
	}
}

//Kalde pg. 98
object BinarySearchSimplifyAuto extends DerivationScript {
    val name = "BinarySearchSimplifyAuto"

	def apply(): SynthTree = {
		//immutable variables
		val f = VarArrayInt("f") //[0..N]
		//Note: The array is no-empty.
		//Note: N is a valid index
		val N = VarInt("N")
		val A = VarInt("A")
		val immutableVars = f :: N :: A :: Nil

		//globalInvs
		val globalInvs = List(N >= c(1), f.select(c(0)) <= A, A < f.select(N))

		//mutable variables
		val x = VarInt("x")
		val mutableVars = x :: Nil

		//new variables
		val y = VarInt("y")
		val h = VarInt("h")

		//pre and post
		val pre = TermBool.TrueT
		val post = f.select(x) <= A && A < f.select(x + c(1)) && c(0) <= x && x < N

		//derivation start
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("binarySearch", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(x + c(1), y, N, x < y && y <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(y eqeq x+c(1), y - x))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic(List((x, c(0)))))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
//		//.applyTacticBatch(new InsertVariableTactic(h, c(0)))
//		//.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
//		//.applyTacticBatch(new IntroCompositionTactic(x < h && h < y))
//		.applyTacticBatch(new StartIfDerivationTactic(x :: y :: Nil))
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		.applyTacticBatch(new StepIntoConsequentTactic())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(y), y):: Nil))
//		//.applyTacticBatch(new SimplifyAutoTactic())////
	}
}

//	//Cohen pg. 171 (array access violation problem.)
//	def binarySearch(): SynthTree = {
//		//immutable variables
//		val b = VarArrayBool("b") //[0..N)
//		val N = VarInt("N")
//
//		//global invs
//		val globalInvs = (N >= c(0)) :: Nil
//
//		//mutable variables
//		val x = VarInt("x")
//		null
//	}

