package progsynth.synthesisnew

import progsynth.types._
import scala.util.control.Breaks._

object FrameFinder {

	/** Example
        +------------------------------------------------------------+
        | vals: x   vars: Nil   globalInvs:\phi    macros: abc       |<-----+
        +------------------------------------------------------------+      |
        |  if                                                        |      |
        |                                                            |      |
        |    x > 0 -->  x := x - 1                                   |      |
        |                                                            |      |
        |    x <= 0 --> x := x + 1                                   |      |
        |                                                            |      |
        |  fi;                                                       |      | parent
        |                                                            |      |
        |  val z := 0;                                               |      |
        |                                                            |      |
        | +-----------------------------------------------------+    |      |
        | | vals: x, z  vars: Nil  globalInvs: Nil   macros: Nil|    |      |
        | +-----------------------------------------------------+    |      |
        | |z := x                                               +-----------+
        | |                                                     |    |
        | +-----------------------------------------------------+    |
        + -----------------------------------------------------------+

        * Extracts the context of the inner program and creates a frame for it.
        * Used for stepping into subprog.
	 */
	def mkInnerProgFrame(programIn: ProgramAnn, outerFrameIn: Frame)(innerProg: ProgramAnn): Option[Frame] = {

		def mkInnerProgFrame0(program: ProgramAnn, outerFrame: Frame): Option[Frame] = {
    		program match {
    			case FunctionProgC(name, params, retVar, subProg, globalInvs) =>
    				mkInnerProgFrame(subProg, new ProgramFrame(	macros = Nil, parent = Some(outerFrame),
    															varList = retVar::Nil, valList = params,
    															globalInvs = globalInvs))(innerProg)
    				//TODO: ensure that macros are handled properly
    			case ProcedureCallC(procDef, params) => //TODO: check correctness
    				None
    			case ValDefProgC(lhs, Some(rhs)) =>
    				mkInnerProgFrame(rhs, outerFrame)(innerProg)
    			case VarDefProgC(lhs, Some(rhs)) =>
    				mkInnerProgFrame(rhs, outerFrame)(innerProg)
    			case CompositionC(programs) =>
    				var vars: List[Var] = Nil
    				var vals: List[Var] = Nil
    				var retVal: Option[Frame] = None
    				breakable {
    					for(prog <- programs){
    						val accumulatedFrame = new ProgramFrame(macros = Nil, parent = Some(outerFrame),
    																varList = vars, valList = vals,
    																globalInvs = Nil)
    						val ctx = mkInnerProgFrame(prog, accumulatedFrame)(innerProg)
    						if (ctx.isDefined){
    							retVal = ctx
    							break
    						}
    						val (newVars, newVals) = getNewVarsVals(prog)
    						vars = vars ++ newVars
    						vals = vals ++ newVals
    						//Note: No need to accummulate globalInvs or macros since the annotated
    						// program does not contain any frame. Frame is created only when user steps
    						// into a subprogram
    					}
    				}
    				return retVal

    			case IfProgC(grdcmds) =>
    				var retVal: Option[Frame] = None
    				breakable { for(grdcmd <- grdcmds ){
    					val ctx = mkInnerProgFrame(grdcmd.cmd, outerFrame)(innerProg)
    					if(ctx.isDefined) {
    						retVal = ctx
    					}
    				}}
    				return retVal

    			case WhileProgC(_, grdcmds) =>
    				var retVal: Option[Frame] = None
    				breakable { for(grdcmd <- grdcmds ){
    					val ctx = mkInnerProgFrame(grdcmd.cmd, outerFrame)(innerProg)
    					if(ctx.isDefined) {
    						retVal = ctx
    					}
    				}}
    				return retVal
    			//case UnknownProgC(_) => //TODO: test this. Required for asgnDerivation.
    			//	return Some(outerFrame)
    			case
    			 	VarDefProgC(_, None)
    			| 	ValDefProgC(_, None)
    			| 	AssignmentC( _)
    			| 	SkipProgC(_)
    			| 	UnknownProgC(_)
    			|   _: AssumeProg
    			| 	IdentifierC(_, _)
    			| 	LitConstantC(_)
    			| 	ExprProgC(_) => None
    		}
		}

		if (innerProg == programIn)
			return Some(outerFrameIn)

		mkInnerProgFrame0(programIn, outerFrameIn)
	}

	def getNewVarsVals(prog: ProgramAnn): (List[Var], List[Var]) = {
		prog match {
			case VarDefProgC(lhs, rhs) => (lhs::Nil, Nil)
			case ValDefProgC(lhs, rhs) => (Nil, lhs::Nil)
			case  FunctionProgC(_, _, _, _, _)
			|	ProcedureCallC(_, _)
			| 	IfProgC(_)
			| 	WhileProgC(_, _)
			| 	CompositionC(_)
			| 	AssignmentC(_)
			| 	SkipProgC(_)
			| 	UnknownProgC(_)
			|	_: AssumeProg
			| 	IdentifierC(_, _)
			| 	LitConstantC(_)
			| 	ExprProgC(_) => (Nil, Nil)
		}
	}
}