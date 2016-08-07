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
import progsynth.synthesisnew.StepIntoSubFormulaTactic

@RunWith(classOf[JUnitRunner])
class MaxSegSumTest extends Specification with ShouldMatchers {

    val fakeApp = {
		val newGlobal = new CAPSSettings(){}
        FakeApplication(withGlobal = Some(newGlobal))
    }
	abstract class WithCAPSApplication extends WithApplication(fakeApp) {}

//    "allTrue" should {
//        "run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.allTrue()
//			val lastNode = synthTree.curNode
//			val lastNodeId = lastNode.id
//			lastNode.id mustEqual 6
//			//lastNode.id should be === (19)
//			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
//			//TODO: print progAnn
//        }
//    }

    "maxSegSum" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.MaxSegSum()
			synthTree.applyTacticBatch(new StepIntoSubFormulaTactic(34))
			synthTree.applyTacticBatch(new StepIntoSubFormulaTactic(7))
        }
    }

}

