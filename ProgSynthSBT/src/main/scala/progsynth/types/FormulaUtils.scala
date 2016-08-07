package progsynth.types
import progsynth.debug.PSDbg._

/** tests at:
 * tests.formulas.FormulaAndTermUtilsTest
 * tests.types.FormulaUtilsTest*/
trait FormulaUtils[T] { self: Formula[T] =>
	def simplify(): Formula[T] = {
		writeln0(self)
		self match {
			case And(f1, f2) =>
				val f1s = f1.simplify
				val f2s = f2.simplify
				(f1s, f2s) match {
					case (True1(), f) => f
					case (f, True1()) => f
					case (False1(), _) => False1()
					case (_, False1()) => False1()
					case _ => And(f1s, f2s)
				}
			case Or(f1, f2) =>
				val f1s = f1.simplify
				val f2s = f2.simplify
				(f1s, f2s) match {
					case (True1(), _) => True1()
					case (_, True1()) => True1()
					case (False1(), f) => f
					case (f, False1()) => f
					case _ => Or(f1s, f2s)
				}

			case Not(f) =>
				val fs = f.simplify
				(fs) match {
					case True1() => False1()
					case False1() => True1()
					case _ => Not(fs)
				}

			case Impl(f1, f2) =>
				val f1s = f1.simplify
				val f2s = f2.simplify
				(f1s, f2s) match {
					case (True1(), f) => f
					case (f, True1()) => True1()
					case (False1(), _) => True1()
					case (f, False1()) => Not(f)
					case _ => Impl(f1s, f2s)
				}

			case Iff(f1, f2) =>
				val f1s = f1.simplify
				val f2s = f2.simplify
				(f1s, f2s) match {
					case (True1(), f) => f
					case (f, True1()) => f
					case (False1(), f) => Not(f)
					case (f, False1()) => Not(f)
					case _ => Iff(f1s, f2s)
				}
			case Forall(v, f) => Forall(v, f.simplify)
			case Exists(v, f) => Exists(v, f.simplify)
			case _ => self
		}
	}

	def existsSubF(fun: Formula[T] => Boolean): Boolean = {
		if (fun(self)){
			true
		}else {
			self.childs.exists(_.existsSubF(fun))
		}
	}

	/** collect the subformulas satisfying the given condition
	 * If a subformula matches the condition then
	 * the subformula tree is not searched further.
	 */
	def collectSubFTop(fun: PartialFunction[Formula[T], Boolean]): Stream[Formula[T]] = {
		if (fun.isDefinedAt(self) && fun(self)){
			Stream(self)
		}else {
			self.childs.toStream flatMap (_.collectSubFTop(fun))
		}
	}

	/** Returns stream of all subformulas in ?? first order*/
	def allSubF(): Stream[Formula[T]] = {
		Stream(self) ++ (self.childs.toStream flatMap (_.allSubF))
	}

	/** map formulas */
	def mapF(fun: PartialFunction[Formula[T], Formula[T]]): Formula[T] = {
		fun.lift(self).getOrElse{
			self.updateChilds(self.childs map {_.mapF(fun)})
		}
	}

	def containsUnknown(): Boolean = {
		existsSubF(_.isUnknown)
	}
}