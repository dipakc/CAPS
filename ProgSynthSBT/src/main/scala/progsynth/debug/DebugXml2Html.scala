package progsynth.debug

import progsynth.config.AppConfig
import scala.xml._

//Reads the output debug xml and converts it to html
//http://www.ibm.com/developerworks/library/x-scalaxml/

object DebugXml2Html {
	//val dbgXml: Elem = XML.load(AppConfig.logFile)

	def transform1(ns: Iterable[Node]): Seq[Node] = {
		val zs = new NodeBuffer();
		for (z <- ns) {
			zs &+ transformInvariant(z)
		}
		zs
	}

	def transform2(ns: Iterable[Node])(f: Node => Iterable[Node]): Seq[Node] = {
		val zs = new NodeBuffer();
		for (z <- ns) {
			zs &+ f(z)
		}
		zs
	}

	def transformInvariant(n: Node): Iterable[Node] = {
		n match {
			case x @ <Invariant>
					<loc>{ _* }</loc>
					<formula>{ _* }</formula>
					<retVar>{ _* }</retVar>
				</Invariant>
					=>
					<Invariant>
					<loc>{ x \ "loc" }</loc>
					<formula>{ transform2(x \ "formula")(transformFOLFormula) }</formula>
					<retVar>{ x \ "retVar" }</retVar>
				</Invariant>
			case _ => <NoMatch> </NoMatch>
			//case x @ <Invariant>{ _* }</Invariant> => <Invariant> { transform2(x \ "_") } </Invariant>
		}
	}

	def transformProgramAnn(n: Node): Iterable[Node] = {
		n match {
			case x @ 	<ProgramAnn>
							<id>{_*}</id>
							<pre>{_*}</pre>
							<post>{_*}</post>
							<program>{_*}</program>
						</ProgramAnn>
				=>
					<ProgramAnn>
							<id>{ x \ "id" }</id>
							<pre>{ transform2(x \ "pre")(transformInvariant) }</pre>
							<post>{ transform2(x \ "post")(transformInvariant) }</post>
							<program>{transform2(x \ "program")(transformProgram) } </program>
						</ProgramAnn>
			case _ => <nomatch></nomatch>
		}
	}

	//case class GuardedCmd(guard: FOLFormula, cmd: ProgramAnn)
	def transformGuardedCmd(n: Node): Iterable[Node] = n match {
		case x @ 	<GuardedCmd>
							<guard>{_*}</guard>
							<cmd>{_*}</cmd>
						</GuardedCmd>
					=>	<GuardedCmd>
							<guard>{transform2(x \ "guard")(transformFOLFormula)}</guard>
							<cmd>{transform2(x \ "cmd")(transformProgramAnn)}</cmd>
						</GuardedCmd>
		case _ => <nomatch></nomatch>
	}

	def transformProgram(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformFunctionProg(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}

	//case class IfProg(grdcmds: List[GuardedCmd]) extends Program
	def transformIfProg(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformWhileProg(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformComposition(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformAssignment(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformSkipProg(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformUnknownProg(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
	def transformIdentifier(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}

	def transformLitConstant(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}

	def transformFOLFormula(n: Node): Iterable[Node] = n match {
		case _ => <nomatch></nomatch>
	}
}
