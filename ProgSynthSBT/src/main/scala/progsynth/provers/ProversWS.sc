package progsynth.provers

import progsynth.types._
import progsynth.types.Types._
import progsynth.methodspecs.InterpretedFns

object ProversWS {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
	val x = VarInt("x")                       //> x  : progsynth.types.VarInt = VarInt(x)
	val y = VarInt("y")                       //> y  : progsynth.types.VarInt = VarInt(y)
	val f: TermBool = (x <= y) impl ( (x < y) || (x eqeq y))
                                                  //> f  : progsynth.types.TermBool = FnAppBool(Fn(impl,List(PSBool, PSBool),PSBoo
                                                  //| l),List(FnAppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(x), Var
                                                  //| Int(y))), FnAppBool(Fn($pipe$pipe,List(PSBool, PSBool),PSBool),List(FnAppBoo
                                                  //| l(Fn($less,List(PSInt, PSInt),PSBool),List(VarInt(x), VarInt(y))), FnAppBool
                                                  //| (Fn($eq$eq,List(PSAny, PSAny),PSBool),List(VarInt(x), VarInt(y)))))))
	//create z3Prover
	val z3prover = new PSZ3Prover()           //> z3prover  : progsynth.provers.PSZ3Prover = progsynth.provers.PSZ3Prover@1301
                                                  //| ed8
  // prove using z3Prover
	//val z3Result= z3prover.prove(f)
	// change timeout for all the provers
	PSProverConfigInit.proverConfig.setParam("timeout", "1")
  // reprove using z3Prover
  //val z3Result2 = z3prover.prove(f)
  // change timeout for z3prover
	z3prover.config.setParam("timeout", "10000")
	// reprove using z3Prover
	//val z3Result3 = z3prover.prove(f)
	// reset timeout of all the provers to original value
  PSProverConfigInit.proverConfig.setParam("timeout", "10000")
  //list the provers used
  PSProverMgr.proverSequence                      //> res0: List[String] = List(Z3Prover, Why3AltErgoProver)
  //prove using PSProverMgr
	//PSProverMgr.prove(f)

	val altErgoProver = new PSWhy3AltErgoProver()
                                                  //> altErgoProver  : progsynth.provers.PSWhy3AltErgoProver = progsynth.provers.P
                                                  //| SWhy3AltErgoProver@1d63e39
	val result = altErgoProver.prove(f).asInstanceOf[PSProofUnknown]
                                                  //> p.ProverExec - BeginSection(ProverExec.prove)
                                                  //| D:\ProgramFilesPortable\SSHWrapper\SSHWrapper.bat why3 -P alt-ergo D:\tmp2\
                                                  //| progsynth\po.txt#############################
                                                  //| p.ProverExec - EndSection(ProverExec.prove)
                                                  //| result  : progsynth.provers.PSProofUnknown = PSProofUnknown()
	result.info                               //> res1: String = ""|
}