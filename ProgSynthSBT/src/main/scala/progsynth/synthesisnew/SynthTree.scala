package progsynth.synthesisnew

import progsynth.proofobligations._
import progsynth.types._
import progsynth.types.Types._
import progsynth.synthesisold.ProgContext
import progsynth.synthesisold.EmptyProgContext
import scala.util.control.Breaks._
import progsynth.ProgSynth.Counter
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

class SynthTree() extends PSTacticFuns with SynthTreeViewUtils {

    val logger= LoggerFactory.getLogger("progsynth.SynthTree")

	var rootNode: SynthNode = null
	var curNode: SynthNode = null

	val synthNodeCounter = new Counter {}

	/* Indicates if a batch tactic application has failed.
	 * Once it becomes true it should always be true */
	var batchTacticFailed = false

	//call constructor
	constructor()

	def constructor() = {
	    //SynthNode.resetCnt //Reset Node Id Counter
	    ProgramAnn.reset //Reset ProgramAnn Id Counter
		rootNode = new SynthNode(tactic = null, nodeObject = null,
								childs = Nil, parent  = null,
								frame = ProgramFrame.mkEmptyFrame(None), this, None).setRootNodeFlag()
		curNode = rootNode
	}

	//def setNewRootNode(aNode: SynthNode) = {
	//	if(curNode == rootNode){
	//		curNode = aNode
	//	}
	//	aNode.setRootNodeFlag()
	//    rootNode = aNode
	//}

	def getRootFrame(): Frame = {
	    rootNode.frame
	}

	def addGlobalMacros(macros: List[Macro]): SynthTree = {
	    //TODO: check that the macros do not exist in the tree.
	    getRootFrame.addMacros(macros)
		this
	}
	def addGlobalMacro(macros: Macro): SynthTree = addGlobalMacros(macros::Nil)

	def applyTactic(tactic: Tactic, idOpt: Option[Int]): Try[SynthTree] = {
		val newChildNodeE = tactic.getNextNode(curNode, idOpt)
		newChildNodeE match {
			case Right(newChildNode) =>
				curNode.childs = curNode.childs :+ newChildNode //append the child
				curNode = newChildNode
				Success(this)
			case Left(te) =>
				Failure(new RuntimeException(te.toString))
		}
	}

	def applyTactic2(t: Tactic): SynthTree = {
		//val status = applyTactic(t)
		//if (!status) {
		//	println("tactic application failed")
		//}
		//this
		applyTactic(t, None) match {
			case Failure(e) => println(e.getMessage())
			case Success(synthTree) => synthTree
		}
		this
	}

	/*Should be used for applying tactics in batch mode (from derivation scripts) */
	def applyTacticBatch(t: Tactic): SynthTree = {
	    /* Apply tactic only if no tactic in the past has failed.*/
	    if(! this.batchTacticFailed) {
            logger.trace(beginSection("applyTacticBatch"))
            logger.trace("TacticName: "+ t.tName)
	    	println(t.tName)
			applyTactic(t, None) match {
				case Success(synthTree) =>
				case Failure(e) =>
				    this.batchTacticFailed = true
				    println("Batch Tactic Application Failed");
				    logger.trace("Batch Tactic Application Failed")
				    println(e.getMessage);
				    logger.trace(e.getMessage)
			}
            logger.trace(endSection("applyTacticBatch"))
	    }
	    this
	}

	/*Applies the tactic to parent of the curNode. curNode must be a leaf node.*/
	def editTactic(tactic: Tactic): Try[SynthTree] = {
	    assert(curNode.childs.isEmpty);
		val newCurNodeE = tactic.getNextNode(curNode.parent, Some(curNode.id))
		newCurNodeE match {
			case Right(newCurNode) =>
			    //Replace curNode with the newCurNode
			    curNode.parent.childs = curNode.parent.childs.map( n => if(n == curNode) newCurNode else n)
				curNode = newCurNode
				Success(this)
			case Left(te) =>
				Failure(new RuntimeException(te.toString))
		}
	}

	def stepOutAll = {
		var status = applyTactic(new StepOutTactic, None)
		while (status.isSuccess) {
			status = applyTactic(new StepOutTactic, None)
		}
		this
	}

	/** reset the tree to the input tree */
	def resetTree(synthTree: SynthTree) = {
		rootNode = synthTree.rootNode
		curNode = synthTree.curNode
	}

	def getNodeFromId(nodeId: Int): Option[SynthNode] = {
		getNodeFromId(rootNode, nodeId)
	}

	def getNodeFromId(baseNode: SynthNode, nodeId: Int): Option[SynthNode] = {
		var retVal: Option[SynthNode] = None
		if (baseNode.id == nodeId) {
			retVal = Some(baseNode)
		} else {
			breakable{ for(child <- baseNode.childs) {
				retVal = getNodeFromId(child, nodeId)
				if(retVal.isDefined) break
			}}
		}
		retVal
	}

	def setCurrentNode(nodeId: Int): SynthTree = {
		val nodeOpt = getNodeFromId(rootNode, nodeId)
		nodeOpt match {
			case Some(node) => curNode = node; this
			case None => null
		}
	}

	def setCurrentNodeBatch(nodeId: Int): SynthTree = {
	    /* Apply tactic only if no tactic in the past has failed.*/
	    if(! this.batchTacticFailed) {
			setCurrentNode(nodeId) match {
			    case null =>
			    	this.batchTacticFailed = true
				    println("Set Current Node application failed");
			    case _ =>
			}
	    }
	    this
	}

	def getRootToCurrentPathNodes(): List[SynthNode] = {
		var retVal: List[SynthNode] = Nil
		var iNode = curNode
		while (iNode != null) {
			retVal = iNode :: retVal
			iNode = curNode.parent
		}
		retVal.reverse
	}

	/**
	 *  - Removes the node (and the subtree) from the tree.
	 *  - Sets the curNode to the the parent of the deleted node and returns the new synthTree
	 *  - Can not delete the root node.
	 */
	def deleteNode(id: Int): Option[SynthTree] = {
	    for {
	        delNode <- getNodeFromId(id)
	        if (delNode.id != rootNode.id)
	    } yield {
            val pNode = delNode.parent
            pNode.removeChild(delNode.id)
            curNode = pNode
            this
	    }
	}
}