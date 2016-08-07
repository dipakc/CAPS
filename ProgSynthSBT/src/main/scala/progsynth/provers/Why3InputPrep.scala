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
import Why3AST._
import progsynth.logger.PSLogUtils._
import org.slf4j.LoggerFactory
import progsynth.printers.XHTMLPrinters2._

class Why3InputPrep(theoryBuilderFact: Why3TBFactory = Why3TBFactory4) {

    private implicit val logger= LoggerFactory.getLogger("progsynth.Why3InputPrep")

	def mkProverInput(po: TermBool): String = traceBeginEnd("Why3InputPrep.mkProverInput"){
		val tb = theoryBuilderFact.mkTheoryBuilder()
		tb.addPO(preprocess(po))
		val t = tb.getTheory
		t.str() +
		s"\n (* \n ${po.pprint()} \n *) \n"
	}
	//==============================================
    /**
     * Rename variables starting with Capital alphabets.
     * Prepend underscore.
     * */
    private def preprocess(po: TermBool): TermBool = traceBeginEnd("Why3InputPrep.preprocess"){
		logger.trace("po")
    	logger.trace(multiline(termToHtml(po).toString))

        val uCaseVars = po.getFreeVars.filter(_.v.charAt(0).isUpper)
        def prependUScore(aVar: Var) = Var.mkVar("_" + aVar.v, aVar.getType)
        val ret = po.replaceVarsSim(uCaseVars, uCaseVars.map(prependUScore))
        traceTerm("return Value", ret)
        ret
    }
}

