/// <reference path="typings/underscore/underscore.d.ts" />
/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jqueryui/jqueryui.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/log4javascript/log4javascript.d.ts" />
"use strict";
var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
define(["require", "exports", "OptionsHandler", "ToggleId", "FoldableDiv", "BottomPanel", "SynthtreeModel", "TemplateMgr", "MinimizableDiv", "MinimizableWithTitle", "PSMenuBar", "PSJqueryUtils", "psweb.jquery.layout"], function(require, exports, optionsHandler, toggleIdModule, foldableDivModule, bp, stm, templateMgr, minimiazableDivModule, minimizableWithTitleModule, psMenuBar, pju, pjl) {
    ;

    pju;
    pjl;

    // javascript dependencies for which return values are required.
    var toastr = require('toastr');
    var log4javascript = require('log4javascript');

    var appRoutes = jsRoutes.controllers.Application;

    //---------------------------------------------------------
    //toastr.info("In ps derivation...");
    var AppView = (function (_super) {
        __extends(AppView, _super);
        function AppView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.AppView");
            /**/ this.logger.trace("BeginSection(AppView.constructor)");
            this.tacticsPanelView = new TacticsPanelView(psState);
            this.tacticsPanelView.setElement(this.$('.caps-left').single());

            //this.tacticsPanelView.render();
            this.contentView = new ContentView(psState);
            this.contentView.setElement(this.$('.caps-content').single());
            this.bottomPanelView = new bp.BottomPanelView(psState);
            this.bottomPanelView.setElement(this.$('.bottomPanel').single());
            this.bottomPanelView.render();
            this.keyBinder = new KeyBinder(psState, { el: 'body' });
            $('body .mathquill-editable').mathquill('editable');
            this.initSideBar();
            /**/ this.logger.trace("EndSection(AppView Constructor)");
        }
        AppView.prototype.start = function () {
            /**/ this.logger.trace("BeginSection(AppView.start)");
            stm.gPSState.getStateFromServer();
            /**/ this.logger.trace("EndSection(AppView.start)");
        };
        AppView.prototype.render = function () {
            return this;
        };

        AppView.prototype.initSideBar = function () {
            this.logger.trace('in initSidebar');
            $('#caps-sidebar.ui.sidebar > a#derivations.item').single().on('click', function () {
                //this.logger.trace('clicked on derivaltions');
                $('#caps-sidebar.ui.sidebar').removeClass('visible');
                $('#caps-pusher.pusher').removeClass('dimmed');

                window.location.href = appRoutes.derivations().url;
            });

            $('#caps-sidebar.ui.sidebar > a#help.item').single().on('click', function () {
                //this.logger.trace('clicked on caps');
                $('#caps-sidebar.ui.sidebar').removeClass('visible');
                $('#caps-pusher.pusher').removeClass('dimmed');
            });
        };
        return AppView;
    })(Backbone.View);

    var TacticsPanelView = (function (_super) {
        __extends(TacticsPanelView, _super);
        function TacticsPanelView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.TacticsPanelView");
            /**/ this.logger.trace("BeginSection(TacticsPanelView.constructor)");
            this.events = {};
            this.path = [];
            this.nsViews = [];
            this.listenTo(psState, 'model:stateChanged', $.proxy(this.onStateChanged, this));
            /**/ this.logger.trace("TacticsPanelView now listening to model:stateChanged");
            this.listenTo(psState, 'model:nodeAdded', $.proxy(this.onNodeAdded, this));
            /**/ this.logger.trace("TacticsPanelView now listening to model:nodeAdded");
            this.listenTo(psState, 'model:selectedNodeChanged', $.proxy(this.onSelectedNodeChanged, this));
            /**/ this.logger.trace("TacticsPanelView now listening to model:selectedNodeChanged");
            this.listenTo(psState, 'model:tacticEdited', $.proxy(this.onSelectedNodeChanged, this));
            /**/ this.logger.trace("EndSection(TacticsPanelView.constructor)");
        }
        TacticsPanelView.prototype.onStateChanged = function () {
            var _this = this;
            /**/ this.logger.trace("BeginSection(TacticsPanelView.onStateChanged)");
            this.cleanup();

            //Update path
            /**/ this.logger.trace("getting path of the selected node ...");
            this.path = this.psState.getPathOfSelNode();

            //Update nsViews
            /**/ this.logger.trace("updating node sibling view of nodes in the path...");
            _.each(this.path, function (synthNode) {
                _this.nsViews.push(new NodeSiblingsView(synthNode, _this.psState));
            });

            //Update $el
            /**/ this.logger.trace("rendering the tacticsPanelView");
            this.render();
            /**/ this.logger.trace("EndSection(TacticsPanelView.onStateChanged)");
        };

        TacticsPanelView.prototype.cleanup = function () {
            /**/ this.logger.trace("TacticsPanelView.cleanup: cleaning up the tacticspanel view");
            this.path = [];
            this.nsViews = []; //TODO: clear the nsViews elements
            this.$el.empty();
        };

        TacticsPanelView.prototype.onNodeAdded = function () {
        };

        TacticsPanelView.prototype.render = function () {
            var _this = this;
            /**/ this.logger.trace("BeginSection(TacticsPanelView.render)");
            _.each(this.nsViews, function (nsView) {
                nsView.setElement($('<div></div>'));
                _this.$el.append(nsView.render().el);
            });
            /**/ this.logger.trace("EndSection(TacticsPanelView.render)");
            return this;
        };

        TacticsPanelView.prototype.onSelectedNodeChanged = function () {
            /**/ this.logger.trace("BeginSection(TacticsPanelView.onSelectedNodeChanged)");
            var selNode = this.psState.getSelNode();
            if (_.contains(this.path, selNode)) {
                //Do nothing
            } else {
                this.onStateChanged();
            }
            /**/ this.logger.trace("EndSection(TacticsPanelView.onSelectedNodeChanged)");
        };
        return TacticsPanelView;
    })(Backbone.View);

    var SiblingView = (function (_super) {
        __extends(SiblingView, _super);
        function SiblingView(node, psState, options) {
            _super.call(this, options);
            this.node = node;
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.SiblingView");
            this.events = {
                'click div.siblingitem': 'onClick'
            };
        }
        SiblingView.prototype.render = function () {
            /**/ this.logger.trace("rendering SiblingView...");
            var template = templateMgr.getTemplate("siblingsView");
            this.$el.append(template());

            //this.$el.append('<div class="siblingitem">O</div>');
            return this;
        };
        SiblingView.prototype.onClick = function () {
            /**/ this.logger.trace("SiblingView.onClick. Update selected node");
            console.log("sibling node clicked");
            this.psState.setSelNodeId(this.node.getNodeId());
        };
        return SiblingView;
    })(Backbone.View);

    var NodeSiblingsView = (function (_super) {
        __extends(NodeSiblingsView, _super);
        /*
        Constructor takes a "node" and "psState" as input. It then extracts the left and right sibling nodes
        of the current node adnd creates the sibling views.
        */
        function NodeSiblingsView(node, psState, options) {
            _super.call(this, options);
            this.node = node;
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.NodeSiblingsView");
            /**/ this.logger.trace("BeginSection(NodeSiblingsView.constructor)");
            /**/ this.logger.trace("populating left right siblings and views...");
            this.populateLeftRightSiblingsAndViews();
            /**/ this.logger.trace("registered handlers for click .nodeview and click .menuicon");
            this.events = {
                "click .nodeview": "onClick",
                "click .menuicon": "onMenuIcon"
            };
            /**/ this.logger.trace("listening to model:selectedNodeChanged and model:currentNodeChanged");
            this.listenTo(psState, 'model:selectedNodeChanged', $.proxy(this.renderSelectedNode, this));
            this.listenTo(psState, 'model:tacticEdited', $.proxy(this.renderSelectedNode, this));
            this.listenTo(psState, 'model:currentNodeChanged', $.proxy(this.renderCurrentNode, this));
            /**/ this.logger.trace("EndSection(NodeSiblingsView.constructor)");
        }
        NodeSiblingsView.prototype.populateLeftRightSiblingsAndViews = function () {
            var _this = this;
            /**/ this.logger.trace("BeginSection(NodeSiblingsView.populateLeftRightSiblingsAndViews)");
            var leftRightSiblings = this.psState.getLeftRightSiblings(this.node);
            this.leftSiblings = leftRightSiblings[0];
            this.rightSiblings = leftRightSiblings[1];
            this.leftViews = [];
            _.each(this.leftSiblings, function (ls) {
                _this.leftViews.push(new SiblingView(ls, _this.psState));
            });
            this.rightViews = [];
            _.each(this.rightSiblings, function (rs) {
                _this.rightViews.push(new SiblingView(rs, _this.psState));
            });
            /**/ this.logger.trace("EndSection(NodeSiblingsView.populateLeftRightSiblingsAndViews)");
        };

        //RefactorTask: Use templates
        /*
        */
        NodeSiblingsView.prototype.render = function () {
            /**/ this.logger.trace("BeginSection(NodeSiblingsView.render)");
            this.$el.addClass('nsview'); //TODO: avoid adding class to $el.

            var leftSiblingsDiv = $("<div class='siblingsview'></div>");
            _.each(this.leftViews, function (lview) {
                lview.setElement($('<div></div>'));
                leftSiblingsDiv.append(lview.render().el);
            });
            this.$el.append(leftSiblingsDiv);

            this.$el.append('<div class="nodeview">' + this.node.getNodeId() + this.node.getTactic().tacticName + '</div>');

            var rightSiblingsDiv = $("<div class='siblingsview'></div>");
            _.each(this.rightViews, function (rview) {
                rview.setElement($('<div></div>'));
                rightSiblingsDiv.append(rview.render().el);
            });

            this.$el.append(rightSiblingsDiv);

            //var contextMenuDiv = $("<div><ul id='nodeContextMenu'><li>menu1</li><li>menu2</li></ul></div>");
            //contextMenuDiv.children("#nodeContextMenu").menu();
            var imageUrl = '"' + pju.getRoolUrl() + '/assets/images/nodeOptions.png' + '"';
            var menuIconDiv = $('<div class="menuicon"><img src=' + imageUrl + ' alt="xxx"> </div>');

            //menuIconDiv.append(contextMenuDiv);
            this.$el.append(menuIconDiv);

            this.renderSelectedNode();
            this.renderCurrentNode();
            /**/ this.logger.trace("EndSection(NodeSiblingsView.render)");
            return this;
        };

        NodeSiblingsView.prototype.renderSelectedNode = function () {
            if (this.psState.getSelNodeId() === this.node.getNodeId()) {
                this.$el.children('.nodeview').addClass('selected');
            } else {
                this.$el.children('.nodeview').removeClass('selected');
            }
        };

        NodeSiblingsView.prototype.renderCurrentNode = function () {
            if (this.psState.getCurNodeId() === this.node.getNodeId()) {
                this.$el.addClass('current');
            } else {
                this.$el.removeClass('current');
            }
        };

        NodeSiblingsView.prototype.onClick = function () {
            console.log("onClick called");
            /**/ this.logger.trace("NodeSiblingsView.onclick called. Updating selected node.");

            //console.log("Event: NodeSiblingsView.event['click *'] = this.onClick");
            this.psState.setSelNodeId(this.node.getNodeId());
        };

        NodeSiblingsView.prototype.onMenuIcon = function () {
            console.log("on menu icon clicked");
            /**/ this.logger.trace("NodeSiblingsView.onMenuIcon called.");
            this.setSelAndCurNode();
        };

        NodeSiblingsView.prototype.setSelAndCurNode = function () {
            var srcNode = this.node.getNodeId();
            this.psState.setSelNodeId(srcNode);
            this.psState.setCurNodeId(srcNode);
        };
        return NodeSiblingsView;
    })(Backbone.View);

    //
    var CurrentDisplayMode;
    (function (CurrentDisplayMode) {
        CurrentDisplayMode[CurrentDisplayMode["FMode"] = 0] = "FMode";
        CurrentDisplayMode[CurrentDisplayMode["PMode"] = 1] = "PMode";
    })(CurrentDisplayMode || (CurrentDisplayMode = {}));
    ;

    var ContentView = (function (_super) {
        __extends(ContentView, _super);
        function ContentView(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            /**/ this.logger = log4javascript.getLogger("webapp.ContentView");
            /**/ this.logger.trace("BeginSection(ContentView.constructor)");

            // Event Handlers
            this.events = {
                "mouseover .HintAndHBox": "hilightActiveElement",
                "mouseover .ThisNode": "hilightActiveElement",
                "click .DisplayIdClass": "onDisplayIdSelected"
            };
            this.contextView = new ContextView(psState);
            _.bindAll(this, 'render');

            // Listeners
            this.listenTo(this.psState, "model:stateChanged", this.render);
            this.listenTo(this.psState, "model:selectedNodeChanged", this.render);
            this.listenTo(this.psState, "model:tacticEdited", this.render);
            this.listenTo(this.psState.getViewerState(), "model:minimalAnnoChanged", this.setMinimalAnnoCSSClass);

            //toggleID
            this.listenTo(this.psState, "model:FSelectClicked", this.toggleFDisplayId);
            this.listenTo(this.psState, "model:PSelectClicked", this.togglePDisplayId);

            /**/ this.logger.trace("listening to model:stateChanged, model:selectedNodeChanged, and model:minimalAnnoChanged");
            /**/ this.logger.trace("EndSection(ContentView.constructor)");
        }
        ContentView.prototype.setMinimalAnnoCSSClass = function () {
            if (this.psState.getViewerState().getMinimalAnno()) {
                this.$el.addClass("MinimalAnno");
            } else {
                this.$el.removeClass("MinimalAnno");
            }
        };

        ContentView.prototype.render = function () {
            /**/ this.logger.trace("rendering content view...");
            this.$el.empty();
            this.contextView.render();
            this.$el.append(this.contextView.$el);
            var sn = this.psState.getSelNodeObj();
            console.log("Logging sn...");

            //console.log($(sn));
            //console.log(sn.html());
            this.setMinimalAnnoCSSClass();
            this.$el.addClass("ContentView"); //TODO: Is this really needed.
            this.$el.append(sn.html()); //.html does not contain the root node ".nodeObj"
            this.delegateEvents(this.events);
            return this;
        };

        ///////////////////////////////////////////////////////
        // Toggle ID Functionality
        ///////////////////////////////////////////////////////
        ContentView.prototype.onDisplayIdSelected = function (event) {
            console.log("onDisplayIdSelected");
            var activeElem = $(event.target).closest('.DisplayIdClass');
            var activeElemId = activeElem.attr('displayIdAttr');
            console.log("ActiveElem " + activeElem);
            console.log("ActiveElemId " + activeElemId);
            event.stopPropagation();
            var did = parseInt(activeElemId);
            if (this.currentDisplayMode == 0 /* FMode */) {
                this.psState.displayId = did;
                this.toggleFDisplayId();
            } else {
                this.psState.pdisplayId = did;
                this.togglePDisplayId();
            }
        };

        ContentView.prototype.deHilightAllHilightedElements = function () {
            this.$el.find("div.DisplayIdClass").removeAttr('title');
            this.$el.find("div.DisplayIdClass").removeClass('outline-element');
        };

        ContentView.prototype.hilightActiveElement = function (event) {
            //console.log("hilightActiveElement");
            var activeElem = $(event.target).closest('.DisplayIdClass');
            if (activeElem != null) {
                this.deHilightAllHilightedElements();
                var activeElemId = activeElem.attr('displayIdAttr');

                //Add tool tip
                if (activeElemId != null) {
                    activeElem.attr('title', "ID: " + activeElemId);
                }

                //hilight the border of the activeElem
                activeElem.addClass('outline-element');
                //display the id in status bar
                //$('.caps-right .statusbar .displayIdDiv').html("ID: " + activeElemId);
            }
        };

        ContentView.prototype.toggleFDisplayId = function () {
            this.currentDisplayMode = 0 /* FMode */;
            this.deHilightAllHilightedElements();
            this.$el.find(".displayId").toggle();
            this.$el.toggleClass('ProgramAnnDisplayIdClass DisplayIdClass');

            //Do not display formula display ids when the node is a program node.
            //Edit: Allow for program node as well: Required for RTVInPost2
            this.$el.find(".Invariant").toggleClass('InvariantDisplayIdClass DisplayIdClass');

            this.$el.find(".Pred").toggleClass('PredDisplayIdClass DisplayIdClass');

            this.$el.find(".Var").toggleClass('VarDisplayIdClass TermDisplayIdClass DisplayIdClass');
            this.$el.find(".Const").toggleClass('ConstDisplayIdClass TermDisplayIdClass DisplayIdClass');
            this.$el.find(".ArrSelect").toggleClass('ArrSelectDisplayIdClass TermDisplayIdClass DisplayIdClass');
            this.$el.find(".ArrStore").toggleClass('ArrStoreDisplayIdClass TermDisplayIdClass DisplayIdClass');
            this.$el.find(".QTerm").toggleClass('QTermDisplayIdClass TermDisplayIdClass DisplayIdClass');
            this.$el.find(".FnApp").toggleClass('FnAppDisplayIdClass TermDisplayIdClass DisplayIdClass');

            this.$el.find(".True1").toggleClass('True1DisplayIdClass  FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".False1").toggleClass('False1DisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Not").toggleClass('NotDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".And").toggleClass('AndDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Or").toggleClass('OrDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Impl").toggleClass('ImplDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Iff").toggleClass('IffDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Forall").toggleClass('ForallDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Exists").toggleClass('ExistsDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
            this.$el.find(".Unknown").toggleClass('UnknownDisplayIdClass FormulaDisplayIdClass DisplayIdClass');

            this.goToBottom();
        };

        ContentView.prototype.togglePDisplayId = function () {
            this.currentDisplayMode = 1 /* PMode */;
            this.deHilightAllHilightedElements();

            //this.$el.find(".displayId").toggle();
            this.$el.find(".ProgramAnn").toggleClass('ProgramAnnDisplayIdClass DisplayIdClass');
        };

        ContentView.prototype.goToBottom = function () {
            //Go to bottom of the div
            $('.caps-right').scrollTop($('.caps-right')[0].scrollHeight);
        };
        return ContentView;
    })(Backbone.View);

    ////
    var ContextView = (function (_super) {
        __extends(ContextView, _super);
        function ContextView(psState, options) {
            _super.call(this, options);
            /**/ this.logger = log4javascript.getLogger("webapp.ContextView");
            this.events = {};
            this.psState = psState;
        }
        ContextView.prototype.render = function () {
            /**/ this.logger.trace("rendering contextview...");
            this.$el.empty();
            var sn = this.psState.getSelNode();
            var ctx = sn.getContext().contextObj;
            this.$el.append(ctx.html());

            //this.$el.append("<div>CONTEXT</div>");
            console.log("ContextView");
            console.log(sn.getContext().contextObj);
            return this;
        };
        return ContextView;
    })(Backbone.View);

    var KeyBinder = (function (_super) {
        __extends(KeyBinder, _super);
        function KeyBinder(psState, options) {
            _super.call(this, options);
            this.psState = psState;
            //this.events = {
            //    'keydown ctrl+down': psState.selectNextNode,
            //    'keydown ctrl+up': psState.selectPrevNode
            //};
            //$(document).bind('keydown', 'ctrl+down', $.proxy(psState.selectNextNode, psState));
            //$(document).bind('keydown', 'ctrl+up', $.proxy(psState.selectPrevNode, psState));
            //toggle id keystroke is implemented in toggleId.ts
        }
        return KeyBinder;
    })(Backbone.View);

    function initPsDerivation() {
        function initializeLogger() {
            var logger = log4javascript.getLogger("webapp");
            logger.setAdditivity(false);
            logger.setLevel(log4javascript.Level.TRACE);
            return logger;
        }

        function addPopupAppender(logger) {
            var popUpAppender = new log4javascript.PopUpAppender();

            //var popUpLayout = new log4javascript.PatternLayout("%d{HH:mm:ss} %-5p - %m%n");
            var popUpLayout = new log4javascript.PatternLayout("%logger %-5p %d ::: %m%n");
            popUpAppender.setLayout(popUpLayout);
            var overrideThreshold = false;
            if (overrideThreshold) {
                popUpAppender.setThreshold(log4javascript.Level.ERROR);
            }
            logger.addAppender(popUpAppender);
        }

        function addAjaxAppender(logger) {
            var ajaxAppender = new log4javascript.AjaxAppender("logClientMsg");
            var jsonLayout = new log4javascript.JsonLayout;
            ajaxAppender.setLayout(jsonLayout);
            ajaxAppender.addHeader("Content-Type", "application/json");
            var overrideAjaxThreshold = false;
            if (overrideAjaxThreshold) {
                ajaxAppender.setThreshold(log4javascript.Level.ERROR);
            }
            logger.addAppender(ajaxAppender);
        }

        function loggerSetup(enablePopupAppender, enableAjaxAppender) {
            var logger = initializeLogger();
            if (enablePopupAppender) {
                addPopupAppender(logger);
            }
            if (enableAjaxAppender) {
                addAjaxAppender(logger);
            }
        }

        function startAppView() {
            var logger = log4javascript.getLogger("webapp");
            /**/ logger.trace("BeginSection(PSDerivation.ts document ready)");
            /**/ logger.trace("Initialized the webapp logger");
            var appView = new AppView(stm.gPSState, { el: 'body' });
            appView.start();
            /**/ logger.trace("EndSection(PSDerivation.ts document ready)");
        }

        var enablePopupAppender = false;
        var enableAjaxAppender = false;
        loggerSetup(enablePopupAppender, enableAjaxAppender);
        startAppView();

        //Initialize the menubar
        psMenuBar.init();

        //Intialize toggle id
        toggleIdModule.init();

        //Intialize options handler
        optionsHandler.init();

        minimiazableDivModule.init();
        minimizableWithTitleModule.init();

        foldableDivModule.init();
    }

    exports.init = _.once(initPsDerivation);

    $(document).ready(function () {
        exports.init();
    });
});
//# sourceMappingURL=PSDerivation.js.map
