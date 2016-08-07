package progsynth.extractors
import progsynth.types.ProgramAnn
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._

import java.lang.RuntimeException
import progsynth.printers.ProgramAnnHtmlDbgPrinter

trait InferAnn { self: ProgramAnn =>
	/**
	 * Infers the Unknown invariants (invariants with unknown subformula)
	 * Modifies the pre and post of pa in place.
	 */
	def inferAnn(): Unit
}

trait FunctionProgInferAnn { self: FunctionProg =>
	def inferAnn(): Unit = {
		annProg.inferAnn()
	}
}

trait ProcedureDefInferAnn { self: ProcedureDef =>
	def inferAnn(): Unit = {
		body.inferAnn()
	}
}

trait ProcedureCallInferAnn { self: ProcedureCall =>
	def inferAnn(): Unit = {
		//1. From post condition infer pre condition
		//2. From pre condition infer post condition
		throw new RuntimeException("ProcedureCallInferAnn not implemented")
	}
}

trait IfProgInferAnn { self: IfProg =>
	/**
	 * Infers the invariants with unknown subformula
	 * Modifies the pre and post of pa in place
	 */
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>IfProg</div>""", """c:\\temp\\infer""")
		grdcmds foreach { grdcmd =>
			//self.pre and self.post might involve a unknown formula
			val cmdPreInv = InvariantT.term.mod(self.pre, _  && grdcmd.guard)
			grdcmd.cmd.setSpecIfUnknown(Some(cmdPreInv), Some(self.post))
			grdcmd.cmd.inferAnn()
			assert(!grdcmd.cmd.post.rvVar.isDefined, "Unimplemented Feature: If the post of each grd.cmd has a rvVar, then they need to be unified.")
		}
		if (pre.containsUnknown) {
			self.withNewParams( pre =
				//InvariantT(None, Formula.mkDisjunct(grdcmds map { _.cmd.pre.term }), None)
				InvariantT(None, TermBool.mkConjunct(grdcmds map { grdcmd => grdcmd.guard impl grdcmd.cmd.pre.term }), None)
			)
		}
		if(post.containsUnknown) {
			self.withNewParams( post =
				InvariantT(None, TermBool.mkDisjunct(grdcmds map { _.cmd.post.term }), None)
			)
		}
	}
}

trait WhileProgInferAnn { self: WhileProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>WhileProg</div>""", """c:\\temp\\infer""")
		grdcmds foreach { grdcmd =>
			if (loopInv.isDefined) {
				if(grdcmd.cmd.pre.containsUnknown){
					grdcmd.cmd.withNewParams( pre =
						InvariantT(None, loopInv.get && grdcmd.guard, None)
					)
				}

				if(grdcmd.cmd.post.containsUnknown){
					grdcmd.cmd.withNewParams( post =
						InvariantT(None, loopInv.get, None)
					)
				}
				grdcmd.cmd.inferAnn()
			}
		}
	}
}

trait CompositionInferAnn { self: Composition =>
	def inferAnn(): Unit = {
		var iPost = post
		//Backward Pass
		programs.reverse foreach { iprog =>
			ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>Backward Pass Composition</div>""", """c:\\temp\\infer""")
			if(iprog.post.containsUnknown && !iPost.containsUnknown){
				iprog.withNewParams( post = iPost)
			}
			iprog.inferAnn()
			iPost = iprog.pre
		}
		if(self.pre.containsUnknown && !iPost.containsUnknown){
			self.withNewParams(pre = iPost)
		}

		//Forward Pass

		var iPre = pre
		programs foreach { iprog =>
			ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>Forward Pass Composition</div>""", """c:\\temp\\infer""")
			if(iprog.pre.containsUnknown && !iPre.containsUnknown){
				iprog.withNewParams(pre = iPre)
			}
			iprog.inferAnn()
			iPre = iprog.post
		}
		if(self.post.containsUnknown && !iPre.containsUnknown ){
			self.withNewParams(post = iPre)
		}

	}
}

trait ValDefProgInferAnn { self: ValDefProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>ValDef</div>""", """c:\\temp\\infer""")
		//val lhsVar = Var(lhs.v, lhs.itype)
		rhsOpt match {
			case Some(rhs) =>
				assert(!post.rvVar.isDefined, "ValDef post should not have rvVar defined")
				if (rhs.pre.containsUnknown && !pre.containsUnknown) {
					rhs.withNewParams(pre = pre)
				}
				if (rhs.post.containsUnknown) {
					rhs.withNewParams ( post =
							//val rhsRvVar = Var("_rv", lhs.itype)
							//post.copy(	formula = 	post.term.replaceVar(lhsVar, rhsRvVar),
							//			rvVar 	= 	Some(rhsRvVar)) //subst lhs by rv
							post.copy(rvVar = Some(lhs))
							)
				}
				
				rhs.inferAnn()
				
				if (pre.containsUnknown && !rhs.pre.containsUnknown) {
					self.withNewParams(pre = rhs.pre)
				}
				
				if (post.containsUnknown) {
					if ( ! rhs.post.term.containsVar(lhs)){
						self.withNewParams (post =
							{var newPostInv = rhs.post.substituteRvVar(lhs) //subst rv by lhs
							newPostInv.removeRvVar}
								)
					}
				}
			case None => //do nothing
		}
	}
}

trait VarDefProgInferAnn { self: VarDefProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>VarDef</div>""", """c:\\temp\\infer""")
		//val lhsVar = Var(lhs.v, lhs.itype)
		rhsOpt match {
			case Some(rhs) =>
				assert(!post.rvVar.isDefined, "VarDef post should not have rvVar defined")
				if (rhs.pre.containsUnknown && !pre.containsUnknown) {
					rhs.withNewParams(pre = pre)
				}
				if (rhs.post.containsUnknown) {
					rhs.withNewParams ( post =
							//val rhsRvVar = Var("_rv", lhs.itype)
							//post.copy(	formula = 	post.term.replaceVar(lhsVar, rhsRvVar),
							//			rvVar 	= 	Some(rhsRvVar)) //subst lhs by rv
							post.copy(rvVar = Some(lhs))
							)
				}
				
				rhs.inferAnn()
				
				if (pre.containsUnknown && !rhs.pre.containsUnknown) {
					self.withNewParams(pre = rhs.pre)
				}
				
				if (post.containsUnknown) {
					if ( ! rhs.post.term.containsVar(lhs)){
						self.withNewParams ( post =
							{var newPostInv = rhs.post.substituteRvVar(lhs) //subst rv by lhs
							newPostInv.removeRvVar}
								)
					}
				}
			case None => //do nothing
		}
	}
}

trait AssignmentInferAnn { self: Assignment =>
	def inferAnn(): Unit =
	self match {
		case AssignmentC((lhs, rhs) :: Nil) =>
			ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>Assignment</div>""", """c:\\temp\\infer""")
			//val lhsVar = Var(lhs.v, lhs.itype)

			assert(!post.rvVar.isDefined, "Assignement post should not have rvVar defined")
			if (rhs.pre.containsUnknown && !pre.containsUnknown) {
				rhs.withNewParams(pre = pre)
			}
			if (rhs.post.containsUnknown) {
				rhs.withNewParams (post =
					//val rhsRvVar = Var("_rv", lhs.itype)
					//post.copy(	formula = 	post.term.replaceVar(lhsVar, rhsRvVar),
					//			rvVar 	= 	Some(rhsRvVar)) //subst lhs by rv
					post.copy(rvVar = Some(lhs))
				)
			}

			rhs.inferAnn()

			if (pre.containsUnknown && !rhs.pre.containsUnknown) {
				self.withNewParams(pre = rhs.pre)
			}

			if (post.containsUnknown) {
				if ( ! rhs.post.term.containsVar(lhs)){
					self.withNewParams ( post =
						{var newPostInv = rhs.post.substituteRvVar(lhs) //subst rv by lhs
						newPostInv.removeRvVar}
					)
				}
			}
		case AssignmentC(_) => throw new RuntimeException("AssignmentInferAnn of simultaneous assignments not implemented")
	}
}

trait ExprProgInferAnn { self: ExprProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>ExprProg</div>""", """c:\\temp\\infer""")
		if(self.pre.containsUnknown && !self.post.containsUnknown) {
			self.withNewParams( pre =
				self.post.substituteRvVar(expr).removeRvVar //replace rv by term
			)
		} else if(!self.pre.containsUnknown && self.post.containsUnknown) {
			self.withNewParams( post =
				{val newRvVar = Var.mkVar("_rv", self.expr.getType())
				val rvEqExpr = newRvVar.eqeq(self.expr)
				val exprPostInv = InvariantT(None, self.pre.term && rvEqExpr, Some(newRvVar))
				exprPostInv}
			)
		}
	}
}

trait SkipProgInferAnn { self: SkipProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>SkipProg</div>""", """c:\\temp\\infer""")
		if(self.pre.containsUnknown && !self.post.containsUnknown) {
			self.withNewParams(pre = self.post.removeRvVar)
		} else if (!self.pre.containsUnknown && self.post.containsUnknown) {
			self.withNewParams(post = self.pre)
		}
	}
}

trait UnknownProgInferAnn { self: UnknownProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>UnknownProg</div>""", """c:\\temp\\infer""")
		//No child programs, do nothing
	}
}

trait AssumeProgInferAnn { self: AssumeProg =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>AssumeProg</div>""", """c:\\temp\\infer""")
		//No child programs, do nothing
	}
}

trait LitConstantInferAnn { self: LitConstant =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>LitConstant</div>""", """c:\\temp\\infer""")
		throw new RuntimeException("inferAnn on LitConstant should not have been called")
	}
}
trait IdentifierInferAnn { self: Identifier =>
	def inferAnn(): Unit = {
		ProgramAnnHtmlDbgPrinter.writeProgramAnnToHtml("""<div>Identifier</div>""", """c:\\temp\\infer""")
		throw new RuntimeException("inferAnn on Identifier should not have been called")
	}
}
