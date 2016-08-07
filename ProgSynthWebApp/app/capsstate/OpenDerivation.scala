package capsstate
import play.api.libs.json.JsValue
import scala.sys.process._
import models.derivations.Derivations._
import progsynth.synthesisnew._
import progsynth.types._
import progsynth._
import progsynth._
import progsynth.synthesisnew.SynthUtils._
import progsynth.synthesisold.ProgContext
import models.parseruntyped.JsonDataCSParser
import models.User
import scala.util._
import models.derivations.scripts
import models.derivations.DerivationUtils
import scala.util.{Success => TSuccess}
import scala.util.{Failure => TFailure}
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import jsonprinter.SynthTreePrinter
import org.slf4j.LoggerFactory //
import progsynth.logger.PSLogUtils._
import progsynth.utils.RichTryT._

class OpenDerivation(val owner: User, val derivName: String, synthTree: SynthTree) {

	import OpenDerivation._

	 val logger= LoggerFactory.getLogger("progsynth.OpenDerivation")

	//Refactor: make this immutable. Declare in constructor argument.
	private var mSynthTree: SynthTree = synthTree


	//Refactor: do not expose gSynthTree
	def getState(): SynthTree = mSynthTree

	def getCurNode(): SynthNode = getState().curNode

	def getParentNode(): SynthNode = getCurNode.parent

	private def parseTactic(tacticJS: JsValue, ctxNode: SynthNode = getCurNode()): Try[Tactic] = {
	    val progContext: ProgContext = getProgramContext(getState())
		val jasonDataCSParser = new JsonDataCSParser()
	    jasonDataCSParser.psParseTactic(tacticJS)(progContext, ctxNode)
	}

	private def saveDerivation(): Try[Unit] = {
		val fs = new FileStorage(owner)
		for {
			treeStr <- new SynthTreePrinter(mSynthTree).synthTreeToJson()
			_ <- fs.saveDerivString(derivName, treeStr)
		} yield {}
	}

	private def toTryUnit[T](x: Try[T]): Try[Unit] = {
		x match {
			case Success(_) => Success()
			case Failure(e) => Failure(e)
		}
	}

	/**
	 * Parses tactic json and applies the tactic to the synth tree.
	 */
	def applyTactic(tacticCallJson:  JsValue): Try[SynthTree] = {
		logger.trace("applyTactic Arguments : " + tacticCallJson.toString)
		for {
			tactic <- parseTactic(tacticCallJson).perr(TACTIC_PARSE_ERROR)
			st <- mSynthTree.applyTactic(tactic.asInstanceOf[Tactic], None).perr(TACTIC_APP_ERROR)
			r <- saveDerivation().perr(DERIVATION_SAVE_ERROR)
		} yield st
	}

	/**
	 * - Parses tactic json (in context of parent node)
	 * - Applies the tactic to the parent node
	 * - Replaces the curnode with the new node
	 * - The now node becomes the curnode
	 */
	def editTactic(tacticCallJson:  JsValue): Try[SynthTree] = {
		logger.trace("editTactic Arguments : " + tacticCallJson.toString)
		for {
			tactic <- parseTactic(tacticCallJson, getParentNode()).perr(TACTIC_PARSE_ERROR)
			st <- mSynthTree.editTactic(tactic.asInstanceOf[Tactic]).perr(TACTIC_APP_ERROR)
			r <- saveDerivation().perr(DERIVATION_SAVE_ERROR)
		} yield st
	}

	//Refactor: Return type should be Try[String]
	def openDerivation(derivNameJson:  JsValue): Option[String] = {
		logger.trace("open Derivation called")
		val derivNameOpt = (derivNameJson\"derivationName").asOpt[String]

		derivNameOpt match {
		    case Some(name) =>
		        getDerivation(name) match {
		            case Success(stree) =>
		                mSynthTree = stree
		                None
		            case Failure(e) =>
		                Some(e.getMessage)
		        }
		    case None => Some("Derivation name empty")
		}
	}

	def resetTree(): Unit = {
		logger.trace("resetTree called")
		mSynthTree.resetTree(scripts.emptyTree())
		saveDerivation()
	}

	//Refactor: Return type should be Try[SynthTree]
	def setCurNode(curNodeJson:  JsValue): Option[SynthTree] = {
	    val valueOpt = (curNodeJson \ "value").asOpt[Int]
	    //val valueOpt = valueStrOpt flatMap (parseInt(_))
	    valueOpt  flatMap { curNodeId =>
	    	val synthTree = mSynthTree.setCurrentNode(curNodeId)
	    	if(synthTree != null){
	    		saveDerivation() //TODO: Error message
	    		Some(synthTree)
	    	}
	    	else
	    		None
	    }
	}

    //Refactor: Return type should be Try[SynthTree]
	def deleteNode(nodeIdJS:  JsValue): Option[SynthTree] = {
	    val valueOpt = (nodeIdJS \ "value").asOpt[Int]
	    //val valueOpt = valueStrOpt flatMap (parseInt(_))
	    valueOpt  flatMap { nodeId =>
	    	val synthTree = mSynthTree.deleteNode(nodeId)
	    	if(synthTree != null){
	    		saveDerivation() //TODO: Error message
	    		synthTree
	    	} else
	    		None
	    }
	}

	def addGlobalMacro(aMacro: Macro) = {
		mSynthTree.addGlobalMacro(aMacro)
	}

	def serializeDerivation(): Array[Byte] = {
	    DerivationUtils.serialize(mSynthTree)
	}

	def deserializeDerivation(derivContent: Array[Byte]) = {
		for (synthTree <- DerivationUtils.deserialize[SynthTree](derivContent)) yield {
			mSynthTree = synthTree
		}
	}

	def replayDerivation(derivString: String): Try[Unit] = {
		val tacticJsVal: JsValue = Json.parse(derivString)

		tacticJsVal match {
			case JsArray(tacticJsVals) =>
				var retVal = Try{}
				for (tacticJsVal <- tacticJsVals) yield {
				  retVal = OpenDerivation.this.applyTactic(tacticJsVal) match {
						case Failure(error) =>
							logger.error("Error in replayDerivation: " + error.getMessage)
							Try{throw new RuntimeException (error.getMessage)}
						case Success( _) => Try{}
					}
				}
				retVal
			case _ => sys.error("The derivation file does not contain JsArray ")
		}
	}


	//Refactor: This is a stub implementation. Remove once the proper function is implemented.
	def derivationToString(): String = {
		def mkJSString(t: Tactic): String = ""
		val pathNodes = mSynthTree.getRootToCurrentPathNodes
		val pathTactics = pathNodes.map(_.tactic)
		pathTactics.map(t => mkJSString(t)).mkString("\n")

	}

    def getNodeTV(nodeId: Int): Try[JsValue] = Try {
        val printer = new SynthTreePrinter(mSynthTree)
        val json = for {
            node <- mSynthTree.getNodeFromId(nodeId)
        } yield
            printer.getTacticEdgeJson(node)\"tactic"

        json.getOrElse(throw new RuntimeException("Unable to get the json for node id " + nodeId))
    }

}

object OpenDerivation {
	val TACTIC_PARSE_ERROR = "Failed to parse the tactic. Please refer tactic documentation.\n"
	val TACTIC_APP_ERROR = "Failed to apply the tactic.\n"
	val DERIVATION_SAVE_ERROR = "Failed to save the derivation.\n"

	def prependMsg[T](msg: String):  PartialFunction[Throwable, Try[T]] = {
		case e => Failure(new RuntimeException(msg + e.getMessage))
	}

	def getBaseName(derivName: String): Try[String] = Try {
		import scala.util.matching.Regex
		val RE = """(.+)\.capstxt""".r
		derivName match {
			case RE(baseName) => baseName
			case _ => throw new RuntimeException("derivName does not have capstxt extension")
		}

	}
	//Refactor: get rid of ProgContext
	// Although ProgContext is no longer used in SynthNode, we still use it in the parser
	def getProgramContext(tree: SynthTree): ProgContext = {
		val fSummary: PFFrameSummary = {
			val cFrame = tree.curNode.frame
		    cFrame.getSummary()
		}

		// Add accumulated fresh variables to context for parsing.
		// Required for AssumePre tactic in binary search
		val accFreshVars = tree.curNode.nodeObj match {
		    case cps: CalcProofStep => cps.freshVariables
		    case _ => Nil
		}

		val metaVars = tree.curNode.nodeObj match {
		    case cps: CalcProofStep => cps.metaVars
		    case _ => Nil
		}

        new ProgContext(	varList = fSummary.progFrameSummary.varList ++ accFreshVars ++ metaVars,
    						valList = fSummary.progFrameSummary.valList,
    						dummyList = fSummary.formulaFrameSummary.dummies)
	}

    def parseInt(s: String): Option[Integer] = s match {
		case "inf" => Some(Integer.MAX_VALUE)
		case _ if s.matches("[+-]?\\d+")  => Some(Integer.parseInt(s))
		case _ => None
    }
}
