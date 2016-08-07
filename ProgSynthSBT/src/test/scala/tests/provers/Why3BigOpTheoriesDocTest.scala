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
class Why3BigOpTheoriesDocTest extends FunSuite with ShouldMatchers {

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

	ignore("f0") {
	    val f: TermBool = TermBool.TrueT

        for (mtb <- Why3TheoryBuilder.getTheoryBuilderFactories) {
            println(s"TheoryId: ${mtb.getId}")
		    val why3z3prover = new PSWhy3Z3Prover(testConfig, mtb)
		    val why3z3Result: Try[PSProofStatus] = why3z3prover.prove(f)
		    why3z3Result should equal(Success(PSProofValid()))
        }
	}

	ignore("f1"){
		val n  = VarInt("n")
		val r  = VarInt("r")
		val i  = VarInt("i")
		val p  = VarInt("p")
		val q  = VarInt("q")
		val N  = VarInt("N")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		//TODO: make separate tests for each theory
		val f = a.select(0) eqeq MaxQTermInt( i, 0 <= i < 1, a.select(i))
		for (mtb <- Why3TheoryBuilder.getTheoryBuilderFactories) {
            println(s"TheoryId: ${mtb.getId}")
    		val why3z3prover = new PSWhy3Z3Prover(testConfig, mtb)
    		val why3z3Result: Try[PSProofStatus] = why3z3prover.prove(f)
    		why3z3Result should equal(Success(PSProofValid()))
		}
	}


	test("MaxSegSumInit") {
		val n  = VarInt("n")
		val r  = VarInt("r")
		val i  = VarInt("i")
		val p  = VarInt("p")
		val q  = VarInt("q")
		val N  = VarInt("N")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")
		val sum = PlusQTermInt( i, p <= i < q, a.select(i))
		val max = MaxQTermInt( p :: q :: Nil, 0 <= p <= q <= 0, sum)

	    val f = 0 eqeq max

	    val prover = new PSWhy3Z3Prover(testConfig, Why3TBFactory4)
		val result: Try[PSProofStatus] = prover.prove(f)
		println(result)

		result should equal(Success(PSProofValid()))
	}
}