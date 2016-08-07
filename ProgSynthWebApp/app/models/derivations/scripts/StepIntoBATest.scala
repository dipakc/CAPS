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

object StepIntoBATest extends StepIntoBATest3

class StepIntoBATest1 extends DerivationScript {
    val name = "StepIntoBATest1"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val i = VarInt("i")
		val tmp = VarInt("tmp")
		val y = VarInt("y")
		val X = VarInt("X")
		val Y = VarInt("Y")
		val arr = VarArrayInt("arr")
		val ARR = VarArrayInt("ARR")
		val N = VarInt("N")
		
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val immutableVars = List (x, y, X, Y, ARR, N)
		val mutableVars = arr :: Nil
		
		val globalInvs = List(c0 <= x && x < N, c0 <= y && y < N)
		
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(
			name = name,
			immutableVars = Nil,
			mutableVars = List(x, y),
			globalInvs = Nil,
			preF = x eqeq y,
			postF = x eqeq y + c1,
			macros = Nil))
		.applyTacticBatch(new StepIntoBATactic(lhsVars = List(x)))
		.applyTacticBatch(new InstantiateMetaTactic(
				primedVarTermList =
					Tuple2(prime(x), x + c1) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut
		
		val html = views.xhtml.showState(synthTree)
		
		synthTree
		       
	}
}


class StepIntoBATest2 extends DerivationScript {
    val name = "StepIntoBATest2"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val i = VarInt("i")
		val tmp = VarInt("tmp")
		val y = VarInt("y")
		val X = VarInt("X")
		val Y = VarInt("Y")
		val arr = VarArrayInt("arr")
		val ARR = VarArrayInt("ARR")
		val N = VarInt("N")
		
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val immutableVars = List (x, y, X, Y, ARR, N)
		val mutableVars = arr :: Nil
		
		val globalInvs = List(c0 <= x && x < N, c0 <= y && y < N)
		
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(
			name = name,
			immutableVars = Nil,
			mutableVars = List(x, y),
			globalInvs = Nil,
			preF = y eqeq x,
			postF = ((c0 <= x) impl (y eqeq c1)) && (( x < c0) impl (y eqeq c0)) ,
			macros = Nil))
		.applyTacticBatch(new StepIntoBATactic(lhsVars = List(y)))
		.applyTacticBatch(new GuessGuardTactic((c0 <= x)))
		.applyTacticBatch(new InstantiateMetaTactic(
				primedVarTermList =
					Tuple2(prime(y), c1) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut
		
		val html = views.xhtml.showState(synthTree)
		
		synthTree
		       
	}
}

class StepIntoBATest3 extends DerivationScript {
    val name = "StepIntoBATest3"

	def apply(): SynthTree = {

		val x = VarInt("x")
		val i = VarInt("i")
		val tmp = VarInt("tmp")
		val y = VarInt("y")
		val z = VarInt("z")
		val X = VarInt("X")
		val Y = VarInt("Y")
		val arr = VarArrayInt("arr")
		val ARR = VarArrayInt("ARR")
		val N = VarInt("N")
		
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val immutableVars = List (x, y, X, Y, ARR, N)
		val mutableVars = arr :: Nil
		
		val globalInvs = List(c0 <= x && x < N, c0 <= y && y < N)
		
		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic(
			name = name,
			immutableVars = Nil,
			mutableVars = List(x, y),
			globalInvs = Nil,
			preF = y eqeq x,
			postF = ((c0 <= x) impl (y eqeq c1)) && (( x < c0) impl (y eqeq c0)) ,
			macros = Nil))
		.applyTacticBatch(new StepIntoBATactic(lhsVars = List(y)))
		.applyTacticBatch(new GuessGuardTactic((c0 <= x)))
		.applyTacticBatch(new InstantiateMetaTactic(
				primedVarTermList =
					Tuple2(prime(y), c1) :: Nil))
		.applyTacticBatch(new AssumePreTactic( List(z), x < z))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut //
		.applyTacticBatch(new StepIntoSubProgTactic(140))
		.applyTacticBatch(new StepIntoIFBATactic(y :: Nil))
		.applyTacticBatch(new GuessGuardTactic(x < c0))
		.applyTacticBatch(new InstantiateMetaTactic(Tuple2(prime(y), c0) :: Nil))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut //
		
		val html = views.xhtml.showState(synthTree)
		
		synthTree
		       
	}
}