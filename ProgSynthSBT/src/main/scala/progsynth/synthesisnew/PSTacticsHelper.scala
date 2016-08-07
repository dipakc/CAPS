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

object PSTacticsHelper {
	object NestedComposition {
		def isComposition(p: ProgramAnn) = p match {
			case _: Composition => true
			case _ => false
		}

		def unapply(pa: ProgramAnn) : Option[List[ProgramAnn]] = pa match {
			case cp @ CompositionC(stmts) =>

				val isNested = stmts.exists(isComposition(_))

				if(isNested){
					val subProgs = stmts.flatMap{
						case CompositionC(stmts2) => stmts2
						case subPrg => List(subPrg)
					}
					Some(subProgs)
				}
				else
					None
			case _ => None
		}
	}

	def getSubTermExtractor(displayId: Int) = new {
		def unapply(term: Term): Option[Term] = term match {
			case _ if term.displayId == displayId => Some(term)
			case _ => None
		}
	}

	def extractSubProg(pa: Any, id: Int): Option[Any] = {
		var subProgOpt: Option[Any] = None
		val queryStr = rule { case sub: ProgramAnn if sub.id == id => subProgOpt = Some(sub); sub }
		oncetd(queryStr)(pa) //Run for its side effect.
		subProgOpt
	}

	def extractSubProgDisplayId(pa: Any, did: Int): Option[ProgramAnn] = {
		var subProgOpt: Option[Any] = None
		val queryStr = rule { case sub: ProgramAnn if sub.displayId == did => subProgOpt = Some(sub); sub }
		oncetd(queryStr)(pa) //Run for its side effect.
		subProgOpt map {_.asInstanceOf[ProgramAnn]}
	}

    def extractSubProgDisplayIdOpt(pa: ProgramAnn, didOpt: Option[Int]): Option[ProgramAnn] = {
    	didOpt match {
            case Some(did) => extractSubProgDisplayId(pa, did)
            case None => Some(pa)
        }
    }

	def replaceSubProgDisplayId(outerProg: ProgramAnn, innerDid: Int, newInnerProg: ProgramAnn): Option[ProgramAnn] = {
		val queryStr = rule {
		    case sub: ProgramAnn if sub.displayId == innerDid => newInnerProg
		}

		oncetd(queryStr)(outerProg)
		.map{_.asInstanceOf[ProgramAnn]}
	}

	def replaceSubProgDisplayIdOpt(outerProg: ProgramAnn, innerDidOpt: Option[Int], newInnerProg: ProgramAnn): Option[ProgramAnn] = {
	    innerDidOpt match {
	        case Some(innerDid) => replaceSubProgDisplayId(outerProg, innerDid, newInnerProg)
	        case None => Some(newInnerProg)
	    }
	}

	def docLink(tacticClass: String) = <div><a class="docLink" href = {docURL(tacticClass)} target="_blank">doc</a></div>
	private def docURL(tacticClass: String) = """../assets/docs/Tactics.html#""" + tacticClass

	//TODO: deprecate this and use verityTerm instead
	/** Verify that (axiom => (f Fn newFormula)) */
	def verify(axiom: TermBool, f: TermBool, relation: Fn,
			newFormula: TermBool, macros: List[Macro]): Boolean = {
		//val trueF: TermBool = TermBool.TrueT
		val po = axiom.impl(FnApp(relation, List(f, newFormula)))
		//println(po.pprint)
		/*
		val z3ResOpt = Z3Prover.expandMacroAndProve(po, macros)
		z3ResOpt match {
			case Some(z3Res) if z3Res.isValid =>
				PSDbg.writeln1("valid")
				true
			case Some(z3Res) if !z3Res.isValid =>
				PSDbg.writeln1("not valid")
				false
			case None =>
				PSDbg.writeln1("Can not be be verified by Z3")
				false
		}
		*/
		verify(po, macros)
	}

	/** Verify that (axiom => (term Fn newterm)) */
	def verifyRelation(term: Term, relation: Fn, newTerm: Term)(cps: CalcProofStep, frm: FormulaFrame): Boolean =
	{
		assert(relation.tpe == PSBool)
		val globalInvs = TermBool.mkConjunct(frm.getSummary.progFrameSummary.globalInvs)
		val assumptions = globalInvs && frm.getSummaryAxiomAndConjectures && cps.guard && TermBool.mkConjunct(cps.assumedPreList)
		val macros = frm.getSummary.getMacros()

		val po = assumptions.impl(FnApp(relation, List(term, newTerm)))
		verify(po, macros)
	}

	def verify2	(po: TermBool)(cps: CalcProofStep, frm: FormulaFrame): Boolean =
	{
		val globalInvs = TermBool.mkConjunct(frm.getSummary.progFrameSummary.globalInvs)
		val assumptions = globalInvs && frm.getSummaryAxiomAndConjectures && cps.guard && TermBool.mkConjunct(cps.assumedPreList)
		val macros = frm.getSummary.getMacros()

		val newPo = assumptions.impl(po)
		verify(newPo, macros)
	}

	def verify(po: TermBool, macros: List[Macro]): Boolean = {
		val pm = new PSProverMgr()
		val mps = pm.expandMacrosAndProve2(po, macros)
		mps.finalStatus match {
			case PSProofValid() => true
			case _ => false
		}
	}

    def twoColTable[L, R, LH, RH](entries: List[(L, R)], lfn: L => LH, rfn: R => RH): Elem = {
		<table border='1' class='tablestyle'>
			{
			    for( (left, right) <- entries) yield {
			    	<tr>
			    		<td align="left">{lfn(left)}</td>
			    		<td align="left">{rfn(right)}</td>
			    	</tr>
			    }
			}
		</table>
    }

    def paramTable(entries: List[(String, Any)]): Elem = {
        <div class='paramTable'>
		<table border='1' class='tablestyle'>
			{
			    for( (left, right) <- entries) yield {
			    	<tr>
			    		<th align="left">{left}</th>
			    		<td align="left">{right}</td>
			    	</tr>
			    }
			}
		</table>
		</div>
    }

    def oneColTable[T, U](list: List[T], fn: T => U): Elem = {
        <div>
		<table class='tablestyle'>{ list.map(elem => <tr><td>{fn(elem)}</td></tr>)}</table>
		</div>
	}

	def buildComposition(prgs: List[ProgramAnn]): ProgramAnn =  prgs match {
		case prg :: Nil => prg
		case Nil => throw new RuntimeException("list should not be empty")
		case _ => Composition(prgs, prgs.head.pre, prgs.last.post)
	}

	def collapseOuterMostComposition(comp: ProgramAnn): ProgramAnn = comp match {
		case NestedComposition(stmts) => mkComposition(comp.pre, stmts, comp.post)
		case _ => comp
	}

	def buildComposition(prgs: ProgramAnn*): ProgramAnn = buildComposition(prgs:_*)

	def hasSubProg(prg: ProgramAnn, id: Int): Boolean = {
		var ret = false
		val queryStr = rule { case prg: ProgramAnn if prg.displayId == id => ret = true; prg}
		oncetd(queryStr)(prg)
		ret
	}

	//span1(List(1, 2, 3, 4), (x:Int) => x == 3)) ===> (List(1, 2), Some(3), List(4))
	def span1[A](xs: List[A], f: A => Boolean): (List[A], Option[A], List[A]) = {
		val (xs1, xs2) = xs.span(f)
		xs2 match {
			case Nil => (xs1, None, xs2)
			case x :: rest => (xs1, Some(x), rest)
		}
	}

	def lo2ol[A](lo: List[Option[A]]): Option[List[A]] = {
		if (lo contains None) None else Some(lo.map(_.get))
	}
}

