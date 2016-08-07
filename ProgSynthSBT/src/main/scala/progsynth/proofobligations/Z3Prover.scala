package progsynth.proofobligations
import progsynth.types._
import progsynth.types.Types._
import z3.scala._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import progsynth.debug.PSDbg._
import progsynth.debug.PSDbg
import progsynth.ProgSynth.Counter
import progsynth.ProgSynth._
import progsynth.synthesisnew.Macro
import scala.actors.Actor
import scala.actors.TIMEOUT
import org.slf4j.LoggerFactory

//dipakc_debug
object abc extends Counter

object Z3Prover extends TestCaseUtils with TimedRunTrait{
	// Z3Config is required if you plan to query models of satisfiable constraints.
	private var cfg: Z3Config = null
	private var z3: Z3Context = null
	private val logger = LoggerFactory.getLogger("progsynth.Z3Prover")

	def expandMacroAndProve(t: TermBool, macroList: List[Macro]): Option[Z3Result] = {
		prove(new MacroExpander(macroList).expand(t).asInstanceOf[TermBool])
	}

	def prove(t: TermBool): Option[Z3Result] = {
		logger.trace("prove on termbool called on \n" + t.pprint() )
		//println("prove on termbool called on \n" + t.toCode() )
		cfg = new Z3Config("MODEL" -> true)
		z3 = new Z3Context(cfg)
		val solver= z3.mkSolver

		/** ------Build AST ---------*/
		val cs = buildTermAST(t, Nil)

		/** ------Run Z3 ---------*/
		val negCs = z3.mkNot(cs)
		solver.assertCnstr(negCs)

		/** ------Extract Result ---------*/
		val retVal = new Z3Result()
		solver.check match {
			case None =>
				retVal.error = Some(solver.getReasonUnknown);
			case Some(false) =>
				retVal.isValid = true;
			case Some(true) =>
				retVal.isValid = false;
				if (solver.isModelAvailable){
					val model = solver.getModel
					retVal.fullModelDesc = model.toString()
					//retVal.modelVarIps = getVarIp(model) //TODO: enable model generation. fix crash
					//retVal.modelFnIps = getFnIp(model) //TODO: enable model generation. fix crash
					model.decRef
				}
		}
		/** ------delete z3 ---------*/
		solver.decRef
		z3.delete

		Some(retVal)
	}

	def prove(f: FOLFormula): Option[Z3Result] = {

		cfg = new Z3Config("MODEL" -> true)
		z3 = new Z3Context(cfg)

		/** ------Build AST ---------*/
		val cs = buildFormulaAST(f, Nil)
		if (cs == None)
			return None

		/** ------Run Z3 ---------*/
		val negCs = z3.mkNot(cs.get)
		z3.assertCnstr(negCs)

		/** ------Extract Result ---------*/
		val retVal = new Z3Result()
		z3.checkAndGetModel match {
			case (None, _) =>
				retVal.error = Some(z3.getSearchFailure.message);
			case (Some(false), _) =>
				retVal.isValid = true;
			case (Some(true), model) =>
				retVal.isValid = false;
				retVal.modelVarIps = getVarIp(model)
				retVal.modelFnIps = getFnIp(model)
				retVal.fullModelDesc = model.toString()
				model.delete
		}
		/** ------delete z3 ---------*/
		z3.delete

		Some(retVal)
	}

	private def ____getModel____ = ()

	/**
	 * Get variable Interpretations
	 * Variables are called Const in z3 jargon
	 */
	protected def getVarIp(model: Z3Model): List[VarIp] = {
		var modelVarIps: List[VarIp] = Nil
		val constIpIterator = model.getModelConstantInterpretations
		while (constIpIterator.hasNext) {
			val (z3FuncDecl, valueAst) = constIpIterator.next()
			writeln0("z3FuncDecl, valueAst : " + z3FuncDecl.toString, valueAst.toString )
			val constName = z3FuncDecl.getName.toString
			val constValue = getValueFromAST(valueAst, model)
			val constTpe = z3ToPSType(valueAst.getSort)
			modelVarIps ::= VarIp(Var.mkVar(constName, constTpe), constValue)
		}
		modelVarIps
	}

	/**
	 * Get Fn Interpretations
	 */
	protected def getFnIp(model: Z3Model): List[FnIp] = {
		var modelFnIps: List[FnIp] = Nil
		val fnIpIterator = model.getModelFuncInterpretations
		while (fnIpIterator.hasNext) {
			val (z3FnDecl, valueList, default) = fnIpIterator.next()
			//writeln0("z3FnDecl, valueList, default: "  + z3FnDecl.toString +  " " + valueList.toString  +  " " + default.toString) //crash if default.ptr == 0
			val fnName = z3FnDecl.getName.toString
			var argTpeList: List[PSType] = Nil
			val domainSize = z3FnDecl.getDomainSize
			for (i <- 0 until z3FnDecl.getDomainSize) {
				val argSort = z3FnDecl.getDomain(i)
				argTpeList = argTpeList ::: List(z3ToPSType(argSort))
			}
			val fn = Fn(fnName, argTpeList, z3ToPSType(z3FnDecl.getRange))

			val fnValues = valueList map {
				case (ips, op) =>
					(ips map { ip => getValueFromAST(ip, model) }, getValueFromAST(op, model))
			}

			val fnDefault = getValueFromAST(default, model)
			modelFnIps ::= FnIp(fn, fnDefault, fnValues)
		}
		modelFnIps
	}

	protected def getValueFromAST(varAST: Z3AST, model: Z3Model): Option[Any] = {
		if(varAST.ptr == 0)//fixed the crash. ptr can be null in case of undefined default values.
			return None
		z3ToPSType(varAST.getSort) match {
			case PSInt => model.evalAs[Int](varAST)
			case PSArrayInt => getArrayValue(varAST, model)
			case PSBool=> model.evalAs[Boolean](varAST)
			case PSArrayBool => getArrayValue(varAST, model)
			case PSArrayReal
			| PSAny
			| PSReal
			| PSUnit => throw new RuntimeException("Z3 Prover. getValueFromAST for "+ varAST.getSort + " not implementd. ")
		}
	}

	protected def getArrayValue(arrayConst: Z3AST, model: Z3Model): Option[Any] = {
		val arrayEvaluated = model.eval(arrayConst)
		arrayEvaluated flatMap { ae =>
			val arrayVal = model.getArrayValue(ae)
			arrayVal flatMap {
				case (valueMap, default) =>
					val resMap = valueMap map {
						case (index, value) =>
							getValueFromAST(index, model) -> getValueFromAST(value, model)
					}
					Some(resMap, getValueFromAST(default, model))
				case _ => None
			}
		}
	}

	/** Not called !*/
	protected def getValue(aVar: Var, model: Z3Model): Option[Any] = {
		val aSort = psToZ3Type(aVar.getType)
		val x = z3.mkConst(z3.mkStringSymbol(aVar.v), aSort)
		getValueFromAST(x, model)
	}

	/**
	 * bVars contains the list of bound variables.
	 * ∀(x) ∃(y) ∘(▶ ∀(z)∀(x) ∘(f))
	 * For f, the bVars = List(x, z, y, x)
	 */

	private def ____BuildAST____ = ()

	protected def buildFormulaAST(f: FOLFormula, bVars: List[Var]): Option[Z3AST] = {
		//writeln0("buildZ3AST: " + f.toString)
		f match {
			case True1() => Some(z3 mkTrue ())
			case False1() => Some(z3 mkFalse ())
			case Atom(aPred) => buildPredAST(aPred, bVars)
			case Not(f) => buildFormulaAST(f, bVars) map { z3.mkNot }
			case And(f1, f2) => buildZ3AST(z3.mkAnd _, bVars, f1, f2)
			case Or(f1, f2) => buildZ3AST(z3.mkOr _, bVars, f1, f2)
			case Impl(f1, f2) => buildZ3AST2(z3.mkImplies _, bVars, f1, f2)
			case Iff(f1, f2) => buildZ3AST2(z3.mkIff _, bVars, f1, f2)
			case Forall(v, f) => buildFormulaAST(f, v :: bVars) map { fbody =>
				//val someName: Z3Symbol = z3.mkIntSymbol(0) //TODO: check decls meaning
				val boundVar: Z3Symbol = z3.mkStringSymbol(v.v)
				val decls = List((boundVar, psToZ3Type(v.getType)))
				z3.mkForAll(weight = 0, patterns = Nil, decls = decls, body = fbody)
			}
			case Exists(v, f) => buildFormulaAST(f, v :: bVars) map { fbody =>
				val boundVar: Z3Symbol = z3.mkStringSymbol(v.v)
				val decls = List((boundVar, psToZ3Type(v.getType)))
				z3.mkExists(weight = 0, patterns = Nil, decls = decls, body = fbody)
			}
			case Unknown() => None
		}
	}

	protected def buildPredAST(aPred: Pred, bVars: List[Var]): Option[Z3AST] = {
		aPred match {
				case Pred("$eq$eq", t1 :: t2 :: Nil) =>
				  Some(z3.mkEq(buildTermAST(t1, bVars), buildTermAST(t2, bVars)))
				case Pred("$less$eq", t1 :: t2 :: Nil) =>
					Some(z3.mkLE(buildTermAST(t1, bVars), buildTermAST(t2, bVars)))
				case Pred("$greater$eq", t1 :: t2 :: Nil) =>
					Some(z3.mkGE(buildTermAST(t1, bVars), buildTermAST(t2, bVars)))
				case Pred("$less", t1 :: t2 :: Nil) =>
					Some(z3.mkLT(buildTermAST(t1, bVars), buildTermAST(t2, bVars)))
				case Pred("$greater", t1 :: t2 :: Nil) =>
					Some(z3.mkGT(buildTermAST(t1, bVars), buildTermAST(t2, bVars)))
				case Pred("BoolPred", t :: Nil) =>
					//Bool termsConstraints are actually formula ASTs
					Some(buildTermAST(t, bVars))
				case _ => throw new RuntimeException
		}
	}

	protected def buildTermAST(t: Term, bVars: List[Var]): Z3AST = {
		//writeln0("buildConstraints: " + t.toString)
		t match {
			case aVar @ Var(v) =>
				val vPos = bVars.indexOf(aVar)
				if (vPos >= 0)
					z3 mkBound (vPos, psToZ3Type(aVar.getType))
				else
					z3 mkConst (z3.mkStringSymbol(v), psToZ3Type(aVar.getType))
			case FnApp(f, ts) =>
				t match {
					case fnAppInt: FnAppInt => buildFnAppIntAST(fnAppInt, bVars)
					case fnAppBool: FnAppBool => buildFnAppBoolAST(fnAppBool, bVars)
					case _ => throw new RuntimeException("Only FnAppInt and FnAppBool are supported")
				}
			case aConst @ Const(_) => aConst match {
				case ConstInt(name) =>
					try {
						val intVal = name.toInt
						z3.mkInt(intVal, psToZ3Type(PSInt))
					} catch {
						case _ => //uninterpreted constant
							val fnDecl = z3 mkFuncDecl (name, Nil, psToZ3Type(PSInt))
							z3 mkApp (fnDecl)
					}
				case ConstBool("true") =>
					z3.mkTrue
				case ConstBool("false") =>
					z3.mkFalse
				case _ => throw new RuntimeException("Creating Non Int literals not supported")
			}
			case ArrSelect(arr, index) =>
				z3 mkSelect (buildTermAST(arr, bVars), buildTermAST(index, bVars))
			case ArrStore(arr: Term, index: Term, value: Term) =>
				z3 mkStore (buildTermAST(arr, bVars), buildTermAST(index, bVars), buildTermAST(value, bVars))
			case ForallTermBool(dummies, range, term) =>
				//All the dummies are handled at once (in the FOLFormula implementation, dummies are handled individually)
				val decls = dummies.map(dummy => (z3.mkStringSymbol(dummy.v), psToZ3Type(dummy.getType)))
				val fbody = range.impl(term)
				val bodyAST = buildTermAST(fbody, dummies ++ bVars)
				z3.mkForAll(weight = 0, patterns = Nil, decls = decls, body = bodyAST)
			case ExistsTermBool(dummies, range, term) =>
				//All the dummies are handled at once (in the FOLFormula implementation, dummies are handled individually)
				val decls = dummies.map(dummy => (z3.mkStringSymbol(dummy.v), psToZ3Type(dummy.getType)))
				val fbody = range.&&(term)
				val bodyAST = buildTermAST(fbody, dummies ++ bVars)
				z3.mkExists(weight = 0, patterns = Nil, decls = decls, body = bodyAST)
			case QTerm(_, _, _, _) =>
				throw new RuntimeException("Exception in buildTermAST: non boolean QTerm not handled")
		}
	}
	private def ____BuildASTOfFnApp____ = ()

	protected def buildFnAppIntAST(fnAppInt: FnAppInt, bVars: List[Var]): Z3AST = {
		val FnAppInt(f, ts) = fnAppInt
		f match {
			case PlusIntFn => z3 mkAdd ((ts map { aTerm => buildTermAST(aTerm, bVars) }): _*)
			case MinusIntFn => z3 mkSub ((ts map { aTerm => buildTermAST(aTerm, bVars) }): _*)
			case TimesIntFn => z3 mkMul ((ts map { aTerm => buildTermAST(aTerm, bVars) }): _*)
			case UnaryMinusIntFn => z3 mkUnaryMinus buildTermAST(ts.head, bVars)
			case PercentIntFn => z3 mkMod (buildTermAST(ts.head, bVars), buildTermAST(ts.tail.head, bVars))
			case DivIntFn => z3 mkDiv (buildTermAST(ts.head, bVars), buildTermAST(ts.tail.head, bVars))
			case _ => buildFnAppUnInterpretedAST(fnAppInt, bVars)
		}
	}

	protected def buildFnAppBoolAST(fnAppBool: FnAppBool, bVars: List[Var]): Z3AST = {
		val FnAppBool(f, ts) = fnAppBool
		val boolArgs = (ts.map(_.getType).forall(_ == PSBool))
		val treatEqEqAsEquivForBoolArgs = false
		f match {
			case AndBoolFn => z3 mkAnd (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case OrBoolFn => z3 mkOr (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case NegBoolFn => z3 mkNot (buildTermAST(ts(0), bVars))
			case ImplBoolFn => z3 mkImplies (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case RImplBoolFn => z3 mkImplies (buildTermAST(ts(1), bVars), buildTermAST(ts(0), bVars))
			case EquivBoolFn  => z3 mkIff (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case EqEqBoolFn =>
				if (boolArgs && treatEqEqAsEquivForBoolArgs)
					z3 mkIff (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
				else
					z3 mkEq (buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case LTBoolFn => z3.mkLT(buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case LEBoolFn => z3.mkLE(buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case GTBoolFn => z3.mkGT(buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case GEBoolFn => z3.mkGE(buildTermAST(ts(0), bVars), buildTermAST(ts(1), bVars))
			case _ => buildFnAppUnInterpretedAST(fnAppBool, bVars)
		}
	}

	protected def buildFnAppUnInterpretedAST(fnApp: FnApp, bVars: List[Var]): Z3AST = {
		val fnDecl = z3 mkFuncDecl (fnApp.f.name, fnApp.f.argTpes map psToZ3Type, psToZ3Type(fnApp.f.tpe))
		z3 mkApp (fnDecl, fnApp.ts map { aTerm => buildTermAST(aTerm, bVars) }: _*)
	}

	private def ____Utils____ = ()

	protected def psToZ3Type(tpe: PSType): Z3Sort = tpe match {
		case PSInt => z3.mkIntSort()
		case PSBool => z3.mkBoolSort()
		case PSArrayInt => z3.mkArraySort(psToZ3Type(PSInt), psToZ3Type(PSInt))
		case PSArrayBool => z3.mkArraySort(psToZ3Type(PSInt), psToZ3Type(PSBool))
		case _ => throw new RuntimeException("makeSort encountered unhandled type " + tpe)
	}

	protected def z3ToPSType(z3sort: Z3Sort): PSType = {
		val tpes = List(PSInt, PSBool, PSArrayInt, PSArrayBool)
		tpes.find( z3sort == psToZ3Type(_)) match {
			case Some(psType) =>
				psType
			case None =>
				throw new RuntimeException("getPSType encountered unhandled type " + z3sort)
		}
	}

	def buildZ3AST(bfun: (Z3AST*) => Z3AST, bVars: List[Var], fs: FOLFormula*): Option[Z3AST] = {
		val asts = fs map { f => buildFormulaAST(f, bVars) }
		if (asts.contains(None)) None
		else Some(bfun(asts map (_.get): _*))
	}

	def buildZ3AST2(bfun: (Z3AST, Z3AST) => Z3AST, bVars: List[Var], f1: FOLFormula, f2: FOLFormula): Option[Z3AST] =
		(buildFormulaAST(f1, bVars), buildFormulaAST(f2, bVars)) match {
			case (Some(ast1), Some(ast2)) => Some(bfun(ast1, ast2))
			case _ => None
		}
}

trait TimedRunTrait {
	@throws(classOf[java.util.concurrent.TimeoutException])
	def timedRun[F](timeout: Long)(f: => F): F = {

	  import java.util.concurrent.{Callable, FutureTask, TimeUnit}

	  val task = new FutureTask(new Callable[F]() {
	    def call() = f
	  })

	  new Thread(task).start()

	  task.get(timeout, TimeUnit.MILLISECONDS)
	}
}

trait TestCaseUtils {

	/** - uninterpreted constants not working ( eg. ConstInt("a") )
	 *  - The generated z3py formula has redundant parenthesis.
	 *  - todo: Output the model in functional style
	*/
	def prepareZ3PyTestCase(formula: FOLFormula): String = {
		def getFreeAndBoundVariables(formula: FOLFormula) = formula.getFreeAndBoundVars

		def getVariablesDeclarationString(formula: FOLFormula) = {
			var variables = getFreeAndBoundVariables(formula)
			val strList = variables map { variable =>
				variable.getType match {
					case PSInt => getTermString(variable) + " = Int('" + getTermString(variable) + "')"
					case PSArrayInt => getTermString(variable) + " = Array('" + getTermString(variable) + "', IntSort(), IntSort())"
					case _ => throw new RuntimeException("Z3Prover.prepareZ3PyTestCase unknown type" + variable.getType)
				}
			}
			(strList map {_ + "\n"}).mkString
		}

		def getZ3SortName(tpe: PSType) = tpe match {
			case PSInt => "IntSort"
			case _ => throw new RuntimeException("Z3Prover.getZ3SortName unknown type: " + tpe)
		}

		def getFunDeclarationString(formula: FOLFormula): String = {
			val fnList = formula.collectItems { case FnApp(aFn, _) if !(InterpretedFns contains aFn) => aFn }
			val fnDeclList = fnList.distinct map {aFn => aFn match {
				case Fn(name, argTpes, tpe) =>
					val tpes = argTpes ::: List(tpe)
					val tpeSortList = (tpes map {t => getZ3SortName(t) + "()"}).mkString(", ")
					name + " = Function('" + name + "', " + tpeSortList + ")"
			}}
			fnDeclList.mkString("\n")
		}
		def getFnAppString(fnApp: FnApp) = {
			fnApp match {
				case FnApp(f, ts) if InterpretedFns.contains(f) =>
					if (InterpretedFns.isInfix(f))
						"(" + getTermString(ts.head) + " " + InterpretedFns.getTextSymbol(f).get + " " + getTermString(ts.tail.head) + ")"
					else
						InterpretedFns.getTextSymbol(f) + "(" + (ts.map{t => getTermString(t)}).mkString(", ")  + ")"
				case FnApp(f, ts) => f.name + "(" + (ts.map{t => getTermString(t)}).mkString(", ")  + ")"
			}
		}

		def getTermString(term: Term): String = term match {
			case Var(v) => v
			case fnApp @ FnApp(_, _) => getFnAppString(fnApp)
			case Const(name) => name
			case ArrSelect(arr, index)  => "(" + getTermString(arr) + ")[" + getTermString(index) + "]"
			case ArrStore(arr, index, value) => "Store(" + getTermString(arr) +" , " + getTermString(index) + ", " + getTermString(value) + " )"
			case _ => throw new RuntimeException("getTermString unknown type: " + term.toString)
		}

		def getPredString(pred: Pred): String = pred match {
			case Pred("$eq$eq", t1 :: t2 :: Nil) => getTermString(t1) + " == " + getTermString(t2)
			case Pred("$less$eq", t1 :: t2 :: Nil) => getTermString(t1) + " <= " + getTermString(t2)
			case Pred("$greater$eq", t1 :: t2 :: Nil) => getTermString(t1) + " >= " + getTermString(t2)
			case Pred("$less", t1 :: t2 :: Nil) => getTermString(t1) + " < " + getTermString(t2)
			case Pred("$greater", t1 :: t2 :: Nil) => getTermString(t1) + " > " + getTermString(t2)
			case _ => throw new RuntimeException("getPredString unknown type: " + pred.toString)
		}

		def getFormulaString(formula: FOLFormula): String = formula match {
			case True1() => "True"
			case False1() => "False"
			case Atom(a) => "(" + getPredString(a) + ")"
			case Unknown() => throw new RuntimeException("Z3Prover.getFormulaString Unknown formula encountered")
			case Not(f) => "Not(" + getFormulaString(f) + ")"
			case And(f1, f2) => "And(" + getFormulaString(f1) + ", " + getFormulaString(f2) + ")"
			case Or(f1, f2) => "Or(" + getFormulaString(f1) + ", " + getFormulaString(f2) + ")"
			case Impl(f1, f2) => "Implies(" + getFormulaString(f1) + ", " + getFormulaString(f2) + ")"
			case Iff(f1, f2) => "(" + getFormulaString(f1) + " == " +  getFormulaString(f2) + ")"
			case Forall(v, f) => "ForAll(" + v + ", " + getFormulaString(f) + ")"
			case Exists(v, f) => "Exists(" + v + ", " + getFormulaString(f) + ")"
		}

		def getQueryString(formulaString: String) = {
			"s12345678 = Solver()\n" +
			"s12345678.add("+ formulaString + ")\n" +
			"print 'Solver:'\n" +
			"print s12345678\n" +
			"print 'SAT Check:'\n" +
			"print s12345678.check()\n" +
			"print 'Model:'\n" +
			"print s12345678.model()\n" +
			"print 'Model SExpr'\n" +
			"print s12345678.model().sexpr()\n"
		}
		val varDeclString = getVariablesDeclarationString(formula)
		val funDeclString = getFunDeclarationString(formula)
		val formulaString = getFormulaString(formula)
		val queryString = getQueryString(formulaString)
		return List(varDeclString, funDeclString, queryString).mkString("\n")
	}
}


case class VarIp(variable: Var, value: Any) //Variable interpretations
case class ConstIp(variable: Var, value: Any) //Constant interpretations
case class FnIp(fn: Fn, default: Any, values: Seq[(Seq[Any], Any)]) //Function interpretations

class Z3Result {
	var error: Option[String] = None
	var isValid = false

	/**
	 * - Contains variable interpretations in the counterexample.
	 * - Uninterpreted constants are modeled as nullary functions in Z3. But the model can be obtained from modelVarIps.
	 *
	 * '''Dummy variable renaming''' in the z3 model. (Sat example)
	 * - Phi : ∃x : x = 10 ∧ (∃x : x = 20 ∧ (∃x : x = 30) ∧ (∃x : x = 40))
	 * - Model : [x!1 = 20, x!2 = 30, x!3 = 40, x!0 = 10]
	 * - Psi: ∃x : (∃x : x = 2) ∧ x = 5 ∧ (∃x : x = 6)
	 * - Model: [x!4 = 5, x!5 = 2, x!6 = 6]
	 * - The dummy variable x seem to be renamed as x!n. n is incremented for each re-occurrence of same quantified variable in the formula.

	 * - modelVarIps is '''Nil''' if formula is Valid.
	 * - The model may contain additional variables that are created internally by z3.
	 */
	var modelVarIps: List[VarIp] = Nil

	//Uniterpreted constants are modeled as nullay functions in Z3. But the model can be obtained from modelVarIps.
	//var modelConstIps: List[ConstIp] = Nil

	/**
	 * Contains function interpretations in the counterexample.
	 * Uniterpreted constants are modeled as nullay functions in Z3. But the model can be obtained from modelVarIps.
	 * modelFnIps is Nil if formula is Valid.
	 *
	 * '''Problems:'''
	 *  - Uninterpreted function models are sometimes not retrieved. (This happens when these models are represented
	 *  as functional programs. Use fullModelDesc function in this case.
	 */
	var modelFnIps: List[FnIp] = Nil

	/** Full String representation of the Z3 Model*/
	var fullModelDesc : String = ""
}

/** Sample Z3TestAPp
 * To reduce build time, copy this to Expt package and then experiment
 * TODO: move to other file.*/
object Z3TestApp {
	def main1(args: Array[String]) {
        val f: FOLFormula = Atom(Pred("$less", List(ConstInt("5"), VarInt("x"))))
        val falseConst = ConstBool("false")
        val rVar = VarBool("r")
        val f2: FOLFormula = (rVar feqeq falseConst) impl (rVar.fm)
        val result = Z3Prover.prove(f2)
        if (result.isDefined)
            PSDbg.logln(result.get.isValid)
        else
            PSDbg.logln(result)
    }

	def main2(args: Array[String]) {
		val N = VarInt("N")
		val zero = ConstInt("0")
		val r = VarBool("r")
		val falseConst = ConstBool("false")
		//val f: TermBool = FnAppBool(Fn("impl",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$amp$amp",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$greater$eq",List(PSInt, PSInt),PSBool),List(N, zero)), FnAppBool(Fn("$eq$eq",List(PSAny, PSAny),PSBool),List(r, falseConst)))), FnAppBool(Fn("$amp$amp",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$greater$eq",List(PSInt, PSInt),PSBool),List(N, zero)), FnAppBool(Fn("$eq$eq",List(PSAny, PSAny),PSBool),List(r, falseConst))))))
		val f: TermBool = FnAppBool(Fn("$eq$eq",List(PSAny, PSAny),PSBool),List(r, falseConst))
        val result = Z3Prover.prove(f)
        if (result.isDefined)
            PSDbg.logln(result.get.isValid)
        else
            PSDbg.logln(result)
    }

	def main3(args: Array[String]) {
		val fimpl_469 = Fn("impl", List(PSBool, PSBool), PSBool)
		val f_amp_amp_470 = Fn("$amp$amp", List(PSBool, PSBool), PSBool)
		val f_greater_eq_471 = Fn("$greater$eq", List(PSInt, PSInt), PSBool)
		val N_472 = Var.mkVar("N", PSInt)
		val c0_473 = Const.mkConst("0", PSInt)
		val greatereq_474 = FnApp(f_greater_eq_471, List(N_472, c0_473))
		val f_eq_eq_475 = Fn("$eq$eq", List(PSAny, PSAny), PSBool)
		val r_476 = Var.mkVar("r", PSBool)
		val cfalse_477 = Const.mkConst("false", PSBool)
		val eqeq_478 = FnApp(f_eq_eq_475, List(r_476, cfalse_477))
		val ampamp_479 = FnApp(f_amp_amp_470, List(greatereq_474, eqeq_478))
		val n_480 = Var.mkVar("n", PSInt)
		val eqeq_481 = FnApp(f_eq_eq_475, List(n_480, c0_473))
		val ampamp_482 = FnApp(f_amp_amp_470, List(ampamp_479, eqeq_481))
		val f_equiv_483 = Fn("$equiv", List(PSBool, PSBool), PSBool)
		val ctrue_484 = Const.mkConst("true", PSBool)
		val f_pipe_pipe_485 = Fn("$pipe$pipe", List(PSBool, PSBool), PSBool)
		val p_486 = Var.mkVar("p", PSInt)
		val f_less_eq_487 = Fn("$less$eq", List(PSInt, PSInt), PSBool)
		val lesseq_488 = FnApp(f_less_eq_487, List(c0_473, p_486))
		val lesseq_489 = FnApp(f_less_eq_487, List(p_486, c0_473))
		val ampamp_490 = FnApp(f_amp_amp_470, List(lesseq_488, lesseq_489))
		val i_491 = Var.mkVar("i", PSInt)
		val lesseq_492 = FnApp(f_less_eq_487, List(c0_473, i_491))
		val f_less_493 = Fn("$less", List(PSInt, PSInt), PSBool)
		val less_494 = FnApp(f_less_493, List(i_491, p_486))
		val ampamp_495 = FnApp(f_amp_amp_470, List(lesseq_492, less_494))
		val arr_496 = Var.mkVar("arr", PSArrayBool)
		val arrsel_497 = ArrSelect.mkArrSelect(arr_496, i_491, PSBool)
		val qTerm_498 = QTerm.mkQTerm(f_amp_amp_470, List(i_491), ampamp_495, arrsel_497)
		val lesseq_499 = FnApp(f_less_eq_487, List(p_486, i_491))
		val less_500 = FnApp(f_less_493, List(i_491, c0_473))
		val ampamp_501 = FnApp(f_amp_amp_470, List(lesseq_499, less_500))
		val f_bang_502 = Fn("$bang", List(PSBool), PSBool)
		val bang_503 = FnApp(f_bang_502, List(arrsel_497))
		val qTerm_504 = QTerm.mkQTerm(f_amp_amp_470, List(i_491), ampamp_501, bang_503)
		val ampamp_505 = FnApp(f_amp_amp_470, List(qTerm_498, qTerm_504))
		val qTerm_506 = QTerm.mkQTerm(f_pipe_pipe_485, List(p_486), ampamp_490, ampamp_505)
		val equiv_507 = FnApp(f_equiv_483, List(ctrue_484, qTerm_506))
		val lesseq_508 = FnApp(f_less_eq_487, List(c0_473, c0_473))
		val lesseq_509 = FnApp(f_less_eq_487, List(c0_473, N_472))
		val ampamp_510 = FnApp(f_amp_amp_470, List(lesseq_508, lesseq_509))
		val ampamp_511 = FnApp(f_amp_amp_470, List(equiv_507, ampamp_510))
		val impl_512 = FnApp(fimpl_469, List(ampamp_482, ampamp_511))

		val f2 = QTerm.mkQTerm(f_pipe_pipe_485, List(p_486), ampamp_490, qTerm_498)

		val result = Z3Prover.prove(qTerm_506.asInstanceOf[TermBool])
        if (result.isDefined){
            PSDbg.logln(result.get.isValid)
            PSDbg.logln(result.get.fullModelDesc)
        } else
            PSDbg.logln(result)

	}

	def main4(args: Array[String]) {
		val equiv = (f1: TermBool, f2: TermBool) => EqEqEqTermBool(f1, f2)
		val impl =  (f1: TermBool, f2: TermBool) => ImplTermBool(f1, f2)
		val rimpl =  (f1: TermBool, f2: TermBool) => RImplTermBool(f2, f1)

		val x = VarBool("x")
		val xp = VarBool("xp")

		//--------------
		val R = equiv
		val r = equiv
		//-------------
		val B = VarBool("B")
		val ctxFn = (f: TermBool) => ExistsTermBool(VarBool("i"), f, B )
		//-------------
		val newCtx = B//TermBool.TrueT
		//---------------
		val po = newCtx.impl(r(x, xp)).impl(R(ctxFn(x), ctxFn(xp)))

        val result = Z3Prover.prove(po)
        if (result.isDefined)
            PSDbg.logln(result.get.isValid)
        else
            PSDbg.logln(result)
    }

	def main5(args: Array[String]) {
		val equiv = (f1: TermBool, f2: TermBool) => EqEqEqTermBool(f1, f2)
		val impl =  (f1: TermBool, f2: TermBool) => ImplTermBool(f1, f2)
		val rimpl =  (f1: TermBool, f2: TermBool) => RImplTermBool(f2, f1)

		val p = VarBool("p")
		val x = VarBool("x")
		val y = VarBool("y")
		val b1 = VarBool("b1")
		val b2 = VarBool("b2")

		val f1 = p impl ((x && b1) || (y && b2))
		val f21 = (p && b1) impl (x)
		val f22 = (p && b2) impl (y)
		val f23 = p impl (b1 || b2)
		val f2 = f21 && f22 && f23
		val f3 = p impl (b1 || b2) && ((b1 impl (x)) && (b2 impl y))
		val f = (f1 && (b1 equiv !b2)) equiv (f3 && (b1 equiv !b2))
		val b = VarBool("b")
		val d = VarBool("d")
		val a = VarBool("a")
		val c = VarBool("c")

		val g = (a || c) impl ((a && b || c && d)equiv((a impl b) && (c impl d)))
		val g2 = (a || c) impl ( d equiv((a impl d) && (c impl d)))

        val result = Z3Prover.prove(g2)
        if (result.isDefined) {
            PSDbg.logln(result.get.isValid)
            PSDbg.logln("\n" + result.get.fullModelDesc)
        }
        else
            PSDbg.logln(result)

	}
	def main6(args: Array[String]) {
		val n1 = VarInt("n1");val n2 = VarInt("n2");val n3 = VarInt("n3");
		val n4 = VarInt("n4");val n5 = VarInt("n5");val n6 = VarInt("n6");val n7 = VarInt("n7");
		val e12 = VarInt("e12");val e24 = VarInt("e24");val e25 = VarInt("e25");
		val e13 = VarInt("e13");val e36 = VarInt("e36");val e37 = VarInt("e37")
		val size = VarInt("size")
		val M = VarInt("M")
		val c0 = ConstInt("0"); val c100 = ConstInt("0")
		val c1 = ConstInt("1");

		var list: List[TermBool] = Nil
		list = List(n1 >= c0, n2 >= c0, n3 >= c0, n4 >= c0, n5 >= c0, n6 >= c0, n7 >= c0) ++ list
		list = List(n1 <= c100, n2 <= c100, n3 <= c100, n4 <= c100, n5 <= c100, n6 <= c100, n7 <= c100) ++ list
		list = List((e12 eqeq c0) || (e12 eqeq c1), (e24 eqeq c0) || (e24 eqeq c1), (e25 eqeq c0) || (e25 eqeq c1)) ++ list
		list = List((e13 eqeq c0) || (e13 eqeq c1), (e36 eqeq c0) || (e36 eqeq c1), (e37 eqeq c0) || (e37 eqeq c1)) ++ list
		list = List(size eqeq e12 + e24 + e25 + e13 + e36 + e37 ) ++ list
		list = ((e12 eqeq c1) impl (n2 + n4 + n5 >= M && n1 + n3 + n6 + n7 >= M)) ::  list
		list = ((e24 eqeq c1) impl ( n4 >= M && n1 + n2 + n5 + n3 + n6 + n7 >= M)) ::  list
		list = ((e25 eqeq c1) impl ( n5 >= M && n1 + n2 + n4 + n3 + n6 + n7 >= M)) ::  list
		list = ((e13 eqeq c1) impl (n1 + n2 + n4 + n5 >= M &&  n3 + n6 + n7 >= M)) ::  list

		list = ((e36 eqeq c1) impl (n2 + n4 + n5 >= M && n1 + n3 + n6 + n7 >= M)) ::  list
		list = ((e37 eqeq c1) impl (n2 + n4 + n5 >= M && n1 + n3 + n6 + n7 >= M)) ::  list
		//list = (c eqeq e12 + e24 + e25 + e13 + e36 + e37) :: list
	}

	def main(args: Array[String]) {
		def c(aConst: Int) = ConstInt(aConst.toString)
		val f = VarArrayBool("f") //[0..N)
		val d = VarInt("d") //0 < d
		val N = VarInt("N") // N >= 0
		val immutableVars = f :: N :: Nil
		//GlobalInvs
		val globalInvs = List(N >= c(0), d > c(0))
		//Mutable variables
		val r = VarBool("r")
		//New variables
		val n = VarInt("n")
		val x = VarInt("x")
		val y = VarInt("y")
		val z = VarInt("z")
		val s = VarBool("s")
		val mutableVars = r :: x :: y :: s :: Nil
		//Dummies
		val i = VarInt("i")
		val j = VarInt("j")
		val m = VarInt("m")
		val xn = VarInt("xn")
		val yn = VarInt("yn")
		val c1 = c(1)
		val c0 = c(0)
		val f0 = VarBool("f0")
		val f1 = VarBool("f1")
		val f2 = VarBool("f2")
		val f3 = VarBool("f3")
		val f4 = VarBool("f4")

		def tt(start: TermInt, end: TermInt) = ForallTermBool(i, start <= i && i < end, f.select(i))
		val xRange = c0 <= x && x < n
		val nRange = c0 <= n //&& (n eqeq c(4)) && (f0 eqeq f.select(c(0))) && (f1 eqeq f.select(c(1))) && (f2 eqeq f.select(c(2))) && (f3 eqeq f.select(c(3))) && (f4 eqeq f.select(c(4)))
		val ctx: TermBool =
			d > c0 &&
			nRange &&
			xRange &&
			tt(x, n) &&
			!f.select(x - c1)

		val formula: TermBool = (n - x >= d) equiv tt(n-d, n)
		val toProve = ctx impl formula
        val result = Z3Prover.prove(toProve)
        if (result.isDefined) {
            PSDbg.logln(result.get.isValid)
            PSDbg.logln("\n" + result.get.fullModelDesc)
        }
        else
            PSDbg.logln(result)
	}

	def testRule(testExpnFn: Fn, stepIntoFirst: Boolean) {
		val a = VarInt("a")
		val b = VarInt("b")
		val term = if(testExpnFn.tpe == PSInt) {
			FnAppInt(testExpnFn, a :: b:: Nil) //a < b
		} else {
			FnAppBool(testExpnFn, a :: b:: Nil) //a < b
		}

		val outerRelations = testExpnFn.tpe match {
			case PSBool => List(EquivBoolFn, ImplBoolFn, RImplBoolFn)
			case PSInt => List(EqEqBoolFn, LEBoolFn, GEBoolFn)
		}

		val innerRelations = testExpnFn.argTpes(0) match {
			case PSBool => List(EquivBoolFn, ImplBoolFn, RImplBoolFn)
			case PSInt => List(EqEqBoolFn, LEBoolFn, GEBoolFn)
		}

		for(outerR <- outerRelations; innerR <- innerRelations) {
			//val testExpnFn = LTBoolFn // <
			//val outerR = ImplBoolFn // ==>
			//val innerR =  LEBoolFn // <=

			val po = if(!stepIntoFirst) {
				val b_prime = VarInt("b_prime")
				val f1 = FnAppBool(innerR, b :: b_prime:: Nil) // b <= b'
				val newOuter =
					if(testExpnFn.tpe == PSInt)
						FnAppInt(testExpnFn, a :: b_prime:: Nil) //a < b'
					else
						FnAppBool(testExpnFn, a :: b_prime:: Nil) //a < b'

				val f2 = FnAppBool(outerR,  term :: newOuter:: Nil) // a < b ==> a < b'
				f1.impl(f2)
			} else{
				val a_prime = VarInt("a_prime")
				val f1 = FnAppBool(innerR, a :: a_prime:: Nil) // a <= a'
				val newOuter =
					if(testExpnFn.tpe == PSInt)
						FnAppInt(testExpnFn, a_prime :: b:: Nil)
					else
						FnAppBool(testExpnFn, a_prime :: b:: Nil)
				val f2 = FnAppBool(outerR, term :: newOuter :: Nil) // a < b ==> a' < b
				(b > ConstInt("0")).impl(f1.impl(f2))
			}


			println(po)
			val result = Z3Prover.prove(po)
			if (result.isDefined) {
				//PSDbg.logln(result.get.isValid)
				//if(!result.get.isValid) PSDbg.logln(result.get.fullModelDesc)
				if(result.get.isValid)
					println(testExpnFn.name, stepIntoFirst, outerR.name, innerR.name)
				else
					println(testExpnFn.name, stepIntoFirst, outerR.name, innerR.name)
					PSDbg.logln(result.get.fullModelDesc)
			} else {
				PSDbg.logln(result)
			}
		}
	}

	def maintmp(args: Array[String]) {
		z3crash()
		return

		//for (aFn <- List(/*EqEqBoolFn, LTBoolFn, GTBoolFn, LEBoolFn, GEBoolFn, PlusIntFn, */MinusIntFn, TimesIntFn, PercentIntFn, DivIntFn)) {
		for (aFn <- List(DivIntFn)) {
			testRule(aFn, true)
			//testRule(aFn, false)
		}
	}

	def z3crash() = {
		//val po = FnAppBool(Fn("impl",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$greater",List(PSInt, PSInt),PSBool),List(VarInt("b"), ConstInt("0"))), FnAppBool(Fn("impl",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$greater$eq",List(PSInt, PSInt),PSBool),List(VarInt("a"), VarInt("a_prime"))), FnAppBool(Fn("$eq$eq",List(PSAny, PSAny),PSBool),List(FnAppInt(Fn("$div",List(PSInt, PSInt),PSInt),List(VarInt("a"), VarInt("b"))), FnAppInt(Fn("$div",List(PSInt, PSInt),PSInt),List(VarInt("a_prime"), VarInt("b")))))))))
		val po = FnAppBool(Fn("impl",List(PSBool, PSBool),PSBool),List(FnAppBool(Fn("$greater$eq",List(PSInt, PSInt),PSBool),List(VarInt("a"), VarInt("a_prime"))) && FnAppBool(Fn("$greater",List(PSInt, PSInt),PSBool),List(VarInt("b"), ConstInt("0"))), FnAppBool(Fn("$greater$eq",List(PSInt, PSInt),PSBool),List(FnAppInt(Fn("$div",List(PSInt, PSInt),PSInt),List(VarInt("a"), VarInt("b"))), FnAppInt(Fn("$div",List(PSInt, PSInt),PSInt),List(VarInt("a_prime"), VarInt("b")))))))
		println(po)
		val result = Z3Prover.prove(po)
		if (result.isDefined) {
			if(result.get.isValid)
				println("valid")
			else
				PSDbg.logln(result.get.fullModelDesc)
		} else {
			PSDbg.logln(result)
		}

	}
}
