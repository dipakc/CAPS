package models

//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//import org.junit.Assert
//import org.scalatest.matchers.ShouldMatchers
//import play.api.test.FakeApplication
//import play.api.GlobalSettings

//import progsynth.types._



//@RunWith(classOf[JUnitRunner])
//class DerivationServerTest extends FunSuite with ShouldMatchers {
//	/** Test Functions */
//
//	//app.Global.onStart(null) //TODO: use withApplication and FakeApplication.
//	app.Global.initConfig();
//
//	test("intDiv") {
//	    //x should equal y
//		val synthTree = Derivations.intDiv()
//		val lastNode = synthTree.curNode
//	}
//
//}

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import app.CAPSSettings
import progsynth.types._
import org.specs2.matcher.ShouldMatchers
import models.derivations.scripts
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
import progsynth.printers._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

@RunWith(classOf[JUnitRunner])
class ArraySwapDev extends Specification with ShouldMatchers {

	val logger = LoggerFactory.getLogger("progsynth.xxx")

    val fakeApp = {
		val newGlobal = new CAPSSettings(){}
        FakeApplication(withGlobal = Some(newGlobal))
    }
	abstract class WithCAPSApplication extends WithApplication(fakeApp) {}
    /*
    "ArrayStorePOCheck" should {
        "run successfully" in new WithCAPSApplication {
			val x = VarInt("x")
			val tmp = VarInt("tmp")
			val y = VarInt("y")
			val X = VarInt("X")
			val Y = VarInt("Y")
			val arr = VarArrayInt("arr")
			val N = VarInt("N")
			
			val c0 = ConstInt("0")
			val immutableVars = List (x, y, X, Y, N)
			val mutableVars = arr :: Nil
			
			val globalInvs = List(c0 <= x && x < N, c0 <= y && y < N)
			
			val pre = c0 <= x && x < N
			val post = (arr.select(x) eqeq Y) //arr[x] == Y
			
			val annProg = mkAssignmentTerm(
			    pre.inv, arr,
			    arr.store(x, Y),
			    post.inv)
			POGenerator.populatePOs(annProg)(Nil)
			
			ProgramAnnPOProver.provePOs(annProg, Nil)
			for( pobg <- annProg._proofObligs ) {
				logger.trace(pobg.term)
			}
			
			if (annProg.allPOsAreValid())
				logger.trace("All pos are valid")
			else
			    logger.trace("Proof obligation can not be discharged.")
        }
    }
    */
	/*
    "ArraySwapProcCallPOCheck" should {
        "run successfully" in new WithCAPSApplication {

          def old(aVar: VarArrayInt):VarArrayInt = aVar.addPrefix("_old_").asInstanceOf[VarArrayInt]
			
			val SwapProcDef = {
				val x = VarInt("x")
				val y = VarInt("y")
				val arr = VarArrayInt("arr")
			  
				val pre: TermBool = TermBool.TrueT
				val post: TermBool =
				  (arr.select(x) eqeq old(arr).select(y)) && (arr.select(y) eqeq old(arr).select(x))
				var spd: ProcedureDef = ProcedureDef("arraySwap", List(arr, x, y), null, Nil, List(arr))
				spd.setPre(pre.inv)
				spd.setPost(post.inv)
				spd
			}
						
			val swapProcCall = {
				val p = VarInt("p")
				val q = VarInt("q")
				val A = VarInt("A")
				val B = VarInt("B")
				val barr = VarArrayInt("barr")
			  
				val pre: TermBool = (barr.select(p) eqeq A) && (barr.select(q) eqeq B)
				val post: TermBool =
				  (barr.select(p) eqeq B) && (barr.select(q) eqeq B)
			  
				val spc = ProcedureCall(SwapProcDef, List(barr, p, q))
				spc.setPre(pre.inv)
				spc.setPost(post.inv)
				spc
			}
			
			POGenerator.populatePOs(swapProcCall)(Nil)
			
			ProgramAnnPOProver.provePOs(swapProcCall, Nil)
			
			for( pobg <- swapProcCall._proofObligs ) {
				logger.trace(pobg.term)
			}
			
			if (swapProcCall.allPOsAreValid())
				logger.trace("All pos are valid")
			else
			    logger.trace("Proof obligation can not be discharged.")
        }
    }
    */
    "ArraySwapGhostProcCallPOCheck" should {
        "run successfully" in new WithCAPSApplication {

          def old(aVar: VarArrayInt):VarArrayInt = aVar.addPrefix("_old_").asInstanceOf[VarArrayInt]
			
			val SwapProcDef = {
				val x = VarInt("x")
				val y = VarInt("y")
				val A = VarInt("A")
				val B = VarInt("B")
				val arr = VarArrayInt("arr")
			  
				val ghostVars = A :: B :: Nil
				val frameVars = List(arr)
				val fParams = List(arr, x, y)
				val body: ProgramAnn = null
				val pre: TermBool = (arr.select(x) eqeq A) && (arr.select(y) eqeq B)
				val post: TermBool = (arr.select(x) eqeq B) && (arr.select(y) eqeq A)
				var spd: ProcedureDef = ProcedureDef("arraySwap", fParams, body, ghostVars, frameVars, pre.inv, post.inv)
				spd
			}
						
			val swapProcCall = {
				val p = VarInt("p")
				val q = VarInt("q")
				val C = VarInt("C")
				val D = VarInt("D")
				val barr = VarArrayInt("barr")
			  
				val pre: TermBool = (barr.select(p) eqeq C) && (barr.select(q) eqeq D)
				val post: TermBool =
				  (barr.select(p) eqeq D) && (barr.select(q) eqeq D)
			  
				val spc = ProcedureCall(SwapProcDef, List(barr, p, q), pre.inv, post.inv)
				spc
			}
			
			val SwapProcDef2 = {
				val x = VarInt("x")
				val y = VarInt("y")
				val A = VarInt("A")
				val B = VarInt("B")
				val arr = VarArrayInt("arr")
			  
				val ghostVars = Nil
				val frameVars = List(arr)
				val fParams = List(arr, x, y)
				val body: ProgramAnn = null
				
				val oarr = old(arr)
				val pre: TermBool = TermBool.TrueT
				val post: TermBool = arr eqeq oarr.store(x, oarr.select(y)).store(y, oarr.select(x))
				var spd: ProcedureDef = ProcedureDef("arraySwap", fParams, body, ghostVars, frameVars, pre.inv, post.inv)
				spd
			}

			val swapProcCall2 = {
				val p = VarInt("p")
				val q = VarInt("q")
				val r = VarInt("r")
				val C = VarInt("C")
				val D = VarInt("D")
				val barr = VarArrayInt("barr")
				val c0 = ConstInt("0")
			  
				val pre: TermBool = (barr.select(p) eqeq C) && (barr.select(q) eqeq D) &&
						(p neqeq r) && (q neqeq r) && (barr.select(r) eqeq c0 )
				val post: TermBool = (barr.select(p) eqeq D) && (barr.select(q) eqeq C) &&
				  		(barr.select(r) eqeq c0)
			  
				val spc = ProcedureCall(SwapProcDef2, List(barr, p, q), pre.inv, post.inv)
				spc
			}

			val swapProg = {
				val p = VarInt("p")
				val q = VarInt("q")
				val r = VarInt("r")
				val tmp = VarInt("tmp")
				val C = VarInt("C")
				val D = VarInt("D")
				val barr = VarArrayInt("barr")
				val c0 = ConstInt("0")
				
				def mkUnkInv() = UnkTerm.mkUnkTermBool().inv
			  
				val pre: TermBool = (barr.select(p) eqeq C) && (barr.select(q) eqeq D) &&
						(p neqeq r) && (q neqeq r) && (barr.select(r) eqeq c0 )
				val post: TermBool = (barr.select(p) eqeq D) && (barr.select(q) eqeq C) &&
				  		(barr.select(r) eqeq c0)
				  		
				val swapProg = mkComposition3(
				    pre.inv,
				    mkAssignmentTerm(pre.inv, tmp, barr.select(p), mkUnkInv),
				    mkAssignmentTerm(mkUnkInv, barr, barr.store(p, barr.select(q)), mkUnkInv),
				    mkAssignmentTerm(mkUnkInv, barr, barr.store(q, tmp), post.inv),
				    post.inv)
				
				swapProg
			}
			
			swapProg.inferAnn
			
			POGenerator.populatePOs(swapProg)(Nil)
			
			ProgramAnnPOProver.provePOs(swapProg, Nil)
			
			for( pobg <- swapProg._proofObligs ) {
				logger.trace(XHTMLPrinters2.termToHtml(pobg.term).toString)
			}
			
			if (swapProg.allPOsAreValid())
				logger.trace("All pos are valid")
			else
			    logger.trace("Proof obligation can not be discharged.")
        }
    }

    /*
    "IncrProcCallPOCheck" should {
        "run successfully" in new WithCAPSApplication {
        	
        	def old(aVar: VarInt):VarInt = aVar.addPrefix("_old_").asInstanceOf[VarInt]
        	
			val x = VarInt("x")
			val y = VarInt("y")
			val z = VarInt("z")
			val A = VarInt("A")
			val c1 = ConstInt("1")
			
			
			val IncrProcDef = {
        		val pre = TermBool.TrueT
        		val post = x eqeq (old(x) + c1)
        		val ipd = ProcedureDef("Incr", List(x), null, Nil, List(x))
        		ipd.setPre(pre.inv)
        		ipd.setPost(post.inv)
        		ipd
        	}
						
			val incrProcCall = {
        		val pre = TermBool.TrueT
        		val post = y eqeq (old(y) + c1)
        		val pc = ProcedureCall(IncrProcDef, List(y))
        		pc.setPre(pre.inv)
        		pc.setPost(post.inv)
        		pc
        	}
			
			POGenerator.populatePOs(incrProcCall)(Nil)
			
			ProgramAnnPOProver.provePOs(incrProcCall, Nil)
			
			for( pobg <- incrProcCall._proofObligs ) {
				logger.trace(XHTMLPrinters2.termToHtml(pobg.term))
			}
			
			if (incrProcCall.allPOsAreValid())
				logger.trace("All pos are valid")
			else
			    logger.trace("Proof obligation can not be discharged.")
        }
    }
*/
}
