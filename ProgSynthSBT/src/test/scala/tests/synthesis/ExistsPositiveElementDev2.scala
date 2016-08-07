package tests.synthesis
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.{utils=>psu, _}
import scalaz.{Const => SConst, _}
import Scalaz._
import progsynth._

/*
object ExistsPositiveElementDev2 extends App {
	//val synthTreeObject = progsynth.synthesisold.PSSynthTree
	//val prototypeImpl = progsynth.synthesisnew.Expt7

    def main() {
	    val r = VarInt("r")
	    val c1 = ConstInt("1")
	    val c0 = ConstInt("0")
	    val i = VarInt("i")
	    val n = VarInt("n")
	    val N = VarInt("N")
	    val arr = VarArrayInt("arr")

	    val ip = mkFunctionProg2(
	            name = "existsPositiveElement",
	            params =  List(arr, N),
	            retVar = VarInt("r"),
	            annProg = mkUnknownProg (
	            		pre = (N > c0).inv,
	            		upid = 0,
	            		post = ((r eqeq c1) equiv ExistsTermBool(i, c0 <= i && i < N impl arr.select(i) > c0)).inv(r)
	            ), Nil  	)

    	val synthTree = new SynthTree()
    	synthTree.setOutputFile("""d:\tmp\xyz2.html""")
		synthTree
		.initTactic2(ip)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(None)
		.stepIntoUnknownProgIdx(1)
		.rcvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, c0))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.introAssignmentEnd(n, n + c1)
		.stepIntoUnknownProgIdx(1)
		.stepInPostFormula
		.replaceByEquiv( i < n + c1, i < n || (i eqeq n))
		.dumpState
    }
	main()
}
*/