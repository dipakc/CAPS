package tests.testutils
/*
import progsynth.plugin.ProgSynthPlugin
import scala.tools.nsc.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.io.VirtualDirectory
import java.io.File
import scalaz.Lens
import progsynth.types.Invariant

trait PSPluginTester {
	private var m_compiler: Option[scala.tools.nsc.Global] = None
	private var m_run: Option[scala.tools.nsc.Global#Run] = None

	def runCompiler(testFiles: List[String]) = {
	  	val cpEntries: List[String] =
						"""extlib\scala-library-2.9.1.jar""" ::
						"""extlib\scala-compiler-2.9.1.jar""" ::
						"""extlib\kiama_2.9.1-1.2.0.jar""" ::
						"""target\scala-2.9.1\progsynth_2.9.1-0.0.1.jar""" ::
						Nil

		val compilerArgStr = """ -Xplugin:target\scala-2.9.1\progsynth_2.9.1-0.0.1.jar""" +
						""" -Xplugin:extlib\scalaz3-3.2.b_scala.jar""" +
						""" -Xplugin:extlib\scalaz3-3.2.b_java.jar""" +
						""" -Xplugin:extlib\scalaz-core_2.9.1-6.0.4.jar""" +
						""" -Xplugin:extlib\kiama_2.9.1-1.2.0.jar""" +
						""" -classpath """ + cpEntries.mkString(File.pathSeparator) +
						""" -Ystop-after:ProgSynth """

		val settings = new Settings
		settings.processArgumentString(compilerArgStr)
		settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

		val reporter = new ConsoleReporter(settings)
		val compiler = new Global(settings, reporter)
		val aRun = new compiler.Run()
		val sources = testFiles map {compiler getSourceFile _}
		aRun.compileSources(sources)
		m_compiler = Some(compiler)
		m_run = Some(aRun)
		(m_compiler, m_run)
	}
}
*/