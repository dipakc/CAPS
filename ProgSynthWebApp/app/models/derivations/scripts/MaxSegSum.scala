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
import progsynth.proofobligations.POGenerator
import progsynth.provers.ProgramAnnPOProver

//Direct assignment.
//TODO: Separate the Min quantifier dummies by commas in the GUI.
//TODO: Exception in provePOs
//Top down displayid numbering
object MaxSegSum extends DerivationScript {
    val name = "MaxSegSum"

	def apply(): SynthTree = {
		val s = VarInt("s")
		val arr = VarArrayInt("arr")
		val p = VarInt("p")
		val i = VarInt("i")
		val q = VarInt("q")
		val r = VarInt("r")
		val N = VarInt("N")
		val n = VarInt("n")
		val one = ConstInt("1")
		val zero = ConstInt("0")
		val immutableVars = arr :: N :: Nil
		val mutableVars = r :: Nil
		val globalInvs = (N >= 1) :: Nil

		val sumParams = List(p, q)
		val sumBody = PlusQTermInt(i, p <= i < q, arr(i))
		//TODO: Ensure that macro names do not collide with quantifier keywords. eg SUM
		//Sum.p.q = (SUM i: p <= i < q: arr[i])
		val sumMacro = Macro(
				name = "sum",
				params = sumParams,
				retType = PSBool,
				body = sumBody)
		val sumFn = Fn("sum", List(PSInt, PSInt), PSInt)

		val pre = TermBool.TrueT
		// r = ( MAX p, q : 0 <= p <= q <= N: sum.p.q)

		val rawTerm = sumBody
		val macroTerm = FnApp(sumFn, List(p, q)).asInstanceOf[TermInt]
		val post =  r eqeq MaxQTermInt(
				dummies = List(p,q),
				range =  0 <= p <= q <= N,
				term = rawTerm) //TODO sumFn is not visible in GUI

		val macros = List(sumMacro)
		val synthTree = new SynthTree()
		synthTree
		//.applyTacticBatch(new Init4Tactic("MaxSegSum", immutableVars, mutableVars, globalInvs, pre, post, macros))
		.applyTacticBatch(new Init4Tactic("MaxSegSum", immutableVars, mutableVars, globalInvs, pre, post, Nil))
		.applyTacticBatch(new RTVInPostTactic(N, n, zero, 0 <= n <= N))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new DeleteConjunctTactic(n eqeq N, N - n))
		//TODO: Derive unknown program to establish the inv at the start of the loop.
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
        .applyTacticBatch(new IntroAssignmentTactic((r, zero) :: Nil))
//		.applyTacticBatch(new StepIntoBATactic(r :: Nil))
//		.applyTacticBatch(new StepIntoSubFormulaTactic(31))
//		.applyTacticBatch(new ReplaceSubFormulaTactic(14, (p eqeq 0) && (q eqeq 0)))
//		.applyTacticBatch(new SplitoutBoundVariableTactic(23, p))
//		.applyTacticBatch(new OnePointTactic(23))
//		.applyTacticBatch(new OnePointTactic(18))
//		.applyTacticBatch(new EmptyRangeTactic(13))
//		.applyTacticBatch(new InstantiateMetaTactic((prime(r), c(0)) :: Nil))
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
//		.applyTacticBatch(new StepOutTactic())
//		.applyTacticBatch(new StepOutTactic())
        .applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new IntroAssignmentEndTactic(List((n, n + 1))))
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(r :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(44))// (=>, 2)
		.applyTacticBatch(new StepIntoSubFormulaTactic(33)) // 0 <= n + 1 /\ n + 1 <= N
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new SimplifyTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(15, (q <= n) || (q eqeq n + 1))) //q <= n + 1
		.applyTacticBatch(new DistributivityTactic(7)) //range
		.applyTacticBatch(new RangeSplitTactic(4)) //MAX term
		.applyTacticBatch(new StepIntoSubFormulaTactic(39))// p <= q second instance. Not working
		.applyTacticBatch(new ReplaceFormulaTactic(p <= n + 1))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new SplitoutBoundVariableTactic(31, p))// MAX second instance
		.applyTacticBatch(new OnePointTactic(42)) //MAX third instance
		.applyTacticBatch(new StepIntoSubFormulaTactic(5)) //MAX first instance
		.applyTacticBatch(new ReplaceSubTermTactic(2, r))//MAX first instance (whole term)
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new AssumePreTactic(List(s), s eqeq MaxQTermInt(
				dummies = List(p),
				range =  0 <= p <= n + 1,
				term = PlusQTermInt(
						dummies = List(i),
						range = p <= i && i < n + 1,
						term = arr.select(i)))))
		.applyTacticBatch(new ReplaceSubTermTactic(6, s)) //MAX
		.applyTacticBatch(new InstantiateMetaTactic(List((prime(r), FnApp(MaxIntFn, List(r, s))))))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new CollapseCompositionsTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoSubProgTactic(107))//875 While Prog
		.applyTacticBatch(new WhileStrInvSPTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new CollapseCompositionsTactic())
		.applyTacticBatch(new PropagateAssertionsDownSPTactic(2, 192))//old: 17, 274 // n = 0 to first Assume
		.applyTacticBatch(new StepIntoSubProgTactic(95))//119 //1st Assume
		.applyTacticBatch(new IntroAssignmentTactic((s, c(0)) :: Nil))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepIntoUnknownProgIdxTactic(1))
		.applyTacticBatch(new StepIntoBATactic(s :: Nil))
		.applyTacticBatch(new StepIntoSubFormulaTactic(68)) //consequent
		.applyTacticBatch(new ReplaceSubFormulaTactic(3, TermBool.TrueT)) // (and, 1)
		.applyTacticBatch(new SimplifyTactic())
		.applyTacticBatch(new ReplaceSubFormulaTactic(10, (p <= n) || (p eqeq n + 1))) // p <= n + 1
		.applyTacticBatch(new DistributivityTactic(6))// Range
		.applyTacticBatch(new RangeSplitTactic(4)) // MAX
		.applyTacticBatch(new ReplaceSubFormulaTactic(30, p eqeq n + 1)) // MAX instance 2 > range
		.applyTacticBatch(new OnePointTactic(28)) // MAX instance 2
		.applyTacticBatch(new EmptyRangeTactic(28)) //SUM
		.applyTacticBatch(new ReplaceSubFormulaTactic(20, (i < n) || (i eqeq n))) // i <= n + 1
		.applyTacticBatch(new DistributivityTactic(16)) //SUM range
		.applyTacticBatch(new RangeSplitTactic(14)) //SUM
		//.applyTacticBatch(new ReplaceSubFormulaTactic(29, i eqeq n))
		.applyTacticBatch(new StepIntoSubFormulaTactic(29)) //SUM instance 2 > range
		.applyTacticBatch(new ReplaceFormulaTactic(i eqeq n))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new OnePointTactic(27))// SUM instance 2
		.applyTacticBatch(new QDistributivityTactic(5)) //MAX
		.applyTacticBatch(new ReplaceSubTermTactic(9, s)) //MAX
		.applyTacticBatch(new InstantiateMetaTactic(List((prime(s), arr.select(n) + s max 0))))
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new ReplaceFormulaTactic(TermBool.TrueT))
		.applyTacticBatch(new StepOutTactic())
		.applyTacticBatch(new StepOutTactic())
    }
}

