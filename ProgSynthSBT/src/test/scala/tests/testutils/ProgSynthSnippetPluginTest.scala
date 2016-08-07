package tests.utils
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

trait ProgSynthSnippetPluginTest {
	val settings = new Settings
	val argStr = "" + /*" -Xshow-phases" +  " -Xprint:refchecks" +
		" -Yshow-trees" + */ " -Ystop-after:refchecks"
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
		"""target\scala-2.9.1\progsynth_2.9.1-0.0.1.jar""" :: Nil
	if (addScalaJarsToClassPath) {
		if (prepend)
			classPathEntries.foreach( settings.javabootclasspath.prepend(_))
		else
			settings.classpath.tryToSet(List(classPathEntries.mkString(File.pathSeparator)))
	}
	val reporter = new ConsoleReporter(settings)
	val interCompiler = new scala.tools.nsc.interactive.Global(settings, reporter) //TODO: is this needed?
	val compiler0 = new Global(settings, reporter) {
		val progSynthPlugin = new ProgSynthPlugin(this)
		var progSynthComponent = progSynthPlugin.progSynthComponent

		override protected def computeInternalPhases() {
			super.computeInternalPhases
			phasesSet += progSynthComponent
		}
	}

	val psc = compiler0.progSynthPlugin.progSynthComponent
	val compiler = psc.global
	////////////////////////////////////
	val snippetMap: HashMap[String, String] = new HashMap[String, String]
	def addSnippet(name: String, snippet: String) = {
		assert(snippet.trim.startsWith("def " + name + "("))
		snippetMap += name -> snippet
	}
	def populateSnippetMap() //abstract

	val aRun = new compiler.Run()
	var testTree:this.compiler.Tree = null
	def runCompiler() = {
		populateSnippetMap()
		//snippetMap += "testMethod" -> """def testMethod(x: Int) = sAssert(x == x)"""
		def getSources() = {
			val sources0 = {
				val filenames: List[String] = List()
				//"""src\main\scala\progsynth\spec\StaticAssertions.scala""",
				//"""src\main\scala\progsynth\types\Invariant.scala""",
				//"""src\main\scala\progsynth\types\Pred.scala""",
				//"""src\main\scala\progsynth\types\Term.scala""",
				//"""src\main\scala\progsynth\types\FOLFormula.scala""",
				//"""src\main\scala\progsynth\types\FormulaApplyRec.scala""",
				//"""src\main\scala\progsynth\types\FormulaMonad.scala""",
				//"""src\main\scala\progsynth\types\FormulaOperators.scala""",
				//"""src\main\scala\progsynth\types\FormulaToString.scala""",
				//"""src\main\scala\progsynth\types\package.scala""",
				//"""src\main\scala\progsynth\\utils\folformulautils\BoolToFormula.scala""")
				filenames map compiler.getSourceFile
			}

			val batchSrc = {
				//val code = "object Foo extends Application { writeln0(42 / 2) }"
				val snippetList = for ((name, snippet) <- snippetMap) yield { snippet }
				val snippetStr = snippetList.mkString("\n")
				val code =
					"""
							package tests.testutils
							import progsynth.spec.StaticAssertions._
							import progsynth.types._
							import progsynth.types.Types._
							import progsynth.utils.folformulautils.BoolToFormula._
							object testObj {""" + snippetStr + "}"
				val name = "<testfile>"

				def createBatchSourceFile(fname: String, fcode: Array[Char]) = {
					//new BatchSourceFile(fname, fcode)
					//dipakc: hack: set the container to self.
					//container is used to rank the files in coreClassesFirst in Global
					val virtFile = new VirtualFile(fname) {
						override val container = new VirtualFile("test")
					}
					new BatchSourceFile(virtFile, fcode)
				}
				createBatchSourceFile(name, code.toCharArray)
			}

			sources0 ::: List(batchSrc)
		}

		val sources = getSources() //sources
		aRun.compileSources(sources)
		testTree = (aRun.units filter (_.toString == "<testfile>")).next().body
	}

	private def getSubTreeFromName(inputTree: compiler.Tree, mname: String) = {
		var theProgram: Option[compiler.Tree] = None
		object methodTreeExtractor extends compiler.Traverser {
			override def traverse(t: compiler.Tree): Unit = {
				//writeln0(t)
				//writeln0("*************************")
				t match {
					case compiler.DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
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

	def getSnippetTree(methName: String) = {
		//writeln0(testTree)
		//writeln0("*************************")
		getSubTreeFromName(testTree, methName)
	}
}
*/