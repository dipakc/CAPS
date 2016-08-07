package progsynth.types
//import progsynth.PSPredef._ //Do not include PSPredef since this file is in the client library
import scala.collection.mutable.LinkedHashMap
import scala.xml.Elem

trait ProgramAnnUtils { self: ProgramAnn =>
	
    def strengthenPre(spre: TermBool ) = {
    	this.withNewParams( pre = pre.addConjunct(spre))
    }
    def strengthenPost(spost: TermBool ) = {
    	this.withNewParams(post = post.addConjunct(spost))
    }
	def setSpecIfUnknown(newPre: Option[InvariantT], newPost: Option[InvariantT]): Unit = {
		if (self.pre.term.existsSubTerm(_.isUnknown) && newPre.isDefined) {
			self.withNewParams(pre = newPre.get)
		}
		if (self.post.term.existsSubTerm(_.isUnknown) && newPost.isDefined) {
			self.withNewParams(post = newPost.get)
		}
	}

	def setPreIfUnknown(newPre: Option[InvariantT]) = {
		if (self.pre.term.existsSubTerm(_.isUnknown) && newPre.isDefined) {
			self.withNewParams(pre = newPre.get)
		}
	}

	def setPostIfUnknown(newPost: Option[InvariantT]) = {
		if (self.post.term.existsSubTerm(_.isUnknown) && newPost.isDefined) {
			self.withNewParams(post = newPost.get)
		}
	}

	def allPOsAreValid(): Boolean = {
		def allSelfPOsAreValid: Boolean = {
			proofObligs.forall{po =>
				po.status.isDefined && po.status.get.isValid
			}
		}

		allSelfPOsAreValid &&  getSubPas().forall(pa => pa.allPOsAreValid)
	}

	//f: floatingPre => Some(newProg) : a function that builds a program from post of the previous program.
	//Returns a composition program
	def compose(f: InvariantT => Option[ProgramAnn]) : ProgramAnn = {
		val newProgOpt =  f(self.post)
		newProgOpt match {
			case Some(newProg) =>
				val newProgs = self match {
					case CompositionC(progs) => progs ++ List(newProg)
					case _ => List(self, newProg)
				}
				
				val compProg = new Composition(newProgs, self.pre, newProg.post)
				compProg
			case None => this
		}
	}
	
	def appendProg(newPrg: ProgramAnn): Composition = newPrg match {
		case cmp: Composition =>
			Composition(cmp.programs ++ List(newPrg), self.pre, newPrg.post)
		case prg =>
			Composition(prg, newPrg, prg.pre, newPrg.post)
	}

	/*
	/**Returns scala code(String) that constructs the ProgramAnn. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the ProgramAnn
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the ProgramAnn is returned */
	def toCode ( ctxMap: LinkedHashMap[String, String] , introduceVar: Boolean=true): String = {
		def addPrePostCode(retVar: String):Unit = {
			val preCode = self.pre.toCode(ctxMap)
			val postCode = self.post.toCode(ctxMap)
			val prePostCode = <a>{ retVar}.setPrePost({ preCode }, { postCode })</a>.text
			GenCodeObj.gcaam(prePostCode, ctxMap, None, true)
		}
		self match {
			case FunctionProg(name, params, retType, annProg) =>
				val paramCodes = params map { _.toCode(ctxMap) }
				val paramCodesCsv = paramCodes.mkString(", ")
				val annProgCode = annProg.toCode(ctxMap)
				//----------
				val codeRhs =
					<a>FunctionProg("{name}", List({paramCodesCsv}), {retType}, {annProgCode})</a>.text
				val seed = Some("fun")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case IfProg(grdcmds) =>
				val grdCmdCodes = grdcmds map { _.toCode(ctxMap)}
				val grdCmdCodeCsv =  grdCmdCodes.mkString(", ")
				//-------------------
				val codeRhs = <a>IfProg(List({ grdCmdCodeCsv }))</a>.text
				val seed = Some("ifprog")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case WhileProg(liOpt, grdcmds) =>
				val grdCmdCodes = grdcmds map { _.toCode(ctxMap) }
				val grdCmdCodeCsv =  grdCmdCodes.mkString(", ")
				val loopInvCode = liOpt match {
					case Some(li) => "Some(" + FOLFormula.toCode(li, ctxMap) + ")"
					case None => "None"
				}
				//-------------------
				val codeRhs = <a>WhileProg({loopInvCode}, List({ grdCmdCodeCsv }))</a>.text
				val seed = Some("whileprog")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case Composition(programs) =>
				val programCodesCsv = (programs map { _.toCode(ctxMap) }).mkString(", ")

				val codeRhs = <a>Composition(List({ programCodesCsv }))</a>.text
				val seed = Some("compprog")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case ValDefProg(lhs, rhs) =>
				val lhsCode = lhs.toCode(ctxMap)
				val rhsCode = rhs.toCode(ctxMap)
				val codeRhs = <a>ValDefProg({ lhsCode }, { rhsCode })</a>.text
				val seed = Some("valDef")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case VarDefProg(lhs, rhs) =>
				val lhsCode = lhs.toCode(ctxMap)
				val rhsCode = rhs.toCode(ctxMap)
				val codeRhs = <a>VarDefProg({ lhsCode }, { rhsCode })</a>.text
				val seed = Some("varDef")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case Assignment((lhs, rhs)::Nil) =>
				val lhsCode = lhs.toCode(ctxMap)
				val rhsCode = rhs.toCode(ctxMap)
				val codeRhs = <a>Assignment({ lhsCode }, { rhsCode })</a>.text
				val seed = Some("assgn")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case Assignment(_) => throw new RuntimeException("toCode of simultaneous assignments not implemented")
			case SkipProg() =>
				val codeRhs = <a>SkipProg()</a>.text
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, None, introduceVar)
				addPrePostCode(retVar)
				retVar
			case UnknownProg(id) =>
				val codeRhs = <a>UnknownProg({ id })</a>.text
				val seed = Some("unk")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case ExprProg(term) =>
				val termCode = term.toCode(ctxMap)
				val codeRhs = <a>ExprProg({ termCode })</a>.text
				val seed = Some("expr")
				val retVar = GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				addPrePostCode(retVar)
				retVar
			case Identifier(_, _) => throw new RuntimeException("Identifier encountered")
			case LitConstant(_) => throw new RuntimeException("LitConstant encountered")
		}
	}
	*/
	def toHtml(withPO: Boolean = true)(implicit focusId: Option[Int]): Elem = {
	    import progsynth.printers.XHTMLPrinters2._
	    if (withPO)
	    	programAnnToHtmlMain(self)
	    else
	    	<div class="srcfile">
	    	{programAnnToHtml(self)}
	    	</div>
	}

}

trait GuardedCmdUtils { self: GuardedCmd =>
	//GuardedCmd(guard: FOLFormula, cmd: ProgramAnn)
	/*
	def toCode ( ctxMap: LinkedHashMap[String, String] , introduceVar: Boolean=true): String = {
		val grdCode = FOLFormula.toCode(guard, ctxMap)
		val cmdCode = cmd.toCode(ctxMap)
		val codeRhs = <a>GuardedCmd({grdCode}, {cmdCode})</a>.text
		val seed = Some("grdcmd")
		GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}
	* */
	
	def strengthenCmdPre( f: TermBool ) = {
		GuardedCmd(guard, cmd.strengthenPre(f))
	}
	
	def strengthenCmdPost(f: TermBool) = {
		GuardedCmd(guard, cmd.strengthenPost(f))
	}

	def strengthenGrd(f: TermBool) = {
		GuardedCmd(guard && f, cmd)
	}
	
	def mapCmd(f: ProgramAnn => ProgramAnn) = GuardedCmd(this.guard, f(this.cmd))
}

trait CompositionUtils { self: Composition =>
	
	/**
	 * requires self.programs is non-empty
	 */
	def getTailProg(): ProgramAnn = {
		self.programs match  {
			case head :: Nil => SkipProg(head.post, self.post)
			case head :: tail => buildComposition(tail)
		}
	}
	
	/**
	 * requires prgs is nonEmpty
	 */
	private def buildComposition(prgs: List[ProgramAnn]) =  prgs match {
		case prg :: Nil => prg
		case Nil => throw new RuntimeException("list should not be empty")
		case _ => Composition(prgs, prgs.head.pre, prgs.last.post)
	}

}