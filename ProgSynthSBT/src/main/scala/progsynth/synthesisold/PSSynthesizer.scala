package progsynth.synthesisold
/*
import progsynth.types._
import progsynth.types.Types._
import scala.collection.mutable.LinkedHashMap
import scala.xml.Node
import progsynth.printers.RetValTacticResultPrinter
import progsynth.printers.RCVTacticResultPrinter


object PSSynthesizer extends PSSynthesizerUtils {
	/** main tactics list */
	val tlist: List[PSMainTactic] = List(RetValMainTactic, RCVAllComb)

	/** Synthesize all the unknown sub programs in an annProg:ProgramAnn */
	def synthAllUnkProgs(annProg: ProgramAnn): LinkedHashMap[UnknownProg, List[PSTacticResult]] = {
		val ctxMap = getCtxMap(annProg)
		val unkProgs = getUnkProgs(annProg)
		val retMap = LinkedHashMap[UnknownProg, List[PSTacticResult]]()
		unkProgs foreach { unkProg =>
			retMap += (unkProg -> synthUnkProg(unkProg, ctxMap.get(unkProg)))
		}
		retMap
	}

	/** Synthesize a unknown program given the context */
	def synthUnkProg(unkProg: UnknownProg, ctx: Option[ProgContext]): List[PSTacticResult] = {
		val x = for(t <- tlist) yield {
			t.applyTactic(unkProg, ctx)
		}
		x.flatten
	}
}
*/