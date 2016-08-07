package progsynth.extractors
//
////import scala.reflect.generic.Universe
//import scala.collection.mutable.ListBuffer
//import scala.tools.nsc.plugins.PluginComponent
//import progsynth.PSPredef._
//import progsynth.types._
//import progsynth.types.Types._
//
////import scala.reflect.generic.Names
//import scala.xml.Elem
//import progsynth.utils.folformulautils.BoolToFormula._
//import progsynth.printers._
//import progsynth.logger.FunLogger._
//import progsynth.logger._
//import progsynth.printers.RichTree._
//
//trait ExtractorsOnAST extends PluginComponent {
//	implicit val iglobal = global
//	import global._
//
//	//////////////////* Basic Extractors/////////////////f//////////////
//	object BoolOfTree {
//		def unapply(t: Tree): Option[Boolean] =
//			funlog("BoolOfTree::unapply", t) { t =>
//				t match {
//					case Literal(Constant(aBool)) if (aBool.toString == "true") => Some(true)
//					case Literal(Constant(aBool)) if (aBool.toString == "false") => Some(false)
//					case _ => None
//				}
//			}
//	}
//
//	object CaseConstructorTree {
//		def unapply(t: Tree): Option[(String, List[Tree])] =
//			funlog("CaseConstructorTree::unapply", t) { t =>
//				t match {
//					case Apply(Select(New(_), name), argList) if (name.toString == "<init>") =>
//						(Some(t.tpe.toString, argList))
//					case _ =>
//						None
//				}
//			}
//	}
//
//	/** Extractor for companion object apply method (constructor)*/
//	object ApplyConstructorTree{
//		def unapply(t: Tree): Option[(String, List[Tree])] =
//			funlog("ApplyConstructorTree::unapply", t) { t =>
//				t match {
//					case Apply( Select(_, NameDef("apply")),argList) =>
//						(Some(t.tpe.toString, argList))
//					case _ =>
//						None
//				}
//			}
//	}
//
//	//* FunApp(fQualifier:String, fName:String, argTreeList:List[Tree])
//	object FunApp {
//		def unapply(t: Tree): Option[(String, String, List[Tree])] =
//			funlog("FunApp::unapply", t) { t =>
//				t match {
//					case Apply(Select(qualifier, fname), argList) =>
//						Some(qualifier.toString, fname.toString, argList)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. FunApp</info>);
//						None
//				}
//			}
//	}
//
//	//* TFunApp(fQualifier:String, fName:String, argTreeList:List[Tree])
//	object TFunApp {
//		def unapply(t: Tree): Option[(String, String, List[Tree])] =
//			funlog("TFunApp::unapply", t) { t =>
//				t match {
//					case Apply(TypeApply(Select(qualifier, fname), _), argList) =>
//						Some(qualifier.toString, fname.toString, argList)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. TFunApp</info>);
//						None
//				}
//			}
//	}
//
//	/**FunctionDef(fnName: String, params: List[Var], retType: PSType, body: Tree)*/
//	object FunctionDef {
//		def unapply(aTree: Tree): Option[(String, List[Var], PSType,  Tree)] =
//			funlog("FunctionDef::unapply", aTree) { aTree =>
//				aTree match {
//					case DefDef(mods, name, tparams, List(vParams), TypeTreeFromPSType(rtpe), rhs) =>
//						val varList = vParams flatMap {
//							case ValDefTreeFromVar(aVar) => Some(aVar)
//							case _ => None
//						}
//						if (varList.length == vParams.length) {
//							Some((name.toString, varList, rtpe,  rhs))
//						} else {
//							None
//						}
//					case _ =>
//						None
//				}
//			}
//	}
//
//	object ValDefTreeFromVar {
//		def unapply(aTree: Tree): Option[Var] = aTree match {
//			case ValDef(_, NameDef(varName), TypeTreeFromPSType(psTpe), _) => Some(Var.mkVar(varName, psTpe))
//			case _ => None
//		}
//	}
//
//	//* Extractor for single param annonymous function.
//	//* Returns parameter and body.
//	object AnnonymousFunction {
//		def unapply(aTree: Tree): Option[(String, String, Tree)] =
//			funlog("AnnonymousFunction::unapply", aTree) { aTree =>
//				aTree match {
//					case Function(List(ValDef(_, rvVar, tpeTree, _)), body) =>
//						Some(rvVar.toString, tpeTree.tpe.toString, body)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. AnnonymousFunction</info>)
//						None
//				}
//			}
//	}
//
//	//* Scala: foo(arglist1)(arglist2)
//	//* Curried2FunctionApp(fqualifier:String, fname:String, args1: List[Tree], args2: List[Tree])
//	object Curried2FunctionApp {
//		def unapply(aTree: Tree): Option[(String, String, List[Tree], List[Tree])] =
//			funlog("Curried2FunctionApp::unapply", aTree) { aTree =>
//				aTree match {
//					case Apply(Apply(Select(fqualifier, fname), args1), args2) =>
//						Some((fqualifier.toString, fname.toString, args1, args2))
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. Curried2FunctionApp</info>)
//						None
//				}
//			}
//	}
//
//	//* Scala: foo[T](arglist1)(arglist2)
//	//* TypedCurried2FunctionApp(fqualifier: String, fname: String, args1: List[Tree], args2: List[Tree])
//	object TypedCurried2FunctionApp {
//		def unapply(aTree: Tree): Option[(String, String, List[Tree], List[Tree])] =
//			funlog("TypedCurried2FunctionApp::unapply", aTree) { aTree =>
//				aTree match {
//					case Apply(Apply(TypeApply(Select(fqualifier, fname), _), args1), args2) =>
//						Some((fqualifier.toString, fname.toString, args1, args2))
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. TypedCurried2FunctionApp</info>)
//						None
//				}
//			}
//	}
//
//	object UnaryMethodApp {
//		def unapply(aTree: Tree): Option[(String, Tree)] =
//			funlog("UnaryMethodApp::unapply", aTree) { aTree =>
//				aTree match {
//					case Select(t, unaryf) =>
//						Some(unaryf.toString, t)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. UnaryMethodApp <tree>{ aTree }</tree> </info>)
//						None
//				}
//			}
//	}
//
//	//* Scala: alpha.foo(beta)
//	//* ObjMethodApp(alpha:Tree, foo:String, beta:List[Tree])
//	object ObjMethodApp {
//		def unapply(aTree: Tree): Option[(Tree, String, List[Tree])] =
//			funlog("ObjMethodApp::unapply", aTree) { aTree =>
//				aTree match {
//					case Apply(Select(alpha, methodName), beta) =>
//						Some(alpha, methodName.toString, beta)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. ObjMethodApp <tree>{ aTree }</tree> </info>)
//						None
//				}
//			}
//	}
//
//	object RootPackage {
//		def unapply(aTree: Tree): Option[String] =
//			funlog("RootPackage::unapply", aTree) { aTree =>
//				aTree match {
//					case Ident(name) =>
//						Some(name.toString)
//					case _ =>
//						None
//				}
//			}
//	}
//
//	object SubPackage {
//		def unapply(aTree: Tree): Option[(Tree, String)] =
//			funlog("SubPackage ::unapply", aTree) { aTree =>
//				aTree match {
//					case Select(subTree, name) =>
//						Some(subTree, name.toString)
//					case _ =>
//						None
//				}
//			}
//	}
//
//	object ValueExtractor {
//		def unapply(aTree: Tree): Option[(Tree, String)] =
//			funlog("ValueExtractor::unapply", aTree) { aTree =>
//				aTree match {
//					case Select(subTree, aVal) =>
//						Some(subTree, aVal.toString)
//					case _ =>
//						None
//				}
//			}
//	}
//
//	object ObjExtractor {
//		def unapply(aTree: Tree): Option[(Tree, String)] =
//			funlog("ObjExtractor::unapply", aTree) { aTree =>
//				aTree match {
//					case Select(subTree, objName) =>
//						Some(subTree, objName.toString)
//					case _ =>
//						None
//				}
//			}
//	}
//	//Already existing case classes : If, Block, Assign, Ident.
//	/* Input: AST corresponding to While loop.
//	 * while loops are desugared to label defs as follows:
//	 * while (cond) body ==> LabelDef($L, List(), if (cond) { body; L$() } else ())
//	 * Output: (condTree, bodyTree)
//	 *
//	 */
//	object WhileDef {
//		def unapply(aTree: Tree): Option[(Tree, Tree)] =
//			funlog("WhileDef::unapply", aTree) { aTree =>
//				aTree match {
//					case LabelDef(lblName, List(), rhs) if lblName.toString.startsWith("while$") => rhs match {
//						case If(condTree, thenTree, _elseTree) =>
//							Some(condTree, thenTree)
//						case _ => None
//					}
//					case _ => None
//				}
//			}
//	}
//
//	object NameDef {
//		def unapply(aName: global.Name): Option[String] = aName match {
//			case _ => Some(aName.toString)
//		}
//	}
//	////////////////* II Tier Extractors *///////////////////////////////
//
//	object SAssert {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("SAssert::unapply", aTree) { aTree =>
//				//writeln0((aTree.printNodes(global))
//				aTree match {
//					/**formula without quantifiers*/
//					case ObjMethodApp(_, "sAssert", List(formulaTree)) =>
//						Some(formulaTree) //TODO: check the package
//					/**formula with quantifiers: Anonymous function*/
//					case Apply(
//						TypeApply(
//							Select(
//								Select(
//									Select(
//										Ident(NameDef("progsynth")),
//										NameDef("spec")),
//									NameDef("StaticAssertions")),
//								NameDef("sAssert")),
//							_
//							),
//						List(Function(_, formulaTree))) => Some(formulaTree)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. SRequire</info>);
//						None
//				}
//			}
//	}
//
//	//* returns preTree, blockTree
//	object SRequire {
//		def unapply(aTree: Tree): Option[(Tree, Tree)] =
//			funlog("SRequire::unapply", aTree) { aTree =>
//				aTree match {
//					case TypedCurried2FunctionApp(fqualifier, "sRequire", preTree :: Nil, blockTree :: Nil) if fqualifier.endsWith("StaticAssertions") =>
//						Some(preTree, blockTree)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. SRequire</info>)
//						None
//				}
//			}
//	}
//	////* Extractor for sEnsuring function call
//	////* block.sEnsuring(postTree)
//	////* block here is the tree for the any2sensuring function call
//	//object SEnsuring {
//	//	def unapply(aTree: Tree): Option[(Tree, Tree)] =
//	//	funlog("SEnsuring::unapply", aTree){ aTree =>
//	//		writeln0(aTree.printNodes(global))
//	//		aTree match {
//	//			case ObjMethodApp(blockTree, "sEnsuring", List(postTree)) =>
//	//				Some(blockTree, postTree)
//	//			case _ =>
//	//				XMLLogWriter.writelog(<info>match failed. SEnsuring<tree>{aTree}</tree></info>)
//	//				None
//	//		}
//	//	}
//	//}
//
//	/**
//	 * Extractor for sEnsuring function call
//	 * block.sEnsuring((x)=>(p, q)=> postTree)
//	 * block here is the tree for the any2sensuring function call
//	 */
//	object SEnsuring {
//		def unapply(aTree: Tree): Option[(Tree, Tree)] =
//			funlog("SEnsuring::unapply", aTree) { aTree =>
//				//writeln0((aTree.printNodes(global))
//				aTree match {
//					case Apply(
//						Select(
//							blockTree,
//							NameDef("sEnsuring")), //TODO: add more checks
//						List(postTree)
//						) =>
//						Some((blockTree, postTree))
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. SEnsuring<tree>{ aTree }</tree></info>)
//						None
//				}
//			}
//	}
//
//	object SLoopInv {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("SLoopInv::unapply", aTree) { aTree =>
//				//writeln0((aTree.printNodes(global))
//				aTree match {
//					/**formula without quantifiers*/
//					case ObjMethodApp(_, "sLoopInv", List(formulaTree)) =>
//						Some(formulaTree) //TODO: check the package
//					/**formula with quantifiers: Anonymous function*/
//					case Apply(
//						TypeApply(
//							Select( // sym=method sLoopInv, sym.owner=trait LoopInvDefs,
//								Select( // sym=object StaticAssertions, sym.owner=package spec, sym.tpe=object progsynth.spec.StaticAssertions
//									Select( // sym=package spec, sym.owner=package progsynth, sym.tpe=package progsynth.spec
//										Ident(NameDef("progsynth")), // sym=package progsynth, sym.owner=package <root>, sym.tpe=package progsynth, tpe=progsynth.type
//										NameDef("spec")),
//									NameDef("StaticAssertions")),
//								NameDef("sLoopInv")),
//							_
//							),
//						List(Function(_, formulaTree))) => Some(formulaTree)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. SLoopInv</info>);
//						None
//				}
//			}
//	}
//
//	//* Extractor for the any2sEnsuring function call
//	// returns the tree for block argument to this method
//	object Any2Ensuring {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("Any2Ensuring::unapply", aTree) { aTree =>
//				aTree match {
//					case TFunApp(_, "any2Ensuring", block :: Nil) => //TODO: add qualifier constraint
//						Some(block)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed Any2Ensuring</info>);
//						None
//				}
//			}
//	}
//
//	//* Extractor for boolean2atom function call
//	//* Boolean2AtomApp(booleanTree)
//	object Boolean2AtomApp {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("Boolean2AtomApp::unapply", aTree) { aTree =>
//				aTree match {
//					case FunApp(_, "boolean2Atom", booleanTree :: Nil) => //todo: add qualifier condition as well.
//						Some(booleanTree)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed Booean2AtomApp</info>)
//						None
//				}
//			}
//	}
//
//	///////////////////////////////* Tier III *///////////////////////////////
//	// * Returns (preTree, blockTree, postTree)
//	object SRequireSEnsuring {
//		def unapply(aTree: Tree): Option[(Tree, Tree, Tree)] =
//			funlog("SRequireSEnsuring::unapply", aTree) { aTree =>
//				aTree match {
//					case SEnsuring(Any2Ensuring(SRequire(pre, block)), post) =>
//						Some(pre, block, post)
//					case _ =>
//						XMLLogWriter.writelog(<info>match failed. SRequireSEnsuring</info>)
//						None
//				}
//			}
//	}
//
//	object WhileBlockDef {
//		def unapply(aTree: Tree): Option[(Tree, Tree, Tree)] =
//			funlog("WhileBlockDef::unapply", aTree) { aTree =>
//				aTree match {
//					case Block(SLoopInv(loopInvTree) :: Nil, WhileDef(condTree, bodyTree)) =>
//						Some(loopInvTree, condTree, bodyTree)
//					case _ => None
//				}
//			}
//	}
//
//	/**
//	 * DefinePredicateTree(body: Tree)
//	 * For extracting bodyTree enclosed in definePredicate
//	 * {{{
//	 * object myPreds extends PredicateDefsTrait {
//	 * //...
//	 * //arr[m] is the minimum of the of arr[p..q]
//	 * def minElem(m: Int, arr: Array[Int], p: Int, q: Int) = definePredicate { (i: Int) =>
//	 * ∀(i)∘(p <= i && i < q impl arr(m) <= arr(i))
//	 * }
//	 * //...
//	 * }}}
//	 */
//	object DefinePredicateTree {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("DefinePredicateTree::unapply", aTree) { aTree =>
//				aTree match {
//					case Apply(TypeApply(Select(Select(Select(
//						Ident(NameDef("progsynth")), NameDef("spec")),
//						NameDef("StaticAssertions")), NameDef("definePredicate")), _),
//						List(body)) => Some(body)
//					case _ => None
//				}
//			}
//	}
//
//	//AnonymousFunctionTree(body: Tree)
//	object AnonymousFunctionTree {
//		def unapply(aTree: Tree): Option[Tree] =
//			funlog("AnonymousFunction::unapply", aTree) { aTree =>
//				aTree match {
//					case Function(_, body) => Some(body)
//					case _ => None
//				}
//			}
//	}
//
//	/*	Formal parameter of a method. Third argument of ValDef is a TypeTree
//	 * ValDef( // sym=value m, sym.owner=method Sorted, sym.tpe=Int, tpe=<notype>, tpe.sym=<none>
//		ram>, // flags=<param>, annots=List()
//		"m",
//		TypeTree(), // sym=class Int, tpe=Int, tpe.sym=class Int, tpe.sym.owner=package scala,
//		EmptyTree
//		)
//	*/
//	object TypeTreeFromPSType {
//		def unapply(aTree: Tree): Option[PSType] =
//			funlog("TypeTreeFromPSType::unapply", aTree) { aTree =>
//				aTree match {
//					case tt @ TypeTree() =>
//						tt.tpe.toString match {
//							case "Int" => Some(PSInt)
//							case "Boolean" => Some(PSBool)
//							case "Array[Int]" => Some(PSArrayInt)
//							case "Unit" => Some(PSUnit)
//							case _ => None
//						}
//					case _ => None
//				}
//			}
//	}
//}
