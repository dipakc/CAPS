package progsynth.utils

import progsynth.types._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.printers.XHTMLPrinters2._

object QuantUtils {

	private implicit val logger= LoggerFactory.getLogger("progsynth.QuantUtils")

	def eliminateOldVars(axms: TermBool, f: TermBool): TermBool = traceBeginEnd("eliminateOldVars"){

		logger.trace("f")
		logger.trace(multiline(termToHtml(f).toString))
		logger.trace("axms")
		logger.trace(multiline(termToHtml(axms).toString))

		val vars = f.getFreeVars

		def elimQuant(axms: TermBool, f: TermBool)(implicit accDummies: List[Var]): TermBool = traceBeginEnd("elimQuant"){

			logger.trace("f")
			logger.trace(multiline(termToHtml(f).toString))
			logger.trace("axms")
			logger.trace(multiline(termToHtml(axms).toString))
			logger.trace("accDummies")
			logger.trace(inlineList(accDummies.flatMap{d => termToHtml(d).toList}))

			(f match {
				case NotTermBool(fin) =>
					NotTermBool(elimQuant(axms, fin))

				case AndTermBool(f1, f2) =>
					val f1new = elimQuant(axms &&& f2, f1)
					val f2new = elimQuant(axms &&& f1new, f2)
					AndTermBool(f1new, f2new)
				case OrTermBool(f1, f2) =>
					val f1new = elimQuant(axms &&& !f2, f1)
					val f2new = elimQuant(axms &&& !f1new, f2)
					OrTermBool(f1new, f2new)
				case ImplTermBool(f1, f2) =>
					val f2new = elimQuant(axms &&& f1, f2) //step into f2
					val f1new = elimQuant(axms &&& !f2new, f1)//step into f1
					ImplTermBool(f1new, f2new)
				case RImplTermBool(f1, f2) =>
					val f1new = elimQuant(axms &&& f2, f1) //step into f1
					val f2new = elimQuant(axms &&& !f1new, f2) //step into f2
					RImplTermBool(f1new, f2new)
				case EqEqEqTermBool(f1, f2) =>
					val f1new = elimQuant(axms, f1) //step into f1
					val f2new = elimQuant(axms, f2) //step into f2
					EqEqEqTermBool(f1new, f2new)
				case ForallTermBool(dummies, f1, f2) =>
					val commonVars = (vars ++ accDummies).intersect(dummies)
					if (commonVars.isEmpty) {
						val accDummiesUpd =  accDummies ++ dummies
						val f2new = elimQuant(axms &&& f1, f2)(accDummiesUpd) //step into f2
						val f1new = elimQuant(axms &&& !f2new, f1)(accDummiesUpd) //step into f1
						ForallTermBool(dummies, f1new, f2new)
					} else {
						throw new RuntimeException("dummy variable clash")
					}
				case ExistsTermBool(dummies, f1, f2) =>
					val commonVars = (vars ++ accDummies).intersect(dummies)
					if (commonVars.isEmpty) {
						val accDummiesUpd =  accDummies ++ dummies
						val f2new = elimQuant(axms &&& f1, f2)(accDummiesUpd) //step into f2
						val f1new = elimQuant(axms &&& f2new, f1)(accDummiesUpd) //step into f1
						ExistsTermBool(dummies, f1new, f2new)
					} else {
						throw new RuntimeException("dummy variable clash")
					}
				case atom =>
					val fvs = atom.getFreeVars
					val varsToEliminate = accDummies.filter(fvs contains _)
					val ret = varsToEliminate.foldLeft(atom)( (atm: TermBool, aVar: Var) =>
						eliminateVarFromAtom(atm, aVar, axms)
					)
					ret
			}) tap { ret =>
				logger.trace("return value")
				logger.trace(multiline(termToHtml(ret).toString))
			}

		}

		def eliminateVarFromAtom(atom: TermBool, aDummy: Var, axms: TermBool): TermBool = traceBeginEnd{"eliminateVarFromAtom"}{

			logger.trace("atom")
			logger.trace(multiline(termToHtml(atom).toString))
			logger.trace("aDummy")
			logger.trace(multiline(termToHtml(aDummy).toString))
			logger.trace("axms")
			logger.trace(multiline(termToHtml(axms).toString))

			val AndNTermBool(cs) = axms
			(cs.foldLeft(atom){ (a, c) => c match {
				case EqEqEqTermBool(alpha, beta) =>
					val ad = alpha.containsVar(aDummy)
					val bd = beta.containsVar(aDummy)
					(ad, bd) match {
						case (true, false) => a.mapSubTerms({case `alpha` => beta}).asInstanceOf[TermBool]
						case (false, true) => a.mapSubTerms({case `beta` => alpha}).asInstanceOf[TermBool]
						case _ => a
					}
				case EqEqTermBool(alpha, beta) =>
					val ad = alpha.containsVar(aDummy)
					val bd = beta.containsVar(aDummy)
					val ret = (ad, bd) match {
						case (true, false) => a.mapSubTerms({case `alpha` => beta}).asInstanceOf[TermBool]
						case (false, true) => a.mapSubTerms({case `beta` => alpha}).asInstanceOf[TermBool]
						case _ => a
					}
					ret
				case _ => a
				}
			}) tap { ret =>
				logger.trace("return Value")
				logger.trace(multiline(termToHtml(ret).toString))
			}
		}

		elimQuant(axms, f)(Nil)
	}

	// test class: tests.proofobligations.StrongestPostTest
	// Push the outermost quantifier in.
	def quantifierIn(f: TermBool): TermBool =  f match {
		case cf @ ForallTermBool(dummies, AndNTermBool(rs), OrNTermBool(ts)) =>
			val (zrs, drs) = rs.partition(_.isFreeOf(dummies))
			val (zts, dts) = ts.partition(_.isFreeOf(dummies))
			val zrTerm = OrNTermBool(zrs.map(!_))
			val drTerm = OrNTermBool(drs)
			val ztTerm = OrNTermBool(zts)
			val dtTerm = OrNTermBool(dts)
			(zrTerm || ztTerm || ForallTermBool(dummies, drTerm, dtTerm)).simplify

		case cf @ ExistsTermBool(dummies, AndNTermBool(rs), AndNTermBool(ts)) =>
			val (zrs, drs) = rs.partition(_.isFreeOf(dummies))
			val (zts, dts) = ts.partition(_.isFreeOf(dummies))
			val zrTerm = AndNTermBool(zrs)
			val drTerm = AndNTermBool(drs)
			val ztTerm = AndNTermBool(zts)
			val dtTerm = AndNTermBool(dts)
			(zrTerm && ztTerm && ExistsTermBool(dummies, drTerm, dtTerm)).simplify

		case _ => f
	}

	//TODO: dummies might not be same as qvs
	//TODO: conjuct of f1 or f2 might be free of qvs
	def simplifyQuantifiedTerms(axms: TermBool, f: TermBool, qvs: List[Var]): TermBool = {
		def filter(formula: TermBool): Boolean = formula match {
			case ExistsTermBool(`qvs`, _, _) => true
			case _ => false
		}
		new SimplifyAuto().simplifyAuto(axms, EquivBoolFn, f, filter)(f.getFreeVars ++ axms.getFreeVars, Nil)
	}

}