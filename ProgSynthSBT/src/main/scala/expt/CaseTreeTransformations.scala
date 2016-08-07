package expt
import progsynth.debug.PSDbg._

object CaseTreeTransformations extends App{
	abstract class Expr
	case class Var(var name: String) extends Expr
	case class Number(var num: Double) extends Expr
	case class UnOp(var operator: String, var arg: Expr) extends Expr
	case class BinOp(var operator: String, var left: Expr, var right: Expr) extends Expr
	case class HHH() extends Expr

	val anExpr = BinOp("*", BinOp("%", BinOp("-", Number(1), Var("x")), BinOp("+", Number(2), Var("y"))), BinOp("*", Number(3), Var("x")))

	def aMethod (expr: Expr, ctx: Expr) = {
		//writeln0("aMethod _in : " + expr)
		val anExpr = expr match {
			case binop @ BinOp("*", left, right ) =>
				binop.copy(operator="*#", aMethod2(left, binop.copy(left = HHH())), aMethod2(right, binop.copy(right=HHH())))
			case _ => expr
		}
		//writeln0("aMethod _out: " + anExpr)
		anExpr
	}

	def aMethod2 (expr: Expr, ctx: Expr): Expr = {
		//writeln0("aMethod2_in : " + expr)
		val anExpr = expr match {
			case Var("x") => Var("x#")
			case unop @ UnOp(n, arg) => UnOp(n, aMethod2(arg, unop.copy(arg= HHH())))
			case binop @ BinOp(n, left, right) => BinOp(n, aMethod2(left, binop.copy(left=HHH())), aMethod2(right, binop.copy(right=HHH())))
			case Number(_) => expr
			case _ => expr
		}
		//writeln0("aMethod2_out: " + anExpr)
		anExpr
	}

	val a = Number(3)
	val b = Var("x")
	val abc = BinOp("*", a, b)

	def myMethod(b: Expr, abc:Expr) = {
		//writeln0(b.hashCode())
		//writeln0(abc.asInstanceOf[BinOp].right.hashCode())
	}
	myMethod(b, abc)
	//aMethod(anExpr, anExpr)


}