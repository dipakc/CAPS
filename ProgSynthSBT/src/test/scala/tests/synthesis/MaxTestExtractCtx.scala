//package tests.synthesis
//
//import org.junit.runner.RunWith
//import org.scalatest.FunSuite
//import org.scalatest.junit.JUnitRunner
//import tests.utils.ProgSynthPluginTest
//import progsynth.types._
//import progsynth.types.Types._
//import progsynth.config.AppConfig
//import progsynth.printers.XHTMLPrinters._
//import progsynth.utils._
//import progsynth.proofobligations.POGenerator
//import progsynth.proofobligations.POZ3Prover
//import org.junit.Assert
//import org.scalatest.matchers.ShouldMatchers
//import progsynth.synthesisold.PSSynthesizer
//
//@RunWith(classOf[JUnitRunner])
//class MaxTestExtractCtx extends FunSuite with ShouldMatchers with ProgSynthPluginTest {
//	//override def argStr = """ -Ystop-after:refchecks""" //TODO: should be able to augment to the base class arguments.
//	override def addSourceFiles() = {
//	    addSourceFiles(List("""src\test\scala\tests\synthesis\MaxTestExtractCtxSrc.scala"""))
//	}
//
//    ignore("CtxMap test") {
//		val maxProgramTreeO = getDefTree("""MaxTestExtractCtxSrc.scala""", "getMax")
//		withClue ("maxProgramTreeO.isDefined:"){maxProgramTreeO.isDefined should equal(true)}
//		//writeln0(maxProgramTreeO.get)
//		val maxAnnProgram = psc.extractAnnProgramFromMethodDef(maxProgramTreeO.get)
//		withClue ("maxAnnProgram.isDefined:"){maxAnnProgram.isDefined should equal(true)}
//		//writeln0(maxAnnProgram)
//		overwriteFile(AppConfig.resultFile, programAnnToHtmlMain(maxAnnProgram.get))
//		val ctxMap = PSSynthesizer.getCtxMap(maxAnnProgram.get)
//		val ctxMapStrList = ctxMap.toList map {case (prog, ctx) =>
//				prog.toString + ", " + ctx.toString
//			}
//		val ctxMapStr = "Map("+ ctxMapStrList.mkString(", ") + ")"
//		//writeln0(ctxMapStr)
//
//		val resStr =
//			(ctxMap.toList.zip(Range(0, ctxMap.toList.length)) map { case ((prog, ctx),i )=>
//				i + " " + ctx.toString
//			}).mkString(" ")
//		val expectedStr = "0 [] [] 1 [] [x y] 2 [] [x y] 3 [] [x y] 4 [max] [x y] 5 [max] [x y] 6 [max] [x y] 7 [max] [x y]"
//		withClue ("Ctx String mismatch: "){resStr should equal(expectedStr)}
//	}
//}
