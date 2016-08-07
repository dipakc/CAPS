package jsonprinter

import progsynth.synthesisnew.SynthTree
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import progsynth.synthesisnew.SynthNode
import scala.util.Try

class SynthTreePrinter(tree: SynthTree) {

	def getTacticEdgeJson(cNode: SynthNode): JsValue = {
		val tactic = cNode.tactic
		val pid = cNode.parent.id
		val cid = cNode.id

		val tv = TacticTVMapper.getTacticTV(tactic)
		val tacticJson = tv.toJSON()

		Json.obj("tactic" -> tacticJson, "pid" -> pid, "cid" -> cid)
	}

	private def tacticsJS(): JsValue = {
		var tacticList: List[JsValue] = Nil
		//DFS : exclude rootNode
		var nodeList = tree.rootNode.childs
		while( !nodeList.isEmpty) {
			val iNode = nodeList.head
			nodeList = nodeList.drop(1)

			nodeList = iNode.childs ++ nodeList
			tacticList ::= getTacticEdgeJson(iNode)
		}

		tacticList = tacticList.reverse
		Json.toJson(tacticList)
	}

	private def synthTreeJS(): JsValue = {
		Json.obj(
			"curNodeId" -> tree.curNode.id,
			"synthNodeCounterCnt" -> tree.synthNodeCounter.cnt,
			"batchTacticFailed"-> tree.batchTacticFailed,
			"tacticEdges" -> tacticsJS()
		)
	}

	def synthTreeToJson(): Try[String] = Try{
		val retJS = synthTreeJS()
		retJS.toString
	}
}

object SynthTreePrinter {
	def apply(tree: SynthTree) = new SynthTreePrinter(tree)
}