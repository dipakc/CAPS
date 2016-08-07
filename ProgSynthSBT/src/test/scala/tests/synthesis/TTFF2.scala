package tests.synthesis
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.{utils=>psu, _}
import scalaz.{Const => SConst, Forall => SForall, _}
import Scalaz._
import progsynth._
import scala.sys.process._

object TTFF2 extends App {
	//val synthTreeObject = progsynth.synthesisold.PSSynthTree
	//val prototypeImpl = progsynth.synthesisnew.Expt7

	def prime(aVar: Var) = MetaVarUtilities.mkPrimedVar(aVar)

    def main() {
	    val r = VarBool("r")
	    val c1 = ConstInt("1")
	    val c0 = ConstInt("0")
	    val i = VarInt("i")
	    val n = VarInt("n")
	    val p = VarInt("p")
	    val N = VarInt("N")
	    val arr = VarArrayBool("arr")


	    val ip = mkFunctionProg2(
	            name = "TTFF",
	            params =  List(arr, N),
	            retVar = VarBool("r"),
	            annProg = mkUnknownProg (
	            		pre = (N >= c0).inv,
	            		upid = 0,
	            		//post = (r.fm iff Forall(i, c0 <= i && i < N - c1 impl arr.select(i).fm || !arr.select(i + c1).fm)).inv(r)
	            		post = (r equiv
	            					ExistsTermBool(p, c0 <= p && p <= N
	            							&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
	            							&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
	            ), Nil  	)

    	val synthTree = new SynthTree()
    	synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		synthTree
		.initTactic2(ip)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		/*
		.rtvInPost(N - c1, n, c0, c0 <= n && n <= N - c1)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N - c1, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.assignmentDerivation(r :: n :: Nil)
		.instantiateMeta((prime(n), n + c1) :: Nil)
		.applyHint(antecedents = List(c0 <= n, n <= N - c1, Not(n eqeq N - c1)),
				consequents = List(c0 <= n + c1, n + c1 <= N - c1))
//		.instantiateMeta((mkPrimedVar(r), r && ( arr.select(n) || !(arr.select(n + c1)) ) ) :: (mkPrimedVar(n), n + c1) :: Nil)
		.stepOutAll

		*/
		.dumpState
    }

	main()
	Seq("""D:\ProgramFilesx86\MozillaFirefox\firefox.exe""", """d:\tmp\TTFF2.html""").!
}
