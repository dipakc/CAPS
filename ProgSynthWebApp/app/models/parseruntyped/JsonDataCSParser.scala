package models.parseruntyped

import play.api.libs.json.JsValue
import pswebutils.Pswebutils._
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import progsynth.synthesisold.ProgContext
import progsynth.synthesisnew._
import scala.util.control.Breaks._
import scala.collection.mutable.ArrayBuffer
import scalaz._
import Scalaz._
import progsynth.types._
import progsynth.types.Types._
import pswebutils.comment
import scala.util.Try
import scala.util.{Success => TSuccess, Failure => TFailure}
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
import progsynth.printers.XHTMLPrinters2.getArrInternalLhsRhs

/*
 .\docs\models.TermParsers.xlsx
 */

class JsonDataCSParser extends PSCSParser(isMathQuill=true) {

	override val logger = LoggerFactory.getLogger("progsynth.JsonDataCSParser")

	implicit class JSValueImprovements(j: JsValue) {
		//def asTry[Int]: Try[Int] = Try(j.as[Int])
		import play.api.libs.json.Reads
		import play.api.libs.json.JsResultException

		def asTry[T](implicit fjs: Reads[T]): Try[T] = Try{
			fjs.reads(j).fold(
					valid = identity,
					invalid = e => throw new JsResultException(e)
					)
		}
	}

	implicit class TryImprovements[T](t: Try[T]) {
		def errorMsg(error: String): Try[T] = t match {
			case TSuccess(_) => t
			case TFailure(_) =>
				throw new Exception(error)
		}

		def errorMsg(f: String => String): Try[T] = t match {
			case TSuccess(_) => t
			case TFailure(e) =>
				throw new Exception(f(e.getMessage))
		}
	}

//	/** Currently not used. But might need in future*/
//	def psParse(jsVal: JsValue)(implicit ctx: ProgContext, curNode: SynthNode): Option[Any] = {
//		val coreVal = psParseCore(jsVal)
//		if(coreVal.isDefined){
//			coreVal
//		} else {
//			psParseTactic(jsVal)
//		}
//	}

	/** Why to pass ctx as It can be queried from the curNode?
	 * Currently ctx and curNode.progContext are the same.
	 * But in future they might differ in case psParseTactic is called recursively
	 * and in the inner call, we want to focus on some subProgram.
	 * This won't change the curNode but will change the context.
	 */
	def psParseTactic(jsVal: JsValue)(implicit ctx: ProgContext, curNode: SynthNode): Try[Tactic] = {

		def field[T](fieldName: String)(implicit ctx: ProgContext): Try[T] = {
			getField[T](jsVal, fieldName)(ctx)
		}

		val tvName = (jsVal \ "tvName").asOpt[String]
		tvName match {
			case Some("TacticTV") =>
				return psParseTactic((jsVal \ "concreteTV"));

			case Some("StepIntoUnknownProgIdxTV") =>
				field[Integer]("idx") map (new StepIntoUnknownProgIdxTactic(_))

			case Some("RetValTV") =>
				//TODO: Even if the parser fails, the tactic will be applied.
				//Type of initTerm should be Option[Option[Term]]
				field[Term]("initTerm").map(x => new RetValTactic(Some(x)))

			case Some("RTVInPostTV") =>
				for {
					constant <- field[Term]("constant")
					variable <- field[Var]("variable")
					newCtx = ctx.addVal(variable)
					initValue <- field[Term]("initValue")(newCtx)
					bounds <- field[TermBool]("bounds")(newCtx)
				} yield {
					new RTVInPostTactic(constant, variable, initValue, bounds)
				}

			case Some("RTVInPost2TV") =>
				for {
					displayId <- field[Integer]("displayId")
					variable <- field[Var]("variable")
					newCtx = ctx.addVal(variable)
					initValue <- field[Term]("initValue")(newCtx)
					bounds <- field[TermBool]("bounds")(newCtx)
				} yield {
					new RTVInPost2Tactic(displayId, variable, initValue, bounds)
				}

			case Some("SplitoutBoundVariableTV") =>
				for {
					displayId <- field[Integer]("displayId")
					boundVar <- field[Var]("boundVar")
				} yield {
					new SplitoutBoundVariableTactic(displayId, boundVar)
				}

			case Some("DeleteConjunctTV") =>
				val prgError= "Current node is not a Program node"
				val ctxError = "Unable to find context of the postcondition"

				val x = for {
				    CalcProgStep(nodeProg, _) <- Try(curNode.nodeObj)
				} yield {
				    nodeProg
				}

				for {
					CalcProgStep(nodeProg, _) <- Try(curNode.nodeObj)
					programAnn <- Try(nodeProg.asInstanceOf[ProgramAnn]).errorMsg(prgError)
					postInv <- Try(programAnn.post)
					postCtx <- ContextFinder.deleteConjunctFindContext(nodeProg, ctx)(postInv).errorMsg(ctxError)
					conjunct <- field[TermBool]("conjunct")(postCtx)
					variant <- field[Term]("variant")(postCtx)
				} yield {
					new DeleteConjunctTactic(conjunct, variant)
				}

			case Some("StepOutTV") =>
				Try(new StepOutTactic())

			case Some("WhileStrInvSPTV") =>
				Try(new WhileStrInvSPTactic())

			case Some("CollapseCompositionsTV") =>
				Try(new CollapseCompositionsTactic())

			case Some("AssumeToIfTV") =>
				for {
					displayId <- field[Integer]("displayId")
				} yield {
					new AssumeToIfTactic(displayId)
				}
			case Some("PropagateAssumeUpTV") =>
				for {
					displayId <- field[Integer]("displayId")
				} yield {
					new PropagateAssumeUpTactic(displayId)
				}
			case Some("PropagateAssertionsDownSPTV") =>
				for {
					displayId1 <- field[Integer]("displayId1")
					displayId2 <- field[Integer]("displayId2")
				} yield {
					new PropagateAssertionsDownSPTactic(displayId1, displayId2)
				}

			case Some("StrengthenPostSPTV") =>
				for {
					displayId <- field[Integer]("displayId")
				} yield {
					new StrengthenPostSPTactic(displayId)
				}

			case Some("SimplifyTV") =>
				Try(new SimplifyTactic())

			case Some("SimplifyAutoTV") =>
				Try(new SimplifyAutoTactic())

			case Some("InitTV") =>
				for {
					name <- field[String]("name")
					params <- field[List[Var]]("params")
					retVar <- field[Var]("retVar")
					newCtx = ctx.addVals(params)
					preF <- field[TermBool]("preF")(newCtx)
					newCtx2 = newCtx.addVal(retVar)
					postF <- field[TermBool]("postF")(newCtx2)
				} yield {
					new InitTactic(name, params, retVar, preF, postF)
				}

			case Some("IntroAssignmentTV") =>
				for {
					lhsRhsTuples <- field[List[(Term, Term)]]("lhsRhsTuples")
				} yield {
				    /**Handling the arr assignment*/
				    val lhsRhsTuples2: List[(Var, Term)] = lhsRhsTuples map (t => getArrInternalLhsRhs(t._1, t._2))
					new IntroAssignmentTactic(lhsRhsTuples2)
				}

			case Some("IntroAssignmentEndTV") =>
				for {
					lhsRhsTuples <- field[List[(Term, Term)]]("lhsRhsTuples")
				} yield {
				    val lhsRhsTuples2: List[(Var, Term)] = lhsRhsTuples map (t => getArrInternalLhsRhs(t._1, t._2))
					new IntroAssignmentEndTactic(lhsRhsTuples2)
				}

			case Some("Init4TV") =>
				for {
					name <- field[String]("name")
					mutableVars <- field[List[Var]]("mutableVars")
					immutableVars <- field[List[Var]]("immutableVars")
					newCtx = ctx.addVals(immutableVars)
					newCtx2 = newCtx.addVals(mutableVars)
					preF <- field[TermBool]("preF")(newCtx2)
					postF <- field[TermBool]("postF")(newCtx2)
					globalInvs <- field[List[TermBool]]("globalInvs")(newCtx)
				} yield {
					new Init4Tactic(name, immutableVars, mutableVars, globalInvs, preF, postF, Nil)//TODO: read macros
				}

			case Some("MagicTV") =>
				for {
					vars <- field[List[Var]]("vars")
					newCtx = ctx.addVals(vars)
					newF <- field[TermBool]("newF")(newCtx)
				} yield {
					new MagicTactic(vars, newF)
				}

			case Some("AssumePreTV") =>
				for {
					vars <- field[List[Var]]("freshVariables")
					newCtx = ctx.addVals(vars)
					newF <- field[TermBool]("assumedPre")(newCtx)
				} yield {
					new AssumePreTactic(vars, newF)
				}

//			case Some("StartIfDerivationTV") =>
//				for {
//					lhsVars <- field[List[Var]]("lhsVars")
//				} yield {
//					new StartIfDerivationTactic(lhsVars)
//				}

			case Some("StepIntoBATV") =>
				for {
					lhsVars <- field[List[Var]]("lhsVars")
				} yield {
					new StepIntoBATactic(lhsVars)
				}

			case Some("StepIntoIFBATV") =>
				for {
					lhsVars <- field[List[Var]]("lhsVars")
				} yield {
					new StepIntoIFBATactic(lhsVars)
				}

			case Some("IntroSwapTV") =>
				for {
					array <- field[Var]("array")
					index1 <- field[TermInt]("index1")
					index2 <- field[TermInt]("index2")
				} yield {
					new IntroSwapTactic(array, index1, index2)
				}

			case Some("IntroIfTV") =>
				field[List[TermBool]]("guards") map (new IntroIfTactic(_))

			case Some("InstantiateMetaTV") =>
				val primedVarTermListTry: Try[List[(String, Term)]] = field[List[(String, Term)]]("primedVarTermList")

				var failureMsg: Option[String] = None
				var primedVarTermList: List[(Var, Term)]  = Nil
				primedVarTermListTry match {
					case TSuccess(primedVarStrTermList) =>
						for ((primedVarStr, term) <- primedVarStrTermList) {
							val varName = primedVarStr.replace("'", "")
							//check if the varName is in the context
							if(varName.length > 0){
								val aVarOpt = ctx.getVar(varName)(false)
								aVarOpt match {
									case Some(aVar) =>
										primedVarTermList = primedVarTermList ++ List((Var.mkVar(primedVarStr, aVar.getType), term))
									case None =>
										failureMsg = Some("InstantiateMeta: Unable to parse varName: " + varName)
								}
							}else {
								//ignore empty varName since the listGUI has empty rows.
								//TODO: handle the empty rows at client side
							}
						}

					case TFailure(_) =>
						failureMsg = Some("InstantiateMeta: Unable to extrat variable term list")
				}
				if(!failureMsg.isDefined){
					Try(new InstantiateMetaTactic(primedVarTermList))
				} else
					TFailure(new Exception(failureMsg.get))

			case Some("ReplaceFormulaTV") =>
				for {
					//newCtx <- Try(ctx.addVals(List(VarBool("s'"))))
					newFormula  <- field[TermBool]("newFormula")(ctx)
				} yield {
					new ReplaceFormulaTactic(newFormula)
				}

			case Some("GuessGuardTV") =>
				for {
					guard <- field[TermBool]("guard")(ctx)
				} yield {
					new GuessGuardTactic(guard)
				}

			case Some("StartGCmdDerivationTV") =>
				Try(new StartGCmdDerivationTactic())

			case Some("DistributivityTV") =>
				field[Integer]("displayId") map (new DistributivityTactic(_))

			case Some("EmptyRangeTV") =>
				field[Integer]("displayId") map (new EmptyRangeTactic(_))

			case Some("OnePointTV") =>
				field[Integer]("displayId") map (new OnePointTactic(_))

			case Some("QDistributivityTV") =>
				field[Integer]("displayId") map (new QDistributivityTactic(_))

			case Some("RangeSplitTV") =>
				field[Integer]("displayId") map (new RangeSplitTactic(_))

			case Some("ReplaceSubformulaTV") =>
				for {
					oldSubFId <- field[Integer]("oldSubFId")
					newPrgCtx <- extractContext(curNode, oldSubFId)
					newSubF <- field[TermBool]("newSubF")(newPrgCtx)
				} yield
					new ReplaceSubFormulaTactic(oldSubFId, newSubF)

			case Some("ReplaceSubTermTV") =>
				//The new term may contain bound variables that are not there in the current context.
			    for {
			    	subTermId <- field[Integer]("subTermId")
			    					.errorMsg("Failed to parse subTermId")
			        newPrgCtx <- extractContext(curNode, subTermId)
			        				.errorMsg("Failed to extract context of the subTerm with Id " + subTermId)
			        newSubTerm <- field[TermInt]("newSubTerm")(newPrgCtx)
			        				.errorMsg("Failed to parse newSubTerm")
			    } yield {
			    	new ReplaceSubTermTactic(subTermId, newSubTerm)
			    }

			case Some("StartAsgnDerivationTV") =>
				field[List[Var]]("lhsVars") map (new StartAsgnDerivationTactic(_))

			case Some("StepIntoPOTV") =>
				Try(new StepIntoPO())

			//StepIntoProgIdTactic(id: java.lang.Integer)
			case Some("StepIntoProgIdTV") =>
				field[Integer]("id") map (new StepIntoProgIdTactic(_))

			case Some("StepIntoSubProgTV") =>
				field[Integer]("displayId") map (new StepIntoSubProgTactic(_))

			case Some("StepIntoSubFormulaTV") =>
				field[Integer]("subId") map (new StepIntoSubFormulaTactic(_))

			case Some("StrengthenInvariantTV") =>
				field[List[TermBool]]("newInvs") map (new StrengthenInvariantTactic(_))

			case Some("TradingMoveToTermTV") => None
				for {
					displayId <- field[Integer]("displayId")
					termToBeMovedId <- field[Integer]("termToBeMovedId")
				} yield new TradingMoveToTermTactic(displayId, termToBeMovedId)

			case Some("UseAssumptionsTV") =>
				for {
					subFormulaId <- field[Integer]("subFormulaId")
					newSubF <- field[TermBool]("newSubF")
				} yield new UseAssumptionsTactic(subFormulaId, newSubF)

			case Some("Init3TV") =>
				for {
					name <- field[String]("name")
					params <- field[List[Var]]("params")
					retVar <- field[Var]("retVar")
					newCtx = ctx.addVals(params)
					preF <- field[TermBool]("preF")(newCtx)
					newCtx2 =  newCtx.addVal(retVar)
					postF <- field[TermBool]("postF")(newCtx2)
					globalInvs <- field[List[TermBool]]("globalInvs")(newCtx)
				} yield {
					new InitTactic3(name, params, retVar, preF, postF, globalInvs)
				}

			case Some("InsertVariableTV") =>
				//TODO: check types
				for {
					aVar <- field[Var]("aVar")
					initVal <- field[Term]("initVal")
				} yield new InsertVariableTactic(aVar, initVal)

			case _ =>
				logger.error("Tactic not found. Possible Reason: Parser not implemented for the tactic: " + tvName)
				TFailure(new Exception("Tactic not found. Possible Reason: Parser not implemented for the tactic: " + tvName))
		}
	}

	/**
	 * Extract ProgContext of the subformula
	 */
	def extractContext(curNode: SynthNode, subId: Int): Try[ProgContext] = {

		//Find the subTerm and subFrame
		val stepIntoSubTermUtil = new StepIntoSubTerm2{}
		import stepIntoSubTermUtil.getSubTermSubFrame

		(curNode.nodeObj, curNode.frame) match {
			case (cps @ CalcProofStep(term: Term, _, _, freshVariables, _, metaVars), frm: FormulaFrame) =>
				optToTry(getSubTermSubFrame(term, frm)(subId)) map { case (subTerm, subFrame) =>
					// Add accumulated fresh variables and metaVariable to context for parsing.
				    val fSummary = subFrame.getSummary()
			        new ProgContext(
			            varList = fSummary.progFrameSummary.varList ++ freshVariables ++ metaVars,
						valList = fSummary.progFrameSummary.valList,
						dummyList = fSummary.formulaFrameSummary.dummies)
				}
			case _ =>
				logger.error("(nodeObj, Frame) is not of type (CalcProofStep, FormulaFrame) " )
				TFailure(new Exception("(nodeObj, Frame) is not of type (CalcProofStep, FormulaFrame) "))
		}
	}

	/** Currently this function does not need the `curNode` since it is not calling the `psParseTactic` function.
	 *  If you need to call `psParseTactic` from this function, add curNode argument.
	 */

	def psParseCore(jsVal: JsValue)(implicit ctx: ProgContext): Try[Any] = {
		val tvName = (jsVal \ "tvName").asOpt[String]
		tvName match {
			case Some("ListTV") => Try{
				val arrTry = (jsVal \ "items").asTry[List[JsValue]]
				arrTry match {
					case TSuccess(arr) =>
						val a: List[Try[Any]] = arr map psParseCore

						// badCode: This will allow empty rows in list GUI
						// badCode: However incorrect list entries will be ignored !
						val goodCode = true
						if(goodCode) {
							if (a.forall(_.isSuccess))
								a.map(_.get)
							else
								throw new RuntimeException("Unable to parse some of the items in the list")
						} else {
							val a2 = a.filter(_.isSuccess)
							a2.map(_.get)
						}
					case TFailure(e) =>
						logger.error("Error in psParseCore" + e.getMessage())
						throw e
				}
			}
			case Some("TupleTV") =>
				for {
					item1 <- (jsVal \ "item1").asTry[JsValue] flatMap psParseCore
					item2 <- (jsVal \ "item2").asTry[JsValue] flatMap psParseCore
				} yield {
					(item1, item2)
				}
			case Some("TermTV") =>
				val value = getValue(jsVal)
				value flatMap (applyParser(termCSP, _))
			case Some("TermBoolTV") =>
				val value = getValue(jsVal)
				value flatMap (applyParser(termBoolCSP, _))
			case Some("VarTV") =>
				val value = getValue(jsVal)
				value flatMap (applyParser(termCSP, _))
			case Some("NewVarTV") =>
				comment.newvartv
				val varNameTry = (jsVal \ "varNameTV" \ "value").asTry[String]
				val varTypeTry = (jsVal \ "varTypeTV" \ "selectedElem").asTry[String]
				val retValTry = for {
					varName <- varNameTry
					varType <- varTypeTry
				} yield{
					val stringToParse = varName + ":" + varType
					applyParser(newVarCSP, stringToParse)
				}
				retValTry.flatten
			case Some("FOLFormulaTV") =>
				val value = getValue(jsVal)
				val retVal = value flatMap (applyParser(folFormulaCSP, _))
				retVal
			case _ => psParsePrimitive(jsVal)
		}
	}

	def psParsePrimitive(jsVal: JsValue): Try[Any] = {
	    val tvName = (jsVal \ "tvName").asTry[String]
	    tvName match {
			case TSuccess("IntegerTV") | TSuccess("FDisplayIdTV") | TSuccess("PDisplayIdTV")=>
				val value = getValue(jsVal)
				value flatMap (parseInt(_))
			case TSuccess("StringTV") =>
				val value = getValue(jsVal)
				value
			case TSuccess(x) =>
				logger.error("Error in psParsePrimitive" + "TV not supported: " + x)
				TFailure(new Exception("TV not supported: " + x))
			case TFailure(e) =>
				val msg = "Unable to parse tvName value: " + jsVal + e.getMessage
				logger.error(msg)
				TFailure(new Exception(msg))
	    }
	}

	private def getValue(jsVal: JsValue): Try[String] = (jsVal \ "value").asTry[String]

	private def getFieldJsVal(jsVal: JsValue, fieldName: String): Try[JsValue] = Try{
		val fields = (jsVal \ "fields").as[List[JsValue]]
		val filteredFields = fields filter {field =>
			(field \ "fname").asOpt[String] == Some(fieldName)
		}
		if (filteredFields.length == 1)
			filteredFields(0) \ "ftv"
		else
			throw new RuntimeException("filteredFields.length is not 1 for fieldName = " + fieldName + " jsVal = " + jsVal)
	}

	private def getField[T](jsVal: JsValue, fieldName: String)(implicit ctx: ProgContext): Try[T] = {
		val fieldTVTry: Try[JsValue] = getFieldJsVal(jsVal, fieldName)
		val fieldTry: Try[T] = fieldTVTry flatMap (psParseCore(_).asInstanceOf[Try[T]])
		fieldTry
	}

	def applyParser[T](parser: Parser[T], str: String): Try[T] = {
		val res = parseAll(parser, str) match {
			case Success(result, _) => TSuccess(result)
			case NoSuccess(msg, _) =>
				logger.error("Error in applyParser: " + msg)
				TFailure(new Exception(msg))
		}
		res
	}

	def parseInt(s: String): Try[Integer] = s match {
		case "inf" => TSuccess(Integer.MAX_VALUE)
		case _ if s.matches("[+-]?\\d+")  => TSuccess(Integer.parseInt(s))
		case _ =>
			val msg = "Unable to parse integer."
			logger.error(msg)
			TFailure(new Exception(msg))
	}

	//Temporary utility function. TODO: Remove is later
	def optToTry[T](xOpt: Option[T]) = xOpt match {
		case Some(x) => TSuccess(x)
		case None =>
			TFailure(new Exception("XXX"))
	}
}

