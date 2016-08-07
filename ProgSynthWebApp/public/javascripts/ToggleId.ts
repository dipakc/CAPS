/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jqueryui/jqueryui.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />


"use strict";
export var _dummyExport = undefined;

function deHilightAllHilightedElements() {
    $(".caps-content div.DisplayIdClass").removeAttr('title');
    $(".caps-content div.DisplayIdClass").removeClass('outline-element');
}

function hilightActiveElement(event) {
    console.log("hilightActiveElement");
    var activeElem = $(event.target).closest('.DisplayIdClass');
    if (activeElem != null) {
        deHilightAllHilightedElements();
        var activeElemId = activeElem.attr('displayIdAttr');
        //Add tool tip 
        if (activeElemId != null) {
            activeElem.attr('title', "ID: " + activeElemId);
        }
        //hilight the border of the activeElem
        activeElem.addClass('outline-element');
        //display the id in status bar
        $('.caps-right .statusbar .displayIdDiv').html("ID: " + activeElemId);
    }
}

function toggleDisplayId() {
    deHilightAllHilightedElements();
    $(".caps-content .displayId").toggle();

    $(".caps-content").toggleClass('ProgramAnnDisplayIdClass DisplayIdClass');

    //Do not display formula display ids when the node is a program node.
    //Edit: Allow for program node as well: Required for RTVInPost2
    $(".caps-content .Invariant")/*.not(".caps-content .ProgramAnn .Invariant")*/.toggleClass('InvariantDisplayIdClass DisplayIdClass');

    $(".caps-content .Pred")/*.not(".caps-content .ProgramAnn .Pred")*/.toggleClass('PredDisplayIdClass DisplayIdClass');

    $(".caps-content .Var")/*.not(".caps-content .ProgramAnn .Var")*/.toggleClass('VarDisplayIdClass TermDisplayIdClass DisplayIdClass');
    $(".caps-content .Const")/*.not(".caps-content .ProgramAnn .Const")*/.toggleClass('ConstDisplayIdClass TermDisplayIdClass DisplayIdClass');
    $(".caps-content .ArrSelect")/*.not(".caps-content .ProgramAnn .ArrSelect")*/.toggleClass('ArrSelectDisplayIdClass TermDisplayIdClass DisplayIdClass');
    $(".caps-content .ArrStore")/*.not(".caps-content .ProgramAnn .ArrStore")*/.toggleClass('ArrStoreDisplayIdClass TermDisplayIdClass DisplayIdClass');
    $(".caps-content .QTerm")/*.not(".caps-content .ProgramAnn .QTerm")*/.toggleClass('QTermDisplayIdClass TermDisplayIdClass DisplayIdClass');
    $(".caps-content .FnApp")/*.not(".caps-content .ProgramAnn .FnApp")*/.toggleClass('FnAppDisplayIdClass TermDisplayIdClass DisplayIdClass');

    $(".caps-content .True1")/*.not(".caps-content .ProgramAnn .True1")*/.toggleClass('True1DisplayIdClass  FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .False1")/*.not(".caps-content .ProgramAnn .False1")*/.toggleClass('False1DisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Not")/*.not(".caps-content .ProgramAnn .Not")*/.toggleClass('NotDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .And")/*.not(".caps-content .ProgramAnn .And")*/.toggleClass('AndDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Or")/*.not(".caps-content .ProgramAnn .Or")*/.toggleClass('OrDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Impl")/*.not(".caps-content .ProgramAnn .Impl")*/.toggleClass('ImplDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Iff")/*.not(".caps-content .ProgramAnn .Iff")*/.toggleClass('IffDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Forall")/*.not(".caps-content .ProgramAnn .Forall")*/.toggleClass('ForallDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Exists")/*.not(".caps-content .ProgramAnn .Exists")*/.toggleClass('ExistsDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
    $(".caps-content .Unknown")/*.not(".caps-content .ProgramAnn .Unknown")*/.toggleClass('UnknownDisplayIdClass FormulaDisplayIdClass DisplayIdClass');

    $('.caps-right .statusbar .displayIdDiv').html('');

}

function toggleDisplayIdAndGoToBottom() {
    toggleDisplayId();
    //Go to bottom of the div
    $('.caps-right').scrollTop($('.caps-right')[0].scrollHeight);
}

function selectClickedElement(event) {
    // Get the outlined element
    console.log("in mousedown");
    var activeElem = $(this).find('.outline-element');
    // Get the id
    var activeElemId = activeElem.attr('displayIdAttr');
    console.log(activeElemId);
    if (activeElemId != null) {
        console.log("selected element " + activeElemId);
        toggleDisplayIdAndGoToBottom();
    }
}

function toggleDbg() {
    //a utility function to hide the debug elements.    
    console.log("toggleDbg called");
    if ((<any>toggleDbg).show == undefined) {
        (<any>toggleDbg).show = true;
    }
    else {
        (<any>toggleDbg).show = !(<any>toggleDbg).show;
    }

    if ((<any>toggleDbg).show == true) {
        $(".ProgramAnn .id").show();
        //$(".posummary").show(); //TODO: enable these lines
        //$(".nodeObjDbg").show();
        //$(".pa_proofobligs").show();
        //$(".top").show();
        $(".statusbar").show();

    } else {
        $(".ProgramAnn .id").hide();
        //$(".posummary").hide(); //TODO: enable these lines
        //$(".nodeObjDbg").hide();
        //$(".pa_proofobligs").hide();
        //$(".top").hide();
        $(".statusbar").hide();
    }
}

function main() {
    $("#toggleIdBtn.topButton").click(toggleDisplayIdAndGoToBottom);

    //Keyboard shortcut for displaying the id  and going to bottom of the page.
    $(document).bind('keydown', 'ctrl+F7', toggleDisplayIdAndGoToBottom);

    //$(".caps-content").on({
    //    mouseover: hilightActiveElement
    //});

    //$(".caps-content").on({
    //    mousedown: selectClickedElement
    //});
    // For some reason can not call it directly from console, hence binding to Ctrl+F9   
    // Update: Disabled toggleDbg
    //$(document).bind('keydown', 'ctrl+F9', toggleDbg); 
}

export var init = _.once(main);