///// <reference path="typings/backbone/backbone.d.ts" />
///// <reference path="typings/requirejs/require.d.ts" />
///// <reference path="typings/toastr/toastr.d.ts" />
///// <reference path="typings/log4javascript/log4javascript.d.ts" />
//"use strict";
//var log4javascript = require('log4javascript');
//// implicit dependencies. Return values not used.
//import pju = require("PSJqueryUtils"); pju;
////"toastr" returns toastr object
//var toastr = require('toastr');
//var html = require('pithy');
//export var _dummyExport = undefined;
////---------------------------------
//var logger = log4javascript.getLogger("webapp.SavedDerivations");
//var $tableRow = $('body #caps-derivations-table > .ui.table tr');
//function origin() {
//    return window.location.protocol + "//" + window.location.host;
//}
//function notImplementedError() {
//    alert('This functionality is not implemented.');
//}
////Refactor: Duplicate code (psMenuBar.ts)
//function newDerivation() {
//    var newDerivationUrl = origin() + "/newDerivation";
//    /**/logger.trace("Sending ajax request: newDerivationUrl = " + newDerivationUrl);
//    $.ajax({
//        url: newDerivationUrl,
//        type: 'POST',
//        contentType: 'application/json; charset=utf-8',
//        data: JSON.stringify({}),
//        dataType: 'json',
//        success: (derivName) => {
//            var redirectUrl = origin() + "/derivation/" + derivName;
//            /**/logger.trace("newDerivation request successful. derivName:" + derivName);
//            /**/logger.trace("Redirecting to :" + redirectUrl);
//            window.location.href = redirectUrl;
//        },
//        error: (jqXHR, textStatus, errorThrown) => {
//            /**/logger.trace("ajax request: " + newDerivationUrl + " failed.");
//            /**/logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
//        }
//    });
//}
////Refactor: Duplicate code (psMenuBar.ts)
//function logout() {
//    var logoutUrl = origin() + "/logout";
//    /**/logger.trace("Sending ajax request: logoutUrl = " + logoutUrl);
//    $.ajax({
//        url: logoutUrl,
//        type: 'POST',
//        contentType: 'application/json; charset=utf-8',
//        data: JSON.stringify({}),
//        dataType: 'html',
//        success: () => {
//            logger.trace("logout successful");
//            //window.location.reload(true);
//            login();
//        },
//        error: (jqXHR, textStatus, errorThrown) => {
//            /**/logger.trace("ajax request: " + logoutUrl + " failed.");
//            /**/logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
//        }
//    });
//}
////Refactor: Duplicate code (psMenuBar.ts)
//function login() {
//    //window.open('login', '_self', false)
//    window.location.href = 'login';
//}
//function openDerivation(derivName) {
//    var redirectUrl = origin() + "/derivation/" + derivName;
//    /**/logger.trace("Opening derivatino: " + derivName);
//    window.location.href = redirectUrl;
//}
//function importDerivation() {
//    notImplementedError();
//}
//function getSelDerivations(): string[] {
//    var $tbody = (<any>$('#caps-derivations-table > table.ui.table > tbody')).single();
//    var $activeDerivs = $tbody.find("> tr.active > td");
//    var selNames = $.map($activeDerivs, function (row) {
//        //return $(row).attr('id');
//        return $(row).html();
//    });
//    return selNames;
//}
//function renameDerivation() {
//    toastr.options = {
//        //"closeButton": true,
//        //"debug": false,
//        //"newestOnTop": false,
//        //"progressBar": false,
//        //"positionClass": "toast-top-left",
//        //"preventDuplicates": false,
//        //"onclick": null,
//        //"showDuration": "3000000",
//        //"hideDuration": "1000",
//        //"timeOut": "50000"
//        //"extendedTimeOut": "1000",
//        //"showEasing": "swing",
//        //"hideEasing": "linear",
//        //"showMethod": "fadeIn",
//        //"hideMethod": "fadeOut"
//    }
//    var modalSelector = "#renameModal.ui.modal";
//    //message helper functions
//    function hideMessage() {
//        $(modalSelector + " .message").addClass("hidden");
//    }
//    function activateMessage() {
//        $(modalSelector + " .message").removeClass("hidden");
//        $(modalSelector + " .message").addClass("active");
//    }
//    function clearMessage() {
//        $(modalSelector + " .message .area").html("");
//    }
//    function setMessage(msg) {
//        $(modalSelector + " .message .area").html(msg);
//    }
//    function clearInput() {
//        $(modalSelector + " .ui.input input").val("");
//    }
//    //Helper functions
//    function initModal() {
//        hideMessage();
//        clearMessage();
//        clearInput();
//        //Clear message on input change
//        $(modalSelector + " input").on("input", null, null, function () {
//            hideMessage();
//            clearMessage();
//        });
//    }
//    function renameRequest(oldName, newName) {
//        var retVal = [false, "Failed"]
//        return retVal;
//    }
//    function setModalMsg(msg: string) {
//        activateMessage();
//        setMessage(msg);
//    }
//    //Get the selected derivation name
//    var selNames = getSelDerivations();
//    if (selNames.length != 1) {
//        toastr.error("Select a single derivation.");
//        return;
//    }
//    var oldName = selNames[0];
//    // Init the rename modal
//    initModal();
//    // Show the rename modal
//    (<any>$(modalSelector))
//        .modal({
//            onDeny: function () {
//                toastr.info("Rename operation cancelled");
//                console.log("Rename operation cancelled");
//                return true;
//            },
//            onApprove: function () {
//                var newName = $(modalSelector + " .ui.input input").val();
//                var retVal = renameRequest(oldName, newName);
//                var status = retVal[0];
//                var msg = <string>retVal[1];
//                if (status) {
//                    toastr.info("Rename successful");
//                    console.log("Rename successful");
//                    return true;
//                } else {
//                    setModalMsg(msg);
//                    console.log("Error: " +  msg);
//                    return false;
//                }
//            }
//        })
//        .modal('show');
//}
//function copyDerivation() {
//    notImplementedError();
//}
//function downloadDerivation() {
//    notImplementedError();
//}
//function deleteDerivation() {
//    notImplementedError();
//}
//function logoutDerivation() {
//    notImplementedError();
//}
//function bindMenuEvents() {
//    function bindMenu(menuSelector, handler) {
//        $('body').on('click', menuSelector, function (e) {
//            handler();
//        });
//    }
//    function bindSemUIMenuBar() {
//        bindMenu('caps-derivations-menubar #caps-menu-new', newDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-import', importDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-rename', renameDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-copy', copyDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-download', downloadDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-delete', deleteDerivation);
//        bindMenu('#caps-derivations-menubar #caps-menu-logout', logoutDerivation);
//    }
//    bindSemUIMenuBar();
//}
//function bindTableEvents() {
//    $tableRow.on('click', function (e) {
//        if (e.ctrlKey || e.metaKey) {
//            $(this)
//                .toggleClass('active')
//            ;
//            //msg('ctrl/meta click ' + getSelected());
//        } else if (e.shiftKey) {
//            //msg('shift click ' + getSelected());
//        } else {
//            $(this)
//                .toggleClass('active')
//                .siblings('tr').removeClass('active')
//            ;
//            //msg('click ' + getSelected());
//        }
//    });
//    $tableRow.on('dblclick', function () {
//        var derivName = $(this).children('td').html();
//        openDerivation(derivName);
//    });
//}
//function getSelectedRows() {
//    var ret = '';
//    $.each($tableRow.siblings('.active'), function (index, sib) {
//        ret += " " + $(sib).attr('id');
//    });
//    return ret;
//}
//function initDerivationsSemUI() {
//    // Note: this is different from the caps-menubar
//    //Refactor: Move this to initsemui
//    var dropDownSelector = 'body #caps-derivations-menubar > .ui.menu .ui.dropdown';
//    (<any>$(dropDownSelector)).dropdown({
//        action: 'hide'
//    });
//}
//export var init = _.once(function () {
//    /**/logger.trace('In SavedDerivations');
//    console.log('In SavedDerivations');
//    initDerivationsSemUI();
//    bindTableEvents();
//    bindMenuEvents();
//});
//$(document).ready(function () {
//    init();
//});
//# sourceMappingURL=SavedDerivations.js.map
