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

object ArraySwap extends DerivationScript {
    val name = "ArraySwap"

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
		val immutableVars = List (x, y, X, Y, ARR, N)
		val mutableVars = arr :: Nil

		val globalInvs = List(c0 <= x && x < N, c0 <= y && y < N)

		val pre =
		  (arr.select(x) eqeq X) &&
		  (arr.select(y) eqeq Y) &&
		  ForallTermBool(i, (i neqeq x) && (i neqeq y), arr.select(i) eqeq ARR.select(i))

		val post =
			(arr.select(x) eqeq Y) &&
			(arr.select(y) eqeq X) &&
			ForallTermBool(i, (i neqeq x) && (i neqeq y), arr.select(i) eqeq ARR.select(i))

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("ArraySwap", immutableVars, mutableVars, Nil, pre, post, Nil))
		.applyTacticBatch(new IntroSwapTactic(arr, x, y)) //

		//val html = views.xhtml.showState(synthTree)

		synthTree

	}
}

