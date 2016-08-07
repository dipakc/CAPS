package progsynth.types

import progsynth.methodspecs.InterpretedFns._


trait TermBoolUtils { self: TermBool =>
	def inv(rv: Var): InvariantT = InvariantT(None, TermBoolUtils.this, Some(rv))
	def inv(): InvariantT = InvariantT(None, TermBoolUtils.this, None)

	/** Is this function required */
	def applyRec(fun: PartialFunction[TermBool, TermBool]): TermBool = {
		val TrueT = TermBool.TrueT
		val FalseT = TermBool.FalseT

		if (fun.isDefinedAt(TermBoolUtils.this))
			fun(TermBoolUtils.this)
		else TermBoolUtils.this match { //TODO: fix the warning here.
			case TrueT => TermBoolUtils.this
			case FalseT => TermBoolUtils.this
			case _ : UnkTerm => TermBoolUtils.this
			case NotTermBool(t) => NotTermBool(t applyRec fun)
			case AndTermBool(t1, t2) => AndTermBool(t1 applyRec fun, t2 applyRec fun)
			case OrTermBool(t1, t2) =>  OrTermBool(t1 applyRec fun, t2 applyRec fun)
			case ImplTermBool(t1, t2) => ImplTermBool(t1 applyRec fun, t2 applyRec fun)
			case RImplTermBool(t1, t2) => RImplTermBool(t1 applyRec fun, t2 applyRec fun)
			case EqEqEqTermBool(t1, t2) => EqEqEqTermBool(t1 applyRec fun, t2 applyRec fun)
			case QTermBool(opr, dummies, range, term) => QTermBool(opr, dummies, range applyRec fun, term applyRec fun)
			//case Formula => throw new RuntimeException
		}
}

	def simplify(): TermBool = {
		val TrueT = TermBool.TrueT
		val FalseT = TermBool.FalseT
		TermBoolUtils.this match {
			case AndTermBool(t1, t2) =>
				val t1s = t1.simplify
				val t2s = t2.simplify

				(t1s, t2s) match {
					case (TrueT, t) => t
					case (t, TrueT) => t
					case (FalseT, _) => FalseT
					case (_, FalseT) => FalseT
					case _ => AndTermBool(t1s, t2s)
				}
			case OrTermBool(t1, t2) =>
				val t1s = t1.simplify
				val t2s = t2.simplify
				(t1s, t2s) match {
					case (TrueT, _) => TrueT
					case (_, TrueT) => TrueT
					case (FalseT, t) => t
					case (t, FalseT) => t
					case _ => OrTermBool(t1s, t2s)
				}

			case NotTermBool(t) =>
				val ts = t.simplify
				(ts) match {
					case TrueT => FalseT
					case FalseT => TrueT
					case _ => NotTermBool(ts)
				}

			case ImplTermBool(t1, t2) =>
				val t1s = t1.simplify
				val t2s = t2.simplify
				(t1s, t2s) match {
					case (TrueT, t) => t
					case (t, TrueT) => TrueT
					case (FalseT, _) => TrueT
					case (t, FalseT) => NotTermBool(t)
					case _ => ImplTermBool(t1s, t2s)
				}

			case RImplTermBool(t1, t2) =>
				val t1s = t1.simplify
				val t2s = t2.simplify
				(t1s, t2s) match {
					case (t, TrueT) => t
					case (TrueT, t) => TrueT
					case (_, FalseT) => TrueT
					case (FalseT, t) => NotTermBool(t)
					case _ => RImplTermBool(t1s, t2s)
				}

			case EqEqEqTermBool(t1, t2) =>
				val t1s = t1.simplify
				val t2s = t2.simplify
				(t1s, t2s) match {
					case (TrueT, t) => t
					case (t, TrueT) => t
					case (FalseT, t) => NotTermBool(t)
					case (t, FalseT) => NotTermBool(t)
					case _ => EqEqEqTermBool(t1s, t2s)
				}
			case QTermBool(afn, dummies, range, term) =>
				QTermBool(afn, dummies, range.simplify, term.simplify)
			case _ => TermBoolUtils.this
		}
	}

	def getTopLevelConjuncts(): List[TermBool] = {
		TermBoolUtils.this match {
			case AndTermBool(t1, t2) => t1.getTopLevelConjuncts ++ t2.getTopLevelConjuncts
			case _: UnkTerm => Nil
			case _ => List(TermBoolUtils.this)
		}
	}

	def getTopLevelDisjuncts(): List[TermBool] = {
		TermBoolUtils.this match {
			case OrTermBool(t1, t2) => t1.getTopLevelDisjuncts ++ t2.getTopLevelDisjuncts
			case _: UnkTerm => Nil
			case _ => List(TermBoolUtils.this)
		}
	}

	def getTopLevelEqEqEqs(): List[TermBool] = {
		TermBoolUtils.this match {
			case EqEqEqTermBool(t1, t2) => t1.getTopLevelEqEqEqs ++ t2.getTopLevelEqEqEqs
			case _: UnkTerm => Nil
			case _ => List(TermBoolUtils.this)
		}
	}
	/*
	 * a <= b
	 * a = b OR a < b
	 * a = b OR a + 1 <= b
	 * a = b OR a <= b - 1
	 *
	 * a >= b
	 * a = b OR a > b
	 * a = b OR a >= b + 1
	 * a = b OR a - 1 >= b
	 *
	 * a <= c + 1
	 * a = c + 1 OR a < c + 1
	 * a = c + 1 OR a <= c
	 *
	 */

	/**
	 * a <= b
	 * a < b OR a = b
	 */
	def splitOutEquality(aTerm: TermBool): Option[TermBool] = aTerm match {
		case LETermBool(t1, t2) =>
			val rt1 = LTTermBool(t1, t2 )
			val rt2 = EqEqTermBool(t1, t2)
			val rt = OrTermBool(rt1, rt2)
			Some(rt)
		case GETermBool(t1, t2@ FnAppInt(PlusIntFn, t3 :: ConstInt("1") :: Nil)) =>
			val rt1 = GTTermBool(t1, t2 )
			val rt2 = EqEqTermBool(t1, t2)
			val rt = OrTermBool(rt1, rt2)
			Some(rt)
		case _ => None
	}

	def distribute(aTerm: Term) : Option[Term] = aTerm match {
		case FnApp(aFn1, t1 :: FnApp(aFn2, t2s)) if distributesOver(aFn1, aFn2)=>
			val terms = t2s.map(t2 => FnApp(aFn1, t1 :: t2 :: Nil))
			val retVal = terms.reduce((a, b) => FnApp(aFn2, a :: b :: Nil))
			Some(retVal)
		case FnApp(aFn1, t1 :: QTerm(aFn2, dummies, range, term))
			if distributesOver(aFn2, aFn2) && t1.getFreeVars.intersect(dummies).isEmpty && isNonEmpty(range) => {
				val newTerm = FnApp(aFn1, List(t1, term))
				Some(QTerm.mkQTerm(aFn2, dummies, range, newTerm))
			}
		case _ => None
	}

}
