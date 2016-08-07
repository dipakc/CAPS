package models.mqprinter

import progsynth.methodspecs.InterpretedFns._
import progsynth.types._
import progsynth.types.Types._
import MQBindingPowers._
import MQPrinterUtils._
import MQSym._

import progsynth.methodspecs.InterpretedFns
import InterpretedFns.isInfix
import MQBindingPowers._

/**
 * - Binding power of all the atomic constructs in MAXBP
 * - Binding power of all the constructs whose concrete representation is surrounded by brackets have MAXBP binding power
 * - Atomic Constructs(maxBP) - III(intBP) - IIB(predBP) - BBB(boolBP)- minBP
 * - No outer parenthesis if child constructs have more binding power than parent construct.
 *   eg (5 + (2 * 4)) = 5 + 2 * 4
 * - Add outer parenthesis if child constructs have less binding power than parent constuct
 * 	 eg (5 * (2 + 4)) = 5 * (2 + 4)
 * - If the concrete representation makes it impossible for childs to interact with outer context,
 * 		then you don't need to parathesize the childs. This can be achieved by calling mqprintTerm0
 */

object MQPrinter extends MQPrinter

trait MQPrinter {

	//parentBP is minBP when mqprint is called at the outermost level
	def mqprintTerm0(term: Term) = mqprintTerm(term, minBP)

	def mqprintTerm(term: Term, parentBP: Int): String = {
		import MQSym._

		val selfBp = getBP(term)
		def rec(t: Term) = mqprintTerm(t, selfBp)
		def rec0(t: Term) = mqprintTerm0(t)
		def csv(xs: List[String]) = xs.mkString(COMSP)
		def varcsv(vs: List[Var]) = csv(vs.map(_.v))

		var retVal = term match {
			case Const(name) =>
				name
				.replaceAll("""\(""", """\\left(""")
				.replaceAll("""\)""", """\\right)""")
			case Var(v) => v
			case FnApp(bfn, List(t1, t2) ) if isInfix(bfn) =>
				rec(t1) + space(getSym(bfn)) + rec(t2)
			case FnApp(ufn, List(t1)) if isPrefixWithSym(ufn) =>
				val sym = getSym(ufn)
				//no space in case of unary minus
				val csp = if (sym.contains("\\")) SP else ""
				sym + csp + rec(t1)
			case FnApp(aFn, ts) =>
				val tsCSV = csv(ts.map(rec0(_)))
				getSym(aFn) + paren(tsCSV)
			case ArrSelect(arr, index) =>
				rec(arr) + sparen(rec(index))
			case ArrStore(arr, index, value) =>
				paren(rec(arr) + sparen(rec(index)) + space(ASGN) + rec(value))
			case QTerm(aFn, dummies, range, term) =>
				val argTpes = aFn.argTpes
				assert(argTpes.length == 2) // ensure that aFn is a binary function
				val tpe = aFn.tpe
				assert(argTpes.forall(_ == tpe) )
				val qSym = getQSym(aFn)
				paren( qSym + SP + varcsv(dummies) + surround(rec0(range), COL + SP) + rec0(term))
		}
		//Parenthesize only if parentBP is greater than selfBP
		parenIf(retVal, parentBP > selfBp)
	}
}


