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
define(["require", "exports", "TypeRegistry", "TypeViews", "TypeTVs", "HtmlJsonUtils", "PSJqueryUtils", "psweb.jquery.layout"], function(require, exports, tpeReg, typeViews, typeTVs, htmlJsonUtils, pju, psweb_jquery_layout) {
    pju;
    psweb_jquery_layout;
    var mkTag = htmlJsonUtils.mkTag;
    var mkTag2 = htmlJsonUtils.mkTag2;

    var log4javascript = require('log4javascript');

    //---------------------------------
    var EditTacticView = (function (_super) {
        __extends(EditTacticView, _super);
        function EditTacticView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.EditTacticView");
            this.tagName = "div";
            this.tacticView = new typeViews.TacticView(new typeTVs.TacticTV);

            //this.tacticView = this.getSelTacticView();
            this.listenTo(psState, 'model:selectedNodeChanged', $.proxy(this.onSelNodeChanged, this));
        }
        EditTacticView.prototype.initialize = function () {
            tpeReg.init();
            this.events = {
                "click #editTacticBtn": "onEditTactic"
            };
        };

        EditTacticView.prototype.render = function () {
            this.$el.empty();

            var template2 = mkTag('form', { action: '' }, this.tacticView.render().$el, mkTag('div', { 'class': 'button', 'id': 'editTacticBtn' }, 'Apply'), mkTag('div', { 'class': 'EditTacticError' }, ''));

            this.$el.append(template2);

            //(<any>this.$('mathquill-editable')).mathquill('editable');
            this.$el.addClass("editTacticPanel editTacticPanelEL");
            return this;
        };

        EditTacticView.prototype.onEditTactic = function () {
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
        };

        EditTacticView.prototype.getSelTacticView_test = function () {
            var x = new typeTVs.TacticTV();
            var dtv = new typeTVs.DistributivityTV(6);
            x.concreteTV = dtv;
            var selTacticView = new typeViews.TacticView(x);
            selTacticView.concreteView = new typeViews.DistributivityView(dtv);
            return selTacticView;
        };

        EditTacticView.prototype.getSelTacticView = function () {
            //Get the TV from the server
            var selNodeId = this.psState.getSelNodeId();
            var derivName = pju.extractLastUrlSegment();
            var tacticTVObj = this.psState.getTVFrmServer(derivName, selNodeId);

            //Parse tacticTV
            var tacticTV = typeTVs.objToTV(tacticTVObj);

            //Create view
            var tacticView = tpeReg.createView(tacticTV);

            return tacticView;
        };

        EditTacticView.prototype.onSelNodeChanged = function () {
            //this.tacticView = this.getTmpSelTacticView();
            //this.tacticView = this.psState.getSelTacticView();
            this.tacticView = this.getSelTacticView();
            this.render();
        };

        EditTacticView.prototype.cleanup = function (event) {
            var $fieldValDiv = $(event.target).closest(".AbstractVal");
            $fieldValDiv.children('.CompositeVal').remove();
        };

        EditTacticView.prototype.close = function () {
        };
        return EditTacticView;
    })(Backbone.View);
    exports.EditTacticView = EditTacticView;
});
//# sourceMappingURL=EditTactic.js.map
