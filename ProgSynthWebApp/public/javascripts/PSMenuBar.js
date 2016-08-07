/// <reference path="typings/requirejs/require.d.ts" />
"use strict";
define(["require", "exports", "psweb.jquery.layout", "SynthtreeModel", "InitSemUI", "PSJqueryUtils"], function(require, exports, pjl, stm, semUI, pju) {
    pjl;
    stm;
    semUI;
    var log4javascript = require('log4javascript');
    var logger = log4javascript.getLogger("webapp.psMenuBar");
    pju;

    var appRoutes = jsRoutes.controllers.Application;
    var authRoutes = jsRoutes.controllers.Authentication;

    exports._dummyExport = undefined;

    //Execute the derivation script and load the page
    function loadGalleryDeriv(derivName) {
        var r = appRoutes.loadGalleryDeriv(derivName);
        var loadGalleryDerivUrl = r.url;

        /**/ logger.trace("Sending ajax request. loadGalleryDerivUrl= " + loadGalleryDerivUrl);
        $.ajax({
            url: loadGalleryDerivUrl,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'json',
            success: function (newName) {
                /**/ logger.trace("ajax request: " + loadGalleryDerivUrl + " sucessful. newName: " + newName);
                var redirectUrl = appRoutes.derivation(newName).url;
                /**/ logger.trace("Redirecting to " + redirectUrl);
                window.location.href = redirectUrl;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                /**/ logger.trace("ajax request: " + loadGalleryDerivUrl + " failed.");
                /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    function populateSemUIGalleryPanel() {
        /**/ logger.trace("in populateSemUIGalleryPanel");

        var r = appRoutes.galleryDerivations();
        var galleryDerivationsUrl = r.url;

        /**/ logger.trace("Sending ajax request: galleryDerivationsUrl = " + galleryDerivationsUrl);

        $.ajax({
            url: galleryDerivationsUrl,
            type: r.method,
            dataType: 'json',
            success: function (derivationList) {
                processDerivs(derivationList);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                /**/ logger.trace("ajax request: " + galleryDerivationsUrl + " failed.");
                /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });

        function processDerivs(derivs) {
            /**/ logger.trace("in processDerivs");

            //populate galleryPanel
            var i = 0;
            var containerDiv = $('<div class=derivContainer></div>');
            for (i = 0; i < derivs.length; i++) {
                var derivDiv = $('<div id=' + derivs[i] + '>' + derivs[i] + '</div>');
                derivDiv.addClass("derivDiv");
                derivDiv.addClass("FakeLink ");
                containerDiv.append(derivDiv);
            }

            $(('#semuiGalleryPanel')).single();
            $('#semuiGalleryPanel').append(containerDiv);

            //bind click events on derivation divs.
            //Refactor: Take the ui.panel jquery object as an argument to the function.
            var derivationDivs = $('.derivDiv');
            derivationDivs.on('click', function (e) {
                $('#semuiGalleryPanel.ui.modal').single().modal('hide');
                var derivationName = e.target.id;
                loadGalleryDeriv(derivationName);
            });
        }
    }

    function resetTree() {
        var derivName = pju.extractLastUrlSegment();
        var r = appRoutes.resetTree(derivName);
        var resetTreeUrl = r.url;
        /**/ logger.trace("Sending ajax request: resetTreeUrl = " + resetTreeUrl);
        $.ajax({
            url: resetTreeUrl,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'html',
            success: function () {
                /**/ logger.trace("");
                window.location.reload(true);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                /**/ logger.trace("ajax request: " + resetTreeUrl + " failed.");
                /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    /*
    function viewDebugLog() {
    var url = "/logs/debugLog";
    window.open(url, '_blank');
    }
    
    function clearDebugLog() {
    $.post("/logs/clearDebugLog");
    }
    */
    function isDerivNameValid(derivName) {
        return derivName.length <= 254 && derivName.match(/^[A-Za-z0-9_-]+$/) !== null;
    }

    function launchSidebar() {
        $('#caps-sidebar.ui.sidebar').sidebar('toggle');
    }

    function newDerivation() {
        var r = appRoutes.newDerivation();
        var newDerivationUrl = r.url;

        /**/ logger.trace("Sending ajax request: newDerivationUrl = " + newDerivationUrl);
        $.ajax({
            url: newDerivationUrl,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'json',
            success: function (derivName) {
                var r2 = appRoutes.derivation(derivName);
                var redirectUrl = r2.url;
                /**/ logger.trace("newDerivation request successful. derivName:" + derivName);
                /**/ logger.trace("Redirecting to :" + redirectUrl);
                window.location.href = redirectUrl;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                /**/ logger.trace("ajax request: " + newDerivationUrl + " failed.");
                /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    function logout() {
        var r = authRoutes.logout();
        var logoutUrl = r.url;

        /**/ logger.trace("Sending ajax request: logoutUrl = " + logoutUrl);
        $.ajax({
            url: logoutUrl,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'html',
            success: function () {
                logger.trace("logout successful");

                //window.location.reload(true);
                login();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                /**/ logger.trace("ajax request: " + logoutUrl + " failed.");
                /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    function login() {
        //window.open('login', '_self', false)
        var r = authRoutes.login();
        window.location.href = r.url;
    }

    function toggleLog(errorContainerLayout) {
        logger.trace("state: " + errorContainerLayout.state.south.isHidden);
        if (errorContainerLayout.state.south.isHidden) {
            logger.trace("is hidden...showing now...");
            errorContainerLayout.show("south");
            errorContainerLayout.open("south");
            //TODO: update log
        } else {
            logger.trace("hiding");
            errorContainerLayout.hide("south");
        }
    }

    function minimalAnno() {
        logger.trace("minimalAnno called");
        var vs = stm.gPSState.getViewerState();
        vs.setMinimalAnno(!vs.getMinimalAnno());
    }

    function notImplementedError() {
        alert('This functionality is not implemented.');
    }

    function setAsCurNode() {
        var selNodeId = stm.gPSState.getSelNodeId();
        stm.gPSState.setCurNodeId(selNodeId);
    }

    function delSelNode() {
        stm.gPSState.delSelNodeId();
    }

    function tacticDoc() {
        var url = pju.getRoolUrl() + "/assets/docs/Tactics.html";
        window.open(url, '_blank');
    }

    function userDoc() {
        var url = pju.getRoolUrl() + "/assets/docs/CAPSUserDoc.html";
        window.open(url, '_blank');
    }

    function loadLocalFile() {
        var r = appRoutes.uploadForm();
        window.location.href = r.url;
    }

    function downloadDerivationBin() {
        var pathName = window.location.pathname;
        if (pathName.match(/^\/derivation\//) !== null) {
            var derivName = pju.extractLastUrlSegment();
            window.location.href = appRoutes.downloadDerivationBin(derivName).url;
        } else {
            alert("unable to download");
        }
    }

    function downloadDerivationTxt() {
        var pathName = window.location.pathname;
        if (pathName.match(/^\/derivation\//) !== null) {
            var derivName = pju.extractLastUrlSegment();
            window.location.href = appRoutes.downloadDerivationTxt(derivName).url;
        } else {
            alert("unable to download");
        }
    }

    function showGalleryPanel() {
        logger.trace("inside showGalleryPanel");

        populateSemUIGalleryPanel();

        //Init Gallery Panel
        $('#semuiGalleryPanel.ui.modal').single().modal({
            closable: true
        }).modal('show');
    }

    function bindSemUI() {
        function bindMenu(menuSelector, handler) {
            //$('body').on('click', menuSelector, function (e) {
            //    handler();
            //});
            $(menuSelector).single().click(function (e) {
                handler();
            });
        }

        semUI.initMenuBar();

        logger.trace("calling dolayout");
        var errorContainerLayout = pjl.dolayout();

        function bindSemUIMenuBar() {
            bindMenu('#caps-menubar #caps-sidebar-menu', launchSidebar);
            bindMenu('#caps-menubar #caps-menu-new', newDerivation);

            //bindMenu('#caps-menubar #caps-menu-gallery', notImplementedError);
            bindMenu('#caps-menubar #caps-menu-gallery', showGalleryPanel);
            bindMenu('#caps-menubar #caps-menu-load-local', loadLocalFile);
            bindMenu('#caps-menubar #caps-menu-download-text', downloadDerivationTxt);
            bindMenu('#caps-menubar #caps-menu-download-bin', downloadDerivationBin);
            bindMenu('#caps-menubar #caps-menu-reset', resetTree);

            //bindMenu('#caps-menubar #caps-debug-log', viewDebugLog);
            //bindMenu('#caps-menubar #caps-clear-debug-log', clearDebugLog);
            //bindMenu('#caps-menubar #caps-menu-toggle-log', toggleLog);
            bindMenu('#caps-menubar #caps-menu-minimal-anno', minimalAnno);
            bindMenu('#caps-menubar #caps-menu-set-head-node', setAsCurNode);
            bindMenu('#caps-menubar #caps-menu-delete-sel-node', delSelNode);
            bindMenu('#caps-menubar #caps-menu-preferences', notImplementedError);
            bindMenu('#caps-menubar #caps-tactic-doc', tacticDoc);
            bindMenu('#caps-menubar #caps-user-doc', userDoc);
            bindMenu('#caps-menubar #caps-menu-logout', logout);
        }
        bindSemUIMenuBar();
    }

    exports.init = _.once(function () {
        bindSemUI();
    });
});
//# sourceMappingURL=PSMenuBar.js.map
