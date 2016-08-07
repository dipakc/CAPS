package tests.navigator
import progsynth.extractors._
//import progsynth.plugin._
import progsynth.types._
import progsynth.types.Types._
//import tests.annprogramextractor._
import tests.inputfiles._
import progsynth.spec.StaticAssertions
import progsynth.types.PredSig

object PSNavigator {

	object ProgSynthTestCases {
		import tests.types.TermPPrinter.TermPPrinterTest
		import tests.types.FormulaUtilsTest
		import tests.types.DisplayIdTest
		import tests.types.CaseRewritableTest
		import tests.types.TermGenTest
		import tests.types.FormulaFIdTest
		import tests.formulas.FormulaAndTermUtilsTest
		import tests.formulas.FormulaAndTermUtilsCheck
		import tests.formulas.FOLFormulaRichTest
		import tests.proofobligations.StrongestPostTest
		import tests.provers.PSProverMgrTest
		import tests.provers.Why3InputOutputPrepTest
		import tests.z3prover.Z3ProverTest
		import tests.synthesis.RetValTacticTest
		import tests.synthesis.TacticTests
		import tests.kiama.KiamaRewrite
		import tests.utils.QuantUtilsTest
	}


	//val psp: ProgSynthPlugin = null
	//plugin component
	//val psc = psp.progSynthComponent
	//Types
	//type prog = Program
	type annProg = ProgramAnn
	type p = Pred
	type f = Formula[Pred]
	type t = Term

	//Tree is composed of smaller trees
	//Tree extractors
	//type extractorTrait = ExtractorsOnAST
	//val cct = psc.CaseConstructorTree;
	//psc.CaseConstructorTree; 		psc.FunApp; 				psc.TFunApp
	//psc.FunctionDef;				psc.AnnonymousFunction;		psc.Curried2FunctionApp
	//psc.TypedCurried2FunctionApp;	psc.UnaryMethodApp;			psc.ObjMethodApp
	//psc.RootPackage;				psc.SubPackage;				psc.ValueExtractor
	//psc.ObjExtractor;				psc.WhileDef;				psc.SAssert
	//psc.SRequire;					psc.SEnsuring;				psc.SLoopInv
	//psc.Any2Ensuring;				psc.Boolean2AtomApp;		psc.SRequireSEnsuring

	//Spec Tree is composed of smaller spec trees
	//Spec Tree extractors
	//type extractorOnSpecTreeTrait = ExtractorsOnSpecTree
	//psc.SpecTree //any specTree
	//psc.WhileRetExprTree //A call to synthetic while$<n> method. This method is called by scala at the end of a while block.
	//psc.IfSpecTree
	//psc.WhileSpecTree
	//psc.CompositionSpecTreeWithSpec //a Block with sRequire and/or sEnsuring
	//psc.CompositionSpecTree
	//psc.ValDefSpecTree
	//psc.AssignmentSpecTree
	//psc.FunctionSpecTree //The outer most function definition. Inner functions are not supported.
	//psc.MethodAppSpecTree
	//Terminal Spec Trees
	//IdentifierSpecTree
	//LiteralSpecTree

	//----------------------------
	//Create Annotated Program from SpecTree
	//psc.AnnProgramOfSpecTree
	////-------------------------
	////Extractinv WhileSpecTree
	//psc.WhileSpecTree
	//WhileProg Type
	type t3 = progsynth.types.WhileProg
	//WhileSpecTree to WhileProg
	//psc.AnnProgramOfSpecTree
	//While Loop PO
	progsynth.proofobligations.POGenerator
	//issues
	// * What if Loop Invariant is not given
	// . For now, it is compulsary to give loop inv. Else program will not extract.
	//While Loop Extraction Test Case
	//type _test = WhileLoopExtractionTest; val _inputFile = WhileLoopExample
	//PPrint
	progsynth.printers.ProgramPPrinter
	//-----------------------------------
	//Arithmetic Expressions as programs
	//Step 1
	// (x + y) ---> ExprSpecTree(<Term for x + y>)
	//psc.ExprSpecTree //Terminal spec Tree. Extracts a Term
	///*Uses*/ psc.TermOfTree
	//Test case
	//type t2 = tests.termprograms.ArithmeticOprExtractionTest
	tests.inputfiles.ArithmeticOprExamples
	//Step 2
	//ExprSpecTree(<Term for x + y>) --> AnnProgram
	//psc.AnnProgramOfSpecTree // Create AnnProgram from the ExprSpecTree
	//Step 3
	//PO generation
	progsynth.proofobligations.POGenerator
	//Issues:
	//*. Identifier and Literals are also expressions. Are the specTrees for these are redundant?
	// . ExprSpecTree is called before IdentifierSpecTree and LiteralSpecTree in AnnProgramOfSpecTree.
	// . So for now, IdentifierSpecTree and LiteralSpecTree are redundant
	//*. What kind of expressions are allowed.
	// . Expressions with standard operators +, -, *, /, % etc. User defined function calls not allowed.
	//*. lhs of assignment is not a program so it is still recognized as Identifier
	// . whereas in rhs it is extracted as a ExprProgram-> Term.
	// . -> No problem so far ( Should Identifier be replaced by a Var )
	//---------------------------------

	/*
	 * Refactor Html Printer: The print functions are currently utility functions wrapped
	 * in the XHTMLPrinters object.
	 * Should these be the methods of the respective classes? But it will clutter the classes.
	 * Other option is the pimp my library pattern...to have Rich Classes extending various traits.
	 * Decision1: Keep it as it is. html printing is any not a core functionality.
	 */
	//

	/* pritner object */progsynth.printers.XHTMLPrintersOld
	/*main function*/progsynth.printers.XHTMLPrintersOld.programAnnToHtmlMain _
	///*trait*/  type t5 = progsynth.printers.ProgramSpecPrinter
	/*trait */ type t6 = progsynth.printers.ProgramAnnPOPrinter
	/*trait */ type t7 = progsynth.printers.FormulaPrinter
	/*trait */ type t8 = progsynth.printers.ProgramPrinter
	/*trait */ type t9 = progsynth.printers.Preamble
	/*trait */ type t10 = progsynth.printers.Preamble
	/*trait */ type t11 = progsynth.printers.GeneralPrinters

	type t12 = progsynth.types.ProgramAnn
	//type t14 = progsynth.types.Program
	type t13 = progsynth.types.InvariantF
	type t15 = progsynth.types.Types.FOLFormula
	type t16 = progsynth.types.Term
	type t17 = progsynth.types.Pred
	type t18 = progsynth.types.GuardedCmd

	//Diff program extractor
	/*Junit Test*/ //type t4 = tests.annprogramextractor.DiffTestExtract
	/*Input File*/// tests.inputprog.Diff

	//
	/*Import the repository*/ import progsynth.methodspecs.InterpretedFns._
	/*Fn Repository*/ progsynth.methodspecs.InterpretedFns
	/*Fn Type */ type fnType = progsynth.types.Fn
	//progsynth.extractors.FnAppOfExprTree

	// EndEnd ProgSynth Test Case
	//type progSynthMain = progsynth.plugin.ProgSynthMain
	//type maxProgramTest = tests.outputfiles.MaxProgramTest
	//type psTester = tests.utils.PSPluginTester
	//type astTest = tests.formulas.AST2FormulaTest
	//val cmpObj = tests.utils.CompareFiles

	// Debuggable EndEnd ProgSynth Test Case
	//type progSynthMain2 = progsynth.plugin.ProgSynthMain
	//type maxProgramTest2 = tests.outputfiles.MaxProgramTest
	//type psTester2 = tests.utils.PSPluginTester
	//type psTester3 = tests.utils.PSPluginTesterDbg
	//type astTest2 = tests.formulas.AST2FormulaTest
	tests.testutils.CompareFiles

	//-----------------------------
	//Printing a tree
	//import progsynth.printers.RichTree._
	//writeln0((aTree.printNodes(global))
	//psc.UnknownFragmentSpecTree
	//psc.AnnProgramOfSpecTree
	//type t19 = tests.annprogramextractor.PartialRetValExtract
	//type t20 = tests.verification.PartialRetValVeri
	//type t21 = tests.outputfiles.PartialRetValTest

	//tests.inputprog.PartialRetVal
	progsynth.proofobligations.POGenerator

	//Partial Annotation
	//type t22 = progsynth.plugin.ProgSynthMain //inferAnnotations function call
	//type t23 = progsynth.extractors.InferAnn

	//Partial Annotation, while loop.
	/*implementation */type t25 = progsynth.extractors.InferAnn
	/*input program tests.inputprog.DiffPartialAnn */
	///*test */ type t26 = tests.outputfiles.DiffPartialAnnTestDbg


	//Spurious Skip
	//psc.AnnProgramOfSpecTree

	//Integer Division
	/*input program tests.inputprog.IntegerDivisionPartialAnn */
	///*test*/ type t27 = tests.outputfiles.IntegerDivisionPartialAnnTestDbg
	//Implement inference Algo.
	type t28 = progsynth.extractors.InferAnn
	//pimp my library and polymorphism
	//post on stack overflow

	//TODO: Invariant calculus. define. And between two invariants. etc rvVar calculations.
	//TODO: Function for generating new rvVar
	//TODO: substututeRvVarinF ...should it be combined with rvVar removal, updation
	//TODO: remove location in invariant
	//TODO: Do not modify rv of unknown
	//Done: Forward infering for assignment (only for expr post that does not involve lhs.)
	//Done: composition backward pass.
	//Done: verify the progsynth result for integer div 1
	//Done: verify the progsynth result for integer div 2
		//Done: verify the xml prrogram
		//Done: spurious composition in while. Keep it
		//Done: Expr Prog xml dump not implemented
		//Done: Progsynth.xml has spurious program element.
		//Done: While deco not printed properly.
		//Done: css in git
		//TODO: store step filters.
		//TODO: extraction is also setting some unknown inv...keep it in inference.
		//TODO: walk through the inference debug run.
		//Done: While loop pre should be set to loopInv at the time of extraction.
		//TODO: refactor: Invariant setIfUnknown should take a function.
	//TODO: extraction slide shoe
	//TODO: inference slide show.
	//TODO: While End deco and If End deco.
	//Done: verify the progsynth inference for integer div 4
	//TODO: wp and sp conditions utilities
	//Done: integer div proof obligations.
	//TODO: dsl for formulae
	//TODO: PO: handle expr assignment separately to make the POs simpler.
	//TODO: html report: Remove the expr pre and post.
	//TODO: html report: kalde style report.
	//TODO: expression printing in html is buggy
	//Done: Composition with single program. Eliminate
	//TODO: implement http://www.cs.tufts.edu/~nr/pubs/unparse-abstract.html unparsing.
	//Done: Proof Obligations...failed POs. check.
	//Done: programAnn copy problem.
	//Done: new result file.

	//TODO: Dot file generation
	//progsynth.printers.AnnCodeGraphWriter
	//saveAsGraph
	//EXTRACTION OF SELECTION SORT
	//Extraction of quantified formulae.
	/**input program */
	/**extract test case *///type t29 = tests.annprogramextractor.SelectionSortExtract

	/**formula extract test case */ //type t30 = tests.formulas.AST2FormulaTest
	/**input program */tests.inputfiles.AST2FormulaTestExamples

	/**Array DSL */ progsynth.spec.ArraySpec
	type t31 = progsynth.types.ArrSelect
	//TermOfTermTree
	object predicate_support {
		/** Test case */
		////type t1 = tests.outputfiles.UserDefinedPredicateTestDbg
		tests.formulas.UserDefinedPredicateSubstitution
		/** Input File */
		//tests.inputprog.UserDefinedPredicate

		/** USAGE
 		  * Wrap all your predicate defining methods in object deriving from [[progsynth.spec.PredicateDefsTrait]].
		  * Wrap the bodies of all the predicate defining methods function call "definePredicate".
		  */
		import progsynth.spec.StaticAssertions.@@
		import progsynth.spec.StaticAssertions.@@._
		import progsynth.utils.folformulautils.BoolToFormula._

		object xyz extends progsynth.spec.PredicateDefsTrait{
			def minElem(m: Int, arr: Array[Int], p: Int, q: Int) = StaticAssertions.definePredicate {
				(i: Int) => ∀(i)∘(p <= i && i < q impl arr(m) <= arr(i))
			}
		}

		/** EXTRACTOR
		  * PredInfoExtractorTrait
		  * def extractPredicateInfo(unitTree: PredInfoExtractorTrait.this.global.Tree): HashMap[PredSig, PredDef]
		  * ProgSynthComponent inherits this trait.
		  */
		//type t2 = progsynth.plugin.PredInfoExtractorTrait

		/** PREDICATE_INFO
		  * HashMap[PredSig, PredDef]
		  */
		type t3 = progsynth.types.PredSig //PredSig(val name: String, val args: List[PSType]) {
		type t4 = progsynth.types.PredDef //PredDef(val params: List[String], val formula: FOLFormula)

		/** PredicateInfo example */
		import scala.collection.mutable.HashMap
		val p = VarInt("p")
		val i = VarInt("i")
		val q = VarInt("q")
		val arr = VarArrayInt("arr")
		val m = VarInt("m")
		val predInfo: HashMap[PredSig, PredDef] =
			HashMap(PredSig("minElem", List(PSInt, PSArrayInt, PSInt, PSInt)) ->
					PredDef(List("m", "arr", "p", "q"),
							//(p <= i && i < q impl arr(m) <= arr(i))
							Forall(i, Impl(Atom(Pred("$less$eq", List(p, i))), Atom(Pred("$less$eq", ArrSelectInt(arr, m)::ArrSelectInt(arr, i)::Nil))))))

		/** Predicate Substitution */
		//progsynth.proofobligations.POZ3Prover.replacePredAppWithPredDef(True, Some(predInfo))
	}

	object existsPositiveElementSynthesis {
		/** Test case */
		//type t0 = tests.synthesis.ExistsPositiveElement0

		/** Test case Source File*/
		tests.synthesis.ExistsPositiveElementSrc0

		/** Synthesis methods */
		//progsynth.synthesisold.PSSynthesizer
		//def synthProg(unkProg: UnknownProg): List[ProgramAnn]
		/** Main call from */
		//type t1 = progsynth.plugin.ProgSynthMain
		//def psMain(unitTree: ProgSynthMain.this.global.Tree): Unit

		/** synthesis information to html */
		progsynth.printers.XHTMLPrintersOld.synthInfoToHtml(null)

		object AddingParamsToFunctionProg {
			import progsynth.types.FunctionProg
			/**test case*/
			//import tests.outputfiles.IntSqrtDbg
			/**test case input file*/

		}
	}
	/**
	 * ProgSynth output for method pkg1.pkg2.obj1.foo is now stored in <AppConfig.resultDir>\pkg1.pkg2.obj1.html
	 * ResultHtmlWriter maintains a map of outputfilePath -> List(contentHtml) (there can be multiple methods in a object)
	 * Output files are written to disk at the end of each "compilation unit" processing. (psEnd method)
	 * */
	object outfilePerObject {
		/**test case*/
		//import tests.outputfiles.IntSqrtDbg
		/**test case input file*/
		/** desired output file name: tests.inputprog.IntSqrtObject.html */
		/** DefTree.symbol.ownerchain results in the following List */
		/*List(method intSqrt2, object IntSqrtObject, package inputprog, package tests, package <root>) */
		//import progsynth.plugin.ProgSynthMain
		import progsynth.printers.ResultHtmlWriter
		import progsynth.config.AppConfig
	}

	/*
	/** Exists Positive Element */
	object ExistsPositiveElement {
	    /**Test case */
	    //worksheet ExistsPositiveElement2WS
	    //tests.synthesis.ExistsPositiveElement2WSApp
	    //EPETkzEdt.tikz
	    type t1 = progsynth.synthesisold.RetValTactic
	    tests.synthesis.ExistsPositiveElementDev2
	    /**New tactics */
	    type t2 = progsynth.synthesisnew.PSTacticFuns
	}
	*/

	object AsgnDerivationAsNormalNode {
		type t2 = progsynth.synthesisnew.AsgnDerivation
		type t2_1 = progsynth.synthesisnew.CalcProofStep

		//type t4 = progsynth.synthesisnew.StartAsgnDerivationTactic
		type t6 = progsynth.synthesisnew.InstantiateMetaTactic

		/** nodeToXhtml */
		//main\scala\progsynth\synthesisnew\SynthNode.scala:nodeToXhtml
		type t8 = progsynth.printers.AsgnDerivationPrinter2
		/**asgnDerivation2ToHtml*/
		//main\scala\progsynth\printers\XhtmlPrinters2.scala:798
		/**InitGlobalState*/
		//D:\EclipseProjects\ProgSynthWebApp\app\models\InitGlobalState.scala:431
		/**StepIntoFormulaTactic*/
		//progsynth\synthesisnew\PSTactics.scala:486
	}

	object SynthNodeToHierarchicalTree {
		type synthNodeClass = progsynth.synthesisnew.SynthNode
		type synthTreeClass = progsynth.synthesisnew.SynthTree
		type stepInTactic = progsynth.synthesisnew.StepInTactic
		type stepOutTactic = progsynth.synthesisnew.StepOutTactic

	}

	//Implment this first and then implent the GUI stuff.
	object StepIntoConsequentTactic {
		import progsynth.synthesisnew._
		type t1 = StepIntoConsequentTactic
		type t2 = FormulaFrame
		/**InitGlobalState*/
		//D:\EclipseProjects\ProgSynthWebApp\app\models\InitGlobalState.scala:431

	}

	object MacroSupport {
		import progsynth.synthesisnew._
		import progsynth.types._
		type a = Frame
		//val macros: List[Macro] = Nil

		Macro
		//case class Macro(){}

		//.applyTactic2(new AddMacrosTactic(macros))
		//alt-2

		//type b = AddMacrosTactic
		type c = MacroExpander
	}

	object PSProverMgrImpl {
	    //test case
	    type t0 = progsynth.provers.PSProver
	    type pm = progsynth.provers.PSProverMgr
	    type t1 = tests.provers.PSProverMgrTest
	    //--------------
	    //reference implementation
	    progsynth.proofobligations.Z3Prover
	    //reference test case
	    type t2 = tests.z3prover.Z3ProverTest
	    //TODO: enable all the tests in PSProverMgrTest.
	    // Will need implementation of arrays in PSProverMgr

	    /*
	    test("f1") fails
	    problem: logger is null
	    logback-classic already added to dependencies.
	    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.7"
	    solution: lazy val logger
	    */
	}

	object Config {
	    type t = progsynth.provers.Config
	}

	object EndToEndUsingProverMgr {

	}

	object Why3Integration {
		type pm = progsynth.provers.PSProverMgr
		type t1 = progsynth.provers.PSProver
		type ta = progsynth.provers.ProverExec
		type t2 = progsynth.provers.PSZ3Prover
		type t3 = progsynth.provers.PSWhy3AltErgoProver
		type t4 = progsynth.synthesisnew.SimplifyAutoTactic
	}

}

