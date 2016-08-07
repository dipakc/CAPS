package progsynth.provers
import progsynth.types._
import progsynth.types.Types._

object Why3GenQuantEncoderWS {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val i = VarInt("i")                             //> i  : progsynth.types.VarInt = VarInt(i)
  val n = VarInt("n")                             //> n  : progsynth.types.VarInt = VarInt(n)
  val p = VarInt("p")                             //> p  : progsynth.types.VarInt = VarInt(p)
  val q = VarInt("q")                             //> q  : progsynth.types.VarInt = VarInt(q)
  val N = VarInt("N")                             //> N  : progsynth.types.VarInt = VarInt(N)
  val a = VarArrayInt("a")                        //> a  : progsynth.types.VarArrayInt = VarArrayInt(a)
  0 <= p && p <= q && q <= N                      //> res0: progsynth.types.TermBool = FnAppBool(Fn($amp$amp,List(PSBool, PSBool),
                                                  //| PSBool),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBo
                                                  //| ol(Fn($less$eq,List(PSInt, PSInt),PSBool),List(ConstInt(0), VarInt(p))), FnA
                                                  //| ppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(p), VarInt(q))))),
                                                  //|  FnAppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(q), VarInt(N))
                                                  //| )))
  (0 <= p <= q) <= N                              //> java.lang.RuntimeException: Chaining operator failed
                                                  //| 	at progsynth.types.TermDSLBool$class.$less$eq(Term.scala:400)
                                                  //| 	at progsynth.types.FnAppBool.$less$eq(Term.scala:152)
                                                  //| 	at progsynth.provers.Why3GenQuantEncoderWS$$anonfun$main$1.apply$mcV$sp(
                                                  //| progsynth.provers.Why3GenQuantEncoderWS.scala:14)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at progsynth.provers.Why3GenQuantEncoderWS$.main(progsynth.provers.Why3G
                                                  //| enQuantEncoderWS.scala:5)
                                                  //| 	at progsynth.provers.Why3GenQuantEncoderWS.main(progsynth.provers.Why3Ge
                                                  //| nQuantEncoderWS.scala)
  /*
  val wio = new Why3InputOutputPrep()
  wio.genQTermToWTerm(MinTermInt(i, 0 <= i < n, a(i)))
	wio.theory
	wio.theory.str
	val wio2 = new Why3InputOutputPrep()
	wio2.poToWhy3Str(MinTermInt(i, 0 <= i < n, a(i)))

	val wio3 = new Why3InputOutputPrep()
	wio3.mkProverInput(MinTermInt(i, 0 <= i < n, a(i)) eqeq 0)
*/

	                                                  
}