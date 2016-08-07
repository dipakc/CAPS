package models.mqprinter
import progsynth.methodspecs.InterpretedFns._
import progsynth.types.Fn

object MQSym {
	def isPrefixWithSym(fn: Fn) = {
		val list = List(UnaryMinusIntFn, NegBoolFn)
		list contains fn
	}

	def getSym(fn: Fn) = fn match {
		case UnaryMinusIntFn => "-"
		case TimesIntFn => """\ast"""
		case DivIntFn => """\slash"""
		case PercentIntFn => """\%"""
		case MinusIntFn => """-"""
		case PlusIntFn => """+"""
		case MinIntFn => """min"""
		case MaxIntFn => """max"""
		case PowIntFn => """\pow"""
		case NegBoolFn => """\neg"""
		case AndBoolFn => """\wedge"""
		case OrBoolFn => """\vee"""
		case ImplBoolFn => """\Rightarrow"""
		case RImplBoolFn => """\Leftarrow"""
		case EquivBoolFn => """\equiv"""
		case GTBoolFn => """>"""
		case LTBoolFn => """<"""
		case GEBoolFn => """\ge"""
		case LEBoolFn => """\le"""
		case EqEqBoolFn => """="""
		case _ => fn.name
	}

	def getQSym(fn: Fn) = fn match {
		case AndBoolFn => """\forall"""
		case OrBoolFn => """\exists"""
		case PlusIntFn => """\Sum"""
		case MaxIntFn => """\Max"""
		case MinIntFn => """\Min"""
		case _ => fn.name.toUpperCase()
	}

}
