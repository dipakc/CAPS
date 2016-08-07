package progsynth.types

trait ProgramAnnApplyRec { self: ProgramAnn =>
	def applyRec(fun: PartialFunction[ProgramAnn, ProgramAnn]): ProgramAnn = {

		if (fun.isDefinedAt(self))
			fun(self)
		else self match {
			case funProg @ FunctionProgC(_, _, _, annProg, _) =>
				funProg.copy(annProg = annProg applyRec fun)
			case ifProg @ IfProgC(grdcmds) =>
				ifProg.copy(grdcmds = grdcmds map {g => g.copy(cmd = g.cmd applyRec fun)})
			case whileProg @ WhileProgC(_, grdcmds) =>
				whileProg.copy(grdcmds = grdcmds map {g => g.copy(cmd = g.cmd applyRec fun)})
			case compProg @ CompositionC(programs) =>
				compProg.copy(programs = programs map {_ applyRec fun})
			case valDefProg @ ValDefProgC(_, rhsOpt) =>
				valDefProg.copy(rhsOpt = rhsOpt map (_ applyRec fun))
			case varDefProg @ VarDefProgC(_, rhsOpt) =>
				varDefProg.copy(rhsOpt = rhsOpt map (_ applyRec fun))
			case asgn @ AssignmentC(asgns) =>
				asgn.copy(asgns = asgns map {case (aVar, rhs) => (aVar, rhs applyRec fun)})
			case SkipProgC() => self
			case UnknownProgC(id) => self
			case _: AssumeProg => self
			case IdentifierC(_, _) => self
			case LitConstantC(_) => self
			case ExprProgC(_) => self
		}
	}

	/** Returns child PAs */
	def getSubPas(): List[ProgramAnn] = self match {
		case FunctionProgC(name, params, retVar, annProg, _) =>
			List(annProg)
		case ProcedureCallC(_, _) =>
			Nil
		case IfProgC(grdcmds) =>
			grdcmds map {_.cmd}
		case WhileProgC(_loopInvFOpt, grdcmds) =>
			grdcmds map {_.cmd}
		case CompositionC(programs) => programs
		case ValDefProgC(lhs, Some(rhs)) => List(rhs)
		case VarDefProgC(lhs, Some(rhs)) => List(rhs)
		case AssignmentC(asgns) => asgns map {case (aVar, aProg) => aProg}
		case _: SkipProg => Nil
		case _: UnknownProg => Nil
		case _: AssumeProg => Nil
		case ValDefProgC(_, None) => Nil
		case VarDefProgC(_, None) => Nil
		case _: Identifier => Nil
		case _: LitConstant => Nil
		case _: ExprProg => Nil
	}

	/** Returns a list of sub PAs that satisfy the given condition.
	 * If partial function is not defined
	 * If a subPA satisifes the condition, then its sub programs are not returned.*/
//	def collectSubPas(fun: PartialFunction[ProgramAnn, Boolean]): List[ProgramAnn] = {
//
//	    def collectSubPas2
//
//
//	}
}
