package progsynth.types
import Types._
import scalaz.Lens
import progsynth.proofobligations.POData
import progsynth.extractors._
import org.kiama.rewriting.Rewritable

//import progsynth.PSPredef._ //Do not include PSPredef since this file is in the client library
//import progsynth.printers.ProgramPPrinter._

// images\ProgramClassDiag.svg
//sealed abstract class Program extends ProgramApplyRec
/**
 * When adding mutable data to ProgramAnn, also modify "reconstruct" method in ProgramAnnRewritable appropriately.
 * */

object ProgramAnn {
    private var id = 0
    def getId = { id = id + 1; id }
    def reset() = { id = 0 }
}

trait ProgramAnn
	extends InferAnn with ProgramAnnUtils with POData with ProgramAnnApplyRec
	with ProgramAnnRewritable with PSProgTree
{
	def pre: InvariantT
	def post: InvariantT
	
	val id: Int = ProgramAnn.getId
	
	def withNewParams(	pre: InvariantT = pre, post: InvariantT= post): ProgramAnn
	
	def setPre(pre: InvariantT) = withNewParams(pre = pre)
	
	def setPost(post: InvariantT) = withNewParams(post = post)
	
	def setDisplayIdPA( x: Int) = {this.displayId = x; this}
}

////////////////////////////////////////////////////////////////////////

case class GuardedCmd(guard: TermBool, cmd: ProgramAnn) extends GuardedCmdUtils with PSProgTree with CaseRewritable

///////////////////////////////////////////////////////////////////////

case class FunctionProg(name: String, params: List[Var], retVar: Var,
	annProg: ProgramAnn, globalInvs: List[TermBool],
	pre: InvariantT, post: InvariantT) extends ProgramAnn
    with FunctionProgInferAnn
    with FunctionProgToString
{
	override def withNewParams(pre: InvariantT = pre,
		post: InvariantT= post): FunctionProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object FunctionProgC {
	def unapply(fnProg: FunctionProg): Option[(String, List[Var], Var, ProgramAnn, List[TermBool])] = {
		Some((fnProg.name, fnProg.params, fnProg.retVar, fnProg.annProg, fnProg.globalInvs))
	}
}

object FunctionProgSpec {
	def unapply(fnProg: FunctionProg): Option[(InvariantT, InvariantT)] = {
		Some((fnProg.pre, fnProg.post))
	}
}

/////////////////////////////////////////////////////////////////////////////////
case class ProcedureDef(name: String, fParams: List[Var],
	body: ProgramAnn, ghostVars: List[Var], frameVars: List[Var],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with ProcedureDefInferAnn
    with ProcedureDefToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): ProcedureDef = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object ProcedureDefC {
	def apply(name: String, fParams: List[Var],
		body: ProgramAnn, ghostVars: List[Var], frameVars: List[Var]): ProcedureDef =
	{
		val pre = TermBool.TrueT.inv()
		val post = TermBool.TrueT.inv()
		ProcedureDef(name, fParams, body, ghostVars, frameVars, pre, post)
	}
	
	def unapply(pd: ProcedureDef) = {
		Some((pd.name, pd.fParams, pd.body, pd.ghostVars, pd.frameVars))
	}
}

///////////////////////////////////////////////////////////////
//ProcedureCall(procDef, params)
case class ProcedureCall(procDef: ProcedureDef, params: List[Term],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with ProcedureCallInferAnn
    with ProcedureCallToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): ProcedureCall = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}
	
object ProcedureCallC {
	def unapply(pc: ProcedureCall) = {
		Some((pc.procDef, pc.params))
	}
}

///////////////////////////////////////////////////////////////////////
//todo: enforce that FunctionProg inside FunctionProg is not supported.
case class IfProg(grdcmds: List[GuardedCmd],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with IfProgInferAnn
    with IfProgToString {
	
	var grdsComplete = false

	override def withNewParams( pre: InvariantT = pre, post: InvariantT= post): IfProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object IfProg {
	def apply(grdcmds1: List[GuardedCmd], grdcmd: GuardedCmd, grdcmds2: List[GuardedCmd], pre: InvariantT, post: InvariantT): IfProg = {
		IfProg((grdcmds1 :+ grdcmd) ++ grdcmds2, pre, post)
	}
}
object IfProgC {
	def unapply(i: IfProg) = {
		Some(i.grdcmds)
	}
}
///////////////////////////////////////////////////////////////////////
case class WhileProg(loopInv: Option[TermBool], grdcmds: List[GuardedCmd],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with WhileProgInferAnn
    with WhileProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): WhileProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object WhileProgC {
	def unapply(w: WhileProg): Option[(Option[TermBool], List[GuardedCmd])] = {
		Some((w.loopInv, w.grdcmds))
	}
}

object WhileProgSingle {
	def apply(loopInv: Option[TermBool], grdcmd: GuardedCmd,
				pre: InvariantT, post: InvariantT) = {
		WhileProg(loopInv, grdcmd :: Nil, pre, post)
	}
	def unapply(w: WhileProg): Option[(Option[TermBool], GuardedCmd, InvariantT, InvariantT)] = w match {
		case WhileProg(loopInv, grdCmd :: Nil, pre, post) =>
			Some((loopInv, grdCmd, pre, post))
		case _ =>
			None
	}
}
///////////////////////////////////////////////////
case class Composition(programs: List[ProgramAnn],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with CompositionInferAnn
    with CompositionToString
    with CompositionUtils
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): Composition = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
	
}

object Composition {
	def apply(p1: ProgramAnn, p2: ProgramAnn, pre: InvariantT, post: InvariantT): Composition = {
		Composition(p1 :: p2 :: Nil, pre, post)
	}
}

object CompositionC {
	def unapply(c: Composition): Option[List[ProgramAnn]] =
		Some(c.programs)
}

/////////////////////////////////////////////////////
case class Identifier(name: String, itype: PSType,
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with IdentifierInferAnn
    with IdentifierToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): Identifier = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
	
}

object IdentifierC {
	def unapply(id: Identifier): Option[(String, PSType)] = {
		Some((id.name, id.itype))
	}
}
/////////////////////////////////////////////////////////////////
case class ValDefProg(lhs: Var, rhsOpt: Option[ProgramAnn],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with ValDefProgInferAnn
    with ValDefProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): ValDefProg =	{
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

    
object ValDefProgC {
	def unapply(vdp: ValDefProg): Option[(Var, Option[ProgramAnn])] = {
		Some(vdp.lhs, vdp.rhsOpt)
	}
}
/////////////////////////////////////////////////////////////////
case class VarDefProg(lhs: Var, rhsOpt: Option[ProgramAnn],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with VarDefProgInferAnn
    with VarDefProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): VarDefProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

    
object VarDefProgC {
	def unapply(vdp: VarDefProg): Option[(Var, Option[ProgramAnn])] =
		Some(vdp. lhs, vdp.rhsOpt)
}
/////////////////////////////////////////////////////////////////
case class Assignment(asgns: List[(Var, ProgramAnn)],
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with AssignmentInferAnn
    with AssignmentToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): Assignment = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object AssignmentC {
	def unapply(asgn: Assignment): Option[List[(Var, ProgramAnn)]]  = {
		Some(asgn.asgns)
	}
}

object AssignmentExpr {

	def apply(asgns: List[(Var, Term)],
			pre: InvariantT, post: InvariantT) =
	{
		val asgnsPs = asgns map { asgn =>
			val epre = TermBool.TrueT
			val epost = TermBool.TrueT
			asgn match {
				case (aVar, aTerm) => (aVar, ExprProg(aTerm, epre.inv, epost.inv))
		}}
			
		Assignment(asgnsPs, pre, post)

	}

	def unapply(asgn: Assignment): Option[(List[(Var, Term)], InvariantT, InvariantT)]  = {
		asgn match {
			case Assignment(asgns, pre, post) =>
				val asgnTs = asgns map {
					case (aVar, ExprProg(aTerm, epre, epost)) =>
						(aVar, aTerm)
					case _ => throw new RuntimeException("Assignment RHS is not Expr")
				}
				Some((asgnTs, pre, post))
		}
	}
}

object AssignmentExprC {
	def unapply(asgn: Assignment): Option[List[(Var, Term)]]  = {
		asgn match {
			case Assignment(asgns, pre, post) =>
				val asgnTs = asgns map {
					case (aVar, ExprProg(aTerm, epre, epost)) =>
						(aVar, aTerm)
					case _ => throw new RuntimeException("Assignment RHS is not Expr")
				}
				Some(asgnTs)
		}
	}
}

////////////////////////////////////////////////////////////////////
case class SkipProg(
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with SkipProgInferAnn
    with SkipProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): SkipProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object SkipProgC {
	def unapply(skp: SkipProg) = {
		Some(())
	}
}
///////////////////////////////////////////////////////////////////
case class UnknownProg(upid: Int, pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with UnknownProgInferAnn
    with UnknownProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): UnknownProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object UnknownProgC {
	def unapply(u: UnknownProg): Option[Int] = {
		Some(u.upid)
	}
}

///////////////////////////////////////////////////////////////////
case class AssumeProg(pred: TermBool, pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with AssumeProgInferAnn
    with AssumeProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): AssumeProg = {
		val x = copy(pred = pred, pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}

object AssumeProgC {
	def unapply(ap: AssumeProg): Option[TermBool] = {
		Some(ap.pred)
	}
}

/////////////////////////////////////////////////////////////////
    
case class LitConstant(name: String,
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with LitConstantInferAnn
    with LitConstantToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): LitConstant = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}

}
object LitConstant {
	
	def apply(name: String): LitConstant =
		LitConstant(name, TermBool.TrueT.inv(), TermBool.TrueT.inv())
}

object LitConstantC {
	def unapply(lc: LitConstant): Option[String] = {
		Some(lc.name)
	}
}
///////////////////////////////////////////////////////////////////////////////
case class ExprProg(expr: Term,
	pre: InvariantT, post: InvariantT)
	extends ProgramAnn
    with ExprProgInferAnn
    with ExprProgToString
{
	override def withNewParams(pre: InvariantT = pre, post: InvariantT= post): ExprProg = {
		val x = copy(pre = pre, post = post)
		x.displayId = this.displayId
		x
	}
}
object ExprProg {
	
	def apply(expr: Term): ExprProg =
		ExprProg(expr, TermBool.TrueT.inv(), TermBool.TrueT.inv())
}

object ExprProgC {
	def unapply(ep: ExprProg): Option[Term] =
		Some(ep.expr)
}
