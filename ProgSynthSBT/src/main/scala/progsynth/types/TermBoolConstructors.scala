package progsynth.types

import progsynth.methodspecs.InterpretedFns._

object AndTermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool): TermBool = applyBinary(tb1, tb2)(AndBoolFn)
	def unapply(tb: TermBool): Option[(TermBool, TermBool)] = unapplyBinary(tb)(AndBoolFn)
}

object AndN2TermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool, tbs: List[TermBool]): TermBool = {
		(tb1:: tb2 :: tbs).reduceLeft((t1, t2) => applyBinary(t1, t2)(AndBoolFn))
	}
	def unapply(tb: TermBool): Option[(TermBool, TermBool, List[TermBool])] = {
		val conjuncts = tb.getTopLevelConjuncts()
		conjuncts match {
			case c1 :: c2 :: cs => Some(c1, c2, cs)
			case _ => None
		}
	}
}

object OrTermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool): TermBool = applyBinary(tb1, tb2)(OrBoolFn)
	def unapply(tb: TermBool): Option[(TermBool, TermBool)] = unapplyBinary(tb)(OrBoolFn)
}

object OrN2TermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool, tbs: List[TermBool]): TermBool = {
		(tb1:: tb2 :: tbs).reduceLeft((t1, t2) => applyBinary(t1, t2)(OrBoolFn))
	}
	def unapply(tb: TermBool): Option[(TermBool, TermBool, List[TermBool])] = {
		val disjuncts = tb.getTopLevelDisjuncts()
		disjuncts match {
			case d1 :: d2 :: ds => Some(d1, d2, ds)
			case _ => None
		}
	}
}

object ImplTermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool): TermBool = applyBinary(tb1, tb2)(ImplBoolFn)
	def unapply(tb: TermBool): Option[(TermBool, TermBool)] = unapplyBinary(tb)(ImplBoolFn)
}

object RImplTermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool): TermBool = applyBinary(tb1, tb2)(RImplBoolFn)
	def unapply(tb: TermBool): Option[(TermBool, TermBool)] = unapplyBinary(tb)(RImplBoolFn)
}

object EqEqEqTermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool): TermBool = applyBinary(tb1, tb2)(EquivBoolFn)
	def unapply(tb: TermBool): Option[(TermBool, TermBool)] = unapplyBinary(tb)(EquivBoolFn)
}

object EqEqEqN2TermBool extends TermBoolCostructorUtils {
	def apply(tb1: TermBool, tb2: TermBool, tbs: List[TermBool]): TermBool = {
		(tb1:: tb2 :: tbs).reduceLeft((t1, t2) => applyBinary(t1, t2)(EquivBoolFn))
	}
	def unapply(tb: TermBool): Option[List[TermBool]] = {
		val equivs= tb.getTopLevelEqEqEqs()
		if(equivs.length >= 2)
			Some(equivs)
		else
			None
	}
}

object NotTermBool extends {
	def apply(tb: TermBool): TermBool = {
		FnAppBool(NegBoolFn, tb :: Nil)
	}

	def unapply(tb: TermBool): Option[TermBool] = {
		tb match {
			case FnAppBool(NegBoolFn, t :: Nil) if(t.getType == PSBool) =>
				Some(t.asInstanceOf[TermBool])
			case _ => None
		}
	}
}

object ForallTermBool extends {
	def apply(dummies: List[Var], range: TermBool, term: TermBool): QTermBool = {
		QTermBool(AndBoolFn, dummies, range, term)
	}

	def apply(dummy: Var, range: TermBool, term: TermBool): QTermBool = {
		QTermBool(AndBoolFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermBool): QTermBool = {
		QTermBool(AndBoolFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermBool): QTermBool = {
		QTermBool(AndBoolFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(tb: TermBool): Option[(List[Var], TermBool, TermBool)] = {
		tb match {
			case QTermBool(AndBoolFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}

object ExistsTermBool extends {
	def apply(dummies: List[Var], range: TermBool, term: TermBool): QTermBool = {
		QTermBool(OrBoolFn, dummies, range, term)
	}
	def apply(dummy: Var, range: TermBool, term: TermBool): QTermBool = {
		QTermBool(OrBoolFn, dummy :: Nil, range, term)
	}

	def apply(dummies: List[Var], term: TermBool): QTermBool = {
		QTermBool(OrBoolFn, dummies, TermBool.TrueT, term)
	}

	def apply(dummy: Var, term: TermBool): QTermBool = {
		QTermBool(OrBoolFn, dummy :: Nil, TermBool.TrueT, term)
	}

	def unapply(tb: TermBool): Option[(List[Var], TermBool, TermBool)] = {
		tb match {
			case QTermBool(OrBoolFn, dummies, range, term) =>
				Some((dummies, range, term))
			case _ => None
		}
	}
}

trait LTTermBoolTrait {
	val opr = LTBoolFn
	def apply(t1: TermInt, t2: TermInt): TermBool = {
		FnAppBool(opr, t1 :: t2 :: Nil)
	}

	def unapply(tb: TermBool): Option[(TermInt, TermInt)] = tb match {
		case FnAppBool(`opr`, List(t1: TermInt, t2: TermInt)) => Some((t1, t2))
		case _ => None
	}
}

object LTTermBool extends LTTermBoolTrait
object LT extends LTTermBoolTrait

trait GTTermBoolTrait {
	val opr = GTBoolFn
	def apply(t1: TermInt, t2: TermInt): TermBool = {
		FnAppBool(opr, t1 :: t2 :: Nil)
	}

	def unapply(tb: TermBool): Option[(TermInt, TermInt)] = tb match {
		case FnAppBool(`opr`, List(t1: TermInt, t2: TermInt)) => Some((t1, t2))
		case _ => None
	}
}
object GTTermBool extends GTTermBoolTrait
object GT extends GTTermBoolTrait

trait LETermBoolTrait {
	val opr = LEBoolFn
	def apply(t1: TermInt, t2: TermInt): TermBool = {
		FnAppBool(opr, t1 :: t2 :: Nil)
	}

	def unapply(tb: TermBool): Option[(TermInt, TermInt)] = tb match {
		case FnAppBool(`opr`, List(t1: TermInt, t2: TermInt)) => Some((t1, t2))
		case _ => None
	}
}
object LETermBool extends LETermBoolTrait
object LE extends LETermBoolTrait

trait GETermBoolTrait {
	val opr = GEBoolFn
	def apply(t1: TermInt, t2: TermInt): TermBool = {
		FnAppBool(opr, t1 :: t2 :: Nil)
	}

	def unapply(tb: TermBool): Option[(TermInt, TermInt)] = tb match {
		case FnAppBool(`opr`, List(t1: TermInt, t2: TermInt)) => Some((t1, t2))
		case _ => None
	}
}
object GETermBool extends GETermBoolTrait
object GE extends GETermBoolTrait

trait EqEqTermBoolTrait {
	val opr = EqEqBoolFn
	def apply(t1: TermInt, t2: TermInt): TermBool = {
		FnAppBool(opr, t1 :: t2 :: Nil)
	}

	def unapply(tb: TermBool): Option[(TermInt, TermInt)] = tb match {
		case FnAppBool(`opr`, List(t1: TermInt, t2: TermInt)) => Some((t1, t2))
		case _ => None
	}
}
object EqEqTermBool extends EqEqTermBoolTrait
object EqEq extends EqEqTermBoolTrait

trait TermBoolCostructorUtils {
	def applyBinary(tb1: TermBool, tb2: TermBool)(aFn: Fn): TermBool = {
		FnAppBool(aFn, tb1 :: tb2 :: Nil)
	}
	def unapplyBinary(tb: TermBool)(aFn: Fn): Option[(TermBool, TermBool)] = {
		tb match {
			case FnAppBool(`aFn`, t1 :: t2 :: Nil) if ((t1.getType == PSBool) && (t2.getType == PSBool) ) =>
					Some((t1.asInstanceOf[TermBool], t2.asInstanceOf[TermBool]))
			case _ => None
		}
	}

}

