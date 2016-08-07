package progsynth.types

import progsynth.methodspecs.InterpretedFns._

trait PlusTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(PlusIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(PlusIntFn)
}

object PlusTermInt extends PlusTermIntTrait
object PLUS extends PlusTermIntTrait

trait MinusTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(MinusIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(MinusIntFn)
}

object MinusTermInt extends MinusTermIntTrait
object MINUS extends MinusTermIntTrait

trait TimesTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(TimesIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(TimesIntFn)
}

object TimesTermInt extends TimesTermIntTrait
object TIMES extends TimesTermIntTrait

trait DivTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(DivIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(DivIntFn)
}

object DivTermInt extends DivTermIntTrait
object DIV extends DivTermIntTrait

trait ModTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(PercentIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(PercentIntFn)
}

object ModTermInt extends ModTermIntTrait
object MOD extends ModTermIntTrait

trait UMinusTermIntTrait extends TermIntCostructorUtils {
	def apply(t: TermInt): TermInt= applyUnary(t)(UnaryMinusIntFn)
	def unapply(t: TermInt): Option[TermInt] = unapplyUnary(t)(UnaryMinusIntFn)
}

object UMinusTermInt extends UMinusTermIntTrait
object UMINUS extends UMinusTermIntTrait

trait MinTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(MinIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(MinIntFn)
}

object MinTermInt extends MinTermIntTrait
object MIN extends MinTermIntTrait

trait MaxTermIntTrait extends TermIntCostructorUtils {
	def apply(t1: TermInt, t2: TermInt): TermInt= applyBinary(t1, t2)(MaxIntFn)
	def unapply(t: TermInt): Option[(TermInt, TermInt)] = unapplyBinary(t)(MaxIntFn)
}

object MaxTermInt extends MaxTermIntTrait
object MAX extends MaxTermIntTrait


trait MinQTermIntTrait {
	def apply(dummies: List[Var], range: TermBool, term: TermInt): QTermInt = {
		QTermInt(MinIntFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermInt): QTermInt = {
		QTermInt(MinIntFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermInt): QTermInt = {
		QTermInt(MinIntFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermInt): QTermInt = {
		QTermInt(MinIntFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(ti: TermInt): Option[(List[Var], TermBool, TermInt)] = {
		ti match {
			case QTermInt(MinIntFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}
object MinQTermInt extends MinQTermIntTrait
object MINQ extends MinQTermIntTrait

trait MaxQTermIntTrait {
	def apply(dummies: List[Var], range: TermBool, term: TermInt): QTermInt = {
		QTermInt(MaxIntFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermInt): QTermInt = {
		QTermInt(MaxIntFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermInt): QTermInt = {
		QTermInt(MaxIntFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermInt): QTermInt = {
		QTermInt(MaxIntFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(ti: TermInt): Option[(List[Var], TermBool, TermInt)] = {
		ti match {
			case QTermInt(MaxIntFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}
object MaxQTermInt extends MaxQTermIntTrait
object MAXQ extends MaxQTermIntTrait

trait PlusQTermIntTrait {
	def apply(dummies: List[Var], range: TermBool, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(ti: TermInt): Option[(List[Var], TermBool, TermInt)] = {
		ti match {
			case QTermInt(PlusIntFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}
object PlusQTermInt extends PlusQTermIntTrait
object PLUSQ extends PlusQTermIntTrait

/*
trait CountQTermIntTrait {
    //TODO:
	def apply(dummies: List[Var], range: TermBool, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermInt): QTermInt = {
		QTermInt(PlusIntFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(ti: TermInt): Option[(List[Var], TermBool, TermInt)] = {
		ti match {
			case QTermInt(PlusIntFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}
object CountQTermInt extends CountQTermIntTrait
object COUNTQ extends CountQTermIntTrait
*/

trait TimesQTermIntTrait {
	def apply(dummies: List[Var], range: TermBool, term: TermInt): QTermInt = {
		QTermInt(TimesIntFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermInt): QTermInt = {
		QTermInt(TimesIntFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermInt): QTermInt = {
		QTermInt(TimesIntFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermInt): QTermInt = {
		QTermInt(TimesIntFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(ti: TermInt): Option[(List[Var], TermBool, TermInt)] = {
		ti match {
			case QTermInt(TimesIntFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}

object TimesQTermInt extends TimesQTermIntTrait
object TIMESQ extends TimesQTermIntTrait

trait TermIntCostructorUtils {
	def applyBinary(tb1: TermInt, tb2: TermInt)(aFn: Fn): TermInt= {
		FnAppInt(aFn, tb1 :: tb2 :: Nil)
	}
	def unapplyBinary(tb: TermInt)(aFn: Fn): Option[(TermInt, TermInt)] = {
		tb match {
			case FnAppInt(`aFn`, t1 :: t2 :: Nil) if ((t1.getType == PSInt) && (t2.getType == PSInt) ) =>
					Some((t1.asInstanceOf[TermInt], t2.asInstanceOf[TermInt]))
			case _ => None
		}
	}

	def applyUnary(t: TermInt)(aFn: Fn): TermInt= {
		FnAppInt(aFn, t :: Nil)
	}

	def unapplyUnary(t: TermInt)(aFn: Fn): Option[TermInt] = {
		t match {
			case FnAppInt(`aFn`, t :: Nil) if (t.getType == PSInt ) =>
					Some(t.asInstanceOf[TermInt])
			case _ => None
		}
	}

}
