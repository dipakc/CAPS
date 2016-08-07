/// <reference path="typings/underscore/underscore.d.ts" />
/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/log4javascript/log4javascript.d.ts" />

"use strict";
var log4javascript = require('log4javascript');
import pju = require("PSJqueryUtils"); pju;
declare var jsRoutes: any;
var appRoutes = jsRoutes.controllers.Application;
//---------------------------------

/**/var smlogger = log4javascript.getLogger("webapp.synthtree_models");

//TODO: move to utils
function origin() {
    return window.location.protocol + "//" + window.location.host;
}

export interface IBackboneEvents {
    on(eventName: string, callback: (...args: any[]) => void , context?: any): any;
    listenTo(object: any, events: string, callback: (...args: any[]) => void ): any;
    //off(eventName?: string, callback?: (...args: any[]) => void , context?: any): any;
    trigger(eventName: string, ...args: any[]): any;
}

export class BackboneEventsImpl implements IBackboneEvents {
    on: (eventName: string, callback: (model: any, error: any) => any) => any;
    listenTo: (object: any, eventName: string, callback: (model: any, error: any) => any) => any;
    //off: (event: string) => any;
    trigger: (event: string, data?: any) => any;

    constructor() {
        _.extend(this, Backbone.Events);
    }
}

module NodeMapExtractor {
    var idNodeMap = {};
    /**/var logger = log4javascript.getLogger("webapp.NodeMapExtractor");

    export function extractIdNodeMap($data: JQuery): {} {
        /**/logger.trace("in NodeMapExtractor->extractIdNodeMap");
        var $rootNode: JQuery = (<any>$data).closest_descendent('.node');
        var rootNodeModel = processNode($rootNode, null, idNodeMap);
        return idNodeMap;
    }

    export function processNode($node, parent: SynthNodeModel, nodeMapParam: Object): SynthNodeModel {
        /**/logger.trace("in NodeMapExtractor->processNode");
        //step1 : create the node model (without childs)
        var nodeModel = createNodeModel($node, parent);
        //step2 : create the child nodes (with parent set properly)
        var childNodes = $node.children('.node');
        var childNodeModels = _.map(childNodes, (child) => {
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

    export function createNodeModel($node: JQuery, parent: SynthNodeModel) {
        /**/logger.trace("in NodeMapExtractor->createNodeModel");
        var nodeId: number = parseInt($node.children('.nodeid').text().trim());
        var nodeObj = $node.children('.nodeObj');
        var tacticName: String = $node.children('.tactic').text();
        var contextObj = $node.children('.contextObj');
        var contextModel = new ContextModel(contextObj);
        var tactic: TacticModel = new TacticModel(tacticName);
        var retVal = new SynthNodeModel(nodeId, nodeObj, tactic, contextModel, parent, []);
        return retVal;
    }
}

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

module PSStateJQueryUtils {
    /**/var logger = log4javascript.getLogger("webapp.PSStateJQueryUtils");

    export function getNodeId(node: JQuery): number {
        return parseInt($.trim(node.children(".nodeid").first().text()));
    }

    export function getNodeElem(tree: JQuery, nodeId: number): JQuery {
        var $nodeElem = tree.find('.node').filter(function () {
            var $nodeIdElem = $(this).children('.nodeid').first();
            var iNodeId: number = parseInt($nodeIdElem.text().trim());
            return iNodeId === nodeId;
        });
        console.assert($nodeElem.length > 0);
        return $nodeElem;
    }
}

export class ViewerState extends BackboneEventsImpl {
    private _minimalAnno: boolean;
    /**/private logger: Log4Javascript;

    constructor() {
        super();
        /**/this.logger = log4javascript.getLogger("webapp.ViewerState");
        /**/this.logger.trace("BeginSection(ViewerState.constructor)");
        this._minimalAnno = true;
        /**/this.logger.trace("EndSection(PSState.constructor)");
    }

    setMinimalAnno(flag: boolean) {
        this._minimalAnno = flag;
        this.trigger("model:minimalAnnoChanged");
        //TODO: should the trigger name be changed to viewerState:minimalAnnoChanged
        //TODO: should the PSState trigger this instead of this class.
    }

    getMinimalAnno() {
        return this._minimalAnno;
    }
}

export class PSState extends BackboneEventsImpl {

    private _synthTree: any; //TODO: rename synthTree as $synthTree
    private _selNodeId: number;
    private _curNodeId: number;
    private _idNodeMap: {};
    private _viewerState: ViewerState;
    private _displayId: number;
    private _pdisplayId: number;

    /**/private logger: Log4Javascript;

    constructor() {
        super();
        this._viewerState = new ViewerState();
        /**/this.logger = log4javascript.getLogger("webapp.PSState");
        /**/this.logger.trace("BeginSection(PSState.constructor)");
        this._idNodeMap = {};
        this._displayId = -1;
        this._pdisplayId = -1;
        //_.extend(this, Backbone.Events);
        _.bindAll(this, 'getStateFromServerHandler');
        /**/this.logger.trace("EndSection(PSState.constructor)");
    }

    public get displayId():number {
        return this._displayId;
    }

    public set displayId(value: number) {
        this._displayId = value;
        this.trigger("model:DisplayIdSelected");
    }

    public get pdisplayId(): number {
        return this._pdisplayId;
    }

    public set pdisplayId(value: number) {
        this._pdisplayId = value;
        this.trigger("model:PDisplayIdSelected");
    }

    getViewerState() {
        return this._viewerState;
    }
    //------------------------------------------
    //Setters
    private init(tree, curNode: number, selNode: number, idNodeMap: Object) {
        /**/this.logger.trace("BeginSection(PSState.init)");
        this._synthTree = tree;
        this._curNodeId = curNode;
        this._selNodeId = selNode;
        this._idNodeMap = idNodeMap;
        this.trigger("model:stateChanged");
        /**/this.logger.trace("EndSection(PSState.init)");
    }

    /*When formula selection button is clicked on the FDisplayIdView*/
    onFSelectClick() {
        this.logger.trace("FSelect clicked");
        this.trigger("model:FSelectClicked");
    }

    onPSelectClick() {
        this.logger.trace("PSelect clicked");
        this.trigger("model:PSelectClicked");
    }

    setSelNodeId(nodeId: number) {
        console.assert(this._idNodeMap[nodeId] !== undefined);
        if (this._selNodeId !== nodeId) {
            this._selNodeId = nodeId;
            this.trigger("model:selectedNodeChanged");
        }
    }

    setCurNodeId(nodeId: number) {
        console.assert(this._idNodeMap[nodeId] !== undefined);
        if (this._curNodeId !== nodeId) {
            this._curNodeId = nodeId;
            //Do not sync with server.
            //Any future request to server should sync the current node.
            //Edit: Syncing with applytactic will complicate things.
            this.setCurrentNodeOnServer(nodeId);            
        }
    }

    delSelNodeId() {
        var selId = this.getSelNodeId();
        console.assert(this._idNodeMap[selId] !== undefined);        
        /**/this.logger.trace("Delete Selected Node On Server");
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
            success: (synthTree) => {
                /**/this.logger.trace("delSelNodeId request successful");
                //this._curNodeId = newCurNodeId;
                this.getStateFromServer();
                this.trigger("model:stateChanged");
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/this.logger.trace("delSelNodeId request Failed!!");
                /**/this.logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    //delSubTreeFromSynthTree(nodeId: number) {
        //this._synthTree.remove("") //TODO: Also update the synthTree
        

    //}

    addNode($nodeDiv) {
        /**/this.logger.trace("addNode");
        var newNodeId: number = PSStateJQueryUtils.getNodeId($nodeDiv);
        var curNodeId = this.getCurNodeId();

        if (curNodeId != newNodeId) {
            //JQuery: add new node div to current node.
            var curNode: JQuery = this.getNodeElem(curNodeId);
            curNode.append($nodeDiv);

            //Model: Add the new node to the map
            var newNodeModel = NodeMapExtractor.createNodeModel($nodeDiv, this.getCurNode());
            this._idNodeMap[newNodeId] = newNodeModel
                
            //fix the branching issue
            //Model: Register newNodeModel as child of the current Node
            this.getCurNode()._childs.push(newNodeModel)

            //Update the selected node and the current node
            this.setCurNodeId(newNodeId);
            this.setSelNodeId(newNodeId);
            this.trigger("model:nodeAdded");
        }
    }

    replaceHeadNode($nodeDiv) {
        /**/this.logger.trace("replaceHeadNode");
        var newNodeId: number = PSStateJQueryUtils.getNodeId($nodeDiv);
        var curNodeId = this.getCurNodeId();

        if (curNodeId == newNodeId) {
            //JQuery: add new node div to "parent of current node".
            var curNode: JQuery = this.getNodeElem(curNodeId);
            curNode.replaceWith($nodeDiv);

            //Model: Add the new node to the map
            var parentNode = this.getCurNode().getParent();
            var newNodeModel = NodeMapExtractor.createNodeModel($nodeDiv, parentNode);
            this._idNodeMap[newNodeId] = newNodeModel

            //Model: replace curNode with newNodeModel in the childs of the parent node.
            var newChilds = _.map(parentNode._childs, function (child) { 
                var childNode = <SynthNodeModel> child;
                if (childNode.getNodeId() == curNodeId)
                    return newNodeModel;
                else 
                    return childNode
            });
            parentNode._childs = newChilds;
                        
            this.trigger("model:tacticEdited");
        } else {
            alert("Head node id and new node id are different.");
        }
    }

    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    //-------------------------------------------
    //Getters
    getSynthTree() { return this._synthTree; }
    getSelNodeId() { return this._selNodeId; }
    getCurNodeId() { return this._curNodeId; }
    getSynthNode(nodeId: number) { return this._idNodeMap[nodeId]; }

    private getNodeElem(nodeId: number): JQuery {
        return PSStateJQueryUtils.getNodeElem($(this.getSynthTree()), nodeId);
    }

    private getSelNodeJQ(): JQuery {
        return this.getNodeElem(this.getSelNodeId());
    }

    getSelNode(): SynthNodeModel {
        return this.getSynthNode(this.getSelNodeId());
    }

    getCurNode(): SynthNodeModel {
        return this.getSynthNode(this.getCurNodeId());
    }

    getSelNodeObj(): any {
        return this.getSelNode().getNodeObj();
    }

    getCurNodeObj(): any {
        return this.getCurNode().getNodeObj();
    }
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    getStateFromServer() {
        var offline = false
        if (!offline) {
            /**/this.logger.trace("Registering getStateFromServerHandler to process state information...");
            /**/this.logger.trace("Getting state from the server...");
            var derivName = pju.extractLastUrlSegment();
            var r = appRoutes.getState(derivName);
            $.ajax({
                dataType: 'html',
                url: r.url,
                type: r.method,
                success: this.getStateFromServerHandler,
                error: (errorDiv) => {
                    var r2 = appRoutes.derivations();
                    window.location.href = r2.url;
                }
            });
        } else {
            this.getStateFromStoredElement();
        }
    }

    private getStateFromServerHandler(data) {
        this.logger.trace("data retrieved from server");        
        /**/this.logger.trace("State information received from the server...");
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
    }

    /*Useful in offline html files. Call from getStateFromServer() */
    private getStateFromStoredElement() {
        //var data = $('body').data('ServerData');
        var data = $('body .ServerData').children(":first");
        this.getStateFromServerHandler(data);
    }
    //------------------------------------------------------
    applyTactic(inputData, derivName) {
        var data = JSON.stringify(inputData);
        /**/this.logger.trace("sending request to server. data:");
        /**/this.logger.trace("Sending request to server...");
        /**/this.logger.trace(data);
        var r = appRoutes.applyTactic(derivName);        
        var applyTacticUrl =  r.url;
        $.ajax({
            url: applyTacticUrl,
            type: r.method,
            data: data,
            contentType: 'application/json; charset=utf-8',
            dataType: 'html',
            success: (nodeDiv) => {
                var $nodeDiv = $(nodeDiv);
                this.addNode($nodeDiv);
                $(".ApplyTacticError").empty(); //TODO: Move to Error view
            },
            error: (errorDiv) => {
                this.logger.trace("applytactic error");       
                console.log($(".ApplyTacticError"));
                //TODO: Move to Error view
                $(".ApplyTacticError").html("<div>" + errorDiv.responseText + "</div>");
            }
        });

        return false;
    }

    editTactic(inputData, derivName) {
        var data = JSON.stringify(inputData);
        /**/this.logger.trace("Sending request to server. data:");
        /**/this.logger.trace("Sending request to server...");
        /**/this.logger.trace(data);
        var r = appRoutes.editTactic(derivName);
        var editTacticUrl = r.url;
        $.ajax({
            url: editTacticUrl,
            type: r.method,
            data: data,
            contentType: 'application/json; charset=utf-8',
            dataType: 'html',
            success: (nodeDiv) => {
                var $nodeDiv = $(nodeDiv);
                this.replaceHeadNode($nodeDiv);
                //TODO: Move to Error view
                $(".EditTacticError").empty(); 
            },
            error: (errorDiv) => {
                this.logger.trace("applytactic error");
                console.log($(".ApplyTacticError"));
                //TODO: Move to Error view
                $(".EditTacticError").html("<div>" + errorDiv.responseText + "</div>");
            }
        });
        return false;
    }

    setCurrentNodeOnServer(curNodeId: number) {
        /**/this.logger.trace("Set Current Node On Server");
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
            success: () => {
                /**/this.logger.trace("current Node request successfuly.");
                this.trigger("model:currentNodeChanged");
                $(".ApplyTacticError").empty();
            },
            error: () => {
                /**/this.logger.trace("current Node request Failed.");
                $(".ApplyTacticError").html("<div>Failed to head node.</div>");
            }
        });        
    }

    test() {
        this.logger.trace("test called");
    }
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
    getRightMostPath(node: SynthNodeModel): SynthNodeModel[]{
        /**/this.logger.trace("getRightMostPath");
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
    }

    //Utilities
    // Condition 1: The returned path must contain the selNode
    // Condition 2: If possible, the returned path should contain the curNode
    // Condition 3: If there are multiple paths satisfying condition1 and condition 2
    // select the rightmost path.
    getPathOfSelNode(): SynthNodeModel[]{
        /**/this.logger.trace("getPathOfSelNode");
        var selNode = this.getSelNode();
        var curNode = this.getCurNode();
        var curPath = this.getRightMostPath(curNode);
        if (_.contains(curPath, selNode))
            return curPath;
        else {
            return this.getRightMostPath(selNode);
        }
    }

    getLeftRightSiblings(node: SynthNodeModel) {
        /**/this.logger.trace("getLeftRightSiblings of node" + node._nodeId);
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
    }

    getTVFrmServer(derivName, nodeId) {
        var r = appRoutes.getNodeTV(derivName, nodeId);
        var url = r.url;
        var ret = undefined;
        var self = this;
        $.ajax({
            async: false,
            url: url,
            type: r.method,
            dataType: 'json',
            success: (tv) => {
                ret = tv;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/self.logger.trace("getNodeTV request Failed!!");
                /**/self.logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
        return ret;
    } 
}

export class SynthNodeModel extends BackboneEventsImpl {

    _nodeId: number;
    _nodeObj: any;
    _tactic: TacticModel;
    _context: ContextModel;
    _parent: SynthNodeModel;
    _childs: SynthNodeModel[];
    /**/logger: Log4Javascript;

    constructor(nodeId: number, nodeObj: any, tactic: TacticModel, context: ContextModel, parent: SynthNodeModel, childs: SynthNodeModel[]) {
        super();
        /**/this.logger = log4javascript.getLogger("webapp.SynthNodeModel");
        //_.extend(this, Backbone.Events);
        this._nodeId = nodeId;
        this._nodeObj = nodeObj;
        this._tactic = tactic;
        this._context = context;
        this._parent = parent;
        this._childs = childs;
    }

    getNodeId() { return this._nodeId; }
    getNodeObj() { return this._nodeObj; }
    getTactic() { return this._tactic; }
    getContext() { return this._context; }
    getParent() { return this._parent; }
    getChilds() { return this._childs; }
    isLeaf() { return (this._childs.length == 0);}
}

export class TacticModel extends BackboneEventsImpl {
    constructor(public tacticName: String) {
        super();
    }
}

export class ContextModel extends BackboneEventsImpl {
    contextObj: any;
    constructor(contextObj: any) {
        super();
        this.contextObj = contextObj;
    }
}

export var gPSState = new PSState();
/**/smlogger.trace("create gPSState: PSState");

