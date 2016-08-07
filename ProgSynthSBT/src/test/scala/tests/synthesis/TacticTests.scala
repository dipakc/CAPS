package tests.synthesis

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
import tests.testutils.CompareFiles._
import progsynth.synthesisold.RetValMainTactic
import progsynth.synthesisold.PSTacticResult
import progsynth.debug.PSDbg._
import progsynth.testobjects._


@RunWith(classOf[JUnitRunner])
class TacticTests  extends FunSuite with ShouldMatchers {
	test("PropagateAssertionsDownTest") {
		val synthTree = PropagateAssertionDownSPTest()
	}

	test("PropagateAssertionsDownTestBase") {
		val synthTree = PropagateAssertionDownSPTestBase()
	}

}

