package progsynth.types

trait TermChilds { self: Term =>
	def childs(): List[Term] = {
		self match {
			case Var(v) => Nil
			case Const(name) => Nil
			case FnApp(f, ts) => ts
			case ArrSelect(arr, index) => List(arr, index)
			case ArrStore(arr, index, value) => List(arr, index, value)
			case QTerm(opr, dummies, range, term) => List(range, term) //Dummies are not included.
		}
	}
}