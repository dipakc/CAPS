package progsynth.types
import Types._
import progsynth.utils.PSErrorCodes._

trait ProgramAuxConstructors {
	def mkFunctionProgC
		(name: String, params: List[Var], retVar: Var)
		(pre: InvariantT)
		(annProg: ProgramAnn)
		(post: InvariantT)
		(globalInvs: List[TermBool])=
		{
			new FunctionProg(name, params, retVar, annProg, globalInvs, pre, post)
		}

	def mkFunctionProg
		(name: String, params: List[Var], retVar: Var,
		pre: InvariantT,
		annProg: ProgramAnn,
		post: InvariantT,
		globalInvs: List[TermBool]) =
		{
			new FunctionProg(name, params, retVar, annProg, globalInvs, pre, post)
		}

	def mkFunctionProg2
	(name: String, params: List[Var], retVar: Var,
	annProg: ProgramAnn, globalInvs: List[TermBool]) =
	{
		new FunctionProg(name, params, retVar, annProg, globalInvs, annProg.pre, annProg.post)
	}

	def validateProcedureDef(procDef: ProcedureDef) = {
		//TODO: Implement validateProcedureDef
		// pre should not contain old(.) annotation
		// Free variables in pre should be either in fParams or ghostVars
		// No duplicates in fParams
		// ghostVars should be disjoint from fParams
		// All the frameVars should be in the fParams list
		// Free variables in post should be either in fParams or ghostVars
		// In post, only frame variables can be annotated as old(.)
	}
	
	// ProcedureDef(pre, name, fParams, body, ghostVars, frameVars, post)
	def mkProcedureDef (
	    pre: InvariantT,
	    name: String,
	    fParams: List[Var],
	    body: ProgramAnn,
	    ghostVars: List[Var],
	    frameVars: List[Var],
	    post: InvariantT
	 ): ProcedureDef = {
				
		val pd = ProcedureDef(name, fParams, body, ghostVars, frameVars, pre, post)
		validateProcedureDef(pd)
		pd
	}
	
	def validateProcedureCall(procCall: ProcedureCall) = {
		val procDef = procCall.procDef
		//The number of params should be same as the formal parameters
		if(procDef.fParams.length != procCall.params.length)
			PSError(ErrNumParamMismatch(procDef.fParams.length, procCall.params.length))

		val formalActualMap: Map[Var, Term] = (procDef.fParams zip procCall.params).toMap
		
		//The types of the params should match with the type of formal parameters
		for(fp_ap <- formalActualMap) {
		  	fp_ap match {
		  	  	case (_: VarInt, _: TermInt) =>
		  	  	case (_: VarArrayInt, _: TermArrayInt) =>
		  	  	case (_: VarBool, _: TermBool) =>
		  	  	case (_: VarArrayBool, _: TermArrayBool) =>
		  	  	case _ => PSError(ErrTypeMismatch(fp_ap._1))
		  	}
		}
		
		//The params which are frameVariables should be of type Var
		procDef.frameVars foreach { fv =>
		  	val actualParam = formalActualMap.get(fv).get
		  	actualParam match {
		  	  	case _: Var =>
		  	  	case  _ =>
		  	  	  	PSError(ErrModifiableParamNotLValue(fv))
		  	}
		}
	}
	
	// mkProcedureCall(pre, procDef, params, post)
	def mkProcedureCall (
	    pre: InvariantT,
	    procDef: ProcedureDef,
	    params: List[Term],
	    post: InvariantT
	): ProcedureCall = {
		val pc = ProcedureCall(procDef, params, pre, post)
		//TODO: should be called from ProcedureCall constructor.
		validateProcedureCall(pc)
		pc
	}
	
	def mkIfProgC
		(pre: InvariantT)
		(grdcmds: GuardedCmd*)
		(post: InvariantT) =
		{
			new IfProg(grdcmds.toList, pre, post)
		}

	def mkIfProgCL
		(pre: InvariantT)
		(grdcmds: List[GuardedCmd])
		(post: InvariantT) =
		{
			new IfProg(grdcmds.toList, pre, post)
		}

	def mkIfProg
		(pre: InvariantT,
		grdcmds: List[GuardedCmd],
		post: InvariantT) =
		{
			new IfProg(grdcmds, pre, post)
		}

	def mkIfProg1
		(pre: InvariantT,
		grdcmd: GuardedCmd,
		post: InvariantT) =
		{
			new IfProg(grdcmd :: Nil, pre, post)
		}

	def mkGrdCmdC(grd: TermBool)(cmd: ProgramAnn) = GuardedCmd(grd, cmd)
	
	def mkWhileProgC
		(pre: InvariantT)
		(loopInv: Option[TermBool])
		(grdcmds: GuardedCmd*)
		(post: InvariantT) =
		{
			new WhileProg(loopInv, grdcmds.toList, pre, post)
		}

	def mkWhileProgCL
		(pre: InvariantT)
		(loopInv: Option[TermBool])
		(grdcmds: List[GuardedCmd])
		(post: InvariantT) =
		{
			new WhileProg(loopInv, grdcmds.toList, pre, post)
		}

	def mkWhileProg
		(pre: InvariantT,
		loopInv: Option[TermBool],
		grdcmds: List[GuardedCmd],
		post: InvariantT) =
		{
			new WhileProg(loopInv, grdcmds, pre, post)
		}

	def mkCompositionC
		(pre: InvariantT)
		(programs: ProgramAnn*)
		(post: InvariantT) =
		{
			new Composition(programs.toList, pre, post)
		}

	def mkCompositionCL
		(pre: InvariantT)
		(programs: List[ProgramAnn])
		(post: InvariantT) =
		{
			new Composition(programs.toList, pre, post)
		}

	def mkComposition
		(pre: InvariantT,
		programs: List[ProgramAnn],
		post: InvariantT) =
		{
			new Composition(programs, pre, post)
		}

	def mkComposition2
		(pre: InvariantT,
		program1: ProgramAnn,
		program2: ProgramAnn,
		post: InvariantT) =
		{
			new Composition(program1 :: program2 :: Nil, pre, post)
		}

	def mkComposition3
		(pre: InvariantT,
		program1: ProgramAnn,
		program2: ProgramAnn,
		program3: ProgramAnn,
		post: InvariantT) =
		{
			new Composition(program1 :: program2 :: program3 :: Nil, pre, post)
		}

	def mkIdentifierC
		(pre: InvariantT)
		(name: String, itype: PSType)
		(post: InvariantT) =
		{
			new Identifier(name, itype, pre, post)
		}

	def mkIdentifier
		(pre: InvariantT,
		name: String, itype: PSType,
		post: InvariantT) =
		{
			new Identifier(name, itype, pre, post)
		}

	def mkValDefProgC
		(pre: InvariantT)
		(lhs: Var, rhs: ProgramAnn)
		(post: InvariantT) =
		{
			new ValDefProg(lhs, Some(rhs), pre, post)
		}

	def mkValDefProg
		(pre: InvariantT,
		lhs: Var, rhs: ProgramAnn,
		post: InvariantT) =
		{
			new ValDefProg(lhs, Some(rhs), pre, post)
		}

	def mkValDefProg
		(pre: InvariantT,
		lhs: Var, rhs: Term,
		post: InvariantT) =
		{
			val rhsProg = mkExprProg(pre, rhs, post.updateRvVar(lhs))
			new ValDefProg(lhs, Some(rhsProg), pre, post)
		}

	def mkVarDefProgC
		(pre: InvariantT)
		(lhs: Var, rhs: ProgramAnn)
		(post: InvariantT) =
		{
			new VarDefProg(lhs, Some(rhs), pre, post)
		}

	def mkVarDefProg
		(pre: InvariantT,
		lhs: Var, rhs: ProgramAnn,
		post: InvariantT) =
		{
			new VarDefProg(lhs, Some(rhs), pre, post)
		}

	def mkVarDefProg
		(pre: InvariantT,
		lhs: Var, rhs: Term,
		post: InvariantT) =
		{
			val rhsProg = mkExprProg(pre, rhs, post.updateRvVar(lhs))
			new VarDefProg(lhs, Some(rhsProg), pre, post)
		}

	def mkAssignmentC
		(pre: InvariantT)
		(lhs: Var, rhs: ProgramAnn)
		(post: InvariantT) = {
			new Assignment((lhs, rhs) :: Nil, pre, post)
		}

	def mkAssignment
		(pre: InvariantT,
		lhs: Var, rhs: ProgramAnn,
		post: InvariantT) = {
			new Assignment((lhs, rhs) :: Nil, pre, post)
		}

	def mkAssignments
		(pre: InvariantT,
		asgns: List[(Var, ProgramAnn)],
		post: InvariantT) = {
			new Assignment(asgns, pre, post)
			
			
		}

	def mkAssignmentTerm
		(pre: InvariantT,
		lhs: Var, rhs: Term,
		post: InvariantT) = {
			new Assignment((lhs, mkExprProg(pre, rhs, post.updateRvVar(lhs))) :: Nil, pre, post)
	}

	def mkAssignmentTerms
		(pre: InvariantT,
		lhsRhsPairs: List[(Var, Term)],
		post: InvariantT) = {
			val asgns = lhsRhsPairs.map{case (lhs, rhs) => (lhs, mkExprProg(pre, rhs, post.updateRvVar(lhs)))}
			new Assignment(asgns, pre, post)
		}

	def mkSkipProgC
		(pre: InvariantT)
		(post: InvariantT) =
		{
			new SkipProg(pre, post)
		}

	def mkSkipProg
		(pre: InvariantT,
		post: InvariantT) =
		{
			new SkipProg(pre, post)
		}

	def mkUnknownProgC
		(pre: InvariantT)
		(upid: Int)
		(post: InvariantT) =
		{
			new UnknownProg(upid, pre, post)
		}
	def mkUnknownProg
		(pre: InvariantT,
		upid: Int,
		post: InvariantT) =
		{
			new UnknownProg(upid, pre, post)
		}
	def mkLitConstantC
		(pre: InvariantT)
		(name: String)
		(post: InvariantT) =
		{
			new LitConstant(name, pre, post)
		}

	def mkLitConstant
		(pre: InvariantT,
		name: String,
		post: InvariantT) =
		{
			new LitConstant(name, pre, post)
		}

	def mkExprProgC
		(pre: InvariantT)
		(expr: Term)
		(post: InvariantT) =
		{
			new ExprProg(expr, pre, post)
		}
	
	def mkExprProg
	(pre: InvariantT, expr: Term, post: InvariantT) =
	{
		ExprProg(expr, pre, post)
	}
	
}

/*Build a composition program in chained fashion */
case class CompUtil(compOpt: Option[ProgramAnn], fpre: InvariantT ) {
	
	def compose(getNextProg: InvariantT => Option[ProgramAnn]): CompUtil = {
		
		val newProgOpt =  getNextProg(fpre)
		
		newProgOpt match {
			case Some(newProg) =>
				compOpt match {
					case Some(comp: Composition) =>
						CompUtil(
							compOpt = Some(mkComposition(
								pre = comp.pre,
								programs = comp.programs ++ List(newProg),
								post = newProg.post)),
							fpre = newProg.post)
					case Some(firstProg: ProgramAnn) =>
						CompUtil(
							compOpt = Some(mkComposition(
								pre = firstProg.pre,
								programs = List(firstProg, newProg),
								post = newProg.post)),
							fpre = newProg.post)
					case None =>
						CompUtil(Some(newProg), newProg.post)
				}
			case None => this
		}
	}
}
