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


object TTFF50 extends DerivationScript {
    val name = "TTFF50"

	def apply(): SynthTree = {
		//Constants
		val f = VarArrayBool("f") //[0..N)
		val d = VarInt("d") //0 < d
		val N = VarInt("N") // N >= 0
		val immutableVars = f :: N :: Nil
		//GlobalInvs
		val globalInvs = List(N >= c(0), d > c(0))
		//Mutable variables
		val r = VarBool("r")
		val mutableVars = r :: Nil
		//New variables
		val n = VarInt("n")
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")

		val s = VarBool("s")
		//Dummies
		val i = VarInt("i")
		val j = VarInt("j")
		val m = VarInt("m")
		//macros
		val d2 = c(2) * d
		//pre and post
		val pre = TermBool.TrueT
		val post = r equiv ExistsTermBool(m, d2 <= m && m <= N,
								ForallTermBool(i, m - d2 <= i && i < m - d, f.select(i))
								&& ForallTermBool(i, m - d <= i && i < m, !f.select(i)) )

		val ttffxyn = ForallTermBool(i, x <= i && i < y, f.select(i)) && ForallTermBool(i, y <= i && i < n, !f.select(i))

		val ttParams = List(f, x, y)
		val ttBody = ForallTermBool(i, x <= i && i < y, f.select(i))
		val ttMacro = Macro("tt", ttParams, PSBool, ttBody)

		val ffParams = List(f, x, y)
		val ffBody = ForallTermBool(i, x <= i && i < y, !f.select(i))
		val ffMacro = Macro("ff", ffParams, PSBool, ffBody)

		val ttffParams = List(f, x, y, z)
		val ttffBody = ForallTermBool(i, x <= i && i < y, f.select(i)) &&
					ForallTermBool(i, y <= i && i < z, !f.select(i))
		val ttffMacro = Macro("ttff", ttffParams, PSBool, ttffBody)
		val ttffFn = Fn("ttff", List(PSArrayBool, PSInt, PSInt, PSInt), PSBool)

		val postMacro = r equiv  ExistsTermBool(m, d2 <= m && m <= N, FnApp(ttffFn, List(m-d2, m-d, m)).asInstanceOf[TermBool])

		val macros:List[Macro] =   List(ttMacro, ffMacro, ttffMacro)
		//val macros:List[Macro] =   Nil

		val t = TermBool.TrueT

		//derivation start
		val synthTree = new SynthTree()
		synthTree
		//.applyTacticBatch(new AddMacrosTactic(macros))
		.applyTacticBatch(new Init4Tactic("ttff50", immutableVars, mutableVars, globalInvs, pre, postMacro, macros))
		.rtvInPost(N, n, c(0), c(0) <= n && n <= N)
		.stepIntoUnknownProgIdx(1)
		.deleteConjunct(n eqeq N, N - n)
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("false")), (n, c(0)))
		.stepOut
		.stepIntoUnknownProgIdx(1)
//		.applyTacticBatch(new StartIfDerivationTactic(r :: n :: Nil))
//		.applyTacticBatch(new StartGCmdDerivationTactic())
//		//.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
//		.applyTacticBatch(new StepIntoPO())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c(1)) :: Nil))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(120))
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.stepOut
//		.applyTacticBatch(new StepIntoSubFormulaTactic(109))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(12))
//		.applyTacticBatch(new ReplaceFormulaTactic(m <= n || (m eqeq (n + c(1)))))
//		.stepOut
//		.applyTacticBatch(new DistributivityTactic(17))
//		.applyTacticBatch(new RangeSplitTactic(58))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(46))
//		.applyTacticBatch(new ReplaceFormulaTactic(r))
//		.stepOut
//		.applyTacticBatch(new TradingMoveToTermTactic(49, 8))
//		.applyTacticBatch(new OnePointTactic(49))
//		.applyTacticBatch(new AssumePreEquivTactic(s, 52))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(52))
//		.applyTacticBatch(new ReplaceFormulaTactic(s))//
//		.stepOut
//		.applyTacticBatch(new InstantiateMetaTactic((prime(r), r || s) :: Nil))
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))//
//		.stepOut
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))//
//		.stepOut
//		.stepOut
//		.stepOut
//		.applyTacticBatch(new StepIntoProgIdTactic(62))
//		//.stepOut
//		//TODO: Problem: StrengthenInvariant only allowed for while loop containing unknown program
//		//.applyTacticBatch(new StrengthenInvariantTactic( ttffxyn
//		//											:: (y > c(0)).impl(f.select(y - c(1)))
//		//											:: (x > c(0)).impl(f.select(x - c(1)))
//		//											:: Nil))
//		.setCurrentNode(8)
//		.applyTacticBatch(new StepIntoProgIdTactic(30))
//		.applyTacticBatch(new StrengthenInvariantTactic( ttffxyn
//													:: (y > c(0)).impl(f.select(y - c(1)))
//													:: (x > c(0)).impl(!f.select(x - c(1)))
//													:: Nil))
//		.stepOutAll
    }
}

object TTFF50B extends DerivationScript {
    val name = "TTFF50"

	def apply(): SynthTree = {
		//Constants
		val f = VarArrayBool("f") //[0..N)
		val d = VarInt("d") //0 < d
		val N = VarInt("N") // N >= 0
		val immutableVars = f :: N :: Nil
		//GlobalInvs
		val globalInvs = List(N >= c(0), d > c(0))
		//Mutable variables
		val r = VarBool("r")
		//New variables
		val n = VarInt("n")
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val s = VarBool("s")
		val mutableVars = r :: x :: y :: s :: Nil
		//Dummies
		val i = VarInt("i")
		val j = VarInt("j")
		val m = VarInt("m")
		val xn = VarInt("xn")
		val yn = VarInt("yn")

		//macros
		val d2 = c(2) * d
		val macros:List[Macro] =   Nil
		val trueInv = TermBool.TrueT.inv
		//pre and post
		val pre = TermBool.TrueT

		def post(n: TermInt) = r equiv ExistsTermBool(m, d2 <= m && m <= n,
								ForallTermBool(i, m - d2 <= i && i < m - d, f.select(i))
								&& ForallTermBool(i, m - d <= i && i < m, !f.select(i)) )
		val nRange = c(0) <= n && n <=N

		def ttff(x: TermInt, y: TermInt, n: TermInt) =
			ForallTermBool(i, x <= i && i < y, f.select(i)) && ForallTermBool(i, y <= i && i < n, !f.select(i))


		def xyRange(n: TermInt) = c(0) <= x && x <= y && y <= n

		def windowConst(n: TermInt) =
			((y > c(0)).impl(f.select(y - c(1)))) &&
			((x > c(0)).impl(!f.select(x - c(1)))) &&
			xyRange(n)

		def windowConstb(n: TermInt) = {
			val xnewRange = c(0) <= xn && xn < x
			val ynewRange= xn <= yn && yn <= n
			ForallTermBool(xn :: yn :: Nil, xnewRange && ynewRange, !ttff(xn, yn, n)) &&
			xyRange(n)
		}

		def hint(n: TermInt) = (y - x >= d && ((n - y) eqeq d)) impl (
				d2 <= n && ForallTermBool(i, n - d2 <= i && i < m - d, f.select(i)) &&
				ForallTermBool(i, n - d <= i && i < n, !f.select(i))
		)

		val loopInv = post(n) && nRange && ttff(x, y, n) &&  windowConstb(n) && hint(n)

		val midInv = post(n) && nRange && ttff(x,y, n + c(1)) &&  windowConstb(n + c(1)) && (n neqeq N) && hint(n + c(1))
		//val midInv = TermBool.TrueT

		//derivation start
		val synthTree = new SynthTree()
		synthTree
		//.applyTacticBatch(new AddMacrosTactic(macros))
		.applyTacticBatch(new Init4Tactic("ttff50", immutableVars, mutableVars, globalInvs, pre, post(N), macros))
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new AssumeProgTactic(mkCompositionC(trueInv)(
				mkUnknownProgC(trueInv)(2)(loopInv.inv),
				mkUnknownProgC(loopInv.inv)(3)(post(N).inv)
			)(post(N).inv)))
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, ConstBool("false")), (n, c(0)), (x, c(0)), (y, c(0)))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.applyTacticBatch(new IntroWhileTactic(loopInv, n neqeq N))
		.stepIntoUnknownProgIdx(2)
		.applyTacticBatch(new AssumeProgTactic(mkCompositionC((loopInv && (n neqeq N)).inv)(
				mkUnknownProgC((loopInv && (n neqeq N)).inv)(4)(midInv.inv),
				mkUnknownProgC(midInv.inv)(5)(loopInv.inv)
			)(loopInv.inv)))
		.stepIntoUnknownProgIdx(1)//
//		.applyTacticBatch(new AssumeProgTactic(mkIfProgC((loopInv && (n neqeq N)).inv)(
//				GuardedCmd(f.select(n), mkUnknownProgC(UnkTermBool().inv)(5)(UnkTermBool().inv)),
//				GuardedCmd(!f.select(n),mkSkipProg(UnkTermBool().inv, UnkTermBool().inv))
//			)(midInv.inv)))
//		.stepIntoUnknownProgIdx(1)//
		.applyTacticBatch(new AssumeProgTactic(mkIfProgC((loopInv && (n neqeq N)).inv)(
				GuardedCmd(f.select(n) && (y neqeq n), mkUnknownProgC(UnkTermBool().inv)(5)(UnkTermBool().inv)),
				GuardedCmd(f.select(n)&& (y eqeq n),mkUnknownProgC(UnkTermBool().inv)(6)(UnkTermBool().inv)),
				GuardedCmd(!f.select(n), mkSkipProg(UnkTermBool().inv, UnkTermBool().inv))
			)(midInv.inv)))
		.stepIntoUnknownProgIdx(1)////
		.introAssignment((x, n), (y, n + c(1)))
		.stepOut
		.stepIntoUnknownProgIdx(1)////
		.introAssignment((y, n + c(1)))
		.stepOut
		.stepOut
		.stepOut
		.stepIntoUnknownProgIdx(1)////
		.introAssignment((r, TermBool.FalseT), (n, c(0)), (x, c(0)), (y, c(0)))
		.stepOut
		.stepIntoUnknownProgIdx(1)
		.introAssignment((r, r || ((y - x >= d) && ((n - y + c(1)) eqeq d))), (n, n + c(1)))
		//.stepOut//
    }
}

