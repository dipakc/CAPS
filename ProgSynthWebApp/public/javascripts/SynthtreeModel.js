/// <reference path="typings/underscore/underscore.d.ts" />
/// <reference path="typings/backbone/backbone.d.ts" />
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
define(["require", "exports", "PSJqueryUtils"], function(require, exports, pju) {
    var log4javascript = require('log4javascript');
    pju;

    var appRoutes = jsRoutes.controllers.Application;

    //---------------------------------
    /**/ var smlogger = log4javascript.getLogger("webapp.synthtree_models");

    //TODO: move to utils
    function origin() {
        return window.location.protocol + "//" + window.location.host;
    }

    var BackboneEventsImpl = (function () {
        function BackboneEventsImpl() {
            _.extend(this, Backbone.Events);
        }
        return BackboneEventsImpl;
    })();
    exports.BackboneEventsImpl = BackboneEventsImpl;

    var NodeMapExtractor;
    (function (NodeMapExtractor) {
        var idNodeMap = {};
        /**/ var logger = log4javascript.getLogger("webapp.NodeMapExtractor");

        function extractIdNodeMap($data) {
            /**/ logger.trace("in NodeMapExtractor->extractIdNodeMap");
            var $rootNode = $data.closest_descendent('.node');
            var rootNodeModel = processNode($rootNode, null, idNodeMap);
            return idNodeMap;
        }
        NodeMapExtractor.extractIdNodeMap = extractIdNodeMap;

        function processNode($node, parent, nodeMapParam) {
            /**/ logger.trace("in NodeMapExtractor->processNode");

            //step1 : create the node model (without childs)
            var nodeModel = createNodeModel($node, parent);

            //step2 : create the child nodes (with parent set properly)
            var childNodes = $node.children('.node');
            var childNodeModels = _.map(childNodes, function (child) {
                var $child = $(child);
                var childNodeModel = processNode($child, nodeModel, nodeMapParam);
                return childNodeModel;
            });

            //step3: set the childs
            nodeModel._childs = childNodeModels;

            //step4: add to map
            nodeMapParam[nodeModel.getNodeId()] = nodeModel;

            //srep5: return the model
            return nodeModel;
        }
        NodeMapExtractor.processNode = processNode;

        function createNodeModel($node, parent) {
            /**/ logger.trace("in NodeMapExtractor->createNodeModel");
            var nodeId = parseInt($node.children('.nodeid').text().trim());
            var nodeObj = $node.children('.nodeObj');
            var tacticName = $node.children('.tactic').text();
            var contextObj = $node.children('.contextObj');
            var contextModel = new ContextModel(contextObj);
            var tactic = new TacticModel(tacticName);
            var retVal = new SynthNodeModel(nodeId, nodeObj, tactic, contextModel, parent, []);
            return retVal;
        }
        NodeMapExtractor.createNodeModel = createNodeModel;
    })(NodeMapExtractor || (NodeMapExtractor = {}));

    //class PSStateData {
    //    synthTree: any;
    //    selNodeId: number;
    //    curNodeId: number;
    //    idNodeMap: {};
    //    constructor() {
    //    }
    //    init(tree, curNode: number, selNode: number, idNodeMap: Object) {
    //        this.synthTree = tree;
    //        this.curNodeId = curNode;
    //        this.selNodeId = selNode;
    //        this.idNodeMap = idNodeMap;
    //    }
    //}
    var PSStateJQueryUtils;
    (function (PSStateJQueryUtils) {
        /**/ var logger = log4javascript.getLogger("webapp.PSStateJQueryUtils");

        function getNodeId(node) {
            return parseInt($.trim(node.children(".nodeid").first().text()));
        }
        PSStateJQueryUtils.getNodeId = getNodeId;

        function getNodeElem(tree, nodeId) {
            var $nodeElem = tree.find('.node').filter(function () {
                var $nodeIdElem = $(this).children('.nodeid').first();
                var iNodeId = parseInt($nodeIdElem.text().trim());
                return iNodeId === nodeId;
            });
            console.assert($nodeElem.length > 0);
            return $nodeElem;
        }
        PSStateJQueryUtils.getNodeElem = getNodeElem;
    })(PSStateJQueryUtils || (PSStateJQueryUtils = {}));

    var ViewerState = (function (_super) {
        __extends(ViewerState, _super);
        function ViewerState() {
            _super.call(this);
            /**/ this.logger = log4javascript.getLogger("webapp.ViewerState");
            /**/ this.logger.trace("BeginSection(ViewerState.constructor)");
            this._minimalAnno = true;
            /**/ this.logger.trace("EndSection(PSState.constructor)");
        }
        ViewerState.prototype.setMinimalAnno = function (flag) {
            this._minimalAnno = flag;
            this.trigger("model:minimalAnnoChanged");
            //TODO: should the trigger name be changed to viewerState:minimalAnnoChanged
            //TODO: should the PSState trigger this instead of this class.
        };

        ViewerState.prototype.getMinimalAnno = function () {
            return this._minimalAnno;
        };
        return ViewerState;
    })(BackboneEventsImpl);
    exports.ViewerState = ViewerState;

    var PSState = (function (_super) {
        __extends(PSState, _super);
        function PSState() {
            _super.call(this);
            this._viewerState = new ViewerState();
            /**/ this.logger = log4javascript.getLogger("webapp.PSState");
            /**/ this.logger.trace("BeginSection(PSState.constructor)");
            this._idNodeMap = {};
            this._displayId = -1;
            this._pdisplayId = -1;

            //_.extend(this, Backbone.Events);
            _.bindAll(this, 'getStateFromServerHandler');
            /**/ this.logger.trace("EndSection(PSState.constructor)");
        }
        Object.defineProperty(PSState.prototype, "displayId", {
            get: function () {
                return this._displayId;
            },
            set: function (value) {
                this._displayId = value;
                this.trigger("model:DisplayIdSelected");
            },
            enumerable: true,
            configurable: true
        });


        Object.defineProperty(PSState.prototype, "pdisplayId", {
            get: function () {
                return this._pdisplayId;
            },
            set: function (value) {
                this._pdisplayId = value;
                this.trigger("model:PDisplayIdSelected");
            },
            enumerable: true,
            configurable: true
        });


        PSState.prototype.getViewerState = function () {
            return this._viewerState;
        };

        //------------------------------------------
        //Setters
        PSState.prototype.init = function (tree, curNode, selNode, idNodeMap) {
            /**/ this.logger.trace("BeginSection(PSState.init)");
            this._synthTree = tree;
            this._curNodeId = curNode;
            this._selNodeId = selNode;
            this._idNodeMap = idNodeMap;
            this.trigger("model:stateChanged");
            /**/ this.logger.trace("EndSection(PSState.init)");
        };

        /*When formula selection button is clicked on the FDisplayIdView*/
        PSState.prototype.onFSelectClick = function () {
            this.logger.trace("FSelect clicked");
            this.trigger("model:FSelectClicked");
        };

        PSState.prototype.onPSelectClick = function () {
            this.logger.trace("PSelect clicked");
            this.trigger("model:PSelectClicked");
        };

        PSState.prototype.setSelNodeId = function (nodeId) {
            console.assert(this._idNodeMap[nodeId] !== undefined);
            if (this._selNodeId !== nodeId) {
                this._selNodeId = nodeId;
                this.trigger("model:selectedNodeChanged");
            }
        };

        PSState.prototype.setCurNodeId = function (nodeId) {
            console.assert(this._idNodeMap[nodeId] !== undefined);
            if (this._curNodeId !== nodeId) {
                this._curNodeId = nodeId;

                //Do not sync with server.
                //Any future request to server should sync the current node.
                //Edit: Syncing with applytactic will complicate things.
                this.setCurrentNodeOnServer(nodeId);
            }
        };

        PSState.prototype.delSelNodeId = function () {
            var _this = this;
            var selId = this.getSelNodeId();
            console.assert(this._idNodeMap[selId] !== undefined);
            /**/ this.logger.trace("Delete Selected Node On Server");
            var derivName = pju.extractLastUrlSegment();
            var r = appRoutes.deleteNode(derivName);
            var delNodeUrl = r.url;
            var data = { 'value': selId };
            console.log(data);
            $.ajax({
                url: delNodeUrl,
                type: r.method,
                data: JSON.stringify(data),
                contentType: 'application/json; charset=utf-8',
                dataType: 'html',
                success: function (synthTree) {
                    /**/ _this.logger.trace("delSelNodeId request successful");

                    //this._curNodeId = newCurNodeId;
                    _this.getStateFromServer();
                    _this.trigger("model:stateChanged");
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ _this.logger.trace("delSelNodeId request Failed!!");
                    /**/ _this.logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
                }
            });
        };

        //delSubTreeFromSynthTree(nodeId: number) {
        //this._synthTree.remove("") //TODO: Also update the synthTree
        //}
        PSState.prototype.addNode = function ($nodeDiv) {
            /**/ this.logger.trace("addNode");
            var newNodeId = PSStateJQueryUtils.getNodeId($nodeDiv);
            var curNodeId = this.getCurNodeId();

            if (curNodeId != newNodeId) {
                //JQuery: add new node div to current node.
                var curNode = this.getNodeElem(curNodeId);
                curNode.append($nodeDiv);

                //Model: Add the new node to the map
                var newNodeModel = NodeMapExtractor.createNodeModel($nodeDiv, this.getCurNode());
                this._idNodeMap[newNodeId] = newNodeModel;

                //fix the branching issue
                //Model: Register newNodeModel as child of the current Node
                this.getCurNode()._childs.push(newNodeModel);

                //Update the selected node and the current node
                this.setCurNodeId(newNodeId);
                this.setSelNodeId(newNodeId);
                this.trigger("model:nodeAdded");
            }
        };

        PSState.prototype.replaceHeadNode = function ($nodeDiv) {
            /**/ this.logger.trace("replaceHeadNode");
            var newNodeId = PSStateJQueryUtils.getNodeId($nodeDiv);
            var curNodeId = this.getCurNodeId();

            if (curNodeId == newNodeId) {
                //JQuery: add new node div to "parent of current node".
                var curNode = this.getNodeElem(curNodeId);
                curNode.replaceWith($nodeDiv);

                //Model: Add the new node to the map
                var parentNode = this.getCurNode().getParent();
                var newNodeModel = NodeMapExtractor.createNodeModel($nodeDiv, parentNode);
                this._idNodeMap[newNodeId] = newNodeModel;

                //Model: replace curNode with newNodeModel in the childs of the parent node.
                var newChilds = _.map(parentNode._childs, function (child) {
                    var childNode = child;
                    if (childNode.getNodeId() == curNodeId)
                        return newNodeModel;
                    else
                        return childNode;
                });
                parentNode._childs = newChilds;

                this.trigger("model:tacticEdited");
            } else {
                alert("Head node id and new node id are different.");
            }
        };

        //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        //-------------------------------------------
        //Getters
        PSState.prototype.getSynthTree = function () {
            return this._synthTree;
        };
        PSState.prototype.getSelNodeId = function () {
            return this._selNodeId;
        };
        PSState.prototype.getCurNodeId = function () {
            return this._curNodeId;
        };
        PSState.prototype.getSynthNode = function (nodeId) {
            return this._idNodeMap[nodeId];
        };

        PSState.prototype.getNodeElem = function (nodeId) {
            return PSStateJQueryUtils.getNodeElem($(this.getSynthTree()), nodeId);
        };

        PSState.prototype.getSelNodeJQ = function () {
            return this.getNodeElem(this.getSelNodeId());
        };

        PSState.prototype.getSelNode = function () {
            return this.getSynthNode(this.getSelNodeId());
        };

        PSState.prototype.getCurNode = function () {
            return this.getSynthNode(this.getCurNodeId());
        };

        PSState.prototype.getSelNodeObj = function () {
            return this.getSelNode().getNodeObj();
        };

        PSState.prototype.getCurNodeObj = function () {
            return this.getCurNode().getNodeObj();
        };

        //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        PSState.prototype.getStateFromServer = function () {
            var offline = false;
            if (!offline) {
                /**/ this.logger.trace("Registering getStateFromServerHandler to process state information...");
                /**/ this.logger.trace("Getting state from the server...");
                var derivName = pju.extractLastUrlSegment();
                var r = appRoutes.getState(derivName);
                $.ajax({
                    dataType: 'html',
                    url: r.url,
                    type: r.method,
                    success: this.getStateFromServerHandler,
                    error: function (errorDiv) {
                        var r2 = appRoutes.derivations();
                        window.location.href = r2.url;
                    }
                });
            } else {
                this.getStateFromStoredElement();
            }
        };

        PSState.prototype.getStateFromServerHandler = function (data) {
            this.logger.trace("data retrieved from server");
            /**/ this.logger.trace("State information received from the server...");

            // Enable when you want to generate file from which you want to generate the offline file.
            //set to "false" in offline and production mode.
            var generateOfflineMode = true;
            if (generateOfflineMode) {
                /* Store data to facilitate generation of offline html files */
                var $container = $('<div/>').html("<div class=ServerData></div>").contents();
                var $serverData = $container.append($(data));
                $('body').append($serverData);
            }

            var $data = $(data);
            var curNodeId = parseInt($data.children('.curNodeId').text().trim());

            //Remove the curNodeId div from the synthTree since we have already stored it in this.curNodeId
            $data.children('.curNodeId').remove();
            var synthTree = $data;
            var selNodeId = curNodeId;

            //var idNodeMap = this.extractIdNodeMap($data);
            var idNodeMap = NodeMapExtractor.extractIdNodeMap($data);
            this.init(synthTree, curNodeId, selNodeId, idNodeMap);
        };

        /*Useful in offline html files. Call from getStateFromServer() */
        PSState.prototype.getStateFromStoredElement = function () {
            //var data = $('body').data('ServerData');
            var data = $('body .ServerData').children(":first");
            this.getStateFromServerHandler(data);
        };

        //------------------------------------------------------
        PSState.prototype.applyTactic = function (inputData, derivName) {
            var _this = this;
            var data = JSON.stringify(inputData);
            /**/ this.logger.trace("sending request to server. data:");
            /**/ this.logger.trace("Sending request to server...");
            /**/ this.logger.trace(data);
            var r = appRoutes.applyTactic(derivName);
            var applyTacticUrl = r.url;
            $.ajax({
                url: applyTacticUrl,
                type: r.method,
                data: data,
                contentType: 'application/json; charset=utf-8',
                dataType: 'html',
                success: function (nodeDiv) {
                    var $nodeDiv = $(nodeDiv);
                    _this.addNode($nodeDiv);
                    $(".ApplyTacticError").empty(); //TODO: Move to Error view
                },
                error: function (errorDiv) {
                    _this.logger.trace("applytactic error");
                    console.log($(".ApplyTacticError"));

                    //TODO: Move to Error view
                    $(".ApplyTacticError").html("<div>" + errorDiv.responseText + "</div>");
                }
            });

            return false;
        };

        PSState.prototype.editTactic = function (inputData, derivName) {
            var _this = this;
            var data = JSON.stringify(inputData);
            /**/ this.logger.trace("Sending request to server. data:");
            /**/ this.logger.trace("Sending request to server...");
            /**/ this.logger.trace(data);
            var r = appRoutes.editTactic(derivName);
            var editTacticUrl = r.url;
            $.ajax({
                url: editTacticUrl,
                type: r.method,
                data: data,
                contentType: 'application/json; charset=utf-8',
                dataType: 'html',
                success: function (nodeDiv) {
                    var $nodeDiv = $(nodeDiv);
                    _this.replaceHeadNode($nodeDiv);

                    //TODO: Move to Error view
                    $(".EditTacticError").empty();
                },
                error: function (errorDiv) {
                    _this.logger.trace("applytactic error");
                    console.log($(".ApplyTacticError"));

                    //TODO: Move to Error view
                    $(".EditTacticError").html("<div>" + errorDiv.responseText + "</div>");
                }
            });
            return false;
        };

        PSState.prototype.setCurrentNodeOnServer = function (curNodeId) {
            var _this = this;
            /**/ this.logger.trace("Set Current Node On Server");
            var derivName = pju.extractLastUrlSegment();
            var r = appRoutes.setCurNode(derivName);
            var data = { 'value': curNodeId };
            console.log(data);
            $.ajax({
                url: r.url,
                type: r.method,
                data: JSON.stringify(data),
                contentType: 'application/json; charset=utf-8',
                dataType: 'html',
                success: function () {
                    /**/ _this.logger.trace("current Node request successfuly.");
                    _this.trigger("model:currentNodeChanged");
                    $(".ApplyTacticError").empty();
                },
                error: function () {
                    /**/ _this.logger.trace("current Node request Failed.");
                    $(".ApplyTacticError").html("<div>Failed to head node.</div>");
                }
            });
        };

        PSState.prototype.test = function () {
            this.logger.trace("test called");
        };

        //TODO: move these methods to view.
        //selectNextNode() {
        //    var $selectedNode = this.getSelNodeJQ();
        //    //find the next .node
        //    var $nextNode = $selectedNode.children('.node');
        //    //activate the next .node
        //    if ($nextNode.length > 0) {
        //        var nextNodeId = PSStateJQueryUtils.getNodeId($nextNode);
        //        this.setSelNodeId(nextNodeId);
        //    }
        //}
        //selectPrevNode() {
        //    //find the selected .node
        //    var $selectedNode = this.getSelNodeJQ();
        //    //find the prev .node
        //    var $prevNode = $selectedNode.parent('.node');
        //    //activate the prev .node
        //    if ($prevNode.length > 0) {
        //        var prevNodeId = PSStateJQueryUtils.getNodeId($prevNode);
        //        this.setSelNodeId(prevNodeId);
        //    }
        //}
        //Get rightmost path containing the given node
        PSState.prototype.getRightMostPath = function (node) {
            /**/ this.logger.trace("getRightMostPath");
            var iNode = node;
            var iChilds = iNode.getChilds();
            while (iChilds.length > 0) {
                iNode = iChilds[iChilds.length - 1];
                iChilds = iNode.getChilds();
            }
            var retVal = [];

            //RefactorTODO: abstract out this method.
            var jNode = iNode;
            while (jNode.getParent() !== null) {
                retVal.push(jNode);
                jNode = jNode.getParent();
            }
            retVal.push(jNode);

            //return nodeModels;
            return retVal.reverse();
        };

        //Utilities
        // Condition 1: The returned path must contain the selNode
        // Condition 2: If possible, the returned path should contain the curNode
        // Condition 3: If there are multiple paths satisfying condition1 and condition 2
        // select the rightmost path.
        PSState.prototype.getPathOfSelNode = function () {
            /**/ this.logger.trace("getPathOfSelNode");
            var selNode = this.getSelNode();
            var curNode = this.getCurNode();
            var curPath = this.getRightMostPath(curNode);
            if (_.contains(curPath, selNode))
                return curPath;
            else {
                return this.getRightMostPath(selNode);
            }
        };

        PSState.prototype.getLeftRightSiblings = function (node) {
            /**/ this.logger.trace("getLeftRightSiblings of node" + node._nodeId);
            var parentNode = node.getParent();
            var leftSiblings = [];
            var rightSiblings = [];
            if (parentNode !== null) {
                var childs = parentNode.getChilds();
                var nodeIndex = _.indexOf(childs, node);
                leftSiblings = _.take(childs, nodeIndex);
                rightSiblings = _.drop(childs, nodeIndex + 1);
            }
            return [leftSiblings, rightSiblings];
        };

        PSState.prototype.getTVFrmServer = function (derivName, nodeId) {
            var r = appRoutes.getNodeTV(derivName, nodeId);
            var url = r.url;
            var ret = undefined;
            var self = this;
            $.ajax({
                async: false,
                url: url,
                type: r.method,
                dataType: 'json',
                success: function (tv) {
                    ret = tv;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ self.logger.trace("getNodeTV request Failed!!");
                    /**/ self.logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
                }
            });
            return ret;
        };
        return PSState;
    })(BackboneEventsImpl);
    exports.PSState = PSState;

    var SynthNodeModel = (function (_super) {
        __extends(SynthNodeModel, _super);
        function SynthNodeModel(nodeId, nodeObj, tactic, context, parent, childs) {
            _super.call(this);
            /**/ this.logger = log4javascript.getLogger("webapp.SynthNodeModel");

            //_.extend(this, Backbone.Events);
            this._nodeId = nodeId;
            this._nodeObj = nodeObj;
            this._tactic = tactic;
            this._context = context;
            this._parent = parent;
            this._childs = childs;
        }
        SynthNodeModel.prototype.getNodeId = function () {
            return this._nodeId;
        };
        SynthNodeModel.prototype.getNodeObj = function () {
            return this._nodeObj;
        };
        SynthNodeModel.prototype.getTactic = function () {
            return this._tactic;
        };
        SynthNodeModel.prototype.getContext = function () {
            return this._context;
        };
        SynthNodeModel.prototype.getParent = function () {
            return this._parent;
        };
        SynthNodeModel.prototype.getChilds = function () {
            return this._childs;
        };
        SynthNodeModel.prototype.isLeaf = function () {
            return (this._childs.length == 0);
        };
        return SynthNodeModel;
    })(BackboneEventsImpl);
    exports.SynthNodeModel = SynthNodeModel;

    var TacticModel = (function (_super) {
        __extends(TacticModel, _super);
        function TacticModel(tacticName) {
            _super.call(this);
            this.tacticName = tacticName;
        }
        return TacticModel;
    })(BackboneEventsImpl);
    exports.TacticModel = TacticModel;

    var ContextModel = (function (_super) {
        __extends(ContextModel, _super);
        function ContextModel(contextObj) {
            _super.call(this);
            this.contextObj = contextObj;
        }
        return ContextModel;
    })(BackboneEventsImpl);
    exports.ContextModel = ContextModel;

    exports.gPSState = new PSState();
    /**/ smlogger.trace("create gPSState: PSState");
});
//# sourceMappingURL=SynthtreeModel.js.map
