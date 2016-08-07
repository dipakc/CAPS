package progsynth.synthesisnew

import SynthUtils.setDisplayId
import progsynth.types.DisplayIdPrinter

trait CalcStep

/**
 * SynthNode
 * tactic : tactic whose application results in this node. null in case of the root node
 * nodeObj: CalcStep
 * childs: Child SynthNodes
 *
 */

class SynthNode(
	val tactic: Tactic,
	nodeObject: CalcStep, //Can be ProgramAnn, FOLFormula, Term, Invariant etc.
						   //TODO: change type of nodeObject to PSProgTree. for nonPSProgTree, displayID will not be shown
	var childs: List[SynthNode],
	val parent: SynthNode,
	val frame: Frame,
	val synthTree: SynthTree,
	idOpt: Option[Int] //Optional node id if you want to set it explicitly.
	) extends SynthNodeViewUtils
{
	val nodeObj: CalcStep = setDisplayId(nodeObject).asInstanceOf[CalcStep]

	val id: Int = idOpt.getOrElse(synthTree.synthNodeCounter.getCnt)

	private var _isRoot = false

	def isRoot = _isRoot

	def setRootNodeFlag() = {
		_isRoot = true
		this
	}

	//def copy( tactic: Tactic = this.tactic, nodeObject: Any = this.nodeObj,
    //        childs: List[SynthNode] = this.childs, parent:  SynthNode = this.parent,
    //		frame: Frame = this.frame) = {
    //	new SynthNode(tactic, nodeObject, childs, parent, frame)
    //}

	def removeChild(id: Int): Unit = {
	    childs = childs.filterNot(_.id == id)
	}
}

