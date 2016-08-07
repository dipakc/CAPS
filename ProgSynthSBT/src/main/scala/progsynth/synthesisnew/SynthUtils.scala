package progsynth.synthesisnew
import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz._
import scalaz.Scalaz._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.ProgSynth.toRichFormula

object SynthUtils {

	type ==>[T, U] = PartialFunction[T, U]

	def setDisplayId(obj: Any) = obj match {
		case psNode: PSProgTree =>
		    psNode.setDisplayIdAll.get
		case CalcProgStep(prog, focusIdOpt) =>
		    CalcProgStep(prog.setDisplayIdAll.get.asInstanceOf[ProgramAnn], focusIdOpt)
		case _ => obj
	}
}

//1. Multiple types AnnProg, Formula, Term, Invariant etc.
//2. What if the application of certain tactic fails. It shouldn't return same object.
//3.