package progsynth.extractors
/*
import progsynth.PSPredef._
import progsynth.logger.XMLLogWriter
import progsynth.logger.FunLogger._
import scala.tools.nsc.plugins.PluginComponent
import progsynth.types._
import progsynth.types.Types._

import progsynth.printers.RichTree._
import progsynth.types.PSType._
import progsynth.debug.PSDbg

trait ASTTerm2Term extends ExtractorsOnAST { self: AST2Term =>
	import global._

	object TermsOfTermsTree {
		def unapply(aTree: Tree): Option[List[Term]] =
		funlog("TermsTree::unapply", aTree){ aTree =>
			aTree match {
				case ListApplyTree(TermsOfTermTrees(terms)) =>
					Some(terms)
				case _ =>
					None
			}
		}
	}

	private object ListApplyTree {
		def unapply(aTree: Tree): Option[List[Tree]] =
			funlog("ListApplyTree::unapply", aTree){ aTree =>
			aTree match {
			case Apply(TypeApply(abc, _), rList)
			if ((abc.symbol.toString == "method apply") && (abc.symbol.owner.toString == "object List")) =>
			Some(rList)
			case _ =>
			None
			}
		}
	}

	private object TermsOfTermTrees {
		def unapply(aTree: List[Tree]): Option[List[Term]] =
		funlog("TermTrees::unapply", aTree){ aTree =>
			aTree match {
				case Nil =>
					Some(Nil)
				case TermOfTermTree(term) :: TermsOfTermTrees(terms) =>
					Some(term :: terms)
				case _ =>
					None
			}
		}
	}

	object TermOfTermTree {
		def unapply(aTree: Tree): Option[Term] = {
			PSDbg.writeln0(aTree.printNodes(global))
			aTree match {
			//	case VarTree(aVar) =>
			//		Some(aVar)
			//	case ConstTree(aConst) =>
			//		Some(aConst)
			//	case FnAppTree(aFn) =>
			//		Some(aFn)
			//	case ArrSelectTree(aArrSelect) =>
			//		Some(aArrSelect)
				case _ =>
					None
			}
		}
	}

	object VarTree {
		def unapply(aTree: Tree): Option[Term] =
		funlog("VarTree::unapply", aTree){ aTree =>
			val varClass = classOf[Var].getName
			/**val varClass = "progsynth.types.Var"*/
			PSDbg.writeln0((aTree.printNodes(global)))
			aTree match {
				//If Var is a case class now
				case CaseConstructorTree(`varClass`, Literal(Constant(varName)) :: PSTypeOfTree(aPSType) :: Nil) =>
					Some(Var.mkVar(varName.toString, aPSType))
				//If Var is a not a case class.
				case ApplyConstructorTree(`varClass`, Literal(Constant(varName)) :: PSTypeOfTree(aPSType) :: Nil) =>
					Some(Var.mkVar(varName.toString, aPSType))
				case _ =>
					None
			}
		}
	}

	object ConstTree {
		def unapply(aTree: Tree): Option[Const] =
			funlog("FnTree::unapply", aTree){ aTree =>
			val constClass = classOf[Const].getName
			/**val constClass = "progsynth.types.Const"*/
			aTree match {
			//If Const is a case class
			case CaseConstructorTree(`constClass`, Literal(Constant(constName)) :: PSTypeOfTree(aPSType) :: Nil) =>
				Some(Const.mkConst(constName.toString, aPSType))
			//If Const is not a case class
			case ApplyConstructorTree(`constClass`, Literal(Constant(constName)) :: PSTypeOfTree(aPSType) :: Nil) =>
				Some(Const.mkConst(constName.toString, aPSType))
			case _ =>
				None
			}
		}
	}

	object FnAppTree {
		def unapply(aTree: Tree): Option[FnApp] =
		funlog("FnAppTree::unapply", aTree){ aTree =>
			val fnAppClass = classOf[FnApp].getName
			aTree match {
				// If FnApp is a case class
				case CaseConstructorTree(`fnAppClass`, FnTree(aFn) :: TermsOfTermsTree(terms) :: Nil) =>
					Some(FnApp.mkFnApp(aFn, terms, aFn.tpe))
				// If FnApp is not a case class
				case ApplyConstructorTree(`fnAppClass`, FnTree(aFn) :: TermsOfTermsTree(terms) :: Nil) =>
					Some(FnApp.mkFnApp(aFn, terms, aFn.tpe))
				case _ =>
					None
			}
		}
	}

	object ArrSelectTree {
		def unapply(aTree: Tree): Option[ArrSelect] =
		funlog("ArrSelectTree::unapply", aTree){ aTree =>
			//val fnAppClass = classOf[FnApp].getName
			aTree match {
				case _ =>
					None
			}
		}
	}

	object FnTree {
		def unapply(aTree: Tree): Option[Fn] =
		funlog("FnTree::unapply", aTree){ aTree =>
			//writeln0((aTree.printNodes(global))
			//writeln0((progsynth.printers.XMLPrinters.toXml(aTree))
			val fnClass = classOf[Fn].getName
			aTree match {
				case CaseConstructorTree(`fnClass`,
						Literal(Constant(fnName)) ::
						ListApplyTree(PSTypesOfTree(argTypes)) ::
						PSTypeOfTree(aPSType) :: Nil) =>
							Some(Fn(fnName.toString, argTypes, aPSType))
				case _ => None
			}
		}
	}

	object PSTypeOfTree {
		def unapply(aTree: Tree): Option[PSType] = aTree match {
			case ObjExtractor(SubPackage(RootPackage("progsynth"), "types"), typeName) => typeName match {
				case "PSInt" => Some(progsynth.types.PSInt)
				case "PSReal" => Some(progsynth.types.PSReal)
				case "PSBool" => Some(progsynth.types.PSBool)
				case "PSUnit" => Some(progsynth.types.PSUnit)
				case "PSArrayInt" => Some(progsynth.types.PSArrayInt)
				case _ => throw new RuntimeException("Failed to extract "+ typeName)
			}
			case _ =>
				None
		}
	}

	object PSTypesOfTree {
		def unapply(trees: List[Tree]): Option[List[PSType]] = trees match {
			case PSTypeOfTree(aType) :: PSTypesOfTree(types) => Some(aType :: types)
			case PSTypeOfTree(aType) => Some(List(aType))
			case Nil => Some(Nil)
		}
	}
}
*/