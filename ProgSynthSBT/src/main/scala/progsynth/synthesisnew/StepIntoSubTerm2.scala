package progsynth.synthesisnew

import progsynth.ProgSynth.Counter
import progsynth.types._
import progsynth.types.Types._
import scala.xml.Elem
import progsynth.utils._
import scalaz.{Forall => SForall, Const => SConst, LT => SLT, GT => SGT, _}
import Scalaz._
import org.kiama.rewriting.Rewriter.{Term=> KTerm, _}
import progsynth.proofobligations.POGenerator
import progsynth.debug.PSDbg
import progsynth.ProgSynth.toRichFormula
import progsynth.printers.XHTMLPrinters2
import SynthUtils._
import progsynth.methodspecs.InterpretedFns._
//import progsynth.provers.FnAlias
import progsynth.methodspecs._

/***********************************************
 * New File
 */
trait StepIntoSubTerm2  {

	def getSubTermSubFrame(f: Term, frm: FormulaFrame)(implicit subId: Int): Option[(Term, FormulaFrame)] = {
	    //debug_start
	    println(progsynth.types.DisplayIdPrinter.toIdString(f))
	    //debug_end
		val retVal =  f match {
			case fb: TermBool => getSubTermSubFrameBBB(fb, frm).orElse(getSubTermSubFrameIIB(fb, frm))
			case fi: TermInt => getSubTermSubFrameIII(fi, frm)
			case _ => None
		}
		retVal map { case (subTerm, subFrame) =>
			(subTerm, collapse(frm, subFrame))
		}
	}


	/**
	 *  Traverses 'f' syntax tree in top down order till the desired subformula is reached.
	 *  Creates a new frame with appropriate axioms, and relation based on the context.
	 *  Assumes top-down numbering.
	 *  */
	def getSubTermSubFrameBBB(f: Term, frm: FormulaFrame)(implicit subId: Int): Option[(Term, FormulaFrame)] = {
		if (f.displayId != subId) {
			f match {
				case NotTermBool(t) =>
					val newFrm = new FormulaFrame(parent = Some(frm), relation = invertRelation(frm.relation))
					getSubTermSubFrame(t, newFrm)
				case AndTermBool(t1, t2) =>
					if(subId < t2.displayId) {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(t2), relation = frm.relation)
						getSubTermSubFrame(t1, newFrm)
					} else {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(t1), relation = frm.relation)
						getSubTermSubFrame(t2, newFrm)
					}
				case OrTermBool(t1, t2) =>
					if(subId < t2.displayId) {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(!t2), relation = frm.relation)
						getSubTermSubFrame(t1, newFrm)
					} else {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(!t1), relation = frm.relation)
						getSubTermSubFrame(t2, newFrm)
					}
				case ImplTermBool(t1, t2) =>
					if(subId < t2.displayId) {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(!t2), relation = invertRelation(frm.relation))
						getSubTermSubFrame(t1, newFrm)
					} else {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(t1), relation = frm.relation)
						getSubTermSubFrame(t2, newFrm)
					}
				case RImplTermBool(t1, t2) =>
					if(subId < t2.displayId) {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(t2), relation = frm.relation)
						getSubTermSubFrame(t1, newFrm)
					} else {
						val newFrm = new FormulaFrame(parent = Some(frm), axioms = List(!t1), relation = invertRelation(frm.relation))
						getSubTermSubFrame(t2, newFrm)
					}
				case EqEqEqTermBool(t1, t2) =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, frm)
					} else {
						getSubTermSubFrame(t2, frm)
					}
				case ForallTermBool(dummies, range, term) =>
					if(subId < term.displayId) {
						//step into range
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(!term),
										relation = invertRelation(frm.relation))
						getSubTermSubFrame(range, newFrm)
					} else {
						//step into term
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(range),
										relation = frm.relation)
						getSubTermSubFrame(term, newFrm)
					}
				case ExistsTermBool(dummies, range, term) =>
					if(subId < term.displayId) {
						//step into range
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(term),
										relation = frm.relation)
						getSubTermSubFrame(range, newFrm)

					} else {
						//step into term
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(range),
										relation = frm.relation)
						getSubTermSubFrame(term, newFrm)
					}
				case TermBool.TrueT => None //Reached leaf node
				case TermBool.FalseT => None //Reached leaf node
				case _: UnkTerm => None //Reached leaf node
				case _ => None
			}
		}
		else {
			Some((f, frm))
		}
	}

	def getSubTermSubFrameIIB(f: TermBool, frm: FormulaFrame)(implicit subId: Int): Option[(Term, FormulaFrame)] = {
		//inner relation 1
		def ir1(aFn: Fn) =  aFn match {
			case ImplBoolFn => LEBoolFn
			case EquivBoolFn => EqEqBoolFn
			case RImplBoolFn => GEBoolFn
			case _ => throw new RuntimeException(s"Only BooleanFn supported. Found function {aFn.name} ")
		}

		//inner relation 2
		def ir2(aFn: Fn) =  aFn match {
			case ImplBoolFn => GEBoolFn
			case EquivBoolFn => EqEqBoolFn
			case RImplBoolFn => LEBoolFn
			case _ => throw new RuntimeException(s"Only BooleanFn supported. Found function {aFn.name} ")
		}

		//Make child frame
		def mkChildFrm(innerRel: Fn): FormulaFrame = new FormulaFrame(parent = Some(frm), relation = innerRel)
		def mkChildFrm1() =  mkChildFrm(ir1(frm.relation))
		def mkChildFrm2() =  mkChildFrm(ir2(frm.relation))

		if (f.displayId == subId) {
			Some((f, frm))
		} else {
			f match {
				case  t1 LE t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm2)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case  t1 LT t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm2)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case  t1 GE t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm2)
					}
				case  t1 GT t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm2)
					}
				case  t1 EqEq t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm2)
					}
				case _ => None
			}
		}
	}

	def getSubTermSubFrameIII(f: TermInt, frm: FormulaFrame)(implicit subId: Int): Option[(Term, FormulaFrame)] = {
		//inner relation 1
		def ir1(aFn: Fn) =  aFn match {
			case LEBoolFn => LEBoolFn
			case EqEqBoolFn => EqEqBoolFn
			case GEBoolFn => GEBoolFn
			case _ => throw new RuntimeException(s"Function ${aFn.name} Not supported")
		}

		//inner relation 2
		def ir2(aFn: Fn) =  aFn match {
			case LEBoolFn => LEBoolFn
			case EqEqBoolFn => EqEqBoolFn
			case GEBoolFn => GEBoolFn
			case _ => throw new RuntimeException(s"Function ${aFn.name} Not supported")
		}

		//inner relation 3
		def ir3(aFn: Fn) =  aFn match {
			case LEBoolFn => ImplBoolFn
			case EqEqBoolFn => EqEqBoolFn
			case GEBoolFn => RImplBoolFn
			case _ => throw new RuntimeException(s"Function ${aFn.name} Not supported")
		}

		//Make child frame
		def mkChildFrm(innerRel: Fn): FormulaFrame = new FormulaFrame(parent = Some(frm), relation = innerRel)
		def mkChildFrm1() =  mkChildFrm(ir1(frm.relation))
		def mkChildFrm2() =  mkChildFrm(ir2(frm.relation))

		if (f.displayId == subId) {
			Some((f, frm))
		} else {
			f match { //TODO: verity all the rules
				case t1 PLUS t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case t1 MINUS t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm2)
					}
				case t1 TIMES t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case t1 DIV t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case t1 MOD t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case UMINUS(t) =>
					getSubTermSubFrame(t, mkChildFrm1)
				case t1 MIN t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case t1 MAX t2 =>
					if(subId < t2.displayId) {
						getSubTermSubFrame(t1, mkChildFrm1)
					} else {
						getSubTermSubFrame(t2, mkChildFrm1)
					}
				case MAXQ(dummies, range, term) =>
					if (subId < term.displayId) {
						//step into range
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = Nil,
										relation = ir3(frm.relation))
						getSubTermSubFrame(range, newFrm)

					} else {
						//step into term
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(range),
										relation = ir1(frm.relation))
						getSubTermSubFrame(term, newFrm)
					}
				case PLUSQ(dummies, range, term) =>
					if (subId < term.displayId) {
						//step into range
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = Nil,
										relation = ir3(frm.relation))
						getSubTermSubFrame(range, newFrm)

					} else {
						//step into term
						val newFrm = new FormulaFrame(parent = Some(frm),
										dummies = dummies,
										axioms = List(range),
										relation = ir1(frm.relation))
						getSubTermSubFrame(term, newFrm)
					}
				case _ => None
			}
		}
	}

	def invertRelation(aFn: Fn): Fn = aFn match {
		case EquivBoolFn => EquivBoolFn
		case ImplBoolFn => RImplBoolFn
		case RImplBoolFn => ImplBoolFn
		case _ => throw new RuntimeException("unhandled relation in invertRelation")
	}

	def invertRelationOfFrame(aFrm: FormulaFrame): FormulaFrame = {
		aFrm.copy(relation = invertRelation(aFrm.relation))
	}

	/** Remove intermediate frames
	 *  A(B(C(D()))) ---> A(D())*/
	def collapse(frm: FormulaFrame, subFrame: FormulaFrame) : FormulaFrame = {
		var iSubFrame = subFrame
		while(frm != iSubFrame && Some(frm) != iSubFrame.parent) {
			iSubFrame.absorbParentFormulaFrame() match {
				case Some(newFrm) => iSubFrame = newFrm
				case None => throw new RuntimeException("Unable to collapse")
			}
		}
		iSubFrame
	}
}