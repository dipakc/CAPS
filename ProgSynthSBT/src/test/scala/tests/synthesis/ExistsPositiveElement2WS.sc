package tests.synthesis
import progsynth.types._
//import ProgramAuxConstructors._
//import progsynth.dsl.FOLFormulaDSL._
//import FOLFormulaRich._
import progsynth.debug.FormulaParseTreeViewer
import scalaz.{Const => SConst, _}
import Scalaz._

object ExistsPositiveElement2WS {
		//def updatePost(prog: ProgramAnn, formula: FOLFormula) = {

		//}
		val r = VarInt("r")           //> r  : progsynth.types.Var = VarInt(r)
		val c1 = Const("1", PSInt)        //> c1  : progsynth.types.Const = Const(1,PSInt)
		val c0 = Const("0", PSInt)        //> c0  : progsynth.types.Const = Const(0,PSInt)
		val i = VarInt("i")           //> i  : progsynth.types.Var = VarInt(i)
		val n = VarInt("n")           //> n  : progsynth.types.Var = VarInt(n)
		val N = VarInt("N")           //> N  : progsynth.types.Var = VarInt(N)
		val arr = VarArrayInt("arr")  //> arr  : progsynth.types.Var = Var(arr,PSArrayInt)

    val ip = mkUnknownProgC {
        Invariant(None,
            ((r eqeq c1) iff Exists("i", c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n),
            None)
    }(1) {
        Invariant(None,
            ((r eqeq c1) iff Exists("i", c0 <= i && i < n + c1 && arr.select(i) > c0)) && n + c1 <= N,
            None)
    }                                             //> ip  : progsynth.types.UnknownProg = UnknownProg(Invariant(None,And(And(Iff(A
                                                  //| tom(Pred($eq$eq,List(VarInt(r), Const(1,PSInt)))),Exists(i,And(And(Atom(P
                                                  //| red($less$eq,List(Const(0,PSInt), 
                                                  //| Output exceeds cutoff limit.

		//Splitting i < n + 1  to i < n || i == c1
    val ip2 = ip.copyPost(
    		(((r eqeq c1) iff Exists("i", c0 <= i && ((i < n) || (i eqeq c1)) && arr.select(i) > c0)) && n + c1 <= N).inv
    		)                                 //> ip2  : tests.synthesis.ExistsPositiveElement2WS.ip.type = UnknownProg(Invar
                                                  //| iant(None,And(And(Iff(Atom(Pred($eq$eq,List(VarInt(r), Const(1,PSInt))))
                                                  //| ,Exists(i,And(And(Atom(Pred($less
                                                  //| Output exceeds cutoff limit.
		val ip3 =
		mkUnknownProgC (
    	pre =	(((r eqeq c1) iff Exists("i", c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n)).inv
    )(upid = 1)(
			post = (((r eqeq c1) iff Exists("i", c0 <= i && ((i < n) || (i eqeq c1)) && arr.select(i) > c0)) && n + c1 <= N).inv
    )                                             //> ip3  : progsynth.types.UnknownProg = UnknownProg(Invariant(None,And(And(Iff
                                                  //| (Atom(Pred($eq$eq,List(VarInt(r), Const(1,PSInt)))),Exists(i,And(And(Ato
                                                  //| m(Pred($less$eq,List(Const(0,PSIn
                                                  //| Output exceeds cutoff limit.
		ip3.post.formula.setFIdAll.toStringId
                                                  //> res0: String = 12@And(10@Iff(1@Atom(Pred($eq$eq,List(VarInt(r), Const(1,
                                                  //| PSInt)))), 9@Exists(i, 8@And(6@And(2@Atom(Pred($less$eq,List(Const(0,PSInt)
                                                  //| , VarInt(i)))), 5@Or(3@Atom(Pr
                                                  //| Output exceeds cutoff limit.
		//TODO: Add as a formula method.
		ip3.post.formula.setFIdAll.saveDotJpg
                                                  //> res1: String = d:\tmp\abc_1.dot.jpg
  	val ip4 = ip3.copyPost(ip3.post.formula.splitRange(5).inv)
                                                  //> ip4  : tests.synthesis.ExistsPositiveElement2WS.ip3.type = UnknownProg(Inva
                                                  //| riant(None,And(And(Iff(Atom(Pred($eq$eq,List(VarInt(r), Const(1,PSInt)))
                                                  //| ),Exists(i,And(And(Atom(Pred($les
                                                  //| Output exceeds cutoff limit.
		val ip5 =
		mkUnknownProgC (
    	pre =	(((r eqeq c1) iff Exists("i", c0 <= i && i < n && arr.select(i) > c0)) && n <= N && (N neq n)).inv
    )(upid = 1)(
			post = (((r eqeq c1) iff (Exists("i", c0 <= i && (i < n) && arr.select(i) > c0) || Exists("i", c0 <= i && (i eqeq c1) && arr.select(i) > c0))  ) && n + c1 <= N).inv
    )                                             //> ip5  : progsynth.types.UnknownProg = UnknownProg(Invariant(None,And(And(Iff
                                                  //| (Atom(Pred($eq$eq,List(VarInt(r), Const(1,PSInt)))),Exists(i,And(And(Ato
                                                  //| m(Pred($less$eq,List(Const(0,PSIn
                                                  //| Output exceeds cutoff limit.

    ip4.post                                      //> res2: progsynth.types.Invariant = Invariant(None,And(Iff(Atom(Pred($eq$eq,L
                                                  //| ist(VarInt(r), Const(1,PSInt)))),Exists(i,And(And(Atom(Pred($less$eq,Lis
                                                  //| t(Const(0,PSInt), VarInt(i))))
                                                  //| Output exceeds cutoff limit.
		ip5.post                          //> res3: progsynth.types.Invariant = Invariant(None,And(Iff(Atom(Pred($eq$eq,L
                                                  //| ist(VarInt(r), Const(1,PSInt)))),Or(Exists(i,And(And(Atom(Pred($less$eq,
                                                  //| List(Const(0,PSInt), VarInt(i)
                                                  //| Output exceeds cutoff limit.
}