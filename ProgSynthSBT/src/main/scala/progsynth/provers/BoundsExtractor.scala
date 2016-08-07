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

//(LoExtractAtom.unapply(tbDummy), HiExtractAtom.unapply(tbDummy))

object BoundsExtractor {
	object LoExtractAtom {
		/*Extract lo bounds from Predicate */
		def extractLowerBound(term: TermBool, dummy: VarInt): Option[TermInt] = {
			val Dm = dummy
			
			def isDummyFree(t: TermInt) = t.isFreeOf(Dm)
			
			term match {
				case lo LE Dm if isDummyFree(lo) => Some(lo)
				case Dm GE lo if isDummyFree(lo) => Some(lo)
	
				case lo LT Dm if isDummyFree(lo) => Some(lo + 1)
				case Dm GT lo if isDummyFree(lo) => Some(lo + 1)
				
				case _ => None
			}
		}
	}
	
	object HiExtractAtom  {
		/*Extract hi bounds from Predicate */
		def extractUpperBound(term: TermBool, dummy: VarInt, dummyCheck: Boolean = true): Option[TermInt] = {
			val Dm = dummy
			
			def isDummyFree(t: TermInt) = t.isFreeOf(Dm)
			
			term match {
				case Dm LT hi if isDummyFree(hi) => Some(hi)
				case hi GT Dm if isDummyFree(hi) => Some(hi)
	
				case Dm LE hi if isDummyFree(hi) => Some(hi + 1)
				case hi GE Dm if isDummyFree(hi) => Some(hi + 1)
				
				case _ => None
			}
		}
	}
	
	import LoExtractAtom._
	import HiExtractAtom._
	
	//Returns (lo: Int, hi: Int, rest: TermBool)
	def unapply(tbDummy: (TermBool, VarInt)): Option[(Option[TermInt], Option[TermInt], TermBool)] = {
		val (term, dummy) = tbDummy
		var (curLoOpt, curHiOpt)  = (None: Option[TermInt], None:Option[TermInt])
		val conjuncts = AndNTermBool.unapply(term).get
		var restTms = conjuncts
		
		def isDummyFree(t: TermInt) = t.isFreeOf(dummy)
		
		def removeConjunct(t: TermBool) =
			restTms = restTms.filterNot(_ == t)
		
		for (t <- conjuncts){
			
			val loOpt = extractLowerBound(t, dummy)
			val hiOpt = extractUpperBound(t, dummy)
			
			(loOpt, hiOpt) match {
				case (Some(lo), Some(hi)) =>
					curLoOpt = Some(lo)
					curHiOpt = Some(hi)
					removeConjunct(t)
				case (Some(lo), None) =>
					curLoOpt = Some(lo)
					removeConjunct(t)
				case (None, Some(hi)) =>
					curHiOpt = Some(hi)
					removeConjunct(t)
				case (None, None) =>
					//Do nothing
			}
		}
		Some(curLoOpt, curHiOpt, TermBool.mkConjunct(restTms))
	}
}

object BoundsExtractorChained {
	
	//Returns (lo: Int, hi: Int, rest: TermBool)
	def unapply(tbDummy: (TermBool, VarInt)): Option[(Option[TermInt], Option[TermInt], TermBool)] = {
		val (term, dummy) = tbDummy
		val conjuncts = AndNTermBool.unapply(term).get
		
		def isDummyFree(t: TermInt) = t.isFreeOf(dummy)
		
		var rest = conjuncts
		
		def removeConjunct(t: TermBool) =
			rest = rest.filterNot(_ == t)
		

		val curLBOpt = {
			var curLB: TermInt = dummy
			var offset = 0
			var found = false
			
			for (c <- conjuncts.reverse if !found) {
				c match {
					case x LE y if y == curLB =>
						curLB = x
						if (isDummyFree(curLB)) {
							found = true
							if (y == dummy){
								removeConjunct(c)
							}
						}
					case x LT y if y == curLB =>
						curLB = x
						offset += 1
						if (isDummyFree(curLB)) {
							found = true
							if (y == dummy){
								removeConjunct(c)
							}
						}
					case _ =>
				}
			}
			
			if (found)
				if (offset > 0)
					Some(curLB + offset)
				else if (offset < 0)
					Some(curLB - (-offset))
				else
					Some(curLB)
			else
				None
		}
			
		val curUBOpt = {
			var curUB: TermInt = dummy
			var offset = 0
			var found = false
			
			for (c <- conjuncts if !found) {
				c match {
					case x LE y if x == curUB =>
						curUB = y
						if (isDummyFree(curUB)) {
							found = true
							if (x == dummy){
								removeConjunct(c)
							}
						}
					case x LT y if x == curUB =>
						curUB = y
						offset -= 1
						if (isDummyFree(curUB)) {
							found = true
							if (x == dummy){
								removeConjunct(c)
							}
						}
					case _ =>
				}
			}
			offset += 1 //Since upper bound is non-inclusive
			
			if (found)
				if (offset > 0)
					Some(curUB + offset)
				else if (offset < 0)
					Some(curUB - (-offset))
				else
					Some(curUB)
			else
				None
		}
		
		Some((curLBOpt, curUBOpt, TermBool.mkConjunct(rest)))
	}
}
