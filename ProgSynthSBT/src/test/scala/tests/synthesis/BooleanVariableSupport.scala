package tests.synthesis
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.{utils=>psu, _}
import scalaz.{Const => SConst, _}
import Scalaz._
import progsynth._


object BooleanVariableSupport extends App {
	//val synthTreeObject = progsynth.synthesisold.PSSynthTree
	//val prototypeImpl = progsynth.synthesisnew

    def main() {
	    val b = VarBool("b")
	    val r = VarBool("r")
	    val cfalse = ConstBool("False") //TODO: should it be small case false
	    val ctrue = ConstBool("True")

	    val ip = mkFunctionProg2(
	            name = "returnTrue",
	            params =  List(b),
	            retVar = VarBool("r"),
	            annProg = mkUnknownProg (
	            			pre = TermBool.TrueT.inv,
	            			upid = 0,
	            			post = (r eqeq ctrue).inv(r)
	            		),
	            Nil
	            )

    	val synthTree = new SynthTree()
    	synthTree.setOutputFile("""d:\tmp\xyz3.html""")
		synthTree
		.initTactic2(ip)
		.stepIntoUnknownProgIdx(1)
		//.retValTactic
		//.stepOut
		.dumpState
    }
	main()
}
