package progsynth.printers
import progsynth.utils.PSUtils
import progsynth.config.AppConfig
import progsynth.types._
import progsynth.types.Types._


//object AnnCodeGraphWriter extends HierarchicalDirectedGraph {
//
//	def saveAsGraph(annProg: ProgramAnn): Unit = {
//		getDotStr(getProgGraph(annProg))
//	}
//
////	case class ProgGraph(var start: Node, var end: Node,
////						var nodes: List[Node], var edges:List[Edge], var lbl: Option[String])
////	case class Node(var uid: Int, var f: FOLFormula, var childs: List[Node])
////	case class Edge(var start: Node, var subProgGraphs: List[ProgGraph], var end: Node, var innerEnd: Option[Node])
//
//	def getDotStr(progGraph: ProgGraph): String = {
//		var retVal = ""
//		progGraph.nodes foreach { node =>
//			retVal += getDotStr(node)
//		}
//
//		progGraph.edges foreach {edge =>
//			retVal += getDotStr(edge)
//		}
//		retVal
//	}
//
//	def getDotStr(aNode: Node): String = {
//		//print the node
//		""
//	}
//
//	def getDotStr(anEdge: Edge): String = {
//		//print the edge
//		""
//	}
//
//	case class GrdSubGraph(guard: FOLFormula, cmdGraph: ProgGraph)
//	def getProgGraph(annProg: ProgramAnn): ProgGraph =
//		{
//			var nodes: List[Node] = Nil
//			var edges: List[Edge] = Nil
//			def mkNode(uid: Int, t: TermBool, childs: List[Node]) : Node = {
//				val aNode = Node(uid, t, childs)
//				nodes = nodes ::: List(aNode)
//				aNode
//			}
//
//			def mkEdge(start: Node, subProgGraphs: List[ProgGraph], end: Node, innerEnd: Option[Node]) : Edge = {
//				val anEdge= Edge(start, subProgGraphs, end, innerEnd)
//				edges = edges ::: List(anEdge)
//				anEdge
//			}
//
//			annProg match {
//				case funProg @ FunctionProg(name, params, retVar, annProg, globalInvs) =>
//					getProgGraph(annProg)
//				case ifProg @ IfProg(grdcmds) =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = ifProg.pre.term, childs = Nil )
//					val postNode = mkNode(uid = NodeIdGen.getId, t = ifProg.post.term, childs = Nil )
//					grdcmds foreach { grdcmd =>
//						val grdNode = mkNode(uid = NodeIdGen.getId, t= grdcmd.guard, childs = Nil)
//						preNode.childs = preNode.childs::: List(grdNode)
//						val cmdGraph = getProgGraph(grdcmd.cmd)
//						mkEdge(start = grdNode, subProgGraphs = List(cmdGraph), end = postNode, None)
//						//postNode.childs  = postNode.childs ::: List(cmdGraph.end)
//					}
//					ProgGraph(start = preNode, end = postNode, nodes = nodes, edges = edges, lbl = None)
//				case whileProg @ WhileProg(_, grdcmds) =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = whileProg.pre.term, childs = Nil )
//					val loopInvNode = mkNode(uid = NodeIdGen.getId,
//											t = whileProg.loopInv.get,
//											childs = Nil) //TODO: get used without checking.
//					val preToLoopInv = mkEdge(start = preNode, subProgGraphs = Nil, end = loopInvNode, innerEnd = None)
//
//					grdcmds foreach { grdcmd =>
//						val grdNode = mkNode(uid = NodeIdGen.getId, t= grdcmd.guard, childs = Nil)
//						loopInvNode.childs = loopInvNode.childs ::: List(grdNode)
//						val cmdGraph = getProgGraph(grdcmd.cmd)
//						val grdToCmd = mkEdge(grdNode, List(cmdGraph), loopInvNode, None)
//					}
//					val exitF= TermBool.mkConjunct(grdcmds map {gc => gc.guard.unary_!})
//					val exitGrdNode = mkNode(uid = NodeIdGen.getId, t= exitF, childs = Nil)
//					val postNode = mkNode(uid = NodeIdGen.getId, t = whileProg.post.term, childs = Nil)
//					val exitEdge = mkEdge(start = exitGrdNode, subProgGraphs = Nil, end = postNode, None)
//
//					ProgGraph(start = preNode, end = postNode, nodes = nodes, edges = edges, lbl = None)
//				case compProg @ Composition(programs) =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = compProg.pre.term, childs = Nil )
//					val postNode = mkNode(uid = NodeIdGen.getId, t = compProg.post.term, childs = Nil )
//					val progGraphs = compProg.programs map getProgGraph
//					//compProg.programs map getProgGraph
//					val aEdge = mkEdge(start = preNode, subProgGraphs = progGraphs, end = postNode, None)
//					ProgGraph(start = preNode, end = postNode, nodes = nodes, edges = edges, lbl = None)
//				case asgn @ Assignment((_, rhs)::Nil) =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = asgn.pre.term, childs = Nil )
//					val postNode = mkNode(uid = NodeIdGen.getId, t = asgn.post.term, childs = Nil )
//					val edge =  mkEdge(preNode, Nil, postNode, None)
//					ProgGraph(start = preNode, end = postNode, nodes = nodes, edges = edges, lbl = Some(asgn.toString()))
//				case Assignment(_) => throw new RuntimeException("getProgGraph of simultaneous assignments not implemented")
//				case SkipProg() =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = annProg.pre.term, childs = Nil )
//					val postNode = mkNode(uid = NodeIdGen.getId, t = annProg.post.term, childs = Nil )
//					val edge = mkEdge(preNode, Nil, postNode, None)
//					ProgGraph(preNode, postNode, nodes, edges, None)
//				case UnknownProg(id) =>
//					val preNode = mkNode(uid = NodeIdGen.getId, t = annProg.pre.term, childs = Nil )
//					val postNode = mkNode(uid = NodeIdGen.getId, t = annProg.post.term, childs = Nil )
//					val edge = mkEdge(preNode, Nil, postNode, None)
//					ProgGraph(preNode, postNode, nodes, edges, Some(id.toString))
//				//case ExprProg(_) => ""
//				//case Identifier(_, _) =>
//				//case LitConstant(_) =>
//			}
//		}
//}
//
//trait HierarchicalDirectedGraph {
//	case class ProgGraph(var start: Node, var end: Node,
//						var nodes: List[Node], var edges:List[Edge], var lbl: Option[String])
//	case class Node(var uid: Int, var t: TermBool, var childs: List[Node])
//	case class Edge(var start: Node, var subProgGraphs: List[ProgGraph], var end: Node, var innerEnd: Option[Node])
//}
//
//object NodeIdGen {
//	var id = 0
//	def getId = { id = id + 1; id }
//}