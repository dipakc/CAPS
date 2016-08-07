package progsynth.provers
import progsynth.types._
import progsynth.ProgSynth._
import progsynth.types.Types._
import scala.collection.immutable.{HashMap => IHashMap}
import scala.collection.mutable.{HashMap => MHashMap}
import progsynth.debug.PSDbg._
import progsynth.debug.PSDbg
import progsynth.synthesisnew.Macro
import progsynth.synthesisnew.PSTacticsHelper
import progsynth.proofobligations.Z3Result
import progsynth.proofobligations.POZ3Prover
import progsynth.proofobligations.POGenerator

object ProgramAnnPOProver {



	/** pa_in: input ProgramAnn
	 *  macroList: List of macros
	 *  side effect: updates the proof "status" of the "proofObligs" in "pa_in" and its subprograms
	 *  * */
	def provePOs(pa_in: ProgramAnn, macroList: List[Macro]): Unit = {

	    pa_in.proofObligs map { pobg =>
			try {

				val pm = new PSProverMgr()
				val mps = pm.expandMacrosAndProve2(pobg.term, macroList)
				val zr = new Z3Result()
				mps.finalStatus match {
				    case PSProofValid() =>
				        zr.isValid = true
				    case PSProofInvalid(counterexample) =>
				        zr.isValid = false
				        zr.fullModelDesc = counterexample
				    case PSProofUnknown(info) =>
				        zr.isValid = false
				    case PSProofTimeout(info) =>
				        zr.isValid = false
				}
				pobg.status = Some(zr)//TODO: do not store z3result in pobg.

			    //PSTactics.verify(pobg.term, macroList)

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
	
	def provePO(po: TermBool, macroList: List[Macro]) : MultiProverStatus = {
		val pm = new PSProverMgr()
		pm.expandMacrosAndProve2(po, macroList)
	}

	def setGrdsCompleteFlag(ifProg: IfProg, gInvF: TermBool, macros: List[Macro]): Unit = {
		val grdsCompletePO = POGenerator.getGrdsCompletePO(ifProg, gInvF)
		val mps = provePO(grdsCompletePO, macros)
		mps.finalStatus match {
			case PSProofValid() => ifProg.grdsComplete = true
			case _ => ifProg.grdsComplete = false
		}
	}
}