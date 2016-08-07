package progsynth.types
import scala.collection.mutable.LinkedHashMap

trait PredUtils { self: Pred =>
	def mapTerms(fun: Term => Term) = {
			self.copy(ts = self.ts map fun)
	}

	def mapSubTerms(fun: PartialFunction[Term, Term]) = {
		self.copy(ts = self.ts map {_ mapSubTerms fun})
	}

	def collectItems[A](fun: PartialFunction[Any, A]): List[A] = {
		if (fun.isDefinedAt(self))
			fun(self) :: Nil
		else self match {
			case Pred(r, ts) => ts flatMap (_.collectItems(fun))
		}
	}

	def collectItemsWithContext[C, R]	( fun: PartialFunction[(Any, C), R], ctxUpdate: PartialFunction[(Any, C), C])
										( ctx: C): List[R] = {
		if (fun.isDefinedAt(self, ctx))
			fun(self, ctx) :: Nil
		else self match {
			case Pred(r, ts) =>
				ts flatMap {t =>
					val newCtx = if (ctxUpdate.isDefinedAt(self, ctx))
						ctxUpdate(self, ctx)
					else
						ctx
					t.collectItemsWithContext(fun, ctxUpdate)(newCtx)
				}
		}
	}
	def existsSubTerm(fun: Term => Boolean) = {
		//writeln0(("pred " + self)
		self.ts exists {_.existsSubTerm(fun)}
	}

		/**Returns scala code(String) that constructs the Predicate. */
	def toCode(): String = {
		val ctxMap = LinkedHashMap[String, String]()
		toCode(ctxMap, true)
		(for((code, vari) <- ctxMap) yield {
			<a>val {vari} = {code}</a>.text
		}).mkString("\n")
	}

	/**Returns scala  code(String) that constructs the Predicate
	 * given the context Map(code-> variable_name) of the already converted program
	 * If introduceVar is true, then the ctxMap is updated and a variable name is returned.
	 * If introduceVar is false, program fragment corresponding to the Predicate is returned */
	def toCode(ctxMap: LinkedHashMap[String, String] , introduceVar: Boolean=true): String = {
		//Pred(r: String, ts: List[Term])
		val tsCodes = (ts map (_.toCode(ctxMap))).mkString(", ")
		val tsList = <a>List({tsCodes})</a>.text
		//--------
		val codeRhs = <a>Pred("{r}", {tsList})</a>.text
		val seed = Some("pred")
		GenCodeObj.gcaam(codeRhs, ctxMap, seed, introduceVar)
	}
}