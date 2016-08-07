package progsynth.provers

import org.slf4j.LoggerFactory
import progsynth.logger.PSLogUtils._
object Why3AST {

    //TODO: use this function in str to reduce excess parenthesis.
    def getBP(wt: WTerm): Int = {
        wt match {
            case _: WEquiv => 1
            case _: WImpl => 2
            case _: WAnd => 3
            case _: WOr => 4
            case _ => 10 /*Everything else is atomic or bracketed*/
        }
    }

    def indent(idt: Int) = "\t" * idt

	def pstr(c: WObject, idt: Int = 0): String = indent(idt) + (if (c.isAtom) c.str() else s"(${c.str()})")

	trait WObject extends Why3IOStringUtils {
		def str(idt: Int = 0): String
		def isAtom: Boolean
	}

	trait WType extends WObject
	trait WTerm extends WObject
	trait WDecl extends WObject {
	    def symbolName: Option[String]
	}
	trait WLogicDecl

	case class WRawTerm(raw: String) extends WTerm{
		def isAtom = false
		def str(idt: Int = 0) = indent(idt) + raw
	}

	case class WTrue() extends WTerm {
		def str(idt: Int = 0) = indent(idt) + "True"
		def isAtom = true
	}

	case class WFalse() extends WTerm {
		def str(idt: Int = 0) = indent(idt) + "False"
		def isAtom = true
	}

	case class WImpl(t1: WTerm, t2: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val t1str = pstr(t1)
			val t2str = pstr(t2)
			indent(idt) + s"$t1str -> $t2str"
		}
	}

	case class WEquiv(t1: WTerm, t2: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val t1str = pstr(t1)
			val t2str = pstr(t2)
			indent(idt) + s"$t1str <-> $t2str"
		}
	}

	case class WAnd(t1: WTerm, t2: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val t1str = pstr(t1)
			val t2str = pstr(t2)
			indent(idt) + s"$t1str /\ $t2str"
		}
	}

	case class WOr(t1: WTerm, t2: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val t1str = pstr(t1)
			val t2str = pstr(t2)
			indent(idt) + s"$t1str \/ $t2str"
		}
	}

	case class WNot(t: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val tstr = pstr(t)
			indent(idt) + s"not $tstr"
		}

	}

	case class WITEBool(t1: WTerm, t2: WTerm, t3: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val t1str = pstr(t1)
			val t2str = pstr(t2)
			val t3str = pstr(t3)
			indent(idt) + s" if $t1str then $t2str else $t3str"
		}
	}

	//(forall | exists) (lident+ : type)+csv triggers+ . formula
	case class WForall(bs: List[WBinder], ts: List[WTrigger], f: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val bsStr = bs.map(_.str()).mkString(", ")
			val tsStr = if(!ts.isEmpty) " " + sparen(ts.map(_.str()).mkString("| ")) else ""
			val fStr = f.str()
			indent(idt) + paren(s"forall $bsStr$tsStr. $fStr")
		}
	}

	case class WExist(bs: List[WBinder], ts: List[WTrigger], f: WTerm) extends WTerm {
		def isAtom = false

		def str(idt: Int = 0) = {
			val bsStr = bs.map(_.str()).mkString(", ")
			val tsStr = if(!ts.isEmpty) " " +  sparen(ts.map(_.str()).mkString("| ")) else ""
			val fStr = f.str()
			indent(idt) + paren(s"exists $bsStr$tsStr. $fStr")
		}
	}

	case class WBinder(ids: List[String], tpe: WType) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) =  {
			val idsStr = ids.mkString(" ")
			val tpeStr = tpe.str()
			indent(idt) + s"$idsStr: $tpeStr"
		}
	}

	case class WTrigger(trigger: WTerm) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = indent(idt) + trigger.str()
	}

	////////////////////////
	// example: list int
	case class WTypeSymbol(id: String, tpes: List[WType]) extends WType {
		def isAtom = false
		def str(idt: Int = 0) = {
			val tpeStrs = tpes.map(_.str()).mkString(" ")
			indent(idt) + s"$id $tpeStrs"
		}
	}

	//' lident
	case class WTypeVar(id: String) extends WType {
		def isAtom = true

		def str(idt: Int = 0) = indent(idt) + "'" + id
	}

	case class WTupleType(tps: List[WType]) extends WType {
		def isAtom = true
		def str(idt: Int = 0) = {
			val tpStr = tps.map(_.str()).mkString(", ")
			indent(idt) + paren(tpStr)
		}
	}

	//
	case class WFnType(tps: List[WType]) extends WType {
	    def isAtom = true
		def str(idt: Int = 0) = {
			val tpStr = tps.map(_.str()).mkString(" -> ")
			indent(idt) + tpStr
		}
	}
	///////////////////////////////
	case class WInteger(v: Int) extends WTerm {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + v.toString
	}

	case class WReal(v: Double) extends WTerm {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + v.toString
	}

	case class WSymbol(id: String) extends WTerm {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + id
	}

	case class WPrefixOp(prefixOp: String, t: WTerm) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = indent(idt) + prefixOp + pstr(t)
	}

	case class WBangOp(bangOp: String, t: WTerm) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = indent(idt) + bangOp + pstr(t)
	}

	case class WInfixOp(infixOp: String, t1: WTerm, t2: WTerm) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = {
			val t1Str = pstr(t1)
			val t2Str = pstr(t2)
			indent(idt) + s"$t1Str $infixOp $t2Str"
		}
	}

	case class WBrackets(baseTerm: WTerm, indexTerm: WTerm) extends WTerm {//baseTerm[indexTerm]
		def isAtom = false
		def str(idt: Int = 0) = {
			val baseStr = pstr(baseTerm)
			val indexStr = indexTerm.str()
			indent(idt) + s"$baseStr[$indexStr]"
		}
	}

	case class WTernaryBrackets(baseTerm: WTerm, t1: WTerm, t2: WTerm) extends WTerm{ //baseTerm[t1 <- t2]
		def isAtom = false
		def str(idt: Int = 0) = {
			val baseStr = pstr(baseTerm)
			val t1Str = t1.str()
			val t2Str = t2.str()
			indent(idt) + s"$baseStr[t1Str <- t2Str]"
		}
	}


	/** (\i: int, j:int. i + j)
	 *  */

	case class WFnLambda(params: List[(String, WType)], body: WTerm, bodyType: WType) extends WTerm { //baseTerm[t1 <- t2]

	    /** Setting to true since parenthesis is always inserted around the lambda*/
	    def isAtom = true

	    //retType is not printed
		def str(idt: Int = 0) = {
		    val paramStr = params.map(p => p._1 + ": " + p._2.str()).mkString(", ")
		    val bodyStr = body.str()
			indent(idt) + s"(\\$paramStr. $bodyStr)"
		}

	    def lambdaType: WType = WFnType(params.map(_._2) :+ bodyType)
	}

	case class WFnApp(fname: String, args: List[WTerm]) extends WTerm{ //baseTerm[t1 <- t2]
		def isAtom = false
		def str(idt: Int = 0) = {
			val argStrs = args.map(pstr(_)).mkString(" ")
			indent(idt) + s"$fname $argStrs"
		}
	}

	case class WITE(t1: WTerm, t2: WTerm, t3: WTerm) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = {
			val t1Str = t1.str()
			val t2Str = t2.str()
			val t3Str = t3.str()
			indent(idt) + s"if $t1Str then $t2Str else $t3Str"
		}
	}

	case class WCast(t: WTerm, tpe: WType) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = {
			val tstr = t.str()
			val tpestr = tpe.str()
			indent(idt) + s"$tstr: $tpestr"
		}
	}

	case class WTuple(ts: List[WTerm]) extends WTerm {
		def isAtom = false
		def str(idt: Int = 0) = {
			indent(idt) + ts.map(_.str()).mkString(", ")
		}
	}

	////////////////////////////////////
	case class WTheory(name: String, lbls: List[String], decls: List[WDecl], usedNames: List[String]) extends WObject {

	    private implicit val logger= LoggerFactory.getLogger("progsynth.WTheory")
	    def isAtom = true
		def str(idt: Int = 0) = {
			val lblStrs = lbls.mkString(" ")
			val declStrs = decls.map(_.str(idt + 1))
			(List(s"theory $name $lblStrs") ++
			        declStrs ++
			        List("end")).mkString("\n")
		}
		def addDecl(decl: WDecl): WTheory = {
			addDecl(List(decl))
		}

		def addDecl(newDecls: List[WDecl]): WTheory = traceBeginEnd("Why3TheoryBuilder.addDeclToTheory"){
		    logger.trace("Declarations")
    	    newDecls.foreach { decl =>
	            logger.trace(multiline(decl.str()))
		    }
		    val newNames =  for {
		        decl <- newDecls
		        if decl.symbolName.isDefined
		    } yield decl.symbolName.get

		    WTheory(name, lbls, decls ++ newDecls, usedNames ++ newNames)
		}
	}

	case class WRawDecl(raw: String) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + raw
		def symbolName = None
	}

	case class WTypeDecl(raw: String) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + raw
		def symbolName = None
	}

	/** constant name l1 l2 l3: tpe = body*/
	case class WConstDecl(name: String, lbls: List[String], tpe: WType, body: Option[WTerm]) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val lblStrs = lbls.mkString(" ")
			val tpeStr = tpe.str()
			val bodyStr = body.map("\n"+ indent(idt + 1) + "= " + _.str()).getOrElse("")
			indent(idt) + s"constant $name $lblStrs: $tpeStr $bodyStr"
		}
		def symbolName = Some(name)
	}

	//function rf "label" (n: int) (x: int) : int = n + x
	case class WFunDecl(name: String, lbls: List[String], tparams: List[String], tpe: WType, body: Option[WTerm], withDecls: List[WLogicDecl]) extends WDecl with WLogicDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val lblStrs = lbls.mkString(" ")
			val tpeStr = tpe.str()
			val tpeParamsStr = tparams.mkString(" ")
			val bodyStr = body.map("\n" + indent(idt+1) + "= " + _.str()).getOrElse("")
			"\n" + indent(idt) + s"function $name $lblStrs $tpeParamsStr: $tpeStr $bodyStr"
		}
		def symbolName = Some(name)
	}

	case class WPredDecl(name: String, lbls: List[String], tparams: List[String], body: Option[WTerm], withDecls: List[WLogicDecl]) extends WDecl with WLogicDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val lblStrs = lbls.mkString(" ")
			val tpeParamsStr = tparams.mkString(" ")
			val bodyStr = body.map("\n" + indent(idt+1) + "= " + _.str()).getOrElse("")
			"\n" + indent(idt) + s"predicate $name $lblStrs $tpeParamsStr $bodyStr"
		}
		def symbolName = Some(name)
	}

	case class WIndDecl() extends WDecl {
		def str(idt: Int = 0) = {
			"\n" + indent(idt) + "" //TODO: implement
		}
		def isAtom = false
		def symbolName = None
	}

	case class WCoIndDecl() extends WDecl {
		def str(idt: Int = 0) = {
			"\n" + indent(idt) + "" //TODO: implement
		}
		def isAtom = false
		def symbolName = None
	}

	case class WAxiomDecl(name: String, f: WTerm) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val fstr = f.str(idt + 1)
			"\n" + indent(idt) + s"axiom $name:"+ "\n" + "$fstr"
		}
		def symbolName = Some(name)
	}

	case class WLemmaDecl(name: String, f: WTerm) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val fstr = f.str(idt + 1)
			"\n" + indent(idt) + s"lemma $name:" + "\n" + "$fstr"
		}
		def symbolName = Some(name)
	}

	case class WGoalDecl(name: String, f: WTerm	) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = {
			val fstr = f.str(idt + 1)
			"\n" + indent(idt) + s"goal $name:\n$fstr"
		}
		def symbolName = Some(name)
	}

	case class WUseDecl(raw: String) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + raw
        def symbolName = None
	}

	case class WCloneDecl(raw: String) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) =
		    "\n" + indent(idt) + raw
		def symbolName = None
	}

	case class WNamespaceDecl(raw: String) extends WDecl {
		def isAtom = true
		def str(idt: Int = 0) = indent(idt) + raw
		def symbolName = None
	}
	///////////////////
	object WBool extends WTypeSymbol("bool", Nil)
	object WInt extends WTypeSymbol("int", Nil)
	object WArrayInt extends WTypeSymbol("array", WInt :: Nil)
	object WArrayBool extends WTypeSymbol("array", WBool :: Nil)

}

