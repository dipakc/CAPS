package jsonprinter
package tactics

class TacticTV(concreteTV: TV) extends AbstractTV(
	List("StepIntoUnknownProgIdxTV", 
    "RTVInPost", "DeleteConjunct", "StepOut", "SimplifyAuto", "Simplify", "IntroAssignment",
    "IntroAssignmentEnd", "Init4", "InstantiateMeta", "ReplaceFormula", "GuessGuard",
    "Distributivity", "EmptyRange", "OnePoint", "QDistributivity", "RangeSplit", "ReplaceSubformula",
    "ReplaceSubTerm", "StepIntoPO","StepIntoSubProg","StepIntoSubFormula","StrengthenInvariant",
    "TradingMoveToTerm", "InsertVariable", "AssumePre", "RTVInPost2", "SplitoutBoundVariable",
    "StepIntoBA", "StepIntoIFBA", "IntroSwap", "IntroIf", "WhileStrInvSP", "AssumeToIf",
    "PropagateAssumeUp", "PropagateAssertionsDownSP", "StrengthenPostSP", "CollapseCompositions").sorted,
    concreteTV) {

	override val tvName = "TacticTV"
}