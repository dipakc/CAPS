package progsynth.types

import progsynth.methodspecs.InterpretedFns._


trait TermUtilsBoolStatic {
	def mkConjunct(ts: List[TermBool]): TermBool = {
		ts match {
			case Nil => TrueT
			case t :: Nil => t
			case t :: tail => t && mkConjunct(tail)
		}
	}
	def mkDisjunct(ts: List[TermBool]): TermBool = {
		ts match {
			case Nil => FalseT
			case t :: Nil => t
			case t :: tail => t || mkDisjunct(tail)
		}
	}

	val TrueT = ConstBool("true")
	val FalseT = ConstBool("false")

}
