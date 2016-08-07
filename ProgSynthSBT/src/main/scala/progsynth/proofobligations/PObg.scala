package progsynth.proofobligations
import progsynth.types._
import progsynth.types.Types._

class PObg(val term: TermBool, var status: Option[Z3Result]) {
	override def toString() = "PObg(" + term.toString + ", " + status.toString + ")"
	
}

object PObg {
	def apply(term: TermBool, status: Option[Z3Result] = None) = {
		new PObg(term, status)
	}
}
