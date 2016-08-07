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

@RunWith(classOf[JUnitRunner])
class HelloWorldSpec extends Specification with ShouldMatchers {

    val fakeApp = {
		val newGlobal = new CAPSSettings(){}
        FakeApplication(withGlobal = Some(newGlobal))
    }
	abstract class WithCAPSApplication extends WithApplication(fakeApp) {}

    "intDiv" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.IntDiv()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 19
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }

    "intSqrt" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.IntSqrt()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 19
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }//

    "intDivMacroTest" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.intDivMacroTest()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 9
			//lastNode.id should be === (19)
			//val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }

    "arrayMin" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.ArrayMin()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 10
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }

    "binarySearch" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.BinarySearch()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 47
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }

    "TTFF7" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.TTFF7()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 94
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }

    "TTFF8" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.TTFF8()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 50
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }


    "allTrue" should {
        "run successfully" in new WithCAPSApplication {
			val synthTree = scripts.allTrue()
			val lastNode = synthTree.curNode
			val lastNodeId = lastNode.id
			lastNode.id mustEqual 6
			//lastNode.id should be === (19)
			val progAnn = lastNode.nodeObj.asInstanceOf[ProgramAnn]
			//TODO: print progAnn
        }
    }
}

