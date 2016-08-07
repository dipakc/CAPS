package tests.types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import tests.testutils.PSPropertyChecks
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import progsynth.types._
import progsynth.types.Types._
import progsynth.types.TermGen._

@RunWith(classOf[JUnitRunner])
class TermGenTest  extends PSPropertyChecks with ShouldMatchers with FunSuite {
	test("test1") {
		forAll(termGenHt(1)) { term: Term =>
			term match {
				case _: TermArrayBool  | _: TermArrayInt | _: TermArrayReal=>
					val arrTpe = term.getType()
					val basicTpe = PSType.getBasicTpe(arrTpe)
					term match {
						case _: Var =>
						case _: Const =>
						case FnApp(fn, argList) =>
							fn.tpe should equal (arrTpe)
							fn.argTpes should equal (argList.map(_.getType()))
						case _: ArrSelect =>
							throw new RuntimeException("ArrSelect is of type " + arrTpe)
						case ArrStore(arr, index, value) =>
							arr.getType should equal (arrTpe)
							index.getType should equal (PSInt)
							value.getType should equal (basicTpe)
					}
				case _: ArrSelect  =>
					//Unit arrays not supported
					term.getType() should not equal (PSUnit)
				case _ =>
			}
		}
	}
}