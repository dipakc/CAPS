package progsynth.extractors
/*
import progsynth.PSPredef._
import progsynth.logger.XMLLogWriter
import progsynth.logger.FunLogger._
import scala.tools.nsc.plugins.PluginComponent
import progsynth.types._
import progsynth.types.Types._

import progsynth.types.PSType._
import progsynth.printers.RichTree._

trait ASTBool2Formula extends AST2Term { self: AST2Formula =>
	import global._
	object FormulaOfBoolTree {
		def unapply(aTree: Tree): Option[FOLFormula] =
		funlog("FormulaOfBoolTree::unapply", aTree){ aTree =>
			//writeln0(aTree)
			//writeln0(aTree.printNodes(global))
			aTree match {
				case Boolean2AtomApp(FormulaOfTree(formula)) =>
					Some(formula)
				case UnaryMethodApp("unary_$bang", FormulaOfTree(formula)) =>
					Some(Not(formula))
				case ObjMethodApp(objTree, fname, argTree :: Nil) =>
					if (fname == "$amp$amp") {
						(objTree, argTree) match {
							case (FormulaOfTree(f1), FormulaOfTree(f2)) =>
								Some(And(f1, f2))
							case _ =>
								None
						}
					} else if (fname == "$bar$bar") {
						(objTree, argTree) match {
							case (FormulaOfTree(f1), FormulaOfTree(f2)) =>
								Some(Or(f1,f2))
							case _ =>
								None
						}
					} else if (fname == "$bang$eq") {
						(objTree, argTree) match {
							case (TermOfTree(objTerm), TermOfTree(argTerm)) =>
								Some(Not(Atom(Pred("$eq$eq", List(objTerm, argTerm)))))
							case _ => None
						}
					} else if (List("$eq$eq", "$less$eq", "$greater$eq", "$less", "$greater").contains(fname)) {
						(objTree, argTree) match {
							case (TermOfTree(objTerm), TermOfTree(argTerm)) =>
								Some(Atom(Pred(fname, List(objTerm, argTerm))))
							case _ => None
						}
					}
					else
						None
				// any function call (The function should return boolean)
				case Apply( Select( _, NameDef(predName)), TermsOfTrees(argTerms)) =>
					//writeln0(aTree.printNodes(global))
					Some(Atom(Pred(predName, argTerms)))
				case _ =>
					//writeln0("xxxxxxxxxxxxxxxx")
					//writeln0(aTree.printNodes(global))
					XMLLogWriter.writelog(<info>match failed. FormulaOfTree</info>)
					None
			}
		}
	}
	object BoolLitTree {

	}

	object BoolAndTree {

	}

	object BoolOrTree {

	}

	object BoolNotTree {

	}

	//forall(scalaVarDecls => \varphi
	object ForallFunTree {

	}

	//exists(scalaVarDecls => \varphi
	object ExistsFunTree {

	}

	object InfixBoolOprTree {

	}

	object BoolFunCallTree {

	}
}
*/