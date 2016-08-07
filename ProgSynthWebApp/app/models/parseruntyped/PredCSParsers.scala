package models.parseruntyped

import progsynth.utils.PSUtils
import progsynth.synthesisold.ProgContext
import progsynth.types._

trait PredCSParsers extends PredTParsers with PredValidator with PSUtils with PSWebParserUtils{
	def predCSP(implicit progCtx: ProgContext):Parser[List[Pred]] =
		strengthen(predTP, getPred, "Can not parse pred. Failure in type checking: ")
}

trait PredValidator extends TermValidator with PSUtils {
	def getPred(result: List[PredT])(implicit progCtx: ProgContext): Option[List[Pred]] =  {
		allmap(result, getPred)
	}

	def getPred(aPredT: PredT)(implicit progCtx: ProgContext): Option[Pred] = aPredT match {
		case PredT("BoolPred", t1 :: Nil) =>
			getTermBool(t1) map (bt => Pred("BoolPred", bt :: Nil))
		case PredT(r, ts) =>
			val termIntsOpt = allmap(ts, getTermInt)
			val res = termIntsOpt map {Pred(r, _)}
			res
	}
}