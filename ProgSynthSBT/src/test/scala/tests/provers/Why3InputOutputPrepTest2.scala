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
class Why3InputOutputPrepTest2 extends FunSuite with ShouldMatchers {
    private val logger = LoggerFactory.getLogger("progsynth.Why3InputOutputPrepTest2")

    def mtest(name: String)(body: => Unit): Unit = {
  		test(name){
  		    val testName = this.getClass.getName() + "." +  name
    	    TestLogHelper.setupSiftLogger(testName, logger)(body)
   		}
	}
	def debugfn(testName: String, proverIn: String) = {
		val winDir = """D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\"""
		val winPath = winDir + testName + """.mlw"""
		val vboxDir = """~/host/Why3ExamplesDipakc/MaxSegSum/"""
		val vboxPath =  vboxDir + testName + ".mlw"

		PSUtils.overwriteFile(winPath, proverIn)
		println("why3output generated. Run the following commands")
        println("notepad++ " + winPath)
		println("why3 prove -P z3 " + vboxPath)
}

	ignore("true") {
		val f: TermBool  = TermBool.TrueT

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)
		//TODO: Fix hardcoded path
		PSUtils.overwriteFile("""D:\VirtualBoxShare\Why3ExamplesDipakc\MaxSegSum\true.mlw""", proverIn)
		0 should equal(0)
	}

	ignore("and") {
	    val a = VarBool("a")
	    val b = VarBool("b")
		val f: TermBool  = a && b

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("and", proverIn)

		0 should equal(0)
	}

	ignore("max_one_point") {
		val n  = VarInt("n")
		val i  = VarInt("i")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		val f: TermBool  =
		    (n eqeq one)
		    .impl(
		        MaxQTermInt(i, zero <= i && i < n, a.select(i) )
                .eqeq (a.select(zero)))

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("max_one_point", proverIn)

		0 should equal(0)
	}


	//Declare HO functions
	ignore("HO_functions") {
		val n  = VarInt("n")
		val i  = VarInt("i")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		/**
		 * sf = (Max i: 0 <= i < n: a[i])
		 * f = sf eqeq sf
		 * */
		val sf: TermInt  =
		    MaxQTermInt(i, zero <= i && i < n, a.select(i))
		val f = sf eqeq sf

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("ho_functions", proverIn)

		0 should equal(0)
	}

	ignore ("freshFunctionConstants") {
		val n  = VarInt("n")
		val i  = VarInt("i")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		/**
		 *
		 * f1 = (Max i: 0 <= i < n: 1 + a[i])
		 * f2 = (Max i: 0 <= i < n: a[i])
		 * Goal G:
		 *  n >= 1 ->
		 *  f1 eqeq 1 + f2
		 * */
		val f1: TermInt  =
		    MaxQTermInt(i, zero <= i && i < n, one + a.select(i))

		val f2: TermInt  =
		    MaxQTermInt(i, zero <= i && i < n, a.select(i))

	    val f = (n >= one) impl (f1 eqeq (one + f2))

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("freshFunctionConstants", proverIn)

		0 should equal(0)
	}

	ignore ("ArraySumGoal") {
		val n  = VarInt("n")
		val s  = VarInt("s")
		val i  = VarInt("i")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		/**
		 * n >= 1 ->
		 * 		s = (Sum i: 0 <= i < n: a[i])
		 * */

	    val f = (n >= one) impl
	            (s eqeq PlusQTermInt(i, zero <= i && i < n, a.select(i)))

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("ArraySumGoal", proverIn)

		0 should equal(0)
	}

	ignore("nested_functions") {
		val n  = VarInt("n")
		val i  = VarInt("i")
		val j = VarInt("j")
		val a  = VarArrayInt("a")
		val zero = ConstInt("0")
		val one = ConstInt("1")

		/**
		 * sf = (Max i: 0 <= i < n:
		 *     		(Max j: 0 <= j < i: i + j))
		 * f = sf eqeq sf
		 * */
		val sf: TermInt  =
            MaxQTermInt(i, zero <= i && i < n,
		                MaxQTermInt(j, zero <= j && j < n, i + j))
		val f = sf eqeq sf

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(f)

		debugfn("nested_functions", proverIn)

		0 should equal(0)
	}

	ignore("MaxSegInvInit") {
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

		/**
		 * arr[0] = (Max p q: 0 <= p <= q <= 1:
		 *     		(Sum i: p <= i < q: a[i]))
		 * */
		val f1 = 0 eqeq MaxQTermInt( List(p,q), 0 <= p <= q <= 0, PlusQTermInt(i, p <= i < q, a(i)))

		val f2 = 0 eqeq
		    MaxQTermInt( p, 0 <= p <= 0,
		        MaxQTermInt(q, p <= q <= 0, PlusQTermInt(i, p <= i < q, a(i))))

		val goal: TermBool = f1

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(goal)

		debugfn("MaxSegInvInit", proverIn)

		0 should equal(0)
	}

	ignore("RemoveNesting") {
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

		/**
		 * (Max p q: 0 <= p <= q <= 0: (Sum i: p <= i < q: a[i]))
		 * */
		val f1 = MaxQTermInt( List(p,q), 0 <= p <= q <= 0, PlusQTermInt(i, p <= i < q, a(i)))

		val t = new MkNestedQTerm{}.mkNestedQTerm(f1)
		println(t.pprint())

		0 should equal(0)
	}

	test("LoHiAndHO") {
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

		/**
		 * (Max i: 0 <= i < 6) = (Max i: 0 <= i < 6)
		 *
		 * */
		val f1 = 0 eqeq MaxQTermInt( List(p,q), 0 <= p <= q <= 0, PlusQTermInt(i, p <= i < q, a(i)))

		val f2 = 0 eqeq
		    MaxQTermInt( p, 0 <= p <= 0,
		        MaxQTermInt(q, p <= q <= 0, PlusQTermInt(i, p <= i < q, a(i))))

		val goal: TermBool = f1

		val proverIn = new Why3InputPrep(Why3TBFactory4).mkProverInput(goal)

		debugfn("LoHiAndHO", proverIn)

		0 should equal(0)
	}

	//SSHWrapper.bat why3 prove -P z3 ~/host/Why3ExamplesDipakc/MaxSegSum/InitThmA0.mlw
}