package tests.provers

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.provers._
import progsynth.dsl.ForallQuantifier
import scala.util.{Try, Success, Failure}
import progsynth.config.PSConfig
import progsynth.methodspecs.InterpretedFns._

@RunWith(classOf[JUnitRunner])
class PSProverMgrTest extends FunSuite with ShouldMatchers {

	val testConfig = new PSConfig {
		var configMap: Map[String, Any] = Map(
			"provers.path.z3" -> """D:\ProgramFilesx86\Z3-4.3\bin\z3.exe""",
			"provers.path.why3" -> """D:\ProgramFilesPortable\SSHWrapper\SSHWrapper.bat why3""",
			"provers.path.why3lib" -> """~/ProgSynthWebApp/prjfiles/why3""",
			"provers.sequence" -> List("z3"),
			"application.data" -> """data""",
			"ostype" -> "Windows"
		)
	}


	test("f0") {
		val f: TermBool = TermBool.TrueT
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

    // Use eqeq for x == y.
	// It is not advisable to override "==" of Var
	test("f1") {
		val x = VarInt("x")
		val y = VarInt("y")
		val f: TermBool = (x <= y) impl ( (x < y) || (x eqeq y))
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	test("f2") {
		val x = VarInt("x")
		val y = VarInt("y")
		val c3 = ConstInt("3")
		val c2 = ConstInt("2")
		val c1 = ConstInt("1")
		val minusX = -x
		val f: TermBool = ((x + c1) eqeq c3) impl (x eqeq c2)
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
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
		val f: TermBool = ((arr.select(x) eqeq x) && (arr.store(x, y) eqeq arr)) impl !(x eqeq y)

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}

	test("array formula valid") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: TermBool = ((arr.select(x) eqeq x) && (arr.store(x, y) eqeq arr)) impl (x eqeq y)

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	ignore("Quantifier invalid") { //TODO: uninterpreted functions
		val x = VarInt("x")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val funFn = Fn("fun", List(PSInt, PSInt), PSInt)
		def fun(t1: Term, t2: Term): TermInt = FnAppInt(funFn, List(t1, t2))

		val f: TermBool = ForallTermBool(x, TermBool.TrueT, fun(x, x) eqeq x)
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
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
		var f: TermBool = (N > c0)
		f = f && (c0 <= n)
		f = f &&  (n < N)
		f = f &&  ForallTermBool(List(i, j),(c0 <= i) && (i < n) && (i <= j) && (j < N) && (c0 <= j), arr.select(i) <= arr.select(j))
		f = f && n < N - c1
		var f2: TermBool = ForallTermBool(List(p,q),(c0 <= p) && (p < n) && (p <= q) && (q < N) && (c0 <= q), arr.select(p) <= arr.select(q))
		f = f impl f2
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
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
		var f: TermBool = ForallTermBool(i, c0 <= i, arr.select(i) >= c0)
		var f2: TermBool = ForallTermBool(j, c1 <= j, arr.select(j) >= c0)
		f = f impl f2
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	//why3z3 times out for this
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
		var f1: TermBool = x <= y
		f1 = f1 && y <= N
		f1 = f1 && N >= c0
		f1 = f1 && (arr.select(N) <= v)
		f1 = f1 && (arr.select(N) >= v)
		f1 = f1 && (arr.select(y) >= v)
		f1 = f1 && c0 <= x
		f1 = f1 && x <= N
		f1 = f1 && ForallTermBool(i, c0 <= i && i < x, arr.select(i) < v)
		f1 = f1 && x >= y

		var psi = ForallTermBool( j,c0 <= j && j < i, arr.select(j) < v)
		var psi2 = ForallTermBool(j, TermBool.TrueT, j < i)
		var phi = c0 <= i &&  i <= N && psi
		var f2: TermBool = ForallTermBool( List(i, j), TermBool.TrueT, j < i)
		f2 = ForallTermBool(List(i, j), TermBool.TrueT, i < j)
		val f = f1 impl f2
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}

	test("Array CounterExample") {
		val a = VarInt("a")
		val i = VarInt("i")
		val c2 = ConstInt("2")

		val arr = VarArrayInt("arr")
		val arr2 = VarArrayInt("arr2")
		var f: TermBool = (arr.store(i, a).store(i + c2, (arr.store(i, a)).select(i) + c2)).select(i + c2) eqeq (a + c2)
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	test("array formula valid2") {
		val arr = VarArrayInt("arr")
		val x = VarInt("x")
		val y = VarInt("y")
		val f: TermBool = ((arr.select(x) eqeq x) && (arr.store(x, y) eqeq arr)) impl (x eqeq y)

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	ignore("invalid formula involving uninterpreted function") {//TODO
		val x = VarInt("x")
		val y = VarInt("y")
		val fn = Fn("fun", List(PSInt), PSInt)
		val funApp = FnAppInt(fn, List(x))
		val funApp2 = FnAppInt(fn, List(funApp))
		//val f: TermBool = (funApp2 eqeq x) && (funApp eqeq y) && Not( x eqeq y)
		val f: TermBool = NotTermBool(funApp2 eqeq x) || NotTermBool(funApp eqeq y) || (x eqeq y)

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}

	ignore("invalid formula involving uninterpreted constant") {//TODO
		val x = VarInt("x")
		val y = VarInt("y")
		val a = ConstInt("a")

		val f: TermBool = NotTermBool(((x - y) eqeq a) && (x eqeq y))

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}


	test ("boolean variable Array") {
		val a = VarBool("a")
		val i = VarInt("i")
		val c2 = ConstInt("2")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		var f: TermBool = (arr.store(i, a).store(i + c2, (arr.store(i, a)).select(i))).select(i + c2) eqeq (a)
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
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
		val f: TermBool = (i eqeq c0) && (arr2 eqeq arr.store(i, a)) impl ExistsTermBool(j, TermBool.TrueT, arr2.select(j) eqeq a)

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
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
		val f: TermBool = (arr2 eqeq arr.store(i, a)) && (a neqeq b) impl NotTermBool(ForallTermBool(j, TermBool.TrueT, (arr2.select(j) eqeq b)))
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	test( "boolean and failing 1") {
		val a = VarBool("a")
		val b = VarBool("b")
		val c = VarBool("c")
		val f: TermBool = (a && b) impl c
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
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
		val f: TermBool = (arr2 eqeq arr.store(i, a)) && (a neqeq b) && ForallTermBool(j, (arr2.select(j) eqeq b))
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}

	/**This test case hangs in scalaz3 */
	test ("long test") {
		val a = VarBool("a")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val c0 = ConstInt("0")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: TermBool = (i eqeq c0) && (arr2 eqeq arr.store(i, a)) impl ExistsTermBool(j, j > c2, arr2.select(j) eqeq a)
		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}

	test ("timeout test") {
		val a = VarBool("a")
		val i = VarInt("i")
		val j = VarInt("j")
		val c2 = ConstInt("2")
		val c0 = ConstInt("0")
		val cfalse = ConstBool("false")

		val arr = VarArrayBool("arr")
		val arr2 = VarArrayBool("arr2")
		val f: TermBool = (i eqeq c0) && (arr2 eqeq arr.store(i, a)) impl ExistsTermBool(j, j > c2, arr2.select(j) eqeq a)

		val z3prover = new PSZ3Prover(testConfig)
		z3prover.config.setParam("timeout", "1000")
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		println(z3Result)
		val status = z3Result match {
		    case Success(PSProofTimeout(_)) => true
		    case _ => false
		}
		status should equal(true)
	}

	test("Power operator Success") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val c2 = ConstInt("2")
		val c10 = ConstInt("10")
		val c100 = ConstInt("100")

		val f: TermBool  = FnApp(PowIntFn, c10 :: c2 :: Nil) eqeq c100

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		z3Result should equal(Success(PSProofValid()))
	}

	test("Power operator Invalid") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")
		val c2 = ConstInt("2")
		val c10 = ConstInt("10")
		val c20 = ConstInt("20")

		val f: TermBool  = FnApp(PowIntFn, c10 :: c2 :: Nil) eqeq c20

		val z3prover = new PSZ3Prover(testConfig)
		val z3Result: Try[PSProofStatus] = z3prover.prove(f)
		println(z3Result)
		val res = z3Result match {
		    case Success(PSProofInvalid(_)) => true
		    case _ => false
		}
		res should equal(true)
	}
}