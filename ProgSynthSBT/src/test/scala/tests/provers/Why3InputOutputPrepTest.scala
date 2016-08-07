package tests.provers

import org.junit.runner.RunWith
import progsynth.utils.PSUtils
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import progsynth.provers._
import progsynth.dsl.ForallQuantifier
import progsynth.methodspecs.InterpretedFns._
import org.slf4j.LoggerFactory
import progsynth.utils.TestLogHelper

@RunWith(classOf[JUnitRunner])
class Why3InputOutputPrepTest extends FunSuite with ShouldMatchers {
    private val logger = LoggerFactory.getLogger("progsynth.Why3InputOutputPrepTest")

    def mtest(name: String)(body: => Unit): Unit = {
  		test(name){
  		    val testName = this.getClass.getName() + "." +  name
    	    TestLogHelper.setupSiftLogger(testName, logger)(body)
   		}
	}

    //TODO: Should fail as it is using infinity
	mtest("min_empty_range") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val infinity = ConstInt("infinity")

		val f: TermBool  = ((m eqeq infinity) && (n eqeq c0)) impl (m eqeq MinQTermInt(i, c0 <= i && i < n, a.select(i) ))

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		//TODO: Fix hardcoded path
		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\Why3InputOutputPrepTest.mlw""", proverIn)
		0 should equal(0)
	}

	mtest("min_induction_SIM") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")

		val min_n_plus_1 = MinQTermInt(i, 0 <= i < n + 1, a(i) )
		val min_n = MinQTermInt(i, 0 <= i < n, a(i) )
		val f: TermBool  = min_n_plus_1 eqeq min(min_n, a(n))

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\Why3InputOutputPrepTest2.mlw""", proverIn)
		0 should equal(0)
	}

	mtest("max_induction_SIM") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")

		val max_n_plus_1 = MaxQTermInt(i, c0 <= i && i < n + c1, a.select(i) )
		val max_n = MaxQTermInt(i, c0 <= i && i < n, a.select(i) )

		val f: TermBool  = max_n_plus_1 eqeq FnApp(MaxIntFn, max_n :: a.select(n) :: Nil)
//		val f: TermBool  = min_n_plus_1 eqeq n

//		val f: TermBool = TermBool.TrueT
		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\Why3InputOutputPrepTest4.mlw""", proverIn)
		0 should equal(0)
	}

	mtest("plus_induction_SIM") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")

		val plus_n_plus_1 = PlusQTermInt(i, c0 <= i && i < n + c1, a.select(i) )
		val plus_n = PlusQTermInt(i, c0 <= i && i < n, a.select(i) )

		val f: TermBool  = plus_n_plus_1 eqeq FnApp(PlusIntFn, plus_n :: a.select(n) :: Nil)
//		val f: TermBool  = min_n_plus_1 eqeq n

//		val f: TermBool = TermBool.TrueT
		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\Why3InputOutputPrepTest5.mlw""", proverIn)
		0 should equal(0)
	}

	mtest("times induction SIM") {
		val m  = VarInt("m")
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val c0 = ConstInt("0")
		val c1 = ConstInt("1")

		val times_n_plus_1 = TimesQTermInt(i, c0 <= i && i < n + c1, a.select(i) )
		val times_n = TimesQTermInt(i, c0 <= i && i < n, a.select(i) )

		val f: TermBool  = times_n_plus_1 eqeq FnApp(TimesIntFn, times_n :: a.select(n) :: Nil)
//		val f: TermBool  = min_n_plus_1 eqeq n

//		val f: TermBool = TermBool.TrueT
		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\Why3InputOutputPrepTest6.mlw""", proverIn)
		0 should equal(0)
	}
	//SSHWrapper.bat why3 prove -P z3 ~/host/Why3ExamplesDipakc/MaxSegSum/InitThmA0.mlw

}