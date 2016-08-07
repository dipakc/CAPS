package progsynth.provers

object Why3InputGenWC {
  
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
	import progsynth.provers._
	import progsynth.types._
	val inputGen = new Why3InputPrep{}        //> inputGen  : progsynth.provers.Why3InputPrep = progsynth.provers.Why3InputGen
                                                  //| WC$$anonfun$main$1$$anon$1@34e4c825
		val i = VarInt("i")               //> i  : progsynth.types.VarInt = VarInt(i)
		val j = VarInt("j")               //> j  : progsynth.types.VarInt = VarInt(j)
		val x = VarInt("x")               //> x  : progsynth.types.VarInt = VarInt(x)
		val y = VarInt("y")               //> y  : progsynth.types.VarInt = VarInt(y)

		val p = VarInt("p")               //> p  : progsynth.types.VarInt = VarInt(p)
		val q = VarInt("q")               //> q  : progsynth.types.VarInt = VarInt(q)
		val v = VarInt("v")               //> v  : progsynth.types.VarInt = VarInt(v)
		val k = VarInt("k")               //> k  : progsynth.types.VarInt = VarInt(k)
		val c0 = ConstInt("0")            //> c0  : progsynth.types.ConstInt = ConstInt(0)
		val c1 = ConstInt("1")            //> c1  : progsynth.types.ConstInt = ConstInt(1)
		val c5 = ConstInt("5")            //> c5  : progsynth.types.ConstInt = ConstInt(5)
		var N = VarInt("N")               //> N  : progsynth.types.VarInt = VarInt(N)
		var n = VarInt("n")               //> n  : progsynth.types.VarInt = VarInt(n)
		val arr = VarArrayInt("arr")      //> arr  : progsynth.types.VarArrayInt = VarArrayInt(arr)
		var f1: TermBool = x <= y         //> f1  : progsynth.types.TermBool = FnAppBool(Fn($less$eq,List(PSInt, PSInt),PS
                                                  //| Bool),List(VarInt(x), VarInt(y)))
		f1 = f1 && y <= N
		f1 = f1 && N >= c0
		f1 = f1 && (arr.select(N) <= v)
		f1 = f1 && (arr.select(N) >= v)
		f1 = f1 && (arr.select(y) >= v)
		f1 = f1 && c0 <= x
		f1 = f1 && x <= N
		f1 = f1 && ForallTermBool(i, c0 <= i && i < x, arr.select(i) < v)
		f1 = f1 && x >= y

		var psi = ForallTermBool( j,c0 <= j && j < i, arr.select(j) < v)
                                                  //> psi  : progsynth.types.QTermBool = QTermBool(Fn($amp$amp,List(PSBool, PSBool
                                                  //| ),PSBool),List(VarInt(j)),FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool)
                                                  //| ,List(FnAppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(ConstInt(0), Var
                                                  //| Int(j))), FnAppBool(Fn($less,List(PSInt, PSInt),PSBool),List(VarInt(j), VarI
                                                  //| nt(i))))),FnAppBool(Fn($less,List(PSInt, PSInt),PSBool),List(ArrSelectInt(Va
                                                  //| rArrayInt(arr),VarInt(j)), VarInt(v))))
		var psi2 = ForallTermBool(j, TermBool.TrueT, j < i)
                                                  //> psi2  : progsynth.types.QTermBool = QTermBool(Fn($amp$amp,List(PSBool, PSBoo
                                                  //| l),PSBool),List(VarInt(j)),ConstBool(true),FnAppBool(Fn($less,List(PSInt, PS
                                                  //| Int),PSBool),List(VarInt(j), VarInt(i))))
		var phi = c0 <= i &&  i <= N && psi
                                                  //> phi  : progsynth.types.TermBool = FnAppBool(Fn($amp$amp,List(PSBool, PSBool)
                                                  //| ,PSBool),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppB
                                                  //| ool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(ConstInt(0), VarInt(i))), Fn
                                                  //| AppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(i), VarInt(N)))))
                                                  //| , QTermBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(VarInt(j)),FnAppBo
                                                  //| ol(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(Fn($less$eq,List(
                                                  //| PSInt, PSInt),PSBool),List(ConstInt(0), VarInt(j))), FnAppBool(Fn($less,List
                                                  //| (PSInt, PSInt),PSBool),List(VarInt(j), VarInt(i))))),FnAppBool(Fn($less,List
                                                  //| (PSInt, PSInt),PSBool),List(ArrSelectInt(VarArrayInt(arr),VarInt(j)), VarInt
                                                  //| (v))))))
		var f2: TermBool = ForallTermBool( List(i, j), TermBool.TrueT, j < i)
                                                  //> f2  : progsynth.types.TermBool = QTermBool(Fn($amp$amp,List(PSBool, PSBool)
                                                  //| ,PSBool),List(VarInt(i), VarInt(j)),ConstBool(true),FnAppBool(Fn($less,List
                                                  //| (PSInt, PSInt),PSBool),List(VarInt(j), VarInt(i))))
		f2 = ForallTermBool(List(i, j), TermBool.TrueT, i < j)
		val f = f1 impl f2                //> f  : progsynth.types.TermBool = FnAppBool(Fn(impl,List(PSBool, PSBool),PSBo
                                                  //| ol),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(
                                                  //| Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(Fn($amp$amp,List(PS
                                                  //| Bool, PSBool),PSBool),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBoo
                                                  //| l),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(F
                                                  //| n($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(Fn($amp$amp,List(PSB
                                                  //| ool, PSBool),PSBool),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool
                                                  //| ),List(FnAppBool(Fn($amp$amp,List(PSBool, PSBool),PSBool),List(FnAppBool(Fn
                                                  //| ($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(x), VarInt(y))), FnAppBool
                                                  //| (Fn($less$eq,List(PSInt, PSInt),PSBool),List(VarInt(y), VarInt(N))))), FnAp
                                                  //| pBool(Fn($greater$eq,List(PSInt, PSInt),PSBool),List(VarInt(N), ConstInt(0)
                                                  //| )))), FnAppBool(Fn($less$eq,List(PSInt, PSInt),PSBool),List(ArrSelectInt(Va
                                                  //| rArrayInt(arr),VarInt(N
                                                  //| Output exceeds cutoff limit.


	val Some((Some(lo), Some(hi), rest)) = BoundsExtractorChained.unapply((ConstInt("0")  <= p <=  q  <=  n, p))
                                                  //> lo  : progsynth.types.TermInt = ConstInt(0)
                                                  //| hi  : progsynth.types.TermInt = FnAppInt(Fn($plus,List(PSInt, PSInt),PSInt)
                                                  //| ,List(VarInt(q), ConstInt(1)))
                                                  //| rest  : progsynth.types.TermBool = FnAppBool(Fn($less$eq,List(PSInt, PSInt)
                                                  //| ,PSBool),List(VarInt(q), VarInt(n)))
	
	lo.pprint                                 //> res0: String = 0
	hi.pprint                                 //> res1: String = q + 1

	//val input = inputGen.mkProverInput(f)
  //progsynth.utils.overwriteFile(raw"D:\VirtualBoxShare\test.mlw", input)

	//val outputParser = new Why3OutputParser{}
	//val output1 = raw"/home/dipakc/host/test.mlw Test G : Valid (0.16s)"
	//val status1 = outputParser.parseProverOutput(None, List(output1), Nil)
  //val output2 = raw"/home/dipakc/host/test.mlw Test G : Invalid (0.16s)"
  //val status2 = outputParser.parseProverOutput(None, List(output2), Nil)
	//the prover can’t determine if the task is valid
	//val output3 = raw"/home/dipakc/host/test.mlw Test G : Unknown (0.64s)"
	//val status3 = outputParser.parseProverOutput(None, List(output3), Nil)
  //the prover exceeds the time or memory limit.
	//val output4 = raw"/home/dipakc/host/test.mlw Test G : Timeout (24.01s)"
	//val status4 = outputParser.parseProverOutput(None, List(output4), Nil)
  // the prover reports a failure, i.e. it was unable to read correctly its input task.
  //val output5 = raw"/home/dipakc/host/test.mlw Test G : Failure (0.16s)"
  //val status5 = outputParser.parseProverOutput(None, List(output5), Nil)
  //an error occurred while trying to call the prover,
  //or the prover answer was not understood
  //val output6 = raw"/home/dipakc/host/test.mlw Test G : HighFailure (0.16s)"
  //val status6 = outputParser.parseProverOutput(None, List(output6), Nil)

	
	
	
}