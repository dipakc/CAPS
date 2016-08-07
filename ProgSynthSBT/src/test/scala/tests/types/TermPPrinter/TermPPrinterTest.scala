package tests.types.TermPPrinter
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.debug.FormulaParseTreeViewer
import scalaz.{ Const => SConst, _ }
import Scalaz._

@RunWith(classOf[JUnitRunner])
class TermPPrinterTest extends FunSuite with ShouldMatchers {
    trait Setup {
        val c1 = ConstInt("1") //> c1  : progsynth.types.Const = ConstInt(1)
        val c0 = ConstInt("0") //> c0  : progsynth.types.Const = ConstInt(0)
        val c2 = ConstInt("2") //> c2  : progsynth.types.Const = ConstInt(2)
        val c3 = ConstInt("3") //> c3  : progsynth.types.Const = ConstInt(3)
        val c4 = ConstInt("4") //> c4  : progsynth.types.Const = ConstInt(4)
        val c5 = ConstInt("5") //> c5  : progsynth.types.Const = ConstInt(5)
        val c6 = ConstInt("6") //> c6  : progsynth.types.Const = ConstInt(6)
        val c7 = ConstInt("7") //> c7  : progsynth.types.Const = ConstInt(7)
        val i = VarInt("i") //> i  : progsynth.types.Var = VarInt(i)
        val a = VarInt("a") //> a  : progsynth.types.Var = VarInt(a)
        val b = VarInt("b") //> b  : progsynth.types.Var = VarInt(b)
        val c = VarInt("c") //> c  : progsynth.types.Var = VarInt(c)
        val d = VarInt("d") //> d  : progsynth.types.Var = VarInt(d)

        val arr = VarArrayInt("arr") //> arr  : progsynth.types.Var = Var(arr,PSArrayInt)

    }

    test("pprint basic") {
        new Setup {
            a
            var x1: Term = (a + b) * (c + d)
            x1.pprint should equal("(a + b) * (c + d)")
            x1 = -(a + b)
            x1.pprint should equal("-(a + b)")
            x1 = -(a + b * c)
            x1.pprint should equal("-(a + b * c)")
            x1 = -a
            x1.pprint should equal("-a")
        }
    }

    test("pprint array") {
        new Setup {
            var x1 = arr.select(c5)
            x1.pprint should equal("arr[5]")
            x1 = arr.select(a + b * c)
            x1.pprint should equal("arr[a + b * c]")
            x1 = arr.select((arr.select(a) + c2) * c3)
            x1.pprint should equal("arr[(arr[a] + 2) * 3]")
            x1 = (arr.store(c1, c2)).select(c3)
            x1.pprint should equal("(arr[1] := 2)[3]")
            x1 = (arr.store(c1, c2 * c3)).select(c3)
            x1.pprint should equal("(arr[1] := 2 * 3)[3]")
            x1 = (arr.store(c1, c2 * c3 + c4)).select(c6)
            x1.pprint should equal("(arr[1] := 2 * 3 + 4)[6]")
            x1 = (arr.store((c1 + c2) * c3, (c3 + c4) * c5)).select(c7)
            x1.pprint should equal("(arr[(1 + 2) * 3] := (3 + 4) * 5)[7]")
            val x2 = arr.store(c1, c2)
            x2.pprint should equal("arr[1] := 2")
            x1 = ((arr.store(c1, c2)).select(c2) + c3) * c4
            x1.pprint should equal("((arr[1] := 2)[2] + 3) * 4")
            x1 = c3 + arr.select(c5)
            x1.pprint should equal("3 + arr[5]")
            x1 = (c3 + arr.select(c5)) * c4
            x1.pprint should equal("(3 + arr[5]) * 4")
            x1 = (c3 + arr.select(c5)) + c4
            x1.pprint should equal("3 + arr[5] + 4")
            x1 = c3 + arr.select(c5) * c4
        }
    }

}