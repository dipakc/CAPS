package progsynth.extractors
/*
import progsynth.PSPredef._
import progsynth.logger.XMLLogWriter
import progsynth.logger.FunLogger._
import scala.tools.nsc.plugins.PluginComponent
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.printers.RichTree._

trait ASTFormula2Formula extends AST2Term { self: AST2Formula =>
	import global._
	object FormulaOfFormulaTree {
		def unapply(aTree: Tree): Option[FOLFormula] =
		funlog("FormulaOfCaseTree::unapply", aTree){ aTree =>
			aTree match {
				case TrueFalseTree(tf) =>
					Some(tf)
				case NotTree(FormulaOfTree(f)) =>
					Some(Not(f))
				case AtomTree(PredTree(pred)) =>
					Some(Atom(pred))
				case AndTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					Some(And(f1, f2))
				case OrTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					Some(Or(f1, f2))
				case ImplTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					Some(Impl(f1, f2))
				case IffTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					Some(Iff(f1, f2))
				case _ => aTree match {
					case ImplInfixTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
						Some(Impl(f1, f2))
					case IffInfixTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
						Some(Iff(f1, f2))
					case AndInfixTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
						Some(And(f1, f2))
					case OrInfixTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
						Some(Or(f1, f2))
					//case ForallTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					//	Some(Iff( f1, f2))
					//case ExistsTree(FormulaOfTree(f1), FormulaOfTree(f2)) =>
					//	Some(Iff( f1, f2))
					case _ =>
						None
				}
			}
		}
	}

	object TrueFalseTree {
		def unapply(aTree: Tree): Option[FOLFormula] =
		funlog("TrueFalseTree::unapply", aTree){ aTree =>
			aTree match {
				case ValueExtractor(ObjExtractor(SubPackage(RootPackage("progsynth"), "types"), "package"), tf)
				if (tf == "True") || (tf == "False") =>
					val dummyForTypeChecking = progsynth.types.Types.TrueF
					if (tf == "True")
						Some(True1())
					else
						Some(False1())
				case _ =>
					None
			}
		}
	}

	object NotTree {
		def unapply(aTree: Tree): Option[Tree] =
		funlog("NotTree::unapply", aTree){ aTree =>
			val notClass = classOf[Not[Pred]].getName + "["+ classOf[Pred].getName + "]"
			/**val notClass = "progsynth.types.Not[progsynth.types.Pred]"*/
			//writeln0((aTree.printNodes(global))
			val retVal = aTree match {
				case CaseConstructorTree(`notClass`, List(t)) =>
					Some(t)
				//! prefix operator
				case Apply(s @ Select( t, NameDef("unary_$bang")), Nil) if s.symbol.owner.toString == "class Formula" =>
					Some(t)
				case _ =>
					None
			}
			retVal
		}
	}

	object AtomTree {
		def unapply(aTree: Tree): Option[Tree] =
		funlog("AtomTree ::unapply", aTree){ aTree =>
			val atomClass = classOf[Atom[Pred]].getName + "["+ classOf[Pred].getName + "]"
			/**val atomClass = "progsynth.types.Atom[progsynth.types.Pred]"*/
			aTree match {
				case CaseConstructorTree(`atomClass`, predTree :: Nil) =>
					Some(predTree)
				case _ =>
					//writeln0(("Default Match AtomTree")
					None
			}
		}
	}

	def binaryUnapply(oprClass: String, aTree: Tree): Option[(Tree, Tree)] =
		funlog("binaryUnapply ::unapply", oprClass, aTree){ (oprClass, aTree) =>
		aTree match {
			case CaseConstructorTree(`oprClass`, List(t1, t2)) =>
				Some(t1, t2)
			case _ =>
				None
		}
	}

	val andClass = classOf[And[Pred]].getName + "["+ classOf[Pred].getName + "]"
	val orClass = classOf[Or[Pred]].getName + "["+ classOf[Pred].getName + "]"
	val iffClass = classOf[Iff[Pred]].getName + "["+ classOf[Pred].getName + "]"
	val implClass = classOf[Impl[Pred]].getName + "["+ classOf[Pred].getName + "]"

	object AndTree { def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryUnapply(andClass, aTree) }
	object OrTree { def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryUnapply(orClass, aTree) }
	object IffTree { def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryUnapply(iffClass, aTree) }
	object ImplTree { def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryUnapply(implClass, aTree) }

	def binaryInfixUnapply(oprName: String, aTree: Tree): Option[(Tree, Tree)] = {
		val dummyForTypeChecking: progsynth.types.Formula[progsynth.types.Pred] = progsynth.types.True1()
		val retVal =
			if (aTree.symbol == null || aTree.symbol.toString != "method " + oprName)
				None
			else if (aTree.tpe == null || aTree.tpe.toString != "progsynth.types.Formula[progsynth.types.Pred]")
				None
			else aTree match {
				case Apply(Select(t1, mName), List(t2)) if (mName.toString == oprName) =>
					Some(t1, t2)
				case _ =>
					None
			}
		retVal
	}

	//prefix operator ! is handled in NotTree

	object ImplInfixTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryInfixUnapply("impl", aTree)
	}

	object IffInfixTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryInfixUnapply("iff", aTree)
	}

	object AndInfixTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryInfixUnapply("&&", aTree)
	}

	object OrInfixTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = binaryInfixUnapply("||", aTree)
	}

	object ForAllTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = {
			aTree match {
				//TODO: Implement ExistsTree FormulaExtractor
				case _ => None
			}
		}
	}

	object ExistsTree {
		def unapply(aTree: Tree): Option[(Tree, Tree)] = {
			aTree match {
				//TODO: Implement ExistsTree FormulaExtractor
				case _ => None
			}
		}
	}

	object PredTree {
		def unapply(aTree: Tree): Option[Pred] =
		funlog("PredTree::unapply", aTree){ aTree =>
			val predClass = classOf[Pred].getName
			/**val predClass = "progsynth.types.Pred""*/
			aTree match {
				case CaseConstructorTree(`predClass`, Literal(Constant(predName))::TermsOfTermsTree(terms)::Nil) =>
					Some(Pred(predName.toString, terms))
				case _ =>
					None
			}
		}
	}
}
*/