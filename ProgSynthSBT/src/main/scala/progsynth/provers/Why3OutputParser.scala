package progsynth.provers
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import expt.PSTimeout.ProcessStatus
import scala.PartialFunction._
import scala.util.control.Breaks._

/* Mixed in Why3InputOutputPrep */
trait Why3OutputParser {

	def parseProverOutput(ps: ProcessStatus): PSProofStatus = {
        condOpt(ps.retcode){
            case None => PSProofTimeout("")
        }.orElse{
	        var retVal: Option[PSProofStatus] = None;
	        breakable{
		        ps.stdout.foreach {line =>
			    	line match {
			    	    case ValidOutput(msg) =>
			    	        retVal = Some(PSProofValid())
			    	        break;
			    	    case InvalidOutput(msg) =>
			    	        retVal = Some(PSProofInvalid(msg))
			    	    	break;
			    	    case UnknownOutput(msg) =>
			    	        retVal = Some(PSProofUnknown(msg))
			    	        break;
			    	    case FailureOutput(msg) =>
			    	        retVal = Some(PSProofUnknown(msg))
			    	        break;
			    	    case HighFailureOutput(msg) =>
			    	        retVal = Some(PSProofUnknown(msg))
			    	        break;
			    	    case TimeoutOutput(msg) =>
			    	        retVal = Some(PSProofTimeout(msg))
			    	        break;
			    	    case _ =>
			    	}
		        }
	        }
	        retVal
	    }.orElse{
	        Some(PSProofUnknown(ps.stdout.mkString("\n") + ps.stderr.mkString("\n")))
	    }.get
	}
	//==============================================
	val ValidOutput = ".* Test G : Valid (.*)".r
    val InvalidOutput = ".* Test G : Invalid (.*)".r
    val UnknownOutput = ".* Test G : Unknown (.*)".r
    val FailureOutput = ".* Test G : Failure (.*)".r
    val HighFailureOutput = ".* Test G : HighFailure (.*)".r
    val TimeoutOutput = ".* Test G : Timeout (.*)".r
}