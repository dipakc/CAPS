package tests.synthesis
import progsynth.synthesisnew._
import progsynth.types._
import progsynth.types.Types._
import progsynth.{utils=>psu, _}
import scalaz.{Const => SConst, _}
import Scalaz._
import progsynth._
import progsynth.ProgSynth._
import scala.sys.process._

object ExistsPositiveElementBool extends App {
	//val synthTreeObject = progsynth.synthesisold.PSSynthTree
	//val prototypeImpl = progsynth.synthesisnew.Expt7

	def mkPrimedVar(aVar: Var) = MetaVarUtilities.mkPrimedVar(aVar)

    def main() {
	    val r = VarBool("r")
	    val c1 = ConstInt("1")
	    val c0 = ConstInt("0")
	    val i = VarInt("i")
	    val n = VarInt("n")
	    val N = VarInt("N")
	    val arr = VarArrayBool("arr")


	    val ip = mkFunctionProg2(
	            name = "existsPositiveElement",
	            params =  List(arr, N),
	            retVar = VarBool("r"),
	            annProg = mkUnknownProg (
	            		pre = (N >= c0).inv,
	            		upid = 0,
	            		//post = (r.fm iff Exists(i, c0 <= i && i < N && arr.select(i).fm)).inv(r)
	            		post = (r eqeq ExistsTermBool(i :: Nil, TermBool.TrueT, c0 <= i && i < N && arr.select(i))).inv(r)
	            ), Nil  	)

    	val synthTree = new SynthTree()
    	synthTree.setOutputFile("""d:\tmp\ExistsPositiveElementBool.html""")
		synthTree
		.initTactic2(ip)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rcvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.assumeSkip()
		.stepOut
		.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((mkPrimedVar(r), r || arr.select(n)) :: (mkPrimedVar(n), n + c1) :: Nil)
		.stepOut
//		.introAssignment((r, r || arr.select(n)), (n, n + c1))
		.stepOutAll
/*		.stepIntoUnknownProgIdx(1)
		.introAssignmentEnd(n, n + c1)
		.stepIntoUnknownProgIdx(1)
		.stepInPostFormula
		.replaceByEquiv( i < n + c1, i < n || (i eqeq n)) */
		.dumpState
    }

	main()
	Seq("""C:\Program Files (x86)\Mozilla Firefox\firefox.exe""", """d:\tmp\ExistsPositiveElementBool.html""").!
}
