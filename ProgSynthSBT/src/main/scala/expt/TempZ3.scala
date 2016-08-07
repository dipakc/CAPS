package expt
import progsynth._
import progsynth.types._
import progsynth.types.Types._

import z3.scala._
import progsynth.methodspecs.InterpretedFns._
import progsynth.debug.PSDbg._

/*
object TempZ51 extends App {
	val typeOfx = PSInt
	//val f1 = Atom(Pred("==", VarInt("x") :: ConstInt("2") :: Nil), PSInt) //x == 2
	val f2 = Atom(Pred("==", VarInt("y") :: FnAppInt(PlusIntFn, VarInt("x") :: ConstInt("1") :: Nil) :: Nil)) //y == x + 1
	val f3 = Atom(Pred("==", VarInt("y") :: ConstInt("3") :: Nil)) //y == 3

	def isValid(f: FOLFormula): Boolean = {
		false
	}

	logln("TempZ3")

	val cfg = new Z3Config("MODEL" -> true) // required if you plan to query models of satisfiable constraints
	val z3 = new Z3Context(cfg)

	// prepares the integer sort and three constants (the "unknowns")
	val i = z3.mkIntSort
	val x = z3.mkConst(z3.mkStringSymbol("x"), i)
	val y = z3.mkConst(z3.mkStringSymbol("y"), i)

	// builds a constant integer value from the CL arg.
	val c1 = z3.mkInt(1, i)
	val c2 = z3.mkInt(2, i)
	val c3 = z3.mkInt(3, i)

	// builds the constraint h*3600 + m * 60 + s == totSecs
	val cs1 = z3.mkEq(x, c2)
	val cs2 = z3.mkEq(y, z3.mkAdd(x, c1))
	val cs3 = z3.mkEq(y, c3)

	val cs = z3.mkNot(z3.mkImplies(z3.mkAnd(cs1, cs2), cs3 ))


	// pushes the constraints to the Z3 context
	z3.assertCnstr(cs)

	// attempting to solve the constraints, and reading the result
	z3.checkAndGetModel match {
		case (None, _) => logln("Z3 failed. The reason is: " + z3.getSearchFailure.message)
		case (Some(false), _) => logln("Unsat.")
		case (Some(true), model) => {
			logln("x: " + model.evalAs[Int](x))
			logln("y: " + model.evalAs[Int](y))
			model.delete
		}
	}

	z3.delete
}

class TempZ52 {
	logln("dipakc")
	val totSecs = 12345

	val cfg = new Z3Config("MODEL" -> true) // required if you plan to query models of satisfiable constraints
	val z3 = new Z3Context(cfg)

	// prepares the integer sort and three constants (the "unknowns")
	val i = z3.mkIntSort
	val h = z3.mkConst(z3.mkStringSymbol("h"), i)
	val m = z3.mkConst(z3.mkStringSymbol("m"), i)
	val s = z3.mkConst(z3.mkStringSymbol("s"), i)
	// builds a constant integer value from the CL arg.
	val t = z3.mkInt(totSecs, i)
	// more integer constants
	val z = z3.mkInt(0, i)
	val sx = z3.mkInt(60, i)

	// builds the constraint h*3600 + m * 60 + s == totSecs
	val cs1 = z3.mkEq(
		z3.mkAdd(
			z3.mkMul(z3.mkInt(3600, i), h),
			z3.mkMul(sx, m),
			s),
		t)

	// more constraints
	val cs2 = z3.mkAnd(z3.mkGE(h, z), z3.mkLT(h, z3.mkInt(24, i))) // h > 0 and h < 24
	val cs3 = z3.mkAnd(z3.mkGE(m, z), z3.mkLT(m, sx)) // m > 0 and m < 60
	val cs4 = z3.mkAnd(z3.mkGE(s, z), z3.mkLT(s, sx))// s > 0 and s < 60

	// pushes the constraints to the Z3 context
	z3.assertCnstr(z3.mkAnd(cs1, cs2, cs3, cs4))

	// attempting to solve the constraints, and reading the result
	z3.checkAndGetModel match {
		case (None, _) => logln("Z3 failed. The reason is: " + z3.getSearchFailure.message)
		case (Some(false), _) => logln("Unsat.")
		case (Some(true), model) => {
			logln("h: " + model.evalAs[Int](h))
			logln("m: " + model.evalAs[Int](m))
			logln("s: " + model.evalAs[Int](s))
			model.delete
		}
	}

	z3.delete
}

*/