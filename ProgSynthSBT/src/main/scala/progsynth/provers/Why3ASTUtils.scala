package progsynth.provers

import progsynth.types._
import progsynth.provers.Why3AST._
import Why3TypeUtils._

//object Why3ASTUtils extends Why3ASTUtils
trait Why3ASTUtils  { /*self: Why3PoToWTerm =>*/

	def mkWFunDecl(name: String)(args: List[Var])(retType: PSType)(body: WTerm): WFunDecl = {
		WFunDecl(name = name,
				lbls = Nil,
				tparams = varListToTParamList(args),
				tpe = getWType(retType),
				body = Some(body),
				withDecls = Nil)
	}

	def mkWFunDecl2(name: String)(args: List[Var])(retType: PSType): WFunDecl = {
		WFunDecl(name = name,
				lbls = Nil,
				tparams = varListToTParamList(args),
				tpe = getWType(retType),
				body = None,
				withDecls = Nil)
	}

	def getWTypeOfTerm(term: Term): WType = getWType(term.getType())

	def mkWFnLambda(params: List[Var], term: WTerm, bodyTpe: WType) = {
	    WFnLambda(
	            params = params.map(p => (p.v, getWTypeOfTerm(p))),
	            body = term,
	            bodyType = bodyTpe)
	}

	def mkWLambdaConstant(constName: String, lambda: WFnLambda): WConstDecl = {
	    WConstDecl(constName, Nil, lambda.lambdaType, Some(lambda))
	}

	/**
	 *  val i = VarInt("i")
	 *  val j = VarInt("j")
	 *  mkWLambdaConstant2("sum", List(i, j),  i+ j)
	 *  -----
	 *  constant sum: int -> int -> int = (\i j: int. i + j)
	 *  */
	def mkWLambdaConstant2(constName: String, params: List[Var], term: WTerm, bodyTpe: WType): WConstDecl = {
	    val lambda = mkWFnLambda(params, term, bodyTpe)
	    WConstDecl(constName, Nil, lambda.lambdaType, Some(lambda))
	}

	/**
	 * //val List((a, a1), (b, b1)) = withList
	 * returns
	 * clone $name as $newName with $a = $a1, $b = $b1 */
	def mkCloneDecl(name: String, newName: String, withList: List[(String, String)]) = {
	    val withStr = withList.map(p => p._1 + " = " +  p._2 ).mkString(", ")
	    WCloneDecl(s"clone $name as $newName with $withStr")
	}

	def mkWFnApp(fn: WFunDecl)(args: List[WTerm]) = {
		WFnApp(fn.name, args)
	}

	def mkWFnApp2(fn: WFunDecl)(args: List[Var]) = {
		WFnApp(fn.name, args.map(varToWTerm))
	}

	def mkBinWFnApp(opr: String)(t1: WTerm, t2: WTerm) = {
		WFnApp(opr, t1 :: t2 :: Nil)
	}

	def mkITE(c: WTerm)(t1: WTerm)(t2: WTerm) = {
		WITE(c, t1, t2)
	}

	def mkWForall(dummyVars: List[Var], range: WTerm, term: WTerm): WForall = {
		WForall(varListToBinderList(dummyVars), Nil, WInfixOp("->", range, term))
	}

	def mkWForall2(dummyVars: List[Var], term: WTerm): WForall = {
		WForall(varListToBinderList(dummyVars), Nil, term)
	}

	def mkWForall3(dummyVars: List[Var])(term: WTerm): WForall = {
		WForall(varListToBinderList(dummyVars), Nil, term)
	}

	def mkWForall4(dummyVars: List[Var])(range: WTerm)(term: WTerm): WForall = {
		WForall(varListToBinderList(dummyVars), Nil, WInfixOp("->", range, term))
	}

	def mkWExist(dummyVars: List[Var], range: WTerm, term: WTerm): WExist = {
		WExist(varListToBinderList(dummyVars), Nil, WInfixOp("/\\", range, term))
	}

	def getWType(tpe: PSType): WTypeSymbol = {
		WTypeSymbol(getWhy3Tpe(tpe), Nil)
	}

	def getWType(term: Term): WTypeSymbol = {
		WTypeSymbol(getWhy3Tpe(term.getType), Nil)
	}

	def varToWTerm(aVar: Var): WTerm  = {
		WSymbol(aVar.v)
	}
	// VarInt(x) ---> "(x: int)"
	def varToTParamStr(avar: Var): String = {
		val varName: String = avar.v
		val varTpe: String = getWhy3Tpe(avar)
		s"($varName: $varTpe)"
	}

	// List(VarInt(x), VarInt(x)) ---> List("(x: int)", "(y: int)")
	def varListToTParamList(varList: List[Var]): List[String] = {
		varList.map(varToTParamStr)
	}

	def varToBinder(avar: Var): WBinder = {
		WBinder(avar.v :: Nil, WTypeSymbol(getWhy3Tpe(avar), Nil))
	}

	def varListToBinderList(varList: List[Var]): List[WBinder] = {
		varList.map{varToBinder}
	}

	implicit class WTermMethods(t: WTerm) {
		def <(that: WTerm): WTerm = WInfixOp("<", t, that)
		def >(that: WTerm): WTerm= WInfixOp(">", t, that)
		def <=(that: WTerm): WTerm = WInfixOp("<=", t, that)
		def >=(that: WTerm): WTerm = WInfixOp(">=", t, that)
		def eqeq(that: WTerm): WTerm = WInfixOp("=", t, that)

		def + (that: WTerm): WTerm = WInfixOp("+", t, that)
		def - (that: WTerm): WTerm = WInfixOp("-", t, that)
		def * (that: WTerm): WTerm = WInfixOp("*", t, that)
		def % (that: WTerm): WTerm = WInfixOp("%", t, that)
		def / (that: WTerm): WTerm = WInfixOp("/", t, that)
		def unary_-(): WTerm = WPrefixOp("-", t)
		//def min (that: WTerm): WTerm = WInfixOp("min", t, that)
		//def max (that: WTerm): WTerm = WInfixOp("==", t, that)
		//def pow (that: WTerm): WTerm = WInfixOp("==", t, that)

	}
}