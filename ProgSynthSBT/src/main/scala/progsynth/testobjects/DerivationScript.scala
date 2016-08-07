package progsynth.testobjects

import progsynth.synthesisnew.SynthTree

trait DerivationScript {
    val name: String
    def apply(): SynthTree
}