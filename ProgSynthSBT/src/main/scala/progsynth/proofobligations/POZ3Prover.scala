package progsynth.proofobligations
import progsynth.types._
import progsynth.ProgSynth._
import progsynth.types.Types._
import z3.scala._
import scala.collection.immutable.{HashMap => IHashMap}
import scala.collection.mutable.{HashMap => MHashMap}
import progsynth.debug.PSDbg._
import progsynth.debug.PSDbg
import progsynth.synthesisnew.Macro

/**Deprecated. Use ProgramAnnPOProver.*/
object POZ3Prover {
	/** debug start */
	object abc extends progsynth.ProgSynth.Counter
	/** debug end */

	/** pa_in: input ProgramAnn
	 *  macroList: List of macros
	 *  side effect: updates the proof "status" of the "proofObligs" in "pa_in" and its subprograms
	 *  * */
	def provePOs(pa_in: ProgramAnn, macroList: List[Macro]): Unit = {
		pa_in.proofObligs map { pobg =>
			try {
				/** debug start */
				abc.cnt += 1
				PSDbg.writeln0(abc.cnt)
				if(abc.cnt == 21)
					PSDbg.writeln0(abc.cnt)
				/** debug end */

				pobg.status = Z3Prover.expandMacroAndProve(pobg.term, macroList)

			} catch {
				case e if (e.getMessage == null) =>
				    logln("UnknownException in provePOs" + pobg.term.pprint)
				case e =>
					println("Exception in " + this.toString + " tactic " + "\n"
					        + e.getMessage() +
					        "Stack Trace:" + "\n" +
					        e.getStackTrace().map{_.toString()}.mkString("\n") )
				    logln("Exception in provePOs \n" + e.getMessage + /*i.toString +*/ "\n" + pobg.term)
			}
		}

		//call recursively on subprograms
		pa_in.getSubPas() map { subpa =>
			provePOs(subpa, macroList)
			//Note: There are no frames insize a subpa. Therefore, the macro list is the same as
			// that of the outer pgoram.
		}
	}
}
