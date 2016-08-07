package progsynth.provers

import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import expt.PSTimeout.ProcessStatus
import scala.PartialFunction._
import scala.util.control.Breaks._
import Why3AST._
import progsynth.logger.PSLogUtils._
import org.slf4j.LoggerFactory


trait Why3PoToWTerm extends  MkNestedQTerm with Why3TypeUtils  { self: IWhy3GenQuantEncoder  =>

    private implicit val logger= LoggerFactory.getLogger("disable.progsynth.Why3PoToWTerm ")


	def poToWTerm(po: Term, ctxDummies: List[Var]): WTerm = traceBeginEnd("Why3PoToWTerm.poToWTerm"){
        traceTerm("po", po)
		val ret = po match {
			case aVar @ Var(v) =>
				WSymbol(v)
			case FnApp(f, ts) =>
				po match {
					case fnAppInt: FnAppInt => fnAppIntToWhy3(fnAppInt, ctxDummies)
					case fnAppBool: FnAppBool => fnAppBoolToWhy3(fnAppBool, ctxDummies)
					case _ =>
					    throw new RuntimeException("Only FnAppInt and FnAppBool are supported")
				}
			case TermBool.TrueT =>
			    //List(if (treatAsPred) "true" else "True")
				WTrue()
			case TermBool.FalseT =>
			    //List(if (treatAsPred) "false" else "False")
				WFalse()
			case aConst @ Const(name) =>
				WSymbol(name)

			case ArrSelect(arr, index) =>
				WBrackets(baseTerm = poToWTerm(arr, ctxDummies), indexTerm = poToWTerm(index, ctxDummies))

			case ArrStore(arr: Term, index: Term, value: Term) =>
				throw new RuntimeException("ArrStore to why3 not implemented")

			case ForallTermBool(dummies, range, term) =>
				val bs = dummies map { dummy =>
					WBinder(dummy.v :: Nil, WTypeSymbol(getWhy3Tpe(dummy), Nil))
				}
				WForall(bs = bs, ts = Nil, f = poToWTerm(range impl term, ctxDummies))

			case ExistsTermBool(dummies, range, term) =>
				val bs = dummies map { dummy =>
					WBinder(dummy.v :: Nil, WTypeSymbol(getWhy3Tpe(dummy), Nil))
				}
				WExist(bs = bs, ts = Nil, f = poToWTerm(range && term, ctxDummies))
			case genQ @ QTerm(_, _, _, _) =>
				val genQ2 = mkNestedQTerm(genQ)
			    genQTermToWTerm(genQ2, ctxDummies)
		}
        logger.trace("Return value")
		logger.trace(multiline(ret.str()))
		ret
	}
	////////////////////////////////////////////////////////////////////////////////
	def fnAppIntToWhy3(fnAppInt: FnAppInt, ctxDummies: List[Var]): WTerm = {
		val FnAppInt(f, ts) = fnAppInt
		f match {
			case PlusIntFn => binFnAppStr("+", ts, ctxDummies)
			case MinusIntFn => binFnAppStr("-", ts, ctxDummies)
			case TimesIntFn => binFnAppStr("*", ts, ctxDummies)
			case UnaryMinusIntFn => unaryFnAppStr("-", ts, ctxDummies)
			case PercentIntFn => binFnAppStr("%", ts, ctxDummies)
			case DivIntFn => binFnAppStr("/", ts, ctxDummies)
			case MinIntFn => prefixFnAppStr("min", ts, ctxDummies)
			case MaxIntFn => prefixFnAppStr("max", ts, ctxDummies)
			case PowIntFn => prefixFnAppStr("power", ts, ctxDummies)
			case aFn => prefixFnAppStr(aFn.name, ts, ctxDummies)
		}
	}

	def fnAppBoolToWhy3(fnAppBool: FnAppBool, ctxDummies: List[Var]): WTerm = {
		val FnAppBool(f, ts) = fnAppBool
		val boolArgs = (ts.map(_.getType).forall(_ == PSBool))
		val treatEqEqAsEquivForBoolArgs = false

		f match {
			case AndBoolFn => binFnAppStr("""/\""", ts, ctxDummies: List[Var])
			case OrBoolFn => binFnAppStr("""\/""", ts, ctxDummies: List[Var])
			case NegBoolFn => unaryFnAppStr("not", ts, ctxDummies: List[Var])
			case ImplBoolFn => binFnAppStr("->", ts, ctxDummies: List[Var])
			case RImplBoolFn => binFnAppStr("->", ts.reverse, ctxDummies: List[Var])
			case EquivBoolFn  => binFnAppStr("<->", ts, ctxDummies: List[Var])
			case EqEqBoolFn =>
				if (boolArgs && treatEqEqAsEquivForBoolArgs)
					binFnAppStr("<->", ts, ctxDummies: List[Var])
				else
					binFnAppStr("=", ts, ctxDummies: List[Var])
			case LTBoolFn => binFnAppStr("<", ts, ctxDummies: List[Var])
			case LEBoolFn => binFnAppStr("<=", ts, ctxDummies: List[Var])
			case GTBoolFn => binFnAppStr(">", ts, ctxDummies: List[Var])
			case GEBoolFn => binFnAppStr(">=", ts, ctxDummies: List[Var])
			case _ => throw new RuntimeException("Why3Input: Uninterpreted functions not supported: " + f)
		}
	}

    def binFnAppStr(fnName: String, ts: List[Term], ctxDummies: List[Var]): WTerm =
    	WInfixOp(fnName, poToWTerm(ts(0), ctxDummies), poToWTerm(ts(1), ctxDummies))

    def prefixFnAppStr(fnName: String, ts: List[Term], ctxDummies: List[Var]): WTerm =
        WFnApp(fnName, ts.map(t => poToWTerm(t, ctxDummies)))

    def unaryFnAppStr(fnName: String, ts: List[Term], ctxDummies: List[Var]) =
    	WPrefixOp(fnName, poToWTerm(ts(0), ctxDummies))

    def poToWhy3Str(po: Term, ctxDummies: List[Var]): String = {
    	poToWTerm(po, ctxDummies).str()
    }
}


trait MkNestedQTerm {
	import QTerm._

	def mkNestedQTerm(qterm: QTerm): QTerm = qterm match {
		case QTerm(opr, i :: Nil, _, _) => qterm

		case QTerm(opr, i :: js, range, term) =>
		    val iRange = ExistsTermBool(js, range)
			mkQTerm(opr, i :: Nil, iRange, mkNestedQTerm(mkQTerm(opr, js, range, term)))
	}
}