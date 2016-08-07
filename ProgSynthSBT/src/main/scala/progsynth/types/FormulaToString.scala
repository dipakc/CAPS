package progsynth.types

trait FormulaToString[T] { self: Formula[T] =>
	def toString(): String
}
