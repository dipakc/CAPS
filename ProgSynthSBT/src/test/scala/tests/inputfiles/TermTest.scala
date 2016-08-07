package tests.inputfiles
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns._

object TermTest {
	def testMethod() = {
		val x1 = VarInt("x")
		val x2 = ConstInt("5")
		val x3 = FnAppInt(new Fn("""$times""", List(PSInt, PSInt), PSInt), List(FnAppInt(new Fn("""$plus""", List(PSInt, PSInt), PSInt), List(FnAppInt(new Fn("""$times""", List(PSInt, PSInt), PSInt), List(ConstInt("5"), VarInt("z"))), VarInt("t"))), FnAppInt(new Fn("""$plus""", List(PSInt, PSInt), PSInt), List(VarInt("t"), ConstInt("2")))))
		val x4 = FnAppInt(new Fn("""unary_$minus""", List(PSInt), PSInt), List(VarInt("z")))
		val x5 = FnAppInt(new Fn("""$percent""", List(PSInt, PSInt), PSInt), List(VarInt("z"), ConstInt("2")))
		val x6 = FnAppInt(new Fn("""$times""", List(PSInt, PSInt), PSInt), List(ConstInt("5"), VarInt("z")))
	}
}
