//package progsynth.provers
//
//import progsynth.types.Var
//import progsynth.types.TermBool
//
//case class Why3Tpe(name: String) {
//	def toText() = name
//}
//////////////////////////////////
//abstract class Why3Stmt {
//	def toText(): List[String]
//}
//case class UseImport(theory: String) extends Why3Stmt {
//	def toText() = List("use", "import", theory)
//}
//case class Use(theory: String) extends Why3Stmt {
//	def toText() = List("use", theory)
//}
//
//case class ConstDecl(name: String, tpe: Why3Tpe) extends Why3Stmt {
//	def toText() = List("constant", name, ": ", tpe.toText)
//}
//
//case class Function(name: String, args: List[(String, Why3Tpe)], retType: Why3Tpe, body: Option[String]) extends Why3Stmt {
//	def toText() = {
//		val argss = (args.map{arg => s"( ${arg._1} : ${arg._2.toText} )"})
//		val retStr = retType.toText()
//		val bodyTxt = body.map{b=> s"\t = $b"} getOrElse ""
//
//		List("function",  name) ++  argss ++ List(": ", retStr, "\n", bodyTxt)
//	}
//}
//case class Axiom(name: String, body: List[String]) extends Why3Stmt{
//	def toText() = {
//		List("axiom", name, ": ", "\n\t") ++ body
//	}
//}
//case class Goal(name: String, body: List[String]) extends Why3Stmt{
//	def toText() = {
//		List("goal", name, ": ", "\n\t") ++ body
//	}
//}
//
//case class StrStmt(content: List[String]) extends Why3Stmt {
//	def toText() = {
//		content
//	}
//}
//
//case class BlankStmt() extends Why3Stmt {
//	def toText() = {
//		List("\n")
//	}
//}
//
//case class MinQuantEncodingStmt(fname: String, rf: Why3Stmt, tf: Why3Stmt, minqFn: Why3Stmt, axioms: List[Why3Stmt]) extends Why3Stmt {
//	def toText() = {
//		val sep = BlankStmt()
//		val stmts = List(rf, tf, minqFn) ++ axioms
//		//stmts.map(_.toText()).mkString("\n\n")
//		stmts.flatMap(_.toText() ++ List("\n"))
//	}
//}
////////////////////////////////
//class Why3Theory {
//	var stmts: List[Why3Stmt] = Nil
//	def toText(): String = {
//		val stmtList: List[String] = stmts.map{_.toText}.map{_.mkString(" ")}
//
//		("theory Test\n" +: stmtList :+ "\nend").mkString("\n")
//	}
//
//	def addStmts(s: Why3Stmt) = {
//		stmts = stmts :+ s
//	}
//	def addStmts(s: List[Why3Stmt]) = {
//		stmts = stmts ++ s
//	}
//	def addSep() = {
//		addStmts(BlankStmt())
//	}
//
//}
//
//trait Why3TheoryUtils extends TypeUtils {
//	def mkFunction(name: String, vars: List[Var], retType: Why3Tpe, body: String) =
//		Function(name, vars.map(varToTypedWhy3Tpe(_)), retType, Some(body))
//
//	def mkFunctionDecl(name: String, vars: List[Var], retType: Why3Tpe) =
//		Function(name, vars.map(varToTypedWhy3Tpe(_)), retType, None)
//
//}