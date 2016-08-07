package tests.utils

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.utils.ProcessServerLog
import TermBool.TrueT
import TermBool.FalseT
import progsynth.utils.SimplifyAuto
import progsynth.methodspecs.InterpretedFns._
import progsynth.utils.QuantUtils
import progsynth.utils.TestLogHelper
import org.slf4j.LoggerFactory

@RunWith(classOf[JUnitRunner])
class QuantUtilsTest extends FunSuite with ShouldMatchers {

	val testItem = progsynth.utils.QuantUtils
	object VarObj {
		val n0 = VarInt("n0")
		val n = VarInt("n")
		val x = VarInt("x")
		val z = VarInt("z")
		val y = VarInt("y")
		val x0 = VarInt("x0")
		val y0 = VarInt("y0")
		val s = VarBool("s")
		val i = VarInt("i")
		val arr = VarArrayBool("arr")
		val iarr = VarArrayInt("iarr")
		val c1 = ConstInt("1")
		val c0 = ConstInt("0")
	}
	implicit val logger= LoggerFactory.getLogger("progsynth.QuantUtilsTest")

    def mtest(name: String)(body: => Unit): Unit = {
  		test(name){
  		    val testName = this.getClass.getName() + "." +  name
    	    TestLogHelper.setupSiftLogger(testName, logger)(body)
   		}
	}

	mtest("eqQuantElim.1") {
		import QuantUtils.{logger => qlogger, _}
		import VarObj._

		val f1 = s equiv ForallTermBool(i, c0 <= i && i < n0 + c1 , arr.select(i))
		val f = ExistsTermBool(n0, (n eqeq n0 + c1) && f1 )
		val actual: TermBool = eliminateOldVars(TrueT, f)

		val f2 = s equiv ForallTermBool(i, c0 <= i && i < n , arr.select(i))
		val expected = ExistsTermBool(n0, (n eqeq n0 + c1) && f2 )
		actual should equal(expected)
	}

	mtest("eqQuantElim.2") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(n0, (n eqeq n0 + c1) || n0 + c1 < x )
		val actual = eliminateOldVars(TrueT, f)
		val expected = ExistsTermBool(n0, (n eqeq n0 + c1) || n0 + c1 < x )
		actual should equal(expected)
	}

	mtest("eqQuantElim.3_Issue") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(n0, !(n eqeq n0 + c1) || n0 + c1 < x )
		val actual = eliminateOldVars(TrueT, f)
		val expectedIdeal = ExistsTermBool(n0, !(n eqeq n0 + c1) || n < x )
		val expected = f
		//println(actual.pprint)
		actual should equal(expected)
	}

	mtest("eqQuantElim.4") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(n0, (n eqeq n0 + c1) impl (n0 + c1 < x) )
		val actual = eliminateOldVars(TrueT, f)
		val expected = ExistsTermBool(n0, (n eqeq n0 + c1) impl (n < x) )
		actual should equal(expected)
	}

	mtest("eqQuantElim.5") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(n0,  (n0 + c1 < x) rimpl (n eqeq n0 + c1) )
		val actual = eliminateOldVars(TrueT, f)
		val expected = ExistsTermBool(n0, (n < x) rimpl (n eqeq n0 + c1) )
		actual should equal(expected)
	}

	mtest("eqQuantElim.6_Issue") {
		import QuantUtils._
		import VarObj._

		val opr = MinIntFn
		val f = n eqeq (n0 + c1) impl QTermInt(opr, n0 :: Nil, TrueT, iarr.select(n0 + c1))
		val actual = eliminateOldVars(TrueT, f)
		//println(actual.pprint)
		val expectedIdeal = n eqeq (n0 + c1) impl iarr.select(n)
		val expected = f
		actual should equal(expected)
	}

	//TODO: add tests for MaxIntFn

	mtest("eqQuantElim.7_Issue") {
		import QuantUtils._
		import VarObj._

		val opr = MinIntFn
		val f =  x < QTermInt(opr, n0 :: Nil, n eqeq (n0 + c1), iarr.select(n0 + c1))
		val actual = eliminateOldVars(TrueT, f)
		//println("actual" + actual.pprint)
		val expectedIdeal = x < QTermInt(opr, n0 :: Nil, n eqeq (n0 + c1), iarr.select(n))
		val expected = f
		//println("expected" + expected.pprint)
		actual should equal(expected)
	}

	mtest("eqQuantElim.8") {
		import QuantUtils._
		import VarObj._

		val f = ForallTermBool(n0, n eqeq n0 + c1, n0 + c1 < x)
		val actual = eliminateOldVars(TrueT, f)
		//println(actual.pprint)
		val expected = ForallTermBool(n0, n eqeq n0 + c1, n < x)
		actual should equal(expected)
	}

	mtest("eqQuantElim.9") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(x0, x eqeq x0 + c1, ExistsTermBool(y0, y eqeq y0 + x0, x0 + c1 < y0 + x0 ))
		val actual = eliminateOldVars(TrueT, f)
		//println(actual.pprint)
		val expected = ExistsTermBool(x0, x eqeq x0 + c1, ExistsTermBool(y0, y eqeq y0 + x0, x < y ))
		actual should equal(expected)
	}

	mtest("eqQuantElim.10 Issue") {
		import QuantUtils._
		import VarObj._

		val b = (y0 + z) + c1 < y
		val f = ExistsTermBool(x0, x eqeq x0 + c1, ExistsTermBool(y0, x0 eqeq y0 + z, b))
		val actual = eliminateOldVars(TrueT, f)
		//println(actual.pprint)
		val expectedIdeal = ExistsTermBool(x0, x eqeq x0 + c1, ExistsTermBool(y0, x0 eqeq y0 + z, x < y))
		val expected = ExistsTermBool(x0, x eqeq x0 + c1, ExistsTermBool(y0, x0 eqeq y0 + z, x0 + c1 < y))
		actual should equal(expected)
	}

	ignore("eqQuantElim.11") {
		import QuantUtils._
		import VarObj._

		val f = ExistsTermBool(x0, x eqeq x0 + c1, x0 < y)
		val actual = eliminateOldVars(TrueT, f)
		//println(actual.pprint)
		val expected = f
		actual should equal(expected)
	}

	ignore("quantifierIn.1") {
		val n0 = VarInt("n0")
		val n = VarInt("n")
		val r = VarBool("r")
		val c0 = ConstInt("0")
		val f = ExistsTermBool(n0, (r eqeq TrueT) && (n0 eqeq c0) && (n eqeq c0))
		val actual = QuantUtils.quantifierIn(f)
		val expected = (r eqeq TrueT) && (n eqeq c0) && ExistsTermBool(n0, n0 eqeq c0 )
		actual should equal(expected)
	}

}