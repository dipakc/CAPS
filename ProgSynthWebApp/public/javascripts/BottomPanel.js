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
define(["require", "exports", "InputPanel", "EditTactic", "SynthtreeModel", "HtmlJsonUtils"], function(require, exports, ip, et, stm, hju) {
    ip;
    et;
    stm;
    hju;
    var mkTag = hju.mkTag;
    var log4javascript = require('log4javascript');

    //---------------------------------
    var BottomPanelView = (function (_super) {
        __extends(BottomPanelView, _super);
        function BottomPanelView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.BottomPanelView");
            /**/ this.logger.trace("BeginSection(BottomPanelView.constructor)");
            this.tagName = "div";
            this.inputPanelView = new ip.InputPanelView(psState);
            this.editTacticView = new et.EditTacticView(psState);
            /**/ this.logger.trace("EndSection(BottomPanelView.constructor)");
        }
        BottomPanelView.prototype.initialize = function () {
            this.events = {
                "click div.ui.tabular.menu > a.item[data-tab='second']": "onEditTabClicked"
            };
        };

        BottomPanelView.prototype.onEditTabClicked = function () {
            console.log("Edit tab clicked");
            this.editTacticView.onSelNodeChanged();
        };

        BottomPanelView.prototype.render = function () {
            /**/ this.logger.trace("BeginSection(BottomPanelView.render)");
            this.$el.addClass("BottomPanelEL");
            var $tabsDiv = $(hju.mkTag('div', {}));

            if (false) {
                this.$el.append($tabsDiv);
                this.inputPanelView.setElement($('<div></div>'));
                this.$el.append(this.inputPanelView.render().el);
            }

            if (true) {
                this.$el.append($tabsDiv);

                //create the tab menu
                var firstTabName = 'Apply Tactic';
                var secondTabName = 'Edit Tactic';
                var thirdTabName = 'Error Log';

                $tabsDiv.append(mkTag('div', { 'class': "ui tabular menu" }, mkTag('a', { 'class': "item active", 'data-tab': 'first' }, firstTabName), mkTag('a', { 'class': "item", 'data-tab': 'second' }, secondTabName), mkTag('a', { 'class': "item", 'data-tab': 'third' }, thirdTabName)));

                this.inputPanelView.setElement($('<div></div>')); //TODO: can this be avoided
                this.inputPanelView.render();
                this.editTacticView.render();

                $tabsDiv.append(mkTag('div', {
                    'class': "ui bottom attached tab segment active",
                    'data-tab': 'first'
                }, this.inputPanelView.$el));

                $tabsDiv.append(mkTag('div', {
                    'class': "ui bottom attached tab segment",
                    'data-tab': 'second'
                }, this.editTacticView.$el));

                $tabsDiv.append(mkTag('div', {
                    'class': "ui bottom attached tab segment",
                    'data-tab': 'third'
                }, 'Third Tab Content'));

                this.$el.find('.ui.tabular.menu > .item').tab();
            }

            /**/ this.logger.trace("EndSection(BottomPanelView.render)");
            return this;
        };
        return BottomPanelView;
    })(Backbone.View);
    exports.BottomPanelView = BottomPanelView;
});
//# sourceMappingURL=BottomPanel.js.map
