package expt

import scala.tools.nsc.{ Settings, Global }
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.util.BatchSourceFile
import scala.tools.nsc.util.SourceFile
import scala.tools.nsc.io.VirtualFile
import scala.tools.nsc.io.AbstractFile
import progsynth.debug.PSDbg._

object RunCompiler extends App {
	// prepare the code you want to compile
	//val code = "object Foo extends Application { writeln0(42 / 0) }"
	//val sources = List(new BatchSourceFile("<test>", code))

	val settings = new Settings
	// save class files to a virtual directory in memory
	//settings.stopAfter.value = List("refchecks") //what name to give here
	//settings.processArguments(List("-Xshow-phases", "-Yshow-trees", "-Xprint:refchecks", "-Ystop-after:refchecks"), true)
	var argStr = ""
	//argStr += " -Xshow-phases"
	//argStr += " -Xprint:refchecks"
	argStr += " -Yshow-trees"
	argStr += " -Ystop-after:refchecks"
	settings.processArgumentString(argStr)
	//settings.showPhases.value = true //not working
	//settings.skip.value = List("refchecks")
	settings.outputDirs.setSingleOutput(new VirtualDirectory("(memory)", None))

	val reporter = new ConsoleReporter(settings)
	val compiler = new Global(settings, reporter) {
		override protected def computeInternalPhases() {
			//super.computeInternalPhases

			val phs = List(
				syntaxAnalyzer			-> "parse source into ASTs, perform simple desugaring",
				analyzer.namerFactory	-> "resolve names, attach symbols to named trees",
				analyzer.packageObjects	-> "load package objects",
				analyzer.typerFactory	-> "the meat and potatoes: type the trees",
				superAccessors			-> "add super accessors in traits and nested classes",
				pickler					-> "serialize symbol tables",
				refChecks				-> "reference/override checking, translate nested objects",
				uncurry					-> "uncurry, translate function values to anonymous classes",
				tailCalls				-> "replace tail calls by jumps",
				specializeTypes			-> "@specialized-driven class and method specialization",
				explicitOuter			-> "this refs to outer pointers, translate patterns",
				erasure					-> "erase types, add interfaces for traits",
				lazyVals				-> "allocate bitmaps, translate lazy vals into lazified defs",
				lambdaLift				-> "move nested functions to top level",
				constructors			-> "move field definitions into constructors",
				mixer					-> "mixin composition",
				cleanup					-> "platform-specific cleanups, generate reflective calls",
				genicode				-> "generate portable intermediate code",
				inliner					-> "optimization: do inlining",
				closureElimination		-> "optimization: eliminate uncalled closures",
				deadCode				-> "optimization: eliminate dead code",
				terminal				-> "The last phase in the compiler chain"
				)

			phs foreach (addToPhasesSet _).tupled

			//for (phase <- new DivByZero(this).components)
			//	phasesSet += phase
		}
	}



//	val flist = for(afile <- filenames) yield {
//		(afile, scala.io.Source.fromFile(afile).mkString)
//	}

	val sources = {
		val sources0 = {
			val filenames = List (
				"""src\localhost\StaticAssertions.scala""",
				"""src\localhost\ProgramTypes.scala""",
				"""src\localhost\FOLFormula.scala"""
				)

			filenames map compiler.getSourceFile
		}

		val batchSrc = {
			//val code = "object Foo extends Application { writeln0(42 / 2) }"
			val code =
				"""
				package progsynth
				import StaticAssertions._
				import FormulaUtils._
				import Formula._
				object MaxObject {
					def cf(a:Int)(b:String) = {
						a.toString + b
					}
				  def getMax(x: Int): Int =
				  	sRequire(x==x){
				  	x
				  } sEnsuring(rv => rv == x)
				}
				"""
			val name = "<test>"

			def createBatchSourceFile(fname:String, fcode:Array[Char]) = {
				//new BatchSourceFile(fname, fcode)
				//dipakc: hack: set the container to self.
				//container is used to rank the files in coreClassesFirst in Global
				val virtFile = new VirtualFile(fname){
					override val container = new VirtualFile("test")}
				new BatchSourceFile(virtFile, fcode)
			}
			createBatchSourceFile(name, code.toCharArray)
		}

		sources0 ::: List(batchSrc)
	}

	//var sources = filenames map compiler.getSourceFile
	//var sources:List[SourceFile] = Nil
//	val sources = for((fname, fcode) <- flist2) yield {
//		writeln0(fname)
//		writeln0(fcode)
//		createBatchSourceFile(fname, fcode)
//	}


	writeln0(sources)

	val aRun = new compiler.Run()
	aRun.compileSources(sources)
	for (unit <- aRun.units /*if unit.toString == "Max.scala"*/){
		logln(" *********** %s ***********".format(unit))
		logln(unit.body)
	}
	//scala.reflect.Code
	reporter.printSummary()
}
