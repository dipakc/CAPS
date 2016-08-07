package models

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.Assert
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.methodspecs.InterpretedFns._
import scala.util.parsing.combinator.Parsers
import mqprinter.MQPrinter._

object TestData {
	val x = VarInt("x")
	val y = VarInt("y")
	val z = VarInt("z")
	
	val i = VarInt("i")
	val j = VarInt("j")
	val k = VarInt("k")
	
	val a = VarBool("a")
	val b = VarBool("b")
	val c = VarBool("c")

	val p = VarArrayInt("p")
	val q = VarArrayInt("q")
	val r = VarArrayInt("r")
	
	val l = VarArrayBool("l")
	val m = VarArrayBool("m")
	val n = VarArrayBool("n")
	
}

@RunWith(classOf[JUnitRunner])
class MQPrinterTest extends FunSuite with ShouldMatchers {
	
	def mytest(in: Term, out: String) = {
		val actualOut = mqprintTerm0(in)
		withClue( "\n" + "actualOut: " + actualOut + "\n" + "expectOut: " + out + "\n") {
			actualOut should equal(out)}
	}
		
	import TestData._
	
	test("termInt 1") { mytest(ConstInt("1"), "1") }
	test("termInt 2") { mytest(x, "x") }
	test("termInt 3") { mytest(-x, "-x") }
	test("termInt 4") { mytest(x * y, """x \ast y""") }
	test("termInt 5") { mytest(x / y, """x \slash y""") }
	test("termInt 6") { mytest(x % y, """x \% y""") }
	test("termInt 7") { mytest(x - y, """x - y""") }
	test("termInt 8") { mytest(x + y, """x + y""") }
	test("termInt 9") { mytest(p.select(i), """p\left[i\right]""") }
	test("termInt 10") { mytest(QTermInt(PlusIntFn, List(i), TestData.a, p.select(i)),
			"""\left(\Sum i: a: p\left[i\right]\right)""")}
	
	test("termBool 1") { mytest(ConstBool("true"), 	"""true""")}
	test("termBool 2") { mytest(ConstBool("false"), """false""")}
	test("termBool 3") { mytest(b, """b""")}
	test("termBool 4") { mytest(!b, """\neg b""")}
	test("termBool 5") { mytest(b && c, """b \wedge c""")}
	test("termBool 6") { mytest(b || c, """b \vee c""")}
	test("termBool 7") { mytest(b.impl(c), """b \Rightarrow c""")}
	test("termBool 8") { mytest(b.rimpl(c), """b \Leftarrow c""")}
	test("termBool 9") { mytest(b.eqeq(c), """b = c""")}
	
	test("termBool 10") { mytest(x < y, """x < y""")}
	test("termBool 11") { mytest(x <= y, """x \le y""")}
	test("termBool 12") { mytest(x > y, """x > y""")}
	test("termBool 13") { mytest(x >= y, """x \ge y""")}
	test("termBool 14") { mytest(x eqeq y, """x = y""")}
	
	test("termBool 15") { mytest(m.select(i), """m\left[i\right]""")}
	
	test("termBool 16") { mytest(
			QTermBool(AndBoolFn, List(i), b, p.select(i) < ConstInt("5")),
			"""\left(\forall i: b: p\left[i\right] < 5\right)""")}

	test("termBool 17") { mytest(
			QTermBool(OrBoolFn, List(i), b, p.select(i) < ConstInt("5")),
			"""\left(\exists i: b: p\left[i\right] < 5\right)""")}
	
	test("termArrayInt 1") { mytest(VarArrayInt("p"), 	"""p""")}
	test("termArrayInt 2") { mytest(ArrStoreArrayInt(p, i, x), 	"""\left(p\left[i\right] := x\right)""")}

	test("termArrayBool 1") { mytest(VarArrayBool("p"), 	"""p""")}
	test("termArrayBool 2") { mytest(ArrStoreArrayBool(m, i, b), 	"""\left(m\left[i\right] := b\right)""")}
}
