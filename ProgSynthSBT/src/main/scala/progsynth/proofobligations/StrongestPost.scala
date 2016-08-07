package progsynth.proofobligations
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns._
import progsynth.synthesisnew.PSTacticsHelper._
import progsynth.utils.SimplifyAuto
import progsynth.utils.QuantUtils
import TermBool.TrueT
import TermBool.FalseT
import progsynth.utils.SimplifyAuto
import scala.util.{Try, Success => TSuccess, Failure => TFailure}

object StrongestPost extends StrongestPost

trait StrongestPost {
	def oldVar(aVar: Var) = {
		aVar.rename(_ + "0")
	}

//	def computeWeakenedStrongestPost(prog: ProgramAnn, pre: TermBool): Try[TermBool] = {
//
//		val newProg = prog match {
//			case asgnProg: Assignment => getSimplifiedAssignment(asgnProg, pre.getFreeVars)
//			case _ => prog
//		 }
//		computeStrongestPost(newProg, pre, simplify = true)
//	}

	def computeStrongestPost(prog: ProgramAnn, pre: TermBool)(simplify: Boolean, weakened: Boolean): Try[TermBool] = {
		prog match {
			case IfProgC(grdcmds) =>
				//TODO: Implement
				TFailure(new Exception("computeStrongestPost not implemented for 'If' Program construct"))
			case comp: Composition => computeStrongestPostComp(comp, pre)(simplify, weakened)
			case asgn: Assignment => computeStrongestPostAsgn(asgn, pre)(simplify, weakened)
			case vd: VarDefProg => computeStrongestPostVarDef(vd, pre)(simplify, weakened)
			case vd: ValDefProg => computeStrongestPostValDef(vd, pre)(simplify, weakened)
			case _ =>
				//TODO: implement.
				TFailure(new Exception("computeStrongestPost not implemented for the program construct"))
		 }
	}

	private def computeStrongestPostComp(comp: Composition, pre: TermBool)(simplify: Boolean, weakened: Boolean) = {
		val CompositionC(programs)  = comp
		val zero: Try[TermBool] = TSuccess(pre)
		programs.foldLeft(zero){(spTry: Try[TermBool], prog: ProgramAnn) =>
			spTry.flatMap(sp =>
				computeStrongestPost(prog, sp)(simplify, weakened))
		}
	}
	private def computeStrongestPostVarDef(vd: VarDefProg, pre: TermBool)
		(simplify: Boolean, weakened: Boolean): Try[TermBool] =
	vd match {
		case VarDefProgC(lhs, Some(rhs)) =>
			val asgn = mkAssignment(vd.pre, lhs, rhs, vd.post)
			computeStrongestPostAsgn(asgn, vd.pre.term)(simplify, weakened)
		case VarDefProgC(lhs, None) =>
			TSuccess(vd.pre.term)
	}

	private def computeStrongestPostValDef(vd: ValDefProg, pre: TermBool)
		(simplify: Boolean, weakened: Boolean): Try[TermBool] =
	vd match {
		case ValDefProgC(lhs, Some(rhs)) =>
			val asgn = mkAssignment(vd.pre, lhs, rhs, vd.post)
			computeStrongestPostAsgn(asgn, vd.pre.term)(simplify, weakened)
		case ValDefProgC(lhs, None) =>
			TSuccess(vd.pre.term)
	}

	private def computeStrongestPostAsgn(asgn: Assignment, pre: TermBool)(simplify: Boolean, weakened: Boolean) = {

		val newAsgn = if (weakened) getSimplifiedAssignment(asgn, pre.getFreeVars) else asgn
		val AssignmentExprC(varTerms) = newAsgn
		val xs = varTerms map {_._1}
		val es = varTerms map {_._2}
		val x0s = xs map oldVar
		val e0s = es map (_.replaceVarsSim(xs, x0s))
		val pre0 = pre.replaceVarsSim(xs, x0s)

		// sp(x, y:= ex, ey, pre)
		// = (∃x0 y0 :: x = ex[x, y := x0, y0 ] ∧ y  = ey[x, y := x0, y0 ] ∧  pre[x, y := x0, y0 ])

		val eQs = (xs.zip(e0s) map {
			case (x, e0) => x eqeq e0
		})

		val range = TermBool.mkConjunct(eQs)
		val body = pre0
		val fvs = range.getFreeVars ++ body.getFreeVars
		val qvs = x0s intersect fvs
		val spTry: Try[TermBool] = qvs match {
			case Nil => TSuccess((range && body).simplify)
			case _ => TSuccess((ExistsTermBool(qvs, range && body).simplify))
		}

		if(simplify) {
			spTry
			.map(QuantUtils.eliminateOldVars(TrueT, _))
			.map(QuantUtils.quantifierIn)
			.map{ sp =>
				QuantUtils.simplifyQuantifiedTerms(axms = TrueT, sp, qvs)
			}
		} else
			spTry
	}

	private def getSimplifiedAssignment(prog: Assignment, impVars: List[Var]): Assignment = {
		val AssignmentExpr(varTermsOrg, _, _) = prog
		val xyMap = varTermsOrg.toMap
		val xs = varTermsOrg map {_._1}
		var filteredXs = xs.intersect(impVars)


		var processedVars: List[Var] = Nil
		def process(x: Var): Unit = {
			if(processedVars contains x )
				return
			processedVars = x :: processedVars
			if (xs contains x)
				filteredXs = filteredXs :+ x
				xyMap(x).getFreeVars.foreach { v =>
					process(v)
			}
		}

		for (x <- filteredXs) {
			process(x)
		}

		val varTerms = varTermsOrg.filter(filteredXs contains _._1 )

		AssignmentExpr(varTerms, prog.pre, prog.post)
	}
}
