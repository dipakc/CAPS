package progsynth.provers
import progsynth.types.TermBool
import progsynth.utils
import scala.sys.process._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import collection.immutable.HashMap
import scala.util.control.Breaks._
import PartialFunction._
import scala.xml.Elem
import progsynth.printers.XHTMLPrinters2
import progsynth.config.AppConfig
import progsynth.synthesisnew.Macro
import progsynth.types.MacroExpander
import scala.util.{Try, Success, Failure}

/**
 * Acts as an interface between ProgSynth and the provers.
 */
class PSProverMgr extends {
    //without lazy, the test cases fail (logger is null inside methods)
	lazy val logger= LoggerFactory.getLogger("progsynth.PSProverMgr")

	var proverSequence = AppConfig.configMap("provers.sequence").asInstanceOf[List[String]]
	/*
	var proverSequence = List(
	        					"Z3Prover"
	        					//,"Why3AltErgoProver"
	        					//,"Why3CVC3Prover"
	        					//"Why3SPASSProver"
	        					//,"Why3Z3Prover"
	        				 )
	*/

	def prove(f:TermBool): PSProofStatus =
	    prove2(f).finalStatus


	def expandMacrosAndProve2(f: TermBool, macros: List[Macro]): MultiProverStatus  = {
		val macroFreef = new MacroExpander(macros).expand(f).asInstanceOf[TermBool]
		prove2(macroFreef)
	}

	private def prove2(f: TermBool): MultiProverStatus  = {
	    var result: Option[PSProofStatus] = None
	    var proverStatusList: List[(PSProver, PSProofStatus)]= Nil
	    breakable {
	        proverSequence.foreach { proverStr =>
			    val prover: PSProver = getProver(proverStr)
			    logger.trace(s"Trying prover ${prover.getId}")

			    val sf = f.simplify
			    val resT = prover.prove(sf)
			    val res = resT match {
			    	case Success(status) => status
			    	case Failure(e) => PSProofError(e.getMessage)
			    }

			    logger.trace(multiline(res.toHtml.toString))
			    proverStatusList ::= (prover, res)
			    res match {
			        case PSProofUnknown(_)| PSProofTimeout(_) | PSProofError(_) =>
			        case PSProofValid() | PSProofInvalid(_) =>
			             	result = Some(res)
			             	logger.trace(s"Breaking from the loop.")
			             	break
			    }
	        }
	    }
	    result match {
	        case Some(r) => MultiProverStatus(r, proverStatusList.reverse)
	        case None => MultiProverStatus(PSProofUnknown(""), proverStatusList.reverse)
	    }
	}

	/** Get prover instance from the prover id */
    def getProver(proverId: String): PSProver = {
	    proverId match {
	        case "z3" => new PSZ3Prover()
	        case "why3alt-ergo" => new PSWhy3AltErgoProver()
	        case "why3cvc3" => new PSWhy3CVC3Prover()
	        case "why3spass" => new PSWhy3SPASSProver()
	        case "why3z3" => new PSWhy3Z3Prover()
	    }
	}
}
case class MultiProverStatus(	finalStatus: 	PSProofStatus,
        						proversRetInfo: List[(PSProver, PSProofStatus)])
{
	def isMatching[T](x: T)(pf: PartialFunction[T,Unit]): Boolean = {
	    if (pf.isDefinedAt(x)) {
	        pf.apply(x)
	        true
	    }else {
	        false
	    }
	}

    val isValid = isMatching(finalStatus){case PSProofValid() => }
    def toHtml() = {
	    <div class="MultiProverStatus">
    			<div class="finalStatus">{finalStatus.toShortHtml}</div>
    			{
    			XHTMLPrinters2.mkMinimizableWithTitle(
    			    <div>ProofInfo</div>
    			)(
    				<table class="proversRetInfo" border="1">
	    				{
    					proversRetInfo.map{ case (psProver, status) =>
	    			    	<tr>
	    		        		<td>{psProver.toHtml}</td>
	    		        		<td>{status.toHtml}</td>
	    		        		</tr>
    					}
    				}
	    				</table>
	    		)(minimize=true)
			}
	    	</div>
    }
}
