package progsynth.synthesisnew

import progsynth.synthesisold.ProgContext
import progsynth.types._
import progsynth.types.Types._
import scala.util.control.Breaks._
import scala.util.Try
import scala.util.{Success => TSuccess, Failure => TFailure}

object ContextFinder {
	//returns context of innerObj.
	//innerCtx variables = outerCtx variables + the variables added by outerObj.
	//Note: innerObj might be deep inside outerObj
    //Only used on delete conjunct parsing. TODO: remove it.
	def deleteConjunctFindContext(outerObj: ProgramAnn, outerCtx: ProgContext)(innerObj: Any): Try[ProgContext] = {
		if (innerObj == outerObj)
			return TSuccess(outerCtx)

		outerObj match {
			case FunctionProgC(name, params, retVar, subProg, globalInvs) => //TODO: ensure that globalInvs handled properly
				deleteConjunctFindContext(subProg, outerCtx.addVals(params).addVars(retVar::Nil))(innerObj)
			case ValDefProgC(lhs, Some(rhs)) =>
				deleteConjunctFindContext(rhs, outerCtx)(innerObj)
			case VarDefProgC(lhs, Some(rhs)) =>
				deleteConjunctFindContext(rhs, outerCtx)(innerObj)
			case CompositionC(programs) =>
				var (vars, vals) = (outerCtx.varList, outerCtx.valList)
				var retVal: Try[ProgContext] = TFailure(new Exception("Unable to find context in WhileProg"))
				breakable {(
					for(prog <- programs) {
						val ctx = deleteConjunctFindContext(prog, new ProgContext(vars, vals, Nil))(innerObj) //TODO: dummies set to Nil.
						if (ctx.isSuccess){
							retVal = ctx
							break
						}
						val (newVars, newVals) = getNewVarsVals(prog)
						vars = vars ++ newVars
						vals = vals ++ newVals
					})
				}
				return retVal

			case IfProgC(grdcmds) =>
				var retVal: Try[ProgContext] = TFailure(new Exception("Unable to find context in IfProg"))
				breakable { (for(grdcmd <- grdcmds) {
					val ctx = deleteConjunctFindContext(grdcmd.cmd, outerCtx)(innerObj)
					if(ctx.isSuccess) {
						retVal = ctx
						//TODO: Investigate why break is missing
					}
				})}
				return retVal

			case WhileProgC(_, grdcmds) =>
				var retVal: Try[ProgContext] = TFailure(new Exception("Unable to find context in WhileProg"))
				breakable { (for(grdcmd <- grdcmds) {
					val ctx = deleteConjunctFindContext(grdcmd.cmd, outerCtx)(innerObj)
					if(ctx.isSuccess) {
						retVal = ctx
						//TODO: Investigate why break is missing
					}
				})}
				return retVal
			case UnknownProgC(_) => //TODO: test this. Required for asgnDerivation.
				return TSuccess(outerCtx)
			case AssignmentC( _)
			| 	_: SkipProg
			| 	UnknownProgC(_)
			|   _: AssumeProg
			| 	IdentifierC(_, _)
			| 	LitConstantC(_)
			| 	ExprProgC(_) => TFailure(new Exception("Unable to find context. Reached an atomic node"))
			//case _ =>
			//	println(outerObj)
			//	None
		}
	}

	def getNewVarsVals(prog: ProgramAnn): (List[Var], List[Var]) = {
		prog match {
			case VarDefProgC(lhs, rhs) => (lhs::Nil, Nil)
			case ValDefProgC(lhs, rhs) => (Nil, lhs::Nil)
			case  FunctionProgC(_, _, _, _, _)
			| 	IfProgC(_)
			| 	WhileProgC(_, _)
			| 	CompositionC(_)
			| 	AssignmentC(_)
			| 	SkipProgC(_)
			| 	UnknownProgC(_)
			|	AssumeProgC(_)
			| 	IdentifierC(_, _)
			| 	LitConstantC(_)
			| 	ExprProgC(_) => (Nil, Nil)
		}
	}

	//type t12 = progsynth.synthesisold.PSSynthesizerUtils
}