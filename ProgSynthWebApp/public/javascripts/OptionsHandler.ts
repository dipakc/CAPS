/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jqueryui/jqueryui.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
"use strict";

//var toastr = require('toastr');
export var _dummyExport = undefined
//---------------------------------------------------------
//toastr.info("In ps derivation"); 

//Options Handlers
function ShowExprProgHandler() {
    var $theProg = $(".ProgramAnn .program .ExprProg").parent();
    var $theProgramAnn = $theProg.parent();
    $theProgramAnn.siblings("collapse").hide();
    $theProgramAnn.children(".id,.displayId").hide();
    if (localStorage.getItem('ShowExprProg') !== "checked") {
        console.log("Hide Expr Prog");
        $theProgramAnn.children(".pre,.post").hide();
        $theProgramAnn.toggleClass("ProgramAnn", false).css('clear', 'both');
        $theProg.toggleClass("program", false).css('clear', 'both');
    } else {
        console.log("Show Expr Prog");
        $theProgramAnn.children(".pre,.post").show();
        $theProgramAnn.toggleClass("ProgramAnn", true).css('clear', 'both');
        $theProg.toggleClass("program", true).css('clear', 'both');
    }
}

//PopulateOptions from local storage
function populateOptions() {
    //alert('populate Called');
    if (localStorage) {
        if (localStorage.getItem('ShowExprProg') === "checked") {
            console.log("Setting ShowExprProg setting");
            $("#ShowExprProg").attr("checked", "checked");
        }
    }
}

function initOptionsHandler() {
    console.log("OptionasHandler On Document Ready");
    //Call all options handlers
    ShowExprProgHandler();

    //Bind show option dialog to the Options button.
    $("#optionsBtn.topButton").click(function () {
        var optionsDialog = '' +
            '<div id="OptionsId">' +
            '<div>Options</div><br>' +
            '<label class="checkbox">' +
            '<input name="ShowExprProg" id="ShowExprProg" type="checkbox">' +
            'Show ExprProg' +
            '</label>' +
            '</div>';
        $('head').append(optionsDialog)
        populateOptions();
        $('head #OptionsId').dialog();
    });

    //Bind show option dialog to the Options button.
    $("#resetBtn.topButton").click(function () {
        $.ajax({
            url: 'resetTree',
            type: 'POST',
            //data: JSON.stringify(inputData),
            data: JSON.stringify("{}"),
            contentType: 'application/json; charset=utf-8',
            dataType: 'html',
            //async: false,
            success: function () { location.reload(); }
        });
    });

    //UI change hanlders
    $("input[type=checkbox]").on({
        change: function (event) {
            var $this = $(this);
            localStorage[$this.attr("name")] = $this.attr("checked");
            //call handler
            ShowExprProgHandler();
        }
    });

    $("input[type=text],select,textarea").on({
        change: function (event) {
            //alert('checkbox changed');
            var $this = $(this);
            localStorage[$this.attr("name")] = $this.val();
            //call handler
        }
    });
}

export var init = _.once(initOptionsHandler);
