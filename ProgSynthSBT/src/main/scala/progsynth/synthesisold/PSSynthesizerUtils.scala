package progsynth.synthesisold
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import scala.collection.mutable.LinkedHashMap
import scala.xml.Node

/** Program context: list of mutable and immutable variables
 *  There might two variables of same name but different types, one in vars and other in dummies*/
class ProgContext(val varList: List[Var], val valList: List[Var], val dummyList: List[Var]) {
	def addVals(vals: List[Var]) = new ProgContext(varList, valList ++ vals, dummyList)
	def addVal(aval: Var) = addVals(aval :: Nil)
	def addVars(vars: List[Var]) = new ProgContext(varList ++ vars, valList, dummyList)
	def addVar(avar: Var) = addVars(avar :: Nil)
	def addDummies(vars: List[Var]) = new ProgContext(varList, valList, dummyList ++ vars)

	override def toString() = {
		val varStr = "[" + (varList map {vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
		val valStr = "[" + (valList map {vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
		val dummyStr = "[" + (dummyList map {vVar => vVar.v /*+ ": " + vVar.t*/ }).mkString(" ") + "]"
		varStr + " " + valStr
	}

	def contains(aVar: Var)(includeDummies: Boolean): Boolean =
		(varList contains aVar) ||
		(valList contains aVar) ||
		(includeDummies && (dummyList contains aVar))

	def contains(aVarStr: String)(includeDummies: Boolean): Boolean =
		(varList.map(_.v) contains aVarStr) ||
		(valList.map(_.v) contains aVarStr) ||
		(includeDummies && (dummyList.map(_.v) contains aVarStr))

	def getVar(aVarStr: String)(includeDummies: Boolean): Option[Var] = {
		if(includeDummies)
			(varList ++ valList ++ dummyList).find(_.v == aVarStr)
		else
			(varList ++ valList).find(_.v == aVarStr)
	}

	def getVar(aVarStr: String, tpe: PSType)(includeDummies:Boolean): Option[Var] = {
		getVar(aVarStr)(includeDummies) filter (_.getType == tpe )
	}
}

object EmptyProgContext extends ProgContext(Nil, Nil, Nil)

trait PSSynthesizerUtils {
	/** Extract all the unknown sub-programs from an annProg:ProgramAnn */
	def getUnkProgs(annProg: ProgramAnn): List[UnknownProg] = {
		annProg match {
			case unkProg @ UnknownProgC(_) => List(unkProg)
			case _ => annProg.getSubPas flatMap getUnkProgs
		}
	}

	/** Extract 'context' of all the unknown sub-programs in an 'annProg: ProgramAnn'
	 * testcase : [tests.synthesis.MaxTestExtractCtx1] */
	def getCtxMap(annProg: ProgramAnn): LinkedHashMap[ProgramAnn, ProgContext] = {

		var ctxMap = LinkedHashMap[ProgramAnn, ProgContext]()

		def addToCtxMap(annProg: ProgramAnn, ctx: ProgContext) = {
			ctxMap += (annProg -> ctx)
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
				| 	SkipProgC()
				| 	UnknownProgC(_)
				|	AssumeProgC(_)
				| 	IdentifierC(_, _)
				| 	LitConstantC(_)
				| 	ExprProgC(_) => (Nil, Nil)
			}
		}

		/**Populate Ctx Map of the annotated Program given the context of the root node*/
		def populateCtxMap(	curProg: ProgramAnn, curCtx: ProgContext): Unit = {
			//add the current node to the map
			addToCtxMap(curProg, curCtx)
			//traverse
			curProg match {
				case FunctionProgC(name, params, retVar, subProg, _) =>
					val a = subProg //dipDebug
					populateCtxMap(subProg, curCtx.addVals(params).addVars(retVar::Nil))
				case ValDefProgC(lhs, Some(rhs)) =>
					populateCtxMap(rhs, curCtx)
				case VarDefProgC(lhs, Some(rhs)) =>
					populateCtxMap(rhs, curCtx)
				case CompositionC(programs) =>
					var cumuVars: Set[Var] = Set()
					var cumuVals: Set[Var] = Set()
					programs foreach {prog =>
						val (cumuVarList, cumuValList) = (cumuVars.toList, cumuVals.toList)
						populateCtxMap(prog, curCtx.addVars(cumuVarList).addVals(cumuValList))
						val (newVars, newVals) = getNewVarsVals(prog)
						cumuVars = cumuVars ++ newVars
						cumuVals = cumuVals ++ newVals
					}
				case IfProgC(grdcmds) => grdcmds foreach { grdcmd =>
					populateCtxMap(grdcmd.cmd, curCtx)
				}
				case WhileProgC(_, grdcmds) => grdcmds foreach { grdcmd =>
					populateCtxMap(grdcmd.cmd, curCtx)
				}
				case AssignmentC( _)
				| 	SkipProgC()
				| 	UnknownProgC(_)
				|	AssumeProgC(_)
				|	ValDefProgC(_, None)
				|	VarDefProgC(_, None)
				| 	IdentifierC(_, _)
				| 	LitConstantC(_)
				| 	ExprProgC(_) =>
			}
		}

		populateCtxMap(annProg, new ProgContext(Nil, Nil, Nil))

		ctxMap
	}

	def getNewVars(consts: List[Var], context: ProgContext): List[Var] = {
		/**New variable counter*/
		object nvcnt {
			var _cnt = 0
			def apply() = {_cnt += 1; _cnt.toString}
		}
		for(const <- consts) yield {
			var baseVar = if (const.v.forall(_.isUpper)) {
				Var.mkVar(const.v.toLowerCase(), const.getType)
			} else {
				const
			}
			var newVar: Var = null
			do{
				newVar = Var.mkVar(baseVar.v + nvcnt(), baseVar.getType)
			} while (context.contains(newVar)(true)) //TODO: check the second parameter
			newVar
		}
	}

	/** Checks if 'aVar' is appears in 'formula' as an array index. */
	def isArrIndexVar(formula: FOLFormula, aVar: Var): Boolean = {
		def isAiv(aTerm: Term): Boolean = {
			//writeln0((aTerm)
			aTerm match {
				case ArrSelect(_, indexTerm) if indexTerm.getVars contains aVar => true
				case ArrStore(_, indexTerm, _) if indexTerm.getVars contains aVar => true
				case ArrSelect(_, _) | ArrStore(_, _, _)
					| Const(_) | Var(_) | FnApp(_, _) => false
				case QTerm(_, _, _, _ ) => throw new RuntimeException("exception in isArrIndexVar: QTerm not handled. ")
			}
		}
		val retVal = formula.existsSubTerm(isAiv)
		retVal
	}

	/** Checks if 'N' appears as an upper bound on array index in the 'formula' */
	def isArrIndexUpperBound(formula: FOLFormula, N: Var): Boolean = {
		/** TODO: This implementation is not quite correct.
		 * - The Atom can be in negative context. This may lead to false positives !
		 * - Instead of i and N, there can be terms involving i and N. May lead to false negatives.
		 * */
		val retVal = formula.existsSubF {
			case Atom(Pred("$less", List(i @ Var(_), N))) if isArrIndexVar(formula, i)=> true
			case Atom(Pred("$greater", List(N, i @ Var(_)))) if isArrIndexVar(formula, i)=> true
			case Atom(Pred("$less$eq", List(i @ Var(_), N))) if isArrIndexVar(formula, i)=> true
			case Atom(Pred("$greater$eq", List(N, i @ Var(_)))) if isArrIndexVar(formula, i)=> true
			case _ => false
		}
		retVal
	}

}

