package jsonprinter

import tactics._
import progsynth.synthesisnew.Tactic
import progsynth.synthesisnew._
import models.mqprinter.MQPrinter._
import jsonprinter.tactics.RTVInPostTV
import jsonprinter.tactics.StepIntoBATV
import jsonprinter.tactics.StepIntoSubFormulaTV
import jsonprinter.tactics.SplitoutBoundVariableTV
import jsonprinter.tactics.OnePointTV
import jsonprinter.tactics.EmptyRangeTV
import jsonprinter.tactics.IntroIfTV
import jsonprinter.tactics.AssumePreTV
import jsonprinter.tactics.ReplaceSubFormulaTV
import jsonprinter.tactics.ReplaceSubTermTV

object TacticTVMapper {
	def getTacticTV(tactic: Tactic): TV =  {
		val concreteTV: TV = tactic match {
			case ct : Init4Tactic =>
				Init4TV(ct.name, ct.immutableVars, ct.mutableVars, ct.globalInvs, ct.preF, ct.postF);
			
			case ct: DeleteConjunctTactic =>
				DeleteConjunctTV(ct.conjunct, ct.variant)
				
			case ct: StepIntoUnknownProgIdxTactic =>
				new StepIntoUnknownProgIdxTV(ct.idx)
				
			case ct: IntroAssignmentTactic =>
				IntroAssignmentTV(ct.lhsRhsTuples)
				
			case ct: StepOutTactic =>
				new StepOutTV()

			case ct: RTVInPostTactic =>
				new RTVInPostTV(ct.constant, ct.variable, ct.initValue, ct.bounds)

			case ct: RTVInPost2Tactic =>
				new RTVInPost2TV(ct.displayId, ct.variable, ct.initValue, ct.bounds)
				
			case ct: StepIntoBATactic =>
				new StepIntoBATV(ct.lhsVars)

			case ct: StepIntoSubFormulaTactic =>
				new StepIntoSubFormulaTV(ct.subId)
			
			case ct: ReplaceSubFormulaTactic =>
				new ReplaceSubFormulaTV(ct.oldSubFId, ct.newSubF)

			case ct: SplitoutBoundVariableTactic =>
				new SplitoutBoundVariableTV(ct.displayId, ct.boundVar)
			
			case ct: OnePointTactic =>
				new OnePointTV(ct.displayId)

			case ct: EmptyRangeTactic =>
				new EmptyRangeTV(ct.displayId)

			case ct: RangeSplitTactic =>
				new RangeSplitTV(ct.displayId)

			case ct: SimplifyTactic =>
				new SimplifyTV()

			case ct: SimplifyAutoTactic =>
				new SimplifyAutoTV()

			case ct: QDistributivityTactic =>
				new QDistributivityTV(ct.displayId)
			
			case ct: AssumeToIfTactic =>
				new AssumeToIfTV(ct.displayId)
			
			case ct: CollapseCompositionsTactic =>
				new CollapseCompositionsTV()
			
			case ct: DistributivityTactic =>
				new DistributivityTV(ct.displayId)
			
			case ct: PropagateAssumeUpTactic =>
				new PropagateAssumeUpTV(ct.displayId)
			
			case ct: PropagateAssertionsDownSPTactic =>
				new PropagateAssertionsDownSPTV(ct.displayId1, ct.displayId2)

			case ct: GuessGuardTactic =>
				GuessGuardTV(ct.guard)
				
			case ct: InsertVariableTactic =>
				new InsertVariableTV(ct.aVar, ct.initVal)

			case ct: InstantiateMetaTactic =>
				InstantiateMetaTV(ct.primedVarTermList)

			case ct: IntroAssignmentEndTactic =>
				IntroAssignmentEndTV(ct.lhsRhsTuples)

			case ct: IntroIfTactic =>
				IntroIfTV(ct.guards)

			case ct: IntroSwapTactic =>
				IntroSwapTV(ct.array, ct.index1, ct.index2)
				
			case ct: AssumePreTactic =>
				new AssumePreTV(ct.freshVariables, ct.assumedPre)
 
			case ct: ReplaceFormulaTactic =>
				ReplaceFormulaTV(ct.newFormula)
				
			case ct: ReplaceSubTermTactic =>
				new ReplaceSubTermTV(ct.subTermId, ct.newSubTerm)
												
			case ct: StepIntoIFBATactic =>
				new StepIntoIFBATV(ct.lhsVars)

			case ct: StepIntoSubProgTactic =>
				new StepIntoSubProgTV(ct.displayId)

			case ct: StrengthenInvariantTactic =>
				StrengthenInvariantTV(ct.newInvs)

			case ct: TradingMoveToTermTactic =>
				new TradingMoveToTermTV(ct.displayId, ct.termToBeMovedId)

			case ct: WhileStrInvSPTactic =>
				new WhileStrInvSPTV()

			case _ =>
				throw new RuntimeException("TacticTVMapper not implemented for tactic: " + tactic.tName)
		}
		
		new TacticTV(concreteTV)
	}
}