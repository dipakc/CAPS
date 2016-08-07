package progsynth.extractors
/*
import progsynth.PSPredef._
import progsynth.logger.XMLLogWriter
import progsynth.logger.FunLogger._
import scala.tools.nsc.plugins.PluginComponent
import progsynth.types._
import progsynth.types.Types._
import progsynth.ProgSynth._
import progsynth.types.PSType._
import progsynth.printers.RichTree._

trait ASTQuant2Formula extends AST2Term { self: AST2Formula =>
	import global._
	val ringOpr = "$u2218" //°
	val forallOpr = "$u2200" //∀
	val existsOpr = "$u2203" //∃

	object FormulaOfQuantTree {
		/**
		 * Scala expr: ∀(i)∀(j)∘((0 <= i && i < N && i <= j && 0 <= j && j < N) impl (arr(i) <= arr(j)))
		 *
		 * AST: $at$at.∀[Int](i).∀[Int](j).∘(boolean2Atom(0.<=(i).&&(i.<(N)).&&(i.<=(j)).&&(0.<=(j)).&&(j.<(N))).impl(boolean2Atom(arr.apply(i).<=(arr.apply(j)))))
		 *
		 * */
		def unapply(aTree: Tree): Option[FOLFormula] =
		funlog("FormulaOfQuantTree::unapply", aTree){ aTree =>
			aTree match {
			case ringApp @ Apply(Select( t1, NameDef(`ringOpr`)), List(FormulaOfTree(f1)))
				if (ringApp.tpe.toString() == "progsynth.types.package.FOLFormula" &&
						ringApp.symbol.owner.toString() == "object @@" )=>
				type dummyType = progsynth.types.Types.FOLFormula /** For static type checking */
				val dummyVal = progsynth.spec.StaticAssertions.@@
				//writeln0((aTree)
				extractFormula(t1, f1)
			case _ =>
				XMLLogWriter.writelog(<info>match failed. FormulaOfQuantTree</info>)
				None
			}
		}
	}

	/**
	 * aTree: $at$at.∀[Int](i).∀[Int](j)
	 *
	 * formula: Impl(And(And(And(And(Atom(Pred($less$eq,List(ConstInt(0), VarInt(i)))),Atom(Pred($less,List(VarInt(i), VarInt(N))))),Atom(Pred($less$eq,List(VarInt(i), VarInt(j))))),Atom(Pred($less$eq,List(ConstInt(0), VarInt(j))))),Atom(Pred($less,List(VarInt(j), VarInt(N))))),Atom(Pred($less$eq,List(ArrSelect(VarInt(arr,PSArrayInt),Var(i)), ArrSelect(VarInt(arr,PSArrayInt),Var(j))))))
	 *
	 * result: Some(Forall(i,Forall(j,<formula>)))
	 * */
	def extractFormula(qTree: Tree, formula: FOLFormula): Option[FOLFormula] = {
		val atatObj = progsynth.spec.StaticAssertions.@@
		qTree match {
			case Select(
                   Select(
                     Select(
                       Ident(NameDef("progsynth")),
                       NameDef("spec")),
                     NameDef("StaticAssertions")),
                   NameDef("$at$at")) => Some(formula)
			case  Apply(
		            TypeApply(
		              Select(
		                t2,
		                NameDef(quantName)),
		              List(
		              TypeTree()
		              )
		            ),
		            List(
		              Ident(NameDef(varName))
		            )
		          ) => quantName match {
		          			case `forallOpr` => extractFormula(t2, Forall(VarInt(varName), formula)) //TODO: Removed hardcoded VarInt
		          			case `existsOpr` => extractFormula(t2, Exists(VarInt(varName), formula)) //TODO: Removed hardcoded VarInt
		          			case _ => None
		           		}
			case _ => None
		}
	}
}
*/