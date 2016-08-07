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

object emptyTree extends DerivationScript {
    val name = "emptyTree"

	def apply(): SynthTree = {

		val synthTree = new SynthTree()
		synthTree
	}
}
