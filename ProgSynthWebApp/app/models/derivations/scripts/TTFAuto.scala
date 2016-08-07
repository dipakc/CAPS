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

object TTFFAuto extends DerivationScript {
    val name = "TTFFAuto"

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
		val constants = List(arr, N);
		val variables = r :: Nil;
		val globalInvs: List[TermBool] = (N >= c0) :: Nil
		val preF: TermBool = TermBool.TrueT
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
		.applyTacticBatch(new Init4Tactic(name, constants, variables, globaInvs, preF, postF, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, c0, c0 <= n && n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1)) //
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n)) //TODO: Problem in parsing.
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentTactic((r, ConstBool("true")) :: (n, c0) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: Nil))
		.applyTacticBatch(new StepIntoPO())
		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(103))
		.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1)))
		.applyTacticBatch(new DistributivityTactic(15))
		.applyTacticBatch(new RangeSplitTactic(48))
		.applyTacticBatch(new SimplifyAutoTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(26, (i < n) || (i eqeq n )))
		.applyTacticBatch(new DistributivityTactic(29))
		.applyTacticBatch(new RangeSplitTactic(38))
		.applyTacticBatch(new TradingMoveToTermTactic(43, 34))
		.applyTacticBatch(new OnePointTactic(43))//
//		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
//		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new QDistributivityTactic(41))
//		.applyTacticBatch(new UseAssumptionsTactic(40, r))
//		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
//		.setCurrentNodeBatch(9)//branch
//		.applyTacticBatch(new StepIntoProgIdTactic(34)) //TODO: Tactic not applicable error //was 47
//		.applyTacticBatch(new InsertVariableTactic(s, ConstBool("true")))
//		.applyTacticBatch(new StepIntoProgIdTactic(48))
//		.applyTacticBatch(new StrengthenInvariantTactic(EqEqEqTermBool(s, ForallTermBool(i, c0 <= i && i < n, arr.select(i))):: Nil))
//		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
//		.applyTacticBatch(new StartAsgnDerivationTactic(r :: n :: s :: Nil))
//		.applyTacticBatch(new StepIntoPO())
//		.applyTacticBatch(new InstantiateMetaTactic((prime(n), n + c1) :: Nil))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(135))
//		//.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + c1))) //Parse Error
//		.applyTacticBatch(new StepIntoSubFormulaTactic(10))
//		.applyTacticBatch(new ReplaceFormulaTactic((p <= n) || (p eqeq n + c1)))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new DistributivityTactic(15))
//		.applyTacticBatch(new RangeSplitTactic(48))
//		//.applyTacticBatch(new ReplaceSubFormulaTactic(48, p eqeq n + c1 ))//Parse Error
//		.applyTacticBatch(new StepIntoSubFormulaTactic(48))
//		.applyTacticBatch(new ReplaceFormulaTactic(p eqeq n + c1 ))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new OnePointTactic(73))
//		.applyTacticBatch(new EmptyRangeTactic(69))
//		//.applyTacticBatch(new ReplaceSubFormulaTactic(30, (i < n) || (i eqeq n ))) //Parse Error
//		.applyTacticBatch(new StepIntoSubFormulaTactic(30))
//		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n )))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new DistributivityTactic(33))
//		.applyTacticBatch(new RangeSplitTactic(42))
//		.applyTacticBatch(new TradingMoveToTermTactic(47, 38))
//		.applyTacticBatch(new OnePointTactic(47))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(42))
//		.applyTacticBatch(new ReplaceFormulaTactic(!arr.select(n)))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new QDistributivityTactic(41))
//		.applyTacticBatch(new UseAssumptionsTactic(40, r))
//		.applyTacticBatch(new UseAssumptionsTactic(34, TermBool.TrueT))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(21))
//		.applyTacticBatch(new ReplaceFormulaTactic(VarBool("s'")))
//		.applyTacticBatch(new StepOutTactic())
//		//.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n)))//Parse Error
//		.applyTacticBatch(new StepIntoSubFormulaTactic(20))
//		.applyTacticBatch(new ReplaceFormulaTactic((i < n) || (i eqeq n)))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new DistributivityTactic(23))
//		.applyTacticBatch(new RangeSplitTactic(31))
//		.applyTacticBatch(new UseAssumptionsTactic(23, s))
//		.applyTacticBatch(new TradingMoveToTermTactic(24, 16))
//		.applyTacticBatch(new OnePointTactic(24))
//		.applyTacticBatch(new UseAssumptionsTactic(15, TermBool.TrueT))
//		.applyTacticBatch(new InstantiateMetaTactic((prime(s), s && arr.select(n)) :: Nil))
//		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
//		.applyTacticBatch(new InstantiateMetaTactic((prime(r), (!arr.select(n) && r) || (s && arr.select(n))) :: Nil))
//		.applyTacticBatch(new UseAssumptionsTactic(25, TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.stepOutAll

		//.stepOutAll //TODO: Not Implemented
		//#############################
		return synthTree1
		synthTree1
		//#############################
	}
}
