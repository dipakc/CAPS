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

import pju = require("PSJqueryUtils"); pju;
import psweb_jquery_layout = require("psweb.jquery.layout"); psweb_jquery_layout;
var mkTag = htmlJsonUtils.mkTag;
var mkTag2 = htmlJsonUtils.mkTag2;

var log4javascript = require('log4javascript');
//---------------------------------

export class EditTacticView extends Backbone.View {

    tacticView: typeViews.TacticView;
    /**/logger: Log4Javascript;

    constructor(public psState: stm.PSState, options?) {
        super(options);
        /**/this.logger = log4javascript.getLogger("webapp.EditTacticView");
        this.tagName = "div";
        this.tacticView = new typeViews.TacticView(new typeTVs.TacticTV);
        //this.tacticView = this.getSelTacticView();
        this.listenTo(psState, 'model:selectedNodeChanged', $.proxy(this.onSelNodeChanged, this));        
    }
    
    initialize() {
        tpeReg.init();
        this.events = {
            "click #editTacticBtn": "onEditTactic"
        };
    }

    render() {
        this.$el.empty();

        var template2 =
            mkTag('form', { action: '' },
                this.tacticView.render().$el,
                mkTag('div', { 'class': 'button', 'id': 'editTacticBtn' }, 'Apply'),
                mkTag('div', { 'class': 'EditTacticError' }, ''));

        
        this.$el.append(template2);
        //(<any>this.$('mathquill-editable')).mathquill('editable');
        this.$el.addClass("editTacticPanel editTacticPanelEL");
        return this;
    }

    onEditTactic() {
        if (!this.psState.getCurNode().isLeaf()) {
            alert("Head node must be a leaf node.");
            return;
        }

        if (this.psState.getCurNodeId() !== this.psState.getSelNodeId()) {
            alert("Can not edit tactic. Head node different from selected node.");
            return;
        }

        this.tacticView.updateVal();
        var inputData = this.tacticView.tv;
        //add derivation name to inputData
        var derivName = pju.extractLastUrlSegment();
        this.psState.editTactic(inputData, derivName);
    }

    getSelTacticView_test() {
        var x = new typeTVs.TacticTV();
        var dtv = new typeTVs.DistributivityTV(6);
        x.concreteTV = dtv;
        var selTacticView = new typeViews.TacticView(x);
        selTacticView.concreteView = new typeViews.DistributivityView(dtv)
        return selTacticView;
    }

    getSelTacticView() {
        
        //Get the TV from the server
        var selNodeId = this.psState.getSelNodeId();
        var derivName = pju.extractLastUrlSegment();
        var tacticTVObj = this.psState.getTVFrmServer(derivName, selNodeId);

        //Parse tacticTV
        var tacticTV = <typeTVs.TacticTV>typeTVs.objToTV(tacticTVObj);

        //Create view
        var tacticView = <typeViews.TacticView> tpeReg.createView(tacticTV);

        return tacticView;
    }

    onSelNodeChanged() {
        //this.tacticView = this.getTmpSelTacticView();
        //this.tacticView = this.psState.getSelTacticView();
        this.tacticView = this.getSelTacticView();
        this.render();
    }

    cleanup(event) {
        var $fieldValDiv = $(event.target).closest(".AbstractVal");
        $fieldValDiv.children('.CompositeVal').remove();
    }

    close() {
    }
}
