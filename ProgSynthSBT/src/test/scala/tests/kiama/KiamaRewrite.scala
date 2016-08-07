package tests.kiama

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.kiama.rewriting.Rewriter._
import progsynth.debug.PSDbg

object PQR {
    abstract class E
    case class B(l: E, r: E) extends E
    case class P() extends E
    case class Q() extends E
    case class PP() extends E
    case class QQ() extends E
}
@RunWith(classOf[JUnitRunner])
class KiamaRewrite extends FunSuite {
    test("test1") {
    	import PQR._
        val t = B(P(), Q())
        val d = rule {
            case P() => PP()
            case Q() => QQ()
        }

        val d2 = rule {
            case PP() => P()
        }

        //val s = bottomup(d)(t)
        PSDbg.logln("done")
        val res = oncetd(d2)(t)
        println(res)
    }
}