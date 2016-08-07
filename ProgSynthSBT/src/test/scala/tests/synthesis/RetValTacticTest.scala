package tests.synthesis

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
//import tests.utils.PSPluginTesterDbg
import tests.testutils.CompareFiles._
import progsynth.synthesisold.RetValMainTactic
import progsynth.synthesisold.PSTacticResult
import progsynth.debug.PSDbg._

@RunWith(classOf[JUnitRunner])
class RetValTacticTest extends FunSuite with ShouldMatchers {
	//TODO: enable this test case
	test("RetValTacticTest") {
		val inputUnkProg = {
			val arr = VarArrayInt("arr")
			val N = VarInt("N")
			val c0 = ConstInt("0")
			//val atm_5 = Atom(Pred("$greater", List(N, c0)))
			val atm_5: TermBool = GTTermBool(N, c0)
			val inv_6 = InvariantT(None, atm_5, None)
			val r = VarInt("r")
			val c1 = ConstInt("1")
			val atm_9 = EqEqTermBool(r, c1)
			val i = VarInt("i")
			val atm_11 = LETermBool(c0, i)
			val atm_12 = LTTermBool(i, N)
			val and_13 = atm_11 && atm_12
			val arri = ArrSelectInt(arr, i)
			val arri_positive = GTTermBool(arri, c0)
			val impl_16 = and_13 impl arri_positive
			val exists_17 = ExistsTermBool(i :: Nil, TermBool.TrueT, impl_16)
			val iff_18 = EqEqEqTermBool(atm_9, exists_17)
			val inv_19 = InvariantT(None, iff_18, Some(r))
			val unk = UnknownProg(0, inv_6, inv_19)
			val unk_22 = UnkTerm.mkUnkTermBool
			val inv_23 = InvariantT(None, unk_22, None)
			val fun_21 = FunctionProg("existsPositiveElement0", List(arr, N), VarBool("r"), unk, Nil, inv_23, inv_23)
			unk
		}
		val expectedProg = {
			val r_1 = VarInt("r")
			val c0_2 = ConstInt("0")
			val expr_3 = ExprProg(c0_2)
			val N_4 = VarInt("N")
			val atm_5 = GTTermBool(N_4, c0_2)
			val inv_6 = InvariantT(None, atm_5, None)
			val atm_7 = EqEqTermBool(r_1, c0_2)
			val and_8 = atm_5 && atm_7
			val inv_9 = InvariantT(None, and_8, Some(r_1))
			val v_10 = expr_3.withNewParams(pre = inv_6, post = inv_9)
			val inv_12 = InvariantT(None, and_8, None)
			val valDef_11 = VarDefProg(r_1, Some(expr_3), inv_6, inv_12)
			val unk_14 = UnknownProg(1, null, null)
			val c1_15 = ConstInt("1")
			val atm_16 = EqEqTermBool(r_1, c1_15)
			val i_17 = VarInt("i")
			val atm_18 = LETermBool(c0_2, i_17)
			val atm_19 = LTTermBool(i_17, N_4)
			val and_20 = atm_18 && atm_19
			val arr_21 = VarArrayInt("arr")
			val arrsel_22 = ArrSelectInt(arr_21, i_17)
			val atm_23 = GTTermBool(arrsel_22, c0_2)
			val impl_24 = and_20 impl atm_23
			val exists_25 = ExistsTermBool(i_17 :: Nil, TermBool.TrueT, impl_24)
			val iff_26 = EqEqEqTermBool(atm_16, exists_25)
			val inv_27 = InvariantT(None, iff_26, None)
			val v_28 = unk_14.withNewParams(pre = inv_12, post = inv_27)
			val expr_29 = ExprProg(r_1)
			val v_30 = expr_29.withNewParams(pre = inv_27, post = inv_27)
			val inv_32 = InvariantT(None, iff_26, Some(r_1))
			val compprog_31 = Composition(List(valDef_11, unk_14, expr_29), inv_6, inv_32)
			compprog_31
		}

		val trs = RetValMainTactic.applyTactic(inputUnkProg, None)
		//val expectedCode = (for(tr <- trs; resProg <- tr.resultProg) yield {
		//	resProg.toCode
		//}).mkString("/////////////\n")
		assert(trs.length == 1)
		val tr = trs.head
		val resProgOpt = tr.resultProg
		assert(resProgOpt.isDefined)
		val resProg = resProgOpt.get
		writeln0(resProg.toString)
		writeln0("//////////////")
		writeln0(expectedProg.toString)
		//assert(resProg == expectedProg) //does not test the pre and post.
		withClue ("Synthesized program differ from expected program") {
			resProg.toString should equal (expectedProg.toString)
		}
		/////////////////////
	}
}
