package progsynth.types
import org.scalacheck.Gen
import org.scalacheck.Shrink
import org.scalacheck.Arbitrary
import progsynth.debug.PSDbg._
import java.lang.RuntimeException
import progsynth.methodspecs.InterpretedFns._

//import progsynth.types.TermGen

/**
 *  Dependency
digraph dependency {
	node [shape=box];
	psTypeGen;
	psTypeGenTpe;
	termGenHtTpe -> leafTermGenTpe;
	termGenHtTpe -> internalTermGenHtTpe;
	termGenHt -> psTypeGen;
	termGenHt -> termGenHtTpe;
	leafTermGenTpe -> varGenTpe;
	leafTermGenTpe -> constGenTpe;
	internalTermGenHtTpe -> arrStoreGenHtTpe;
	internalTermGenHtTpe -> arrSelectGenHtTpe;
	internalTermGenHtTpe -> fnAppGenSzTpe;
	constGenTpe -> intConstGen;
	constGenTpe -> boolConstGen;
	constGenTpe -> arrayIntConstGen;
	constGenTpe -> arrayBoolConstGen;
	varGenTpe ;
	arrStoreGenHtTpe -> termGenHtTpe;
	arrStoreGenHtTpe -> indexGenHt;
	arrSelectGenHtTpe -> psTypeGenTpe;
	arrSelectGenHtTpe -> termGenHtTpe;
	arrSelectGenHtTpe -> indexGenHt;
	fnAppGenSzTpe -> termGenHt;
	fnAppGenSzTpe -> fnGen;
	fnGen;
	intConstGen -> psTypeGenTpe;
	boolConstGen- > psTypeGenTpe;
	arrayIntConstGen;
	arrayBoolConstGen;
	indexGenHt -> termGenHtTpe;
	termBoolGenHt -> termGenHtTpe;
	varGen -> psTypeGen;
	varGen -> varGenTpe;

}
*/

object TermGen extends TermGenerators
class TermGenerators extends SubTermAndPathGen with ShrinkUtils {
	/** PSType generator: PSInt, PSArrayInt, PSBool, PSArrayBool
	 *  Types not supported: PSReal, PSArrayReal, PSUnit */
	def psTypeGen() = {
		Gen.oneOf(List(PSInt, PSArrayInt, PSBool, PSArrayBool))
	}

	def  psTypeGenTpe(tpe: PSType): Gen[PSType] = Gen.value(tpe)

	/** Term generator for specific height and type := varGen | fnAppGen | arrSelectGen | arrStoreGen | constGen
	*/
	def termGenHtTpe(ht: Int, tpe: PSType ):Gen[Term] = ht match {
		case _ if ht > 0 =>
			if (tpe == PSUnit)
				leafTermGenTpe(tpe)
			else
				Gen.frequency((3, internalTermGenHtTpe(ht, tpe)),
							  (1, leafTermGenTpe(tpe)))
		case _ if ht <= 0 =>
			leafTermGenTpe(tpe)
	}

	/** Term generator of specified height */
	def termGenHt(ht: Int): Gen[Term] = {
		for {
			tpe <- psTypeGen()
			term <- termGenHtTpe(ht, tpe)
		} yield term
	}

	/** TermBool generator of specified height */
	def termBoolGenHt(ht: Int): Gen[TermBool] = {
		for {
			term <- termGenHtTpe(ht, PSBool)
		} yield term.asInstanceOf[TermBool]
	}

	def leafTermGenTpe(tpe: PSType): Gen[Term] = {
		Gen.oneOf(varGenTpe(tpe), constGenTpe(tpe))
	}

	def internalTermGenHtTpe(ht: Int, tpe: PSType): Gen[Term] = {
		if (PSType.isArrayType(tpe))
			arrStoreGenHtTpe(ht, tpe)
		else if (PSType.isBasicType(tpe))
			Gen.oneOf(fnAppGenSzTpe(ht, tpe), arrSelectGenHtTpe(ht, tpe))
		else Gen.fail
	}

	/** Const generator: Const(v: String, t: PSType)*/
	def constGenTpe(tpe: PSType): Gen[Const] = tpe match {
		case PSInt => intConstGen()
		case PSBool => boolConstGen()
		case PSArrayInt => arrayIntConstGen()
		case PSArrayBool => arrayBoolConstGen()
		case _ => Gen.fail
	}

	/** Var generator: Var(v: String, t: PSType)*/
	lazy val varGen: Gen[Var] =
		for {
			tpe <- psTypeGen
			aVar <- varGenTpe(tpe)
		} yield aVar

	def varGenTpe(tpe: PSType) = {
		val aList = tpe match {
			case PSInt => List("x", "y", "z", "w")
			case PSBool => List("p", "q", "r", "s")
			case PSArrayInt => List("arr", "arr2", "arr3")
			case PSArrayBool => List("barr", "barr2", "barr3")
			case _ => List("v1", "v2", "v3", "v4")
		}
		for {
			v <- Gen.oneOf(aList)
		} yield Var.mkVar(v, tpe)
	}

	/** ArrStore generator for specific type
	 * arrStoreGen(ht, PSArrayBool) returns
	 * ArrStoreBool(arr: TermArrayBool, index: Int, value: TermBool): ArrStoreBool
	*/
	def arrStoreGenHtTpe(ht: Int, tpe: PSType): Gen[Term] = {
		assert(PSType.isArrayType(tpe) )
		for {
			arr <- termGenHtTpe(ht - 1, tpe)
			index <- indexGenHt(ht - 1)
			value <- termGenHtTpe(ht - 1, PSType.getBasicTpe(tpe))
			aTpe <- Gen.value(tpe)
		} yield ArrStore.mkArrStore(arr, index, value, aTpe)
	}

	/** ArrSelect generator for specific type
	 * arrSelectGen(ht, PSBool) returns
	 * ArrSelectBool(arr: TermArrayBool, index: Int): ArrSelectBool
	*/
	def arrSelectGenHtTpe(ht: Int, basicTpe: PSType): Gen[Term] = {
		assert(PSType.isBasicType(basicTpe) )
		for {
			arrTpe <- psTypeGenTpe(PSType.getArrTpe(basicTpe))
			arr <- termGenHtTpe(ht - 1, arrTpe)
			index <- indexGenHt(ht - 1)
			aTpe <- psTypeGenTpe(basicTpe)
		} yield ArrSelect.mkArrSelect(arr, index, aTpe)

	}

	//	/** FnApp generator: FnApp(f: Fn, ts: List[Term]) */
	//	class FnAppGenSzTpe(config: Config) {
	//		lazy val termGenHt = config.termGenHt
	//		lazy val fnGenSig = config.fnGenSig
	//		def mkTerm(sz: Int, rtpe: PSType): Gen[Term] = {
	//			for {
	//				arity <- Gen.choose(1, 4)
	//				ts <- Gen.listOfN(arity, termGenHt.mkTerm(sz - 1))
	//				argTpes = ts map (_.getType)
	//				fn <- fnGenSig.mkTerm(argTpes, rtpe)
	//			} yield FnApp.mkFnApp(fn, ts, fn.tpe)
	//		}
	//	}

	//	/** Fn generator given signature: Fn(name: String, argTpes: List[PSType], tpe: PSType)*/
	//	class FnGenSig(config: Config) {
	//		def mkTerm(argTpes: List[PSType], rtpe: PSType) = {
	//			for {
	//				name <- Gen.oneOf("fn1", "fn2")
	//			} yield Fn(name, argTpes, rtpe)
	//		}
	//	}

	/** FnApp generator: FnApp(f: Fn, ts: List[Term]) */
	def fnAppGenSzTpe(sz: Int, rtpe: PSType): Gen[Term] = {
		for {
			fn <- fnGenTpe(rtpe)
			ts <- Gen.listOfN(fn.numArgs, termGenHt(sz - 1))
		} yield FnApp.mkFnApp(fn, ts, fn.tpe)
	}


	/** Fn generator : Fn(name: String, argTpes: List[PSType], tpe: PSType)*/

	def fnGenTpe(rtpe: PSType) = {
		for {
			arity <- Gen.choose(1, 4)
			argTpes <- Gen.listOfN(arity, psTypeGen())
			name <- Gen.oneOf("fn1", "fn2")
		} yield Fn(name, argTpes, rtpe)
	}

	/** Int Const generator: Const(v: String, t: PSType)*/
	def intConstGen()= for {
		anInt <- Gen.choose(-100, 100)
		intStr = anInt.toString
		t <- psTypeGenTpe(PSInt)
	} yield Const.mkConst(intStr, t)

	/** Bool Const generator: Const(v: String, t: PSBool)*/
	def boolConstGen() = for {
		boolStr <- Gen.oneOf("True", "False")
		tpe <- psTypeGenTpe(PSBool)
	} yield Const.mkConst(boolStr, tpe)

	/** Array Int Const generator: ConstArrayInt(v: String)*/
	def arrayIntConstGen() = for {
		arity <- Gen.choose(1, 4)
		intLst <- Gen.listOfN(arity, Gen.choose(-10, 10))
		intStrLst = (intLst map (_.toString)).mkString(", ")
		arrayStr <- Gen.value("Array(" + intStrLst + ")")
	} yield ConstArrayInt(arrayStr)

	/** Array Bool Const generator: Const(v: String, t: PSArrayInt)*/
	def arrayBoolConstGen() = for {
		arity <- Gen.choose(1, 4)
		boolLst <- Gen.listOfN(arity, Gen.oneOf(true, false))
		boolStrLst = (boolLst map (_.toString)).mkString(", ")
		arrayStr <- Gen.value("Array(" + boolStrLst + ")")
	} yield ConstArrayBool(arrayStr)

	def indexGenHt(ht: Int) = {
		termGenHtTpe(ht - 1, PSInt)
	}
}

trait SubTermAndPathGen { self: TermGenerators =>
	/**
	 * Generator for a Term and a path from the root node to random descendant node
	 * Root is at the head of the sample list.
	 */
	def termAndPathGen(ht: Int): Gen[(Term, List[Term])] = {
		for {
			term <- termGenHt(ht)
			path <- pathGen(term)
		} yield (term, path)
	}

	/** Term and subterm gen */
	def termAndSubTermGen(ht: Int): Gen[(Term, Term)] = {
		for {
			term <- termGenHt(ht)
			subTerm <- subTermGen(term)
		} yield (term, subTerm)
	}

	/** Sub term generator */
	private def subTermGen(aTerm: Term): Gen[Term] = {
		aTerm match {
			case Var(v) => Gen.value((aTerm))
			case FnApp(f, ts) =>
				val tgs = Gen.value(aTerm) :: (ts map (subTermGen(_)))
				val tgsf = tgs.map((1, _))
				Gen.frequency(tgsf: _*)
			case Const(name) => Gen.value(aTerm)
			case ArrSelect(arr, index) =>
				Gen.oneOf(Gen.value(aTerm), subTermGen(arr), subTermGen(index))
			case ArrStore(arr, index, value) =>
				Gen.oneOf(Gen.value(aTerm), subTermGen(arr), subTermGen(index), subTermGen(value))
		}
	}

	private def oneOfList[T](gens: List[Gen[T]]): Gen[T] = gens match {
		case Nil => throw new RuntimeException("oneOfList argument empty list")
		case g :: Nil => g
		case g1 :: g2 :: gs => Gen.oneOf(g1, g2, gs: _*)
	}

	/**
	 * Generator for path from the root node to random descendant node
	 * Root is at the head of the sample list.
	 */
	private def pathGen(aTerm: Term): Gen[List[Term]] = {
		val selfGen = Gen.value(List(aTerm))

		val genList =
			for (c <- aTerm.childs) yield {
				for (cpath <- pathGen(c))
					yield aTerm :: cpath
			}
		oneOfList(selfGen :: genList)
	}
}

//////////////////////////////////////
//#########################################
//#########################################
//case class LTuple(one:[T1], two[T2])
//TODO: This is not getting called.

///////////////////////////////////////////////////////////
//Shrink Utils
///////////////////////////////////////////////////////////
trait ShrinkUtils { self: TermGenerators =>
	implicit def shrinkTermImpl: Shrink[Term] = {
		writeln0("shrink called")
		def shrinkTerm(term: Term): Stream[Term] = {
			writeln0("shrink called")
			term match {
				case Var(_) => Stream()
				case Const(_) => Stream()
				case FnApp(f, ts) =>
					Stream(ts: _*) ++ { ts flatMap { shrinkTerm(_) } }
				case ArrSelect(arr, index) =>
					Stream(arr, index) ++ shrinkTerm(arr) ++ shrinkTerm(index)
				case ArrStore(arr, index, value) =>
					Stream(arr, index, value) ++ shrinkTerm(arr) ++ shrinkTerm(index) ++ shrinkTerm(value)
			}
		}
		Shrink { term => shrinkTerm(term) }
	}

	implicit lazy val arbTerm: Arbitrary[Term] = Arbitrary(termGenHt(4))

	def shrinkTuple3[T1, T2](implicit s1: Shrink[T1], s2: Shrink[T2]): Shrink[(T1, T2)] = {
		writeln0("shrink tuple3 called")
		Shrink {
			case (t1, t2) =>
				(for (x1 <- s1.shrink(t1)) yield (x1, t2))
		}
	}

	def shrinkTermPath(implicit st: Shrink[Term]): Shrink[(Term, List[Term])] = {
		writeln0("shrink Term Path")
		def shrink(arg: (Term, List[Term])): Stream[(Term, List[Term])] = arg match {
			case (_, Nil) => Stream()
			case (_, hd :: Nil) => Stream()
			case (_, hd1 :: hd2 :: tail) =>
				(hd1.childs filter { c: Term => c != hd2 && !c.childs.isEmpty }).toStream map { c =>
					val hd1New = hd1.mapSubTerms { case `c` => ConstUnit("0") }
					(hd1New, hd1New :: hd2 :: tail)
				} append
					Stream((hd2, hd2 :: tail)) append
					shrinkTermPath.shrink((hd2, hd2 :: tail))
		}
		Shrink { shrink _ }
	}
}

object TermGenInterpretedFnObj extends TermWithInterpretedFnGen
class TermWithInterpretedFnGen extends TermGenerators {
	/** Generate interpreted functions only */
	override def fnGenTpe(rtpe: PSType) = rtpe match {
		case PSInt => Gen.oneOf(PlusIntFn, MinusIntFn, TimesIntFn, UnaryMinusIntFn, PercentIntFn, DivIntFn)
		case PSBool => Gen.oneOf(AndBoolFn, OrBoolFn, NegBoolFn, ImplBoolFn, RImplBoolFn,
				EquivBoolFn, GTBoolFn, LTBoolFn, GEBoolFn, LEBoolFn, EqEqBoolFn)
		case _ => Gen.fail
	}
}

object TermGenNewApp2 {
	def main(args: Array[String]) {
		//Term Generator
		val termGen = TermGen.termGenHt(5)
		val aTerm = termGen.sample
		println(aTerm)

		//Terms with interpreted functions
		val termGen2 = TermGenInterpretedFnObj.termGenHt(5)
		val aTerm2 = termGen2.sample
		println(aTerm2)

	}
}