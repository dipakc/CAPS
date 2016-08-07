package progsynth.printers
import scala.tools.nsc.plugins.PluginComponent
import scala.xml._
import progsynth.PSPredef._
import progsynth.types._
import progsynth.types.Types._
import scala.tools.nsc.Global
import scala.collection.mutable.ListBuffer
import progsynth.proofobligations.Z3Result
import progsynth.config.AppConfig
import progsynth.types.ProgramAnn
import progsynth.proofobligations.VarIp
/*
object AnnCodePrinterWrapper {
	object AnnCodePrinter extends ProgramAnnPrinter with ProgramAnnPOPrinter {
		def programAnnToAnnCode(annProg: ProgramAnn): Elem =
			<div class="srcfile">
				{ programAnnToHtml(annProg) }
				{ toHtmlPO(annProg) }
			</div>
	}

	object InfixFns {
		val fns = List("$plus", "$times", "$percent", "$minus", "$div")
		def isInfix(afn: Fn) = fns.contains(afn.name)
		def getsym(afn: Fn): String = afn.name match {
			case "$plus" => "+"
			case "$times" => "*"
			case "$percent" => "%"
			case "$minus" => "-"
			case "$div" => "/"
		}

		def getHtmlFnSym(s: String) = s match {
			case "+" => scala.xml.Unparsed("""&#43;""")
			case "*" => scala.xml.Unparsed("""&#42;""")
			case "%" => scala.xml.Unparsed("""&#37;""")
			case "-" => scala.xml.Unparsed("""&minus;""")
			case "/" => scala.xml.Unparsed("""&#47;""")
			case _ => scala.xml.Unparsed(s)
		}
		def getHtmlFnSym(afn: Fn) = getsym(afn) match {
			case "+" => scala.xml.Unparsed("""&#43;""")
			case "*" => scala.xml.Unparsed("""&#42;""")
			case "%" => scala.xml.Unparsed("""&#37;""")
			case "-" => scala.xml.Unparsed("""&minus;""")
			case "/" => scala.xml.Unparsed("""&#47;""")
			case x @ _ => scala.xml.Unparsed(x)
		}

	}

	trait ProgramAnnPrinter extends GeneralPrinters with FormulaPrinter with ProgramPrinter {
		def programAnnToHtml(annProg: ProgramAnn): Seq[Node] = {
			val (pre, post) = (annProg.pre, annProg.post)
			annProg match {
				case ExprProg(_) => programToHtml(annProg)
				case _ =>
					<collapse>ooo</collapse>
					<div class="ProgramAnn">
						<div class="id">{ anyToHtml(annProg.id) }</div>
						<div class="pre">{ invariantToHtml(pre) }</div>
						{ programToHtml(annProg) }
						<div class="post">{ invariantToHtml(post) }</div>
					</div>

			}
		}
	}

	trait ProgramPrinter extends GeneralPrinters { self: ProgramAnnPrinter =>
		def programToHtml(aProgramAnn: ProgramAnn): Seq[Node] = {
			val pre = aProgramAnn.pre
			val post = aProgramAnn.post
			aProgramAnn match {
				case IfProg(grdcmds) =>
					<div class="IfProg program">
						{ mkDecoDiv("IF") }
						<div class="grdcmds">{ for (grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd) }</div>
					</div>
				case WhileProg(loopInvOpt, grdcmds) =>
					<div class="WhileProg program">
						{ mkDecoDiv("WHILE ") }
						{
							if (loopInvOpt.isDefined) {
								<div class="loopInv">{ termToHtml(loopInvOpt.get) }</div>
							}
						}
						<div class="grdcmds">{ for (grdcmd <- grdcmds) yield grdcmdToHtml(grdcmd) }</div>
					</div>
				case Composition(annPrograms) =>
					<div class="Composition program">
						<div class="programs">
							{ programToHtml(annPrograms.head) }
							{
								for (annProg <- annPrograms.tail) yield { <div class="pre">{ invariantToHtml(annProg.pre) }</div> } ++
									{ programToHtml(annProg) }
							}
							<div class="post">{ invariantToHtml(annPrograms.last.post) }</div>
						</div>
					</div>
				case ValDefProg(lhs, rhs) =>
					<div class="ValDefProg program">
						<div class="lhs">{ termToHtml(lhs) }</div>
						<div class="deco"> = </div>
						<div class="rhs">{ programAnnToHtml(rhs) }</div>
					</div>
				case VarDefProg(lhs, rhs) =>
					<div class="VarDefProg program">
						<div class="lhs">{ termToHtml(lhs) }</div>
						<div class="deco"> = </div>
						<div class="rhs">{ programAnnToHtml(rhs) }</div>
					</div>
				case Assignment((lhs, rhs) :: Nil) =>
					<div class="Assignment program">
						<div class="lhs">{ termToHtml(lhs) }</div>
						<div class="deco"> = </div>
						<div class="rhs">{ programAnnToHtml(rhs) }</div>
					</div>
				case Assignment(asgns) =>
					<div class="MultiAssignment program">
						<div class="lhs">{
							asgns map {case (lhs, _) => <lhsvar>termToHtml(lhs)</lhsvar>}
						}
						</div>
						<div class="deco"> = </div>
						<div class="rhs">{
							asgns map {case (_, rhs) => <rhsprog> programAnnToHtml(rhs)</rhsprog>}
						}
						</div>
					</div>
				case SkipProg() =>
					<div class="SkipProg program">SkipProg</div>
				case UnknownProg(id) =>
					<div class="UnknownProg program">UnknownProg({ id })</div>
				case ExprProg(expr) =>
					<div class="ExprProg program">
						<div class="expr">{ termToHtml(expr) }</div>
					</div>
				case Identifier(name, itype) =>
					<div class="Identifier program">
						<div class="name">{ anyToHtml(name) }</div>
						<div class="deco">:</div>
						<div class="itype">{ anyToHtml(itype) }</div>
					</div>
				case LitConstant(name) =>
					<div class="LitConstant program">
						<div class="name">{ anyToHtml(name) }</div>
					</div>
				case FunctionProg(name, params, retVar, annProg, globalInvs) =>
					<div class="FunctionProg program">
						<div class="name">{ anyToHtml(name) }</div>
						<div class="params">{ params.toString }</div>
						<div class="retVar">{ retVar.toString }</div>
						<div class="annProg">{ programAnnToHtml(annProg) }</div>
						<div class="globalInvs">globalInvs</div>
					</div>
			}
		}

		def grdcmdToHtml(grdcmd: GuardedCmd): Seq[Node] = {
			val GuardedCmd(guard, cmd) = grdcmd
			<div class="GuardedCmd">
				<div class="guard">{ termToHtml(guard) }</div>
				{ mkDecoDiv(scala.xml.Unparsed("""&#10230""")) }
				<div class="cmd">{ programAnnToHtml(cmd) }</div>
			</div>
		}
	}

	trait FormulaPrinter extends GeneralPrinters {
		def termsToHtml(xs: List[Term]): List[Any] = {
			val retVal: ListBuffer[Any] = new ListBuffer[Any]
			retVal += termToHtml(xs.head)
			for (x <- xs.tail) {
				retVal += mkDecoDiv(",")
				retVal += termToHtml(x)
			}
			retVal.toList
		}

		def parenFormulaDiv(f: FOLFormula, className: String): Seq[Node] = {
			val isComposite = f match {
				case True1() | False1() | Atom(_) | TrueF | FalseF | Not(_) => false
				case _ => true
			}
			if (isComposite) {
				{ mkDecoDiv("""(""") } ++
					<div>{ formulaToHtml(f) }</div> % Attribute(None, "class", Text(className), Null) ++
					{ mkDecoDiv(""")""") }
			} else {
				<div>{ formulaToHtml(f) }</div> % Attribute(None, "class", Text(className), Null)
			}
		}

		def formulaToHtml(f: FOLFormula): Seq[Node] = f match {
			case True1() =>
				<div class="True1">True</div>
			case False1() =>
				<div class="False1">False</div>
			case Atom(a) =>
				<div class="Atom">
					<div class="a">{ predToHtml(a) }</div>
				</div>
			case Not(f) =>
				<div class="Not">
					{ mkDecoDiv(scala.xml.Unparsed("""&not;(""")) }
					<div class="f">{ formulaToHtml(f) }</div>
					{ mkDecoDiv(""")""") }
				</div>
			case And(f1, f2) =>
				<div class="And">
					{ parenFormulaDiv(f1, "f1") }
					{ //mkDecoDiv(scala.xml.Unparsed("""&and;"""))
						mkDecoDiv("""/\""")
					}
					{ parenFormulaDiv(f2, "f2") }
				</div>
			case Or(f1, f2) =>
				<div class="Or">
					{ parenFormulaDiv(f1, "f1") }
					{ //mkDecoDiv(scala.xml.Unparsed("""&or;"""))
						mkDecoDiv("""\/""")
					}
					{ parenFormulaDiv(f2, "f2") }
				</div>
			case Impl(f1, f2) =>
				<div class="Impl">
					{ parenFormulaDiv(f1, "f1") }
					{ mkDecoDiv(scala.xml.Unparsed("""&rarr;""")) }
					{ parenFormulaDiv(f2, "f2") }
				</div>
			case Iff(f1, f2) =>
				<div class="Iff">
					{ parenFormulaDiv(f1, "f1") }
					{ mkDecoDiv(scala.xml.Unparsed("""&equiv;""")) }
					{ parenFormulaDiv(f2, "f2") }
				</div>
			case Forall(v, f) =>
				<div class="Forall">
					<div class="v">{ anyToHtml(v) }</div>
					<div class="f">{ formulaToHtml(f) }</div>
				</div>
			case Exists(v, f) =>
				<div class="Exists">
					<div class="v">{ anyToHtml(v) }</div>
					<div class="f">{ formulaToHtml(f) }</div>
				</div>
			case Unknown() => <div class="Unknown">Unknown</div>
		}

		def getHtmlSym(s: String) = s match {
			case "<=" => scala.xml.Unparsed("""&le;""")
			case ">=" => scala.xml.Unparsed("""&ge;""")
			case "==" => scala.xml.Unparsed("""=""")
			case _ => scala.xml.Unparsed(s)
		}

		def predToHtml(p: Pred): Seq[Node] = p match {
			case Pred(r, ts) if ComparisonPreds.preds contains r =>
				<div class="Pred">
					<div class="ts">{ termToHtml(ts(0)) }</div>
					<div class="r">{ getHtmlSym(ComparisonPreds.getsym(r)) }</div>
					<div class="ts">{ termToHtml(ts(1)) }</div>
				</div>
			case Pred(r, ts) =>
				<div class="Pred">
					<div class="r">{ anyToHtml(r) }</div>
					{ mkDecoDiv("(") }
					<div class="ts">{ termsToHtml(ts) }</div>
					{ mkDecoDiv(")") }
				</div>
			//case _ => <div class="NotImpl"></div>
		}

		def fnToHtml(afn: Fn): Seq[Node] = afn match {
			case Fn(name, argTpes, tpe) =>
				<div class="fn">
					<div class="name">{ name }</div>
					<div class="argTpes">{ argTpes }</div>
					<div class="tpe">{ tpe }</div>
				</div>
		}

		def fnAppToHtml(fnapp: FnApp): Seq[Node] = fnapp match {
			case FnApp(fn, ts) if InfixFns.isInfix(fn) =>
				<div class="FnApp">
					{ mkDecoDiv("(") }
					<div class="t1">{ termToHtml(ts(0)) }</div>
					<div class="opr">{ InfixFns.getHtmlFnSym(fn) }</div>
					<div class="t2">{ termToHtml(ts(1)) }</div>
					{ mkDecoDiv(")") }
				</div>
			case FnApp(fn, ts) =>
				<div class="FnApp">
					<div class="afn">{ fnToHtml(fn) }</div>
					{ mkDecoDiv("(") }
					<div class="ts">{ termsToHtml(ts) }</div>
					{ mkDecoDiv(")") }
				</div>
		}

		def termToHtml(t: Term): Seq[Node] = t match {
			case aVar @ Var(v) =>
				<div class="Var">
					<div class="v">{ anyToHtml(v) }</div>
					<div class="deco">:</div>
					<div class="t">{ anyToHtml(aVar.getType) }</div>
				</div>
			case x @ FnApp(_, _) => { fnAppToHtml(x) }
			case aConst @ Const(name) =>
				<div class="Const">
					<div class="name">{ anyToHtml(name) }</div>
					<div class="deco">:</div>
					<div class="tpe">{ anyToHtml(aConst.getType) }</div>
				</div>
			case ArrSelect(arr, index) =>
				<div class="ArrSelect">
					<div class="arr">{ termToHtml(arr) }</div>
					<div class="deco">(</div>
					<div class="index">{ termToHtml(index) }</div>
					<div class="deco">)</div>
				</div>
			case ArrStore(arr, index, value) =>
				<div class="ArrStore">
					<div class="arr">{ termToHtml(arr) }</div>
					<div class="deco">.Store(</div>
					<div class="index">{ termToHtml(index) }</div>
					<div class="deco">,(</div>
					<div class="value">{ termToHtml(value) }</div>
					<div class="deco">)</div>
				</div>
			case QTerm(opr, dummies, range, term) =>
				<div class="QTerm">
					<div>Not Implemented </div>
				</div>

		}

		def invariantToHtml(inv: InvariantT): Seq[Node] = {
			val InvariantT(locOpt, term, rvVarOpt) = inv
			<div class="Invariant">
				<div class="loc">{ anyToHtml(locOpt) }</div>
				<div class="term">{ termToHtml(term) }</div>
				<div class="rvVar">{ rvVarOpt map (_.v) getOrElse "" }</div>
			</div>
		}
	}

	trait GeneralPrinters {
		def anyToHtml(a: Any): String = a.toString()
		def mkDecoDiv(decoStr: String) = <div class="deco">{ decoStr }</div>
		def mkDecoDiv(decoStr: scala.xml.Unparsed) = <div class="deco">{ decoStr }</div>
	}

	trait ProgramAnnPOPrinter extends FormulaPrinter {
		def statusToHtml(statusOpt: Option[Z3Result]): Seq[Node] = {
			if (statusOpt.isDefined) {
				val status = statusOpt.get
				<collapse>ooo</collapse>
				<div class="status">
					{ if (status.error.isDefined) <collapse>ooo</collapse> ++ <div class="error">{ status.error.get }</div> }
					{ if (status.isValid) <div class="valid">valid</div> else <div class="notvalid">not_valid</div> }
					{
						if (!status.modelVarIps.isEmpty)
							<collapse>ooo</collapse>
							<div class="model">
								{
									(for (VarIp(aVar, value) <- status.modelVarIps) yield {
										<div class="varitem">
											<div class="variable">{ aVar.v + ":" + aVar.getType + " =" }</div>
											<div class="value">{ value }</div>
										</div>
									})(collection.breakOut)
								}
							</div>
					}
				</div>
			} else {
				<div class="status failed">Failed</div>
			}
		}

		def toHtmlPO(annProg: ProgramAnn): Seq[Node] = {
			val poIdDiv = <div class="pa_id">{ annProg.id }</div>
			val poDiv = for (pobg <- annProg.proofObligs) yield {
				<div class="proofoblig">
					{ termToHtml(pobg.term) }
					{ statusToHtml(pobg.status) }
				</div>
			}
			val subPoDiv = <div class="prg_proofobligs">{ toHtmlPOContent(annProg) }</div>;

			<collapse>ooo</collapse> ++
				<div class="pa_proofobligs">
					{ poIdDiv ++ poDiv ++ subPoDiv }
				</div>
		}

		def toHtmlPOContent(prog: ProgramAnn): Seq[Node] = prog match {
			case FunctionProg(name, _, _, annProg, _) => toHtmlPO(annProg)
			case IfProg(grdcmds) => grdcmds flatMap toHtmlPO _
			case WhileProg(_, grdcmds) => grdcmds flatMap toHtmlPO _
			case Composition(programs) => programs flatMap toHtmlPO _
			case ValDefProg(lhs, rhs) => toHtmlPO(rhs)
			case VarDefProg(lhs, rhs) => toHtmlPO(rhs)
			case Assignment((lhs, rhs) :: Nil) => toHtmlPO(rhs)
			case Assignment(_) => throw new RuntimeException("toHtmlPOContent of simultaneous assignments not implemented")
			case _: SkipProg => Nil
			case _: UnknownProg => Nil
			case _: Identifier => Nil
			case _: LitConstant => Nil
			case _: ExprProg => Nil
		}

		def toHtmlPO(grdcmd: GuardedCmd): Seq[Node] = {
			val GuardedCmd(guard, cmd) = grdcmd
			toHtmlPO(cmd)
		}
	}
}
*/