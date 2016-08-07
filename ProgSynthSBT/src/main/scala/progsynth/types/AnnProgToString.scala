package progsynth.types

trait FunctionProgToString { self: FunctionProg =>
	override def toString = {
		"FunctionProg(" +
		pre + ", " +
		name + ", " +
		annProg + ", " +
		post + ")"
	}
}

trait ProcedureDefToString { self: ProcedureDef =>
	override def toString = {
		"ProcedureDef(" +
		pre + ", " +
		name + ", " +
		fParams + ", " +
		body + ", " +
		post + ")"
	}
}

trait ProcedureCallToString { self: ProcedureCall =>
	override def toString = {
		"ProcedureCall(" +
		pre + ", " +
		procDef + ", " +
		params + ", " +
		post + ")"
	}
}

trait IfProgToString { self: IfProg =>
	override def toString = {"IfProg(" + pre + "," + grdcmds + "," + post + ")"}
}

trait WhileProgToString { self: WhileProg =>
	override def toString = {"WhileProg(" + pre + "," + loopInv + "," + grdcmds + "," + post + ")"}
}

trait CompositionToString { self:Composition =>
	override def toString = {"Composition(" + pre + "," + programs + "," + post + ")"}
}

trait IdentifierToString{ self: Identifier =>
	override def toString = {"Identifier(" + pre + "," + name + ","+ itype + "," + post + ")"}
}

trait ValDefProgToString{ self: ValDefProg =>
	override def toString = {"ValDefProg(" + pre + "," + lhs + "," + rhsOpt + "," + post + ")"}
}

trait VarDefProgToString{ self: VarDefProg =>
	override def toString = {"VarDefProg(" + pre + "," + lhs + "," + rhsOpt + "," + post + ")"}
}

trait AssignmentToString{ self: Assignment =>
	override def toString = {"Assignment(" + pre + "," + asgns + "," + post + ")"}
}

trait SkipProgToString{ self: SkipProg =>
	override def toString = {"SkipProg(" + pre + "," + post + ")"}
}

trait UnknownProgToString{ self: UnknownProg =>
	override def toString = {"UnknownProg(" + pre + "," + upid + "," + post + ")"}
}

trait AssumeProgToString{ self: AssumeProg =>
	override def toString = {"AssumeProg(" + pre + "," + pred + "," + post + ")"}
}

trait LitConstantToString{ self: LitConstant =>
	override def toString = {"LitConstant(" + pre + "," + name + "," + post + ")"}
}

trait ExprProgToString{ self: ExprProg =>
	override def toString = {"ExprProg(" + pre + "," + expr + "," + post + ")"}
}

