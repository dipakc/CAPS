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
class Why3BigOpTest extends FunSuite with ShouldMatchers {

	val testConfig = new PSConfig {
		var configMap: Map[String, Any] = Map(
			"provers.path.z3" -> """D:\ProgramFilesx86\Z3-4.3\bin\z3.exe""",
			"provers.path.why3" -> """D:\ProgramFilesPortable\SSHWrapper\SSHWrapper.bat why3""",
			"provers.path.why3lib" -> """~/ProgSynthWebApp/prjfiles/why3""",
			"provers.sequence" -> List("why3z3"),
			"application.data" -> """data""",
			"ostype" -> "Windows"
		)
	}
	def testf(f: TermBool) = {
		val why3z3prover = new PSWhy3Z3Prover(testConfig)
		val why3z3Result: Try[PSProofStatus] = why3z3prover.prove(f)
		why3z3Result should equal(Success(PSProofValid()))
	}

	def failtestf(f: TermBool) = {
		val why3z3prover = new PSWhy3Z3Prover(testConfig)
		val why3z3Result: Try[PSProofStatus] = why3z3prover.prove(f)
		why3z3Result should not equal(Success(PSProofValid()))
	}

	object Decls {
		val n  = VarInt("n")
		val r  = VarInt("r")
		val i  = VarInt("i")
		val p  = VarInt("p")
		val q  = VarInt("q")
		val N  = VarInt("N")
		val j = VarInt("j")
		val arr  = VarArrayInt("arr")
		val zero = ConstInt("0")
		val one = ConstInt("1")
	}

	test("f0") {
		val f: TermBool = TermBool.TrueT
		testf(f)
	}

	test("sumEmptyRange"){
	    import Decls._
		val f = 0 eqeq PlusQTermInt( i, 0 <= i < 0, arr.select(i))
        testf(f)
	}

	test("prodEmptyRange"){
	    import Decls._
		val f = 1 eqeq TimesQTermInt( i, 0 <= i < 0, arr.select(i))
	    testf(f)
	}

	test("maxOnePoint"){
	    import Decls._
		val f = arr.select(0) eqeq MaxQTermInt( i, 0 <= i < 1, arr.select(i))
        testf(f)
	}

	test("minOnePoint"){
	    import Decls._
		val f = arr.select(0) eqeq MinQTermInt( i, 0 <= i < 1, arr.select(i))
        testf(f)
	}


	test("sumOnePoint"){
	    import Decls._
		val f = arr.select(0) eqeq PlusQTermInt( i, 0 <= i < 1, arr.select(i))
        testf(f)
	}

	test("prodOnePoint"){
	    import Decls._
		val f = arr.select(0) eqeq TimesQTermInt( i, 0 <= i < 1, arr.select(i))
        testf(f)
	}

	test("maxSplitRange"){
	    import Decls._
		val max0n = MaxQTermInt( i, 0 <= i < n, arr.select(i))
		val maxnN = MaxQTermInt( i, n <= i < N, arr.select(i))
		val max0N = MaxQTermInt( i, 0 <= i < N, arr.select(i))

		val f = (0 < n < N) impl (max0N eqeq max(max0n, maxnN))
        testf(f)
	}
	test("minSplitRange"){
	    import Decls._
		val min0n = MinQTermInt( i, 0 <= i < n, arr.select(i))
		val minnN = MinQTermInt( i, n <= i < N, arr.select(i))
		val min0N = MinQTermInt( i, 0 <= i < N, arr.select(i))

		val f = (0 < n < N) impl (min0N eqeq min(min0n, minnN))
        testf(f)
    }

	test("sumSplitRangeFail"){
	    import Decls._
		val plus0n = PlusQTermInt( i, 0 <= i < n, arr.select(i))
		val plusnN = PlusQTermInt( i, n <= i < N, arr.select(i))
		val plus0N = PlusQTermInt( i, 0 <= i < N, arr.select(i))

		val f = plus0N eqeq (plus0n * plusnN)
        failtestf(f)
    }

	test("sumSplitRange"){
	    import Decls._
		val plus0n = PlusQTermInt( i, 0 <= i < n, arr.select(i))
		val plusnN = PlusQTermInt( i, n <= i < N, arr.select(i))
		val plus0N = PlusQTermInt( i, 0 <= i < N, arr.select(i))

		val f = (0 <= n <= N ) impl (plus0N eqeq (plus0n + plusnN))
        testf(f)
    }

	test("sumSplitRangeFail2"){
	    import Decls._
		val plus0n = PlusQTermInt( i, 0 <= i < n, arr.select(i))
		val plusnN = PlusQTermInt( i, n <= i < N, arr.select(i))
		val plus0N = PlusQTermInt( i, 0 <= i < N, arr.select(i))

		val f = !(0 <= n <= N ) impl plus0N eqeq (plus0n + plusnN)
        failtestf(f)
    }

	test("prodSplitRange"){
	    import Decls._
		val times0n = TimesQTermInt( i, 0 <= i < n, arr.select(i))
		val timesnN = TimesQTermInt( i, n <= i < N, arr.select(i))
		val times0N = TimesQTermInt( i, 0 <= i < N, arr.select(i))

		val f = (0 <= n <= N) impl (times0N eqeq (times0n * timesnN))
        testf(f)
    }

	test("prodSplitRangeFail"){
	    import Decls._
		val times0n = TimesQTermInt( i, 0 <= i < n, arr.select(i))
		val timesnN = TimesQTermInt( i, n <= i < N, arr.select(i))
		val times0N = TimesQTermInt( i, 0 <= i < N, arr.select(i))

		val f = !(0 <= n <= N) impl (times0N eqeq (times0n * timesnN))
        failtestf(f)
    }


	test("sumSplitRangeRight"){
	    import Decls._
		val sum0np1 = PlusQTermInt( i, 0 <= i < n + 1, arr.select(i))
		val sum0n = PlusQTermInt( i, 0 <= i < n, arr.select(i))

		val f = (0 <= n) impl (sum0np1 eqeq (sum0n + arr.select(n)))
        testf(f)
    }

	ignore("prodSplitRangeRight"){ //TODO: Failing. CAPS-30
	    import Decls._
		val times0np1 = TimesQTermInt( i, 0 <= i < n + 1, arr.select(i))
		val times0n = TimesQTermInt( i, 0 <= i < n, arr.select(i))

		val f = (0 < n) impl (times0np1 eqeq (times0n * arr.select(n)))
        testf(f)
    }

	test("maxSplitRangeRight"){
	    import Decls._
		val max0np1 = MaxQTermInt( i, 0 <= i < n + 1, arr.select(i))
		val max0n = MaxQTermInt( i, 0 <= i < n, arr.select(i))

		val f = (0 < n) impl (max0np1 eqeq max (max0n, arr.select(n)))
        testf(f)
    }

	test("minSplitRangeRight"){
	    import Decls._
		val min0np1 = MinQTermInt( i, 0 <= i < n + 1, arr.select(i))
		val min0n = MinQTermInt( i, 0 <= i < n, arr.select(i))

		val f = (0 < n) impl (min0np1 eqeq min(min0n, arr.select(n)))
        testf(f)
    }

	test("maxSegSumInit") { // CAPS31
	    import Decls._
	    val f = (n eqeq 0) impl (0 eqeq MaxQTermInt( List(p, q), 0 <= p <= q <= n, PlusQTermInt( i, p <= i < q, arr.select(i))))
	    testf(f)
	}
}
