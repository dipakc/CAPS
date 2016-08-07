package progsynth.provers
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import expt.PSTimeout.ProcessStatus
import scala.PartialFunction._
import scala.util.control.Breaks._

//Symmetric Associative Binary
case class SABOperator(fn: Fn, unitOpt: Option[Const], zeroOpt: Option[Const], idempotent: Boolean) {
	def str() = fn.name
	def getType() = fn.tpe
	def hasZero() = zeroOpt.isDefined
	def hasUnit() = unitOpt.isDefined
}

object SABOperatorRepo {
	val MinIntOpr = SABOperator(MinIntFn, None, None, true)
	val MaxIntOpr = SABOperator(MaxIntFn, None, None, true)
	val PlusIntOpr = SABOperator(PlusIntFn, Some(ConstInt("0")), None, false)
	val TimesIntOpr = SABOperator(TimesIntFn, Some(ConstInt("1")), Some(ConstInt("0")), false)

	def getSABOperator(fn: Fn): Option[SABOperator] = fn match {
		case MinIntFn => Some(MinIntOpr)
		case MaxIntFn => Some(MaxIntOpr)
		case PlusIntFn => Some(PlusIntOpr)
		case TimesIntFn => Some(TimesIntOpr)
	}
}

case class GenQTermLoHi(
	val opr: SABOperator,
	val fvs: List[Var],
	val dummy: Var,
	val lo: Term,
	val hi: Term,
	val range: TermBool,
	val term: Term) {

	def isSame(that: GenQTermLoHi) = {
		//TODO: different dummies should be treated as same
		opr == that.opr && fvs == that.fvs && range == that.range && term == that.term
	}
}

object GenQTermLoHi {
	def unapply(minq: Term): Option[(List[Var], Var, Term, Term, TermBool, Term)] = {
		termToGenQTermLoHi(minq).map{ ce =>
			(ce.fvs, ce.dummy, ce.lo, ce.hi, ce.range, ce.term)
		}
	}

	def termToGenQTermLoHi(qterm: Term): Option[GenQTermLoHi] = qterm match {
    	case QTerm(fn, dummy :: Nil, range, term) =>
    		SABOperatorRepo.getSABOperator(fn) match {
    			case Some(opr) =>
		    		extractBounds(range, dummy) match {
		    			case Some((lo, hi, newRange)) =>
		    				val fvs = qterm.getFreeVars()
		    				Some(GenQTermLoHi(opr, fvs, dummy, lo, hi, newRange, term))
		    			case None =>
		    				None
		    		}
    			case None => None
    		}
    	case _ => None
	}

	//Refactor: Check if this function is necessary
	def extractBounds(range: TermBool, dummy: Var ): Option[(Term, Term, TermBool)] = {
		(range, dummy) match {
			case BoundsExtractorChained(loOpt, hiOpt, newRange) =>
	    		(loOpt, hiOpt) match {
	    			case (Some(l), Some(h)) => Some((l, h, newRange))
	    			case _ => None
	    		}
			case _ => None
		}
    }

}