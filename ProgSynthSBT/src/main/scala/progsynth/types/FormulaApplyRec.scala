package progsynth.types

trait FormulaApplyRec[T] { self: Formula[T] =>
	def applyRec(fun: PartialFunction[Formula[T], Formula[T]]): Formula[T] = {
		if (fun.isDefinedAt(self))
			fun(self)
		else self match { //TODO: fix the warning here.
			case True1() => self
			case False1() => self
			case Atom(a) => self
			case Unknown() => self
			case Not(f) => Not(f applyRec fun)
			case And(f1, f2) => And(f1 applyRec fun, f2 applyRec fun)
			case Or(f1, f2) =>  Or(f1 applyRec fun, f2 applyRec fun)
			case Impl(f1, f2) => Impl(f1 applyRec fun, f2 applyRec fun)
			case Iff(f1, f2) => Iff(f1 applyRec fun, f2 applyRec fun)
			case Forall(v, f) => Forall(v, f applyRec fun)
			case Exists(v, f) => Exists(v, f applyRec fun)
			//case Formula => throw new RuntimeException
		}
	}
}
