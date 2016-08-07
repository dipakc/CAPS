
import templateMgr = require("TemplateMgr"); templateMgr;
import stm = require("SynthtreeModel"); stm;
import typeTvs = require("TypeTVs"); typeTvs;
import tpeReg = require("TypeRegistry"); tpeReg;
import htmlJsonUtils = require("HtmlJsonUtils"); htmlJsonUtils;
var mkTag = htmlJsonUtils.mkTag;
var mkTag2 = htmlJsonUtils.mkTag2;

var log4javascript = require('log4javascript');


var logger = log4javascript.getLogger("webapp.TypeViews");

//Basic Views 
export interface Closable {
    close(): void;
}
export class PSView extends Backbone.View implements Closable {
    tv: typeTvs.TV;
    constructor(options?) {
        super(options);
    }
    close() { throw new Error("Abstract method close not implemented in subclass of PSView") }
    updateVal() { throw new Error("Abstract method updateVal not implemented in subclass of PSView") }
}

export class AbstractView extends PSView {
    tv: typeTvs.AbstractTV;
    concreteView: PSView;
    constructor(tv: typeTvs.AbstractTV, options?) {
        super(options);
        this.tv = tv;
        this.populateConcreteView();
        
    }

    populateConcreteView() {
        if (this.tv.concreteTV == undefined)
            this.concreteView = undefined;
        else {
            this.concreteView = tpeReg.createView(this.tv.concreteTV);
        }
    }

    render() {
        this.$el.empty();
        this.$el.append("<div>AbstractView</div>");
        if (this.concreteView)
            this.concreteView.render();//TODO: test
        return this;
    }

    close() {
        if (this.concreteView) {
            this.concreteView.close();
        }
    }
    updateVal() {
        this.concreteView.updateVal();
    }
}

export class NewVarView extends PSView {
    tv: typeTvs.NewVarTV;
    varNameView: StringView;
    varTypeView: PSTypeView;
    constructor(tv: typeTvs.NewVarTV, options?) {
        super(options);
        this.tv = tv;
        this.populateViews();
    }
    populateViews() {
        this.varNameView = <StringView>tpeReg.createView(this.tv.varNameTV);
        this.varTypeView = <PSTypeView>tpeReg.createView(this.tv.varTypeTV);
    }

    render() {
        this.$el.empty();
        this.varNameView.render();
        this.varTypeView.render();
        var jsonEle: any[] =
            ['<div class = "NewVarTV"> </div>',
                [this.varNameView.el,
                    '<div> : </div>',
                    this.varTypeView.el]];

        this.$el.append(htmlJsonUtils.json2JQuery(jsonEle));
        return this;
    }

    close() {
        this.varTypeView.close();
    }
    updateVal() {
        this.varNameView.updateVal();
        this.varTypeView.updateVal();
    }
}

export class EnumView extends PSView {
    tv: typeTvs.EnumTV;

    constructor(tv: typeTvs.EnumTV, options?) {
        super(options);
        this.tv = tv;
    }

    render() {
        this.$el.empty();

        var template = templateMgr.getTemplate("enumView");
        var comboBoxDiv = $(template({ 'elements': this.tv.elements }));
        comboBoxDiv.find('select').val(this.tv.selectedElem);
        this.$el.append(comboBoxDiv);
        return this;
    }

    close() {
    }
    updateVal() {
        this.tv.selectedElem = this.$el.find("option:selected").val();
    }
}

export class PSTypeView extends EnumView {
}

export class ClassView extends PSView {
    tv: typeTvs.ClassTV;
    fieldViews: FieldView[];
    constructor(tv: typeTvs.ClassTV, options?) {
        super(options);
        this.tv = tv;
        this.fieldViews = [];
        this.populateFieldViews();
    }

    populateFieldViews() {
        _.forEach(this.tv.fields, (field) => {
            var fv = tpeReg.createView(field);
            this.fieldViews.push(<FieldView>fv);
        });
    }

    render() {
        this.$el.empty();
        var elem = $('<table border = "1" class="tablestyle" ></table>');
        _.forEach(this.fieldViews, (fv: FieldView) => {
            //children().first() strips the root .div node
            var fieldDiv = fv.render().$el;
            elem.append(fieldDiv.children().first());
        });
        this.$el.append(elem);
        return this;
    }

    close() {
        //_.each(this.fieldViews, function (fv) {
        //    fv.close();
        //});
    }
    updateVal() {
        this.fieldViews.forEach(fv => { fv.updateVal() });
    }
}

export class ListView extends PSView {
    tv: typeTvs.ListTV;
    itemViews: PSView[];
    template: any[];

    constructor(tv: typeTvs.ListTV, options?) {
        super(options);
        this.tv = tv;
        this.template =
        ["<div class = 'listVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
        this.itemViews = [];
        this.populateItemViews();

    }
    initialize() {
        this.events = {
            "click .addItemBtn": "addItemHandler",
            "click .removeItemBtn": "removeItemHandler"
        };
    }
    private populateItemViews() {
        if (this.tv.items.length == 0) {
            //this.addEmptyItem();
            //this.addEmptyItem();
        } else {
            _.forEach(this.tv.items, (item) => {
                var fv = tpeReg.createView(item);
                this.itemViews.push(fv);
            });
        }
    }

    render() {
        this.$el.empty();
        var elem = $('<table border = "1" ></table>');
        _.forEach(this.itemViews, (iv: PSView) => {
            var row = $("<tr></tr>");
            row.append($("<td></td>").append(iv.render().el));
            row.append($("<td class='removeItemBtn'><i class='minus square outline icon'></i></td>"))
            elem.append(row);
        });        
        this.$el.append(elem);
        this.$el.append("<div class='addItemBtn'><i class='plus square outline icon'></i></div>");
        return this;
    }

    addEmptyItem() {
        //Why to clone: the updateval directly picks up data from the objects 
        //associated with the views. Hence we need a new object.

        //jQuery extend does not work.
        //var clonedTpe = <TV>jQuery.extend(true, {}, this.tv.itv);
        var newItem = this.tv.metaTV.clone();
        this.tv.items.push(newItem);

        var fv = tpeReg.createView(newItem);
        this.itemViews.push(<PSView>fv);
    }

    addItemHandler() {
        //First update the TVs as we are redering at the end.
        this.updateVal();

        this.addEmptyItem();

        this.render();
    }

    removeItemHandler(event) {
        //First update the TVs as we are redering at the end.
        this.updateVal();

        var tr = $(event.currentTarget).parent();
        var n = tr.index();
        //update items and itemViews
        this.tv.items = _.reject(this.tv.items, function (item, index) { return index == n });
        this.itemViews = _.reject(this.itemViews, function (item, index) { return index == n });

        this.render();
    }

    updateVal() {
        this.itemViews.forEach(fv => { fv.updateVal() });
    }
}

export class TupleView extends PSView {
    tv: typeTvs.TupleTV;
    view1: PSView;
    view2: PSView;
    template: any[];

    constructor(tv: typeTvs.TupleTV, options?) {
        super(options);
        this.tv = tv;
        this.template =
        ["<div class = 'listVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
        this.view1 = tpeReg.createView(tv.item1);
        this.view2 = tpeReg.createView(tv.item2);
    }
    render() {
        this.$el.empty();
        var elem = $("<div class='tupleview'></div>");
        elem.append(this.view1.render().el);
        elem.append($("<div class='TupleSep'/>"));
        elem.append(this.view2.render().el);
        this.$el.append(elem);
        return this;
    }

    updateVal() {
        this.view1.updateVal();
        this.view2.updateVal();
    }
}

export class FieldView extends PSView {
    tv: typeTvs.FieldTV;
    rhsView: PSView;
    constructor(tv: typeTvs.FieldTV, options?) {
        super(options);
        this.tv = tv;
        this.rhsView = tpeReg.createView(this.tv.ftv);
    }

    renderWithTr() {
        //Add in constructor : this.tagName = "tr";
        this.$el.empty();
        this.$el.addClass("fieldDiv");
        this.$el.append("<td>" + this.tv.displayName + "</td>");
        this.$el.append("<td>" + "=" + "</td>");
        this.$el.append($("<td></td>").append(this.rhsView.render().el));
        return this;
    }

    render() {
        this.$el.empty();
        this.$el.addClass("fieldDiv");
        var rowElem = $("<tr></tr>");
        rowElem.append("<td>" + this.tv.displayName + "</td>");
        rowElem.append("<td>" + "=" + "</td>");
        rowElem.append($("<td></td>").append(this.rhsView.render().el));
        this.$el.append(rowElem);
        return this;
    }

    //using template
    render2() {
        var fieldName = this.tv.tvName;
        var rhsTV = this.tv.ftv;
        var fv = tpeReg.createView(rhsTV);

        var fieldValTpl = templateMgr.getTemplate("fieldVal");
        var fieldValElem = $(fieldValTpl({ ftv: rhsTV, fname: fieldName }));
        fieldValElem.find(".placeholder.fieldTVDiv").replaceWith(fv.render().el);

        this.$el.append(fieldValElem);
        return this;
    }

    updateVal() {
        this.rhsView.updateVal();
    }
}

export class PrimitiveView extends PSView {
    tv: typeTvs.PrimitiveTV;
    constructor(tv: typeTvs.PrimitiveTV, options?) {
        super(options);
        this.tv = tv;
    }
}

//User Views    
export class IntegerView extends PrimitiveView {
    tv: typeTvs.IntegerTV;
    template: any[];

    constructor(tv: typeTvs.IntegerTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<input name = 'data' />"]
        ];
    }

    render() {
        this.$el.empty();

        var template =
            mkTag('div', { class: 'primitiveVal' },
                mkTag('input', { name: 'data' }, ''));
        //Set the value
        template.find('input').val(this.tv.value);
        //this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.append(template);
        return this;
    }
    updateVal() {
        this.tv.value = this.$("input").val();
    }
}

export class FDisplayIdView extends IntegerView {
    tv: typeTvs.FDisplayIdTV;
    template: any[];

    constructor(tv: typeTvs.FDisplayIdTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div></div>",
            [
                ["<div class = 'primitiveVal'> </div>",
                    ["<input name = 'data' />"]],
                "<div class = 'FSelect'>Select</div>"
            ]
        ];
        this.events = {
            "click .FSelect": "onFSelect",
            "click .PSelect": "onPSelect"
        };
        this.listenTo(stm.gPSState, "model:DisplayIdSelected", $.proxy(this.onDisplayIdSelected, this));

    }

    render() {
        logger.trace("Render FDisplayIdView claled");
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        
        this.$el.find('input').val(this.tv.value);

        this.delegateEvents(this.events);
        return this;
    }
    updateVal() {
        this.tv.value = this.$el.find("input").val();
        logger.trace("displayId value" + this.tv.value);
    }

    private onFSelect() {
        logger.trace("FSelect clicked");
        this.$el.find("input").addClass("FSelectOn");
        stm.gPSState.onFSelectClick();
    }

    private onPSelect() {
        logger.trace("PSelect clicked");
    }
    onDisplayIdSelected() {
        this.$el.find("input.FSelectOn").val(stm.gPSState.displayId);
        this.$el.find("input").removeClass("FSelectOn");
    }
}

export class PDisplayIdView extends IntegerView {
    tv: typeTvs.PDisplayIdTV;
    template: any[];

    constructor(tv: typeTvs.PDisplayIdTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div></div>",
            [
                ["<div class = 'primitiveVal'> </div>",
                    ["<input name = 'data' />"]],
                "<div class = 'PSelect'>Select</div>"
            ]
        ];
        this.events = {
            "click .PSelect": "onPSelect"
        };
        this.listenTo(stm.gPSState, "model:PDisplayIdSelected", $.proxy(this.onPDisplayIdSelected, this));

    }

    render() {
        this.$el.empty();
        logger.trace("Render PDisplayIdView called");
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('input').val(this.tv.value);
        this.delegateEvents(this.events);
        return this;
    }
    updateVal() {
        this.tv.value = this.$el.find("input").val();
        logger.trace("displayId value" + this.tv.value);
    }

    private onPSelect() {
        logger.trace("PSelect clicked");
        this.$el.find("input").addClass("PSelectOn");
        stm.gPSState.onPSelectClick();
    }

    onPDisplayIdSelected() {
        this.$el.find("input.PSelectOn").val(stm.gPSState.pdisplayId);
        this.$el.find("input").removeClass("PSelectOn");
    }
}

export class StringView extends PrimitiveView {
    tv: typeTvs.StringTV;
    template: any[];

    constructor(tv: typeTvs.StringTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<input name = 'data' />"]
        ];
    }

    render() {
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('input').val(this.tv.value);
        return this;
    }
    updateVal() {
        this.tv.value = this.$("input").val();
    }
}

export class TermView extends PrimitiveView {
    tv: typeTvs.TermTV;
    template: any[];

    constructor(tv: typeTvs.TermTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
    }

    render() {
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('.mathquill-editable').html(this.tv.value);
        (<any>this.$('.mathquill-editable')).mathquill('editable');
        return this;
    }
    updateVal() {
        this.tv.value = (<any>this.$(".mathquill-editable")).mathquill('latex');;
    }
}

export class VarView extends PrimitiveView {
    tv: typeTvs.VarTV;
    template: any[];

    constructor(tv: typeTvs.VarTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
    }

    render() {
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('.mathquill-editable').html(this.tv.value);
        (<any>this.$('.mathquill-editable')).mathquill('editable');
        return this;
    }
    updateVal() {
        this.tv.value = (<any>this.$(".mathquill-editable")).mathquill('latex');;
    }
}

export class FOLFormulaView extends PrimitiveView {
    tv: typeTvs.FOLFormulaTV;
    template: any[];

    constructor(tv: typeTvs.FOLFormulaTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
    }

    render() {
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('.mathquill-editable').html(this.tv.value);
        (<any>this.$('.mathquill-editable')).mathquill('editable');
        return this;
    }
    updateVal() {
        this.tv.value = (<any>this.$(".mathquill-editable")).mathquill('latex');;
    }
}

export class TermBoolView extends PrimitiveView {
    tv: typeTvs.TermBoolTV;
    template: any[];

    constructor(tv: typeTvs.TermBoolTV, options?) {
        super(tv, options);
        this.tv = tv;
        this.template =
        ["<div class = 'primitiveVal'> </div>",
            ["<span class='mathquill-editable'></span>"]
        ];
    }

    render() {
        this.$el.empty();
        this.$el.append(htmlJsonUtils.json2JQuery(this.template));
        this.$el.find('.mathquill-editable').html(this.tv.value);
        (<any>this.$('.mathquill-editable')).mathquill('editable');
        return this;
    }
    updateVal() {
        this.tv.value = (<any>this.$(".mathquill-editable")).mathquill('latex');;
    }
}

export class StepIntoUnknownProgIdxView extends ClassView {
    tv: typeTvs.StepIntoUnknownProgIdxTV;
    constructor(tv: typeTvs.StepIntoUnknownProgIdxTV, options?) {
        super(tv, options);
        this.tv = tv;
    }

    updateVal() {
        super.updateVal();
        this.tv.idx = (<typeTvs.IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class RTVInPost2View extends ClassView {
    tv: typeTvs.RTVInPost2TV;
    constructor(tv: typeTvs.RTVInPost2TV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class SplitoutBoundVariableView extends ClassView {
    tv: typeTvs.SplitoutBoundVariableTV;
    constructor(tv: typeTvs.SplitoutBoundVariableTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class RTVInPostView extends ClassView {
    tv: typeTvs.RTVInPostTV;
    constructor(tv: typeTvs.RTVInPostTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class RetValView extends ClassView {
    tv: typeTvs.RetValTV;
    constructor(tv: typeTvs.RetValTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class InitView extends ClassView {
    tv: typeTvs.InitTV;
    constructor(tv: typeTvs.InitTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class Init4View extends ClassView {
    tv: typeTvs.Init4TV;
    constructor(tv: typeTvs.Init4TV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StartIfDerivationView extends ClassView {
    tv: typeTvs.StartIfDerivationTV;
    constructor(tv: typeTvs.StartIfDerivationTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StepIntoBAView extends ClassView {
    tv: typeTvs.StepIntoBATV;
    constructor(tv: typeTvs.StepIntoBATV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StepIntoIFBAView extends ClassView {
    tv: typeTvs.StepIntoIFBATV;
    constructor(tv: typeTvs.StepIntoIFBATV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class IntroSwapView extends ClassView {
    tv: typeTvs.IntroSwapTV;
    constructor(tv: typeTvs.IntroSwapTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class IntroIfView extends ClassView {
    tv: typeTvs.IntroIfTV;
    constructor(tv: typeTvs.IntroIfTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class InstantiateMetaView extends ClassView {
    tv: typeTvs.InstantiateMetaTV;
    constructor(tv: typeTvs.InstantiateMetaTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class ReplaceFormulaView extends ClassView {
    tv: typeTvs.ReplaceFormulaTV;
    constructor(tv: typeTvs.ReplaceFormulaTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class GuessGuardView extends ClassView {
    tv: typeTvs.GuessGuardTV;
    constructor(tv: typeTvs.GuessGuardTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StartGCmdDerivationView extends ClassView {
    tv: typeTvs.StartGCmdDerivationTV;
    constructor(tv: typeTvs.StartGCmdDerivationTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class MagicView extends ClassView {
    tv: typeTvs.MagicTV;
    constructor(tv: typeTvs.MagicTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class AssumePreView extends ClassView {
    tv: typeTvs.AssumePreTV;
    constructor(tv: typeTvs.AssumePreTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class DeleteConjunctView extends ClassView {
    tv: typeTvs.DeleteConjunctTV;
    constructor(tv: typeTvs.DeleteConjunctTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class IntroAssignmentView extends ClassView {
    tv: typeTvs.IntroAssignmentTV;
    constructor(tv: typeTvs.IntroAssignmentTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class IntroAssignmentEndView extends ClassView {
    tv: typeTvs.IntroAssignmentEndTV;
    constructor(tv: typeTvs.IntroAssignmentEndTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StepOutView extends ClassView {
    tv: typeTvs.StepOutTV;
    constructor(tv: typeTvs.StepOutTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class WhileStrInvSPView extends ClassView {
    tv: typeTvs.WhileStrInvSPTV;
    constructor(tv: typeTvs.WhileStrInvSPTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class CollapseCompositionsView extends ClassView {
    tv: typeTvs.CollapseCompositionsTV;
    constructor(tv: typeTvs.CollapseCompositionsTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class AssumeToIfView extends ClassView {
    tv: typeTvs.AssumeToIfTV;
    constructor(tv: typeTvs.AssumeToIfTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class PropagateAssumeUpView extends ClassView {
    tv: typeTvs.PropagateAssumeUpTV;
    constructor(tv: typeTvs.PropagateAssumeUpTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class StrengthenPostSPView extends ClassView {
    tv: typeTvs.StrengthenPostSPTV;
    constructor(tv: typeTvs.StrengthenPostSPTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class PropagateAssertionsDownSPView extends ClassView {
    tv: typeTvs.PropagateAssertionsDownSPTV;
    constructor(tv: typeTvs.PropagateAssertionsDownSPTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class SimplifyAutoView extends ClassView {
    tv: typeTvs.SimplifyAutoTV;
    constructor(tv: typeTvs.SimplifyAutoTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}

export class SimplifyView extends ClassView {
    tv: typeTvs.SimplifyTV;
    constructor(tv: typeTvs.SimplifyTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
    }
}
///////////////////////////
export class DistributivityView extends ClassView {
    tv: typeTvs.DistributivityTV;
    constructor(tv: typeTvs.DistributivityTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class EmptyRangeView extends ClassView {
    tv: typeTvs.EmptyRangeTV;
    constructor(tv: typeTvs.EmptyRangeTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class OnePointView extends ClassView {
    tv: typeTvs.OnePointTV;
    constructor(tv: typeTvs.OnePointTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class QDistributivityView extends ClassView {
    tv: typeTvs.QDistributivityTV;
    constructor(tv: typeTvs.QDistributivityTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class RangeSplitView extends ClassView {
    tv: typeTvs.RangeSplitTV;
    constructor(tv: typeTvs.RangeSplitTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class ReplaceSubformulaView extends ClassView {
    tv: typeTvs.ReplaceSubformulaTV;
    constructor(tv: typeTvs.ReplaceSubformulaTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class ReplaceSubTermView extends ClassView {
    tv: typeTvs.ReplaceSubTermTV;
    constructor(tv: typeTvs.ReplaceSubTermTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class StartAsgnDerivationView extends ClassView {
    tv: typeTvs.StartAsgnDerivationTV;
    constructor(tv: typeTvs.StartAsgnDerivationTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}
//
export class StepIntoPOView extends ClassView {
    tv: typeTvs.StepIntoPOTV;
    constructor(tv: typeTvs.StepIntoPOTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class StepIntoProgIdView extends ClassView {
    tv: typeTvs.StepIntoProgIdTV;
    constructor(tv: typeTvs.StepIntoProgIdTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class StepIntoSubProgView extends ClassView {
    tv: typeTvs.StepIntoSubProgTV;
    constructor(tv: typeTvs.StepIntoSubProgTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class StepIntoSubFormulaView extends ClassView {
    tv: typeTvs.StepIntoSubFormulaTV;
    constructor(tv: typeTvs.StepIntoSubFormulaTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }

    onDisplayIdSelect() {
        var displayId = 10;
        //this.$el.find('table ')
        /*
        this.$el.empty();
        var elem = $('<table border = "1" ></table>');
        _.forEach(this.fieldViews, (fv: FieldView) => {
            //children().first() strips the root .div node
            var fieldDiv = fv.render().$el;
            elem.append(fieldDiv.children().first());
        });
        this.$el.append(elem);
        return this;
        */

    }
}

export class StrengthenInvariantView extends ClassView {
    tv: typeTvs.StrengthenInvariantTV;
    constructor(tv: typeTvs.StrengthenInvariantTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class TradingMoveToTermView extends ClassView {
    tv: typeTvs.TradingMoveToTermTV;
    constructor(tv: typeTvs.TradingMoveToTermTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class UseAssumptionsView extends ClassView {
    tv: typeTvs.UseAssumptionsTV;
    constructor(tv: typeTvs.UseAssumptionsTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class Init3View extends ClassView {
    tv: typeTvs.Init3TV;
    constructor(tv: typeTvs.Init3TV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}

export class InsertVariableView extends ClassView {
    tv: typeTvs.InsertVariableTV;
    constructor(tv: typeTvs.InsertVariableTV, options?) {
        super(tv, options);
        this.tv = tv;
    }
    updateVal() {
        super.updateVal();
        //this.tv.idx = (<IntegerTV>this.tv.fields[0].ftv).value
    }
}
///////////////////////////
export class TacticView extends AbstractView {
    tv: typeTvs.TacticTV;
    /**/logger: Log4Javascript;

    initialize() {
        if ((<any>this).jel) {
            this.el = (<any>this).jel.get();
        }
        /**///this.logger.trace("TacticView: registering handler for change .inputPanelTVCombo");
        this.events = {
            "change .inputPanelTVCombo": "onComboSelect"
        }
    }

    constructor(tv: typeTvs.TacticTV, options?) {
        super(tv, options);
        /**/this.logger = log4javascript.getLogger("webapp.TacticView");
        this.tv = tv;
    }

    render_old() {
        /**/this.logger.trace("Rendering TacticView ...");        
        this.$el.empty();

        var tacticTVs = _.map(this.tv.subTVNames, (st) => { return st.replace(/TV$/, "") });

        var subViewDiv = this.concreteView ? this.concreteView.render().el : '';

        var template = templateMgr.getTemplate("abstractVal");

        var comboBoxDiv = template({
            'subTVs': tacticTVs,
            'absTVName': this.tv.tvName.replace(/TV$/, ""),
            'concreteDiv': subViewDiv
        });

        this.$el.append(comboBoxDiv);
        return this;
    }

    template(absTVName: string, subTVs: any[], $subView: JQuery) {
        var subTVOptions =
            _.map(subTVs, function (st) { return mkTag('option', { 'value': st }, st) });

        var chooseOptionDiv = mkTag('option', { 'value': '', 'disabled': undefined, 'selected': undefined }, 'Choose ...')

        var comboOptionsDiv = [chooseOptionDiv].concat(subTVOptions);

        var x =
            mkTag('div', { 'class': 'template', 'id': 'abstractVal' },
                mkTag('div', { 'class': "AbstractVal" },
                    mkTag('div', { 'class': 'absValueName' }, absTVName),
                    mkTag('div', {},
                        mkTag2('select', { 'class': "inputPanelTVCombo" },comboOptionsDiv)),
                    mkTag('div', { 'class': 'selectedSubTV' }, $subView)));
        return x;
    }

    render() {
        this.$el.empty();

        var absTVName = this.tv.tvName.replace(/TV$/, "");
        var subTVs = _.map(this.tv.subTVNames, (st) => { return st.replace(/TV$/, "") });
        var $subView = this.concreteView ? this.concreteView.render().$el : $('<div></div>');
                 
        this.$el.append(this.template(absTVName, subTVs, $subView));
        //select the option
        if (this.concreteView) {
            var tacticName = this.concreteView.tv.tvName.replace(/TV$/, "");
            this.$el.find('select.inputPanelTVCombo').val(tacticName);
        }

        return this;
    }


    onComboSelect(event) {
        /**/this.logger.trace("TacticView.onComboSelect called");
        logger.trace("Tactic::onComboSelect");
        this.$('.selectedSubTV').empty();
        var tacticName = $(event.target).find("option:selected").val();
        this.concreteView = tpeReg.createViewFromTVName(tacticName + 'TV');;
        this.tv.concreteTV = this.concreteView.tv;
        this.$('.selectedSubTV').append(this.concreteView.render().el);
        (<any>this.$('.mathquill-editable')).mathquill('redraw');
        //Clear Errors
        $(".ApplyTacticError").html("");
    }
}
