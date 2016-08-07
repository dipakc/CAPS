package expt

import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._

import scalaz.{Forall => SForall, Const => SConst, _}
import Scalaz._
import progsynth.debug.PSDbg

object Expt4{

     /** Replace the suformula of 'f' whose fid  is 'sfid' with 'fnew'*/
    def replaceSubFormula[T](f: Formula[T], sfid: Int, fnew: Formula[T]) = {
        f.applyRec{
            case sf if (sf.fid == sfid) => fnew
        }
    }

    /** Replace the free occurrence of variable 'v' in formula 'f' with term 't' */
    def replaceFreeVarWithTerm(f: FOLFormula, v: Var, t: Term):FOLFormula = {
        f applyRec {
            case sf @ Forall(v2, _) if (v2 == v.v)=> sf
            case sf @ Exists(v2, _) if (v2 == v.v) => sf
            case sf @ Atom(_) => sf mapSubTerms {case `v` => t}
        }
    }

    /** get variable assignment formula and rhs term
     * */
    def getVarAsgnFT(fList: List[FOLFormula], aVar: Var): Option[(FOLFormula, Term)] = {
        fList match {
            case Nil => None
            case f :: fs => f match {
                case Atom(Pred("$eq$eq", List(`aVar`, rhs))) => Some((f, rhs))
                case _ => getVarAsgnFT(fs, aVar)
            }
        }
    }


	def onePointRule(folFormula: FOLFormula, quantId: Int): FOLFormula = {
	    assert(folFormula.fid != -1)
		folFormula.applyRec{
		    case sf if (sf.fid == quantId) => onePointRule(sf)
		}
	}

	private def applyOnePointRule(qFormula:FOLFormula, qVar: Var, qBody: FOLFormula, rangeFs: List[FOLFormula]) = {
        val asgnFOpt = getVarAsgnFT(rangeFs, qVar)
        asgnFOpt match {
            case None => qFormula
            case Some((asgnF, rhs)) =>
                qBody |>
                    { replaceSubFormula(_, asgnF.fid, True1()) } |>
                    { _.simplify } |>
                    { replaceFreeVarWithTerm(_, qVar, rhs) }
        }
	}

   /** \Exists i : \phi /\ i = z /\ \psi /\ \eta /\ ...
      * ===>
      * \phi' /\ \psi' /\ \eta' /\ ... (where primed formulas are obtained by replacing i by z.
      *
      * \Forall i : \phi /\ i = z /\ \eta /\... => \theta
      * ===>
      * \Forall i : \phi' /\ \eta' /\... => \theta'
      */
	def onePointRule(qFormula: FOLFormula): FOLFormula = { //TODO: refactor: Quantifier extractor.
	    assert(qFormula.fid != -1)
		qFormula match {
		    case Exists(v, bodyF @ AndN(fs))=>
		    	applyOnePointRule(qFormula, v, bodyF, fs)
			case Forall(v, bodyF @ Impl(AndN(fs), _)) =>
		    	applyOnePointRule(qFormula, v, bodyF, fs)
			case _ => qFormula
		}
	}

    def main(args: Array[String]) {
        val xVar = VarInt("x")
        val c3 = ConstInt("3")
        val c4 = ConstInt("4")
        val f1 = ExistsTermBool(xVar, xVar > c4 && (xVar eqeq c3))
        val f2 = f1.setDisplayIdAll
        //PSDbg.writeln0(f2.get.saveDotJpg)
        //PSDbg.writeln0(onePointRule(f2))
    }

}