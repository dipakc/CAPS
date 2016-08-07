package tests.synthesis
/*
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import progsynth.types._
import progsynth.types.Types._
import progsynth.config.AppConfig
import progsynth.printers.XHTMLPrintersOld._
import progsynth.utils.PSUtils
import progsynth.proofobligations.POGenerator
import progsynth.proofobligations.POZ3Prover
import org.junit.Assert
import org.scalatest.matchers.ShouldMatchers
import progsynth.logger.XMLLogWriter
import tests.utils.PSPluginTesterDbg
import tests.utils.CompareFiles._
import progsynth.synthesisold.RetValMainTactic
import progsynth.synthesisold.PSTacticResult
import progsynth.synthesisold.RCVAllComb
import progsynth.synthesisold.ProgContext
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class RCVTacticTest extends FunSuite with ShouldMatchers {

	ignore("RetValTacticTest") {
		val inputUnkProg = {
			val arr_1 = VarArrayInt("arr")
			val N_2 = VarInt("N")
			val r_3 = VarInt("r")
			val c0_4 = ConstInt("0")
			val expr_5 = ExprProg(c0_4)
			val atm_6 = Atom(Pred("$greater", List(N_2, c0_4)))
			val inv_7 = Invariant(None, atm_6, None)
			val unk_8 = Unknown[Pred]()
			val inv_9 = Invariant(None, unk_8, None)
			val v_10 = expr_5.setPrePost(inv_7, inv_9)
			val varDef_11 = VarDefProg(r_3, expr_5)
			val atm_12 = Atom(Pred("$eq$eq", List(r_3, c0_4)))
			val and_13 = And(atm_6, atm_12)
			val inv_14 = Invariant(None, and_13, None)
			val v_15 = varDef_11.setPrePost(inv_7, inv_14)
			val unk_16 = UnknownProg(1)
			val c1_17 = ConstInt("1")
			val atm_18 = Atom(Pred("$eq$eq", List(r_3, c1_17)))
			val i_19 = VarInt("i")
			val atm_20 = Atom(Pred("$less$eq", List(c0_4, i_19)))
			val atm_21 = Atom(Pred("$less", List(i_19, N_2)))
			val and_22 = And(atm_20, atm_21)
			val arrsel_23 = ArrSelectInt(arr_1, i_19)
			val atm_24 = Atom(Pred("$greater", List(arrsel_23, c0_4)))
			val and_25 = And(and_22, atm_24)
			val exists_26 = Exists(i_19, and_25)
			val iff_27 = Iff(atm_18, exists_26)
			val inv_28 = Invariant(None, iff_27, None)
			val v_29 = unk_16.setPrePost(inv_14, inv_28)
			val expr_30 = ExprProg(r_3)
			val inv_31 = Invariant(None, iff_27, Some(r_3))
			val v_32 = expr_30.setPrePost(inv_28, inv_31)
			val compprog_33 = Composition(List(varDef_11, unk_16, expr_30))
			val v_34 = compprog_33.setPrePost(inv_7, inv_31)
			val fun_35 = FunctionProg("existsPositiveElement1", List(arr_1, N_2), PSInt, compprog_33)
			val v_36 = fun_35.setPrePost(inv_9, inv_9)
			unk_16
		}
		val expectedProg = {
			val unk_1 = UnknownProg(0)
			val N_2 = VarInt("N")
			val c0_3 = ConstInt("0")
			val atm_4 = Atom(Pred("$greater", List(N_2, c0_3)))
			val r_5 = VarInt("r")
			val atm_6 = Atom(Pred("$eq$eq", List(r_5, c0_3)))
			val and_7 = And(atm_4, atm_6)
			val inv_8 = Invariant(None, and_7, None)
			val c1_9 = ConstInt("1")
			val atm_10 = Atom(Pred("$eq$eq", List(r_5, c1_9)))
			val i_11 = VarInt("i")
			val atm_12 = Atom(Pred("$less$eq", List(c0_3, i_11)))
			val n1_13 = VarInt("n1")
			val atm_14 = Atom(Pred("$less", List(i_11, n1_13)))
			val and_15 = And(atm_12, atm_14)
			val arr_16 = VarArrayInt("arr")
			val arrsel_17 = ArrSelectInt(arr_16, i_11)
			val atm_18 = Atom(Pred("$greater", List(arrsel_17, c0_3)))
			val and_19 = And(and_15, atm_18)
			val exists_20 = Exists(i_11, and_19)
			val iff_21 = Iff(atm_10, exists_20)
			val atm_22 = Atom(Pred("$less$eq", List(n1_13, N_2)))
			val and_23 = And(iff_21, atm_22)
			val inv_24 = Invariant(None, and_23, None)
			val v_25 = unk_1.setPrePost(inv_8, inv_24)
			val atm_26 = Atom(Pred("$eq$eq", List(N_2, n1_13)))
			val not_27 = Not(atm_26)
			val and_28 = And(and_23, not_27)
			val inv_29 = Invariant(None, and_28, None)
			val v_30 = unk_1.setPrePost(inv_29, inv_24)
			val grdcmd_31 = GuardedCmd(not_27, unk_1)
			val whileprog_32 = WhileProg(Some(and_23), List(grdcmd_31))
			val atm_33 = Atom(Pred("$less", List(i_11, N_2)))
			val and_34 = And(atm_12, atm_33)
			val and_35 = And(and_34, atm_18)
			val exists_36 = Exists(i_11, and_35)
			val iff_37 = Iff(atm_10, exists_36)
			val inv_38 = Invariant(None, iff_37, None)
			val v_39 = whileprog_32.setPrePost(inv_24, inv_38)
			val compprog_40 = Composition(List(unk_1, whileprog_32))
			val v_41 = compprog_40.setPrePost(inv_8, inv_38)
			compprog_40
		}

		val ctx = {
			val vars = List(VarInt("r"))
			val vals = List(VarArrayInt("arr"), VarInt("N"))
			Some(new ProgContext(vars, vals, Nil)) //TODO: dummies set to Nil
		}

		val trs = RCVAllComb.applyTactic(inputUnkProg, ctx)
		//val expectedCode = (for(tr <- trs; resProg <- tr.resultProg) yield {
		//	resProg.toCode
		//}).mkString("/////////////\n")
		assert(trs.length == 1)
		val tr = trs.head
		val resProgOpt = tr.resultProg
		assert(resProgOpt.isDefined)
		val resProg = resProgOpt.get
		PSDbg.writeln0(resProg.toString)
		PSDbg.writeln0("\\\\\\\\\\")
		PSDbg.writeln0(expectedProg.toString)
		//TODO: avoid string comparison
		val expectedProgStr = "Composition(Invariant(None,And(Atom(Pred($greater,List(VarInt(N), ConstInt(0)))),Atom(Pred($eq$eq,List(VarInt(r), ConstInt(0))))),None),List(UnknownProg(Invariant(None,And(Atom(Pred($greater,List(VarInt(N), ConstInt(0)))),Atom(Pred($eq$eq,List(VarInt(r), ConstInt(0))))),None),0,Invariant(None,And(Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(n1))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),Atom(Pred($less$eq,List(VarInt(n1), VarInt(N))))),None)), WhileProg(Invariant(None,And(Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(n1))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),Atom(Pred($less$eq,List(VarInt(n1), VarInt(N))))),None),Some(And(Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(n1))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),Atom(Pred($less$eq,List(VarInt(n1), VarInt(N)))))),List(GuardedCmd(Not(Atom(Pred($eq$eq,List(VarInt(N), VarInt(n1))))),UnknownProg(Invariant(None,And(And(Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(n1))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),Atom(Pred($less$eq,List(VarInt(n1), VarInt(N))))),Not(Atom(Pred($eq$eq,List(VarInt(N), VarInt(n1)))))),None),0,Invariant(None,And(Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(n1))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),Atom(Pred($less$eq,List(VarInt(n1), VarInt(N))))),None)))),Invariant(None,Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(N))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),None))),Invariant(None,Iff(Atom(Pred($eq$eq,List(VarInt(r), ConstInt(1)))),Exists(i,And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(N))))),Atom(Pred($greater,List(ArrSelectInt(VarArrayInt(arr),VarInt(i)), ConstInt(0))))))),None))"

		withClue("Synthesized program differ from expected program") {
			PSDbg.writeln0(resProg.toString)
			resProg.toString should equal(expectedProgStr)
		}
		/////////////////////
	}
}
*/