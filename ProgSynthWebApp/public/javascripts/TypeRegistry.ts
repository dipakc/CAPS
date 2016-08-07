import typeViews = require("TypeViews"); typeViews;
import typeTVs = require("TypeTVs"); typeTVs;
var log4javascript = require('log4javascript');

var tvNameTVMap = {};
var logger = log4javascript.getLogger("webapp.TypeRegistry");

function registerClass(className: string, classFn: any) {
    tvNameTVMap[className] = classFn;
}

export function getClass(className: string): any {
    return tvNameTVMap[className];
}

export function createView(aTV: typeTVs.TV): typeViews.PSView {
    //var clsName = Describer.getClassName(aTV);
    var clsName = aTV.tvName;
    var viewClsName = clsName.replace(/TV$/, "View");
    return new tvNameTVMap[viewClsName](aTV);
}

export function createViewFromTVName(tvName: string): typeViews.PSView {
    var viewName = tvName.replace(/TV$/, "View");
    var tvClass = getClass(tvName);
    var viewClass = getClass(viewName);

    var tvClassObj = new tvClass();
    return new viewClass(tvClassObj);
}

function registerTVs() {
    /**/logger.trace("registering TVs...");
    registerClass('EnumTV', typeTVs.EnumTV);
    registerClass('EnumView', typeViews.EnumView);
    registerClass('PSTypeTV', typeTVs.PSTypeTV);
    registerClass('PSTypeView', typeViews.PSTypeView);
    registerClass('NewVarTV', typeTVs.NewVarTV);
    registerClass('NewVarView', typeViews.NewVarView);
    registerClass('FieldTV', typeTVs.FieldTV);
    registerClass('FieldView', typeViews.FieldView);
    registerClass('IntegerTV', typeTVs.IntegerTV);
    registerClass('IntegerView', typeViews.IntegerView);
    registerClass('FDisplayIdTV', typeTVs.FDisplayIdTV);
    registerClass('FDisplayIdView', typeViews.FDisplayIdView);
    registerClass('PDisplayIdTV', typeTVs.PDisplayIdTV);
    registerClass('PDisplayIdView', typeViews.PDisplayIdView);
    registerClass('StringTV', typeTVs.StringTV);
    registerClass('StringView', typeViews.StringView);
    registerClass('TermView', typeViews.TermView);
    registerClass('TermTV', typeTVs.TermTV);
    registerClass('VarView', typeViews.VarView);
    registerClass('VarTV', typeTVs.VarTV);
    registerClass('FOLFormulaView', typeViews.FOLFormulaView);
    registerClass('FOLFormulaTV', typeTVs.FOLFormulaTV);
    registerClass('TermBoolView', typeViews.TermBoolView);
    registerClass('TermBoolTV', typeTVs.TermBoolTV);
    registerClass('InitView', typeViews.InitView);
    registerClass('InitTV', typeTVs.InitTV);
    registerClass('StepIntoUnknownProgIdxView', typeViews.StepIntoUnknownProgIdxView);
    registerClass('StepIntoUnknownProgIdxTV', typeTVs.StepIntoUnknownProgIdxTV);
    registerClass('RetValView', typeViews.RetValView);
    registerClass('RetValTV', typeTVs.RetValTV);
    registerClass('RTVInPostView', typeViews.RTVInPostView);
    registerClass('RTVInPostTV', typeTVs.RTVInPostTV);
    registerClass('DeleteConjunctView', typeViews.DeleteConjunctView);
    registerClass('DeleteConjunctTV', typeTVs.DeleteConjunctTV);
    registerClass('StepOutView', typeViews.StepOutView);
    registerClass('StepOutTV', typeTVs.StepOutTV);
    registerClass('WhileStrInvSPView', typeViews.WhileStrInvSPView);
    registerClass('WhileStrInvSPTV', typeTVs.WhileStrInvSPTV);
    registerClass('AssumeToIfView', typeViews.AssumeToIfView);
    registerClass('AssumeToIfTV', typeTVs.AssumeToIfTV);
    registerClass('PropagateAssumeUpView', typeViews.PropagateAssumeUpView);
    registerClass('PropagateAssumeUpTV', typeTVs.PropagateAssumeUpTV);
    registerClass('StrengthenPostSPView', typeViews.StrengthenPostSPView);
    registerClass('StrengthenPostSPTV', typeTVs.StrengthenPostSPTV);
    registerClass('PropagateAssertionsDownSPView', typeViews.PropagateAssertionsDownSPView);
    registerClass('PropagateAssertionsDownSPTV', typeTVs.PropagateAssertionsDownSPTV);
    registerClass('SimplifyAutoTV', typeTVs.SimplifyAutoTV);
    registerClass('SimplifyAutoView', typeViews.SimplifyAutoView);
    registerClass('SimplifyTV', typeTVs.SimplifyTV);
    registerClass('SimplifyView', typeViews.SimplifyView);
    registerClass('ListView', typeViews.ListView);
    registerClass('ListTV', typeTVs.ListTV);
    registerClass('TupleView', typeViews.TupleView);
    registerClass('TupleTV', typeTVs.TupleTV);
    registerClass('IntroAssignmentView', typeViews.IntroAssignmentView);
    registerClass('IntroAssignmentTV', typeTVs.IntroAssignmentTV);
    registerClass('IntroAssignmentEndView', typeViews.IntroAssignmentEndView);
    registerClass('IntroAssignmentEndTV', typeTVs.IntroAssignmentEndTV);
    registerClass('Init4View', typeViews.Init4View);
    registerClass('Init4TV', typeTVs.Init4TV);
    registerClass('MagicView', typeViews.MagicView);
    registerClass('MagicTV', typeTVs.MagicTV);
    registerClass('AssumePreView', typeViews.AssumePreView);
    registerClass('AssumePreTV', typeTVs.AssumePreTV);
    registerClass('StartIfDerivationView', typeViews.StartIfDerivationView);
    registerClass('StartIfDerivationTV', typeTVs.StartIfDerivationTV);
    registerClass('InstantiateMetaView', typeViews.InstantiateMetaView);
    registerClass('InstantiateMetaTV', typeTVs.InstantiateMetaTV);
    registerClass('ReplaceFormulaView', typeViews.ReplaceFormulaView);
    registerClass('ReplaceFormulaTV', typeTVs.ReplaceFormulaTV);
    registerClass('GuessGuardView', typeViews.GuessGuardView);
    registerClass('GuessGuardTV', typeTVs.GuessGuardTV);
    registerClass('StartGCmdDerivationView', typeViews.StartGCmdDerivationView);
    registerClass('StartGCmdDerivationTV', typeTVs.StartGCmdDerivationTV);
    ///
    registerClass('TacticTV', typeTVs.TacticTV);
    registerClass('TacticView', typeViews.TacticView);

    registerClass('DistributivityTV', typeTVs.DistributivityTV);
    registerClass('DistributivityView', typeViews.DistributivityView);
    registerClass('EmptyRangeTV', typeTVs.EmptyRangeTV);
    registerClass('EmptyRangeView', typeViews.EmptyRangeView);
    registerClass('OnePointTV', typeTVs.OnePointTV);
    registerClass('OnePointView', typeViews.OnePointView);
    registerClass('QDistributivityTV', typeTVs.QDistributivityTV);
    registerClass('QDistributivityView', typeViews.QDistributivityView);
    registerClass('RangeSplitTV', typeTVs.RangeSplitTV);
    registerClass('RangeSplitView', typeViews.RangeSplitView);
    registerClass('ReplaceSubformulaTV', typeTVs.ReplaceSubformulaTV);
    registerClass('ReplaceSubformulaView', typeViews.ReplaceSubformulaView);
    registerClass('ReplaceSubTermTV', typeTVs.ReplaceSubTermTV);
    registerClass('ReplaceSubTermView', typeViews.ReplaceSubTermView);
    registerClass('StartAsgnDerivationTV', typeTVs.StartAsgnDerivationTV);
    registerClass('StartAsgnDerivationView', typeViews.StartAsgnDerivationView);
    registerClass('StepIntoPOTV', typeTVs.StepIntoPOTV);
    registerClass('StepIntoPOView', typeViews.StepIntoPOView);
    registerClass('StepIntoProgIdTV', typeTVs.StepIntoProgIdTV);
    registerClass('StepIntoProgIdView', typeViews.StepIntoProgIdView);
    registerClass('StepIntoSubProgTV', typeTVs.StepIntoSubProgTV);
    registerClass('StepIntoSubProgView', typeViews.StepIntoSubProgView);
    registerClass('StepIntoSubFormulaTV', typeTVs.StepIntoSubFormulaTV);
    registerClass('StepIntoSubFormulaView', typeViews.StepIntoSubFormulaView);
    registerClass('StrengthenInvariantTV', typeTVs.StrengthenInvariantTV);
    registerClass('StrengthenInvariantView', typeViews.StrengthenInvariantView);
    registerClass('TradingMoveToTermTV', typeTVs.TradingMoveToTermTV);
    registerClass('TradingMoveToTermView', typeViews.TradingMoveToTermView);
    registerClass('UseAssumptionsTV', typeTVs.UseAssumptionsTV);
    registerClass('UseAssumptionsView', typeViews.UseAssumptionsView);
    registerClass('Init3TV', typeTVs.Init3TV);
    registerClass('Init3View', typeViews.Init3View);
    registerClass('InsertVariableTV', typeTVs.InsertVariableTV);
    registerClass('InsertVariableView', typeViews.InsertVariableView);
    registerClass('RTVInPost2TV', typeTVs.RTVInPost2TV);
    registerClass('RTVInPost2View', typeViews.RTVInPost2View);
    registerClass('SplitoutBoundVariableTV', typeTVs.SplitoutBoundVariableTV);
    registerClass('SplitoutBoundVariableView', typeViews.SplitoutBoundVariableView);
    registerClass('StepIntoBATV', typeTVs.StepIntoBATV);
    registerClass('StepIntoBAView', typeViews.StepIntoBAView);
    registerClass('StepIntoIFBATV', typeTVs.StepIntoIFBATV);
    registerClass('StepIntoIFBAView', typeViews.StepIntoIFBAView);
    registerClass('IntroSwapTV', typeTVs.IntroSwapTV);
    registerClass('IntroSwapView', typeViews.IntroSwapView);
    registerClass('IntroIfTV', typeTVs.IntroIfTV);
    registerClass('IntroIfView', typeViews.IntroIfView);
    registerClass('CollapseCompositionsTV', typeTVs.CollapseCompositionsTV);
    registerClass('CollapseCompositionsView', typeViews.CollapseCompositionsView);
}

export var init = _.once(registerTVs);
