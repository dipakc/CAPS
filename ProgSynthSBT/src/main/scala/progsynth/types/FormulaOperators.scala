package progsynth.types

trait FormulaOperators[T] { self: Formula[T] =>
	//TODO: ensure precedence
	// &&, ||, (impl, iff)
	// Infix operators : example: "aFormula and bFormula" is same as "aFormula.and(bFormula)"

	//def and(f: Formula[T]): Formula[T] = And(this, f)
	//def /\ (f: Formula[T]): Formula[T] = And(this, f)
	def && (f: Formula[T]): Formula[T] = And(this, f)

	//def or(f: Formula[T]): Formula[T] = Or(this, f)
	//def \/  (f: Formula[T]): Formula[T] = Or(this, f)
	def || (f: Formula[T]): Formula[T] = Or(this, f)

	def impl(f: Formula[T]): Formula[T] = Impl(this, f)
	//def ==>  (f: Formula[T]): Formula[T] = Or(this, f)

	def iff(f: Formula[T]): Formula[T] = Iff(this, f)
	//def ===  (f: Formula[T]): Formula[T] = Or(this, f)
}
