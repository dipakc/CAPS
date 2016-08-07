package progsynth.synthesisold
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Node


/**interface for Main Tactic. */
trait PSMainTactic {
	def applyTactic(up: UnknownProg, ctx: Option[ProgContext]): List[PSTacticResult]
}

trait PSTacticResult {
	def resultProg: Option[ProgramAnn]
	def toHtml(): Seq[Node]
}

trait PSTactic

