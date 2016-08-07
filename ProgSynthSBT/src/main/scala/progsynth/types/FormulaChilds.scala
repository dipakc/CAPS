package progsynth.types

trait FormulaChilds[T] { self: Formula[T] =>
	def childs(): List[Formula[T]] = {
		self match {
			case True1() => Nil
			case False1() => Nil
			case Atom(a) => Nil
			case Not(f) => f :: Nil
			case And(f1, f2) => f1 :: f2 :: Nil
			case Or(f1, f2) => f1 :: f2 :: Nil
			case Impl(f1, f2) => f1 :: f2 :: Nil
			case Iff(f1, f2) => f1 :: f2 :: Nil
			case Forall(v, f) => f :: Nil
			case Exists(v, f) => f :: Nil
			case Unknown() => Nil
		}
	}

	def updateChilds(nchilds: List[Formula[T]]): self.type = {
		val retVal = self match {
			case True1() => self
			case False1() => self
			case Atom(pred) => self
			case Not(f) => Not(nchilds.head)
			case And(f1, f2) => And(nchilds.head, nchilds.tail.head)
			case Or(f1, f2) => Or(nchilds.head, nchilds.tail.head)
			case Impl(f1, f2) => Impl(nchilds.head, nchilds.tail.head)
			case Iff(f1, f2) => Iff(nchilds.head, nchilds.tail.head)
			case Forall(v, f) => Forall(v, nchilds.head)
			case Exists(v, f) => Exists(v, nchilds.head)
			case Unknown() => self
		}
		retVal.asInstanceOf[self.type] //TODO: avoid asInstanceOf
	}
}
