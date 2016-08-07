package progsynth.provers
import scala.collection.mutable.Map
import progsynth.types.TermBool
import expt.PSTimeout.ProcessStatus
import progsynth.config.PSConfig
import progsynth.config.AppConfig


trait PSWhy3Prover {

    val proverConfig: PSConfig

    val tbFactory: Why3TBFactory

    def getId() : String

    def proverName: String

    private def why3Path() = proverConfig.configMap("provers.path.why3").asInstanceOf[String]

    private def why3LibPath() = proverConfig.configMap("provers.path.why3lib").asInstanceOf[String] +
                        "/" + "theory" + tbFactory.getId

    private def why3LibPathSimple() = proverConfig.configMap("provers.path.why3lib").asInstanceOf[String] +
                        "/" + "theory" + tbFactory.getId + "/simple"

    def getCmd(fileName: String) =
        s"""$why3Path prove -P $proverName -L $why3LibPath $fileName"""

    /** use if you want to use simple theories (theories without induction axioms) */
    def getCmdSimple(fileName: String): Option[String] =
        Some(s"""$why3Path prove -P $proverName -L $why3LibPathSimple $fileName""")

    def getInputFileExtension() = "mlw"

    private val why3InputPrep = new Why3InputPrep(tbFactory)
    private val why3OutputParser = new Why3OutputParser(){}

    def mkProverInput(po: TermBool): String = {
    	why3InputPrep.mkProverInput(po)
    }

    def parseProverOutput(ps: ProcessStatus): PSProofStatus = {
    	why3OutputParser.parseProverOutput(ps)
    }

    def toHtml() = {
	    <div class="PSProver PS${getId()}">
    		<div class="name">{getId()}</div>
    	</div>
	}

}


class PSWhy3AltErgoProver(   proverConfig: PSConfig   = AppConfig,
                                val tbFactory: Why3TBFactory = Why3TBFactory4)
    extends PSProver(proverConfig) with PSWhy3Prover
{
    val config = new Config(Some(PSProverConfigInit.why3AltErgoConfig), Map())

    def getId() = "Why3AltErgoProver"

    val proverName = "alt-ergo"
}

class PSWhy3CVC3Prover(   proverConfig: PSConfig   = AppConfig,
                          val tbFactory: Why3TBFactory = Why3TBFactory4)
    extends PSProver(proverConfig) with PSWhy3Prover
{
    val config = new Config(Some(PSProverConfigInit.why3CVC3Config), Map())

    def getId() = "Why3CVC3Prover"

    val proverName = "cvc3"
}


class PSWhy3SPASSProver(   proverConfig: PSConfig   = AppConfig,
                           val tbFactory: Why3TBFactory = Why3TBFactory4)
    extends PSProver(proverConfig) with PSWhy3Prover
{
    val config = new Config(Some(PSProverConfigInit.why3SPASSConfig), Map())

    def getId() = "Why3SPASSProver"

    val proverName = "spass"
}


class PSWhy3Z3Prover(   proverConfig: PSConfig   = AppConfig, val tbFactory: Why3TBFactory = Why3TBFactory4)
    extends PSProver(proverConfig) with PSWhy3Prover
{
    val config = new Config(Some(PSProverConfigInit.why3Z3Config), Map())

    def getId() = "Why3Z3Prover"

    val proverName = "z3"
}

