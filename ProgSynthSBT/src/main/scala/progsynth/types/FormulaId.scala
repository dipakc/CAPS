package progsynth.types

trait FormulaFId[T] { self: Formula[T] =>
	var fid = -1
	def setFId(nid: Int): this.type = { fid = nid; self }
	def toStringId(): String = {
		val idPrefix = fid.toString + "@"
		val retVal = self match {
			case True1() => "True"
			case False1() => "False"
			case Atom(pred) => <a>Atom({pred.toString})</a>.text
			case Not(f) => <a>Not({f.toStringId})</a>.text
			case And(f1, f2) => <a>And({f1.toStringId}, {f2.toStringId})</a>.text
			case Or(f1, f2) => <a>Or({f1.toStringId}, {f2.toStringId})</a>.text
			case Impl(f1, f2) => <a>Impl({f1.toStringId}, {f2.toStringId})</a>.text
			case Iff(f1, f2) => <a>Iff({f1.toStringId}, {f2.toStringId})</a>.text
			case Forall(v, f) => <a>Forall({v}, {f.toStringId})</a>.text
			case Exists(v, f) => <a>Exists({v}, {f.toStringId})</a>.text
			case Unknown() => <a>Unknown()</a>.text
		}
		idPrefix + retVal
	}

	/** Numbers each node in the formula in post-order fashion.
	 * Use toStringId to see the ids.
	 * input : -1@Exists(v, -1@And(-1@Atom(Pred(a,List())), -1@Atom(Pred(b,List()))))
	 * output: 4@Exists(v, 3@And(1@Atom(Pred(a,List())), 2@Atom(Pred(b,List()))))
	 */
	def setFIdAll(): this.type = {
		var fid = 0
		def newFId = { fid = fid + 1; fid }
		def setFIdi(f: Formula[T]):Formula[T] = f match {
			case True1() => True1(newFId)
			case False1() => False1(newFId)
			case Atom(pred) => Atom(pred, newFId)
			case Not(f) => Not(setFIdi(f), newFId)
			case And(f1, f2) => And(setFIdi(f1), setFIdi(f2), newFId)
			case Or(f1, f2) => Or(setFIdi(f1), setFIdi(f2), newFId)
			case Impl(f1, f2) => Impl(setFIdi(f1), setFIdi(f2), newFId)
			case Iff(f1, f2) => Iff(setFIdi(f1), setFIdi(f2), newFId)
			case Forall(v, f) => Forall(v, setFIdi(f), newFId)
			case Exists(v, f) => Exists(v, setFIdi(f), newFId)
			case Unknown() => Unknown(newFId)
		}

		setFIdi(this).asInstanceOf[this.type]//TODO: avoid asInstanceOf
	}

}

// Auxiliary constructors that take id as extra parameter
// Companion objects extend the following traits

trait True1IdCtr {
	def apply[T](fid: Int): True1[T] = True1[T]().setFId(fid)
}

trait False1IdCtr {
	def apply[T](fid: Int): False1[T] = False1[T]().setFId(fid)
}

trait AtomIdCtr {
	def apply[T](a: T, fid: Int):Atom[T]  = Atom[T](a).setFId(fid)
}

trait NotIdCtr {
	def apply[T](f: Formula[T], fid: Int ): Not[T] = Not[T](f).setFId(fid)
}

trait AndIdCtr {
	def apply[T](f1: Formula[T], f2: Formula[T], fid: Int): And[T] = And[T](f1, f2).setFId(fid)
}

trait OrIdCtr {
	def apply[T](f1: Formula[T], f2: Formula[T], fid: Int): Or[T] = Or[T](f1, f2).setFId(fid)
}

trait ImplIdCtr {
	def apply[T](f1: Formula[T], f2: Formula[T], fid: Int): Impl[T] = Impl[T](f1, f2).setFId(fid)
}

trait IffIdCtr {
	def apply[T](f1: Formula[T], f2: Formula[T], fid: Int): Iff[T] =  Iff[T](f1, f2).setFId(fid)
}

trait ForallIdCtr {
	def apply[T](v: Var, f: Formula[T], fid: Int): Forall[T] = Forall[T](v, f).setFId(fid)
}

trait ExistsIdCtr {
	def apply[T](v: Var, f: Formula[T], fid: Int): Exists[T] = Exists[T](v, f).setFId(fid)
}

trait UnknownIdCtr {
	def apply[T](fid: Int): Unknown[T] = Unknown[T]().setFId(fid)
}
