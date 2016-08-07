package progsynth.printers
import progsynth.types._
import progsynth.types.Types._


object ProgramPPrinter {
		//Invariant
	def prettyPrint(inv: InvariantT): String = {
		val locStr = if(inv.loc != None) inv.loc.get.toString + ": " else ""
		val rvStr =  if(inv.rvVar != None) " : " + inv.rvVar.get.toString else ""
		locStr +  inv.term.toString + rvStr
	}

	//ProgramAnn
	def prettyPrint(annProg: ProgramAnn): String = {
		prettyPrint(annProg, "")
	}
	//ProgramAnn Indent
	def prettyPrint(annProg: ProgramAnn, indent: String, printPre: Boolean = true, printPost: Boolean = true): String = {
		(if (printPre) indent + "{{" + annProg.pre + "}}" + "\n" else "" ) +
		prettyPrintContent(annProg, indent) + "\n" +
		(if (printPost) indent + "{{" + annProg.post + "}}" + "\n"  else "" )
	}

	//GuardedCmd Indent
	def prettyPrint(grdcmd: GuardedCmd, indent: String): String = {
		indent + "(" + grdcmd.guard + ") ===> \n" +
				prettyPrint(grdcmd.cmd, indent + "\t")
	}

	//Program Indent
	def prettyPrintContent(prog: ProgramAnn, indent: String): String = {
		prog match {
			case IdentifierC(name, itype) =>
				indent + name
			case LitConstantC(name) =>
				indent + name
			case FunctionProgC(name, _, _, annProg, _) =>
				"Function Call: Not Implemented"
			case IfProgC(grdcmds) =>
				indent + "IF\n" +
				((for (grdcmd <- grdcmds) yield prettyPrint(grdcmd, indent + "\t")) mkString "" )
			case WhileProgC(loopInv, grdcmds) =>
				indent + "LoopInv:" + loopInv + "\n"
				indent + "DO\n" +
				((for (grdcmd <- grdcmds) yield prettyPrint(grdcmd, indent + "\t")) mkString "" )
			case CompositionC(programs) =>
				//indent + "{" + programs.head.pre + "}" + "\n" +
				prettyPrint(programs.head, indent, false, false) + //first element, don't print pre and post
				((for (annProg <- programs.tail) yield prettyPrint(annProg, indent, true, false)) mkString "")
				//"\n"
			case x @ ExprProgC(aTerm) =>
				indent + prettyPrint(aTerm)
			case ValDefProgC(lhs, rhsOpt) =>
				rhsOpt match {
					case Some(rhs) =>
						indent + "val " + lhs + "=\n" + prettyPrint(rhs, indent + "\t")
					case None =>
						indent + "val " + lhs + "=\n"
				}
				
			case VarDefProgC(lhs, rhsOpt) =>
				rhsOpt match {
					case Some(rhs) =>
						indent + "var " + lhs + "=\n" + prettyPrint(rhs, indent + "\t")
					case None =>
						indent + "var " + lhs + "=\n"
				}
				
			case AssignmentC((lhs, rhs)::Nil) =>
				indent + lhs + "=\n" + prettyPrint(rhs, indent + "\t")
			case AssignmentC(_) => throw new RuntimeException("pprint of simultaneous assignments not implemented")
			case SkipProgC() =>
				indent + "Skip"
			case UnknownProgC(id) => //TODO: print id
				indent + "Unknown(" + id + ")"
			case AssumeProgC(pred) =>
				indent + "Assume(" + prettyPrint(pred) + ")"
		}
	}

	//Program
	def prettyPrintContent(prog: ProgramAnn): String = {
		prettyPrintContent(prog, "")
	}

	//Option[ProgramAnn]
	def prettyPrint(annProgOpt: Option[ProgramAnn]): String = {
		annProgOpt match {
			case Some(annProg) => prettyPrint(annProg)
			case None => "None\n"
		}
	}

	def prettyPrint(aTerm: Term) = {
		aTerm.toString
	}
}
