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
//import progsynth.synthesisold.RCVResult
import progsynth.debug.PSDbg
trait MQTrait {
	def isMQ: Boolean
}

object XHTMLPrintersOld extends ProgramAnnPrinter with ProgramAnnPOPrinter with POStatusPrinter
	with SynthInfoPrinter with MQTrait{
	override val isMQ = false
	def programAnnToHtmlMain(annProg: ProgramAnn): Elem = {
		<div class="srcfile">
			{poStatusSummary(annProg)}
			{programAnnToHtml(annProg)}
			{toHtmlPO(annProg)}
		</div>
	}
}

trait GeneralPrinters extends MQTrait {

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
	def mkContent(input: Any) = {
		if (isMQ) mkMQSpan(input.toString) else input
	}

	def mkDecoDiv(decoStr: String) = <div class="deco">{mkContent(decoStr)}</div>

	def mkDecoDiv(decoStr: scala.xml.Unparsed) = <div class="deco">{decoStr}</div>

	def mkSpaceDiv() = mkDecoDiv(scala.xml.Unparsed("""&nbsp;"""))

	def mkAsgnSymDiv() =  mkSpaceDiv ++ mkDecoDiv(""":=""") ++ mkSpaceDiv

	def mkColonDiv() =  mkDecoDiv(""":""")

	def mkLParenDiv() = mkDecoDiv("""(""")

	def mkRParenDiv() = mkDecoDiv(""")""")
}

trait POStatusPrinter {
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

trait ProgramAnnPrinter extends GeneralPrinters with FormulaPrinter with ProgramPrinter {
	def programAnnToHtml(annProg: ProgramAnn): Seq[Node] = {
		annProg match {
			case funProg @ FunctionProgC(_, _, _, _, _) => programToHtml(funProg) /**No pre and post for function program*/
			case _ =>
				val (pre, post) = (annProg.pre, annProg.post)
				<collapse>ooo</collapse>
				<div class = "ProgramAnn unfolded">
					<div class = "id">{mkContent(annProg.id)}</div>
					<div class = "displayId">{mkContent(annProg.displayId)}</div>
					<div class = "pre">{invariantToHtml(pre)}</div>
					<div class = "program">{programToHtml(annProg)}</div>
					<div class="post">{invariantToHtml(post)}</div>
				</div>
		}
	}
}

trait ProgramPrinter extends GeneralPrinters { self: ProgramAnnPrinter =>
	def programToHtml(aProgramAnn: ProgramAnn): Seq[Node] = {
		val pre = aProgramAnn.pre
		val post = aProgramAnn.post
		aProgramAnn match {
		case IfProgC(grdcmds) =>
			<div class="IfProg">
				{mkDecoDiv("IF")}
				<div class="grdcmds">{for(grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd)}</div>
			</div>
		case WhileProgC(loopInvOpt, grdcmds) =>
			<div class="WhileProg">
				{mkDecoDiv("WHILE ")}
				{	if (loopInvOpt.isDefined) {
						<div class="loopInv">{termToHtml(loopInvOpt.get)}</div>
					}
				}
				<div class="grdcmds">{for(grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd)}</div>
			</div>
		case CompositionC(annPrograms) =>
			<div class="Composition">
				<div class="programs">{for(annProg <- annPrograms) yield programAnnToHtml(annProg)}</div>
			</div>
		case valDefProg @ ValDefProgC(lhs, Some(rhs)) =>
			<div class="ValDefProg">
				{mkDecoDiv("""val""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{programAnnToHtml(rhs)}</div>
			</div>
		case valDefProg @ ValDefProgC(lhs, None) =>
			<div class="ValDefProg">
				{mkDecoDiv("""val""")}
				<div class="lhs">{termToHtml(lhs)}</div>
			</div>
		case varDefProg @ VarDefProgC(lhs, Some(rhs)) =>
			<div class="VarDefProg">
				{mkDecoDiv("""var""")}
				<div class="lhs">{termToHtml(lhs)}</div>
				{mkAsgnSymDiv}
				<div class="rhs">{programAnnToHtml(rhs)}</div>
			</div>
		case varDefProg @ VarDefProgC(lhs, None) =>
			<div class="VarDefProg">
				{mkDecoDiv("""var""")}
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
		case ExprProgC(expr) =>
			<div class="ExprProg">
				<div class="expr">{termToHtml(expr)}</div>
			</div>
		case IdentifierC(name, itype) =>
			<div class="Identifier">
				<div class="name">{mkContent(name)}</div>
				{mkColonDiv}
				<div class="itype">{mkContent(itype)}</div>
			</div>
		case LitConstantC(name) =>
			<div class="LitConstant">
				<div class="name">{mkContent(name)}</div>
			</div>
		case FunctionProgC(name, params, retVar, annProg, globalInvs) =>
			<div class="FunctionProg">
				{mkDecoDiv("def " + name + "(" + params.map(av => av.v + ": " + av.getType).mkString(", ") +  "): " + retVar.v + ":" + retVar.getType)}
				<div class="annProg">{programAnnToHtml(annProg)}</div>
			</div>
	}
	}

	def asgnToTable(asgns: List[(Var, ProgramAnn)], asgnSym: String = ":="): Elem = {
		<table class='tablestyle'>
		{ asgns.zipWithIndex map {
				case ((lhs, ExprProgC(ArrStore(arrTerm, indexTerm, valTerm))), index) =>
					<tr>
						<td class="lhsvar">
							{termToHtml(lhs)}
							<div class="deco"> (</div>
							{mkSpaceDiv ++ mkLParenDiv}
							<div class="index">{termToHtml(indexTerm)}</div>
							{mkSpaceDiv ++ mkRParenDiv}
						</td>
						<td rowspan="2"> {asgnSym} </td>
						<td class="rhsprog">{termToHtml(valTerm)}</td>
					</tr>
				case ((lhs, rhs), index) =>
					<tr>
						<td class="lhsvar">{termToHtml(lhs)}</td>
						{ 	if (index == 0 ) <td> {asgnSym} </td> %  Attribute(None, "rowspan", Text(asgns.length.toString), xml.Null)
							else " "
						}
						<td class="rhsprog">{programAnnToHtml(rhs)}</td>
					</tr>
			}
		}
		</table>
	}

	def asgnToTable2(vars: List[Var], terms: List[Term], asgnSym: String = ":=") : Elem = {
		asgnToTable(vars zip (terms map {ExprProg(_)}), asgnSym)
	}

	def grdcmdToHtml(grdcmd: GuardedCmd): Seq[Node] = {
		val GuardedCmd(guard, cmd) = grdcmd
		<div class="GuardedCmd">
			<div class="guard">{termToHtml(guard)}</div>
			{mkDecoDiv(scala.xml.Unparsed("""&#10230"""))}
			<div class="cmd">{programAnnToHtml(cmd)}</div>
		</div>
	}
}

trait BooleanSymbolsPrinter extends MQTrait with GeneralPrinters{
	def mkNotDiv() = if (isMQ) mkDecoDiv("""\neg""") else mkDecoDiv(scala.xml.Unparsed("""&not;"""))
	def mkAndDiv() = if (isMQ) mkDecoDiv("""\and""") else mkDecoDiv("""/\""")
	def mkOrDiv() = if (isMQ) mkDecoDiv("""\or""") else mkDecoDiv(scala.xml.Unparsed("""\/"""))
	def mkImplDiv() = if (isMQ) mkDecoDiv("""\implies""") else mkDecoDiv(scala.xml.Unparsed("""&rarr;"""))
	def mkEquivDiv() = if (isMQ) mkDecoDiv("""\equiv""") else mkDecoDiv(scala.xml.Unparsed("""&equiv;"""))
	def mkForallDiv() = if (isMQ) mkDecoDiv("""\forall""") else mkDecoDiv(scala.xml.Unparsed("""&forall;"""))
	def mkExistsDiv() = if (isMQ) mkDecoDiv("""\exists""") else mkDecoDiv(scala.xml.Unparsed("""&exist;"""))
}

trait FormulaPrinter extends GeneralPrinters with BooleanSymbolsPrinter {
	def termsToHtml(xs: List[Term]):List[Any] = {
		val retVal: ListBuffer[Any] = new ListBuffer[Any]
		retVal += termToHtml(xs.head)
		for ( x <- xs.tail) {
			retVal += mkDecoDiv(",")
			retVal += termToHtml(x)
		}
		retVal.toList
	}

	def parenFormulaDiv(f: FOLFormula, className: String):Seq[Node] = {
		val isComposite = f match {
			case True1() | False1() | Atom(_) |	TrueF | FalseF | Not(_) => false
			case _ => true
		}
		if (isComposite){
			{mkDecoDiv("""(""")} ++
			<div>{formulaToHtml(f)}</div> % Attribute(None, "class", Text(className), Null) ++
			{mkDecoDiv(""")""")}
		}
		else{
			<div>{formulaToHtml(f)}</div> % Attribute(None, "class", Text(className), Null)
		}
	}

	def formulaToHtml(formula: FOLFormula):Seq[Node] = formula match {
		case True1() =>
			<div class="True1" displayIdAttr={formula.displayId.toString}>{mkContent("True")}</div>
		case False1() =>
			<div class="False1"  displayIdAttr={formula.displayId.toString}>{mkContent("False")}</div>
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
				{mkLParenDiv}
				<div class="f">{formulaToHtml(f)}</div>
				{mkRParenDiv}
			</div>
		case AndN(fs) if (fs.length > 1 ) =>
		    val x = fs flatMap {fi => mkAndDiv ++ parenFormulaDiv(fi, "fi")}
		    <div class="AndN" displayIdAttr={formula.displayId.toString}>
				{x.tail}
			</div>
		case And(f1, f2) => /** Unreachable case */
			<div class="And" displayIdAttr={formula.displayId.toString}>
				{parenFormulaDiv(f1, "f1")}
				{//mkDecoDiv(scala.xml.Unparsed("""&and;"""))
					mkAndDiv
				}
				{parenFormulaDiv(f2, "f2")}
				</div>
		case OrN(fs) if (fs.length > 1 ) =>
		    val x = fs flatMap {fi => mkOrDiv ++ parenFormulaDiv(fi, "fi")}
		    <div class="OrN">
				{x.tail}
			</div>

		case Or(f1, f2) => /** Unreachable case */
			<div class="Or" displayIdAttr={formula.displayId.toString}>
				{parenFormulaDiv(f1, "f1")}
				{//mkDecoDiv(scala.xml.Unparsed("""&or;"""))
					mkOrDiv
				}
				{parenFormulaDiv(f2, "f2")}
				</div>
		case Impl(f1, f2) =>
			<div class="Impl" displayIdAttr={formula.displayId.toString}>
				{parenFormulaDiv(f1, "f1")}
				{mkImplDiv}
				{parenFormulaDiv(f2, "f2")}
			</div>
		case Iff(f1, f2) =>
			<div class="Iff" displayIdAttr={formula.displayId.toString}>
				{parenFormulaDiv(f1, "f1")}
				{mkEquivDiv}
				{parenFormulaDiv(f2, "f2")}
			</div>
		case Forall(v, f) =>
			<div class="Forall" displayIdAttr={formula.displayId.toString}>
				{mkForallDiv}
				<div class="v">{mkContent(v.v)}</div>
				{mkColonDiv}
				<div class="f">{formulaToHtml(f)}</div>
			</div>
		case Exists(v, f) =>
			<div class="Exists" displayIdAttr={formula.displayId.toString}>
				{mkExistsDiv}
				<div class="v">{mkContent(v.v)}</div>
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
				<div class="r">{mkContent(r)}</div>
				{mkLParenDiv}
				<div class="ts">{termsToHtml(ts)}</div>
				{mkRParenDiv}
			</div>
		//case _ => <div class="NotImpl"></div>
	}

	def mkInterpretedFnContent(afn: Fn) =
		mkContent(if(isMQ) InterpretedFnsTmp.getMQFnSym(afn) else InterpretedFnsTmp.getHtmlFnSym(afn))

	def mkInfixFnContent(afn: Fn) =
		mkContent(if(isMQ) InfixFns.getMQFnSym(afn) else InfixFns.getHtmlFnSym(afn))

	def fnToHtml(afn: Fn): Seq[Node] = afn match {
		case Fn(name, argTpes, tpe) =>
			<div class="fn">
				<div class="name">{mkInterpretedFnContent(afn)}</div>
			</div>
	}

	def fnAppToHtml(fnapp: FnApp): Seq[Node] = fnapp match {
		case FnApp(fn, ts) if InfixFns.isInfix(fn)=>
			<div class="FnApp" displayIdAttr={fnapp.displayId.toString}>
				{ mkLParenDiv }
				<div class="t1">{termToHtml(ts(0))}</div>
				<div class="opr">{mkInfixFnContent(fn)}</div>
				<div class="t2">{termToHtml(ts(1))}</div>
				{ mkRParenDiv }
			</div>
		case FnApp(fn, ts) =>
			<div class="FnApp" displayIdAttr={fnapp.displayId.toString}>
				<div class="afn">{fnToHtml(fn)}</div>
				{mkLParenDiv}
				<div class="ts">{termsToHtml(ts)}</div>
				{mkRParenDiv}
			</div>
	}

	def termToHtml(t: Term):Seq[Node] = t match {
		case aVar @ Var(v) =>
			<div class="Var" displayIdAttr={t.displayId.toString}>
				<div class="v">{mkContent(v)}</div>
				{mkColonDiv}
				<div class="t">{mkContent(aVar.getType)}</div>
			</div>
		case x @ FnApp(_, _) => {fnAppToHtml(x)}
		case aConst @ Const(name) =>
			<div class="Const" displayIdAttr={t.displayId.toString}>
				<div class="name">{mkContent(name)}</div>
				{mkColonDiv}
				<div class="tpe">{mkContent(aConst.getType)}</div>
			</div>
		case ArrSelect(arr, index)  =>
			<div class="ArrSelect" displayIdAttr={t.displayId.toString}>
				<div class="arr">{termToHtml(arr)}</div>
				{mkLParenDiv}
				<div class="index">{termToHtml(index)}</div>
				{mkRParenDiv}
			</div>
		/*case ArrStore(arr, index, value) =>
			<div class="ArrStore">
				<div class="arr">{termToHtml(arr)}</div>
				<div class="deco">.Store(</div>
				<div class="index">{termToHtml(index)}</div>
				<div class="deco">,(</div>
				<div class="value">{termToHtml(value)}</div>
				{mkRParenDiv}
				{mkRParenDiv}
			</div>*/
		case ArrStore(arr, index, value) =>
			<div class="ArrStore" displayIdAttr={t.displayId.toString}>
				<div class="arr">{termToHtml(arr)}</div>
				<div class="deco">.Store</div>
				{mkLParenDiv}
				<div class="index">{termToHtml(index)}</div>
				{mkDecoDiv(""",""")}
				{mkLParenDiv}
				<div class="value">{termToHtml(value)}</div>
				{mkRParenDiv}
				{mkRParenDiv}
			</div>
	}

	def invariantToHtml(inv: InvariantT): Seq[Node] = {
		val InvariantT(locOpt, term, rvVarOpt) = inv
		<div class="Invariant" displayIdAttr={inv.displayId.toString}>
			<div class="loc">{mkContent(locOpt)}</div>
			<div class="term">{termToHtml(term)}</div>
		</div>
		//			<div class="rvVar">{rvVarOpt map (_.v) getOrElse ""}</div>
	}
}


trait ProgramAnnPOPrinter extends FormulaPrinter{
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
			case FunctionProgC(_, _, _, pa, _) => toHtmlPO(pa)
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
		case ValDefProgC(_, None) => Nil
		case VarDefProgC(_, None) => Nil
		case _: Identifier => Nil
		case _: LitConstant => Nil
		case _: ExprProg => Nil
	}

	def toHtmlPO(grdcmd: GuardedCmd): Seq[Node] = {
		val GuardedCmd(guard, cmd) = grdcmd
		toHtmlPO(cmd)
	}
}

trait SynthInfoPrinter extends ProgramAnnPrinter{
	def synthInfoToHtml(synthInfo: LinkedHashMap[UnknownProg, List[PSTacticResult]]): Seq[Node] = {
		if (!synthInfo.isEmpty){
			val elem = <div class="synth_info">
						{mkDecoDiv("Synthesis Info:")}
						{for( (unkProg, pas) <- synthInfo) yield synthPairToHtml(unkProg, pas)}
						</div>
			List(elem)
		} else
			Nil
	}

	def synthPairToHtml(unkProg: UnknownProg, pstrList: List[PSTacticResult]): Seq[Node] = {
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

trait RetValTacticResultPrinter extends ProgramAnnPrinter{ self: RetValResult =>
	override val isMQ = false
	def toHtml(): Seq[Node] = {
		val x = (self.resultProg map programAnnToHtml)
		val y = x.toSeq.flatten
		<div class="tactic_result">
			<div class="tactic_name">RetValTactic</div>
			<div class="synth_pas">
			{(self.resultProg map programAnnToHtml)
				.toSeq.flatten}
			</div>
		</div>
	}
}

//	var initUnk: Option[ProgramAnn] = None
//	var whileProg: Option[ProgramAnn] = None
//	var whileBodyUnk: Option[ProgramAnn]= None
//	var resultProg: Option[ProgramAnn]= None
//	var whileGuard: Option[ProgramAnn]= None
//	var boundFs: List[FOLFormula]= Nil
/*
trait RCVTacticResultPrinter extends ProgramAnnPrinter { self: RCVResult =>
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

object InfixFns {
	val fns = List("$plus", "$times", "$percent", "$minus", "$div",
			"""$amp$amp""", """$pipe$pipe""", """impl""", """$eq$eq""")

	def isInfix(afn: Fn) = fns.contains(afn.name)

	def getsym(afn: Fn): String = afn.name match {
		case "$plus" => "+"
		case "$times" => "*"
		case "$percent" => "%"
		case "$minus" => "-"
		case "$div" => "/"
		case """$amp$amp""" => """&&"""
		case """$pipe$pipe""" => """||"""
		case """$bang""" => """!"""
		case """impl""" => """impl"""
		case """$eq$eq""" => """eqeq"""

	}

	def getHtmlSym(s: String): scala.xml.Unparsed = s match {
		case "+" => scala.xml.Unparsed("""&#43;""")
		case "*" => scala.xml.Unparsed("""&#42;""")
		case "%" => scala.xml.Unparsed("""&#37;""")
		case "-" => scala.xml.Unparsed("""&minus;""")
		case "/" => scala.xml.Unparsed("""&#47;""")

		case "$amp$amp" => scala.xml.Unparsed("""&38;&38;""")
		case "$pipe$pipe" => scala.xml.Unparsed("""&#124;&#124;""")
		case "$bang" => scala.xml.Unparsed("""&#33;""")
		case "impl" => scala.xml.Unparsed("""impl""")
		case "$eq$eq" => scala.xml.Unparsed("""eqeq""")

		case _ => scala.xml.Unparsed(s)
	}

	def getMQSym(s: String) = s match {
		case "+" => """+"""
		case "*" => """*"""
		case "%" => """%"""
		case "-" => """-"""
		case "/" => """/"""

		case "$amp$amp" => """&&"""
		case "$pipe$pipe" => """||"""
		case "$bang" => """!"""
		case "impl" => """impl"""
		case "$eq$eq" => """eqeq"""

		case _ => scala.xml.Unparsed(s)
	}

	def getHtmlFnSym(afn: Fn): scala.xml.Unparsed = getHtmlSym(getsym(afn))
	def getMQFnSym(afn: Fn) = getMQSym(getsym(afn))
}

object InterpretedFnsTmp {
	def getsym(afn: Fn): String = afn.name match {
		case "$plus" => "+"
		case "$times" => "*"
		case "$percent" => "%"
		case "$minus" => "-"
		case "$div" => "/"
		case """$amp$amp""" => """&&"""
		case """$pipe$pipe""" => """||"""
		case """$bang""" => """!"""
		case """impl""" => """impl"""
		case """$eq$eq""" => """eqeq"""
		case _ => afn.name

	}

	def getHtmlSym(s: String): scala.xml.Unparsed = s match {
		case "+" => scala.xml.Unparsed("""&#43;""")
		case "*" => scala.xml.Unparsed("""&#42;""")
		case "%" => scala.xml.Unparsed("""&#37;""")
		case "-" => scala.xml.Unparsed("""&minus;""")
		case "/" => scala.xml.Unparsed("""&#47;""")

		case "$amp$amp" => scala.xml.Unparsed("""&38;&38;""")
		case "$pipe$pipe" => scala.xml.Unparsed("""&#124;&#124;""")
		case "$bang" => scala.xml.Unparsed("""&#33;""")
		case "impl" => scala.xml.Unparsed("""impl""")
		case "$eq$eq" => scala.xml.Unparsed("""eqeq""")

		case _ => scala.xml.Unparsed(s)
	}

	def getMQSym(s: String) = s match {
		case "+" => """+"""
		case "*" => """*"""
		case "%" => """%"""
		case "-" => """-"""
		case "/" => """/"""

		case "$amp$amp" => """&&"""
		case "$pipe$pipe" => """||"""
		case "$bang" => """!"""
		case "impl" => """impl"""
		case "$eq$eq" => """eqeq"""

		case _ => scala.xml.Unparsed(s)
	}

	def getHtmlFnSym(afn: Fn): scala.xml.Unparsed = getHtmlSym(getsym(afn))
	def getMQFnSym(afn: Fn)= getMQSym(getsym(afn))
}