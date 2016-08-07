//Source: http://www.scala-lang.org/node/140
//Add scala compiler library in build path

package progsynth.plugin
/*
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.Global
import scala.tools.nsc.Phase
import scala.collection.mutable.ListBuffer
import progsynth.PSPredef._
import progsynth._
import types._
import progsynth.utils.folformulautils.BoolToFormula._
import extractors.AnnProgramExtractor
import logger.XMLLogWriter
import progsynth.config.AppConfig

class ProgSynthPlugin(val global: Global) extends Plugin {
	implicit val iglobal = global
	import global._

	val name = "ProgSynth"
	val description = "Program Synthesis"

	val progSynthComponent = new ProgSynthComponent()
	val components = List[PluginComponent](progSynthComponent)

	class ProgSynthComponent extends PluginComponent with ProgSynthMain with AnnProgramExtractor
	{
		val global: ProgSynthPlugin.this.global.type = ProgSynthPlugin.this.global
		val runsAfter = List[String]("refchecks");
		val phaseName = ProgSynthPlugin.this.name
		def newPhase(_prev: Phase) = new ProgSynthPhase(_prev)

		class ProgSynthPhase(prev: Phase) extends StdPhase(prev) {
			override def name = ProgSynthPlugin.this.name

			ProgramAnn.reset() //Reset the ProgramAnn Object. (Object is shared across tests in sbt test)

			//"super.run" will eventually call "apply" for each srcfile
			override def run() = {
				//writeln0("run called") //TODO: not visible client eclipse console
				psStart()
				super.run()
				psEnd()
			}

			def apply(unit: CompilationUnit) = {
				//writeln0("apply called")
				psMain(unit.body)
			}
		}
	}
}

*/