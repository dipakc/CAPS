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

object initIf extends DerivationScript {
    val name = "initIf"

	def apply(): SynthTree = {
		val x = VarInt("x")
		val y = VarInt("y")
		val c0 = ConstInt("0")
		val mutableVars = x :: y :: Nil
		val immutableVars = Nil
		val pre = x > c0 && y > c0 && (x neqeq y)
		val post = x > c0 && y > c0

		val synthTree = new SynthTree()

		synthTree
		.applyTacticBatch(new Init4Tactic("IfProg", immutableVars, mutableVars, Nil, pre, post, Nil))
    }
}

