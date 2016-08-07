package progsynth.synthesisnew

import progsynth.types.ProgramAnn
import PSTacticsHelper._

case class CalcProgStep(prog: ProgramAnn, focusId: Option[Int]) extends CalcStep

object CalcProgStepWithInner
{
    //Option[prog: ProgramAnn, focusId: Option[Int], f_prog: Option[ProgramAnn]]
    def unapply(cps: CalcProgStep): Option[(ProgramAnn, Option[Int], Option[ProgramAnn])] = {
        val CalcProgStep(prog, focusId) = cps
        val innerProgOpt = extractSubProgDisplayIdOpt(prog, focusId)
        Some(prog, focusId, innerProgOpt)
    }
}