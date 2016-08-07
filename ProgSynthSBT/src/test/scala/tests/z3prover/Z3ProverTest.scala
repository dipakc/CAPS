package tests.z3prover
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import progsynth.types._
import progsynth.types.Types._
import progsynth.config.AppConfig
//import tests.utils.TreeExtractor
import progsynth.printers.RichTree._
import org.scalatest.matchers.ShouldMatchers
import progsynth.proofobligations.Z3Prover
import progsynth.dsl.QQ._
import progsynth.methodspecs.InterpretedFns
import progsynth.proofobligations.Z3Result
import progsynth.debug.PSDbg

@RunWith(classOf[JUnitRunner])
class Z3ProverTest extends FunSuite with ShouldMatchers {
	// Use eqeq for x == y.
	// It is not advisable to override "==" of Var
	test("f1") {
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = (x <= y).fm f_==> ((x < y).fm f_\/ (x eqeq y).fm)
		val z3Result = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		z3Result.get.isValid should equal(true)
	}

	test("f2") {
		val x = VarInt("x")
		val y = VarInt("y")
		val c3 = ConstInt("3")
		val c2 = ConstInt("2")
		val c1 = ConstInt("1")
		val minusX = -x
		val f: FOLFormula = ((x + c1) feqeq c3) f_==> (x feqeq c2)
		val z3Result = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0(z3Result.get.modelVarIps)
		z3Result.get.isValid should equal(true)
	}

	//TODO: move to proper place
	test("array select dsl") {
		val arr = VarArrayInt("arr")
		val c2 = ConstInt("2")
		val c100 = ConstInt("100")
		//arr(2) ie. Select(arr, 2)
		val t = arr.select(c2)
		t should equal(ArrSelectInt(arr, c2))
	}

	test("array store dsl") {
		val arr = VarArrayInt("arr")
		val c2 = ConstInt("2")
		val c100 = ConstInt("100")
		//Store(arr, 2, 100)
		val t = arr.store(c2, c100)
		t should equal(ArrStoreArrayInt(arr, c2, c100))
	}

	test("array formula invalid") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = ((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y)

		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0(z3Result.get.modelVarIps)
		z3Result.get.isValid should equal(false)
	}

	test("array formula valid") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = ((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> (x feqeq y)

		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		z3Result.get.isValid should equal(true)
	}

	test("Quantifier invalid") {
		val x = VarInt("x")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val funFn = Fn("fun", List(PSInt, PSInt), PSInt)
		def fun(t1: Term, t2: Term): TermInt = FnAppInt(funFn, List(t1, t2))

		val f: FOLFormula = ▶ ∀ x ∘ (fun(x, x) feqeq x)
		//writeln0(f)
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0(z3Result.get.modelVarIps)
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(false)
	}

	test("Selection Sort 1") {
		val i = VarInt("i")
		val j = VarInt("j")
		val p = VarInt("p")
		val q = VarInt("q")
		val k = VarInt("k")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		var N = VarInt("N")
		var n = VarInt("n")
		val arr = VarArrayInt("arr")
		var f: FOLFormula = (N f_> c0)
		f = And(f, c0 f_<= n)
		f = And(f, n f_< N)
		f = And(f, ▶ ∀ i ∀ j ∘ (((c0 f_<= i) f_/\ (i f_< n) f_/\ (i f_<= j) f_/\ (j f_< N) f_/\ (c0 f_<= j)) impl (arr.select(i) f_<= arr.select(j))))
		f = And(f, n f_< N - c1)
		var f2: FOLFormula = ▶ ∀ p ∀ q ∘ (((c0 f_<= p) f_/\ (p f_< n) f_/\ (p f_<= q) f_/\ (q f_< N) f_/\ (c0 f_<= q)) impl (arr.select(p) f_<= arr.select(q)))
		//f2 = f2 f_/\ (N > c0)
		//f2 = f2 f_/\ (c0 <= n)
		//f2 = f2 f_/\ (n < N - c1)
		//f2 = f2 f_/\ (n + c1 <= n + c1 )
		//f2 = f2 f_/\ (n + c1 <= N )
		//f2 = f2 f_/\ (▶ ∀k ∘(((n <= k) f_/\ (k < n + c1)) impl (arr.select(n) <= arr.select(k))))
		f = f impl f2
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		PSDbg.writeln0("z3Result.get.modelVarIps")
		PSDbg.writeln0(z3Result.get.modelVarIps)
		PSDbg.writeln0("z3Result.get.modelFnIps")
		PSDbg.writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(true)

	}

	test("Selection Sort 2") {
		val i = VarInt("i")
		val j = VarInt("j")
		val p = VarInt("p")
		val q = VarInt("q")
		val k = VarInt("k")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val c5 = ConstInt("5")
		var N = VarInt("N")
		var n = VarInt("n")
		val arr = VarArrayInt("arr")
		var f: FOLFormula = ▶ ∀ i ∘ ((c0 f_<= i) impl (arr.select(i) f_>= c0))
		//var f: FOLFormula = ▶ ∀i∘(arr.select(i) >= c0)
		var f2: FOLFormula = ▶ ∀ j ∘ ((c1 f_<= j) impl (arr.select(j) f_>= c0))
		//var f2: FOLFormula = ▶ ∀p∘(arr.select(p) >= c0)
		//f2 = f2 f_/\ (N > c0)
		//f2 = f2 f_/\ (c0 <= n)
		//f2 = f2 f_/\ (n < N - c1)
		//f2 = f2 f_/\ (n + c1 <= n + c1 )
		//f2 = f2 f_/\ (n + c1 <= N )
		//f2 = f2 f_/\ (▶ ∀k ∘(((n <= k) f_/\ (k < n + c1)) impl (arr.select(n) <= arr.select(k))))
		f = f impl f2
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(true)
	}

	test("Linear Search PO") {
		val i = VarInt("i")
		val j = VarInt("j")
		val x = VarInt("x")
		val y = VarInt("y")

		val p = VarInt("p")
		val q = VarInt("q")
		val v = VarInt("v")
		val k = VarInt("k")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val c5 = ConstInt("5")
		var N = VarInt("N")
		var n = VarInt("n")
		val arr = VarArrayInt("arr")
		var f1: FOLFormula = x f_<= y
		f1 = And(f1, y f_<= N)
		f1 = And(f1, N f_>= c0)
		f1 = And(f1, arr.select(N) f_<= v)
		f1 = And(f1, arr.select(N) f_>= v)
		f1 = And(f1, arr.select(y) f_>= v)
		f1 = And(f1, c0 f_<= x)
		f1 = And(f1, x f_<= N)
		f1 = And(f1, ▶ ∀ i ∘ ((And(c0 f_<= i, i f_< x)) impl (arr.select(i) f_< v)))
		f1 = And(f1, x f_>= y)

		var psi = ▶ ∀ j ∘ (And(c0 f_<= j, j f_< i) impl (arr.select(j) f_< v))
		var psi2 = ▶ ∀ j ∘ (j f_< i)
		var phi = And(And(c0 f_<= i, i f_<= N), psi)
		var f2: FOLFormula = ▶ ∀ i ∘ (▶ ∀ j ∘ (j f_< i))
		f2 = ▶ ∀ i ∀ j ∘ (i f_< j)

		val f = f1 impl f2

		//writeln0("-----------")
		//writeln0(Z3Prover.prepareZ3PyTestCase(Not(f)))
		//writeln0("#############")
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(false)
	}

	test("Array CounterExample") {
		val a = VarInt("a")
		val i = VarInt("i")
		val c2 = ConstInt("2")

		val arr = VarArrayInt("arr")
		val arr2 = VarArrayInt("arr2")
		var f: FOLFormula = (arr.store(i, a).store(i + c2, (arr.store(i, a)).select(i) + c2)).select(i + c2) feqeq (a + c2)
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(true)}
	}

	test("array formula valid2") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = ((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> (x feqeq y)

		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(true)
	}

	test("invalid formula involving uninterpreted function") {
		val x = VarInt("x")
		val y = VarInt("y")
		val fn = Fn("fun", List(PSInt), PSInt)
		val funApp = FnAppInt(fn, List(x))
		val funApp2 = FnAppInt(fn, List(funApp))
		//val f: FOLFormula = (funApp2 eqeq x) f_/\ (funApp eqeq y) f_/\ Not( x eqeq y)
		val f: FOLFormula = Not(funApp2 feqeq x) f_\/ Not(funApp feqeq y) f_\/ (x feqeq y)

		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0("-----------")
		//writeln0(Z3Prover.prepareZ3PyTestCase(Not(f)))
		//writeln0("############")

		assert(z3Result.isDefined)
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(false)
	}

	test("invalid formula involving uninterpreted constant") {
		val x = VarInt("x")
		val y = VarInt("y")
		val a = ConstInt("a")

		val f: FOLFormula = Not(((x - y) feqeq a) f_/\ (x feqeq y))

		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		assert(z3Result.isDefined)
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		z3Result.get.isValid should equal(false)
	}

	test("prepareZ3PyTestCase Uninterpreted const") {
		val x = VarInt("x")
		val y = VarInt("y")
		val a = ConstInt("a")
		val f: FOLFormula = Not(((x - y) feqeq a) f_/\ (x feqeq y))
		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase Simple") {
		val x = VarInt("x")
		val y = VarInt("y")
		val c5 = ConstInt("5")
		val f: FOLFormula = Not(((x - y) feqeq c5) f_/\ (x feqeq y))
		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase invalid formula involving uninterpreted function") {
		val x = VarInt("x")
		val y = VarInt("y")
		val fn = Fn("fun", List(PSInt), PSInt)
		val funApp = FnAppInt(fn, List(x))
		val funApp2 = FnAppInt(fn, List(funApp))
		//val f: FOLFormula = (funApp2 eqeq x) f_/\ (funApp eqeq y) f_/\ Not( x eqeq y)
		val f: FOLFormula = Not(funApp2 feqeq x) f_\/ Not(funApp feqeq y) f_\/ (x feqeq y)

		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase Quantifier invalid") {
		val x = VarInt("x")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val funFn = Fn("fun", List(PSInt, PSInt), PSInt)
		def fun(t1: Term, t2: Term): TermInt = FnAppInt(funFn, List(t1, t2))

		val f: FOLFormula = ▶ ∀ x ∘ (fun(x, x) feqeq x)
		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase array formula invalid") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = ((arr.select(x) feqeq x) f_/\ (arr.store(x, y) feqeq arr)) f_==> Not(x feqeq y)

		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase All Predicates") {
		val x = VarInt("x")
		val y = VarInt("y")
		val z1 = VarInt("z1")
		val z2 = VarInt("z2")
		val z3 = VarInt("z3")
		val z4 = VarInt("z4")
		val z5 = VarInt("z5")
		val z6 = VarInt("z6")
		val z7 = VarInt("z7")
		val z8 = VarInt("z8")
		val z9 = VarInt("z9")
		val z10 = VarInt("z10")
		val c5 = ConstInt("5")
		val f: FOLFormula = ((x - y) feqeq c5) f_/\ (x feqeq y) f_/\ (z2 f_< z3) f_/\ (z3 f_<= z4) f_/\ (z5 f_> z6) f_/\ (z7 f_>= z8) f_/\ (z9 fneq z10)
		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test("prepareZ3PyTestCase True False Iff and Exists") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: FOLFormula = ((x feqeq x) f_/\ ( TrueF f_\/ (x fneq y) )) f_=== (▶ ∃x ∘ (y feqeq x))

		//writeln0(Z3Prover.prepareZ3PyTestCase(f))
	}

	test ("boolean variable Array") {
		val a = VarBool("a")
		val i = VarInt("i")
		val c2 = ConstInt("2")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		var f: FOLFormula = (arr.store(i, a).store(i + c2, (arr.store(i, a)).select(i))).select(i + c2) feqeq (a)
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(true)}
	}

	test ("boolean2") {
		val a = VarBool("a")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val c0 = ConstInt("0")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: FOLFormula = (i feqeq c0) && (arr2 feqeq arr.store(i, a)) impl Exists(j, /*(j > c2) &&*/ (arr2.select(j) feqeq a))
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(true)}
	}

	test( "boolean with Not and passing") {
		val a = VarBool("a")
		val b = VarBool("b")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: FOLFormula = (arr2 feqeq arr.store(i, a)) && (a fneq b) impl Not(Forall(j, (arr2.select(j) feqeq b)))
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(true)}
	}

	test( "boolean and failing 1") {
		val a = VarBool("a")
		val b = VarBool("b")
		val c = VarBool("c")
		val f: FOLFormula = (a && b).fm impl c.fm
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){
			z3Result.isDefined should equal(true)
		}
		PSDbg.writeln1("z3Result.get.modelVarIps")
		PSDbg.writeln1(z3Result.get.modelVarIps)
		PSDbg.writeln1("z3Result.get.modelFnIps")
		PSDbg.writeln1(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){
			z3Result.get.isValid should equal(false)
		}
	}

	test( "boolean and failing 2") {
		val a = VarBool("a")
		val b = VarBool("b")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: FOLFormula = (arr2 feqeq arr.store(i, a)) && (a fneq b) && Forall(j, (arr2.select(j) feqeq b))
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(false)}
	}

	/**Hangs. TODO: set timeout in scalaz3 */
	ignore ("timeout test") {
		val a = VarBool("a")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val c0 = ConstInt("0")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: FOLFormula = (i feqeq c0) && (arr2 feqeq arr.store(i, a)) impl Exists(j, (j f_> c2) && (arr2.select(j) feqeq a))
		val z3Result: Option[Z3Result] = Z3Prover.prove(f)
		//writeln0( (f)
		//writeln0( (Z3Prover.prepareZ3PyTestCase(f))
		withClue ("z3Result.isDefined:"){z3Result.isDefined should equal(true)}
		//writeln0("z3Result.get.modelVarIps")
		//writeln0(z3Result.get.modelVarIps)
		//writeln0("z3Result.get.modelFnIps")
		//writeln0(z3Result.get.modelFnIps)
		withClue("z3Result.isValid: "){z3Result.get.isValid should equal(true)}
	}

}
