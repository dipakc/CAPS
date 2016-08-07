package progsynth.proofobligations
import progsynth.types._
import progsynth.types.Types._
import scala.collection.immutable.HashMap
import progsynth.ProgSynth._
import progsynth.types._
/**
 * Populates the proof obligations in the [[progsynth.types.ProgramAnn]] object
 *
 * <img src="..\..\..\..\..\src\main\resources\doc_resources\proof_obligs\PO.svg" alt="PO.svg" />
 */
object POGenerator {
  
	val GHOST = "_ghost_"
	val OLD = "_old_"
	val POST = "_post_"
	  
	def isGhost(aVar: Var, fParams: List[Var]) =
	  	!(fParams contains aVar) && !isOld(aVar)
	
	def isOld(aVar: Var) = aVar.v.startsWith(OLD)
	
	def mkOldVar(aVar: Var) = aVar.addPrefix(OLD)
	def mkOldVars(vars: List[Var]) = vars map {mkOldVar _}
	
	def mkGhostVar(aVar: Var) = aVar.addPrefix(GHOST)
	def mkGhostVars(vars: List[Var]) = vars map {mkGhostVar _}
  
    /**
     * Get the Before after predicate from a hoare triple.
     * Example:
     * 
     * { y=0 } x := Something {y = 0 /\ x = old(x) + 1}
     * 
     * y = 0 => y = 0 /\ _post_x = x + 1
     */
	def getBAPredicate(preF: TermBool, postF: TermBool, frameVars: List[Var]) = {
		/**
		 * Applies to Postcondition
		 * Replace frame variable "x" by "_post_x"
		 * Replace old frame variable "_old_x" by "x"
		 * Non-frame variables will remain as they are.
		 */
		def transformPostInBAFormat(post: TermBool, frameVars: List[Var]) = {
			val pairs1: List[(Var, Var)] = frameVars map { fv =>
				(fv, fv.addPrefix(POST))
			}
			val pairs2: List[(Var, Var)] = frameVars map { fv =>
				(fv.addPrefix(OLD), fv)
			}
			val pairs = pairs1 ++ pairs2
			val tMap: Map[Var, Var] = Map(pairs: _*)
			
			post.replaceVarsSim(tMap)
		}
	  
		preF impl transformPostInBAFormat(postF, frameVars)
	}
	
	def formalToActual(formalF: TermBool)
		(fParams:List[Var], frameVarsFormal: List[Var], aParams: List[Term]): TermBool =
	{
	    /** Replace formal params with actual params*/
	    val x = formalF.replaceVarsSim(fParams, aParams)
	    /** Replace old frame vars with actual old frame vars*/
	    val y = {
	    	val frameVarsActual = getFrameVarsActual(frameVarsFormal)(fParams, aParams)
	    	val oldFrameVarsFormal = mkOldVars(frameVarsFormal)
	    	val oldFrameVarsActual = mkOldVars(frameVarsActual)
	    	x.replaceVarsSim(oldFrameVarsFormal, oldFrameVarsActual)
	    }
	    /** Prepend ghost variables with GHOST to prevent clash with program variables */
	    val ghostVars = formalF.getFreeVars().filter(isGhost(_, fParams))
	    val z = y.replaceVarsSim(ghostVars, ghostVars.map(_.addPrefix(GHOST)))
	    z
	}
	
	def getFrameVarsActual(frameVarsFormal: List[Var])(fParams:List[Var], aParams: List[Term]): List[Var] = {
	    val faMap: Map[Var, Term] = fParams zip aParams toMap;
	    frameVarsFormal map (fv => faMap.get(fv).get.asInstanceOf[Var])
	}
		
	def populatePOs(pa_in: ProgramAnn)(implicit globalInvs: List[TermBool]): Unit = {
		val gInvFOpt = if (!globalInvs.isEmpty) Some(TermBool.mkConjunct(globalInvs)) else None

		val (pre, post) = (pa_in.pre, pa_in.post)
		pa_in match {
			case FunctionProgC(name, params, retVar, pa, _) => populatePOs(pa)
			
			case ProcedureCallC(procDef @ ProcedureDefC(name, fParams, body, ghostVars, frameVarsFormal), aParams) =>
				val preDefFormal = procDef.pre.term
				val postDefFormal = procDef.post.term
			  
				val preDefActual = formalToActual(preDefFormal)(fParams, frameVarsFormal, aParams)
				val postDefActual = formalToActual(postDefFormal)(fParams, frameVarsFormal, aParams)
				val frameVarsActual = getFrameVarsActual(frameVarsFormal)(fParams, aParams)
			  
				val defBAPredicate = getBAPredicate(preDefActual, postDefActual, frameVarsActual)
				val ghostQuantifiedDefBAPredicate = {
					if (ghostVars.isEmpty)
						defBAPredicate
					else {
						ForallTermBool(mkGhostVars(ghostVars), TermBool.TrueT, defBAPredicate)
					}
				}
			  
				val callBAPredicate = getBAPredicate(pre.term, post.term, frameVarsActual)
			  
				pa_in appendPO (ghostQuantifiedDefBAPredicate impl callBAPredicate)
			
			case CompositionC(subpas) =>
				assert(subpas.length > 0)
				pa_in appendPO (poPreToPre(pa_in, subpas.head, TermBool.TrueT), gInvFOpt)
				for (pa1::pa2::Nil <- subpas.sliding(2)) {
					pa_in.appendPO(poPostToPre(pa1, pa2), gInvFOpt)
				}
				pa_in appendPO (poPostToPost(subpas.last, pa_in), gInvFOpt)
				subpas.foreach(populatePOs(_))
			case IfProgC(grdcmds) =>
				assert(grdcmds.length > 0)
				val grdDisjunct = TermBool.mkDisjunct(grdcmds.map(_.guard))
				pa_in appendPO (pre.term impl grdDisjunct, gInvFOpt)
				for (grdcmd <- grdcmds) {
					pa_in appendPO (poPreToPre(pa_in, grdcmd.cmd, grdcmd.guard), gInvFOpt)
					pa_in appendPO (poPostToPost(grdcmd.cmd, pa_in), gInvFOpt)
				}
				grdcmds.map(_.cmd).foreach(populatePOs(_))
			case WhileProgC(loopInvOpt, grdcmds) =>
				assert(grdcmds.length > 0)
				val loopInv = loopInvOpt getOrElse UnkTerm.mkUnkTermBool
				//init
				pa_in appendPO ((pre.term impl loopInv), gInvFOpt)
				//inPath
				grdcmds foreach { grdcmd =>
					pa_in appendPO ((loopInv && grdcmd.guard).impl(grdcmd.cmd.pre.term), gInvFOpt)
					pa_in appendPO (grdcmd.cmd.post.term impl loopInv, gInvFOpt)
				}
				//exitPath
				val notgrdFs = grdcmds map { grdcmd => grdcmd.guard.unary_!}
				val exitCondition = TermBool.mkConjunct(notgrdFs)
				pa_in appendPO ((loopInv && exitCondition) impl post.term, gInvFOpt)
				grdcmds.map(_.cmd).foreach(populatePOs(_))
			case ValDefProgC(lhs, Some(rhs: ExprProg)) =>
				pa_in appendPO (poPreToPre(pa_in, rhs, TermBool.TrueT), gInvFOpt)
				pa_in appendPO (poMultiAssignment(HashMap(lhs -> rhs.expr), pa_in.pre, pa_in.post), gInvFOpt)
				//pa_in appendPO poAssignment(lhs, rhs, pa_in)
				//populatePOs(rhs)
			case VarDefProgC(lhs, Some(rhs: ExprProg)) =>
				pa_in appendPO (poPreToPre(pa_in, rhs, TermBool.TrueT), gInvFOpt)
				pa_in appendPO (poMultiAssignment(HashMap(lhs -> rhs.expr), pa_in.pre, pa_in.post), gInvFOpt)
				//pa_in appendPO poAssignment(lhs, rhs, pa_in)
				//populatePOs(rhs)
			case ValDefProgC(_, None) =>
			case VarDefProgC(_, None) =>
			case _: ValDefProg
				| _: VarDefProg =>
					throw new RuntimeException("Rhs type is different from ExprProg!")
			case AssignmentC(asgns) =>
				val allRhsAreExpr = asgns.forall{case (lhs, rhs: ExprProg) => true; case _ => false}
				if (allRhsAreExpr){
					val varTermsMap: Map[Var, Term] = (asgns map {case (lhs, ep:ExprProg) => (lhs, ep.expr)}).toMap
					pa_in appendPO (poMultiAssignment(varTermsMap, pa_in.pre, pa_in.post), gInvFOpt)
				}
				else
					throw new RuntimeException("Rhs type is different from ExprProg!")
			case SkipProgC() =>
				pa_in appendPO (pre.term impl post.term, gInvFOpt)
			case UnknownProgC(id) =>
				/**{y = z} (x + y) {rv = x + z}
				 * PO:(y = z) /\ (rv = x + y) => rv = x + z
				 * */
				//No PO is added for UnknownProg
			case AssumeProgC(pred) =>
				pa_in appendPO ((pre.term && pred) impl post.term, gInvFOpt)
			case ExprProgC(aTerm) =>
				if (post.rvVar.isDefined){
					val weakestPre = post.term.replaceVar(post.rvVar.get, aTerm)
					pa_in appendPO (pre.term impl weakestPre, gInvFOpt)
				}else{
					pa_in appendPO (pre.term impl post.term, gInvFOpt)
				}

			case IdentifierC(name, itype) =>
				throw new RuntimeException()
				//val antecedent = post.rvVar match {
				//	case Some(aVar) => And(pre.term, Atom(Pred("$eq$eq", List(aVar, Var(name, itype)))))
				//	case _ => pre.term
				//}
				//pa_in appendPO Impl(antecedent, post.term)
			case LitConstantC(name) =>
				throw new RuntimeException()
				//pa_in appendPO Impl(pre.term, post.term)
		}
	}
	
	def getGrdsCompletePO(ifProg: IfProg, gInvF: TermBool): TermBool = {
		val grdDisjunct = TermBool.mkDisjunct(ifProg.grdcmds.map(_.guard))
		(ifProg.pre.term  && gInvF) impl grdDisjunct
	}

	protected def poPreToPre(pa1: ProgramAnn, pa2: ProgramAnn, guard: TermBool): TermBool = {
		pa1.pre.term.&&(guard).impl(pa2.pre.term).simplify()
	}

	//TODO: fix the image relative path. Should refer to file in target directory.
	/** Returns post-post transition proof obligation.
	 * pa1 is the inner annotated program and pa2 is the outer annotated program
	 * Return formula :
	 * pa1.post.term: \psi_1
	 * pa2.post.term: \psi_2
	 * \psi_1 && rv_is_same(\psi_1, \psi_2) => \psi_2
	 */
	protected def poPostToPost(pa1: ProgramAnn, pa2: ProgramAnn): TermBool = {
		val antecedent: TermBool =
			if(rvEquivAssert(pa1, pa2) != TermBool.TrueT)
				pa1.post.term && rvEquivAssert(pa1, pa2)
			else
				pa1.post.term
		val consequent: TermBool = pa2.post.term
		antecedent impl consequent
	}

	protected def poPostToPre(pa1: ProgramAnn, pa2: ProgramAnn): TermBool = {
		pa1.post.term impl pa2.pre.term //TODO: what about rv of pa1.post
	}

	protected def poAssignment(lhsVar: Var, rhs: ProgramAnn, pa: ProgramAnn): TermBool = {
		//val lhsVar = Var(lhs.name, lhs.itype)
		assert(rhs.post.rvVar.isDefined)
		val weakestPre = pa.post.term.replaceVar(lhsVar, rhs.post.rvVar.get)
		rhs.post.term impl weakestPre
	}

	protected def poMultiAssignment(varTermsMap: Map[Var, Term], pre: InvariantT, post: InvariantT): TermBool = {
		val weakestPre = post.term.replaceVarsSim(varTermsMap)
		pre.term impl weakestPre
	}

	/** Returns a formula asserting that the retval variables of the
	 *  input annotated programs pa1, and pa2 are the same.
	 */
	protected def rvEquivAssert(pa1: ProgramAnn, pa2: ProgramAnn): TermBool = {
		val (rvVar1, rvVar2) = (pa1.post.rvVar, pa2.post.rvVar)
		if (rvVar1 != None && rvVar2 != None){
			rvVar1.get eqeq rvVar2.get
		}else
			TermBool.TrueT
	}
}

