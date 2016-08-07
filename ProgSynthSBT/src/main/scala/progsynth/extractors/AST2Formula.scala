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
import progsynth.types.PSType._

//TODO: remove the dummyForTypeChecking variables.
trait AST2Formula extends ExtractorsOnAST
							with ASTFormula2Formula with ASTBool2Formula with ASTQuant2Formula
							/*with ASTTerm2Term with ASTExpr2Term */{
	import global._
	object InvOfTree {
		def unapply(aTree: Tree): Option[Invariant] =
		funlog("InvOfTree::unapply", aTree){ aTree =>
			import progsynth.printers.RichTree._
			aTree match {
				case AnnonymousFunction(rvVarName, rvVarTpe, FormulaOfTree(formula)) =>
					for (rvVarPSTpe <- getPSType(rvVarTpe)) yield {
						val rvVar = Var.mkVar(rvVarName, rvVarPSTpe)
						val newRvVar =
							if (formula.getFreeVars contains rvVar)
								Some(rvVar)
							else
								None //TODO: make it more efficient.
						Invariant(None, formula, newRvVar)
					}
				//(rvVar) => (boundVars) => FormulaTree
				case AnnonymousFunction(rvVarName, rvVarTpe, Function(boundVars, FormulaOfTree(formula))) =>
					for (rvVarPSTpe <- getPSType(rvVarTpe)) yield {
						val rvVar = Var.mkVar(rvVarName, rvVarPSTpe)
						val newRvVar =
							if (formula.getFreeVars contains rvVar)
								Some(rvVar)
							else
								None //TODO: make it more efficient.
						Invariant(None, formula, newRvVar)
					}
				case FormulaOfTree(formula) =>
					Some(Invariant(None, formula, None))
				case _ =>
					XMLLogWriter.writelog(<info>match failed. InvariantOfTree <tree>{ aTree }</tree></info>)
					None
			}
		}
	}

	object FormulaOfTree {
		def unapply(aTree: Tree): Option[FOLFormula] =
		funlog("FormulaOfTree::unapply", aTree){ aTree =>
			//writeln0((progsynth.print.TreePrinterUtils.treeNodes2Str(global, aTree))
			aTree match {
				case FormulaOfQuantTree(formula) =>
					Some(formula)
				case FormulaOfFormulaTree(formula) =>
					Some(formula)
				case FormulaOfBoolTree(formula) =>
					Some(formula)
				case _ =>
					XMLLogWriter.writelog(<info>match failed. FormulaOfTree</info>)
					None
			}
		}
	}
}
*/