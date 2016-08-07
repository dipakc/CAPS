/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/log4javascript/log4javascript.d.ts" />

"use strict";

import templateMgr = require("TemplateMgr");
import stm = require("SynthtreeModel"); 
import tpeReg = require("TypeRegistry");
import typeViews = require("TypeViews");
import typeTVs = require("TypeTVs");
import htmlJsonUtils = require("HtmlJsonUtils");

import psJqueryUtils = require("PSJqueryUtils"); psJqueryUtils;
import psweb_jquery_layout = require("psweb.jquery.layout"); psweb_jquery_layout;

var log4javascript = require('log4javascript');
//---------------------------------

export class InputPanelView extends Backbone.View {
    tacticInputView: typeViews.TacticView;
    /**/logger: Log4Javascript;
    constructor(public psState: stm.PSState, options?) {
        super(options);
        /**/this.logger = log4javascript.getLogger("webapp.InputPanelView");
        /**/this.logger.trace("BeginSection(InputPanelView.constructor)");
        this.tagName = "div";
        this.tacticInputView = new typeViews.TacticView(new typeTVs.TacticTV);
        /**/this.logger.trace("EndSection(InputPanelView.constructor)");        
    }

    initialize() {
        /**///this.logger.trace("registered handler for click #applyTacticBtn in InputPanelView");
        tpeReg.init();

        this.events = {
            "click #applyTacticBtn": "onApplyTactic"
        };
    }

    render0() {
        var inputFormTpl = templateMgr.getTemplate("inputForm");
        var formElem = $(inputFormTpl());

        this.tacticInputView.render();
        var subEl = this.tacticInputView.el;

        var placeHolder = formElem.find(".placeholder.inputUI");
        placeHolder.replaceWith(subEl);

        this.$el.append(formElem);
        return this;
    }

    render() {
        /**/this.logger.trace("rendering InputPanelView...");
        var template: any[] =
            ["<form action=''></form>",
                [this.tacticInputView.render().el,
                    "<div class='button' id='applyTacticBtn'>Apply</div>",
                    "<div class='ApplyTacticError'></div>"
                ]
            ];
        htmlJsonUtils.json2JQuery(template);
        this.$el.append(htmlJsonUtils.json2JQuery(template));
        //(<any>this.$('mathquill-editable')).mathquill('editable');
        this.$el.addClass("inputPanel inputPanelEL");
        return this;
    }

    rendertest() {
        this.$el.append('<div style="height:300px; background-color: pink;">Hello</div>');
        return this;
    }
    //render2() {
    //    var compositeVal = mt.compositeValDb["rootTactic"];
    //    var inputUI = compositeVal.mkDiv();
    //    var inputFormTemplate = templateMgr.getTemplate("inputForm");
    //    var formElem = inputFormTemplate({ "inputUI": inputUI });
    //    this.$el.append(formElem);
    //    return this;
    //}

    render3() {
        //render subview first
        this.tacticInputView = new typeViews.TacticView(new typeTVs.TacticTV); // Remove from render.
        this.tacticInputView.render();

        //render this
        var inputFormTpl = templateMgr.getTemplate("inputForm");
        var formElem = inputFormTpl({ "inputUI": this.tacticInputView.$el[0].outerHTML });

        this.$el.append(formElem);
        return this;
    }

    onApplyTactic() {
        /**/this.logger.trace("InputPanelView.onApplyTactic");        
        /**/this.logger.trace("Updating values of InputPanelView.tacticInputView...");
        /**/this.logger.trace("Collecting input data...");
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
        /**/this.logger.trace("calling psState.applyTactic...");
        this.psState.applyTactic(inputData, derivName);
    }

    cleanup(event) {
        /**/this.logger.trace("cleaning up inputPanelView...");
        var $fieldValDiv = $(event.target).closest(".AbstractVal");
        $fieldValDiv.children('.CompositeVal').remove();
    }

    close() {
        /**/this.logger.trace("closing inputPanelView...");
    }
}


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