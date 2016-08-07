package progsynth.extractors
/*
//import scala.reflect.generic.Universe
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.plugins.PluginComponent
import progsynth.PSPredef._
import progsynth.types._
import progsynth.types.Types._

import progsynth.types.PSType._
//import scala.reflect.generic.Names
import scala.xml.Elem
import progsynth.utils.folformulautils.BoolToFormula._
import progsynth.logger.CaseStmtLogger._
import progsynth.logger.FunLogger._
import scala.collection.mutable.HashMap

trait AnnProgramExtractor extends ExtractorsOnAST with ExtractorsOnSpecTree{ self:PluginComponent =>
	implicit override val iglobal = global
	import global._

	def extractAnnProgramFromMethodDef(methodDef: Tree): Option[ProgramAnn] =
	funlog("extractAnnProgramFromMethodDef", methodDef){ methodDef =>
		val st = SpecTree(None, Some(methodDef), None)
		st match {
			case AnnProgramOfSpecTree(pa) =>
				Some(removeSingletonCompositions(pa))
			case _ =>
				None
		}
	}

	def removeSingletonCompositions(pa: ProgramAnn): ProgramAnn = {
		pa.applyRec {
			case Composition(programs) if programs.length == 1 =>
				removeSingletonCompositions(programs.head)
		}
	}

	object AnnProgramsOfSpecTrees {
		def unapply(stList: List[SpecTree]): Option[List[ProgramAnn]] =
		funlog("AnnProgramsOfSpecTrees::unapply", stList){ stList =>
			stList match {
				case Nil =>
					Some(Nil)
				case AnnProgramOfSpecTree(pa) :: AnnProgramsOfSpecTrees(pas) =>
					Some(pa :: pas)
				case _ =>
					None
			}
		}
	}

	object AnnProgramOfSpecTree {
		var cnt = 0
		def unapply(st: SpecTree): Option[ProgramAnn] =
		funlog("AnnProgramOfSpecTree::unapply", st){ st =>
			val retVal =
				st match {
					case _ if casestart("NoTree") => None
					case SpecTree(Some(_), None, Some(_)) => //TODO: handle None, None, None?
						val pa = SkipProg()
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("UnknownFragmentSpecTree") => None
					case UnknownFragmentSpecTree(id) =>
						val pa = UnknownProg(id)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("WhileRetExprTree") => None
					case WhileRetExprTree(_) =>
						val pa = SkipProg()
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("IfSpecTree") => None
					case IfSpecTree(cond, thenST, elseST) =>
						(thenST, elseST) match {
							case (AnnProgramOfSpecTree(thenPa), AnnProgramOfSpecTree(elsePa)) =>
								val pa = IfProg(GuardedCmd(cond, thenPa)::GuardedCmd(Not(cond), elsePa)::Nil)
								pa.setPrePost(st.pre.get, st.post.get)
								Some(pa)
							case _ => None
						}
					case _ if casemiddle("WhileSpecTree") => None
					case WhileSpecTree(loopInvFOpt, cond, bodyST) =>
						bodyST match {
							case AnnProgramOfSpecTree(bodyPa) =>
								val pa = WhileProg(loopInvFOpt, GuardedCmd(cond, bodyPa)::Nil)
								val whilePre =
									if ( st.pre.get.formula.isUnknown && loopInvFOpt.isDefined)
										Invariant(None, loopInvFOpt.get, None)
									else {
										st.pre.get
									}
								pa.setPrePost(whilePre, st.post.get)
								Some(pa)
							case _ => None
						}
					case _ if casemiddle("CompositionSpecTreeWithSpec") => None
					case CompositionSpecTreeWithSpec(AnnProgramsOfSpecTrees(paList)) =>
						val pa = Composition(paList)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("CompositionSpecTree") => None
					case CompositionSpecTree(compSTs) =>
						compSTs match {
							case AnnProgramsOfSpecTrees(paList) =>
								val pa = Composition(paList)
								pa.setPrePost(st.pre.get, st.post.get)
								Some(pa)
							case _ => None
						}
					case _ if casemiddle("ValDefSpecTree") => None
					case ValDefSpecTree(lhs, isVariable, AnnProgramOfSpecTree(parhs)) =>
						val pa = if(isVariable) VarDefProg(lhs, parhs) else ValDefProg(lhs, parhs)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("AssignmentSpecTree") => None
					case AssignmentSpecTree(lhs, AnnProgramOfSpecTree(parhs)) =>
						val pa = Assignment((lhs, parhs) :: Nil)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("FunctionSpecTree") => None
					case FunctionSpecTree(fname, params, retType, funSpecTree) =>
						funSpecTree match {
							case AnnProgramOfSpecTree(pa)  =>
								//Some(pa)
								Some(FunctionProg(fname, params, retType, pa))
							case _ => None
						}
					case _ if casemiddle("ExprSpecTree") => None
					case ExprSpecTree(aTerm) =>
						val pa = ExprProg(aTerm)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("IdentifierTree") => None
					case IdentifierSpecTree(idenName, idenTpeStr) =>
						getPSType(idenTpeStr) map { idenTpe =>
							val pa = Identifier(idenName, idenTpe)
							pa.setPrePost(st.pre.get, st.post.get)
							pa
						}
					case _ if casemiddle("ConstantTree") => None
					case ConstantSpecTree(constName) =>
						val pa = LitConstant(constName)
						pa.setPrePost(st.pre.get, st.post.get)
						Some(pa)
					case _ if casemiddle("DefaultCase") => None
					case _ =>
						None
				}
				caseend()
				retVal
		}
	}
}
*/