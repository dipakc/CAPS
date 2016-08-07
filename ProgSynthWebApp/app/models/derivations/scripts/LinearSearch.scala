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

//cohen pp131
//Top down displayid numbering
object LinearSearch extends DerivationScript {
    val name = "LinearSearch"

	def apply(): SynthTree = {
		//immutable variables
		val f = VarArrayBool("f")
		val X = VarInt("X")
		val immutableVars = f :: X :: Nil

		//globalInvs
		val globalInvs = List(c(0) <= X, f.select(X))
		//mutable variables
		val x = VarInt("x")
		val mutableVars = x :: Nil

		//dummies
		val i = VarInt("i")

		val pre = TermBool.TrueT
		val post = c(0) <= x && f.select(x) && ForallTermBool(i, c(0) <= i && i < x, !f.select(i))

		val synthTree = new SynthTree()
		synthTree
		.applyTacticBatch(new Init4Tactic("linearSearch", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new DeleteConjunctTactic(f.select(x), X - x))
		.stepIntoUnknownProgIdx(1)
		.introAssignment((x, c(0)))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.introAssignment((x, x + c(1)))
		.stepOutAll
    }
}

