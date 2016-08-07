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

object Why3IOStringUtils extends Why3IOStringUtils
trait Why3IOStringUtils {

	//addSep(List("a", "b", "c"), "|") = List("a", "|", "b", "|", "c")
	def addSep[T](lst: List[T], sep: T): List[T] = {
		lst.head :: lst.tail.flatMap{x => List(sep, x)  }
	}

	//sepList(List(List("a", "b"), List("1", "2"), List("x", "y")), "|") = List("a", "b", "|", "1", "2", "|", "x", "y")
	def addSep2[T](lst: List[List[T]], sep: T): List[T] =
		addSep(lst, List(sep)).flatMap(x => x)

	def surround(left: String, right: String)(content: String) = left + content + right

    def surroundLst(left: String, right: String)(content: List[String]) =
        List(left) ++ content ++ List(right)

    def paren(content: String) = s"($content)"

    def paren(content: List[String]) =
        surroundLst("(", ")")(content)

    //conditional parenthesis
    def cparen(content: List[String]): List[String] =
        if ( content.length > 1 )
        	surroundLst("(", ")")(content)
        else content

    def sparen(content: String): String =
        "[" + content + "]"

    def sparen(content: List[String]) =
        surroundLst("[", "]")(content)
}

object Why3TypeUtils extends Why3TypeUtils
trait Why3TypeUtils {
    def getWhy3Tpe(tpe: PSType): String = {
	    tpe match {
	    	case PSArrayInt => "array int"
	    	case PSArrayBool => "array bool"
	    	case PSInt => "int"
	    	case PSBool  => "bool"
	    	case _ => throw new RuntimeException("No Why3 type for type for " + tpe)
	    }
	}

    //VarInt("x") ---> "int"
    def getWhy3Tpe(aVar: Var): String = getWhy3Tpe(aVar.getType)

}

