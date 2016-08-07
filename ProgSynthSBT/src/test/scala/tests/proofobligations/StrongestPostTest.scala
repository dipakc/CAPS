package tests.proofobligations

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns
import TermBool.TrueT
import TermBool.FalseT

@RunWith(classOf[JUnitRunner])
class StrongestPostTest extends FunSuite with ShouldMatchers {
	
	import progsynth.proofobligations.StrongestPost
	
	test("xxx") {
		val n0 = VarInt("n0")
		val n = VarInt("n")
		val r = VarBool("r")
		val c0 = ConstInt("0")
		val f = ExistsTermBool(n0, (r eqeq TrueT) && (n0 eqeq c0) && (n eqeq c0))
		val actual = f
		val expected = f
		actual should equal(expected)
	}
}