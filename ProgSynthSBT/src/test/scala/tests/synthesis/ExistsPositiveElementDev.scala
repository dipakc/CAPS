package tests.synthesis
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.{utils=>psu, _}
import scalaz.{Const => SConst, _}
import Scalaz._
import progsynth.debug.PSDbg

/*
object ExistsPositiveElementDev extends App{
	//val synthTreeObject = progsynth.synthesisold.PSSynthTree
	//val prototypeImpl = progsynth.synthesisnew

    def main() {

	    val r = VarInt("r") //> r  : progsynth.types.Var = VarInt(r)
	    val c1 = ConstInt("1") //> c1  : progsynth.types.Const = ConstInt(1)
	    val c0 = ConstInt("0") //> c0  : progsynth.types.Const = ConstInt(0)
	    val i = VarInt("i") //> i  : progsynth.types.Var = VarInt(i)
	    val n = VarInt("n") //> n  : progsynth.types.Var = VarInt(n)
	    val N = VarInt("N") //> N  : progsynth.types.Var = VarInt(N)
	    val arr = VarArrayInt("arr") //> arr  : progsynth.types.Var = Var(arr,PSArrayInt)

	    val ip0 = mkUnknownProgC {
	        InvariantT(None,
	            (N > c0),
	            None)
	    }(0) {
	        InvariantT(None,
	            ((r eqeq c1) eqeq ExistsTermBool(i, c0 <= i && i < N impl arr.select(i) > c0)),
	            r.some)
	    }

	    val ip = mkUnknownProgC {
	        InvariantT(None,
	            ((r eqeq c1) equiv ExistsTermBool(i, c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neqeq n),
	            None)
	    }(1) {
	        InvariantT(None,
	            ((r eqeq c1) equiv ExistsTermBool(i, c0 <= i && i < n + c1 && arr.select(i) > c0)) && n + c1 <= N,
	            None)
	    }

    	val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\abc3.xml""", "xml")
    	synthTree.setOutputFile("""d:\tmp\xyz.html""")
		synthTree
		.initTactic2(ip)
		.stepInPostFormula
		.replaceFormula(TermBool.TrueT)
		.stepOutAll
//		.splitRange(5)
//		.stepOutAll
		PSDbg.logln("done")
    }
	main()
}
*/