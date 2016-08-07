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
import progsynth.methodspecs.InterpretedFns._

trait ASTExpr2Term extends ExtractorsOnAST { self: AST2Term =>
	import global._

	object TermOfExprTree {
		def unapply(aTree: Tree): Option[Term] = {
			aTree match {
				case NoneOfExprTree(_) => None //Rejected cases
				case ConstOfExprTree(const) => Some(const)
				case VarOfExprTree(aVar) => Some(aVar)
				// No need for ArrSelect, FnAppOfExprTree extracts ArrSelect.
				// case ArrSelectOfExprTree(aArrSelect) => Some(aArrSelect)
				case ArrStoreOfExprTree(arrStore) => Some(arrStore)
				case FnAppOfExprTree(aFn) => Some(aFn)
				case _ => None
			}
		}
	}

	object ArrStoreOfExprTree{
		def unapply(aTree: Tree): Option[ArrStore] = aTree match {
			/**The tree LabelDef(NameDef("ArrStoreCall")... is not created by scala.
			 * It is syntheic construct introduced in AssignmentSpecTree*/
			case LabelDef(NameDef("PSArrUpdate"),
					Nil,
					Block(List(TermOfTree(arrTerm), TermOfTree(indexTerm), TermOfTree(valTerm)),
					EmptyTree)) =>
						Some(ArrStoreArrayInt(arrTerm.asInstanceOf[TermArrayInt],
								indexTerm.asInstanceOf[TermInt] ,
								valTerm.asInstanceOf[TermInt])) //TODO: remove hardcoded Int
			case _ => None
		}
	}

	object NoneOfExprTree {
		def unapply(aTree: Tree): Option[Unit] = aTree match {
			//null:  Literal(Constant(null))
			case Literal(Constant(lit)) if (lit == null) => Some()
			case This(_) => Some()
			//Nested inside the l: List[_] block to eliminate "type pattern unchecked" warning.
			case l: List[_] => l match {
				//sum _ (method value)
				//Math.sin _ (method value)
				case List(Block(_, _)) => Some()
				//{p: Int => p + 1} :: ((p: Int) => p.+(1)) (anonymous function)
				case List(Function(_, _), Apply(_, _)) => Some()
				case _ => None
			}
			//tuples (4, 5)
			case New(tpt) if (tpt.symbol.toString() == "class Tuple2" && tpt.symbol.owner.toString == "package scala") => Some()
			//new C(5)
			case New(_) => Some()
			//this.y: Select( This(""), "y")
			case Select(This(_), _) => Some()
			case _ => None
		}
	}

	object ConstOfExprTree {
		def unapply(aTree: Tree): Option[Const] = {
			def parseDouble(s: String) = try { Some(s.toDouble) } catch { case _ => None }
			def parseInt(s: String) = try { Some(s.toInt) } catch { case _ => None }

			def getConstType(str: String): PSType = str match {
				case "True" =>  PSBool
				case "False" =>  PSBool
				case "()" => PSUnit
				case _ if parseInt(str).isDefined => PSInt
				case _ if parseDouble(str).isDefined => PSReal
			}

			aTree match {
				//5:  Literal(Constant(5))
				case Literal(Constant(lit)) =>
					Some(ConstInt(lit.toString)) //TODO: remove hardcoded PSInt
				case _ =>
					None
			}

		}
	}
	object VarOfExprTree {
		def unapply(aTree: Tree): Option[Var] = {
			aTree match {
				//z: Ident("z")
				case i @ Ident(iden) =>
					getVar(iden.toString, i.symbol.tpe.toString)
				case _ => None
			}
		}
	}

	/** Extracts ArrSelect , Array length and FnApp*/
	object FnAppOfExprTree {
		def unapply(aTree: Tree): Option[Term] = {
			//writeln0((aTree.printNodes(global))
			aTree match {
				case arrlen @ Select(TermOfTree(Var(arrName)), NameDef("length"))
				if (arrlen.symbol.toString == "method length" &&
					arrlen.symbol.owner.toString == "class Array" &&
					arrlen.tpe.toLongString == "Int") => {
						Some(VarInt(arrName + ".length"))
				}
				//5 * z : Apply( Select( Literal(Constant(5)), "$times"),List( Ident("z")))
				//sum(2, z) :: ExpnTest.this.sum(2, z): will fail since 'This("ExpnTest")' is not an expression.
				case a @ Apply(Select(TermOfTree(term1), mname), TermsOfTrees(terms)) =>
					val aFnOpt = mkFn(mname.toString, term1::terms map {_.getType()}, a.tpe.toString)
					aFnOpt flatMap { aFn =>
						if ((aFn == Fn("apply", List(PSArrayInt, PSInt),PSInt)) &&
							term1.getType == PSArrayInt &&
							terms.length == 1 &&
							terms.head.getType == PSInt) //TODO: remove hardcoded PSInt
								Some(ArrSelectInt(term1.asInstanceOf[TermArrayInt], terms.head.asInstanceOf[TermInt]))
						else Some(FnAppInt(aFn, term1::terms))
					}
				//-z : Select(Ident("z"), "unary_$minus")	sym.tpe==>
				case unary @ Select(TermOfTree(term), mname) =>
					if ( mname.toString.startsWith("unary_")) {
						val aFnOpt = mkFn(mname.toString, List(term) map {_.getType()}, unary.tpe.toString.replaceAll("=>", ""))
						aFnOpt map { aFn =>
							FnApp.mkFnApp(aFn, List(term), aFn.tpe)
						}
					} else None
				case _ => None
			}
		}
	}

	//Not needed since FnAppOfExprTree also extracts the ArrSelect
	//object ArrSelectOfExprTree {
	//	def unapply(aTree: Tree): Option[ArrSelect] = {
	//		//writeln0((aTree.printNodes(global))
	//		aTree match {
	//			case _ => None
	//		}
	//	}
	//}
}

*/