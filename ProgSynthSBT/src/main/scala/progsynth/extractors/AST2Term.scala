package progsynth.extractors
/*
import progsynth.PSPredef._
import progsynth.logger.XMLLogWriter
import progsynth.logger.FunLogger._
import scala.tools.nsc.plugins.PluginComponent
import progsynth.types._
import progsynth.types.Types._

import progsynth.printers.RichTree._

trait AST2Term extends ASTTerm2Term with ASTExpr2Term {
	import global._
	object TermOfTree {
		def unapply(aTree: Tree): Option[Term] =
		funlog("TermOfTree::unapply", aTree){ aTree =>
			//writeln0(progsynth.print.TreePrinterUtils.treeNodes2Str(global, aTree))
			aTree match {
				case TermOfTermTree(term) => Some(term)
				case TermOfExprTree(term) => Some(term)
				case _ => XMLLogWriter.writelog(<info>match failed. TermOfTree</info>)
					None
			}
		}
	}

	object TermsOfTrees {
		def unapply(trees: List[Tree]): Option[List[Term]] = trees match {
			case Nil => Some(Nil)
			case TermOfTree(term) :: TermsOfTrees(terms) => Some(term :: terms)
			case _ => None
		}
	}

}
*/