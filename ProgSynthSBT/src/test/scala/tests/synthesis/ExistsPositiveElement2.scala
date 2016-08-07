package tests.synthesis
/*
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import progsynth.types._
import progsynth.types.Types._
import progsynth.config.AppConfig
import progsynth.printers.XHTMLPrintersOld._
import progsynth.utils.PSUtils
import progsynth.proofobligations.POGenerator
import progsynth.proofobligations.POZ3Prover
import org.junit.Assert
import org.scalatest.matchers.ShouldMatchers
import progsynth.logger.XMLLogWriter
import tests.utils.PSPluginTesterDbg
import tests.utils.CompareFiles._
import progsynth.debug.PSDbg._
@RunWith(classOf[JUnitRunner])
class ExistsPositiveElement2 extends FunSuite with ShouldMatchers with PSPluginTesterDbg {
	ignore("ExistsPositiveElement. NOT YET IMPLEMENTED") {
		val inputFileName = """src\test\scala\tests\synthesis\ExistsPositiveElement2Src.scala"""
		val outputFileName = """tests.synthesis.ExistsPositiveElement2.html"""
		val (compilerOpt, runOpt, psc) = runCompiler( inputFileName :: Nil, Some(outputFileName))
		withClue ("compilerOpt && runOpt && psc isDefined:"){compilerOpt.isDefined && runOpt.isDefined && psc.isDefined should equal(true)}
		val outputFile = AppConfig.resultDir + "\\" + outputFileName
		logln(outputFile)
		//Path is relative to the "test\resources" directory
		//Run sbt> test-only tests.outputfiles.BubbleSortTest before running the testcase in eclipse.
		//Otherwise the expectedFile will not be copied to the target folder
		val expectedFile = """/golden/""" + outputFileName

		val filesAreSame = compareFiles(outputFile, expectedFile)
		//withClue ("OutputFile differs from ExpectedFile:"){filesAreSame should equal(true)}
	}
}
*/