package tests.types
import progsynth.types._
import progsynth.types.Types._
import progsynth._
import progsynth.ProgSynth._

object SampleObjects {
//ProgramAnn (pa<n>)
//Invariant (inv<n>)
//Formula (f<n>)
//Pred (pred<n>)
//Term (t<n>)
//GuardedCmd (grd<n>)
	//name: String, params: List[Var], retType: PSType, annProg: ProgramAnn
	val x = VarInt("x")
	val y = VarInt("y")
	val max = VarInt("max")
	val c0 = ConstInt("0")
	val maxProgram = mkComposition(
		pre = TermBool.TrueT.inv,
		programs = List(
			mkVarDefProg( pre = TermBool.TrueT.inv,
				lhs = max,
				rhs = mkExprProg(TermBool.TrueT.inv,c0, UnkTerm.mkUnkTermBool.inv),
				post = TermBool.TrueT.inv),
			mkIfProg(
				pre = TermBool.TrueT.inv,
				grdcmds = List(
					GuardedCmd(
						guard = x < y,
						cmd = mkAssignment(
							pre = (x < y).inv,
							lhs = max,
							rhs = mkExprProg(
								pre = (x < y).inv,
								expr = y,
								post = UnkTerm.mkUnkTermBool.inv),
							post = (x < y && (max eqeq y)).inv
						)),
					GuardedCmd(
						guard = !(x < y),
						cmd = mkAssignment(
							pre = (!(x < y)).inv,
							lhs = max,
							rhs = mkExprProg((!(x < y)).inv,x,UnkTerm.mkUnkTermBool.inv),
							post = (!(x < y) && (max eqeq x)).inv
							))
						),
				post = ((x < y && (max eqeq y)) || (!(x < y) && (max eqeq x))).inv
			),
			mkExprProg(
				pre = ((x < y && (max eqeq y)) || (!(x < y) && (max eqeq x))).inv,
				expr = max,
				post = ((x <= y impl (max eqeq y)) && (x >= y impl (max eqeq x))).inv(max)
			)
		),
		post = ((x <= y impl (max eqeq y)) && (x >= y impl (max eqeq x))).inv(max)
	)

	val f1 = (x <= y impl (max eqeq y)) && (x >= y impl (max eqeq x))
	val f2 = x < y && (max eqeq y)
	val f3 = TrueF
	val f4 = (x < y && (max eqeq y)) || (!(x < y) && (max eqeq x))
}

