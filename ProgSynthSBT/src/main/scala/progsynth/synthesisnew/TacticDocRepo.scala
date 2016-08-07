package progsynth.synthesisnew

import scala.xml._
import progsynth.types.Types._
import progsynth.types._
import progsynth.printers.XHTMLPrinters2

/** Print using the TacticDocGen Utility in ProgSynthWebApp project */

object TacticDocRepo  {

	object Init4TacticDoc extends InitTacticDoc {
		val name 				= "Init4"
		val shortDescription    = "Tactic used to specify the program to be derived"
		val tacticInputs        =
			TacticField("name", "Derivation Name", "String", "Name of the derivation") ::
		    TacticField("immutableVars", "Constants", "List[String]", "List of the constants") ::
		    TacticField("mutableVars", "Variables", "List[String]", "List of the variables") ::
		    TacticField( "globalInvs", "Global Invariants", "List[Formula]",
		            """Invariants that should hold through out the program.
		            Typically the assertions involving constants.""") ::
		    TacticField( "preF", "Precondition", "Formula", "Precondition of the program") ::
		    TacticField( "postF", "Postcondition", "Formula", "Postcondition of the program") ::
		    Nil
		val fieldMap = tacticInputs.map(tf => (tf.name, tf)).toMap
		val transfDiv = None
		val appConditions 		=
		    "Head node should be the root node" ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	object ReplaceSubFormulaTacticDoc extends FormulaTacticDoc {
	    type t4 = ReplaceSubFormulaTactic
		val name =
		    "ReplaceSubFormula"
		val shortDescription =
		    "Replaces a subformula with an equivalent formula"
		val tacticInputs =
		    TacticField( "oldSubFId", "oldSubFId", "Int", "Id of the subformula to be replaced") ::
		    TacticField( "newSubF", "newSubF", "Formula", "new formula") ::
		    Nil
	    val transfDiv = None
		val appConditions =
		  	"Head node should be a formula node" ::
		    "oldSubF should be equivalent to the newSubF under the given context" ::
		    Nil
		val hint = "Replace formula by an equivalent formula."
		val notes = """The context of the subformula is not used for checking the equivalence.""" ::
		    Nil
	}

	object SimplifyAutoTacticDoc extends FormulaTacticDoc {
		val name 				= "SimplifyAutoTactic"
		val shortDescription    = ""
		val tacticInputs        =  Nil
		val transfDiv = None
		val appConditions 		= "" ::
			"Head node should be a formula node" ::
			Nil
		val hint 				= ""
		val notes				= Nil
	}

	//class IntroSwapTactic( array: Var, index1: TermInt, index2: TermInt) extends FunTactic {
	object IntroSwapTacticDoc extends ProgramTacticDoc {
		type t = IntroSwapTactic
		val name 				= "IntroSwap"
		val shortDescription    = "Introduce an Array swap statement"
		val tacticInputs        =
		    TacticField(
		        name = "array",
		        displayName = "Array Variable",
		        ftype = "Array",
		        description = "Array whose elements are to be swapped.") ::
		    TacticField(
		        name = "index1",
		        displayName = "index1",
		        ftype = "Int",
		        description = "First index") ::
		    TacticField(
		        name = "index2",
		        displayName = "index2",
		        ftype = "Int",
		        description = "Second index") ::
		    Nil
	    val transfDiv = None
		val appConditions 		=
		    "The head node should be an UnknownProg." ::
		    "The proof obligation for the array swap statement should be valid." ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	object IntroAssignmentTacticDoc extends ProgramTacticDoc {
		type t = IntroAssignmentTactic
		val name 				= "IntroAssignment"
		val shortDescription    = "Introduce an assignment program"
		val tacticInputs        =
		    TacticField(
		        name = "lhsRhsTuples",
		        displayName = "Variable and term pairs",
		        ftype = "List[(Var, Program Term)]",
		        description = "Variables and the correpsonding program expressions that should be assigned to the variables.") ::
		    Nil
	    val transfDiv = None
		val appConditions 		=
		    "The head node should be an UnknownProg." ::
		    "The variables should be already defined." ::
		    "The term should be valid program expressions." ::
		    "The proof obligation for the assignment statement should be valid." ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	object InsertVariableTacticDoc extends ProgramTacticDoc {
	    type t = InsertVariableTactic
		val name 				= "InsertVariable"
		val shortDescription    = "Introduce a variable declaration before the active program."
		val tacticInputs        =
		    TacticField(
		        name = "aVar",
		        displayName = "New variable",
		        ftype = "Var",
		        description = "A new variable to be introduced.") ::
		    TacticField(
		        name = "initVal",
		        displayName = "Initial value",
		        ftype = "Term",
		        description = "Initial value to be assigned to the new variable") ::
		    Nil
	    val transfDiv = None
		val appConditions 		=
		    "Head node should be a program node" ::
		    "Variable name should be fresh" ::
		    "The initial value should be of same type as that of the variable type " ::
		    Nil
		val hint 				= ""
		val notes				= Nil

	}

	object StepIntoSubProgTacticDoc extends ProgramTacticDoc {
	    //class StepIntoSubProgTactic(displayId: java.lang.Integer) extends StepInTactic {
	    type t = StepIntoSubProgTactic
		val name 				= "StepIntoSubProg"
		val shortDescription    = "Step into the subprogram with given display id."
		val tacticInputs        =
		    TacticField(
		        name = "displayId",
		        displayName = "DisplayId",
		        ftype = "Int",
		        description = "DisplayId of the subprogram") ::
		    Nil
		val transfDiv = None
		val appConditions 		=
		    "Head node should be a program node" ::
		    "DisplayId should a valid program display id." ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	object ReplaceFormulaTacticDoc extends FormulaTacticDoc {
	    type t = ReplaceFormulaTactic
		val name 				= "ReplaceFormula"
		val shortDescription    = "Replace a formula with equivalent formula."
		val tacticInputs        =
		    TacticField(
		        name = "newFormula",
		        displayName = "New formula",
		        ftype = "Formula",
		        description = "New equivalent formula.") ::
		    Nil
		val transfDiv = None
		val appConditions 		=
		    "Head node should be a formula node." ::
		    "The head formula and the new formula should be equivalent." ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	//class InstantiateMetaTactic(primedVarTermList: List[(Var, Term)]) extends FunTactic {
	//Replaces the primed variables with the given terms
	object InstantiateMetaTacticDoc extends FormulaTacticDoc {
	    type t = InstantiateMetaTactic
		val name 				= "InstantiateMeta"
		val shortDescription    = "Instantiate the meta variables"
		val tacticInputs        =
		    TacticField(
		        name = "primedVarTermList",
		        displayName = "Meta variables and term pairs",
		        ftype = "List[(Var, Term)]",
		        description = "Meta variables and corresponding instantiation terms") ::
		    Nil
		val transfDiv = None
		val appConditions 		=
		    "Head node should be a formula node." ::
		    "The metavariable should be present in the context variables." ::
		    "the context assumptions should be metavariable free" ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	//class DeleteConjunctTactic(conjunct: TermBool, variant: Term)
	object DeleteConjunctTacticDoc extends ProgramTacticDoc {
	    type t = DeleteConjunctTactic
		val name 				= "DeleteConjunct"
		val shortDescription    =
		    """Introduces a while loop by applying the "Delete Conjuct" heuristics"""
		val tacticInputs =
		    TacticField(
		        name = "conjunct",
		        displayName = "Conjuct",
		        ftype = "Formula",
		        description = """Conjuct to be deleted to get the invariant.
		            Negation of this conjuct becomes the guard of the while loop""" ) ::
		    TacticField(
		        name = "variant",
		        displayName = "Variant",
		        ftype = "Integer Term",
		        description = "Variant of the while loop." ) ::
		    Nil
		val appConditions =
		  "Head node should be a program node" ::
		  Nil
		val hint = ""
		val notes = Nil

		val transfDiv = Some{
	      val A = VarBool("A")
	      val B = VarBool("B")
	      val C = VarBool("C")
	      val Inv = VarBool("B")
	      val V = VarInt("V")

	      val paramDiv: Elem = {
	        val deleteConjunctTactic = new DeleteConjunctTactic(C, V)
	        deleteConjunctTactic.getHint()
	      }

	      //val grdcmds =
	      val iProg = mkUnknownProg(A.inv(), 1, (B && C).inv)
	      val oProg = mkComposition(
			pre = A.inv(),
			programs = List(
				mkUnknownProg(A.inv(), 2, Inv.inv),
				mkWhileProg(
					pre = Inv.inv,
					loopInv = Some(Inv),
					grdcmds = List(GuardedCmd(!C,
						mkUnknownProg((Inv && !C).inv, 3, Inv.inv))),
					post = (B && C).inv)),
			post = (B && C).inv)
			<div class="transfDiv content">
				{paramDiv}
				<div class="inputnode">
				{XHTMLPrinters2.programAnnToHtml(iProg)(None)}
	        	</div>
				<div class="outputnode">
				{XHTMLPrinters2.programAnnToHtml(oProg)(None)}
	        	</div>
			</div>
	    }
	}

	//class RTVInPostTactic(constant: Term, variable: Var, initValue: Term, bounds: TermBool)
	object RTVInPostTacticDoc extends ProgramTacticDoc {
	    type t = RTVInPostTactic
		val name 				= "RTVInPost"
		val shortDescription    = "Replaces a term in the postcondition by a new variable."
		val tacticInputs =
		    TacticField(
		        name = "constant",
		        ftype = "Term",
		        displayName = "Term to be replaced",
		        description = "Term to be replaced by a fresh variable." ) ::
		    TacticField(
		        name = "variable",
		        ftype = "Var",
		        displayName = "New Variable",
		        description = "A new variable which will replace the term." ) ::
		    TacticField(
		        name = "initValue",
		        ftype = "Term",
		        displayName = "Initial value",
		        description = "Initial value for the variable." ) ::
		    TacticField(
		        name = "bounds",
		        ftype = "Formula",
		        displayName = "Bounds",
		        description = "Bounds on the new variable." ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a program node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}
    //class StrengthenInvariantTactic(newInvs: List[TermBool])
	object StrengthenInvariantTacticDoc extends ProgramTacticDoc {
	    type t = StrengthenInvariantTactic
		val name 				= "StrengthenInvariant"
		val shortDescription    = "Strengthens invariant of a while loop"
		val tacticInputs =
		    TacticField(
		        name = "newInvs",
		        displayName = "Additional Loop Invariants",
		        ftype = "List[Formula]",
		        description = "Additional loop invariants for the while loop." ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a While program node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class EmptyRangeTactic(displayId: Int) extends FunTactic {
	object EmptyRangeTacticDoc extends FormulaTacticDoc {
	    type t = EmptyRangeTactic
		val name 				= "EmptyRange"
		val shortDescription    = "EmptyRange"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class OnePointTactic(displayId: Int) extends FunTactic {
	object OnePointTacticDoc extends FormulaTacticDoc {
	    type t = OnePointTactic
		val name 				= "OnePoint"
		val shortDescription    = "OnePoint"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "Id of the quantified formula" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class RangeSplitTactic(displayId: Int) extends FunTactic {
	object RangeSplitTacticDoc extends FormulaTacticDoc {
	    type t = RangeSplitTactic
		val name 				= "RangeSplit"
		val shortDescription    = "RangeSplit"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "Id of the quantified formula" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class DistributivityTactic (displayId: java.lang.Integer) extends FunTactic {
	object DistributivityTacticDoc  extends FormulaTacticDoc {
	    type t = DistributivityTactic
		val name 				= "Distributivity"
		val shortDescription    = "Distributivity"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "Id of the formula to which the tactic is to be applied." ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class TradingMoveToTermTactic(displayId: Int, termToBeMovedId: Int) extends FunTactic {
	object TradingMoveToTermTacticDoc extends FormulaTacticDoc {
	    type t = TradingMoveToTermTactic
		val name 				= "TradingMoveToTerm"
		val shortDescription    = "TradingMoveToTerm"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "Id of the quantified formula" ) ::
		    TacticField(
		        name = "termToBeMovedId",
		        ftype = "Int",
		        displayName = "Id of the formula to be moved",
		        description = "Id of the subformula that needs to be moved from Range to the Term" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class QDistributivityTactic (displayId: Int) extends FunTactic {
	object QDistributivityTacticDoc extends FormulaTacticDoc {
	    type t = QDistributivityTactic
		val name 				= "QDistributivity"
		val shortDescription    = "QDistributivity"
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Int",
		        displayName = "Id of the formula",
		        description = "Id of the quantified formula" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class SimplifyTactic() extends FunTactic {
	object SimplifyTacticDoc extends FormulaTacticDoc {
	    type t = SimplifyTactic
		val name 				= "Simplify"
		val shortDescription    = """Simplifies the formula by eliminating "True" and "False" subformulas """
		val tacticInputs = Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class StepIntoUnknownProgIdxTactic(idx: java.lang.Integer) extends StepInTactic {
	object StepIntoUnknownProgIdxTacticDoc extends ProgramTacticDoc {
	    type t = StepIntoUnknownProgIdxTactic
		val name 				= "StepIntoUnknownProgIdx"
		val shortDescription    = """Steps into the "Idx"th UnknwonProgram"""
		val tacticInputs =
		    TacticField(
		        name = "idx",
		        ftype = "Int",
		        displayName = "Idx of the formula",
		        description = "Idx of the formula" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
  		  "Head node should be a program node" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

	//class StepIntoPO() extends StepInTactic {
	object StepIntoPODoc extends ProgramTacticDoc {
	    type t = StepIntoPO
		val name 				= "StepIntoPO"
		val shortDescription    = "StepIntoPO"
		val tacticInputs =
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be the StartAsgnDerivation node"::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class StepIntoSubFormulaTactic(subId: Int) extends StepInTactic with StepIntoSubTerm {
	object StepIntoSubFormulaTacticDoc extends ProgramTacticDoc {
	    type t = StepIntoSubFormulaTactic
		val name 				= "StepIntoSubFormula"
		val shortDescription    = "StepIntoSubFormula"
		val tacticInputs =
		    TacticField(
		        name = "subId",
		        ftype = "Int",
		        displayName = "subId",
		        description = "Id of the subformula" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class StartAsgnDerivationTactic(lhsVars: List[Var]) extends StepInTactic {
	object StartAsgnDerivationTacticDoc extends ProgramTacticDoc {
	    type t = StartAsgnDerivationTactic
		val name 				= "StartAsgnDerivation"
		val shortDescription    = "Starts derivation of assignment program construct"
		val tacticInputs =
		    TacticField(
		        name = "lhsVars",
		        ftype = "List[Var]",
		        displayName = "lhsVars",
		        description = "List of variables for which you want to derive an assignment program" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be an UnknownProgram node" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

//	//class StartIfDerivationTactic(lhsVars: List[Var]) extends StepInTactic {
//	object StartIfDerivationTacticDoc extends ProgramTacticDoc {
//	    type t = StartIfDerivationTactic
//		val name 				= "StartIfDerivation"
//		val shortDescription    = """Starts derivation for a "If" program construct"""
//		val tacticInputs =
//		    TacticField(
//		        name = "lhsVars",
//		        ftype = "List[Var]",
//		        displayName = "lhsVars",
//		        description = """List of lvariables in the "If" program""" ) ::
//		    Nil
//		val transfDiv = None
//		val appConditions =
//		  "Head node should be an UnknownProgram node" ::
//		  Nil
//
//		val hint = ""
//		val notes = Nil
//	}

	//class StartGCmdDerivationTactic() extends StepInTactic {
	object StartGCmdDerivationTacticDoc extends ProgramTacticDoc {
	    type t = StartGCmdDerivationTactic
		val name 				= "StartGCmdDerivation"
		val shortDescription    = """Starts assignment of a guarded command in the derivation of an "If" construct """
		val tacticInputs =
		    Nil
		val transfDiv = None
		val appConditions =
		  "Should follow the StartIfDerivation tactic application" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class StepOutTactic() extends Tactic {
	object StepOutTacticDoc extends TacticDoc {
	    type t = StepOutTactic
		val name 				= "StepOut"
		val shortDescription    = "StepOut"
		val tacticInputs =
		    Nil
		val transfDiv = None
		val appConditions =
		  "There should be some ancestor StepIn node that is not yet stepped out." ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class AssumePreTactic(freshVariables: List[Var], assumedPre: TermBool) extends FunTactic
	object AssumePreTacticDoc extends TacticDoc {
	    type t = AssumePreTactic
		val name 				= "AssumePre"
		val shortDescription    = """Assumes a precondition in a formula mode.
									An UnknownProg is created on stepout to establish the precondition."""
		val tacticInputs =
		    TacticField(
		        name = "freshVariables",
		        ftype = "List[Var]",
		        displayName = "Fresh Variables",
		        description = "Fresh variables to be introduced." ) ::
		    TacticField(
		        name = "assumedPre",
		        ftype = "Formula",
		        displayName = "Assumed Precondition",
		        description = "A precondition to be assumed." ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node" ::
		  """Head node should be part of "If" program derivation""" ::
		  Nil
		val hint = ""
		val notes = Nil
	}

	//class GuessGuardTactic(guard: TermBool) extends FunTactic
	//deprecated
	object GuessGuardTacticDoc extends TacticDoc {
	    type t = GuessGuardTactic
		val name 				= "GuessGuard"
		val shortDescription    = """Guess a guard in the formula mode.
									Strengthens the already existing guard with the new guard."""
		val tacticInputs =
		    TacticField(
		        name = "guard",
		        ftype = "Formula",
		        displayName = "Guard",
		        description = "Guard to be added to the already existing guard" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		    "Head node should be a formula node" ::
		    """Guard should be a valid program expression""" ::
		    Nil
		val hint = ""
		val notes = Nil
	}

	//RTVInPost2Tactic(displayId: Int, variable: Var, initValue: Term, bounds: TermBool) extends FunTactic {
	object RTVInPost2TacticDoc extends TacticDoc {
	    type t = RTVInPost2Tactic
	    val name 				= "RTVInPost2"
		val shortDescription    = """Replaces a specific occurrence of a term in the postcondition by a new variable."""
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        displayName = "DisplayId of a term in postcondition",
		        ftype = "Int",
		        description = "DisplayId of the term to be replaced by a fresh variable") ::
		    TacticField(
		        name = "variable",
		        ftype = "Var",
		        displayName = "New Variable",
		        description = "A new variable which will replace the term." ) ::
		    TacticField(
		        name = "initValue",
		        ftype = "Term",
		        displayName = "Initial value",
		        description = "Initial value for the variable." ) ::
		    TacticField(
		        name = "bounds",
		        ftype = "Formula",
		        displayName = "Bounds",
		        description = "Bounds on the new variable." ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a program node" ::
		  Nil
		val hint = ""
		val notes = Nil
	}
	//StepIntoBATactic(lhsVars: List[Var]) extends StepInTactic {
	object StepIntoBATacticDoc extends TacticDoc {
	    type t = StepIntoBATactic
		val name 				= "StepIntoBA"
		val shortDescription    = """Steps into Before-After predicate for the program"""
		val tacticInputs =
		    TacticField(
		        name = "lhsVars",
		        ftype = "List[Var]",
		        displayName = "lhsVars",
		        description = """List of lvariables in the desired program""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be an UnknownProgram node" ::
		  Nil

		val hint = ""
		val notes =
			"""In the formula mode, use GuessGuard tactic to guess the guards of the "If". """ +
			    """If no guards are guessed then assignment program is constructed on Stepout. """ ::
			"Use InstantiateMeta tactic to instantiate the meta variables" ::
			"""You can stepout even when you have not instantiated all the metavariables.""" +
			"""In this case, an unknown program is created before the synthesized program to establish the remaining metavariables""" ::
			Nil
	}
	//StepIntoIFBATactic(lhsVars: List[Var]) extends StepInTactic {
	object StepIntoIFBATacticDoc extends TacticDoc {
	    type t = StepIntoIFBATactic
	    val name 				= "StepIntoIFBA"
		val shortDescription    = """Steps into Before-After predicate of the IF program. Used to derive additional guarded commands."""
		val tacticInputs =
		    TacticField(
		        name = "lhsVars",
		        ftype = "List[Var]",
		        displayName = "lhsVars",
		        description = """List of lvariables in the desired guarded commands""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be If Program node" ::
		  Nil

		val hint = ""
		val notes =
		    """This tactic is useful when the guards of a If program do not cover all the cases and you want to derive additional guarded commands.""" ::
			"""Derived guarded commands are added to the if construct on stepout.""" ::
			"Use InstantiateMeta tactic to instantiate the meta variables" ::
			"""You can stepout even when you have not instantiated all the metavariables. """ +
			    """In this case, an unknown program is created before the synthesized program to establish the remaining metavariables""" ::
			Nil
	}

	//IntroIfTactic(guards: List[TermBool]) extends FunTactic {
	object IntroIfTacticDoc extends TacticDoc {
	    type t = IntroIfTactic
	    val name 				= "IntroIf"
		val shortDescription    = """Introduces an IF program construct"""
		val tacticInputs =
		    TacticField(
		        name = "guards",
		        ftype = "List[Formula]",
		        displayName = "guards",
		        description = """List of guards in the IF program""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be an Unknown Program node" ::
		  Nil

		val hint = ""
		val notes = """If the given guards do not cover all the cases, then additional guarded commands can be derived using the StepIntoIFBA tactic.""" :: Nil
	}

	//AssumeToIfTactic(displayId: Integer) extends FunTactic
	object AssumeToIfTacticDoc extends ProgramTacticDoc {
	    type t = AssumeToIfTactic
	    val name 				= "AssumeToIf"
		val shortDescription    = """Introduces an IF program construct"""
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Integer",
		        displayName = "displayId",
		        description = """Assume program Id""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a Program node" ::
		  Nil

		val hint = ""
		val notes = """Introduces an IF program construct to establish the assumption""" :: Nil
	}

	object CollapseCompositionsTacticDoc extends ProgramTacticDoc {
	    type t = CollapseCompositionsTactic
	    val name 				= "CollapseCompositions"
		val shortDescription    = """Collapses all the nested compositions"""
		val tacticInputs =
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a a program with atleast one nested composition" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

	object IntroAssignmentEndTacticDoc extends ProgramTacticDoc {
		type t = IntroAssignmentEndTactic
		val name 				= "IntroAssignmentEnd"
		val shortDescription    = "Introduces an unknown program and the given assignment program"
		val tacticInputs        =
		    TacticField(
		        name = "lhsRhsTuples",
		        displayName = "Variable and term pairs",
		        ftype = "List[(Var, Program Term)]",
		        description = "Variables and the correpsonding program expressions that should be assigned to the variables.") ::
		    Nil
	    val transfDiv = None
		val appConditions 		=
		    "The head node should be an UnknownProg" ::
		    "The variables should be already declared" ::
		    Nil
		val hint 				= ""
		val notes				= Nil
	}

	object PropagateAssertionsDownSPTacticDoc extends ProgramTacticDoc {
	    type t = PropagateAssertionsDownSPTactic
	    val name 				= "PropagateAssertionsDownSP"
		val shortDescription    = """Propagate assertions down"""
		val tacticInputs =
		    TacticField(
		        name = "displayId1",
		        ftype = "Integer",
		        displayName = "displayId1",
		        description = """Display Id of the first program""" ) ::
        	TacticField(
        			name = "displayId2",
        			ftype = "Integer",
        			displayName = "displayId2",
        			description = """Display Id of the first program""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a program node." ::
		  Nil

		val hint = ""
		val notes = """Propagates the pre of the subprogram with displayId1 to the post of the subprogram with displayId2""" :: Nil
	}

	object PropagateAssumeUpTacticDoc extends ProgramTacticDoc {
	    type t = PropagateAssumeUpTactic
	    val name 				= "PropagateAssumeUp"
		val shortDescription    = """Propages assume statements upwards"""
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Integer",
		        displayName = "displayId",
		        description = """Display Id of the Assume statement to be propagated.""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a program node" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

	object ReplaceSubTermTacticDoc extends TacticDoc {
	    type t = ReplaceSubTermTactic
	    val name 				= "ReplaceSubTerm"
		val shortDescription    = """Replaces a subterm with an equivalent term"""
		//subTermId: Int, newSubTerm: Term
		val tacticInputs =
		    TacticField(
		        name = "subTermId",
		        ftype = "Int",
		        displayName = "subTermId",
		        description = """Display id of the subterm to be replaced""" ) ::
		    TacticField(
		        name = "newSubTerm",
		        ftype = "Term",
		        displayName = "newSubTerm",
		        description = """New subterm""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a formula node." ::
		  Nil

		val hint = ""
		val notes = Nil
	}

	object SplitoutBoundVariableTacticDoc extends TacticDoc {
	    type t = SplitoutBoundVariableTactic
	    val name 				= "SplitoutBoundVariable"
		val shortDescription    = """Splitout a bound variable from the given quantified term"""
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Integer",
		        displayName = "displayId",
		        description = """Display id of the target term""" ) ::
        	TacticField(
        			name = "boundVar",
        			ftype = "Integer",
        			displayName = "boundVar",
        			description = """Bound variable""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a quantified formula" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

	object StrengthenPostSPTacticDoc extends ProgramTacticDoc {
	    type t = StrengthenPostSPTactic
	    val name 				= "StrengthenPostSP"
		val shortDescription    = """Strengthens the post of the program with strongest postcondition."""
		val tacticInputs =
		    TacticField(
		        name = "displayId",
		        ftype = "Integer",
		        displayName = "displayId",
		        description = """Display if of the program""" ) ::
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a todo" ::
		  Nil

		val hint = ""
		val notes = """ Strengthens the post of the program with strongest postcondition.""" ::
		    """Updates the pre and post of all the intermediate programs.""" ::
		    Nil
	}

	object WhileStrInvSPTacticDoc extends ProgramTacticDoc {
	    type t = WhileStrInvSPTactic
	    val name 				= "WhileStrInvSP"
		val shortDescription    = """Propagate the assume statement at end the while body and then strengthen the invariant"""
		val tacticInputs =
		    Nil
		val transfDiv = None
		val appConditions =
		  "Head node should be a while program" ::
		  Nil

		val hint = ""
		val notes = Nil
	}

    private val progTacticDocs: List[TacticDoc] =
        AssumeToIfTacticDoc ::
        CollapseCompositionsTacticDoc ::
    	DeleteConjunctTacticDoc ::
    	Init4TacticDoc	 ::
    	InsertVariableTacticDoc ::
    	IntroAssignmentTacticDoc ::
    	IntroAssignmentEndTacticDoc ::
    	IntroIfTacticDoc ::
    	IntroSwapTacticDoc ::
    	PropagateAssertionsDownSPTacticDoc::
    	PropagateAssumeUpTacticDoc ::
    	RTVInPostTacticDoc ::
    	RTVInPost2TacticDoc ::
    	StepIntoBATacticDoc ::
    	StepIntoIFBATacticDoc ::
    	StepIntoSubProgTacticDoc ::
    	StepIntoUnknownProgIdxTacticDoc ::
    	StrengthenInvariantTacticDoc ::
    	StrengthenPostSPTacticDoc ::
    	WhileStrInvSPTacticDoc ::
    	Nil

    private val formulaTacticDocs: List[TacticDoc] =
        AssumePreTacticDoc ::
    	DistributivityTacticDoc ::
    	EmptyRangeTacticDoc	 ::
    	InstantiateMetaTacticDoc ::
    	OnePointTacticDoc ::
    	QDistributivityTacticDoc ::
    	RangeSplitTacticDoc ::
    	ReplaceFormulaTacticDoc ::
    	ReplaceSubFormulaTacticDoc ::
    	ReplaceSubTermTacticDoc ::
    	SimplifyAutoTacticDoc ::
    	SimplifyTacticDoc ::
    	SplitoutBoundVariableTacticDoc ::
    	StepIntoSubFormulaTacticDoc ::
    	TradingMoveToTermTacticDoc ::
    	Nil

    private val otherTacticDocs: List[TacticDoc] =
    	StepOutTacticDoc ::
    	Nil

	def mkTOC(): Elem = {

        def mkLink(name: String): Elem = {
           	<div class="item"><a href={'#' + name}>{name}</a></div>
        }
        def mkHeader(className: String, content: String) = {
            <h3 class={ s"ui header $className" }> {content} </h3>
        }

        def mkList(tacticDocs: List[TacticDoc]) = {
			<div class="ui celled list">
                { tacticDocs.map{ doc => mkLink(doc.name)} }
			</div>
        }

	    <div>
			{mkHeader("ProgTactics", "Program Tactics")}
			{mkList(progTacticDocs)}

            {mkHeader("FormulaTactics", "Formula Tactics")}
			{mkList(formulaTacticDocs)}

            {mkHeader("OtherTactics", "Other Tactics")}
			{mkList(otherTacticDocs)}
		</div>
	}

    def getHtml(): Elem = {

        def mkHeader(headerStr: String) = {
            <h2 class='ui header'>{ headerStr }</h2>
        }

   	    <html>
   	     <head>
   			<link rel="stylesheet" type="text/css" href="tactics.css"> </link>
			<link rel="stylesheet" type="text/css" href="../javascripts/lib/Semantic-UI-CSS/semantic.min.css"> </link>
   		</head>
   		<body>
			<div id='sidebar'>
				{ mkTOC()}
			</div>
			<div id='contentpanel'>
		        <div class='ProgTactics'>
					{ mkHeader("Program Tactics") }
   					{ progTacticDocs.map(_.toHtml) }
   				</div>

		        <div class='FormulaTactics'>
					{ mkHeader("Formula Tactics") }
	        	    { formulaTacticDocs.map(_.toHtml) }
				</div>

        		<div class='OtherTactics'>
					{ mkHeader("Other Tactics") }
        	        { otherTacticDocs.map(_.toHtml) }
				</div>
			</div>
   		</body>
   		</html>
    }
}
