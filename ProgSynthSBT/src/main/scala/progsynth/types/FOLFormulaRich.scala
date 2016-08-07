package progsynth.types

import Types._
import progsynth.debug.FormulaParseTreeViewer
import progsynth.ProgSynth.toRichFormula
import scalaz.Scalaz._

/** == Pimp for FOLFormula ([[progsynth.types.Formula]][Pred]) ==
 * Another pimp [[progsynth.dsl.FOLFormulaDSL]] defines the logical DSL
 *
 * [[progsynth.types.Formula]] defines the boolean DSL
 * */
class FOLFormulaRich(val folFormula: FOLFormula) extends FormulaParseTreeViewer with FOLManipulations {

	/** Apply 'fun' to all the top level terms. Does not traverse the terms. */
	def mapTerms(fun: Term => Term): FOLFormula = {
		folFormula map { pred => pred mapTerms fun }
	}

	/** Apply 'fun' to all the terms. Traverses the term tree. */
	def mapSubTerms(fun: PartialFunction[Term, Term]): FOLFormula = {
		folFormula map { pred => pred mapSubTerms fun }
	}

	def replaceVar(x: Var, y: Term): FOLFormula = {
		folFormula mapSubTerms { case `x` => y }
	}

	//TODO: The implementation does not use varsTermMap values!!
	def replaceVars(varsTermMap: Map[String, Term]): FOLFormula = {
		folFormula applyRec {
			case Atom(Pred(r, ts)) => {
				val newTs = ts map { term =>
					term mapSubTerms { case aVar @ Var(varName) if varsTermMap contains varName => Var.mkVar(varName, aVar.getType) }//TODO: check this
				}
				Atom(Pred(r, newTs))
			}
		}
	}

	/**Replaces variables with corresponding terms.
	 * Type compatibility is not checked.
	 * The variables are simultaneously replaced.
	 * */
	def replaceVarsSim(varsTermMap: Map[Var, Term]): FOLFormula = {
		folFormula applyRec {
			case Atom(Pred(r, ts)) => {
				val newTs = ts map { term =>
					term mapSubTerms {
						case aVar: Var if varsTermMap contains aVar =>
							varsTermMap.get(aVar).get }
				}
				Atom(Pred(r, newTs))
			}
		}
	}
	def replaceVarsSim(vars: List[Var], terms:List[Term]): FOLFormula = {
		val varTermMap = vars.zip(terms).toMap
		replaceVarsSim(varTermMap)
	}


	def collectItems[A](fun: PartialFunction[Any, A]): List[A] = {
		if (fun.isDefinedAt(folFormula))
			fun(folFormula) :: Nil
		else folFormula match {
			case True1() => Nil
			case False1() => Nil
			case Atom(a) => a.collectItems(fun)
			case Not(f) => f.collectItems(fun)
			case And(f1, f2) => f1.collectItems(fun) ::: f2.collectItems(fun)
			case Or(f1, f2) => f1.collectItems(fun) ::: f2.collectItems(fun)
			case Impl(f1, f2) => f1.collectItems(fun) ::: f2.collectItems(fun)
			case Iff(f1, f2) => f1.collectItems(fun) ::: f2.collectItems(fun)
			case Forall(v, f) => f.collectItems(fun)
			case Exists(v, f) => f.collectItems(fun)
			case Unknown() => Nil
		}
	}

	def collectItemsWithContext[C, R]	( fun: PartialFunction[(Any, C), R], ctxUpdate: PartialFunction[(Any, C), C])
										( ctx: C): List[R] = {
		if (fun.isDefinedAt(folFormula, ctx))
			fun(folFormula, ctx) :: Nil
		else {
			val newCtx = if (ctxUpdate.isDefinedAt(folFormula, ctx)) ctxUpdate(folFormula, ctx) else ctx
			def navigateFormula(f: FOLFormula) = f.collectItemsWithContext(fun, ctxUpdate)(newCtx)
			def navigatePred(p: Pred) = p.collectItemsWithContext(fun, ctxUpdate)(newCtx)
			folFormula match {
				case True1() => Nil
				case False1() => Nil
				case Atom(p) => navigatePred(p)
				case Not(f) => navigateFormula(f)
				case And(f1, f2) => navigateFormula(f1) ::: navigateFormula(f2)
				case Or(f1, f2) => navigateFormula(f1) ::: navigateFormula(f2)
				case Impl(f1, f2) => navigateFormula(f1) ::: navigateFormula(f2)
				case Iff(f1, f2) => navigateFormula(f1) ::: navigateFormula(f2)
				case Forall(v, f) => navigateFormula(f)
				case Exists(v, f) => navigateFormula(f)
				case Unknown() => Nil
			}
		}
	}

	def getFreeAndBoundVars(): List[Var] = {
		def isVar: PartialFunction[Any, Var] = { case x: Var => x }
		( folFormula collectItems (isVar) ) distinct
	}

	def getFreeVars(): List[Var] = {
		def isFreeVar: PartialFunction[(Any, List[Var]), Var] = {
			case (x: Var, boundVars) if !(boundVars contains x) => x
		}
		def updateBoundVars: PartialFunction[(Any, List[Var]), List[Var]] = {
			case (Forall(v, f), bVars) => v :: bVars //TODO: remove hardcoded PSInt
			case (Exists(v, f), bVars) => v :: bVars //TODO: remove hardcoded PSInt
		}
		( folFormula.collectItemsWithContext(isFreeVar, updateBoundVars)(Nil)) distinct
	}

	def existsSubTerm(fun: Term => Boolean): Boolean = {
		//writeln0(("formula: " + folFormula)
		folFormula exists { pred =>
			/*writeln0(pred);*/
			pred.existsSubTerm(fun)
		}
	}

	def containsVar(aVar: Var): Boolean = {
		folFormula existsSubTerm { term =>
			term match {
				case `aVar` => true
				case _ => false
			}
		}
	}

	def inv(rv: Var): InvariantF = InvariantF(None, folFormula, rv.some)
	def inv(): InvariantF = InvariantF(None, folFormula, None)

	def getTopLevelConjuncts(): List[FOLFormula] = {
		folFormula match {
			case And(f1, f2) => f1.getTopLevelConjuncts ++ f2.getTopLevelConjuncts
			case Unknown() => Nil
			case _ => List(folFormula)
		}
	}
}

trait FOLManipulations { self: FOLFormulaRich =>
	/**e
	 * \Exists i : \phi /\ i = z /\ \psi /\ \eta /\ ...
	 * ===>
	 * \phi' /\ \psi' /\ \eta' /\ ... (where primed formulas are obtained by replacing i by z.
	 *
	 *  \Forall i : \phi /\ i = z /\ \eta /\... => \theta
	 *  ===>
	 *  \Forall i : \phi' /\ \eta' /\... => \theta'
	 */

//	def onePointRule(quantId: Int): FOLFormula = {
//	    def isBoundVarAsgn(formula: FOLFormula, boundVar: Var): Boolean = false
//		self.folFormula.applyRec{
//		    case Exists(v, AndN(fs)) if (self.fid == quantId) =>
//		        //Get the bound_var_asgn atom
//		        fs.filter(isBoundVarAsgn)
//		        //Remove the bound_var_asgn from the formula
//		        //Subst bound var with rhs in the remaining formula
//		    case Forall(v, Impl(AndN(fs), fr)) if (self.fid == quantId) =>
//		        //Get the bound_var_asgn atom
//		        fs.filter(isBoundVarAsgn)
//		        //Remove the bound_var_asgn from the lhs
//		        //Subst bound var with rhs in the remaining formula
//		}
//	}

}