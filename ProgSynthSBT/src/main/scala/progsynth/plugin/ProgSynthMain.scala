package progsynth.plugin

/*
import progsynth.extractors.AnnProgramExtractor
import scala.tools.nsc.plugins.PluginComponent
import scala.collection.mutable.ListBuffer
import progsynth.proofobligations.POGenerator
import progsynth.proofobligations.POZ3Prover
import progsynth.{utils=>psu}
import progsynth.printers.XHTMLPrintersOld
import progsynth.printers.ResultHtmlWriter
import progsynth.printers.XmlLogFileEvents
import progsynth.extractors.InferAnn
import progsynth.printers.ProgramAnnHtmlDbgPrinter
import progsynth.printers.AnnCodePrinterWrapper.AnnCodePrinter
import progsynth.printers.AnnCodeWriterWrapper.AnnCodeWriter
import scala.collection.mutable.HashMap
import progsynth.types.Fn
import progsynth.types.FOLFormula
import progsynth.printers.RichTree._
import progsynth.types._
import progsynth.types.Types._

import progsynth.synthesisold.PSSynthesizer
import progsynth.config.AppConfig
import progsynth.debug.PSDbg

trait ProgSynthMain extends AnnProgramExtractor with DefDefExtractorTrait with PredInfoExtractorTrait
	with XmlLogFileEvents with ResultHtmlWriter { self: PluginComponent =>

	import global._
	val pscConfig = AppConfig

	/** This method will be called at the start of the ProgSynth phase */
	def psStart() = {
		xmlLogFileStart()
	}

	/** constructs the output file name from the DefTree */
	private def getOutputFileName(aDefTree: Tree) = {
		self.pscConfig.resultFileName.getOrElse {
			val oc = aDefTree.symbol.ownerChain
			val ocNoPkg = oc.tail //Remove Pkg
			val ocRev = ocNoPkg.reverse
			val ocNoMeth = ocRev.tail //Remove Method
			ocNoMeth.map(_.name).mkString(".") + ".html"
		}
	}

	/** This method will be called for each srcfile*/
	def psMain(unitTree: Tree) = {

		val predicateInfo = extractPredicateInfo(unitTree)

		getDefTrees(unitTree) foreach { defTree =>
			xmlLogFileCUStart()
			val annProgOpt = extractAnnProgramFromMethodDef(defTree)
			if(annProgOpt.isDefined){
				//PSDbg.writeln0(annProgOpt.get)/////////////////////
				//PSDbg.writeln0(annProgOpt.get.toCode()) //dipdebug
				val annProg = annProgOpt.get
				ProgramAnnHtmlDbgPrinter.setProgramAnnToMonitor(annProg)
				annProg.inferAnn()
				//val annProg = annProgOpt.get
				PSDbg.writeln0(annProg)///////////////////////
				POGenerator.populatePOs(annProg)
				POZ3Prover.provePOs(annProg, Some(predicateInfo))

				val progAndPoNode = XHTMLPrintersOld.programAnnToHtmlMain(annProg)
				val annProgAndPOsHtml = psu.formatXml(progAndPoNode)
				val synthHtmlInfo = if (AppConfig.synthUnk) {
					val synthInfo = PSSynthesizer.synthAllUnkProgs(annProg)
					val synthInfoNodes = XHTMLPrintersOld.synthInfoToHtml(synthInfo)
					psu.formatXml(synthInfoNodes)
				} else {""}

				val outputFileName = getOutputFileName(defTree)
				val totalContent = annProgAndPOsHtml + synthHtmlInfo
				self.addToHtmlResult(outputFileName, totalContent)

				AnnCodeWriter.writeAnnCode(AnnCodePrinter.programAnnToAnnCode(annProg).toString)
				//AnnCodeGraphWriter.saveAsGraph(annProg)
			}
			xmlLogFileCUEnd()
		}
	}

	/** This method will be called at the end of the ProgSynth phase */
	def psEnd() = {
		xmlLogFileEnd()
		self.writeHtmlResult()
	}

}

/** Extract user defined predicates */
trait PredInfoExtractorTrait { self: PluginComponent with DefDefExtractorTrait with AnnProgramExtractor =>
	import global._

	/** Returns a map of Predicate Signature and Predicate Definitions
	 * Only methods whose body wrapped by the function "definePredicate" are considered for extraction.
	 * Only methods that return boolean are considered for extraction.
	 *
	 * @param unitTree AST of the whole compilation unit
	 * @return a map of Predicate Signature and Predicate Definitions*/
	def extractPredicateInfo(unitTree: Tree): HashMap[PredSig, PredDef] = {
		val predicateInfo = new HashMap[PredSig, PredDef]
		getDefTrees(unitTree) foreach { defTree => defTree match {
			case DefDef(_, NameDef(name), Nil, List(vparams), TypeTreeFromPSType(PSBool), rhs) =>
				val argsOpt = getParamNameTypeList(vparams)
				argsOpt map { args =>
					val aPredSig = PredSig(name, args map {arg => arg._2})

					rhs match {
						case DefinePredicateTree(AnonymousFunctionTree(FormulaOfTree(formula))) =>
							predicateInfo += aPredSig -> PredDef(args map {arg => arg._1}, formula)
						case DefinePredicateTree(FormulaOfTree(formula)) =>
							predicateInfo += aPredSig -> PredDef(args map {arg => arg._1}, formula)
						case _ =>
					}
				}
			case _ =>
				//PSDbg.writeln0(defTree.printNodes(global))
		}}
		return predicateInfo
	}

	private def getParamNameTypeList(vparams: List[ValDef]): Option[List[(String, PSType)]]  = {
		val args: List[(String, PSType)] = vparams flatMap { vparam =>
			//import progsynth.printers.RichTree._
			//PSDbg.writeln0(vparam.printNodes(global))
			vparam match {
				case ValDef(_, argName, TypeTreeFromPSType(argType), _) => List((argName.toString, argType))
				case _ => Nil
			}
		}
		if (args.length == vparams.length)
			Some(args)
		else
			None //Some param might have type that is not supported by ProgSynth
	}
}

trait DefDefExtractorTrait { self: PluginComponent =>
	import global._
	//private var defTrees = new ListBuffer[Tree]
	def getDefTrees(unitTree: Tree) = {
		val defExt = (new DefDefExtractor())
		defExt.traverse(unitTree)
		defExt.defTrees.toList
		//defTrees.toList
	}

	class DefDefExtractor extends Traverser {
		var defTrees = new ListBuffer[Tree]
		override def traverse(t: Tree): Unit = {
			t match {
				case global.DefDef(mods, name, tparams, vparamss, tpt, rhs) =>
					defTrees += t
				case _ =>
					super.traverse(t)
			}
		}
	}
}

*/
