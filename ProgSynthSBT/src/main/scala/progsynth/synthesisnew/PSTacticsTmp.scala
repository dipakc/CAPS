package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz.{Forall => SForall, Const => SConst, _}
import Scalaz._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import progsynth.printers.XHTMLPrinters2
import SynthUtils._
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.provers._
import PartialFunction._
import TacticDocRepo._
import XHTMLPrinters2.termToHtml
import XHTMLPrinters2.macroToHtml
import PSTacticsHelper._
import progsynth.utils.PSErrorCodes._
import scala.util.{Try, Success => TSuccess, Failure => TFailure}
import progsynth.proofobligations.StrongestPost

//Propagates the pre of the subprogram with displayId1 to the post of the subprogram with displayId2
// TODO: Not working as the displayId changes after rewrite.
case class PropagateAssertionsDownSPTactic(displayId1: java.lang.Integer, displayId2: java.lang.Integer) extends PrgFunTactic {

	val logger= LoggerFactory.getLogger("progsynth.PropagateAssertionsDownSPTactic")

	override val tName = "PropagateAssertionsDownSP"

	def getHint(): Elem = {
		import XHTMLPrinters2.termToHtml

		<div>
			<table border='1' class='tablestyle'>
				<tr><th colspan="2">PropagateAssertionsDownSP</th></tr>
			</table>
			<div>{ PSTacticsHelper.docLink(tName) }</div>
		</div>
	}

	def getRelation() = None

	override def progFun(nodeObj: ProgramAnn, frame: Frame): Try[ProgramAnn] = Try {
	    (nodeObj, frame) match {
    		case (pa: ProgramAnn, frame) =>

    			val queryStr = strategy {
    				case prg: ProgramAnn =>
    					propagateDown(prg, displayId1, displayId2)
    			}

    			val res = oncetd(queryStr)(pa)
    			res match {
    				case Some(modifiedProg) => modifiedProg.asInstanceOf[ProgramAnn]
    				case None => throw new RuntimeException("PropagateAssertionsDown tactic application failed.")
    			}
    		case _ =>
    		    throw new RuntimeException("Tactic not applicable")
	    }
	}

	def propagateDown(prg: ProgramAnn): Option[ProgramAnn] =
		propagateDown(prg, prg.displayId, prg.displayId)

	def propagateDown(prg: ProgramAnn, a: Int, b: Int): Option[ProgramAnn] = {

		logger.trace(beginSection("PropagateDown"))

		logger.trace("prg.displayId: " + prg.displayId + "a: " + a + " b: " + b )

		val perfectMatch = prg.displayId == a && prg.displayId == b

		val retVal:Option[ProgramAnn] = (prg match {

			case _: WhileProg if (prg.displayId == a || prg.displayId == b) =>
				None

			case WhileProgSingle(invOpt, GuardedCmd(grd, cmd), pre, post)
			if hasSubProg(cmd, a) && hasSubProg(cmd, b) =>
				propagateDown(cmd, a, b)

			case ifp @ IfProgC(grdcmds) if perfectMatch =>
				val newGCs = for {
					GuardedCmd(grd, cmd) <- grdcmds
					cmd2 = cmd.setPre(ifp.pre.and(grd))
					cmd3 <- propagateDown(cmd2)
				} yield
					GuardedCmd(grd, cmd3)

				if(newGCs.length == grdcmds.length) {
					val post = TermBool.mkDisjunct(newGCs.map(_.cmd.post.term))
					Some(IfProg(newGCs, ifp.pre, post.inv).setDisplayIdPA(ifp.displayId))
				} else
					None


			case ifp @ IfProgC(grdcmds) if ifp.displayId == a =>
				val (gcs1, theGCOpt, gcs2) = span1(grdcmds, (gc: GuardedCmd) => hasSubProg(gc.cmd, b))

				for {
					GuardedCmd(grd, cmdb) <- theGCOpt
					cmdb2 = cmdb.setPre(ifp.pre.and(grd))
					cmdb3 <- propagateDown(cmdb2, cmdb2.displayId, b)
					newGrdCmd = GuardedCmd(grd, cmdb3)
				} yield {
					IfProg(gcs1 ++ (newGrdCmd :: gcs2), ifp.pre, ifp.post)
					.setDisplayIdPA(ifp.displayId)
				}

			case ifp @ IfProgC(grdcmds) =>
				val (gcs1, theGCOpt, gcs2) =
					span1(grdcmds, (gc: GuardedCmd) => hasSubProg(gc.cmd, a) && hasSubProg(gc.cmd, b))

				for {
					GuardedCmd(grd, cmdab) <- theGCOpt
					cmdabNew <- propagateDown(cmdab, a, b)
					newGrdCmd = GuardedCmd(grd, cmdabNew)
				} yield {
					IfProg(gcs1 ++ (newGrdCmd :: gcs2), ifp.pre, ifp.post)
					.setDisplayIdPA(ifp.displayId)
				}

			//case cmp @ CompositionC(prgs) if perfectMatch =>
			//	var iPre = cmp.pre
			//	val newPrgs = for {
			//		prg <- prgs
			//		prg2 = prg.setPre(iPre)
			//		prg3 <- propagateDown(prg2)
			//		iPre = prg3.post
			//	} yield
			//		prg3
			//	if(newPrgs.length == prgs.length)
			//		Some(buildComposition(newPrgs).setDisplayIdPA(cmp.displayId))
			//	else
			//		None

			case cmp @ CompositionC(prgs) if hasSubProg(cmp, a) && hasSubProg(cmp, b) =>
				var iPre = cmp.pre
				var done = false
				var start = cmp.displayId == a

				val newPrgOpts = for(prg <- prgs) yield {

					if(!start && hasSubProg(prg, a)){
						start = true
					}

					if(start && !done){
						if (hasSubProg(prg, b))	done = true

						val prg2 = prg.setPre(iPre)

						propagateDown(prg2, prg2.displayId, if (done) b else prg2.displayId) match {
							case Some(prg3) =>
								iPre = prg3.post
								Some(prg3)
							case None => None
						}
					} else
						Some(prg)
				}

				lo2ol(newPrgOpts).map(prgs => buildComposition(prgs).setDisplayIdPA(cmp.displayId))

			case skp @ SkipProg(pre, post) if perfectMatch=>
				Some(skp.withNewParams(post = pre))

			case unk @ UnknownProg(_, pre, post) if perfectMatch =>
				None

			case asm @ AssumeProg(theta, pre, _) if perfectMatch =>
				Some(asm.withNewParams(post = pre.and(theta)))

			case asgn @ Assignment(_, pre, _) if perfectMatch =>
				(StrongestPost.computeStrongestPost(asgn, pre.term)(simplify = true, weakened = false) map { spF =>
					asgn.withNewParams(post = spF.inv)
				}).toOption

			case vd @ VarDefProgC(_, None) if perfectMatch =>
				 Some(vd.withNewParams(post = vd.pre))

			case vd @ VarDefProgC(_, Some(rhs)) if perfectMatch =>
				(StrongestPost.computeStrongestPost(vd, vd.pre.term)(simplify = true, weakened = false) map { spF =>
					vd.withNewParams(post = spF.inv)
				}).toOption

			case vd @ ValDefProgC(_, None) if perfectMatch =>
				 Some(vd.withNewParams(post = vd.pre))

			case vd @ ValDefProgC(_, Some(rhs)) if perfectMatch =>
				(StrongestPost.computeStrongestPost(vd, vd.pre.term)(simplify = true, weakened = false) map { spF =>
					vd.withNewParams(post = spF.inv)
				}).toOption

			case _ => None
		})
		retVal.map{ r =>
			logger.trace(endSection("PropagateDown"))
			r
		}
	}
}
