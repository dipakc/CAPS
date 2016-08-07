package progsynth.synthesisold
/*
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import scala.collection.mutable.LinkedHashMap
import scala.xml.Node
import progsynth.printers.RetValTacticResultPrinter
import progsynth.printers.RCVTacticResultPrinter
import scalaz._
import Scalaz._

/** tests: tests.synthesis.RCVTacticTest */
object RCVAllComb extends PSMainTactic with PSSynthesizerUtils{
	def applyTactic(unkProg: UnknownProg, context: Option[ProgContext]) : List[RCVResult] = {
		assert(context.isDefined)
		val postF = unkProg.post.formula
		val freeVars = postF.getFreeVars
		val valList = context.get.valList
		/**constants are the common elements in freeVars and valList*/
		val constants = freeVars.toSet.intersect(valList.toSet).filter(_.getType != PSArrayInt).toList
		val allCombinations = (1 to constants.length).flatMap(constants.combinations).toList
		val retList = for( constsToReplace <- allCombinations) yield {
			val newVars = getNewVars(constsToReplace, context.get)
			val rcvTactic = new RCVTactic(constsToReplace, newVars)
			val tacticResultList = rcvTactic.applyTactic(unkProg)
			tacticResultList
		}
		retList.flatten
	}
}


class RCVResult extends PSTacticResult with RCVTacticResultPrinter{
	var initUnk: Option[ProgramAnn] = None
	var whileProg: Option[ProgramAnn] = None
	var whileBodyUnk: Option[ProgramAnn]= None
	var resultProg: Option[ProgramAnn]= None
	var whileGuard: Option[ProgramAnn]= None
	var boundFs: List[FOLFormula]= Nil
}

class RCVTactic(val consts: List[Var], val newvars: List[Var]) extends PSTactic with PSSynthesizerUtils {

	def applyTactic(unkProg: UnknownProg): List[RCVResult] = {
		val constNewVarsPairs = consts zip newvars
		val trueF: FOLFormula = True
		val postF = unkProg.post.formula
		val (postReplaced, boundF, eqvF) =
			constNewVarsPairs.foldLeft((postF, trueF, trueF)) {
			case ((posti, boundi, eqvi), pair)=>
				val newPost = posti.replaceVar(pair._1, pair._2)
				val boundCounjuct =
					if (isArrIndexUpperBound(postF, pair._1)) {
						Atom(Pred("$less$eq", List(pair._2, pair._1)))
					} else True

				val newBound = And(boundi, boundCounjuct)
				val newEqv = And(eqvi, Atom(Pred("$eq$eq", pair._1::pair._2::Nil)))
				(newPost, newBound, newEqv)
		}

		val invF= And(postReplaced, boundF).simplify
		val loopInv: Invariant = Invariant(None, invF, None)
		val guard = Not(eqvF).simplify

		val initUnk = mkUnknownProg(pre = unkProg.pre, upid = 0, post = loopInv)//TODO: set proper unknown id
		val whileBodyUnk = {
			mkUnknownProg(
					pre = Invariant(None, And(invF, guard).simplify(), None),
					upid = 0, //TODO: set proper unknown id
					post = loopInv)
		}

		val whileProg = {
			val grdCmdList = GuardedCmd(guard, whileBodyUnk) :: Nil
			mkWhileProg(
						pre 	=	loopInv,
						loopInv = 	invF.some,
						grdcmds = 	grdCmdList,
						post 	= 	unkProg.post
						)

		}

		val compProg =
			mkComposition (
				pre 		= unkProg.pre,
				programs 	= initUnk ::
							  whileProg ::
							  Nil,
				post 		= unkProg.post
			)

		val rcvResult = new RCVResult()
		rcvResult.initUnk = initUnk.some
		rcvResult.whileProg = whileProg.some
		rcvResult.whileBodyUnk = whileBodyUnk.some
		rcvResult.resultProg = compProg.some

		List(rcvResult)
	}
}
*/