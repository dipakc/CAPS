package tests.testutils
/*
import progsynth.plugin.ProgSynthPlugin
import scala.tools.nsc.Global
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.io.VirtualDirectory
import java.io.File

trait PSPluginTesterDbg {
	val compilerArgStr = " -Ystop-after:ProgSynth"

	// Explicitly add scala library and compiler to classpath since test run from sbt test env
	// does not add these values to classpath.
	// Use "prepend" to prepend the jars to javabootclasspath.
	// This is necessary since eclipse junit test run adds the scala library and compiler in
	// eclipse distribution to the javabootclasspath.
	val cpEntries: List[String] =
		"""extlib\scala-library-2.9.1.jar""" ::
		"""extlib\scala-compiler-2.9.1.jar""" ::
		"""extlib\kiama_2.9.1-1.2.0.jar""" ::
		"""target\scala-2.9.1\progsynth_2.9.1-0.0.1.jar""" :: Nil

	private var m_compiler: Option[scala.tools.nsc.Global] = None
	private var m_run: Option[scala.tools.nsc.Global#Run] = None

	def runCompiler(testFiles: List[String], resultFileName: Option[String] = None) = {
		val settings = new Settings
		settings.processArgumentString(compilerArgStr)
		settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

		addToClassPath(settings, cpEntries, prepend = true)

		val reporter = new ConsoleReporter(settings)
		val compiler0 = new Global(settings, reporter) {
			val progSynthPlugin = new ProgSynthPlugin(this)
			var progSynthComponent = progSynthPlugin.progSynthComponent
			progSynthComponent.pscConfig.resultFileName = resultFileName

			override protected def computeInternalPhases() {
				super.computeInternalPhases
				phasesSet += progSynthComponent
			}
		}

		val psc = compiler0.progSynthPlugin.progSynthComponent
		val compiler1 = psc.global
		val aRun = new compiler1.Run()
		val sources = testFiles map {compiler1 getSourceFile _}
		aRun.compileSources(sources)
		m_compiler = Some(compiler1)
		m_run = Some(aRun)
		(m_compiler, m_run, Some(psc))
	}

	private def addToClassPath(settings: Settings, cpEntries: List[String], prepend: Boolean) = {
		if (prepend)
			cpEntries.foreach( settings.javabootclasspath.prepend(_))
		else
			settings.classpath.tryToSet(List(cpEntries.mkString(File.pathSeparator)))
	}


//	def getFileTree(fileName: String): Option[scala.tools.nsc.Global#Tree] = {
//
//	}
	def getFileTree(fileName: String): Option[scala.tools.nsc.Global#Tree] = {
		(m_compiler, m_run) match {
			case (Some(compiler), Some(run)) =>
				val compilationUnit = (run.units filter (_.toString == fileName))
				if (compilationUnit.hasNext)
					Some(compilationUnit.next().body)
				else None
			case _ => None
		}
	}

	def getMethodTree(inputTree: scala.tools.nsc.Global#Tree, mname: String): Option[scala.tools.nsc.Global#Tree] = {
		if(m_compiler.isDefined && m_run.isDefined) {
			val compiler = m_compiler.get
			var retVal: Option[compiler.Tree] = None
			object methodTreeExtractor extends compiler.Traverser {
				override def traverse(t: compiler.Tree): Unit = {
					t match {
						case compiler.DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
							if (name.toString == mname) {
								retVal = Some(t)
							}
						case _ =>
							super.traverse(t)
					}
				}
			}
			methodTreeExtractor.traverse(inputTree.asInstanceOf[compiler.Tree])
			retVal
		}
		else None
	}

	def getValDefTree(inputTree: scala.tools.nsc.Global#Tree, valName: String): Option[scala.tools.nsc.Global#Tree] = {
		if(m_compiler.isDefined && m_run.isDefined) {
			val compiler = m_compiler.get
			var retVal: Option[compiler.Tree] = None
			object valDefTreeExtractor extends compiler.Traverser {
				override def traverse(t: compiler.Tree): Unit = {
					t match {
						case compiler.ValDef(_, name, _, _) =>
							if (name.toString == valName) {
								retVal = Some(t)
							}
						case _ =>
							super.traverse(t)
					}
				}
			}
			valDefTreeExtractor.traverse(inputTree.asInstanceOf[compiler.Tree])
			retVal
		}
		else None
	}

	def getValDefRhsTree(inputTree: scala.tools.nsc.Global#Tree, valName: String): Option[scala.tools.nsc.Global#Tree] = {
		if(m_compiler.isDefined && m_run.isDefined) {
			val compiler = m_compiler.get
			var retVal: Option[compiler.Tree] = None
			object valDefTreeExtractor extends compiler.Traverser {
				override def traverse(t: compiler.Tree): Unit = {
					t match {
						case compiler.ValDef(_, name, _, rhs) =>
							if (name.toString == valName) {
								retVal = Some(rhs)
							}
						case _ =>
							super.traverse(t)
					}
				}
			}
			valDefTreeExtractor.traverse(inputTree.asInstanceOf[compiler.Tree])
			retVal
		}
		else None
	}

}
*/