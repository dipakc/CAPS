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
import progsynth.printers.XHTMLPrinters2

//Empty Tree.
object initTTFFState0 extends DerivationScript {
    val name = "initTTFFState0"

	def apply(): SynthTree = {
		//val synthTreeObject = progsynth.synthesisold.PSSynthTree
		//val prototypeImpl = progsynth.synthesisnew.Expt7
		import varObj._
		val ip = mkFunctionProg2(
				name = "TTFF",
				params =  List(arr, N),
				retVar = VarBool("r"),
				annProg = mkUnknownProg (
						pre = (N >= c0).inv,
						upid = 0,
						//post = (r equiv ForallTermBool(i, c0 <= i && i < N - c1 impl arr.select(i) || !arr.select(i + c1))).inv(r)
						post = (r equiv ForallTermBool(i, c0 <= i && i < N - c1 impl arr.select(i) || !arr.select(i + c1))).inv(r)
				), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = r equiv ForallTermBool(i, c0 <= i && i < N - c1 impl arr.select(i) || !arr.select(i + c1))

		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF.html""")
		synthTree
		////.initTactic(name, params, retVar, preF, postF)
		//.initTactic2(ip)
		//.stepIntoUnknownProgIdx(1)
		//.retValTactic(Some(ConstBool("false")))
		//.stepIntoUnknownProgIdx(1)
		//.rtvInPost(N, n, c0, c0 <= n && n <= N)
		//.stepIntoUnknownProgIdx(1)
		//.deleteConjunct(n eqeq N, N - n)
		//.stepIntoUnknownProgIdx(1)
		//.introAssignment((r, ConstBool("true")))
		//.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N - c1, Not(n eqeq N - c1)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N - c1))
		//.stepOutAll
		//.dumpState
		////
		//synthTree.treeToXhtml |> xmlToHtml
    }
}
object initTTFFState1 extends DerivationScript {
    val name = "initTTFFState1"

	def apply(): SynthTree = {

		//val synthTreeObject = progsynth.synthesisold.PSSynthTree
		//val prototypeImpl = progsynth.synthesisnew.Expt7
		import varObj._/////
		val ip = mkFunctionProg2(
				name = "TTFF",
				params =  List(arr, N),
				retVar = VarBool("r"),
				annProg = mkUnknownProg (
						pre = (N >= c0).inv,
						upid = 0,
						post = (r equiv ForallTermBool(i, c0 <= i && i < N - c1 impl arr.select(i) || !arr.select(i + c1))).inv(r)
				), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = r equiv ForallTermBool(i, c0 <= i && i < N - c1 impl arr.select(i) || !arr.select(i + c1))
		//val synthTree = new SynthTree(ip)
		//synthTree.setOutputFile("""d:\tmp\TTFF.html""")
		//synthTree
		//.stepIntoUnknownProgIdx(1)
		//.retValTactic(Some(ConstBool("false")))
		//.stepIntoUnknownProgIdx(1)
		//.rtvInPost(N - c1, n, c0, c0 <= n && n <= N - c1)
		//.stepIntoUnknownProgIdx(1)
		//.deleteConjunct(n eqeq N - c1, N - n)
		//.stepIntoUnknownProgIdx(1)
		//.introAssignment((r, ConstBool("true")))
		//.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N - c1, Not(n eqeq N - c1)),
		//		consequents = List(c0 <= n + c1, n + c1 <= N - c1))
		//.stepOutAll
		//.dumpState
		//synthTree.treeToXhtml |> xmlToHtml
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF.html""")
		synthTree
		//.initTactic(name, params, retVar, preF, postF)
		.initTactic2(ip)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N - c1, Not(n eqeq N - c1)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N - c1))
		//.stepOutAll
		//.dumpState
		//
		//synthTree.treeToXhtml |> xmlToHtml
    }
}

object initTTFFState2 extends DerivationScript {
    val name = "initTTFFState2"

	def apply(): SynthTree = {

		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) ))
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		synthTree
		.initTactic(name, params, retVar, preF, postF)
		.initTactic2(ip2)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)//
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N, !(n eqeq N)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N))
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.stepOutAll
		//.dumpState
		//
		//synthTree.treeToXhtml |> xmlToHtml
    }
}


//Strengthening the Invariant
object initTTFFState3 extends DerivationScript {
val name = "initTTFFState3"

def apply(): SynthTree = {

		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) ))
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		val synthTree1 =
		synthTree
		//.initTactic(name, params, retVar, preF, postF)
		.initTactic2(ip2)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)//
		val btNode = synthTree1.curNode
		synthTree1
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0))
		.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N, Not(n eqeq N)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N))
		//.backTrack()
		//synthTree2.curNode = btNode
		//synthTree2
		//.stepIntoProg("While")
		//.insertVariablePre(s, ConstBool("true"))
		//.strengthenInvariant((s equiv ForallTermBool(i, c0 <= i && i < n impl arr.select(i))))
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.stepOutAll
		//.dumpState
		//
		//synthTree.treeToXhtml |> xmlToHtml
    }
}
//Directly strengthened Invariant
object initTTFFState4 extends DerivationScript {
    val name = "initTTFFState4"

	def apply(): SynthTree = {
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) ))
		val loopInv = (r equiv
						ExistsTermBool(p, c0 <= p && p <= n
						&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
						&& ForallTermBool(i, p <= i && i < n impl !arr.select(i))))
		val nbound = c0 <= n && n <= N
		val sLoopInv = loopInv && nbound && (s equiv ForallTermBool(i, c0 <= i && i < n impl arr.select(i)))
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		synthTree
		//.initTactic(name, params, retVar, preF, postF)
		.initTactic2(ip2)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.insertVariable(s, ConstBool("true"))
		.stepIntoUnknownProgIdx(1)
		.introWhile(sLoopInv, n neqeq N)//TODO: add bound
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0), (s, ConstBool("true")))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.introAssignment((s, s && arr.select(n)), (r, (r && !arr.select(n)) || s ), (n, n + c1))
		.stepOutAll
		//.deleteConjunct(n eqeq N, N - n)//
		//.stepIntoUnknownProgIdx(1)
		//.introAssignment((r, ConstBool("true")), (n, c0))
		//.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N, Not(n eqeq N)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N))
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.stepOutAll
		//.dumpState
		//
		//synthTree.treeToXhtml |> xmlToHtml
    }
}

//Branching Demo
object initTTFFState5 extends DerivationScript {
    val name = "initTTFFState5"

	def apply(): SynthTree = {
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) ))
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		val synthTree1 =
		synthTree
		//.initTactic(name, params, retVar, preF, postF)
		.initTactic2(ip2)
		.stepIntoUnknownProgIdx(1)
		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)//
		val btNode = synthTree1.curNode
		val synthTree2 = synthTree1
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0))
		.stepOut
		synthTree2.curNode = btNode
		val synthTree3 = synthTree2
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("false")), (n, c0))
		val btNode2 = synthTree3.curNode
		val synthTree4 = synthTree3
		.stepOut
		.stepOut
		val synthTree5 = synthTree4
		synthTree4.curNode = btNode2
		synthTree4
		.stepOut
		.stepOut
		//.stepIntoUnknownProgIdx(1)
		//.assignmentDerivation(r :: n :: Nil)
		//.instantiateMeta((prime(n), n + c1) :: Nil)
		//.applyHint(antecedents = List(c0 <= n, n <= N, Not(n eqeq N)),
		// 			consequents = List(c0 <= n + c1, n + c1 <= N))
		//.backTrack()
		//synthTree2.curNode = btNode
		//synthTree2
		//.stepIntoProg("While")
		//.insertVariablePre(s, ConstBool("true"))
		//.strengthenInvariant((s equiv ForallTermBool(i, c0 <= i && i < n impl arr.select(i))))
		//.instantiateMeta((prime(r), r && (arr.select(n - c1) || !arr.select(n))) :: Nil)
		//.stepOutAll
		//.dumpState
		//
		//synthTree.treeToXhtml |> xmlToHtml
    }
}


object initTTFFState6 extends DerivationScript {
    val name = "initTTFFState6"

	def apply(): SynthTree = {
		val xxx = 0
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N,
								ForallTermBool(i, c0 <= i && i < p, arr.select(i))
								&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )) /*&& N >= c0*/
		val globaInvs = List(N >= c0)
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		val synthTree1 =
		synthTree
		synthTree //

		.initTactic3(name, params, retVar, preF, postF, globaInvs) // UI checked

		//.initTactic2(ip2)
		.stepIntoUnknownProgIdx(1)
		synthTree1

		.retValTactic(Some(ConstBool("false")))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(103))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse ERROR p is not added to context
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 )) //Parse ERROR
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1))
		.stepOut
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse ERROR
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new VerifiedTransformationTactic(!arr.select(n), EquivBoolFn))
		.stepOut
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.setCurrentNode(11)//branch
		.applyTacticBatch(new StepIntoProgIdTactic(47))

		.applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
		.applyTacticBatch(new StepIntoProgIdTactic(61))
		.applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: s :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(135))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1 ))
		.stepOut
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.stepOut

		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new VerifiedTransformationTactic(!arr.select(n), EquivBoolFn))
		.stepOut
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.applyTacticBatch(new StepIntoSubFormulaTactic(21))
		.applyTacticBatch(new VerifiedTransformationTactic(VarBool("s'"), EquivBoolFn))
		.stepOut
		//.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n)))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(20))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n)))
		.stepOut

		.applyTacticBatch(new DistributivityTactic(23))
		.applyTacticBatch(new RangeSplitTactic(31))
		.applyTacticBatch(new UseAssumptionsTactic(23, s))
		.applyTacticBatch(new TradingMoveToTermTactic(24, 16))
		.applyTacticBatch(new OnePointTactic(24))
		.applyTacticBatch(new UseAssumptionsTactic(15, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.stepOut
		.applyTacticBatch(new VerifiedTransformationTactic(TermBool.TrueT, EquivBoolFn))
		.stepOut
		.stepOut
		.stepOutAll //Not Implemented
		/*
		*/
    }
}

object TTFF7 extends DerivationScript {
    val name = "TTFF7"

	def apply(): SynthTree = {

		val xxx = 0
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "Exe4k71TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N,
								ForallTermBool(i, c0 <= i && i < p, arr.select(i))
								&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )) /*&& N >= c0*/
		val globaInvs = List(N >= c0)
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		//SynthNode.resetCnt //Reset counter
		val synthTree1 =
		synthTree
		.applyTacticBatch(new InitTactic3(name, params, retVar, preF, postF, globaInvs))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(103))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1))//works fine
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n )))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.setCurrentNodeBatch(9)//branch
		.applyTacticBatch(new StepIntoProgIdTactic(34)) //TODO: Tactic not applicable error //was 47
		.applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
		.applyTacticBatch(new StepIntoProgIdTactic(48))
		.applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: s :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(135))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1 ))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.applyTacticBatch(new StepIntoSubFormulaTactic(21))
		.applyTacticBatch(new ReplaceFormulaTactic(VarBool("s'")))
		.applyTacticBatch(new StepOutTactic())
		//.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n)))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(20))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(23))
		.applyTacticBatch(new RangeSplitTactic(31))
		.applyTacticBatch(new UseAssumptionsTactic(23, s))
		.applyTacticBatch(new TradingMoveToTermTactic(24, 16))
		.applyTacticBatch(new OnePointTactic(24))
		.applyTacticBatch(new UseAssumptionsTactic(15, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOutAll

		//.stepOutAll //TODO: Not Implemented
		//#############################
		return synthTree1
		synthTree1
		//#############################
    }
}

object TTFF71 extends DerivationScript {
    val name = "TTFF71"

	def apply(): SynthTree = {

		val xxx = 0
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "Exe4k71TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N,
								ForallTermBool(i, c0 <= i && i < p, arr.select(i))
								&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )) /*&& N >= c0*/
		val globaInvs = List(N >= c0)
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		//SynthNode.resetCnt //Reset counter
		val synthTree1 =
		synthTree
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
		.applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(103))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1))//works fine
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n )))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.setCurrentNodeBatch(9)//branch
		.applyTacticBatch(new StepIntoProgIdTactic(34)) //TODO: Tactic not applicable error //was 47
		.applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
		.applyTacticBatch(new StepIntoProgIdTactic(48))
		.applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: s :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(135))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1 ))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.applyTacticBatch(new StepIntoSubFormulaTactic(21))
		.applyTacticBatch(new ReplaceFormulaTactic(VarBool("s'")))
		.applyTacticBatch(new StepOutTactic())
		//.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n)))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(20))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n)))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new DistributivityTactic(23))
		.applyTacticBatch(new RangeSplitTactic(31))
		.applyTacticBatch(new UseAssumptionsTactic(23, s))
		.applyTacticBatch(new TradingMoveToTermTactic(24, 16))
		.applyTacticBatch(new OnePointTactic(24))
		.applyTacticBatch(new UseAssumptionsTactic(15, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOutAll

		//.stepOutAll //TODO: Not Implemented
		//#############################
		return synthTree1
		synthTree1
		//#############################
    }
}

object TTFF7NoBranching extends DerivationScript {
    val name = "TTFF7NoBranching"

	def apply(): SynthTree = {

		import varObj._

		val synthTree = new SynthTree()
		val synthTree1 =
		synthTree
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
        .applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        //.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
        //.applyTacticBatch(new StepIntoPO())
        .applyTacticBatch(new StepIntoBATactic(r :: n :: Nil))
        .applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
        .applyTacticBatch(new StepIntoSubFormulaTactic(53)) //consequent
        //.applyTacticBatch(new ReplaceSubFormulaTactic(11, (p <= n) || (p eqeq n + c1)))
        .applyTacticBatch(new StepIntoSubFormulaTactic(11)) // p <= n + 1
        .applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
        .applyTacticBatch(new StepOutTactic())

        .applyTacticBatch(new DistributivityTactic(7)) //exists > range

        .applyTacticBatch(new RangeSplitTactic(5)) //exists

        .applyTacticBatch(new ReplaceSubFormulaTactic(45, p eqeq n + c1 ))
        .applyTacticBatch(new OnePointTactic(43)) //exists 2nd instance

        .applyTacticBatch(new EmptyRangeTactic(58)) //forall 4th instance.

        .applyTacticBatch(new ReplaceSubFormulaTactic(34, (i < n) || (i eqeq n )))

        .applyTacticBatch(new DistributivityTactic(30)) //forall 2nd instance > range

        .applyTacticBatch(new RangeSplitTactic(28)) //forall 2nd instance

        .applyTacticBatch(new TradingMoveToTermTactic(42, 45)) //forall 3rd instance,  forall 3rd instance > range > 1st conjunct

        .applyTacticBatch(new OnePointTactic(42)) //forall 3rd instance

        .applyTacticBatch(new StepIntoSubFormulaTactic(42)) //p <= n => \neg arr[n]
        .applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
        .applyTacticBatch(new StepOutTactic()) //TODO: in place replacement not working.

        .applyTacticBatch(new QDistributivityTactic(6)) //exists

        .applyTacticBatch(new ReplaceSubFormulaTactic(11, r)) //exists
        .applyTacticBatch(new SimplifyAutoTactic())

        .applyTacticBatch(new AssumePreTactic(List(s), s equiv ForallTermBool(i, c0 <= i && i < n + c(1), arr.select(i))))

        .applyTacticBatch(new ReplaceSubFormulaTactic(11, s)) //forall

        .applyTacticBatch(new InstantiateMetaTactic(List((prime(r), (!arr.select(n) && r) || s))))

        .applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))

        .applyTacticBatch(new StepOutTactic())

        .applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))

        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())

        .applyTacticBatch(new StepIntoSubProgTactic(179))//910 While
        .applyTacticBatch(new WhileStrInvSPTactic())
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new IntroAssignmentTactic((s, s && arr.select(n)) :: Nil))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())

        .applyTacticBatch(new PropagateAssertionsDownSPTactic(20, 284)) //65, 291 //assignment(r, n := true, 0) to Assume. //TODO: add select buttons in the GUI.
        .applyTacticBatch(new StepIntoSubProgTactic(160)) //91 //assume
        .applyTacticBatch(new IntroAssignmentTactic((s, TermBool.TrueT) :: Nil))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())

		//.stepOutAll //TODO: Not Implemented
		//#############################
		//val pann = synthTree1.curNode.nodeObj.asInstanceOf[ProgramAnn]
		//XHTMLPrinters2.programAnnToHtmlMain(pann)
		return synthTree1
		synthTree1
		//#############################
    }
}

object TTFF8 extends DerivationScript {
    val name = "TTFF8"

	def apply(): SynthTree = {

		val xxx = 0
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N,
								ForallTermBool(i, c0 <= i && i < p, arr.select(i))
								&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )) /*&& N >= c0*/
		val globaInvs = List(N >= c0)
		val mutableVars = r :: Nil
		val immutableVars = arr :: N :: Nil
		val synthTree = new SynthTree()
		//synthTree.setOutputFile("""d:\tmp\TTFF2.html""")
		val synthTree1 =
		synthTree
		//.initTactic3(name, params, retVar, preF, postF, globaInvs)
		//.stepIntoUnknownProgIdx(1)
		//.retValTactic(Some(ConstBool("false")))
		.applyTacticBatch(new Init4Tactic( name,  immutableVars, mutableVars, Nil, preF, postF, Nil))
		.stepIntoUnknownProgIdx(1)
		.rtvInPost(N, n, c0, c0 <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("true")), (n, c0))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(103))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1))//works fine
		.stepOut
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n )))
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.stepOut
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.setCurrentNode(9)//branch #####################
		.applyTacticBatch(new StepIntoProgIdTactic(47)) //TODO: Tactic not applicable error
		.applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
		.applyTacticBatch(new StepIntoProgIdTactic(61))
		.applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: s :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(135))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1 ))
		.stepOut
		.applyTacticBatch(new OnePointTactic(73))
		.applyTacticBatch(new EmptyRangeTactic(69))
		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(33))
		.applyTacticBatch(new RangeSplitTactic(42))
		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
		.applyTacticBatch(new OnePointTactic(47))
		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
		.stepOut
		.applyTacticBatch(new QDistributivityTactic(41))
		.applyTacticBatch(new UseAssumptionsTactic(40, r))
		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
		.applyTacticBatch(new StepIntoSubFormulaTactic(21))
		.applyTacticBatch(new ReplaceFormulaTactic(VarBool("s'")))
		.stepOut
		//.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n)))//Parse Error
		.applyTacticBatch(new StepIntoSubFormulaTactic(20))
		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n)))
		.stepOut
		.applyTacticBatch(new DistributivityTactic(23))
		.applyTacticBatch(new RangeSplitTactic(31))
		.applyTacticBatch(new UseAssumptionsTactic(23, s))
		.applyTacticBatch(new TradingMoveToTermTactic(24, 16))
		.applyTacticBatch(new OnePointTactic(24))
		.applyTacticBatch(new UseAssumptionsTactic(15, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
		.stepOut
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.stepOut
		.stepOut
		.stepOutAll //TODO: Not Implemented
		//#############################
		return synthTree1
		synthTree1
		//#############################
    }
}

object TTFF9 extends DerivationScript {
    val name = "TTFF9"

	def apply(): SynthTree = {

		val xxx = 0
		import varObj._
		val ip2 = mkFunctionProg2(
			name = "TTFF9",
			params =  List(arr, N),
			retVar = VarBool("r"),
			annProg = mkUnknownProg (
					pre = (N >= c0).inv,
					upid = 0,
					post = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N
								&& ForallTermBool(i, c0 <= i && i < p impl arr.select(i))
								&& ForallTermBool(i, p <= i && i < N impl !arr.select(i)) )).inv(r)
			), Nil  	)
		val name = "TTFF9"
		val params = List(arr, N);
		val retVar = r;
		val preF: TermBool = (N >= c0)
		val postF: TermBool = (r equiv
								ExistsTermBool(p, c0 <= p && p <= N,
								ForallTermBool(i, c0 <= i && i < p, arr.select(i))
								&& ForallTermBool(i, p <= i && i < N, !arr.select(i)) )) /*&& N >= c0*/
		val globaInvs = List(N >= c0)
		val mutableVars = r :: Nil
		val immutableVars = arr :: N :: Nil

		val synthTree = new SynthTree()
		synthTree
        .applyTacticBatch(new Init4Tactic( name, immutableVars, mutableVars, Nil, preF, postF, Nil))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new StepIntoBATactic(r :: n :: Nil))
        .applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
        .applyTacticBatch(new StepIntoSubFormulaTactic(53)) //consequent
        .applyTacticBatch(new ReplaceSubFormulaTactic(44, TermBool.TrueT)) // 0 <= n <= N
        .applyTacticBatch(new ReplaceSubFormulaTactic(11, (p <= n) || (p eqeq n + c1))) // p <= n + 1
        .applyTacticBatch(new DistributivityTactic(7)) // Range of exists
        .applyTacticBatch(new RangeSplitTactic(5)) // exists
        .applyTacticBatch(new StepIntoSubFormulaTactic(43)) //exists second instance
        .applyTacticBatch(new ReplaceSubFormulaTactic(4, p eqeq n + c1))//0 <= p /\ p = n + 1
        .applyTacticBatch(new OnePointTactic(2)) // whole formula
        .applyTacticBatch(new EmptyRangeTactic(17)) // (and , 2)
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepIntoSubFormulaTactic(28)) // forall 2nd instance
        .applyTacticBatch(new ReplaceSubFormulaTactic(8, (i < n) || (i eqeq n))) // i < n + 1
        .applyTacticBatch(new DistributivityTactic(4))
        .applyTacticBatch(new RangeSplitTactic(2)) //whole formula
        .applyTacticBatch(new ReplaceSubFormulaTactic(18, i eqeq n)) //p <= i /\ i = n
        .applyTacticBatch(new OnePointTactic(16)) //forall second instance.
        .applyTacticBatch(new StepOutTactic()) //
        .applyTacticBatch(new QDistributivityTactic(6)) //exists
        .applyTacticBatch(new ReplaceSubFormulaTactic(11, r)) //exists
        .setCurrentNodeBatch(9)//branch #################################
        .applyTacticBatch(new StepIntoSubProgTactic(203)) //314 While Prog
        .applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
        .applyTacticBatch(new StepIntoSubProgTactic(401))//492 While Prog
        .applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
        .applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new StepIntoBATactic(r :: n :: s :: Nil))
        .applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
        .applyTacticBatch(new StepIntoSubFormulaTactic(68))// Consequent
        .applyTacticBatch(new ReplaceSubFormulaTactic(12, (p <= n) || (p eqeq n + c1))) // p <= n + 1
        .applyTacticBatch(new DistributivityTactic(8)) //exists range
        .applyTacticBatch(new RangeSplitTactic(6)) //exists
        .applyTacticBatch(new ReplaceSubFormulaTactic(46, p eqeq n + c1 )) //exists 2nd instance > range
        .applyTacticBatch(new OnePointTactic(44)) //exists second instance
        .applyTacticBatch(new EmptyRangeTactic(59)) //forall with range n+1 <= i < n + 1
        .applyTacticBatch(new ReplaceSubFormulaTactic(35, (i < n) || (i eqeq n )))// i < n + 1 first instance
        .applyTacticBatch(new DistributivityTactic(31)) //forall second instance > range.
        .applyTacticBatch(new RangeSplitTactic(29))// forall second instance
        .applyTacticBatch(new TradingMoveToTermTactic(43, 46)) //forall 3rd instance , {forall 3rd instance > range > (/\ 1)}
        .applyTacticBatch(new OnePointTactic(43)) //forall 3rd instance.
        .applyTacticBatch(new StepIntoSubFormulaTactic(43)) // p <= n => \neg arr[n]
        .applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new QDistributivityTactic(7)) //exists
        .applyTacticBatch(new ReplaceSubFormulaTactic(12, r)) //exists
        .applyTacticBatch(new ReplaceSubFormulaTactic(27, TermBool.TrueT)) // 0 <= n + 1 /\ n + 1 <= N
        .applyTacticBatch(new StepIntoSubFormulaTactic(13)) //forall 1st instance.
        //###############
        .applyTacticBatch(new ReplaceFormulaTactic(VarBool("s'")))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new ReplaceSubFormulaTactic(23, (i < n) || (i eqeq n))) // i < n + 1
        .applyTacticBatch(new DistributivityTactic(19)) //forall > range
        .applyTacticBatch(new RangeSplitTactic(17)) // forall
        .applyTacticBatch(new ReplaceSubFormulaTactic(18, s)) //forall 1st instance.
        .applyTacticBatch(new TradingMoveToTermTactic(19, 22)) //forall, forall > range > 1st conjunct
        .applyTacticBatch(new OnePointTactic(17)) //forall
        .applyTacticBatch(new ReplaceSubFormulaTactic(18, TermBool.TrueT))//  0 <= n
        .applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
        .applyTacticBatch(new ReplaceSubFormulaTactic(17, TermBool.TrueT)) // second conjunct
        .applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
        .applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())

    }
}
