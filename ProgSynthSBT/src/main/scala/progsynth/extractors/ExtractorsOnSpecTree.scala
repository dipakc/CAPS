package progsynth.extractors
//import scala.reflect.generic.Universe
/*
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.plugins.PluginComponent
import progsynth.PSPredef._
import progsynth.types._
import progsynth.types.Types._

//import scala.reflect.generic.Names
import scala.xml.Elem
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.logger.FunLogger._
import progsynth.printers.RichTree._
import progsynth.logger.CaseStmtLogger._
import progsynth.types.PSType._

trait ExtractorsOnSpecTree extends ExtractorsOnAST with AST2Formula{ self:PluginComponent =>
	import global._

	//# tree = None means SkipProg
	class SpecTree(val pre: Option[Invariant2], val tree: Option[global.Tree], val post: Option[Invariant2]) {
		override def toString():String = {
			val retVal: String =
				(if (pre.isDefined) "{{" + pre.get.toString() + "}}\n" else "{{-}}\n" ) +
				(if (tree.isDefined) tree.get.pprint() else "No Tree\n") +
				(if (post.isDefined) "{{" + post.get.toString() + "}}\n" else "{{-}}\n" )
			retVal
		}
	}

	object SpecTree{
		def apply(pre: Option[Invariant2], tree: Option[global.Tree], post: Option[Invariant2]): SpecTree =
			new SpecTree(pre, tree, post)

		def unapply(st: SpecTree): Option[(Option[Invariant2], Option[global.Tree], Option[Invariant2])] =
		funlog("SpecTree::unapply", st){ st =>
				Some(st.pre, st.tree, st.post)
		}
	}

	object IdentSpecTree {
		//TODO: implement
	}

	object UnknownFragmentSpecTree {
		def unapply(st: SpecTree) : Option[Int] =
		funlog("UnknownFragmentSpecTree::unapply", st){ st =>
			val retVal = st.tree match {
				case Some(
					Apply( // sym=method UnknownFragment, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
					  TypeApply( // sym=method UnknownFragment, tpe=(id: Int)Int, tpe.sym=<none>
					    Select( // sym=method UnknownFragment, sym.owner=object UnknownFragment, sym.tpe=[T](id: Int)T, tpe=[T](id: Int)T, tpe.sym=<none>
					      Select( // sym=object UnknownFragment, sym.owner=package spec, sym.tpe=object progsynth.spec.UnknownFragment, tpe=progsynth.spec.UnknownFragment.type, tpe.sym=object UnknownFragment, tpe.sym.owner=package spec
					        Select( // sym=package spec, sym.owner=package progsynth, sym.tpe=package progsynth.spec, tpe=progsynth.spec.type, tpe.sym=package spec, tpe.sym.owner=package progsynth
					          Ident(progsynthName), // sym=package progsynth, sym.owner=package <root>, sym.tpe=package progsynth, tpe=progsynth.type, tpe.sym=package progsynth, tpe.sym.owner=package <root>,
					          specName),
					        unknownFragmentName1),
					      unknownFragmentName2),
					    List(
					    TypeTree() // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala
					    )
					  ),
					  List( // 1 arguments(s)
					    Literal(Constant(id))
					  )
					)
				) if (progsynthName.toString() == "progsynth") && (specName.toString == "spec") &&
					unknownFragmentName1.toString == "UnknownFragment" && unknownFragmentName2.toString == "UnknownFragment" =>
						Some(id.asInstanceOf[Int])
				case _ =>
					None
			}
			retVal
		}
	}
	object IfSpecTree {
		def unapply(st: SpecTree) : Option[(FOLFormula, SpecTree, SpecTree)] =
		funlog("IfSpecTree::unapply", st){ st =>
			st.tree match {
				case Some(If(FormulaOfTree(cond), thenp, elsep)) =>
					val thenInvOpt = st.pre map {_ modifyFormula (f => And(f, cond).simplify)}
					val elseInvOpt = st.pre map {_ modifyFormula (f => And(f, Not(cond)).simplify)}
					Some(cond,
						SpecTree(thenInvOpt, Some(thenp), st.post),
						SpecTree(elseInvOpt, Some(elsep), st.post))
				case _ => None
			}
		}
	}

	object WhileRetExprTree {
		def unapply(st: SpecTree) : Option[Int] = { st.tree match {
				case Some(Apply( Ident(whileLblName), Nil)) if whileLblName.toString.startsWith("while$")=> Some(1)
				case _ => None
			}
		}
	}

	object WhileSpecTree {
		/**
		 * Scala Code:
		 * 		{ A; B; SLoopInvCall; WhileLoop; C; D }
		 * Scala AST:
		 * 		Block( ATree :: BTree :: SLoopInvTree :: WhileLoopTree :: CTree :: DTree:: Nil)
		 * TODO: List of ASTs as extracted by CompositionSpecTree:
		 * 		ATree::BTree::Block(SLoopInvTree::WhileLoopTree::Nil, EmptyTree)::CTree::DTree::Nil
		 * Input Tree to WhileSpecTree : Block(SLoopInvTree::WhileLoopTree::Nil, EmptyTree)
		 */
		def unapply(st: SpecTree) : Option[(Option[FOLFormula], FOLFormula, SpecTree)] =
		funlog("WhileSpecTree::unapply", st){ st =>
			st.tree match {
				case Some(WhileBlockDef(loopInvTree, condTree , bodyTree)) =>
					(loopInvTree, condTree) match {
						case (InvOfTree(loopInv), FormulaOfTree(cond)) =>
							val bodyPreInv = Some(loopInv.copy(formula = And(loopInv.formula, cond ).simplify))
							Some(Some(loopInv.formula), cond, SpecTree(bodyPreInv, Some(bodyTree), Some(loopInv)))
						case _ => None
					}
				case _ => None
			}
		}
	}

	/**
	 * Input: SpecTree = PreInv, Tree, PostInv
	 * where Tree is a Block with specifications : SRequire or/and SEnsuring
	 */
	object CompositionSpecTreeWithSpec {
		def unapply(st: SpecTree) : Option[List[SpecTree]] =
		funlog("CompositionSpecTreeWithSpec::unapply", st){ st =>
			val retValBuf = new ListBuffer[SpecTree]
			val SpecTree(pre, tree, post) = st

            tree match {
				case _ if casestart("SRequireSEnsuring") => None
				case Some(SRequireSEnsuring(InvOfTree(preInv), block, InvOfTree(postInv))) =>
					val blockST = SpecTree(Some(preInv), Some(block), Some(postInv))
					block match {
						case CompositionSpecTree(specTrees) =>
							//retValBuf += SpecTree(pre, None, Some(preInv))
							retValBuf ++= specTrees
							//sretValBuf += SpecTree(Some(postInv), None, post)
						case _ =>
					}
				case _ if casemiddle("SRequire") => None
				case Some(SRequire(InvOfTree(preInv), block)) =>
					val blockST = SpecTree(Some(preInv), Some(block), post)
					block match {
						case CompositionSpecTree(specTrees) =>
							//retValBuf += SpecTree(pre, None, Some(preInv))
							retValBuf ++= specTrees
						case _ =>
					}
				case _ if casemiddle("SEnsuring") => None
				case Some(SEnsuring(Any2Ensuring(block), InvOfTree(postInv))) =>
					val blockST = SpecTree(pre, Some(block), Some(postInv))
					blockST match {
						case CompositionSpecTree(specTrees) =>
							retValBuf ++= specTrees
							//retValBuf += SpecTree(Some(postInv), None, post)
						case _ =>
					}
				case _ =>
			}
			caseend()
			if (retValBuf.length > 0) Some(retValBuf.toList) else None
		}
	}

	object CompositionSpecTree {
		def unapply(st: SpecTree) : Option[List[SpecTree]] =
		funlog("CompositionSpecTree::unapply", st){ st =>
			val retVal = new ListBuffer[SpecTree]
			val SpecTree(pre, tree, post) = st
			//assert(pre != None && post != None)
			var ipre: Option[Invariant2] = pre
			var iprogtree: Option[Tree] = None
			var ipost: Option[Invariant2] = None
			st.tree match {
				case Some(Block(stats, expr)) => {
					var newStats = createWhileLoopBlocks(stats)
					//START
					//If the first statement in composition in an assert,
					//treat it as a pre-condition of whole block instead of the original pre
					if(newStats.length > 0) {
						newStats.head match {
							case SAssert(InvOfTree(inv)) =>
								ipre = Some(inv)
								newStats = newStats.tail
							case _ =>
						}
					}
					//END
					for (s <- newStats ::: List(expr)){
						(ipre, iprogtree, ipost, s) match {
							//case (Some(_), None, None, SAssert(InvOfTree(inv))) =>
							//	//disallow consecutive sasserts.
							//	throw new RuntimeException("sAssert can not follow sAssert")
							case (Some(_), _, None, SAssert(InvOfTree(inv))) =>
								//In case of consecutive sasserts, skipprog will be inserted.
								retVal += SpecTree(ipre, iprogtree, Some(inv))
								ipre = Some(inv)
								iprogtree = None
							case (Some(_), None, None, _) =>
								iprogtree = Some(s)
							case (Some(_), Some(_), None, _) =>
								val unknownInv = Invariant2(None, Unknown(), None)
								retVal += SpecTree(ipre, iprogtree, Some(unknownInv))
								ipre = Some(unknownInv)
								iprogtree = Some(s)
							case (None, None, None, _) =>
								throw new RuntimeException("exception")
						}
					}
					if(iprogtree.isDefined){ //avoid spurius skipprogs. //TODO: handle in while
						val lastSpecTree = SpecTree(ipre, iprogtree, post)
						lastSpecTree match {
							case WhileRetExprTree(_) =>
							case _ => retVal += lastSpecTree
						}
					}
					val retList = Some(retVal.toList)
					retList
				}
				case _ => None
			}
		}

		def createWhileLoopBlocks(stList: List[Tree]): List[Tree] = stList match {
			case SLoopInv(_) ::  WhileDef(_, _) :: tail =>
				Block(List(stList.head), stList.tail.head) :: createWhileLoopBlocks(tail)
			case head :: tail => head :: createWhileLoopBlocks(tail)
			case Nil => Nil
		}
	}

	/** FunctionSpecTree(fname: String, params: List[Var], retType: PSType, body: SpecTree)*/
	object FunctionSpecTree {
		def unapply(st: SpecTree) : Option[(String, List[Var], PSType, SpecTree)] =
		funlog("FunctionSpecTree::unapply", st){ st =>
			//* outer most function definition. st.pre and st.post are None
			st.tree match {
				case Some(FunctionDef(fname, params, retType, SRequireSEnsuring(InvOfTree(preInv), block, InvOfTree(postInv))))
				if st.pre == None && st.post == None =>
					val bodySt = SpecTree(Some(preInv), Some(block), Some(postInv))
					Some(fname, params, retType, bodySt)
				case _ => None
			}
		}
	}

	object AssignmentSpecTree {
		def unapply(st: SpecTree) : Option[(Var, SpecTree)] =
		funlog("AssignmentSpecTree::unapply", st){ st =>
			//if (st.tree.isDefined)
			//	st.tree.get.printNodes(global)
			val retVal = st.tree match {
				case Some(Assign(ident@Ident(lhs), rhs)) =>
					getPSType(ident.tpe.toString) map { identTpe =>
						//val newPost = st.post map {_ updateRvVar Var(lhs.toString, identTpe)} //Do not set post of rhs during extraction.
						var unknownInv = Invariant2(None, Unknown(), None)
						val rhsST = SpecTree(st.pre, Some(rhs), Some(unknownInv))
						(Var.mkVar(lhs.toString, identTpe), rhsST)
					}
				/** arr(indexTerm) := valTerm is translated to
				 * arr := Store(arr, indexTerm, valTerm)*/
				case 	Some(app @ Apply( // sym=method update, tpe=Unit, tpe.sym=class Unit, tpe.sym.owner=package scala
						       Select( // sym=method update, sym.owner=class Array, sym.tpe=(i: Int, x: T)Unit, tpe=(i: Int, x: Int)Unit, tpe.sym=<none>
						         arrTerm @ Ident(NameDef(arrName)), // sym=value arr, sym.owner=method selectionSort, sym.tpe=Array[Int], tpe=arr.type, tpe.sym=value arr, tpe.sym.owner=method selectionSort,
						         NameDef("update")),
						       List(
						         indexTerm,
						         valTerm)
						       )
						 )  if (app.symbol.owner.toString == "class Array") =>
							 val storeCallTree = LabelDef("PSArrUpdate", Nil, Block(List(arrTerm, indexTerm, valTerm), EmptyTree) )
							 val pre = st.pre
							 val post = Some(Invariant2(None, Unknown(), None))
							 val rhsST = SpecTree(pre, Some(storeCallTree), post)
							 Some((Var.mkVar(arrName, PSArrayInt), rhsST))
				case _ => None
			}
			retVal
		}
	}

	object ValDefSpecTree {
		/** Extracts Var, isVariable (true in case of var false in case of val) and rhs Spec Tree*/
		def unapply(st: SpecTree) : Option[(Var, Boolean, SpecTree)] =
		funlog("ValDefSpecTree::unapply", st){ st =>
			import progsynth.printers.RichTree._
			val retVal = st.tree match {
				case Some(valdef @ ValDef(_, aTerm, tpeTree, rhs)) =>
					//Some(Atom(Pred("$eq$eq", Var(varName.toString, tpeTree.tpe.toString) :: FnApp(rhs.toString, Nil):: Nil)))
					val varName = aTerm.toString
					getPSType(tpeTree.tpe.toString) map { valTpe =>
						//val newPost = st.post map { _ updateRvVar Var(varName, valTpe) }
						var unknownInv = Invariant2(None, Unknown(), None)
						val rhsST = SpecTree(st.pre, Some(rhs), Some(unknownInv)) //do not set post or rhs during extraction.
						(Var.mkVar(varName, valTpe), valdef.symbol.isVariable, rhsST)
					}
				case _ => None
			}
			retVal
		}
	}

	object IdentifierSpecTree {
		def unapply(st: SpecTree) : Option[(String, String)] =
		funlog("IdentifierSpecTree ::unapply", st){ st =>
			st.tree match {
				case Some(identObj@Ident(name)) => Some(name.toString, identObj.tpe.toString)
				case _ => None
			}
		}
	}

	object ConstantSpecTree {
		def unapply(st: SpecTree) : Option[String] =
		funlog("ConstantSpecTree::unapply", st){ st =>
			st.tree match {
				case Some(Literal(Constant(aConst))) => Some(aConst.toString)
				case _ => None
			}
		}
	}

	object MethodAppSpecTree {
		def unapply(st: SpecTree) : Option[(SpecTree, String, List[SpecTree])] =
		funlog("MethodAppSpecTree::unapply", st){ st =>
			st.tree match {
				case Some(ObjMethodApp(targetTree, methodName, argTrees)) =>
					val targetPostInvOpt = Some(Invariant2(None, Unknown(), None))//TODO: correct
					val targetST = SpecTree(st.pre, Some(targetTree), targetPostInvOpt)
					val argSTs = argTrees map { argTree =>
						val argPostInvOpt = Some(Invariant2(None, Unknown(), None)) //TODO: correct
						SpecTree(st.pre, Some(argTree), argPostInvOpt )
					}
					Some(targetST, methodName, argSTs)
				case _ => None
			}
		}
	}

	object ExprSpecTree {
		def unapply(st: SpecTree) : Option[Term] =
		funlog("ExprSpecTree::unapply", st){ st =>
			st.tree match {
				case Some(TermOfTree(aTerm)) => Some(aTerm)
				case _ => None
			}
		}
	}
}
*/
