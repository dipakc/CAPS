package progsynth.proofobligations
import progsynth.types._
import progsynth.types.Types._

trait POData {
	var _proofObligs: List[PObg] = Nil
	def appendPO(po: PObg) = _proofObligs = po :: _proofObligs
	def appendPO(t: TermBool, globalInvFOpt: Option[TermBool] = None) = {
		val t2 = globalInvFOpt map (_.impl(t)) getOrElse t
		_proofObligs = PObg(t2) :: _proofObligs
	}
	def proofObligs = _proofObligs.reverse
}
