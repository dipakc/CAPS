package models
package parseruntyped

import scala.util.Success
import scala.util.Try

import models.derivations.scripts
import play.api.libs.json.{JsArray, JsValue, Json}
import progsynth.synthesisnew._
import progsynth.synthesisold.ProgContext

object SynthTreeParser {
	case class TacticEdge(tactic: Tactic, pid: Int, cid: Int)

	//Refactor: Same function also implmented in OpenDerivation.
	private def getProgContext(curNode: SynthNode): ProgContext = {
		val fSummary: PFFrameSummary = {
			val cFrame = curNode.frame
		    cFrame.getSummary()
		}

		/** Although ProgContext is no longer used in SynthNode, we still use it in the parser */
		//implicit val progContext = getState().curNode.progContext

		// Add accumulated fresh variables to context for parsing.
		// Required for AssumePre tactic in binary search
		val accFreshVars = curNode.nodeObj match {
		    case cps: CalcProofStep => cps.freshVariables
		    case _ => Nil
		}

		val metaVars = curNode.nodeObj match {
		    case cps: CalcProofStep => cps.metaVars
		    case _ => Nil
		}

		new ProgContext(	varList = fSummary.progFrameSummary.varList ++ accFreshVars ++ metaVars,
    						valList = fSummary.progFrameSummary.valList,
    						dummyList = fSummary.formulaFrameSummary.dummies)

	}

	def parseTacticEdge(tree: SynthTree, teJS: JsValue): Try[TacticEdge] = {
		for {
			tacticJS <- Try{teJS \ "tactic"}
			pid <- Try{ (teJS \ "pid").as[Int] }
			cid <- Try { (teJS \ "cid").as[Int] }
			tactic <- parseTactic(tree, pid, tacticJS)
		} yield {
			TacticEdge(tactic, pid, cid)
		}
	}

	def parseTactic(tree: SynthTree, pid: Int, tacticJS: JsValue): Try[Tactic] = {
		tree.setCurrentNode(pid)
		val curNode = tree.curNode
		val progContext: ProgContext = getProgContext(curNode)

		val parser = new JsonDataCSParser()
	    parser.psParseTactic(tacticJS)(progContext, curNode)
	}

	def applyTacticEdge(tree: SynthTree, tacticEdge: TacticEdge): Try[Unit] = Try{
		tree.setCurrentNode(tacticEdge.pid)
		tree.applyTactic(tacticEdge.tactic, Some(tacticEdge.cid))
	}

	def getUpdatedTree(tree: SynthTree, seq: Seq[JsValue]): Try[SynthTree] = {
		var retVal: Try[SynthTree] = Success(tree)

		for(tacticEdgeJS <- seq; if retVal.isSuccess) {
			retVal = for {
				tacticEdge <- parseTacticEdge(tree, tacticEdgeJS)
				_ <- applyTacticEdge(tree, tacticEdge)
			} yield tree
		}
		retVal
	}

	def jsonToSynthTree(treeStr: String): Try[SynthTree] = {

		val tree = scripts.emptyTree()
		for {
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
	}
}
