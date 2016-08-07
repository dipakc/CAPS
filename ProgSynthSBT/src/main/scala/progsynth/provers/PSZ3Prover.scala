package progsynth.provers
import progsynth.ProgSynth._
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.methodspecs.InterpretedFns._
import scala.collection.mutable.Map
import expt.PSTimeout.ProcessStatus
//import progsynth.config.AppConfig
import progsynth.config.PSConfig
import progsynth.config.AppConfig

class PSZ3Prover(proverConfig: PSConfig = AppConfig) extends PSProver(proverConfig) {

    val config = new Config(Some(PSProverConfigInit.z3Config), Map())

    def getId() = "Z3Prover"

    override def getCmd(fileName: String) = {
        val z3Path = proverConfig.configMap("provers.path.z3").asInstanceOf[String]
        val ostype= proverConfig.configMap("ostype").asInstanceOf[String]
        ostype match {
            case "Windows" => raw"""$z3Path /smt2 $fileName"""
            case "Linux" => raw"""$z3Path -smt2 $fileName"""
        }

    }

    override def getCmdSimple(fileName: String): Option[String] = {
        None
    }

    private def wrapInFn(fnname: String, content: List[String]): List[String] =
	    List("(" + fnname) ++ content ++ List(")")

    private def fnAppStr(fnName: String, ts: List[Term]): List[String] =
        wrapInFn(fnName, ts.map(poToZ3Input(_)).reduce{(l1, l2) => l1 ++ l2})

    /** Generate the input file */
    override def mkProverInput(po: TermBool): String  = {
        mkProverInputMain(removePrimeSymbol(po))
    }

	private def mkProverInputMain(po: TermBool): String  = {
	    var lstStr: List[String]  = List()
		val varDecls: List[String] =
		    po.getFreeVars.map { fv =>
		        fv.getType match {
	        	case PSArrayInt => <a>(declare-const {fv.v} (Array Int Int))</a>.text
	        	case PSArrayBool => <a>(declare-const {fv.v} (Array Int Bool))</a>.text
	        	case PSInt | PSBool  =>
	        	    <a>(declare-fun {fv.v} () {fv.getType.getCleanName})</a>.text
	        	case _ => throw new RuntimeException("type not supported")
		        }
		    }
	    lstStr = lstStr ++ varDecls
	    lstStr = lstStr :+ "(assert "
	    lstStr = lstStr ++ poToZ3Input(!po)//negate the po
	    lstStr = lstStr :+ ") "
	    lstStr = lstStr :+ "(check-sat)"
	    lstStr = lstStr :+ "(get-model)"
	    lstStr.mkString("\n")
	}

    /** Transforms x' to _x_prime */
    def removePrimeSymbol[T <: Term](term: T): T = {
        val fvs = term.getFreeVars
        val replaceMap = fvs.filter(_.v.contains("'")).map{ primedVar =>
            (primedVar, primedVar.rename { "_" + _.replace("'", "_prime")})
        }.toMap
        term.replaceVarsSim(replaceMap)
    }

	private def poToZ3Input(po: Term): List[String] = {
	    def dummiesStr(dummies: List[Var]) = {
	    	val dstr = dummies.flatMap{ d =>
	    	    wrapInFn(d.v, List(d.getType.getCleanName))
	    	}
	    	List("(") ++ dstr ++ List(")")
	    }

		po match {
			case aVar @ Var(v) => List(v)
			case FnApp(f, ts) =>
				po match {
					case fnAppInt: FnAppInt => fnAppIntToZ3Input(fnAppInt)
					case fnAppBool: FnAppBool => fnAppBoolToZ3Input(fnAppBool)
					case _ =>
					    throw new RuntimeException("Only FnAppInt and FnAppBool are supported")
				}
			case aConst @ Const(name) => List(name)
			case ArrSelect(arr, index) =>
				fnAppStr("select", List(arr, index))
			case ArrStore(arr: Term, index: Term, value: Term) =>
				fnAppStr("store", List(arr, index, value))
			case ForallTermBool(dummies, range, term) =>
			    val _x = dummiesStr(dummies)
			    val _y = poToZ3Input(range impl term)
			    wrapInFn("forall", _x ++ _y)
			case ExistsTermBool(dummies, range, term) =>
			    val _x = dummiesStr(dummies)
			    val _y = poToZ3Input(range && term)
			    wrapInFn("exists", _x ++ _y)
			case QTerm(_, _, _, _) =>
				throw new RuntimeException("""Exception in poToZ3Input: non boolean
						QTerm not handled""")
		}
	}

	private def fnAppIntToZ3Input(fnAppInt: FnAppInt): List[String] = {
		val FnAppInt(f, ts) = fnAppInt
		f match {
			case PlusIntFn => fnAppStr("+", ts)
			case MinusIntFn => fnAppStr("-", ts)
			case TimesIntFn => fnAppStr("*", ts)
			case UnaryMinusIntFn => fnAppStr("-", ts)
			case PercentIntFn => fnAppStr("%", ts)
			case DivIntFn => fnAppStr("/", ts)
			case PowIntFn => fnAppStr("^", ts)
			case _ => throw new RuntimeException("PSZ3Prover: Uninterpreted functions not supported: " + f)
		}
	}

	private def fnAppBoolToZ3Input(fnAppBool: FnAppBool): List[String] = {
		val FnAppBool(f, ts) = fnAppBool
		val boolArgs = (ts.map(_.getType).forall(_ == PSBool))
		val treatEqEqAsEquivForBoolArgs = false

		f match {
			case AndBoolFn => fnAppStr("and", ts)
			case OrBoolFn => fnAppStr("or", ts)
			case NegBoolFn => fnAppStr("not", ts)
			case ImplBoolFn => fnAppStr("=>", ts)
			case RImplBoolFn => fnAppStr("=>", ts.reverse)
			case EquivBoolFn  => fnAppStr("iff", ts)
			case EqEqBoolFn =>
				if (boolArgs && treatEqEqAsEquivForBoolArgs)
					fnAppStr("iff", ts)
				else
					fnAppStr("=", ts)
			case LTBoolFn => fnAppStr("<", ts)
			case LEBoolFn => fnAppStr("<=", ts)
			case GTBoolFn => fnAppStr(">", ts)
			case GEBoolFn => fnAppStr(">=", ts)
			case _ => throw new RuntimeException("PSZ3Prover: Uninterpreted functions not supported: " + f)
		}
	}

	override def parseProverOutput(ps: ProcessStatus): PSProofStatus = {
	    if(ps.retcode.isEmpty)
	        PSProofTimeout("")
	    else if (!ps.stdout.isEmpty && ps.stdout(0) == "unsat") {
	        PSProofValid()
	    }else if ( !ps.stdout.isEmpty && ps.stdout(0) == "sat") {
	        PSProofInvalid(ps.stdout.tail.mkString("\n"))
	    } else {
	        PSProofUnknown((ps.stdout ++ ps.stderr).mkString("\n"))
	    }
	}
	def getInputFileExtension() = "txt"

	def toHtml() = {
	    <div class="PSProver PSZ3Prover">
    		<div class="Name">Z3Prover</div>
    	</div>
	}
}