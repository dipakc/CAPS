
package pswebutils
/*
/** This script is intended to be run from the scala interpreter.
 * Lanuch Scala interpreter (ctrl + 3 -> scala interpreter)
 * Select the text inside the TestScript object and press Ctrl + Shift + x to execute.
 *
 */
object TestScript extends App {
	import pswebutils._
	import progsynth.types._
	import models.parser.FOLFormulaParser
	import models.pprint.FOLFormulaPPrint
	import progsynth.synthesisold.ProgContext
	import scalaz._
	import Scalaz._

	val (x, y, z, w) = (VarInt("x"), VarInt("y"), VarInt("z"), VarInt("w"))
	val (arr, arr2, arr3) = (VarArrayInt("arr"), VarArrayInt("arr2"), VarArrayInt("arr3"))
	val (p, q, r, s) = (VarBool("p"), VarBool("q"), VarBool("r"), VarBool("s"))
	val (barr, barr2, barr3) = (VarArrayBool("barr"), VarArrayBool("bar2"), VarArrayBool("barr3"))

	object PPrintAndParserObj extends FOLFormulaPPrint with FOLFormulaParser{
		override var progCtx =
			new ProgContext(varList = Nil,
				valList = List(x, y, z, w, arr, arr2, arr3, p, q, r, s, barr, barr2, barr3), Nil)
		override val isMQ = false
	}

	import PPrintAndParserObj._

	//////////////////////////////////////////////////////
	object IU {

		private def fun[T](str: String, p: Parser[T], pfun: T => String) = {
			val res = parseAll(p, str)
			if (res.successful) {
				println("parse:" + res.get)
				println("pprint: " + pfun(res.get))
			} else {
				println("parse failed: " + res)
			} //
		}

		// parse
		def pi(str: String) = {parseAll(termIntP, str) |> println _ }
		def pb(str: String) = {parseAll(termBoolP, str) |> println _ }
		def pai(str: String) = {parseAll(termArrayIntP, str) |> println _ }
		def pab(str: String) = {parseAll(termArrayBoolP, str) |> println _ }
		def pf(str: String) = {parseAll(folFormulaP, str) |> println _ }
		//parse and print
		def pi2(str: String) = fun(str, termIntP, pprintTermInt0)
		def pb2(str: String) = fun(str, termBoolP, pprintTermBool0)
		def pai2(str: String) = fun(str, termArrayIntP, pprintTermArrayInt0)
		def pab2(str: String) = fun(str, termArrayBoolP, pprintTermArrayBool0)
		def pf2(str: String) = fun(str, folFormulaP, pprintFOLFormula0)
	}

	IU pi2 "x + y"
	IU pi2 "arr[x]"
	IU pb2 """barr[x] && q"""
	IU pai2 """arr"""
	IU pab2 """barr"""
	IU pf2 """\forall x \exists y : 0 < x  /\ y > 5 \impl (\forall z: z < 5)"""
}
*/