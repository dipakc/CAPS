package progsynth.types
import org.kiama.rewriting.Rewritable
import progsynth.types._
import progsynth.types.Types._
import progsynth._

// PSProgTree hierarchy docs/ProgSynthDoc.html#PSProgTree

/** Overrides the behavior of [[progsynth.types.CaseRewritable]] to add the pre and post mutable fields to the tree
 * Test class: [[tests.types.CaseRewritableTest]]
 * */
trait ProgramAnnRewritable extends Product with Rewritable with CaseRewritable {
	self: ProgramAnn =>
		override def arity = super.arity
		
		override def deconstruct = (super.deconstruct)
		
		override def reconstruct(arr: Array[Any]) = {
			val newPa: ProgramAnn = super.reconstruct( arr ).asInstanceOf[ProgramAnn]
			//ProofObligs is not part of the tree, but we need to maintain its value.
			newPa._proofObligs = self._proofObligs
			newPa.displayId = self.displayId
			newPa
		}
}
