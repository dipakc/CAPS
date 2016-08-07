package progsynth.types
import scala.collection.mutable.LinkedHashMap
import progsynth.methodspecs.InterpretedFns._

trait TermUtils { self: Term =>

	def mapSubTerms(fun: PartialFunction[Term, Term]): Term = {
		if (fun.isDefinedAt(self))
			fun(self)
		else self match {
			case Var(v) => self
			case FnApp(fn, ts) => FnApp.mkFnApp(fn, ts map (_ mapSubTerms fun), fn.tpe)
			case Const(name) => self
			case ArrSelect(arr, index) => ArrSelect.mkArrSelect(arr mapSubTerms fun, index mapSubTerms fun, PSType.getBasicTpe(arr.getType))
			case ArrStore(arr, index, value) => ArrStore.mkArrStore(arr mapSubTerms fun, index mapSubTerms fun, value mapSubTerms fun, arr.getType)
			case QTerm(opr, dummies, range, term) => QTerm.mkQTerm(opr, dummies, range mapSubTerms fun, term mapSubTerms fun) //TODO: Check the correctness
		}
	}

	def replaceVar(x: Var, y: Term): self.type = {
		(self mapSubTerms{ case `x` => y }).asInstanceOf[self.type]
	}

	def collectItems[A](fun: PartialFunction[Any, A]): List[A] = {
		if (fun.isDefinedAt(self)) fun(self) :: Nil
		else self match {
			case Var(v) => Nil
			case FnApp(f, ts) => ts flatMap (_.collectItems(fun))
			case Const(name) => Nil
			case ArrSelect(arr, index) =>  arr.collectItems(fun) ::: index.collectItems(fun)
			case ArrStore(arr, index, value) =>  arr.collectItems(fun) ::: index.collectItems(fun) ::: value.collectItems(fun)
			case QTerm(opr, dummies, range, term) => range.collectItems(fun) ::: term.collectItems(fun)
			case _:UnkTerm => Nil
		}
	}

	def getVars = {
		val retVal = collectItems {case v @ Var(_) => v}
		retVal
	}

	def collectItemsWithContext[C, R]	( fun: PartialFunction[(Any, C), R], ctxUpdate: PartialFunction[(Any, C), C])
										( ctx: C): List[R] = {
		if (fun.isDefinedAt(self, ctx))
			fun(self, ctx) :: Nil
		else {
			val newCtx = if (ctxUpdate.isDefinedAt(self, ctx)) ctxUpdate(self, ctx) else ctx
			def navigateTerm(t: Term) = t.collectItemsWithContext(fun, ctxUpdate)(newCtx)

			self match {
				case Var(v) => Nil
				case FnApp(f, ts) => ts flatMap (navigateTerm(_))
				case Const(name) => Nil
				case ArrSelect(arr, index) =>  navigateTerm(arr) ::: navigateTerm(index)
				case ArrStore(arr, index, value) =>  navigateTerm(arr) ::: navigateTerm(index) ::: navigateTerm(value)
				case QTerm(opr, dummies, range, term) => navigateTerm(range) ::: navigateTerm(term)
				case _:UnkTerm => Nil
			}
		}
	}

	def existsSubTerm(fun: Term => Boolean ): Boolean = {
		//writeln0((self)
		self match {
			case Var(_) => fun(self)
			case Const(_) => fun(self)
			case FnApp(_, ts) => fun(self) || (ts exists {_.existsSubTerm(fun)})
			case ArrSelect(arr, index) => fun(self) || (List(arr, index) exists {_.existsSubTerm(fun)})
			case ArrStore(arr, index, value) => fun(self) || (List(arr, index, value) exists {_.existsSubTerm(fun)})
			case QTerm(opr, dummies, range, term) => fun(self) || (List(range, term) exists {_.existsSubTerm(fun)})
			case _: UnkTerm => fun(self)
		}
	}

	def getFreeAndBoundVars(): List[Var] = {
		def isVar: PartialFunction[Any, Var] = { case x: Var => x }
			( self collectItems (isVar) ) distinct
	}

	def getFreeVars(): List[Var] = {
		def isFreeVar: PartialFunction[(Any, List[Var]), Var] = {
			case (x: Var, boundVars) if !(boundVars contains x) => x
		}
		def updateBoundVars: PartialFunction[(Any, List[Var]), List[Var]] = {
			case (QTerm(_, dummies, _, _), bVars) => bVars ++ dummies
		}
		( self.collectItemsWithContext(isFreeVar, updateBoundVars)(Nil)) distinct
	}

	def isUnknown() = self match {
		case _: UnkTerm => true
		case _ => false
	}

	def containsVar(aVar: Var): Boolean = {
		self existsSubTerm { term =>
			term match {
				case `aVar` => true
				case _ => false
			}
		}
	}

	/**Returns scala code(String) that constructs the Term. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the term
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the term is returned */
	def toCode ( ctxMap: LinkedHashMap[String, String] , introduceVar: Boolean=true): String = {
		self match {
			case aVar @ Var(v) =>
				val codeRhs = <a>Var.mkVar("{v}", {aVar.getType})</a>.text
				val seed = Some(v)
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
			case aConst @ Const(name) =>
				val codeRhs = <a>Const.mkConst("{name}", {aConst.getType})</a>.text
				val seed = Some("c" + name)
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
			case FnApp(fn, ts) =>
				val fnCode = fn.toCode(ctxMap)
				val tsCode = {
					val tCodesCsv = (ts map (_.toCode(ctxMap))).mkString(", ")
					<a>List({tCodesCsv})</a>.text
				}
				//-------------
				val codeRhs = <a>FnApp({fnCode}, {tsCode})</a>.text
				val seed = Some(fn.name.replace("$", ""))
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
			case ArrSelect(arr, index) =>
				val arrCode = arr.toCode(ctxMap)
				val indexCode = index.toCode(ctxMap)
				//-------------
				val codeRhs = <a>ArrSelect.mkArrSelect({arrCode}, {indexCode}, {PSType.getBasicTpe(arr.getType)})</a>.text
				val seed = Some("arrsel")
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
			case ArrStore(arr, index, value) =>
				val arrCode = arr.toCode(ctxMap)
				val indexCode = index.toCode(ctxMap)
				val valueCode = value.toCode(ctxMap)
				//-------------
				val codeRhs = <a>ArrStore.mkArrStore({arrCode}, {indexCode}, {valueCode},  {arr.getType})</a>.text
				val seed = Some("arrsto")
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
			case QTerm(opr, dummies, range, term) =>
				val oprCode = opr.toCode(ctxMap)
				val dummiesCode = <a>List({dummies.map(_.toCode(ctxMap)).mkString(", ")})</a>.text
				val rangeCode = range.toCode(ctxMap)
				val termCode = term.toCode(ctxMap)
				val codeRhs = <a>QTerm.mkQTerm({oprCode}, {dummiesCode}, {rangeCode}, {termCode})</a>.text
				val seed = Some("qTerm")
				GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
				//throw new RuntimeException("Exception in toCode: QTerm not handled")
		}
	}

	/**Replaces variables with corresponding terms.
	 * Type compatibility is not checked.
	 * The variables are simultaneously replaced.
	 * */
	def replaceVarsSim(varsTermMap: Map[Var, Term]): self.type = {
		(self mapSubTerms {
			case aVar: Var if varsTermMap contains aVar =>
				varsTermMap.get(aVar).get
		}).asInstanceOf[self.type]
	}

	def replaceVarsSim(vars: List[Var], terms:List[Term]): self.type = {
		val varTermMap = vars.zip(terms).toMap
		replaceVarsSim(varTermMap)
	}

	def replaceVarSim(aVar: Var, aTerm:Term): self.type = {
		
		val varTermMap = List(aVar).zip(List(aTerm)).toMap
		replaceVarsSim(varTermMap)
	}

	def emptyRangeRule(aTerm: Term) : Option[Term] = aTerm match {
		case QTerm(fn, dummies, TermBool.FalseT, term) => getUnit(fn)
		case _ => None
	}

	//Returns true if the term is satisfiable
	def isNonEmpty(aTerm: Term): Boolean = {
		true
	}

	def isEmpty(aTerm: Term): Boolean = {
		true
	}

	/**Checks if the formula is free of given variables */
	def isFreeOf(vars: List[Var]): Boolean = {
		this.getFreeVars.intersect(vars).isEmpty
	}

	def isFreeOf(aVar: Var): Boolean = {
		isFreeOf(aVar :: Nil)
	}
	
}
