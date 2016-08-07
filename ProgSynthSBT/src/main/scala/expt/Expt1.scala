package expt

import progsynth.debug.PSDbg

trait ExprClasses extends Expt1Utils {
	abstract class Expr {
		override def toString(): String = exprToString(this)
		var id = -1
		def setId(nid: Int) = { id = nid; this }
	}
	case class Var(name: String) extends Expr
	object Var {
		def apply(name: String, id: Int) = new Var(name).setId(id)
	}
	case class Number(num: Int) extends Expr
	object Number {
		def apply(num: Int, id: Int) = new Number(num).setId(id)
	}
	case class UnOp(operator: String, arg: Expr) extends Expr
	object UnOp{
		def apply(operator: String, arg: Expr, id: Int) = new UnOp(operator, arg).setId(id)
	}
	case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
	object BinOp{
		def apply(operator: String, left: Expr, right: Expr, id: Int) =
			new BinOp(operator, left, right).setId(id)
	}
}

object Expt1 extends App with Expt1Utils with ExprClasses {
	def distribStar(subExpr: Expr) = subExpr match {
		case BinOp("*", left, BinOp("+", left2, right2)) =>
			BinOp("+", BinOp("*", left, left2), BinOp("*", left, right2))
		case _ => subExpr
	}

	def distribStarRec(expr: Expr, id: Int): Expr = expr match {
		case _ if expr.id == id => distribStar(expr)
		case Var(_) | Number(_) => expr
		case UnOp(opr, arg) => UnOp(opr, distribStarRec(arg, id))
		case BinOp(opr, left, right) =>
			BinOp(opr, distribStarRec(left, id), distribStarRec(right, id))
	}
	// y * ( x + 1 )   +  (y * (x + 1) + 5)
	val expr1 = BinOp("*", "y", BinOp("+", "x", 1))
	val expr2 = BinOp("+", expr1, 5)
	val expr3 = BinOp("+", expr1, expr2)

	PSDbg.writeln0(setIdR2(expr3))
	// (y *  x + y * 1 )   +  (y * (x + 1) + 5)
	PSDbg.writeln0(distribStarRec(setIdR2(expr3), 10))

}

trait Expt1Utils { self: ExprClasses =>
	def paren(s: String) = "( " + s + " )"
	def parens(s: String) = "" //"[" + s + "]"
	implicit def strToVar(s: String): Var = Var(s)
	implicit def intToNumber(i: Int): Number = Number(i)
	def exprToString(e: Expr): String = e match {
		case Var(name) => name + parens(e.id.toString())
		case Number(num) => num.toString + parens(e.id.toString())
		case UnOp(opr, arg) => paren(opr + arg) + parens(e.id.toString())
		case BinOp(opr, left, right) => paren(List(left, opr, right).mkString(" ")) + parens(e.id.toString())
	}
	def setIdR(e: Expr, id: Int): (Expr, Int) = {
		e match {
			case Var(name) => (Var(name).setId(id), id)
			case Number(num) => (Number(num).setId(id), id)
			case UnOp(opr, arg) =>
				val (newArg, argId) = setIdR(arg, id)
				(UnOp(opr, newArg).setId(argId + 1), argId + 1)
			case BinOp(opr, left, right) =>
				val (newLeft, leftId) = setIdR(left, id)
				val (newRight, rightId) = setIdR(right, leftId + 1)
				(BinOp(opr, newLeft, newRight).setId(rightId + 1), rightId + 1)
		}
	}

	def setIdR2(e: Expr): Expr = {
		var id = 0
		def newId = { id = id + 1; id }
		def setIdR2i(expr: Expr): Expr = expr match {
			case Var(name) => Var(name, newId)
			case Number(num) => Number(num, newId)
			case UnOp(opr, arg) =>
				UnOp(opr, setIdR2i(arg), newId)
			case BinOp(opr, left, right) =>
				BinOp(opr, setIdR2i(left), setIdR2i(right), newId)
		}
		setIdR2i(e)
	}

}