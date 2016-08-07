package progsynth.types

trait FormulaMonad[T] {
	def map[U](fun: T => U): Formula[U] = this match {
		case True1() => True1()
		case False1() => False1()
		case Atom(a) => Atom(fun(a))
		case Not(f) => Not(f map fun)
		case And(f1, f2) => And(f1 map fun, f2 map fun)
		case Or(f1, f2) => Or(f1 map fun, f2 map fun)
		case Impl(f1, f2) => Impl(f1 map fun, f2 map fun)
		case Iff(f1, f2) => Iff(f1 map fun, f2 map fun)
		case Forall(v, f) => Forall(v, f map fun)
		case Exists(v, f) => Exists(v, f map fun)
		case Unknown() => Unknown()
	}

	def flatMap[U](fun: T => Formula[U]): Formula[U] = this match {
		case True1() => True1()
		case False1() => False1()
		case Atom(a) => fun(a)
		case Not(f) => Not(f flatMap fun)
		case And(f1, f2) => And(f1 flatMap fun, f2 flatMap fun)
		case Or(f1, f2) => Or(f1 flatMap fun, f2 flatMap fun)
		case Impl(f1, f2) => Impl(f1 flatMap fun, f2 flatMap fun)
		case Iff(f1, f2) => Iff(f1 flatMap fun, f2 flatMap fun)
		case Forall(v, f) => Forall(v, f flatMap fun)
		case Exists(v, f) => Exists(v, f flatMap fun)
		case Unknown() => Unknown()
	}

	//TODO: Implement foreach
	//def foreach[U](f: T => U): Unit = {
	//	map(f)
	//	()
	//}

	def foreach[U](fun: T => U): Unit = this match {
		case True1() =>
		case False1() =>
		case Unknown() =>
		case Atom(a) => fun(a)
		case Not(f) => f foreach fun
		case And(f1, f2) => f1 foreach fun; f2 foreach fun
		case Or(f1, f2) => f1 foreach fun; f2 foreach fun
		case Impl(f1, f2) => f1 foreach fun; f2 foreach fun
		case Iff(f1, f2) => f1 foreach fun; f2 foreach fun
		case Forall(v, f) => f foreach fun
		case Exists(v, f) => f foreach fun
	}

	def exists(fun: T => Boolean): Boolean = this match {
		case True1() => false
		case False1() => false
		case Unknown() => false
		case Atom(a) => fun(a)
		case Not(f) => f exists fun
		case And(f1, f2) => (f1 exists fun) || (f2 exists fun)
		case Or(f1, f2) => (f1 exists fun) || (f2 exists fun)
		case Impl(f1, f2) => (f1 exists fun) || (f2 exists fun)
		case Iff(f1, f2) => (f1 exists fun) || (f2 exists fun)
		case Forall(v, f) => f exists fun
		case Exists(v, f) => f exists fun
	}

	def forall(fun: T => Boolean): Boolean = this match {
		case True1() => true
		case False1() => true
		case Unknown() => true
		case Atom(a) => fun(a)
		case Not(f) => f forall fun
		case And(f1, f2) => (f1 forall  fun) && (f2 forall  fun)
		case Or(f1, f2) => (f1 forall  fun) && (f2 forall  fun)
		case Impl(f1, f2) => (f1 forall  fun) && (f2 forall  fun)
		case Iff(f1, f2) => (f1 forall  fun) && (f2 forall  fun)
		case Forall(v, f) => f forall  fun
		case Exists(v, f) => f forall  fun
	}
}