/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/log4javascript/log4javascript.d.ts" />
"use strict";
var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "TemplateMgr", "TypeRegistry", "TypeViews", "TypeTVs", "HtmlJsonUtils", "PSJqueryUtils", "psweb.jquery.layout"], function(require, exports, templateMgr, tpeReg, typeViews, typeTVs, htmlJsonUtils, psJqueryUtils, psweb_jquery_layout) {
    psJqueryUtils;
    psweb_jquery_layout;

    var log4javascript = require('log4javascript');

    //---------------------------------
    var InputPanelView = (function (_super) {
        __extends(InputPanelView, _super);
        function InputPanelView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.InputPanelView");
            /**/ this.logger.trace("BeginSection(InputPanelView.constructor)");
            this.tagName = "div";
            this.tacticInputView = new typeViews.TacticView(new typeTVs.TacticTV);
            /**/ this.logger.trace("EndSection(InputPanelView.constructor)");
        }
        InputPanelView.prototype.initialize = function () {
            /**/ //this.logger.trace("registered handler for click #applyTacticBtn in InputPanelView");
            tpeReg.init();

            this.events = {
                "click #applyTacticBtn": "onApplyTactic"
            };
        };

        InputPanelView.prototype.render0 = function () {
            var inputFormTpl = templateMgr.getTemplate("inputForm");
            var formElem = $(inputFormTpl());

            this.tacticInputView.render();
            var subEl = this.tacticInputView.el;

            var placeHolder = formElem.find(".placeholder.inputUI");
            placeHolder.replaceWith(subEl);

            this.$el.append(formElem);
            return this;
        };

        InputPanelView.prototype.render = function () {
            /**/ this.logger.trace("rendering InputPanelView...");
            var template = [
                "<form action=''></form>",
                [
                    this.tacticInputView.render().el,
                    "<div class='button' id='applyTacticBtn'>Apply</div>",
                    "<div class='ApplyTacticError'></div>"
                ]
            ];
            htmlJsonUtils.json2JQuery(template);
            this.$el.append(htmlJsonUtils.json2JQuery(template));

            //(<any>this.$('mathquill-editable')).mathquill('editable');
            this.$el.addClass("inputPanel inputPanelEL");
            return this;
        };

        InputPanelView.prototype.rendertest = function () {
            this.$el.append('<div style="height:300px; background-color: pink;">Hello</div>');
            return this;
        };

        //render2() {
        //    var compositeVal = mt.compositeValDb["rootTactic"];
        //    var inputUI = compositeVal.mkDiv();
        //    var inputFormTemplate = templateMgr.getTemplate("inputForm");
        //    var formElem = inputFormTemplate({ "inputUI": inputUI });
        //    this.$el.append(formElem);
        //    return this;
        //}
        InputPanelView.prototype.render3 = function () {
            //render subview first
            this.tacticInputView = new typeViews.TacticView(new typeTVs.TacticTV); // Remove from render.
            this.tacticInputView.render();

            //render this
            var inputFormTpl = templateMgr.getTemplate("inputForm");
            var formElem = inputFormTpl({ "inputUI": this.tacticInputView.$el[0].outerHTML });

            this.$el.append(formElem);
            return this;
        };

        InputPanelView.prototype.onApplyTactic = function () {
            /**/ this.logger.trace("InputPanelView.onApplyTactic");
            /**/ this.logger.trace("Updating values of InputPanelView.tacticInputView...");
            /**/ this.logger.trace("Collecting input data...");
            if (this.psState.getCurNodeId() !== this.psState.getSelNodeId()) {
                alert("Can not apply tactic. Head node different from selected node.");
                return;
            }

            this.tacticInputView.updateVal();
            var inputData = this.tacticInputView.tv;

            //add derivation name to inputData
            var derivName = psJqueryUtils.extractLastUrlSegment();

            //inputData['derivName'] = derivName;
            //var inputData = idc.gatherInputData();
            /**/ this.logger.trace("calling psState.applyTactic...");
            this.psState.applyTactic(inputData, derivName);
        };

        InputPanelView.prototype.cleanup = function (event) {
            /**/ this.logger.trace("cleaning up inputPanelView...");
            var $fieldValDiv = $(event.target).closest(".AbstractVal");
            $fieldValDiv.children('.CompositeVal').remove();
        };

        InputPanelView.prototype.close = function () {
            /**/ this.logger.trace("closing inputPanelView...");
        };
        return InputPanelView;
    })(Backbone.View);
    exports.InputPanelView = InputPanelView;
});
/*
//Utils
class Describer {
static getClassName(inputObj) {
var funcNameRegex = /function (.{1,})\(/;
var results = (funcNameRegex).exec((<any> inputObj).constructor.toString());
return (results && results.length > 1) ? results[1] : "";
}
}
module ViewFactory {
export function createView(aTV: typeTVs.TV): Backbone.View {
var clsName = Describer.getClassName(aTV);
var retTV: Backbone.View = undefined;
if (clsName === 'StepIntoUnknownProgIdxTV') {
retTV = new typeViews.StepIntoUnknownProgIdxView(<typeTVs.StepIntoUnknownProgIdxTV>aTV);
} else if (clsName === 'PrimitiveTV') {
retTV = new typeViews.PrimitiveView(<typeTVs.PrimitiveTV>aTV);
} else if (clsName === 'IntegerTV') {
retTV = new typeViews.IntegerView(<typeTVs.IntegerTV>aTV);
}
return retTV;
}
}
*/
//# sourceMappingURL=InputPanel.js.map
