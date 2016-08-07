package progsynth.printers
//import scala.tools.nsc.plugins.PluginComponent
import scala.xml._
import progsynth.PSPredef._
import progsynth.types._
import progsynth.types.Types._
import scala.tools.nsc.Global
import scala.collection.mutable.ListBuffer
import progsynth.proofobligations.Z3Result
//import progsynth.extractors.AnnProgConverters._
import progsynth.proofobligations.VarIp
import scala.collection.mutable.LinkedHashMap
import progsynth.synthesisold.PSTacticResult
import progsynth.synthesisold.RetValResult
import progsynth.debug.PSDbg
import progsynth.methodspecs.InterpretedFns
import progsynth.synthesisnew.Macro

trait MQTrait2 {
	def isMQ: Boolean
}

abstract class SymbolManager {
	def mkContent(input: Any): Any
	def getFnSym(afn: Fn): Unparsed
	def getQuantFnSym(afn: Fn): Unparsed
}

class MQSymbolManager extends SymbolManager {
	def mkMQSpan(input: String) = {
		<span class = "mathquill-embedded-latex">{input}</span>
	}

	def mkContent(input: Any) = mkMQSpan(input.toString)
	def getFnSym(afn: Fn): Unparsed = InterpretedFns.getMQFnSym(afn)
	def getQuantFnSym(afn: Fn): Unparsed = InterpretedFns.getMQQuantFnSym(afn)
}

class HtmlSymbolManager extends SymbolManager {
	def mkContent(input: Any) = input
	def getFnSym(afn: Fn): Unparsed = InterpretedFns.getHtmlFnSym(afn)
	def getQuantFnSym(afn: Fn): Unparsed = InterpretedFns.getHtmlQuantFnSym(afn)
}

object SymMgr extends HtmlSymbolManager
////////////////////////////////////////////////////////////////////
object XHTMLPrinters2 extends XHTMLPrinters2Trait

trait XHTMLPrinters2Trait extends ProgramAnnPrinter2 with ProgramAnnPOPrinter2 with POStatusPrinter2
									with SynthInfoPrinter2 with AsgnDerivationPrinter2
									with  IfDerivationPrinter with MQTrait2 with MinimizableDiv {

	override val isMQ = false

	def programAnnToHtmlMain(annProg: ProgramAnn)(implicit focusId: Option[Int]): Elem = {
		<div class="srcfile">
			{poStatusSummary(annProg)}
			{programAnnToHtml(annProg)}
			{toHtmlPO(annProg)}
		</div>
	}
}

trait MinimizableDiv {
    def someOnlyIf[A](p: Boolean)(data: => A) = if (p) Some(data) else None

    def mkMinimizable(minContent: Elem)(maxContent: Elem)(minimize: Boolean) = {
    	<div class="Minimizable">
    		<div class="MinContent" style={someOnlyIf(!minimize)(Text("display:none"))}>
    			{minContent}
    			<div class="MaxLink">max</div>
    		</div>
    		<div class="MaxContent" style={someOnlyIf(minimize)(Text("display:none"))}>
    			{maxContent}
    			<div class="MinLink" style="display:none">min</div>
    		</div>
    	</div>
    }

    def mkMinimizableWithTitle(title: Elem)(content: Elem)(minimize: Boolean) = {
        val status = if(minimize) {"Close"} else {"Open"}

		<div class={"MinimizableWithTitle" + " " + status}>
			<div class="T">
				{title}
				<div class="More Anchor">more</div>
				<div class="Less Anchor">less</div>
			</div>
			<div class="C">
				{content}
			</div>
		</div>
    }


}
trait GeneralPrinters2 extends MQTrait2 {

	/**
	Implicits added to facilitate attribute copy
	//http://stackoverflow.com/questions/2569580/how-to-change-attribute-on-scala-xml-element
	val elem = <b attr1 = "100" attr2= "50"/>

	val elem2 = elem.copy(attributes =
	     for (attr <- elem.attributes) yield attr match {
	           case attr @ Attribute( "attr1", _, _) =>
	                attr.goodcopy(value = attr.value.text.toInt * 2)
	           case attr @ Attribute( "attr2", _, _) =>
	                attr.goodcopy(value = attr.value.text.toInt * -1)
	           case other => other
	    })                                //> elem2  : scala.xml.Elem = <b attr1="200" attr2="-50"/>
	 */

	implicit def addGoodCopyToAttribute(attr: Attribute) = new {
		def goodcopy(key: String = attr.key, value: Any = attr.value): Attribute =
			Attribute(attr.pre, key, Text(value.toString), attr.next)
	}

	implicit def iterableToMetaData(items: Iterable[MetaData]): MetaData = {
		items match {
			case Nil => Null
			case head :: tail => head.copy(next = iterableToMetaData(tail))
		}
	}

	def mkMQSpan(input: String) = {
		<span class = "mathquill-embedded-latex">{input}</span>
	}

//	def anyToHtml(a: Any) = {
//		if(isMQ) {
//			mkMQSpan(a.toString())
//		} else {
//			a.toString()
//		}
//	}
//	def SymMgr.mkContent(input: Any) = {
//		if (isMQ) mkMQSpan(input.toString) else input
//	}

	def mkDecoDiv(decoStr: String) = <div class="deco">{SymMgr.mkContent(decoStr)}</div>

	def mkDecoDiv(decoStr: scala.xml.Unparsed) = <div class="deco">{decoStr}</div>

	def mkKeywordDiv(decoStr: String) = <div class="CapsKeyword">{SymMgr.mkContent(decoStr)}</div>

	def mkKeywordDiv(decoStr: scala.xml.Unparsed) = <div class="CapsKeyword">{decoStr}</div>

	def mkSpaceDiv() = mkDecoDiv(scala.xml.Unparsed("""&nbsp;"""))

	def mkAsgnSymDiv() =  mkSpaceDiv ++ mkDecoDiv(""":=""") ++ mkSpaceDiv

	def mkColonDiv() =  mkDecoDiv(""":""")

	def mkLParenDiv() = mkDecoDiv("""(""")

	def mkRParenDiv() = mkDecoDiv(""")""")

	def mkParenDiv(aDiv: Seq[Node]) = mkLParenDiv ++ aDiv ++ mkRParenDiv

	def mkSqLParenDiv() = mkDecoDiv("""[""")

	def mkSqRParenDiv() = mkDecoDiv("""]""")

	def mkSqParenDiv(aDiv: Seq[Node]) = mkSqLParenDiv ++ aDiv ++ mkSqRParenDiv

	//def mkBracketDiv(aDiv: Elem) = addClassToElem(aDiv, "bracket_scroll_mode")
	def mkBracketDiv(aDiv: Elem) = <div class="bracket_scroll_mode">{aDiv}</div>

	def mkBracketDivs(divs: Seq[Node]) = <div class="bracket_scroll_mode">{divs}</div>

	def bracketIfRequired(anElem: Elem)(selfBP: Int)(parentBP: Int) = {
		if(parentBP > selfBP) mkBracketDiv(anElem) else anElem
	}

	def addClassToElem(elem: Elem, classNames: String*) = {
		val curClassVal: String = {
			elem.attributes match {
				case attr@ Attribute("class", _, _) => attr.value.text
				case _ => ""
			}
		}
		val newClassVal = curClassVal + " " + classNames.mkString(" ")
		elem %  Attribute(None, "class", Text(newClassVal), xml.Null)
	}

}

trait POStatusPrinter2 {
	def poStatusSummary(annProg: ProgramAnn): Seq[Node] = {
		<div class="posummary">
			{if (annProg.allPOsAreValid())
				"PO Status: All Valid"
			else
				"PO Status: Some POs are NOT Valid"
			}
		</div>
	}
}

trait ProgramAnnPrinter2 extends GeneralPrinters2 with FormulaPrinter2 with ProgramPrinter2 {

	//outermost call
	def programAnnToHtml(annProg: ProgramAnn)(implicit focusId: Option[Int]): Seq[Node] = {
		//displayId -> (showPre, showPost)
		def genMinimalAnnotationsMap(pa: ProgramAnn): Map[Int, (Boolean, Boolean)] = {
			var ret = Map.empty[Int, (Boolean, Boolean)]
			ret = ret + (pa.displayId -> (true, true))//Add pre post of outermost program

			def addChildTags(pa: ProgramAnn): Unit = pa match {
				case c @ CompositionC(programs) =>
					//Show post of all except the last one.
					programs.init.map { ip =>
						ret = ret + (ip.displayId -> (false, true))
					}
					programs.map(addChildTags)
				case _ =>
					pa.getSubPas map addChildTags
			}

			addChildTags(pa)
			ret
		}

		val maMap = genMinimalAnnotationsMap(annProg)
		programAnnToHtmlRec(annProg)(maMap, focusId)
	}

	def programAnnToHtmlRec(annProg: ProgramAnn)(implicit maMap: Map[Int, (Boolean, Boolean)], focusId: Option[Int]): Seq[Node] = {

		val maClassNames : List[String] =
			maMap.getOrElse(annProg.displayId, (false, false)) match {
				case (true, true) => List("ShowPre", "ShowPost")
				case (true, false) => List("ShowPre")
				case (false, true) => List("ShowPost")
				case (false, false) => Nil
			}

		val className = (List("ProgramAnn", "unfolded", getClassName(annProg)) ++ maClassNames).mkString(" ")

		val progHtml = annProg match {
			case funProg @ FunctionProgC(_, _, _, _, _) => programToHtml(funProg) /**No pre and post for function program*/
			case exprProg @ ExprProgC(_) => programToHtml(exprProg) /**No pre and post for function Expr Prog*/
			case _ =>
				val (pre, post) = (annProg.pre, annProg.post)
				<collapse>ooo</collapse>
				<div class = {className} displayIdAttr={annProg.displayId.toString}>
					<div class = "id">{SymMgr.mkContent(annProg.id)}</div>
					<div class = "displayId">{SymMgr.mkContent(annProg.displayId)}</div>
					<div class = "pre">{invariantToHtml(pre)}</div>
					<div class = "program">{programToHtml(annProg)(maMap, focusId)}</div>
					<div class="post">{invariantToHtml(post)}</div>
				</div>
		}

		if(focusId.isDefined && annProg.displayId == focusId.get) {
		    <div class = "FocusInner">
                {progHtml}
			</div>
		} else {
		    progHtml
		}
	}

	def getClassName(annProg: ProgramAnn): String = annProg match {
		case _:VarDefProg => "VarDefProgAnn"
		case _:ValDefProg => "ValDefProgAnn"
		case _:AssumeProg => "AssumeProgAnn"
		case _:IfProg => "IfProgAnn"
		case _:WhileProg => "WhileProgAnn"
		case _:Composition=> "CompositionAnn"
		case _:Assignment => "AssignmentAnn"
		case _:SkipProg => "SkipProgAnn"
		case _:UnknownProg => "UnknownProgAnn"
		case _:ExprProg => "ExprProgAnn"
		case _:Identifier => "IdentifierAnn"
		case _:LitConstant => "LitConstantAnn"
		case _:FunctionProg => "FunctionProgAnn"
		case _:ProcedureCall => "ProcedureCallAnn"
		case _ => ""
	}
}

trait ProgramPrinter2 extends GeneralPrinters2 { self: ProgramAnnPrinter2 =>
	def programToHtml(aProgramAnn: ProgramAnn)(implicit maMap: Map[Int, (Boolean, Boolean)], focusId: Option[Int]): Seq[Node] = {
		val pre = aProgramAnn.pre
		val post = aProgramAnn.post
		aProgramAnn match {
		case ifProg @ IfProgC(grdcmds) =>
			//println("ifProgStatus:" + ifProg.grdsComplete)
			val classStr = "IfProg " + (if(!ifProg.grdsComplete) "GrdsCompleteError" else "")
			//println("ClassStr:" + classStr)
			<div class={classStr}>
				{mkKeywordDiv("if")}
				<div class="grdcmds">{for(grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd)}</div>
			</div>
		case WhileProgC(loopInvOpt, grdcmds) =>
			<div class="WhileProg">
				{mkKeywordDiv("while ")}
				{	if (loopInvOpt.isDefined) {
						<div class="loopInv">{termToHtml(loopInvOpt.get)}</div>
					}
				}
				<div class="grdcmds">{for(grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd)}</div>
			</div>
		case CompositionC(annPrograms) =>
			<div class="Composition">
				<div class="programs">{for(annProg <- annPrograms) yield programAnnToHtmlRec(annProg)}</div>
			</div>
		/**Do not show pre and post of valDefs or varDefs whose rhs is a ExprProg.*/
		case valDefExprProg @ ValDefProgC(lhs, Some(ExprProgC(rhsExpr))) =>
			<div class="ValDefProg">
				{mkKeywordDiv("""val""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{termToHtml(rhsExpr)}</div>
			</div>
		/**Do not show pre and post of valDefs or varDefs whose rhs is a ExprProg.*/
		case varDefExprProg @ VarDefProgC(lhs, Some(ExprProgC(rhsExpr))) =>
			<div class="VarDefProg">
				{mkKeywordDiv("""var""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{termToHtml(rhsExpr)}</div>
			</div>
		case valDefProg @ ValDefProgC(lhs, Some(rhs)) =>
			<div class="ValDefProg">
				{mkKeywordDiv("""val""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{programAnnToHtmlRec(rhs)}</div>
			</div>
		case varDefProg @ VarDefProgC(lhs, Some(rhs)) =>
			<div class="VarDefProg">
				{mkKeywordDiv("""var""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{programAnnToHtmlRec(rhs)}</div>
			</div>
		case valDefProg @ ValDefProgC(lhs, None) =>
			<div class="ValDefProg">
				{mkKeywordDiv("""val""")}
				<div class="lhs">{termToHtml(lhs)}</div>
			</div>
		case varDefProg @ VarDefProgC(lhs, None) =>
			<div class="VarDefProg">
				{mkKeywordDiv("""var""")}
				<div class="lhs">{termToHtml(lhs)}</div>
			</div>

		// arr := ArrStore(arr, i, 0) should be displayed as
		//arr[i] := 0

		/**<img src="..\..\..\..\..\src\main\resources\doc_resources\MultiAssignmentHtmlDisplay.png" alt="MultiAssignmentHtmlDisplay.png" />*/
		case AssignmentC(asgns) =>
			val retVal =
				<div class="Assignment program">
					{asgnToTable(asgns)}
				</div>
				//Add a MultiAssignement class in case of simultaneous assignments.
				if (asgns.length > 1)
					retVal %  Attribute(None, "class", Text("MultiAssignment"), xml.Null)
				else retVal
		case SkipProgC(_) =>
			<div class="SkipProg">SkipProg</div>
		case UnknownProgC(id) =>
			<div class="UnknownProg">UnknownProg({id})</div>
		case AssumeProgC(pred) =>
			<div class="AssumeProg">{mkDecoDiv("""Assume""")} {mkBracketDivs(termToHtml(pred))} </div>
		case ExprProgC(expr) =>
			<div class="ExprProg">
				<div class="expr">{termToHtml(expr)}</div>
			</div>
		case IdentifierC(name, itype) =>
			<div class="Identifier">
				<div class="name">{SymMgr.mkContent(name)}</div>
				{mkColonDiv}
				<div class="itype">{SymMgr.mkContent(itype)}</div>
			</div>
		case LitConstantC(name) =>
			<div class="LitConstant">
				<div class="name">{SymMgr.mkContent(name)}</div>
			</div>
		case FunctionProgC(name, params, retVar, annProg, globalInvs) => //TODO: print globalInvs
			<div class="FunctionProg">
				{mkDecoDiv("def " + name + "(" + params.map(av => av.v + ": " + av.getType.getCleanName).mkString(", ") +  ") returns " + retVar.v + ": " + retVar.getType.getCleanName)}
				<div class="annProg">{programAnnToHtmlRec(annProg)}</div>
			</div>
		case ProcedureCallC(procDef, params) =>
			<div class="ProcedureCall">
				{mkDecoDiv(procDef.name)}
				{mkDecoDiv("(")}
				{
					termToHtml(params.head) ++
					params.tail.map(p => mkDecoDiv(",") ++ termToHtml(p))
				}
				{mkDecoDiv(")")}
			</div>
		}
	}

	/**
	 * Example: <br>
	 * Input: arr, arr.store(i, exp) <br>
	 * Output: arr[i], exp
	 * */
	def getArrDisplayLhsRhs(lhs: Var, rhs: Term): (Term, Term) = (lhs, rhs) match {
	    case (arr, ArrStore(arr2, i, exp)) if (arr == arr2) =>
	        (ArrSelect.mkArrSelect(arr, i, exp.getType), exp)
        case _ =>
            (lhs, rhs)
	}

	def getArrInternalLhsRhs(lhs: Term, rhs: Term): (Var, Term) = (lhs, rhs) match {
	    case (ArrSelect(arr:Var, i), exp) => (arr, ArrStore.mkArrStore(arr, i, exp, arr.getType))
	    case (lhs: Var, rhs: Term) => (lhs, rhs)
	    case _ => throw new RuntimeException("Failed in getArrInternalLhsRhs")
	}

	def asgnToTable(asgns: List[(Var, ProgramAnn)], asgnSym: String = ":=")(implicit maMap: Map[Int, (Boolean, Boolean)], focusId: Option[Int]): Elem = {

		<table class='tablestyle'>
		{ asgns.zipWithIndex map {
				case ((lhs, ExprProgC(ArrStore(arrTerm, indexTerm, valTerm))), index) if (lhs == arrTerm) => {
				    // "Arr := Arr.store(i, v)" is displayed as "Arr[i] := v"
				    val lhsDisplay = ArrSelect.mkArrSelect(lhs, indexTerm, valTerm.getType())
				    val rhsDisplay = valTerm
					<tr>
						<td class="lhsvar">{termToHtml(lhsDisplay)}</td>
						{ 	if (index == 0 ) <td> {asgnSym} </td> %  Attribute(None, "rowspan", Text(asgns.length.toString), xml.Null)
							else " "
						}
						<td class="rhsprog">{termToHtml(rhsDisplay)}</td>
					</tr>
				}
				case ((lhs, rhs), index) =>
					<tr>
						<td class="lhsvar">{termToHtml(lhs)}</td>
						{ 	if (index == 0 ) <td> {asgnSym} </td> %  Attribute(None, "rowspan", Text(asgns.length.toString), xml.Null)
							else " "
						}
						<td class="rhsprog">{programAnnToHtmlRec(rhs)}</td>
					</tr>
			}
		}
		</table>
	}

	def asgnToTable2(vars: List[Var], terms: List[Term], asgnSym: String = ":=")(implicit maMap: Map[Int, (Boolean, Boolean)], focusId: Option[Int]) : Elem = {
		asgnToTable(vars zip (terms map {ExprProg(_)}), asgnSym)
	}

	def grdcmdToHtml(grdcmd: GuardedCmd)(implicit maMap: Map[Int, (Boolean, Boolean)], focusId: Option[Int]): Seq[Node] = {
		val GuardedCmd(guard, cmd) = grdcmd
		<div class="GuardedCmd">
			<div class="guard-arrow">
				<div class="guard">{termToHtml(guard)}</div>
				{mkKeywordDiv(scala.xml.Unparsed("""&mdash;&mdash;&rarr;"""))}
			</div>
			<div class="cmd">{programAnnToHtmlRec(cmd)}</div>
		</div>
	}
}

trait TermBindingPower {
	import InterpretedFns._

	val minBP = -1
	val maxBP = 100
	def getBP(term: Any): Int = {

		val group0Max = 90
		val group1Max = 80
		val group2Max = 70
		val group3Max = 60

		term match {
			//Group 0: Atomic Group //Because of the high binding power, these terms will not be bracketed.
			case _: Const  => group0Max
			case _: Var => group0Max
			case _: UnkTerm => group0Max
			case _: ArrSelect => group0Max //Array selects are syntactically atomic. eg. a[5 = 4]
			case _ : ArrStore => group0Max //Array stores are syntactically atomic. eg. a.Store(b)
			case _: QTerm => group0Max //All QTerms, by syntax, are already bracketed.

			//==========================================
			// Group 1: Int * Int -> Int
			case FnAppInt(UnaryMinusIntFn, _) => group1Max

			case FnAppInt(DivIntFn, _) => group1Max - 1
			case FnAppInt(PercentIntFn, _) => group1Max - 1
			case FnAppInt(TimesIntFn, _) => group1Max - 1

			case FnAppInt(MinusIntFn, _) => group1Max - 2
			case FnAppInt(PlusIntFn, _) => group1Max - 2
			//==============================================
			// Group 2: Int * Int -> Bool
			case FnAppBool(LTBoolFn, _) => group2Max
			case FnAppBool(GTBoolFn, _) => group2Max
			case FnAppBool(GEBoolFn, _) => group2Max
			case FnAppBool(LEBoolFn, _) => group2Max
			case FnAppBool(EqEqBoolFn, _) => group2Max
			//==============================================
			// Group 3: Bool * Bool -> Bool
			case FnAppBool(NegBoolFn, _) => group3Max
			case FnAppBool(AndBoolFn, _) => group3Max - 1
			case FnAppBool(OrBoolFn, _) => group3Max - 2
			case FnAppBool(ImplBoolFn, _) => group3Max - 3
			case FnAppBool(RImplBoolFn, _) => group3Max - 3
			case FnAppBool(EquivBoolFn, _) => group3Max - 4
			//==============================================
			case FnApp(_, _) => group0Max //uninterpreted function has high binding power

		}
	}
}

trait TermPrinter2 extends MQTrait2 with GeneralPrinters2 with TermBindingPower {
	def mkInterpretedFnContent(afn: Fn) =
		SymMgr.mkContent(SymMgr.getFnSym(afn))

	def fnToHtml(afn: Fn): Seq[Node] = afn match {
		case Fn(name, argTpes, tpe) =>
			<div class="fn">
				<div class="name">{mkInterpretedFnContent(afn)}</div>
			</div>
	}

	def fnAppToHtml(fnapp: FnApp, parentBP: Int): Elem = fnapp match {
		case FnApp(fn, ts) if InterpretedFns.isInfix(fn)=>
		    val fnClass = fn.name.replaceAll("\\$", "")
			<div class="FnApp Term" displayIdAttr={fnapp.displayId.toString}>
						<div class="t1">{termToHtml2(ts(0), getBP(fnapp))}</div>
						<div class={"opr" + " " + fnClass}>{mkInterpretedFnContent(fn)}</div>
						<div class="t2">{termToHtml2(ts(1), getBP(fnapp))}</div>
			</div>
		case FnApp(fn, ts) =>
			<div class="FnApp Term" displayIdAttr={fnapp.displayId.toString}>
				<div class="afn">{fnToHtml(fn)}</div>
				{mkBracketDiv(
					<div class="ts">{termsToHtml2(ts, minBP)}</div>
				)}
			</div>
	}


	def termToHtml(t: Term):Seq[Node] = termToHtml2(t, minBP)

	def termToHtml2(t: Term, parentBP: Int):Seq[Node] = {
		val displayId = t.displayId.toString
		val retVal = t match {
			case aVar @ Var(v) =>
				<div class="Var Term" displayIdAttr={displayId}>
					<div class="v">{SymMgr.mkContent(v)}</div>
					{mkColonDiv}
					<div class="t">{SymMgr.mkContent(aVar.getType)}</div>
				</div>
			case x @ FnApp(_, _) => {fnAppToHtml(x, parentBP)}
			case aConst @ Const(name) =>
				<div class="Const Term" displayIdAttr={displayId}>
					<div class="name">{SymMgr.mkContent(name)}</div>
					{mkColonDiv}
					<div class="tpe">{SymMgr.mkContent(aConst.getType)}</div>
				</div>
			case ArrSelect(arr, index)  =>
				<div class="ArrSelect Term" displayIdAttr={displayId}>
					<div class="arr">{termToHtml2(arr, getBP(t))}</div>
					{mkSqParenDiv(<div class="index">{termToHtml2(index, minBP)}</div>)}
				</div>
			case ArrStore(arr, index, value) =>
				//pass minBP as the parentBP of the subterms since the added bracket is the parent.
				<div class="ArrStore Term" displayIdAttr={displayId}>
					<div class="arr">{termToHtml2(arr, getBP(t))}</div>
					<div class="deco">.Store</div>
					{mkBracketDiv(
					<div>
						<div class="index">{termToHtml2(index, minBP)}</div>
						{mkDecoDiv(""",""")}
						<div class="value">{termToHtml2(value, minBP)}</div>
					</div>
					)}
				</div>
			case QTerm(opr, dummies, range, term) =>
				//pass minBP as the parentBP of the subterms since the added bracket is the parent.
				{mkBracketDiv(	//All QTerms, by syntax, are always bracketed.
				<div class="QTerm Term" displayIdAttr={displayId}>
					<div class="quant Term" >{SymMgr.getQuantFnSym(opr)}</div>
					<div class="dummies">{
						(dummies.init map (termToHtml2(_, minBP) ++ mkDecoDiv(","))) ++
						termToHtml2(dummies.last, minBP)
					}</div>
					<div class="deco">:</div>
					<div class="qrange">{if (range != TermBool.TrueT) termToHtml2(range, minBP)}</div>
					<div class="deco">:</div>
					<div class="qterm">{termToHtml2(term, minBP)} </div>
				</div>
				)}
			case _: UnkTerm => <div class="UnkTerm Term">UnkTerm</div>
		}
		bracketIfRequired(retVal)(getBP(t))(parentBP)
	}

	def macroToHtml(aMacro: Macro): Seq[Node] = {
		<div class="MacroDef">
			<div>
				<div>
				<div>{aMacro.name}</div>
				<div>(</div>
				<div>
				{
					aMacro.params.map{p =>p.v + ":" + p.getType.getCleanName}.mkString(", ")
				}
				</div>
				<div>): {aMacro.retType.getCleanName }  &nbsp; = &nbsp;</div>
				</div>
				{termToHtml(aMacro.body)}
			</div>
		</div>
	}

	def termsToHtml(xs: List[Term]):List[Any] = termsToHtml2(xs, minBP)

	def termsToHtml2(xs: List[Term], parentBP: Int):List[Any] = {
		val retVal: ListBuffer[Any] = new ListBuffer[Any]
		retVal += termToHtml2(xs.head, parentBP)
		for ( x <- xs.tail) {
			retVal += mkDecoDiv(",")
			retVal += termToHtml2(x, parentBP)
		}
		retVal.toList
	}
}

trait FormulaPrinter2Utils extends MQTrait2 with GeneralPrinters2{
	def mkNotDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.NegBoolFn))
	def mkAndDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.AndBoolFn))
	def mkOrDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.OrBoolFn))
	def mkImplDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.ImplBoolFn))
	def mkRImplDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.RImplBoolFn))
	def mkEquivDiv() = mkDecoDiv(SymMgr.getFnSym(InterpretedFns.EquivBoolFn))
	def mkForallDiv() = mkDecoDiv(SymMgr.getQuantFnSym(InterpretedFns.AndBoolFn))
	def mkExistsDiv() = mkDecoDiv(SymMgr.getQuantFnSym(InterpretedFns.OrBoolFn))
}

trait FormulaPrinter2 extends GeneralPrinters2 with FormulaPrinter2Utils with TermPrinter2{

	def formulaDiv (f: FOLFormula, className: String): Elem = {
		addClassToElem(<div>{formulaToHtml(f)}</div>, className)
	}

	def bracketedFormulaDiv (f: FOLFormula, className: String): Elem = {
		val isComposite = f match {
			case True1() | False1() | Atom(_) |	TrueF | FalseF | Not(_) => false
			case _ => true
		}
		val fDiv = formulaDiv(f, className)
		//Add bracket class if composite
		if (isComposite)
			mkBracketDiv(fDiv)
		else
			fDiv
	}

	def parenFormulaDiv(f: FOLFormula, className: String):Seq[Node] = {
		val isComposite = f match {
			case True1() | False1() | Atom(_) |	TrueF | FalseF | Not(_) => false
			case _ => true
		}
		val fDiv = formulaDiv(f, className)
		if (isComposite) mkParenDiv(fDiv) else fDiv
	}

	def formulaToHtml(formula: FOLFormula):Seq[Node] = formula match {
		case True1() =>
			<div class="True1" displayIdAttr={formula.displayId.toString}>{SymMgr.mkContent("True")}</div>
		case False1() =>
			<div class="False1"  displayIdAttr={formula.displayId.toString}>{SymMgr.mkContent("False")}</div>
		case Atom(a) =>
		    val predPPrint = false
		    if( predPPrint){
			<div class="Atom">
				<div class="a">{a.pprint}</div>
			</div>
		    } else {
		    	<div class="Atom">
		    	<div class="a">{predToHtml(a)}</div>
		    	</div>
		    }
		case Not(f) =>
			<div class="Not" displayIdAttr={formula.displayId.toString}>
				{mkNotDiv}
				{mkBracketDiv(<div class="f">{formulaToHtml(f)}</div>)}
			</div>
		case AndN(fs) if (fs.length > 1 ) =>
			val x = fs flatMap {fi =>
				mkAndDiv ++ bracketedFormulaDiv(fi, "fi")
			}
		    <div class="AndN" displayIdAttr={formula.displayId.toString}>
				{x.tail}
			</div>
		case And(f1, f2) => /** Unreachable case */
			<div class="And" displayIdAttr={formula.displayId.toString}>
				{bracketedFormulaDiv(f1, "f1")}
				{//mkDecoDiv(scala.xml.Unparsed("""&and;"""))
					mkAndDiv
				}
				{bracketedFormulaDiv(f2, "f2")}
				</div>
		case OrN(fs) if (fs.length > 1 ) =>
		    val x = fs flatMap {fi => mkOrDiv ++ bracketedFormulaDiv(fi, "fi")}
		    <div class="OrN">
				{x.tail}
			</div>

		case Or(f1, f2) => /** Unreachable case */
			<div class="Or" displayIdAttr={formula.displayId.toString}>
				{bracketedFormulaDiv(f1, "f1")}
				{//mkDecoDiv(scala.xml.Unparsed("""&or;"""))
					mkOrDiv
				}
				{bracketedFormulaDiv(f2, "f2")}
				</div>
		case Impl(f1, f2) =>
			<div class="Impl" displayIdAttr={formula.displayId.toString}>
				{bracketedFormulaDiv(f1, "f1")}
				{mkImplDiv}
				{bracketedFormulaDiv(f2, "f2")}
			</div>
		case Iff(f1, f2) =>
			<div class="Iff" displayIdAttr={formula.displayId.toString}>
				{bracketedFormulaDiv(f1, "f1")}
				{mkEquivDiv}
				{bracketedFormulaDiv(f2, "f2")}
			</div>
		case Forall(v, f) =>
			<div class="Forall" displayIdAttr={formula.displayId.toString}>
				{mkForallDiv}
				<div class="v">{SymMgr.mkContent(v.v)}</div>
				{mkColonDiv}
				<div class="f">{formulaToHtml(f)}</div>
			</div>
		case Exists(v, f) =>
			<div class="Exists" displayIdAttr={formula.displayId.toString}>
				{mkExistsDiv}
				<div class="v">{SymMgr.mkContent(v.v)}</div>
				{mkColonDiv}
				<div class="f">{formulaToHtml(f)}</div>
			</div>
		case Unknown() => <div class="Unknown">Unknown</div>
	}

	def getPredSymContent(s: String) = s match {
		case "<=" => if (isMQ) mkMQSpan("""\le""") else scala.xml.Unparsed("""&le;""")
		case ">=" => if (isMQ) mkMQSpan("""\ge""") else scala.xml.Unparsed("""&ge;""")
		case "==" => if (isMQ) mkMQSpan("""=""") else scala.xml.Unparsed("""=""")
		case "<" => if (isMQ) mkMQSpan("""<""") else scala.xml.Unparsed("""<""")
		case ">" => if (isMQ) mkMQSpan(""">""") else scala.xml.Unparsed(""">""")
		case _ => scala.xml.Unparsed(s)
	}

	def predToHtml(p: Pred):Seq[Node] = p match {
		case Pred(r, ts) if ComparisonPreds.preds contains r =>
			<div class="Pred" displayIdAttr={p.displayId.toString}>
				<div class="ts">{termToHtml(ts(0))}</div>
				<div class="r">{getPredSymContent(ComparisonPreds.getsym(r))}</div>
				<div class="ts">{termToHtml(ts(1))}</div>
			</div>
		case Pred(r, ts) if r == "BoolPred" =>
			<div class="Pred" displayIdAttr={p.displayId.toString}>
				<div class="ts">{termsToHtml(ts)}</div>
			</div>
		case Pred(r, ts) =>
			<div class="Pred" displayIdAttr={p.displayId.toString}>
				<div class="r">{SymMgr.mkContent(r)}</div>
				{mkLParenDiv}
				<div class="ts">{termsToHtml(ts)}</div>
				{mkRParenDiv}
			</div>
		//case _ => <div class="NotImpl"></div>
	}


	def invariantToHtml(inv: InvariantT): Seq[Node] = {
		val InvariantT(locOpt, term, rvVarOpt) = inv
		<div class="Invariant" displayIdAttr={inv.displayId.toString}>
			<div class="loc">{SymMgr.mkContent(locOpt)}</div>
			<div class="term">{termToHtml(term)}</div>
		</div>
		//<div class="rvVar">{rvVarOpt map (_.v) getOrElse ""}</div>
	}
}


trait ProgramAnnPOPrinter2 extends FormulaPrinter2{
	def statusToHtml(statusOpt: Option[Z3Result]): Seq[Node] = {
		if (statusOpt.isDefined) {
			val status = statusOpt.get
			<collapse>ooo</collapse>
			<div class="status">
			{if(status.error.isDefined) <collapse>ooo</collapse> ++ <div class="error">{status.error.get}</div>}
			{if (status.isValid) <div class="valid">valid</div> else <div class="notvalid">not_valid</div>}
			{if (!status.modelVarIps.isEmpty)
				<collapse>ooo</collapse>
				<div class="model">{
					(for (VarIp(aVar, value) <- status.modelVarIps) yield {
						<div class="varitem">
							<div class="variable">{aVar.v + ":" + aVar.getType + " ="}</div>
							<div class="value">{value}</div>
						</div>
					})(collection.breakOut)}
				</div>
			}
			{if (!status.isValid)
				<div class="fullmodel">{status.fullModelDesc}</div>
			}
			</div>
		} else {
			<div class="status failed">Failed</div>
		}
	}

	def toHtmlPO(annProg: ProgramAnn): Seq[Node] = {
		PSDbg.writeln0(annProg.proofObligs.length)
		def handleNonFunctionProg = {
			val poIdDiv = <div class="pa_id">{annProg.id}</div>
			val poDiv = for (pobg <- annProg.proofObligs ) yield {
				<div class="proofoblig">
					{termToHtml(pobg.term)}
					{statusToHtml(pobg.status)}
				</div>
			}
			val subPoDiv = <div class="prg_proofobligs">{toHtmlPOContent(annProg)}</div>;

			<collapse>ooo</collapse> ++
			<div class="pa_proofobligs unfolded">
			{ poIdDiv ++ poDiv ++ subPoDiv }
			</div>
		}
		annProg match {
			case FunctionProgC(_, _, _, pa, _) => toHtmlPO(pa) //TODO: ensure that globalInvs are handled properly
			case _ => handleNonFunctionProg
		}
	}

	def toHtmlPOContent(prog: ProgramAnn): Seq[Node] = prog match {
		case FunctionProgC(name, _, _, annProg, _) =>
			throw new RuntimeException("toHtmlPOContent called on FunctionProg")
		case IfProgC(grdcmds) => grdcmds flatMap toHtmlPO _
		case WhileProgC(_, grdcmds) => grdcmds flatMap toHtmlPO _
		case CompositionC(programs) => programs flatMap toHtmlPO _
		case ValDefProgC(lhs, Some(rhs)) => toHtmlPO(rhs)
		case VarDefProgC(lhs, Some(rhs)) => toHtmlPO(rhs)
		case AssignmentC((lhs, rhs)::Nil) => toHtmlPO(rhs)
		case AssignmentC(_) => Nil //throw new RuntimeException("toHtmlPOContent of simultaneous assignments not implemented")
		case _: SkipProg => Nil
		case _: UnknownProg => Nil
		case _: AssumeProg => Nil
		case _: Identifier => Nil
		case VarDefProgC(_, None) => Nil
		case ValDefProgC(_, None) => Nil
		case _: LitConstant => Nil
		case _: ExprProg => Nil
		case _: ProcedureCall => Nil
	}

	def toHtmlPO(grdcmd: GuardedCmd): Seq[Node] = {
		val GuardedCmd(guard, cmd) = grdcmd
		toHtmlPO(cmd)
	}
}

trait SynthInfoPrinter2 extends ProgramAnnPrinter2{
	def synthInfoToHtml(synthInfo: LinkedHashMap[UnknownProg, List[PSTacticResult]])(implicit focusId: Option[Int]): Seq[Node] = {
		if (!synthInfo.isEmpty){
			val elem = <div class="synth_info">
						{mkDecoDiv("Synthesis Info:")}
						{for( (unkProg, pas) <- synthInfo) yield synthPairToHtml(unkProg, pas)}
						</div>
			List(elem)
		} else
			Nil
	}

	def synthPairToHtml(unkProg: UnknownProg, pstrList: List[PSTacticResult])(implicit focusId: Option[Int]): Seq[Node] = {
		<div class="synth_item"> {
			mkDecoDiv("Unknown Program:") ++
			programAnnToHtml(unkProg) ++
			<div class="synth_pas">
			{mkDecoDiv("Synthesized Programs:")}
			{ 	pstrList map (_.toHtml)}
			</div>
		}
		</div>
	}

}

//trait RetValTacticResultPrinter2 extends ProgramAnnPrinter2{ self: RetValResult =>
//	override val isMQ = false
//	def toHtml(implicit focusId: Option[Int]): Seq[Node] = {
//		val x = (self.resultProg map {x => programAnnToHtml(x)(focusId)})
//		val y = x.toSeq.flatten
//		<div class="tactic_result">
//			<div class="tactic_name">RetValTactic</div>
//			<div class="synth_pas">
//			{(self.resultProg map {x => programAnnToHtml(x)(focusId)})
//				.toSeq.flatten}
//			</div>
//		</div>
//	}
//}

//	var initUnk: Option[ProgramAnn] = None
//	var whileProg: Option[ProgramAnn] = None
//	var whileBodyUnk: Option[ProgramAnn]= None
//	var resultProg: Option[ProgramAnn]= None
//	var whileGuard: Option[ProgramAnn]= None
//	var boundFs: List[FOLFormula]= Nil
/*
trait RCVTacticResultPrinter2 extends ProgramAnnPrinter2 { self: RCVResult =>
	override val isMQ = true
	def toHtml(): Seq[Node] = {
		<div class="tactic_result">
			<div>Tactic Name: Replace constant by variable</div>
			<div>Replaced constants: TODO</div>
			<div>New variables: TODO</div>
			<div class="synth_pas">
			{(self.resultProg map programAnnToHtml)
				.toSeq.flatten}
			</div>
		</div>
	}
}
*/
trait AsgnDerivationPrinter2 extends ProgramAnnPrinter2 {
	import progsynth.synthesisnew.AsgnDerivation
	import progsynth.synthesisnew.Hint

	def hintToHtml(hint: Hint) = {
		PSDbg.writeln1(hint.value)
		<div class="hintValue">{hint.value}</div>
	}

	def asgnDerivation2ToHtml(asgnDeriv: AsgnDerivation)(implicit focusId: Option[Int]) = {
		<div class="asgnDerivation">
			<div class="derivationSrcProg">{programAnnToHtml(asgnDeriv.dummyAsgnProg)}</div>
			<div class="heading"> Proof Obligation: </div>
			<div class="toProve" >
				<div class="antecedent">
				{termToHtml(asgnDeriv.unkProg.pre.term)}
				</div>
				<div class="toProveDeco">&rArr;</div>
				<div class="consequent">
						<div>wp.(S).(</div>
						{termToHtml(asgnDeriv.unkProg.post.term)}
						<div>)</div>
				</div>
			</div>
			<div class="MetaVariableDiv">
			<div class="heading">Meta Variables</div>
			<br></br>
			<table border='1' cellpadding="5">
				<tr><th>Meta-variable</th><th>Derived Term</th></tr>
				{	for ((aVar, termO) <- asgnDeriv.derivedTerms) yield {
						termO match {
							case Some(term) =>
								<tr>
									<td>{XHTMLPrinters2.termToHtml(aVar)}</td>
									<td>{XHTMLPrinters2.termToHtml(term)}</td>
								</tr>
							case None =>
								<tr>
									<td>{XHTMLPrinters2.termToHtml(aVar)}</td>
									<td></td>
								</tr>
						}
					}
				}
			</table>
			<div>Apply the StepIntoPO tactic to step into the proof obligation.</div>
			</div>
		</div>
	}
}

trait IfDerivationPrinter extends ProgramAnnPrinter2 {
	import progsynth.synthesisnew.IfDerivation
	import progsynth.synthesisnew.Hint

	def ifDerivation2ToHtml(ifDeriv: IfDerivation) = {
		<div class="ifDerivation">
			<div class="GuardedCmdDiv">
			<div class="DerivedGuardedCommands">Derived Guarded Commands</div>

			<div class="StatusTable">
			<table border='1' cellpadding="5">
				<tr><td>Precondition: </td><td>{XHTMLPrinters2.termToHtml(ifDeriv.unkProg.pre.term)}</td></tr>
				<tr><td>Postcondition: </td><td>{XHTMLPrinters2.termToHtml(ifDeriv.unkProg.post.term)}</td></tr>

				<tr><td>Status:</td><td><div>{ifDeriv.guardedCmds.length}</div> <div>&nbsp; Guarded Commands derived.</div></td></tr>
			</table>
			</div>

			{assumedPreInfo(ifDeriv)}

			<div class="GrdCmdTable">
			<table border='1' cellpadding="5" >
				<tr><th>Guard</th><th>Cmd</th></tr>
				{	for ((grd, cmds) <- ifDeriv.guardedCmds) yield {
					<tr>
						<td>{XHTMLPrinters2.termToHtml(grd)}</td>
						<td>
							<table border='1' cellpadding="5">
								<tr><th>Meta-variable</th><th>Derived Term</th></tr>
								{	for ((aVar, termO) <- cmds) yield {
										termO match {
											case Some(term) =>
												<tr>
													<td>{XHTMLPrinters2.termToHtml(aVar)}</td>
													<td>{XHTMLPrinters2.termToHtml(term)}</td>
												</tr>
											case None =>
												<tr>
													<td>{XHTMLPrinters2.termToHtml(aVar)}</td>
													<td></td>
												</tr>
										}
									}
								}
							</table>
						</td>
					</tr>
					}
				}
			</table>
			</div>
			<div>Apply the StartGCmdDerivation tactic to start derivation of a guarded command.</div>
			</div>
		</div>
	}

	/**
	 *
	 * */
	def assumedPreInfo(ifDeriv: IfDerivation) = {
		<div class="FVAndAssumedPreInfo">
			<div class="Section">
				<div class="field1">Fresh Variables Introduced</div>
				<div class="field2">{ifDeriv.freshVars.map{avar => avar.v + ": " + avar.getType.toString }.mkString(", ")}</div>
			</div>
			<div class="Section">
				<div class="Header">Assumed Preconditions</div>
				<div class="List">
					<table border="1">
						{
							for (pre <- ifDeriv.assumedPres) yield
								<tr><td>{XHTMLPrinters2.termToHtml(pre)}</td></tr>
						}
					</table>
				</div>
			</div>
		</div>
	}
}
