package expt;
/*
//Source: http://www.scala-lang.org/node/140
//Add scala compiler library in build path

package localhost.expt

import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.collection.mutable.ListBuffer
import ProgramTypes._
import ProgramPrinter._
import localhost._
import Using._
import Formula._
import FormulaUtils._

class ProgSynthPlugin2(val global: Global)(val progSynthComponent: ProgSynthComponent21 = new ProgSynthComponent21(global)) extends Plugin {
	//import global._

	val name = "ProgSynth"
	val description = "Program Synthesis"
	val components = List[PluginComponent](progSynthComponent)

	Logger3.addElem(<info>In ProgSynthPlugin constructor</info>)
}

class ProgSynthComponent21(val global: Global) extends PluginComponent with AnnProgramExtractor {
	//val global: ProgSynthPlugin.this.global.type = ProgSynthPlugin.this.global
	Logger3.addElem(<info>In Component constructor</info>)
	val runsAfter = List[String]("refchecks");
	val phaseName = "ProgSynth"
	def newPhase(_prev: Phase) = new ProgSynthPhase(_prev)
	Logger3.addElem(<info>Created phase ProgSynthPhase. Will run after "refchecks"</info>)

	class ProgSynthPhase(prev: Phase) extends StdPhase(prev) {
		override def name = "ProgSynth"
		Logger3.addElem(<info>In ProgSynthPhase constructor</info>)

		def apply(unit: global.CompilationUnit) = {
		xmlusing( new Logger3.XMLLogScope("ProgSynthPhase_apply")) { _ =>
			Logger3.addElem(<arg name="compilation_unit">{unit}</arg>)
			getMaxExtractor.traverse(unit.body)
			val getMaxFunctionTree = getMaxExtractor.theProgram
			Logger3.addElem(<info>Extracted the max program tree</info>)
			writeln0("dipakc111")
			writeln0(getMaxFunctionTree)
			val maxAnnProgram = extractAnnProgramFromMethodDef(getMaxFunctionTree)
		}}// ensuring (res => { Logger2.dumpXml(); true})

		object getMaxExtractor extends global.Traverser {
			var theProgram:global.Tree = null
			override def traverse(t: global.Tree): Unit = {
				t match {
					case global.DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
						if (name.toString == "getMax"){
							theProgram = t
						}
					case _ =>
						super.traverse(t)
				}
			}
		}

	}
}


*/