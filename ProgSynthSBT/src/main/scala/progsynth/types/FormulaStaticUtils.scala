package progsynth.types

trait FormulaStaticUtils {
	def mkConjunct[T](fs: List[Formula[T]]): Formula[T] = {
		fs match {
			case Nil => True1()
			case f :: Nil => f
			case f :: tail => And(f, mkConjunct(tail))
		}
	}
	def mkDisjunct[T](fs: List[Formula[T]]): Formula[T] = {
		fs match {
			case Nil => False1()
			case f :: Nil => f
			case f :: tail => Or(f, mkDisjunct(tail))
		}
	}
}