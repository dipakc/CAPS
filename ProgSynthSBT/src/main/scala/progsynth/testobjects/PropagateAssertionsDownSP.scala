package progsynth.testobjects


import progsynth.types._
import progsynth.types.Types._
import progsynth.synthesisnew._

//These test objects are used to create test cases
// and to add the test cases to CAPS WebApp GUI(Gallery).

object PropagateAssertionDownSPTest extends DerivationScript {
	val name = "PropagateAssertionDownSPTest"

	def apply(): SynthTree = {
	
		object VarObj  {
			val r = VarBool("r")
			val c1 = ConstInt("1")
			val c0 = ConstInt("0")
			val i = VarInt("i")
			val n = VarInt("n")
			val p = VarInt("p")
			val s = VarBool("s")
			val N = VarInt("N")
			val arr = VarArrayBool("arr")
			var params:List[VarInt] =  List(n, p)
		}

		import VarObj._
		
		new SynthTree()
		.applyTacticBatch(new InitProgTactic(
			name = "TTFF7NoBranching",
			immutableVars = List(arr, N),
			mutableVars = List(r),
			globalInvs = List(N >= c0),
			prog = AssignmentExpr(List((n, c0)), TermBool.TrueT.inv, TermBool.TrueT.inv),
			macros = Nil))
		.applyTacticBatch(new PropagateAssertionsDownSPTactic(12, 12))
	}
}

object PropagateAssertionDownSPTestBase extends DerivationScript {
	val name = "PropagateAssertionDownSPTestBase"

	def apply(): SynthTree = {
	
		object VarObj  {
			val r = VarBool("r")
			val c1 = ConstInt("1")
			val c0 = ConstInt("0")
			val i = VarInt("i")
			val n = VarInt("n")
			val p = VarInt("p")
			val s = VarBool("s")
			val N = VarInt("N")
			val arr = VarArrayBool("arr")
			var params:List[VarInt] =  List(n, p)
		}

		import VarObj._
		
		new SynthTree()
		.applyTacticBatch(new Init4Tactic(
			name = "TTFF7NoBranching",
			immutableVars = List(arr, N),
			mutableVars = List(r),
			globalInvs = List(N >= c0),
			preF = TermBool.TrueT,
			postF = (r equiv
					ExistsTermBool(p, c0 <= p && p <= N,
					ForallTermBool(i, c0 <= i && i < p, arr.select(i))
					&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )),
			macros = Nil))
	}
}
