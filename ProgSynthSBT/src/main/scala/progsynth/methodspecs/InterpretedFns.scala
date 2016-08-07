package progsynth.methodspecs
import scala.collection.mutable.HashMap
import progsynth.types._
import progsynth.types.Types._
import scala.xml._
import progsynth.types.PSType._

object InterpretedFns extends FnUtils with InitFnRepository{
	private val fns = new HashMap[String, Fn]
	private val fnsSymbolMap = new HashMap[Fn, String]
	private var infixFnList: List[Fn]  = Nil

	init()

	def addFn(aFn: Fn, symbol: String, isInfix: Boolean) = {
		fns += aFn.name-> aFn
		fnsSymbolMap += aFn -> symbol
		infixFnList = if (isInfix) aFn :: infixFnList else infixFnList
	}

	def getFn(name: String): Option[Fn] = fns.get(name)

	def getTextSymbol(aFn: Fn): Option[String] = fnsSymbolMap.get(aFn)

	def isInfix(aFn: Fn): Boolean = infixFnList contains aFn

	def contains(aFn: Fn): Boolean = getFn(aFn.name) == Some(aFn)

	def min(x: TermInt, y: TermInt) = x min y
	def max(x: TermInt, y: TermInt) = x max y
	def pow(x: TermInt, y: TermInt) = x pow y

	def btoi (x: TermBool): TermInt = iteInt(x, ConstInt("1"), ConstInt("0"))

	def iteInt(x: TermBool, p: TermInt, q: TermInt): TermInt = FnAppInt(ITEIntFn, List(x, p, q))

	// Int * Int => Int
	val PlusIntFn = fns("""$plus""")
	val MinusIntFn = fns("""$minus""")
	val TimesIntFn = fns("""$times""")
	val UnaryMinusIntFn = fns("""unary_$minus""")
	val PercentIntFn = fns("""$percent""")
	val DivIntFn = fns("""$div""")
	val MinIntFn = fns("""$min""")
	val MaxIntFn = fns("""$max""")
	val PowIntFn = fns("""$pow""")
	//Term * Term => Bool
	val EqEqBoolFn = fns("""$eq$eq""")

	//Bool * Bool => Bool
	val AndBoolFn = fns("""$amp$amp""")
	val OrBoolFn = fns("""$pipe$pipe""")
	val NegBoolFn = fns("""$bang""")
	val ImplBoolFn = fns("""impl""")
	val RImplBoolFn = fns("""rimpl""")
	val EquivBoolFn = fns("""$equiv""")

	//Int * Int => Bool
	val GTBoolFn = fns("""$greater""")
	val LTBoolFn = fns("""$less""")
	val GEBoolFn = fns("""$greater$eq""")
	val LEBoolFn = fns("""$less$eq""")

	//Bool * Int * Int => Int
	val ITEIntFn = fns("""$ite_int""")

	//------------------------------------------------------------
	def getHtmlFnSym(afn: Fn): Unparsed = afn match {
		case EqEqBoolFn => Unparsed("""=""")

		case PlusIntFn => Unparsed("""&#43;""")
		case TimesIntFn => Unparsed("""&#42;""")
		case PercentIntFn => Unparsed("""&#37;""")
		case MinusIntFn => Unparsed("""&minus;""")
		case UnaryMinusIntFn => Unparsed("""&minus;""")
		case DivIntFn => Unparsed("""&#47;""")
		case MinIntFn => Unparsed("""min""")
		case MaxIntFn => Unparsed("""max""")
		case PowIntFn => Unparsed("""pow""")

		case AndBoolFn => Unparsed("""&and;""")
		case OrBoolFn => Unparsed("""&or;""")
		case NegBoolFn => Unparsed("""&not;""")
		case ImplBoolFn => Unparsed("""&rArr;""")
		case RImplBoolFn => Unparsed("""&lArr;""")
		case EquivBoolFn => Unparsed("""&equiv;""")

		case GTBoolFn => Unparsed(""">""")
		case LTBoolFn => Unparsed("""<""")
		case GEBoolFn => Unparsed("""&ge;""")
		case LEBoolFn => Unparsed("""&le;""")

		case _ => Unparsed(afn.name)
	}

	def getHtmlQuantFnSym(afn: Fn): Unparsed = afn match {
		case AndBoolFn => Unparsed("""&forall;""")
		case OrBoolFn => Unparsed("""&exist;""")
		case MinIntFn => Unparsed("""<b>Min</b>&nbsp;""")
		case MaxIntFn => Unparsed("""<b>Max</b>&nbsp;""")
		case PlusIntFn => Unparsed("""<b>&sum;</b>&nbsp;""")
		case TimesIntFn => Unparsed("""<b>Prod</b>&nbsp;""")
		case _ => Unparsed(afn.name)
	}

	def getMQFnSym(afn: Fn): Unparsed = afn match {
		case EqEqBoolFn => Unparsed("""=""")

		case PlusIntFn => Unparsed("""+""")
		case TimesIntFn => Unparsed("""*""")
		case PercentIntFn => Unparsed("""%""")
		case MinusIntFn => Unparsed("""-""")
		case UnaryMinusIntFn => Unparsed("""-""")
		case DivIntFn =>  Unparsed("""/""")

		case AndBoolFn => Unparsed("""\and""")
		case OrBoolFn => Unparsed("""\or""")
		case NegBoolFn => Unparsed("""\neg""")
		case ImplBoolFn => Unparsed("""\implies""")
		case RImplBoolFn => Unparsed("""\Leftarrow""") //TODO: implement \rimplies in mathquill
		case EquivBoolFn => Unparsed("""\equiv""")

		case GTBoolFn => Unparsed(""">""")
		case LTBoolFn => Unparsed("""<""")
		case GEBoolFn => Unparsed("""\ge""")
		case LEBoolFn => Unparsed("""\le""")

		case _ => Unparsed(afn.name)
	}

	def getMQQuantFnSym(afn: Fn): Unparsed = afn match {
		case AndBoolFn => Unparsed("""\forall""")
		case OrBoolFn => Unparsed("""\exists""")
		case _ => Unparsed(afn.name)
	}

	def getUnit (fn: Fn): Option[Term] = fn match {
		case EqEqBoolFn => None

		case PlusIntFn => Some(ConstInt("0"))
		case TimesIntFn => Some(ConstInt("1"))
		case PercentIntFn => None
		case MinusIntFn => None
		case UnaryMinusIntFn => None
		case DivIntFn => None

		case AndBoolFn => Some(TermBool.TrueT)
		case OrBoolFn => Some(TermBool.FalseT)
		case NegBoolFn => None
		case ImplBoolFn => None
		case RImplBoolFn => None
		case EquivBoolFn => None

		case GTBoolFn => None
		case LTBoolFn => None
		case GEBoolFn => None
		case LEBoolFn => None
		case _ => None
	}

	def distributesOver(aFn1: Fn, aFn2: Fn) = (aFn1, aFn2) match {
		case (AndBoolFn, OrBoolFn) => true
		case (OrBoolFn, AndBoolFn) => true
		case _ => false
	}

	//------------------------------------------------------------

	def getBinding(aFn: Fn) = aFn match {
	    case PlusIntFn => 3
	    case MinusIntFn => 3
	    case TimesIntFn => 4
	    case DivIntFn => 4
	    case PercentIntFn => 4
	    case UnaryMinusIntFn => 5
	    case _ => 10
	}

	val maxBinding = 10
	val leastBinding = 3

}

trait InitFnRepository {
	protected def init() {
		//int * int -> int
		InterpretedFns.addFn(Fn("""$plus""", List(PSInt, PSInt), PSInt), symbol="+", isInfix = true)
		InterpretedFns.addFn(Fn("""$minus""", List(PSInt, PSInt), PSInt), symbol="-", isInfix = true)
		InterpretedFns.addFn(Fn("""$times""", List(PSInt, PSInt), PSInt), symbol="*", isInfix = true)
		InterpretedFns.addFn(Fn("""unary_$minus""", List(PSInt), PSInt), symbol="-", isInfix = false)
		InterpretedFns.addFn(Fn("""$percent""", List(PSInt, PSInt), PSInt), symbol="%", isInfix = true)
		InterpretedFns.addFn(Fn("""$div""", List(PSInt, PSInt), PSInt), symbol="/", isInfix = true)
		InterpretedFns.addFn(Fn("""$min""", List(PSInt, PSInt), PSInt), symbol="min", isInfix = true)
		InterpretedFns.addFn(Fn("""$max""", List(PSInt, PSInt), PSInt), symbol="max", isInfix = true)
		InterpretedFns.addFn(Fn("""$pow""", List(PSInt, PSInt), PSInt), symbol="pow", isInfix = false)

		//term * term -> bool
		InterpretedFns.addFn(Fn("""$eq$eq""", List(PSAny, PSAny), PSBool), symbol="==", isInfix = true)

		//bool* bool-> bool
		InterpretedFns.addFn(Fn("""$amp$amp""", List(PSBool, PSBool), PSBool), symbol="&&", isInfix = true)
		InterpretedFns.addFn(Fn("""$pipe$pipe""", List(PSBool, PSBool), PSBool), symbol="||", isInfix = true)
		InterpretedFns.addFn(Fn("""$bang""", List(PSBool), PSBool), symbol="!", isInfix = false)
		InterpretedFns.addFn(Fn("""impl""", List(PSBool, PSBool), PSBool), symbol="impl", isInfix = true)
		InterpretedFns.addFn(Fn("""rimpl""", List(PSBool, PSBool), PSBool), symbol="rimpl", isInfix = true)
		InterpretedFns.addFn(Fn("""$equiv""", List(PSBool, PSBool), PSBool), symbol="===", isInfix = true)

		//int * int -> bool
		InterpretedFns.addFn(Fn("""$greater""", List(PSInt, PSInt), PSBool), symbol=">", isInfix = true)
		InterpretedFns.addFn(Fn("""$less""", List(PSInt, PSInt), PSBool), symbol="<", isInfix = true)
		InterpretedFns.addFn(Fn("""$greater$eq""", List(PSInt, PSInt), PSBool), symbol=">=", isInfix = true)
		InterpretedFns.addFn(Fn("""$less$eq""", List(PSInt, PSInt), PSBool), symbol="<=", isInfix = true)

		InterpretedFns.addFn(Fn("""$ite_int""", List(PSBool, PSInt, PSInt), PSInt), symbol="ITE", isInfix = false)
	}
}

trait FnUtils {
	/**
	 *  Case1: "name" already exists in repository. Retrieve the function from the repository.
	 *  The retrieved function must match the given signature.
	 *  Case2: "name" not there in the repository. Create a new "Fn" instance.
	 */
	def mkFn(name: String, argTpes: List[PSType], tpe: PSType): Option[Fn] = {
		//Predefined Function
		val preFnOpt = InterpretedFns.getFn(name)
		preFnOpt match {
			case Some(preFn) =>
				if ( (preFn.argTpes.sameElements(argTpes)) && preFn.tpe == tpe )
					Some(preFn)
				else None
			case None =>
				Some(Fn(name, argTpes, tpe))
		}
	}
	def mkFn(name: String, argTpes: List[PSType], tpeStr: String): Option[Fn] = {
		val tpeOpt = getPSType(tpeStr)
		tpeOpt flatMap { tpe =>
			mkFn(name, argTpes, tpe)
		}
	}

	def mkFn(fname: String)(vars: List[Var])(retType: PSType): Fn = {
		Fn(fname, vars.map{_.getType}, retType)
	}
}

