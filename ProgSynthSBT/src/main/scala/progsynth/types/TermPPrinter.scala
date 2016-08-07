package progsynth.types

import progsynth.methodspecs.InterpretedFns
import scalaz._
import Scalaz._

/**
 * {{{
 *   	var x1: Term = ( a + b ) * ( c + d)
 * x1.pprint                         //> res0: String = (a + b) * (c + d)
 * x1 = -(a + b)
 * x1.pprint                         //> res1: String = -(a + b)
 * x1 = -(a + b * c)
 * x1.pprint                         //> res2: String = -(a + b * c)
 * x1 = -a
 * x1.pprint                         //> res3: String = -a
 * x1 = arr.select(c5)
 * x1.pprint                         //> res4: String = arr[5]
 * x1 = arr.select(a + b * c)
 * x1.pprint                         //> res5: String = arr[a + b * c]
 * x1 = arr.select((arr.select(a) + c2) * c3)
 * x1.pprint                         //> res6: String = arr[(arr[a] + 2) * 3]
 * x1 = (arr.store(c1, c2)).select(c3)
 * x1.pprint                         //> res7: String = (arr[1] := 2)[3]
 * x1 = (arr.store(c1, c2 * c3)).select(c3)
 * x1.pprint                         //> res8: String = (arr[1] := 2 * 3)[3]
 * x1 = (arr.store(c1, c2 * c3 + c4)).select(c6)
 * x1.pprint                         //> res9: String = (arr[1] := 2 * 3 + 4)[6]
 * x1 = (arr.store((c1 + c2) * c3, (c3 + c4)*c5)).select(c7)
 * x1.pprint                         //> res10: String = (arr[(1 + 2) * 3] := (3 + 4) * 5)[7]
 * x1 = arr.store(c1, c2)
 * x1.pprint                         //> res11: String = arr[1] := 2
 * x1 = ((arr.store(c1, c2)).select(c2) + c3) * c4
 * x1.pprint                         //> res12: String = ((arr[1] := 2)[2] + 3) * 4
 * x1 = c3 + arr.select(c5)
 * x1.pprint                         //> res13: String = 3 + arr[5]
 * x1 = (c3 + arr.select(c5)) * c4
 * x1.pprint                         //> res14: String = (3 + arr[5]) * 4
 * x1 = (c3 + arr.select(c5)) + c4
 * x1.pprint                         //> res15: String = 3 + arr[5] + 4
 * x1 = c3 + arr.select(c5) * c4
 * x1.pprint                         //> res16: String = 3 + arr[5] * 4
 * }}}
 */
trait TermPPrinter { self: Term =>
	// docs/ProgSynthWebAppDoc.html#termprinters

    /** Returns a parenthesized string of the Term. No outermost parenthesis. */
    def pprint(): String = {
        import InterpretedFns._
        self match {
            case aVar: Var => aVar.v
            case aConst: Const => aConst.name
	        case FnApp(aFn, t1 :: t2 :: Nil) if isInfix(aFn) =>
	        	<a>{ t1.pprint(self) } {aFn |> getTextSymbol |> (_.get)} { t2.pprint(self) }</a>.text
            case FnApp(UnaryMinusIntFn, t1 :: Nil) =>
                <a>-{ t1.pprint(self) }</a>.text
            case FnApp(aFn, ts) =>
                <a>{ aFn.name }({ (ts map (_.pprint(self))) mkString (", ") })</a>.text
            case ArrSelect(arr, index) =>
                <a>{ arr.pprint(self) }[{ index.pprint }]</a>.text
            case ArrStore(arr, index, value) =>
                <a>{ arr.pprint(self) }[{ index.pprint }] := { value.pprint(self) }</a>.text
			case QTerm(aFn, dummies, range, term) =>
				val argTpes = aFn.argTpes
				assert(argTpes.length == 2, "Not a binary function") // ensure that aFn is a binary function
				assert(argTpes.forall(_ == aFn.tpe), "argument type mismatch" )
				<a>({aFn.name.toUpperCase()} {dummies.map(_.v).mkString(", ")}: {range.pprint()}: {term.pprint()})</a>.text

        }
    }

    /** Returns a parenthesized string of the Term. Outermost parenthesis is added only if
     * the parent binding power is higher that the term binding power */
    def pprint(parent: Term): String = {
    	pprint() |> paren(parent.getTermBinding > self.getTermBinding)
    }

    def paren(flag: Boolean)(str: String) = if (flag) "(" + str + ")" else str

    def getTermBinding(): Int = {
        import InterpretedFns._
        self match {
            case _: Var => maxBinding
            case _: Const => maxBinding
            case FnApp(aFn, _) => getBinding(aFn)
            case ArrSelect(_, _) => maxBinding
            case ArrStore(_, _, _) => leastBinding
            case QTerm(_, _, _, _) => maxBinding //throw new RuntimeException("Exception in getTermBinding: QTerm not handled")
        }
    }
}