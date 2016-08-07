package tests.inputfiles
import progsynth.types._
import progsynth.types.Types._
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.spec.StaticAssertions.@@
import progsynth.spec.StaticAssertions.@@._

object AST2FormulaTestExamples {
	//def And(f1, f2, f3, fs*) = true
	def testMethod() = {
		var x = 0
		val y = 0
		var z = 0
		val arr: Array[Int] = Array(1, 2, 3)
		val n = 5
		val f1 = TrueF
        val f2 = FalseF
        val f3 = !(x < y)
        //val f4 = Atom(Pred("$less", List(VarInt("x"), VarInt("y"))))
        val f5 = x < y && y == z
        val f6 = x < y || y == z
        val f7 = x < y || y == z && x >= z
        val f8 = x < y || y == z && !(x >= z)
        val f9 = (x < y) iff (y < z)
        val f10 = (x < y) impl (y < z)
        val f11 = (x < y) impl (y < z) && ( x == z)
        val f12 = And((x < y),(y < z))
        val f13 = Not(x < y)
        val f14 = x == y
        val f15 = x == 0
        val f16 = 0 == x
        val f17 = x == y + 5
        //val f18 = Atom(Pred("$eq$eq", List(VarInt("x"), FnAppInt(new Fn("""$plus""", List(PSInt, PSInt), PSInt), List(VarInt("y"), ConstInt("5"))))))
        val f19 = ∀(x)∀(y)∘(x == 0)
        val f20 = ∀(x)∘(And(TrueF, ∀(y)∘(TrueF)))
        val f21 = ∀(x)∘( x == 0 impl ∀(y)∘(x == y))
        val f22 = ∀(x)∀(y)∘( x == 0 impl ∀(y)∀(z)∘(x == y))
        val f23 = @@ ∀x∀y∘(x == 0)
        val f24 = @@ ∀x∘(And(TrueF, @@ ∀y∘(TrueF)))
        val f25 = @@ ∀x∘( x == 0 impl @@ ∀y∘(x == y))
        val f26 = @@ ∀x∀y∘( x == 0 impl @@ ∀y∀z∘(x == y))
        val f27 = arr == arr
        val f28 = arr(x) == 0
        val f29 = ∀(x)∀(y)∘((0 <= x && x < n - 1 && x < y && y < n) impl (arr(x) <= arr(y)))
        val f30 = arr.length == 0
        val f31 = ∀(x)∀(y)∘((0 <= x && x < arr.length - 1 && x < y && y < arr.length) impl (arr(x) <= arr(y)))
        val f32 = (x != y)
        //////////////////////////////
        val f33 = Or(And((x < y),(y < z)), (n > z))
        val f34 = ((x < y) && (y < z) ) || (n > z)
        val f35 = ((x < y) && (y < z) ) || (n > z)
        val f36 = And((x < y), (y < z)) || (n > z)
        val f37 = ((x < y) && (y < z) ) == (n > z)
        val f38 = ((x < y) && (y < z) ) != (n > z)
        val f39 = (x < y) && (y < z) impl (n > z)
        val f40 = And(x < y, y < z) || n > z
        val f41 = !(And(x < y, y < z) || n > z)
        val f42 = Or((x < y) && (y < z), (n > z))
        //val f43 = ∀(x)∀(y)∘( x == 0 impl ∀(y)∀(z)∘(x == y))
        val f44 = ∀(x, y)∘( x == 0 impl ∀(y)∀(z)∘(x == y)) // ∀(x, y) not implemented.
        val f45 = !(∀(x)∀(y)∘( x == 0 impl ∀(y)∀(z)∘(x == y))) // not working
        //val f45 = !(∀(x)∘(True))
        //Add sorting vcs
        val f46 = !TrueF
        val f47 = TrueF && FalseF
        val f48 = TrueF || FalseF
	}

}
