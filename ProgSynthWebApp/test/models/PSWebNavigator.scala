package models

import progsynth.provers.Why3InputPrep
import progsynth.provers.Why3OutputParser
import progsynth.synthesisnew.IntroIfTactic
import progsynth.synthesisnew.IntroSwapTactic
import progsynth.synthesisnew.RTVInPost2Tactic
import progsynth.synthesisnew.StepIntoBATactic
import progsynth.synthesisnew.StepIntoIFBATactic
import progsynth.types._

object PSWebNavigator {

	//ProgSynth testcases
	//import tests.navigator.PSNavigator.ProgSynthTestCases

	object ProgSynthWebAppTestCases {
		//type t1 = models.HelloWorldSpec
		type t2 = models.ArraySwapDev
		type t3 = models.TermParserTest
		type t4 =  models.SaveTreeJsonTest
	}

	object Recipes {
		//How to add a program derivation to gallery
		models.derivations.Derivations

		//How to add derivation to load on startup
		capsstate.InitGlobalState.test_init _

		//How to show both, the parent as well as child
		/*TODO: temporary change. Hide the parent node */
		//progsynthlog.less
		//.ProgramAnnParentNode {
			/*display: none;*/
		//}

	}

	object WebAppParserObj {
		//val x = TestScript
		type t0 = models.pprint.BindingPowers

		type t1 = models.pprint.TermIntPPrint
		type t2 = models.pprint.TermArrayIntPPrint
		type t3 = models.pprint.TermBoolPPrint
		type t4 = models.pprint.TermArrayBoolPPrint
		type t5 = models.pprint.FOLFormulaPPrint

		//type t6 = models.parser.TermIntParser
		//type t7 = models.parser.TermArrayIntParser
		//type t8 = models.parser.TermBoolParser
		//type t9 = models.parser.TermArrayBoolParser

//		type t13 = models.parser.TacticParserOld

		// Web request flow.
		//import controllers.ControllerMain
		//import models.GlobalState
//		import models.parser.TacticParserOld

		//Application.applyTactic
		//GlobalState.applyTactic _
		/**//**///TacticParser.parseTacticData _
		/**//**//**///TacticParser.getValObject _
		/**//**//**///TacticParser.getScalaObj _

		type t10 = progsynth.synthesisnew.SynthTree
		type t11 = progsynth.synthesisnew.SynthNode
		type t12 = progsynth.synthesisold.PSSynthesizerUtils
		progsynth.synthesisnew.ContextFinder

		// SynthTree has findContex method.
		// which calls findContext(curNode.nodeObj, curNode.nodeObj, newNodeObj)
		// Ques: Where to implement getContext.
		// something similiar already there in progsynth.synthesisold.PSSynthesizerUtils
		// Write your own version since there are some differences.
		// Where ?
		// models.ContextFinder object.
		// def findContext(outerObj, outerCtx, innterObj)
		// Before creating a node, client should call SynthTree.findContext to get the context of the node.

		//Modify stepout tactic in Tactics.scala.
	}

	object ConfigManagement {
	    //config file : conf/application.conf
	    //ProgSynthWebApp
	    //play startup hook on app.Global.onStart
	    //Read the config file and sets the progsynth config object
	    app.Global
	    //ProgSynthWeb Config Object
	    progsynth.config.AppConfig.configMap
	    //import progsynth.config.AppConfig
	    //AppConfig.configMap("provers.tmp").asInstanceOf[String]
	}

	object ArraySwapNavig {
		import progsynth.types._
		import progsynth.types.Types._

		models.derivations.scripts.ArraySwap
		models.derivations.scripts.DutchNationalFlag

		type t = progsynth.synthesisnew.IntroSwapTactic
		progsynth.synthesisnew.TacticDocRepo.IntroSwapTacticDoc

		type t2 = progsynth.synthesisnew.RTVInPost2Tactic

		mkProcedureDef _
		validateProcedureDef _
		mkProcedureCall _
		validateProcedureCall _

		progsynth.utils.PSErrorCodes

	}

	object GUIForTactic {
		import progsynth.synthesisnew._
		//RTVInPost2Tactic(displayId: Int, variable: Var, initValue: Term, bounds: TermBool) extends FunTactic {
		type t1 = RTVInPost2Tactic

		//StepIntoBATactic(lhsVars: List[Var]) extends StepInTactic {
		type t2 = StepIntoBATactic

		//StepIntoIFBATactic(lhsVars: List[Var]) extends StepInTactic {
		type t3 = StepIntoIFBATactic

		//IntroSwapTactic( array: Var, index1: TermInt, index2: TermInt) extends FunTactic {
		type t4 = IntroSwapTactic

		//IntroIfTactic(guards: List[TermBool]) extends FunTactic {
		type t5 = IntroIfTactic

	}

	object IFProgGrdsCheck {

		progsynth.types.IfProg

	}

	object Why3MinQuantifierEncodingProject {
		import progsynth.provers._
		/*
		 *
		 */
		Why3InputPrep
		type t2 = Why3OutputParser


	}

	object GalleryLocation {
		val initPrograms = capsstate.GlobalState
		val galleryProgram = models.derivations.Derivations
	}

	object PSTacticsList {
		import progsynth.synthesisnew.StepIntoSubFormulaTactic
		import progsynth.synthesisnew.StepOutTactic
		import progsynth.synthesisnew.ReplaceSubFormulaTactic
		import progsynth.synthesisnew.ReplaceSubTermTactic
		import progsynth.synthesisnew.OnePointTactic
	}

	object StepIntoIntTermsProject {
		import progsynth.types._

		type t = TermIntCostructorUtils

		//-----------------
		PlusTermInt; MinusTermInt; TimesTermInt; DivTermInt; ModTermInt; UMinusTermInt
		PLUS; MINUS; TIMES; DIV; MOD; UMINUS
		//----------------
		PlusQTermInt; TimesQTermInt; MinQTermInt; MaxQTermInt;
		PLUSQ; TIMESQ; MINQ; MAXQ
		//-----------------
		AndTermBool; OrTermBool; NotTermBool;
		ForallTermBool; ExistsTermBool;
		//---------
		LTTermBool; GTTermBool; LETermBool; GETermBool; EqEqTermBool
		LE; GE; LT; GT; EqEq

		type t3 = progsynth.synthesisnew.StepIntoSubTerm2
	}

	object TxtFormat {
		//tests
		type tc = models.SaveTreeJsonTest
		type t2 = models.MQPrinterTest
		type t3 = models.MQPrinterParserTest

		//create TacticTV object from Tactic object.
		jsonprinter.TacticTVMapper

		type t = jsonprinter.tactics.TacticTV
		type t1 = jsonprinter.tactics.Init4TV
		//type t2 = jsonprinter.tactics.DeleteConjunctTV
		//Save Tree Json
		models.mqprinter.MQPrinter
	}

	object CountSupport {
	    /** Count quantifier is parsed as CountTermT using context-free parsing*/
        models.parseruntyped.CountTermT
        /** Context sensitive phase converts CountTermT to Sum
         *  Method name: getIntTerm */
        type t = models.parseruntyped.TermValidator
        /** Not part of the Term Hierarchy */
        type t2 = progsynth.types.CountInt

        //Count is encoded as Sum internally.
        //Print is not implemented
        //ITE function not encoded.

	}

	object DerivationLocation {
	    //conf/application.conf
	    //application.derivations=""".\derivations"""

	    //How to read the config
	    type t = app.ProverSettings
	    import play.api.Play
	    val config = Play.current.configuration
	    config.getString("ostype")

	    //FileStorage
	    type t2 = capsstate.FileStorage
	    //def root: String = "derivations/users/" + user.email

	}

	trait PreorderDisplayId {
	    def test {
	        val x = new DisplayId{}.setDisplayIdAll
    	    import progsynth.printers.ProgramAnnPrinter2
    	    new ProgramAnnPrinter2{override val isMQ = false}.programAnnToHtml(???)(???)
	    }
	}

}