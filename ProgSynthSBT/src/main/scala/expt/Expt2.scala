package expt
import progsynth.types._
import progsynth.types.Types._

import FOLFormula._
import progsynth.debug.PSDbg

object Expt2 extends App{

	val a = Atom(Pred("a", Nil))
	val b = Atom(Pred("b", Nil))
	val c = Atom(Pred("b", Nil))
	val f = Atom(Pred("f", Nil))
	val v = VarInt("v")
	val formula: FOLFormula = a && b
	val case1 = Exists(v, a || b)
	val case2 = Exists(v, (a || b) && f )
	val case3 = Exists(v, f && (a || b))
	val case4 = Forall(v, (a || b ) impl f )
	val case5 = Forall(v, ((a || b ) && c) impl f)
	val case6 = Forall(v, (c && (a || b)) impl f)


	val case1id = case1.setFIdAll()
	PSDbg.writeln0(case1.toStringId)
	//PSDbg.writeln0((fwithid, 2 ))

	def isMatching[T](x: T)(pf: PartialFunction[T,Unit]): Boolean = {
	    if (pf.isDefinedAt(x)) {
	        pf.apply(x)
	        true
	    }else {
	        false
	    }
	}

	val ret = isMatching("Hi"){
	    case "Bi" => println("Bi case")
	    case "Xi" => println("Xi case")
	}
	println(ret)
}
