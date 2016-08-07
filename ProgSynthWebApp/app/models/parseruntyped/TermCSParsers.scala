package models.parseruntyped
import progsynth.types._
import progsynth.synthesisold.ProgContext
import progsynth.methodspecs.InterpretedFns._
import scala.util.control.Breaks._
import scala.collection.mutable.ListBuffer
import progsynth.utils.PSUtils
import progsynth.utils.RichTryT._
import scala.util.parsing.combinator._
import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._

// ./docs/Parsers.html
trait TermCSParsers extends TermTParsers with TermValidator with PSUtils with PSWebParserUtils {

	// Why not strengthen termTP: docs/Parsers.html/#orcfg
	// Why not termArrayCSP | termBasicCSP: docs/Parsers.html/#PrefixParser
	def termCSP(implicit progCtx: ProgContext):Parser[Term] =
		termBasicCSP | termArrayCSP

	def termBasicCSP(implicit progCtx: ProgContext):Parser[Term] =
		strengthen(termBasicTP, getBasicTerm, "Can not parse term. Failure in type checking: ")

	def termIntCSP(implicit progCtx: ProgContext):Parser[TermInt] =
		strengthen(termBasicTP, getTermInt, "Can not parse term. Failure in type checking: ")

	def termBoolCSP(implicit progCtx: ProgContext):Parser[TermBool] =
		strengthen(termBasicTP, getTermBool, "Can not parse term. Failure in type checking: ")

	def termArrayCSP(implicit progCtx: ProgContext):Parser[Term] =
		strengthen(termArrayTP, getArrayTerm, "Can not parse term. Failure in type checking: ")

	def termArrayIntCSP(implicit progCtx: ProgContext):Parser[TermArrayInt] =
		strengthen(termArrayTP, getTermArrayInt, "Can not parse term. Failure in type checking: ")

	def termArrayBoolCSP(implicit progCtx: ProgContext):Parser[TermArrayBool] =
		strengthen(termArrayTP, getTermArrayBool, "Can not parse term. Failure in type checking: ")

	def newVarCSP(implicit progCtx: ProgContext): Parser[Var] =
		strengthen(newVarTP, getNewVar, "Can not parse new variable. ")

}

trait TermValidator { self: PSUtils =>

//	object X {
//		val loggerV= LoggerFactory.getLogger("progsynth.TermValidator")
//	}
//
//	import X.loggerV

	//Making logger private results in runtime error: AbstractMethodError !!
	val logger= LoggerFactory.getLogger("progsynth.TermValidator")

	/** Return a new variable. If a variable of same name exists in the program context, return None*/
	def getNewVar(aNewVarT: NewVarT)(implicit progCtx: ProgContext): Option[Var] = {
		val (varName, varTpe) = (aNewVarT.v, aNewVarT.tpe)

		val isInCtx: Boolean = progCtx.contains(varName)(includeDummies = false)
		if(! isInCtx){
			varTpe match {
				case "Int" => Some(VarInt(varName))
				case "Bool" => Some(VarBool(varName))
				case "ArrayInt" => Some(VarArrayInt(varName))
				case "ArrayBool" => Some(VarArrayBool(varName))
				case _ => None
			}
		} else None
	}

// Why not strengthen termTP: docs/Parsers.html/#orcfg
//	def getTerm(result: TermT)(implicit progCtx: ProgContext): Option[Term] = {
//		val res = result match {
//			case  resultArrayT: TermArrayT => getArrayTerm(resultArrayT)
//			case  resultTermT: TermT => getBasicTerm(resultTermT)
//			case _ => None
//		}
//		res
//	}

	def getBasicTerm(result: TermT)(implicit progCtx: ProgContext): Option[Term] = {
		var res: Option[Term] = None
		if (res.isEmpty) res = getTermInt(result)
		if (res.isEmpty) res = getTermBool(result)
		res
	}

	/** Return a fresh int variable. Variable of same name may exist in the program context.*/
	def getFreshIntVar(aVarT: VarT): Var = {
		VarInt(aVarT.v)
	}

	def getTermInt(result: TermT)(implicit progCtx: ProgContext): Option[TermInt] = result match {

		case VarT(v) =>
			progCtx.getVar(v, PSInt)(true) map (_.asInstanceOf[VarInt])

		case ConstT(name, PInt) => Some(ConstInt(name))

		//TODO: removed hard coded function list
		case FnAppT(f, ts) if (List(PlusIntFn, MinusIntFn, TimesIntFn, DivIntFn, PercentIntFn, UnaryMinusIntFn, MaxIntFn, MinIntFn) contains f)=>
			/**For the arithmetic functions, the arguments are of type TermInt*/
			val res = allmap(ts, getTermInt) map (FnAppInt(f, _))
			res

		case ArrSelectT(arr, index)=>
			for {
				arrPS <- getTermArrayInt(arr)
				indexPS <- getTermInt(index)
			} yield ArrSelectInt(arrPS, indexPS)

		case QTermT(opr, dummies, range, term) =>
			val dummiesPS = dummies map getFreshIntVar //TODO: handle non int dummies
			for{
				rangePS <- getTermBool(range)(progCtx.addDummies(dummiesPS))
				termPS <- getTermInt(term)(progCtx.addDummies(dummiesPS))
				if (List(PlusIntFn, TimesIntFn, MaxIntFn) contains opr )
			} yield {
				QTermInt(opr, dummiesPS, rangePS, termPS)
			}

		case CountTermT(dummies, range, term) =>
			val dummiesPS = dummies map getFreshIntVar //TODO: handle non int dummies
			for{
				rangePS <- getTermBool(range)(progCtx.addDummies(dummiesPS))
				termPS <- getTermBool(term)(progCtx.addDummies(dummiesPS))
			} yield {
				QTermInt(PlusIntFn, dummiesPS, rangePS, btoi(termPS))
			}

		case _ => None

	}

	/** Returns Term since there is no type as TermArray */
	def getArrayTerm(result: TermArrayT)(implicit progCtx: ProgContext): Option[Term] = {
		var res: Option[Term] = None
		if (res.isEmpty) {
			res = getTermArrayInt(result)
		}
		if (res.isEmpty) {
			res = getTermArrayBool(result)
		}
		res
	}

	def getTermArrayInt(result: TermT)(implicit progCtx: ProgContext): Option[TermArrayInt] = result match {
		case VarArrayT(v) =>
			progCtx.getVar(v, PSArrayInt)(true) map (_.asInstanceOf[VarArrayInt])
		case ConstArrayT(name, PArrayInt) =>
			Some(ConstArrayInt(name))
		/** Not tested */
		case FnAppArrayT(f, ts) =>
			val res = allmap(ts, getBasicTerm) map (FnAppArrayInt(f, _))
			res
		case ArrStoreArrayT(arr, index, value) =>
			for {
				arrPS <- getTermArrayInt(arr)
				indexPS <- getTermInt(index)
				valuePS <- getTermInt(value)
			} yield ArrStoreArrayInt(arrPS, indexPS, valuePS)
		case _ => None
	}

	def getTermBool(result: TermT)(implicit progCtx: ProgContext): Option[TermBool] = result match {
		case VarT(v) =>
			progCtx.getVar(v, PSBool)(true) map (_.asInstanceOf[VarBool])

		case ConstT(name, PBool) => Some(ConstBool(name))

		/**Bool * Bool -> Bool functions*/
		case FnAppT(f, ts) if (List(AndBoolFn, OrBoolFn, NegBoolFn, ImplBoolFn, RImplBoolFn, EquivBoolFn) contains f)=>
			val res = allmap(ts, getTermBool) map (FnAppBool(f, _))
			res

		/**Int * Int -> Bool functions*/
		case FnAppT(f, ts) if (List(GTBoolFn, LTBoolFn, GEBoolFn, LEBoolFn) contains f)=>
			val res = allmap(ts, getTermInt) map (FnAppBool(f, _))

			res
			.orElse(getChainTermBool(result)) //x <= y < z Chaining Support

		/** EqEq for int and bool terms*/
		case FnAppT(EqEqBoolFn, ts) => //eqeq should work for bool and int terms. TODO: add test cases.
			logger.trace("----------")
			for(t <- ts) {
				logger.trace(t.toString)
				val x = getTermInt(t)
				logger.trace(x.toString)
			}
			logger.trace("++++++++++")

			val res = allmap(ts, getTermInt) map (FnAppBool(EqEqBoolFn, _))
			if(res.isDefined) res else allmap(ts, getTermBool) map (FnAppBool(EqEqBoolFn, _))

		case ArrSelectT(arr, index) =>
			for {
				arrPS <- getTermArrayBool(arr)
				indexPS <- getTermInt(index)
			} yield ArrSelectBool(arrPS, indexPS)

		case QTermT(opr, dummies, range, term) =>
			val dummiesPS = dummies map getFreshIntVar //TODO: handle non int dummies
			for{
				rangePS <- getTermBool(range)(progCtx.addDummies(dummiesPS))
				termPS <- getTermBool(term)(progCtx.addDummies(dummiesPS))
				if (List(AndBoolFn, OrBoolFn) contains opr )
			} yield {
				QTermBool(opr, dummiesPS, rangePS, termPS)
			}


		case _=>
			None

	}

	/** chains  x <= y < z = y or  x >= y < z = t > p */
	def getChainTermBool(term: TermT)(implicit progCtx: ProgContext): Option[TermBool] = {

	    def and(a: TermBool, b: TermBool) = (a, b) match {
	        case (TermBool.TrueT, _) => b
	        case (_, TermBool.TrueT) => a
	        case _ => a && b
	    }

        val oprs = List(GTBoolFn, LTBoolFn, GEBoolFn, LEBoolFn, EqEqBoolFn)

	    /* Returns chain formula, first link and last link */
	    def getChainTermBoolInner(term: TermT)(implicit progCtx: ProgContext): Option[(TermBool, TermInt, TermInt)] = {
	        term match {
        	    case FnAppT(opr, p :: q :: Nil) if (oprs contains opr) =>
        	        for{
        	            (pTerm, pFirst, pLast) <- getChainTermBoolInner(p)
        	            (qTerm, qFirst, qLast) <- getChainTermBoolInner(q)
        	        } yield {
        	            (and(and(pTerm, FnAppBool(opr, pLast :: qFirst :: Nil)), qTerm) , pFirst, qLast)
        	        }
        	    case _ =>
        	        // Single element is also treated as a chain. (true, x, x)
        	        for( intTerm <- getTermInt(term) ) yield {
        	            (TermBool.TrueT, intTerm, intTerm)
        	        }
    	    }
	    }

	    getChainTermBoolInner(term).map{_._1}
	}

	def getTermArrayBool(result: TermT)(implicit progCtx: ProgContext): Option[TermArrayBool] = result match {
		case VarArrayT(v) =>
			progCtx.getVar(v, PSArrayBool)(true) map (_.asInstanceOf[VarArrayBool])
		case ConstArrayT(name, PArrayBool) =>
			Some(ConstArrayBool(name))
		/** Not tested */
		case FnAppArrayT(f, ts) =>
			val res = allmap(ts, getBasicTerm) map (FnAppArrayBool(f, _))
			res
		case ArrStoreArrayT(arr, index, value) =>
			for {
				arrPS <- getTermArrayBool(arr)
				indexPS <- getTermInt(index)
				valuePS <- getTermBool(value)
			} yield ArrStoreArrayBool(arrPS, indexPS, valuePS)
		case _ => None
	}

}

