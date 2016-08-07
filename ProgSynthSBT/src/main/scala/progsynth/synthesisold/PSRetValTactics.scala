package progsynth.synthesisold
import progsynth.types._
import progsynth.types.Types._
import scala.collection.mutable.LinkedHashMap
import scala.xml.Node
import progsynth.printers.RetValTacticResultPrinter
import progsynth.proofobligations.POGenerator
import progsynth.provers.ProgramAnnPOProver
//import progsynth.proofobligations.POZ3Prover

/** tests: tests.synthesis.RetValTacticTest */
object RetValMainTactic extends PSMainTactic {
	def applyTactic(unkProg: UnknownProg, ctx: Option[ProgContext]): List[PSTacticResult] = {
		val rvTactic = new RetValTactic(None)
		rvTactic.applyTactic(unkProg, ctx)
	}
}

class RetValResult extends PSTacticResult with RetValTacticResultPrinter {

	var resultProg: Option[ProgramAnn] = None
	override def toString() = {
		resultProg.toString
	}
}

/**
 * input:  {phi} UnkProg(0) { eta[x] }
 * output: {phi} val x = 0  { phi && x = 0 } UnkProg(1) { eta } x { eta[x] } */
class RetValTactic(initTerm: Option[Term]) extends PSTactic {
	def applyTactic(unkProg: UnknownProg, ctx: Option[ProgContext]): List[PSTacticResult] = {
		val preInv = unkProg.pre
		val postInv = unkProg.post
		val rvVarOpt = postInv.rvVar
		val newProg = for (rvVar <- rvVarOpt) yield {
			val initProg = {
				val zeroTerm = initTerm match {
					case Some(iTerm) if (iTerm.getType == rvVar.getType) => iTerm
					case None => rvVar.defaultValue
					case _ => throw new RuntimeException("initTerm type does not match with rvVar type")
				}
				val zeroProg = ExprProg(zeroTerm)
				val initRvT = rvVar eqeq zeroTerm
				mkVarDefProg(
						pre = unkProg.pre,
						lhs = rvVar,
						rhs = zeroProg,
						post = preInv.addConjunct(initRvT))
			}

			//TODO: set proper id
			val unkProgNew =
				mkUnknownProg (
					initProg.post,
					unkProg.upid + 1,
					postInv.removeRvVar)

			val returnProg =
				mkExprProg (
					unkProgNew.post,
					rvVar,
					unkProgNew.post)


			val compProg =
				mkComposition3(
					preInv,
					initProg,
					unkProgNew,
					returnProg,
					postInv)

			compProg.inferAnn()
			POGenerator.populatePOs(compProg)(Nil)//TODO: Global Invs
			ProgramAnnPOProver.provePOs(compProg, Nil)//TODO: handle macros

			val result = new RetValResult
			result.resultProg = Some(compProg)
			result
		}
		newProg.toList
	}
}
