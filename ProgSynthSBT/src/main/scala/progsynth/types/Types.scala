package progsynth.types

import progsynth.dsl.FOLFormulaDSL
//import progsynth.types.TermDSL

object Types extends ProgramAuxConstructors/*extends FOLFormulaTrait*/ /*with PSTypeVarUtil*/
	with NaryConstructorsExtractors {

	def getVar(name: String, tpe: String ): Option[Var] =
		PSType.getPSType(tpe) map { Var.mkVar(name, _)}

	def getConst(name: String, tpe: String ): Option[Const] =
		PSType.getPSType(tpe) map { Const.mkConst(name, _)}

//	def getFnApp(f: String, ts: List[Term], tpe: String): Option[FnApp] =
//		PSType.getPSType(tpe) map {FnApp(f, ts, _)}

	type FOLFormula = Formula[Pred]//

	val TrueF = True1[Pred]()
	val FalseF = False1[Pred]()

	implicit def FOLFormulaToFOLFormulaDSL(aFOLFormula: FOLFormula) = new FOLFormulaDSL(aFOLFormula)
	//implicit def TermToTermDSL(aTerm: Term) = new TermDSL(aTerm)
	//////

	implicit class IntToTermInt(v: Int){
		def c = ConstInt(v.toString)
	}
	
	implicit def intToVarInt(x: Int) = ConstInt(x.toString)
}
