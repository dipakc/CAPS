package progsynth.debug
import progsynth._
import progsynth.types._
import progsynth.types.Types._

import progsynth.dsl.QQ._
import progsynth.debug.PSDbg._
import progsynth.utils.{PSUtils=>psu}
import scala.sys.process._
import progsynth.ProgSynth._

trait FormulaParseTreeViewer{ self: FOLFormulaRich =>
	private object NodeIdCtr extends Counter
	private object saveDotJpgObj extends Counter

    ///////////////////////////////////////////////////////////////////////////
	/**Convert the formula to graphviz dot format for visualization*/
	def toDotString() = {
		val abc = mkDotFormula(self.folFormula)._1.mkString("\n")
		List("", "digraph g{", abc , "}", "").mkString("\n")
	}
	/**Convert the predicate to graphviz dot format for visualization*/
	private def toDotString(pred: Pred) = {
		val abc = mkDotPred(pred)._1.mkString("\n")
		List("", "digraph g{", abc , "}", "").mkString("\n")
	}
	/**Convert the term to graphviz dot format for visualization*/
	private def toDotString(term: Term) = {
		val abc = mkDotTerm(term)._1.mkString("\n")
		List("", "digraph g{", abc , "}", "").mkString("\n")
	}

	/** Save the formula parse tree to a jpg file and return the file path */
	//TODO: remove hard coded paths
	//TODO: remove hard coded path

	def saveDotJpg(): String = {
	    val dirName = """d:\tmp\abc_"""
	    val dotExe = """D:\ProgramFilesx86\Graphviz2.28\bin\dot.exe"""
	    val dotStr = self.folFormula.toDotString()
	    val num = saveDotJpgObj.getCnt
	    val dotFile = dirName + num + ".dot"
	    val jpgFile = dotFile + ".jpg"
	    psu.overwriteFile(dotFile, dotStr)
	    Seq(dotExe, "-Tjpg", dotFile, "-O" ).!
	    jpgFile
	}
	///////////////////////////////////////////////////////////////////////////
	private def getLabelPred(p: Pred) = p.r.replace("$", "_")

	private def getLabelFormula(f: FOLFormula): String = {
		(f match {
			case True1() => "True"
			case False1() => "False"
			case Atom(pred) => throw new RuntimeException("Should not have been called")
			case Not(f) => "Not"
			case And(f1, f2) => "And"
			case Or(f1, f2) => "Or"
			case Impl(f1, f2) => "Impl"
			case Iff(f1, f2) => "Iff"
			case Forall(v, f) => "Forall " + v
			case Exists(v, f) => "Exists " + v
			case Unknown() => "Unknown"
		}) +
		<a>  ({f.fid})</a>.text
	}

	private def getLabelTerm(t: Term): String = {
		t match {
			case Var(v) => v
			case Const(name) => name
			case FnApp(f, _) => f.name
			case ArrSelect(_, _) => "Select"
			case ArrStore(_, _, _) => "Store"
		}
	}
	///////////////////////////////////////////////////////////////////////////
	private def mkDotFormula(formula: FOLFormula): (List[String], String) = {
		formula match {
			case Atom(aPred) =>
			    //mkDotPre(aPred)
			    createNodeStr(aPred.pprint + <a>  ({formula.fid})</a>.text, "p")
			case _ =>
				val (nodeStr, nodeId) = createNodeStr(getLabelFormula(formula), "f");
				val childsStrsList =
					for { child <- formula.childs() } yield {
						val (childDotStrs, childRootId) = mkDotFormula(child);
						childDotStrs ++ createEdge(nodeId, childRootId)
					}
				val childsStrs = childsStrsList.flatten
				(nodeStr ++ childsStrs, nodeId)
		}
	}

	private def mkDotPred(aPred: Pred): (List[String], String) = {
		val (predStr, predId) = createNodeStr(aPred.r.toString, "p")
		val childsStrsList =
			for { child <- aPred.ts } yield {
				val (childDotStrs, childRootId) = mkDotTermCollapsed(child);
				childDotStrs ++ createEdge(predId, childRootId)
			}
		val childsStrs = childsStrsList.flatten
		(predStr ++ childsStrs, predId)

	}

	private def mkDotPredCollapsed(aPred: Pred): (List[String], String) = {
		createNodeStr(aPred.pprint, "p")
	}

	private def mkDotTerm(aTerm: Term): (List[String], String) = {
		val (parentStr, parentId) = createNodeStr(getLabelTerm(aTerm), "t")
		val childsStrsList =
			for { child <- aTerm.childs() } yield {
				val (childDotStrs, childRootId) = mkDotTerm(child);
				childDotStrs ++ createEdge(parentId, childRootId)
			}
			val childsStrs = childsStrsList.flatten
		(parentStr ++ childsStrs, parentId)
	}

	/** Returns single node string and node ID for the term*/
	private def mkDotTermCollapsed(aTerm: Term): (List[String], String) = {
	    createNodeStr(aTerm.pprint, "t")
	}

	/////////////////////////////////////////////////////////////////////////////
	/**fpt: formula, predicate or term */
	private def createNodeStr(nodeName: String, fpt: String): (List[String], String) = {
		val nodeId = "node_" + NodeIdCtr.getCnt
		val label = "label=" + "\"" + nodeName + "\""
		val nodeshape = if (fpt != "f" ) "shape=oval" else "shape=box"
		val colorshape = if (fpt == "p") "color=red" else if (fpt == "t") "color=blue" else "color=black"
		val optStr = "[" + List(label, nodeshape, colorshape).mkString(", ") + "]"
		(List(nodeId + " " + optStr + ";"), nodeId)
	}

	private def createEdge(n1: String, n2: String): List[String] = {
		List(n1 + " -> " + n2 + ";")
	}
}

object FormulaParseTreeViewerTestApp extends App {
	val x = VarInt("x")
	val y = VarInt("y")
	val fn = Fn("fun", List(PSInt), PSInt)
	val funApp = FnAppInt(fn, List(x))
	val funApp2 = FnAppInt(fn, List(funApp))
	//val f: FOLFormula = (funApp2 eqeq x) /\ (funApp eqeq y) /\ Not( x eqeq y)
//	val f: FOLFormula = Not(funApp2 eqeq x) \/ Not(funApp eqeq y) \/ (x eqeq y)
//	logln(f.toDotString())
}
