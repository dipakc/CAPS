package models

import scala.util.Success
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import app.CAPSSettings
import jsonprinter.SynthTreePrinter
import models.derivations.scripts
import models.parseruntyped.SynthTreeParser
import play.api.test.FakeApplication
import play.api.test.WithApplication
import progsynth.synthesisnew.CalcProofStep
import progsynth.synthesisnew.SynthNode
import progsynth.synthesisnew.SynthTree
import progsynth.types._
import scala.util.Try
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import scala.util.Failure
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

/** docs\Parsers.svg */
@RunWith(classOf[JUnitRunner])
class SaveTreeJsonTest extends Specification with ShouldMatchers {

	val logger= LoggerFactory.getLogger("progsynth.SaveTreeJsonTest")

	val fakeApp = {
		val newGlobal = new CAPSSettings() {}
		FakeApplication(withGlobal = Some(newGlobal))
	}
	abstract class WithCAPSApplication extends WithApplication(fakeApp) {}

	def compareNodes(n1: SynthNode, n2: SynthNode): Unit = {
		n1.id shouldEqual(n2.id)
		(n1.nodeObj, n2.nodeObj) match {
			case (p1: ProgramAnn, p2: ProgramAnn) => p1 shouldEqual(p2)
			case (f1: CalcProofStep, f2: CalcProofStep) => f1 shouldEqual(f2)
			case _ => throw new RuntimeException("Unknown Node types.")
		}
	}

	def mytest(refTree: SynthTree): Unit = {

		refTree.batchTacticFailed shouldEqual false

		import SynthTreeParser.TacticEdge

		def getRefTactic(id: Int) =
			for ( refNode <- refTree.getNodeFromId(id) ) yield refNode.tactic

		def getUpdatedTree(tree: SynthTree, seq: Seq[JsValue]): Try[SynthTree] = {
			var retVal: Try[SynthTree] = Success(tree)

			for(tacticEdgeJS <- seq; if retVal.isSuccess) {
				retVal = for {
					tacticEdge <- 	{
										logger.trace("tacticEdgeJS")
										logger.trace(tacticEdgeJS.toString)
										SynthTreeParser.parseTacticEdge(tree, tacticEdgeJS)
									}
					_ = logger.trace(tacticEdge.tactic.toString)
					cid = tacticEdge.cid
					Some(refTactic) = getRefTactic(cid)
					_ = logger.trace(refTactic.toString)
					_ = tacticEdge.tactic shouldEqual refTactic
					_ <- SynthTreeParser.applyTacticEdge(tree, tacticEdge)
					Some(refNode) = refTree.getNodeFromId(cid)
					Some(node) = tree.getNodeFromId(cid)
					_ = compareNodes(node, refNode)
				} yield tree
			}
			retVal
		}

		def jsonToSynthTree(treeStr: String): Try[SynthTree] = {
			val tree = scripts.emptyTree()
			val retVal = for {
				treeJS <- Try { Json.parse(treeStr) }
				curNodeId <- Try { (treeJS \ "curNodeId").as[Int] }
				synthNodeCounterCnt <- Try { (treeJS \ "synthNodeCounterCnt").as[Int] }
				batchTacticFailed <- Try{ (treeJS \ "batchTacticFailed").as[Boolean] }
				tacticEdgesJS <- Try { treeJS \ "tacticEdges" }
				seq <- Try { val JsArray(s) = tacticEdgesJS; s }
				updTree <- getUpdatedTree(tree, seq)
				_ <- Try{ updTree.setCurrentNode(curNodeId) }
				_ <- Try{ updTree.synthNodeCounter.resetCnt(synthNodeCounterCnt)}
				_ <- Try{ updTree.batchTacticFailed = batchTacticFailed }
			} yield {
				updTree
			}
			retVal
		}

		def checkOuterData(tree: SynthTree): Try[SynthTree] = Try {
			tree.curNode.id shouldEqual(refTree.curNode.id)
			tree.batchTacticFailed shouldEqual (refTree.batchTacticFailed)
			tree.synthNodeCounter.cnt shouldEqual(refTree.synthNodeCounter.cnt)
			tree
		}

		val retVal = for {
			treeStr <- new SynthTreePrinter(refTree).synthTreeToJson()
			tree <- jsonToSynthTree(treeStr)
			_  <- checkOuterData(tree)
		} yield tree

		retVal match {
			case Success(_) =>
			case Failure(e) => throw e
		}

	}


//	"intDiv Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.IntDiv()
//			mytest(synthTree)
//		}
//	}
//
//	"intSqrt Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.IntSqrt()
//			mytest(synthTree)
//		}
//	}
//
//	"LinearSearch Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.LinearSearch()
//			mytest(synthTree)
//		}
//	}
//
//	"BinarySearch Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.BinarySearch()
//			mytest(synthTree)
//		}
//	}
//
//	"ExistsTrue Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.ExistsTrue()
//			mytest(synthTree)
//		}
//	}
//
//	"TTFF7NoBranching Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.TTFF7NoBranching()
//			mytest(synthTree)
//		}
//	}
//
//	"TTFF9 Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.TTFF9()
//			mytest(synthTree)
//		}
//	}
//
//	"DutchNationalFlag Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.DutchNationalFlag()
//			mytest(synthTree)
//		}
//	}

	"MaxSegSum Save to JSON" should {
		"run successfully" in new WithCAPSApplication {
			val synthTree = scripts.MaxSegSum()
			mytest(synthTree)
		}
	}

//	"HornersRule Save to JSON" should {
//		"run successfully" in new WithCAPSApplication {
//			val synthTree = scripts.HornersRule()
//			mytest(synthTree)
//		}
//	}

}
