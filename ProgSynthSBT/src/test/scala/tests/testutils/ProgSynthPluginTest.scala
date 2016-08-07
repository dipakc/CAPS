package tests.testutils
/*
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.io.VirtualFile
import scala.tools.nsc.Global
import scala.tools.nsc.Settings
import scala.collection.mutable.HashMap
import progsynth.plugin.ProgSynthPlugin
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.io.VirtualDirectory
import scala.util.matching.Regex
import java.io.File
import java.net.URLClassLoader

trait ProgSynthPluginTest extends PSPluginComponent with PSRunCompiler with PSTreeExtractor{
	runCompiler()
}

trait PSPluginComponent {

	def argStr = "" +
				//" -Xshow-phases" +
				//" -Xprint:refchecks" +
			    //" -Yshow-trees" +
				" -Ystop-after:refchecks"
				//""" -classpath """ + classpath.mkString(";")
	val psc = {
		val settings = new Settings
		settings.processArgumentString(argStr)
		settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

		// Explicitly add scala library and compiler to classpath since test run from sbt test env
		// does not add these values to classpath.
		// Use "prepend" to prepend the jars to javabootclasspath.
		// This is necessary since eclipse junit test run adds the scala library and compiler in
		// eclipse distribution to the javabootclasspath.
		val addScalaJarsToClassPath = true
		val prepend = true
		val classPathEntries: List[String] =
			"""extlib\scala-library-2.9.1.jar""" ::
			"""extlib\scala-compiler-2.9.1.jar""" ::
			"""extlib\kiama_2.9.1-1.2.0.jar""" ::
			"""target\scala-2.9.1\progsynth_2.9.1-0.0.1.jar""" :: Nil
		if (addScalaJarsToClassPath) {
			if (prepend)
				classPathEntries.foreach( settings.javabootclasspath.prepend(_))
			else
				settings.classpath.tryToSet(List(classPathEntries.mkString(File.pathSeparator)))
		}

		val reporter = new ConsoleReporter(settings)
		val compiler0 = new Global(settings, reporter) {
			val progSynthPlugin = new ProgSynthPlugin(this)
			var progSynthComponent = progSynthPlugin.progSynthComponent
			override protected def computeInternalPhases() {
				super.computeInternalPhases
				phasesSet += progSynthComponent
			}
		}
		//writeln0("ClassPath:" + compiler0.classPath)
		compiler0.progSynthPlugin.progSynthComponent
	}

	//val compiler = psc.global
}

trait PSRunCompiler { self:PSPluginComponent =>
	def addSourceFiles() /* = {
		addSourceFiles(List("src\tests\inputprog\Max.scala"))
	}*/

	private var usrfs: List[String] = Nil
	def addSourceFiles(srcs: List[String]) = {
		usrfs = srcs
	}
	private val aRun = new psc.global.Run()
	protected def getUnits() = aRun.units
	protected def runCompiler() = {
		addSourceFiles()
		val stdfs = List()
//			"""src\main\scala\progsynth\spec\StaticAssertions.scala""",
//			"""src\main\scala\progsynth\types\ProgramTypes.scala""",
//			"""src\main\scala\progsynth\types\FOLFormula.scala""",
//			"""src\main\scala\progsynth\printers\ProgramPPrinter.scala""")
		val allfs = stdfs ::: usrfs
		val sources = allfs map psc.global.getSourceFile
		aRun.compileSources(sources)
	}
}

trait PSTreeExtractor { self: PSPluginComponent with PSRunCompiler =>
	def getDefTree(fname: String): Option[psc.global.Tree] = {
		//val dbgVar = getUnits() map ( _.toString )
		//dbgVar foreach writeln
		val defTree = (getUnits() filter ( _.toString == fname)).next().body
		//todo: what if there is no match.
		//writeln0(defTree); writeln0("*************************")
		Some(defTree)
	}

	def getDefTree(fname: String, defName: String): Option[psc.global.Tree] = {
		val fTreeO = getDefTree(fname)
		//writeln0(fTree); writeln0("*************************")
		def getSubTreeFromName(inputTree: psc.global.Tree, mname: String): Option[psc.global.Tree] = {
			var theProgram: Option[psc.global.Tree] = None
			object methodTreeExtractor extends psc.global.Traverser {
				override def traverse(t: psc.global.Tree): Unit = {
					//writeln0(t); writeln0("*************************")
					t match {
						case psc.global.DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
							if (name.toString == mname) {
								theProgram = Some(t)
							}
						case _ =>
							super.traverse(t)
					}
				}
			}
			methodTreeExtractor.traverse(inputTree)
			theProgram
		}
		fTreeO.flatMap(getSubTreeFromName(_, defName))
	}
}
*/