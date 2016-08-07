package progsynth.provers

import progsynth.config.{PSConfig, AppConfig}
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import scala.xml.Elem
import expt.PSTimeout.ProcessStatus
import scala.util.Try
import progsynth.utils.{PSUtils=>psu}
import scala.sys.process._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import collection.immutable.HashMap
import java.io.File

class PSProverDoc {
<a>
	+-----------------------------------------------------------------+
	|  {type t = PSProver}                                            |
	+-----------------------------------------------------------------+
	|    def getCmd(): String                                         |
	|    def mkProverInput(po: TermBool): String                      |
	|    def parseProverOutput( retCode:  Option[Int],                |
	|    					    out:      List[String],               |
	|    					    err:      List[String]): PSProofStatus|
	|    def prove(po: TermBool): PSProofStatus                       |
    |    val config: Config                                           |
    +----------------^------------------------------------------------+
                     |
       +-------------+-----------------------------+
       |                                           |
       |                                           |
+------+----------------+       +------------------+---------------+
| {type t = PSZ3Prover} |       | {type t = PSWhy3AltErgoProver}   |
+-----------------------+       +----------------------------------+
Traits
1. { type  w = Why3InputPrep /*implements mkParseInput and parseProverOutput for Why3*/}
2. { type t = Why3OutputParser /*implements mkParseInput and parseProverOutput for Why3*/}

</a>.text
}

abstract class PSProver(val proverConfig: PSConfig) extends ProverExec {
	def getCmd(fileName: String): String
	/** Get cmd with simple theory (without induction axiom) */
	def getCmdSimple(fileName: String): Option[String]
	def mkProverInput(po: TermBool): String
	def parseProverOutput(processStatus: ProcessStatus): PSProofStatus
	//def prove(po: TermBool): Try[PSProofStatus]
	val config: Config
	def toHtml(): Elem
	def getId(): String
	def getInputFileExtension(): String
}


trait ProverExec { self: PSProver =>

	lazy val loggerProverExec = LoggerFactory.getLogger("progsynth.ProverExec")

	val dataDir = self.proverConfig.configMap("application.data").asInstanceOf[String]
	val sep = File.separator

	val tmpDirName = dataDir + sep + "tmp"

	def prove(po: TermBool): Try[PSProofStatus] = Try{
	    //val logger= LoggerFactory.getLogger("progsynth.PSProverMgr")
	    /**/loggerProverExec.trace(beginSection("ProverExec.prove"))

	    /** Generate prover input*/
	    val proverInput = self.mkProverInput(po)
	    //save the prover input in a temporary file

	    /** Write prover input to a file*/
	    val extension = getInputFileExtension()
	    val uid = TMPDIRID.getId()
	    val fileName = raw"""${tmpDirName}${sep}po${uid}.${extension}"""
	    psu.overwriteFile(fileName, proverInput)

    	def executeCommand(cmd: String): PSProofStatus = {
    	    val timeout = self.config.getParam("timeout").get.toLong
    	    //val processStatus = expt.PSTimeout.runProcessTimeout(cmd, timeout)
    	    val processStatus = expt.PSTimeout.runProcess(cmd)//TODO: enable timeout

    	    println(processStatus.stderr.mkString("\n"))//This includes informational messages as well (for ssh wrapper).

    	    /** Parse the command output */
    	    self.parseProverOutput(processStatus)
	    }

	    /** Get the command */
    	val cmd = self.getCmd(fileName)
    	println("Prover Command:\n" + cmd); loggerProverExec.trace("Prover Command:"); loggerProverExec.trace(cmd)

	    var proofStatus = executeCommand(cmd)

	    val enableSimple = false
	    if(enableSimple) {

    	    proofStatus match {
    	        case PSProofUnknown(_) | PSProofTimeout(_) | PSProofError(_) =>
            	    /** Get the simple command (theories without induction axiom )*/
            	    val simpleCmdOpt = self.getCmdSimple(fileName)
            	    simpleCmdOpt match {
            	        case Some(simpleCmd) =>
                	        println("Prover Command:\n" + simpleCmd); loggerProverExec.trace("Prover Command:"); loggerProverExec.trace(simpleCmd)
                	        proofStatus = executeCommand(simpleCmd)
            	        case None =>
            	    }
    	        case _ =>
    	    }
	    }

	    /**/loggerProverExec.trace(endSection("ProverExec.prove"))
	    proofStatus
	}
}

object TMPDIRID {
    private var id = 0

    private val max = 100

    private def incr() = if (id < max ){ id = id + 1 } else { id = 0 }

    def getId() = {
        incr()
        id
    }

}
