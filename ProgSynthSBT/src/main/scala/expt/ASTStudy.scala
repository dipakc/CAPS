package expt
import progsynth._
import progsynth.types._
import progsynth.types.Types._
import z3.scala._
import progsynth.methodspecs.InterpretedFns._
import progsynth.debug.PSDbg._
/*
object TempZ3 extends App {
	val typeOfx = PSInt
	//val f1 = Atom(Pred("==", VarInt("x") :: ConstInt("2") :: Nil), PSInt) //x == 2
	val f2 = Atom(Pred("==", VarInt("y") :: FnAppInt(PlusIntFn, VarInt("x") :: ConstInt("1") :: Nil) :: Nil)) //y == x + 1
	val f3 = Atom(Pred("==", VarInt("y") :: ConstInt("3") :: Nil)) //y == 3

	def isValid(f: FOLFormula): Boolean = {
		false
	}

	//logln("TempZ3")

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
			//logln("x: " + model.evalAs[Int](x))
			//logln("y: " + model.evalAs[Int](y))
			model.delete
		}
	}

	z3.delete
}

class TempZ32 {
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
			writeln0("h: " + model.evalAs[Int](h))
			writeln0("m: " + model.evalAs[Int](m))
			writeln0("s: " + model.evalAs[Int](s))
			model.delete
		}
	}

	z3.delete
}
*/
/*
	def getProgGraph(annProg: ProgramAnn /*, preIdO: Option[Int], postIdO: Option[Int]*/ ): ProgGraph =
		{
			case class GrdSubGraph(guard: FOLFormula, cmdGraph: ProgGraph)
			annProg match {
				case funProg @ FunctionProg(name, annProg) =>
					getProgGraph(annProg)
				case ifProg @ IfProg(grdcmds) =>
					val grdSubGraphs = grdcmds map { grdcmd =>
						GrdSubGraph(guard = grdcmd.guard, cmdGraph = getProgGraph(grdcmd.cmd))
					}

					val grdNodes = grdSubGraphs map { grdSubGraph =>
						val grdNode = Node(uid = NodeIdGen.getId,
							f = grdSubGraph.guard,
							childs = Nil,
							outEdges = Nil)
						val grdToCmdEdge = Edge(start = grdNode,
							lbl = None,
							end = grdSubGraph.cmdGraph.start,
							innerEnd = None)
						grdNode.outEdges = List(grdToCmdEdge)
						grdNode
					}
					var cmdPostNodes = grdSubGraphs map { grdSubGraph =>
						grdSubGraph.cmdGraph.end
					}
					val preNode = Node(uid = NodeIdGen.getId,
						f = ifProg.pre.formula,
						childs = grdNodes,
						outEdges = Nil)
					val postNode = Node(uid = NodeIdGen.getId,
						f = ifProg.post.formula,
						childs = cmdPostNodes,
						outEdges = Nil)
					ProgGraph(start = preNode, end = postNode, lbl = None)
				case whileProg @ WhileProg(_, grdcmds) =>
					val grdSubGraphs = grdcmds map { grdcmd =>
						GrdSubGraph(guard = grdcmd.guard, cmdGraph = getProgGraph(grdcmd.cmd))
					}

					//beta1 to phi
					grdSubGraphs map { grdSubGraph =>
						grdSubGraph.cmdGraph.end.outEdges = List(
								Edge(start = grdSubGraph.cmdGraph.end,
									lbl = None,
									end = loopInvNode,
									innerEnd = None
									)
							)
					}

					//grd1
					val grdNodes = grdSubGraphs map { grdSubGraph =>
						val grdNode = Node(uid = NodeIdGen.getId,
							f = grdSubGraph.guard,
							childs = Nil,
							outEdges = Nil)
						val grdToCmdEdge = Edge(start = grdNode,
							lbl = None,
							end = grdSubGraph.cmdGraph.start,
							innerEnd = None)
						grdNode.outEdges = List(grdToCmdEdge)
						grdNode
					}
					//phi
					val loopInvNode = Node(uid = NodeIdGen.getId,
						f = whileProg.loopInv.get, //TODO: what if not specified
						childs = grdNodes,
						outEdges = Nil)
					//alpha to phi
					val preToLoopInv: Edge = Edge(start = preNode, lbl = None, end = loopInvNode, innerEnd = None)
					//alpha
					val preNode = Node(uid = NodeIdGen.getId,
						f = whileProg.pre.formula,
						childs = Nil,
						outEdges = List(preToLoopInv))
					//beta
					val postNode = Node(uid = NodeIdGen.getId,
						f = whileProg.post.formula,
						childs = Nil,
						outEdges = Nil)
					ProgGraph(start = preNode, end = postNode, lbl = None)
				case compProg @ Composition(programs) => ""
				case asgn @ Assignment(_, rhs) => ""
				case SkipProg() => ""
				case UnknownProg(id) => ""
				case Identifier(_, _) => ""
				case LitConstant(_) => ""
				case ExprProg(_) => ""
			}
		}
}
*/