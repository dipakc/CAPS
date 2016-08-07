/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/log4javascript/log4javascript.d.ts" />

"use strict";

var log4javascript = require('log4javascript');
// implicit dependencies. Return values not used.
import pju = require("PSJqueryUtils"); pju;

//"toastr" returns toastr object
var toastr = require('toastr');
var html = require('pithy');

declare var jsRoutes: any;
var appRoutes = jsRoutes.controllers.Application;
var authRoutes = jsRoutes.controllers.Authentication;

export var _dummyExport = undefined;
//---------------------------------
var logger = log4javascript.getLogger("webapp.SavedDerivationsNew");

module ServerUtils {
    function origin() {
        return window.location.protocol + "//" + window.location.host;
    }

    export function getDerivations() {
        var r = appRoutes.getDerivations();

        var derivations = undefined;

        $.ajax({
            async: false,
            url: r.url,     //'/getDerivations'
            type: r.method,   // 'Get'
            dataType: 'json',
            success: (derivs) => {
                derivations = derivs;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + r.url + " failed.");
                /**/logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });

        return derivations;
    }

    export function rename(oldName, newName) {
        var r = appRoutes.rename();

        var retVal = undefined
        $.ajax({
            async: false,
            url: r.url,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify([oldName, newName]),
            dataType: 'json',
            success: (result) => {
                retVal = result;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + r.url + " failed.");
                var errorMsg = errorThrown + ":" + jqXHR.responseText;
                /**/logger.trace(textStatus, errorMsg);
                retVal = [false, errorMsg];
            }
        });
        return retVal;
    }

    export function copy(oldName, newName) {
        var r = appRoutes.copy();
        var retVal = undefined
        $.ajax({
            async: false,
            url: r.url,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify([oldName, newName]),
            dataType: 'json',
            success: (result) => {
                retVal = result;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + r.url + " failed.");
                var errorMsg = errorThrown + ":" + jqXHR.responseText;
                /**/logger.trace(textStatus, errorMsg);
                retVal = [false, errorMsg];
            }
        });
        return retVal;
    }


    export function deleteDerivs(derivs) {
        var r = appRoutes.delete();
        var retVal = undefined
        $.ajax({
            async: false,
            url: r.url,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(derivs),
            dataType: 'json',
            success: (result) => {
                retVal = result;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + r.url + " failed.");
                var errorMsg = errorThrown + ":" + jqXHR.responseText;
                /**/logger.trace(textStatus, errorMsg);
                retVal = [false, errorMsg];
            }
        });
        return retVal;
    }

    export function newDerivation() {
        var r = appRoutes.newDerivation();
        //var newDerivationUrl = origin() + "/newDerivation";

        /**/logger.trace("Sending ajax request: newDerivationUrl = " + r.url);
        $.ajax({
            url: r.url,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'json',
            success: (derivName) => {
                var r2 = appRoutes.derivation(derivName);
                var redirectUrl = r2.url;
                /**/logger.trace("newDerivation request successful. derivName:" + derivName);
                /**/logger.trace("Redirecting to :" + redirectUrl);
                window.location.href = redirectUrl;
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + r.url + " failed.");
                /**/logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    //Refactor: Duplicate code (psMenuBar.ts)
    export function logout() {
        var r = authRoutes.logout();
        var logoutUrl = r.url;

        /**/logger.trace("Sending ajax request: logoutUrl = " + logoutUrl);
        $.ajax({
            url: logoutUrl,
            type: r.method,
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify({}),
            dataType: 'html',
            success: () => {
                logger.trace("logout successful");
                //window.location.reload(true);
                login();
            },
            error: (jqXHR, textStatus, errorThrown) => {
                /**/logger.trace("ajax request: " + logoutUrl + " failed.");
                /**/logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
            }
        });
    }

    //Refactor: Duplicate code (psMenuBar.ts)
    function login() {
        //window.open('login', '_self', false)
        window.location.href = authRoutes.login().url;
    }

    export function openDerivation(derivName) {
        var redirectUrl = appRoutes.derivation(derivName).url;
        /**/logger.trace("Opening derivation: " + derivName);
        window.location.href = redirectUrl;
    }

    function importDerivation() {
        notImplementedError();
    }

    function notImplementedError() {
        toastr.error('This functionality is not implemented.');
    }

}

class MainView extends Backbone.View {

    menuView: MenuView;
    tableView: TableView;

    constructor(options?) {
        super(options);
        this.tagName = "div";
        this.menuView = new MenuView(this);
        this.tableView = new TableView(this);
    }

    initialize() {
        this.events = {
            //"click #applyTacticBtn": "onApplyTactic"
        };
    }

    render() {
        var template =
            html.div('#caps-derivations-container', [
                //html.h1('.ui.header', "Derivations")
            ]).toString();

        var $template = $(template);
        
        $template.append(this.menuView.render().$el);
        $template.append(this.tableView.render().$el);

        this.$el.append($template);

        return this;
    }

    onRenameSuccess() {
        this.trigger("rename_successful");
    }

    onCopySuccess() {
        this.trigger("copy_successful");
    }

    onDeleteSuccess() {
        this.trigger("delete_successful");
    }

    onNoDerivationsSelected() {
        this.trigger("no_derivation_selected");
    }

    onMultipleDerivationsSelected() {
        this.trigger("multiple_derivation_selected");
    }

    onSingleDerivationsSelected() {
        this.trigger("single_derivation_selected");
    }
}


class MenuView extends Backbone.View {

    constructor(public mainView: MainView, options?) {
        super(options);
        this.tagName = "div";

        this.listenTo(mainView, 'no_derivation_selected', $.proxy(this.disableOpenMenu, this));
        this.listenTo(mainView, 'single_derivation_selected', $.proxy(this.enableOpenMenu, this));
        this.listenTo(mainView, 'multiple_derivation_selected', $.proxy(this.disableOpenMenu, this));

        this.listenTo(mainView, 'no_derivation_selected', $.proxy(this.disableRenameMenu, this));
        this.listenTo(mainView, 'single_derivation_selected', $.proxy(this.enableRenameMenu, this));
        this.listenTo(mainView, 'multiple_derivation_selected', $.proxy(this.disableRenameMenu, this));

        this.listenTo(mainView, 'no_derivation_selected', $.proxy(this.disableCopyMenu, this));
        this.listenTo(mainView, 'single_derivation_selected', $.proxy(this.enableCopyMenu, this));
        this.listenTo(mainView, 'multiple_derivation_selected', $.proxy(this.disableCopyMenu, this));

        this.listenTo(mainView, 'no_derivation_selected', $.proxy(this.disableDeleteMenu, this));
        this.listenTo(mainView, 'single_derivation_selected', $.proxy(this.enableDeleteMenu, this));       
        this.listenTo(mainView, 'multiple_derivation_selected', $.proxy(this.enableDeleteMenu, this));
    }

    initialize() {
        this.events = {
              "click #caps-menu-new":       "onNewDerivation"
            //, "click #caps-menu-import":    "importDerivation"
            , "click #caps-menu-open": "onOpenDerivation"
            ,"click #caps-menu-rename": "onRenameDerivation"
            , "click #caps-menu-copy": "onCopyDerivation"
            //, "click #caps-menu-download":  "downloadDerivation"
            , "click #caps-menu-delete":    "onDeleteDerivations"
            , "click #caps-menu-logout":    "onLogoutDerivation"
        };
    }

    render() {
        var template =
            html.div('#caps-derivations-menubar', [
                html.div('.fixed.ui.menu', [
                    html.div('.ui.container', [
                        html.div('.header.item', "Saved Derivations"),
                        html.div('.ui.dropdown.item', [
                            html.div('.text', "Derivations"),
                            html.i('.dropdown.icon'),
                            html.div('.menu', [
                                html.div('#caps-menu-new.item', "New"),
                                html.div('#caps-menu-new.item', "Import from gallery"),
                                html.div('.divider')
                            ])
                        ]),
                        html.div('.ui.dropdown.item', [
                            html.div('.text', "Actions"),
                            html.i('.dropdown.icon'),
                            html.div('.menu', [
                                html.div('#caps-menu-open.item.disabled', "Open"),
                                html.div('#caps-menu-rename.item.disabled', "Rename"),
                                html.div('#caps-menu-copy.item.disabled', "Make a copy"),
                                html.div('#caps-menu-download.item.disabled', "Download"),
                                html.div('#caps-menu-delete.item.disabled', "Delete")
                            ])
                        ]),
                        html.div('.right.ui.dropdown.item', [
                            html.div('.text', "User"),
                            html.i('.dropdown.icon'),
                            html.div('.menu', [
                                html.div('#caps-menu-logout.item', "Logout")
                            ])
                        ])
                    ])
                ]),
            ]).toString();

        this.$el.append($(template));

        (<any>this.$el.find(".ui.menu .ui.dropdown")).dropdown({
            action: 'hide'
        });

        //this.$el.append($("<div>Menubar tmp Render</div>"));
        return this;
    }

    onNewDerivation() {
        ServerUtils.newDerivation();
    }

    onRenameDerivation() {
        var deriv = this.mainView.tableView.getSelectedDeriv();
        $('body').append(new RenameView(deriv, this).render().$el)
    }

    onOpenDerivation() {
        var deriv = this.mainView.tableView.getSelectedDeriv();        
        ServerUtils.openDerivation(deriv);
    }

    onCopyDerivation() {
        var deriv = this.mainView.tableView.getSelectedDeriv();
        $('body').append(new CopyView(deriv, this).render().$el)
    }

    onDeleteDerivations() {
        var derivs = this.mainView.tableView.getSelDerivations();
        $('body').append(new DeleteView(derivs, this).render().$el)
    }

    onLogoutDerivation() {
        ServerUtils.logout();
    }
    enableOpenMenu() {
        //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
        $('#caps-menu-open.item').removeClass('disabled');
    }

    disableOpenMenu() {
        this.$el.find('#caps-menu-open.item').addClass('disabled');
    }

    enableRenameMenu() {
        //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
        $('#caps-menu-rename.item').removeClass('disabled');
    }

    disableRenameMenu() {
        this.$el.find('#caps-menu-rename.item').addClass('disabled');
    }

    enableCopyMenu() {
        $('#caps-menu-copy.item').removeClass('disabled');
    }

    disableCopyMenu() {
        this.$el.find('#caps-menu-copy.item').addClass('disabled');
    }

    enableDeleteMenu() {
        //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
        $('#caps-menu-delete.item').removeClass('disabled');
        var x = 5;//TODO
    }

    disableDeleteMenu() {
        this.$el.find('#caps-menu-delete.item').addClass('disabled');
    }

}

class RenameView extends Backbone.View {
    msgView: MessageView;

    constructor(public deriv: string, public menuView: MenuView, options?) {
        super(options);
        this.tagName = "div";
        this.msgView = new MessageView();
    }

    initialize() {
        this.events = {
            //"input input": "onInputChange" // not working
        };
    }

    getMainView() {
        return this.menuView.mainView;
    }

    render() {
        var thisView = this;

        var template =
            html.div("#renameModal.small.ui.modal", [
                html.div(".header", "Rename derivation: Enter new name"),
                html.div(".content", [
                    html.div(".ui.input", [
                        html.input({ type: "text", placeholder: "New derivation name" }, ""),
                        html.span({}, ".capstxt")])]),
                html.div(".actions", [
                    html.div(".ui.approve.button.disabled", "Rename"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString()

        var $template = $(template);        
        
        //Render message view
        $template.append(this.msgView.render().$el);

        this.$el.append($template);

        function isValidName(name) {
            var reg = /^[0-9a-zA-Z_-]+$/;
            return reg.test(name);
        }

        //Bind "on input change".        
        this.$el.find('input').on("input", null, null, function (e) {
            thisView.msgView.hideView();
            var value = $(e.currentTarget).val();
            //var $thisEL = thisView.$el //Not working
            var $thisEL = $(e.currentTarget).parents('#renameModal');
            var $approveBtn = $thisEL.find('.ui.approve.button');
            if (isValidName(value)) {
                $approveBtn.removeClass("disabled");
            } else {
                $approveBtn.addClass("disabled");
            }
        });

        //bind rename and cancel handlers.
        var $renameDiv = this.$el.children("#renameModal");
        (<any>$renameDiv)
            .modal({
                onDeny: function () {
                    toastr.info("Rename operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var newName = $renameDiv.find(".ui.input input").val();
                    var retVal = ServerUtils.rename(thisView.deriv, newName + '.capstxt');
                    var status = retVal[0];
                    var msg = <string>retVal[1];

                    if (status) {
                        toastr.info("Rename successful");
                        thisView.getMainView().onRenameSuccess();
                        return true;
                    } else {
                        thisView.msgView.showMsg(msg);
                        return false;
                    }
                }
            })
            .modal('show');

        return this;
    }
}


class CopyView extends Backbone.View {
    msgView: MessageView;

    constructor(public deriv: string, public menuView: MenuView, options?) {
        super(options);
        this.tagName = "div";
        this.msgView = new MessageView();
    }

    initialize() {
        this.events = {
            //"input input": "onInputChange" // not working
        };
    }

    getMainView() {
        return this.menuView.mainView;
    }

    render() {
        var thisView = this;

        var template =
            html.div("#copyModal.small.ui.modal", [
                html.div(".header", "Copy derivation: Enter new name"),
                html.div(".content", [
                    html.div(".ui.input", [
                        html.input({ type: "text", placeholder: "New derivation name" }, ""),
                        html.span({}, ".capstxt")])]),
                html.div(".actions", [
                    html.div(".ui.approve.button.disabled", "Copy"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString()

        var $template = $(template);

        //Render message view
        $template.append(this.msgView.render().$el);

        this.$el.append($template);

        function isValidName(name) {
            var reg = /^[0-9a-zA-Z_-]+$/;
            return reg.test(name);
        }

        //Bind "on input change".        
        this.$el.find('input').on("input", null, null, function (e) {
            thisView.msgView.hideView();
            var value = $(e.currentTarget).val();
            //var $thisEL = thisView.$el //Not working
            var $thisEL = $(e.currentTarget).parents('#copyModal');
            var $approveBtn = $thisEL.find('.ui.approve.button');
            if (isValidName(value)) {
                $approveBtn.removeClass("disabled");
            } else {
                $approveBtn.addClass("disabled");
            }
        });

        //bind copy and cancel handlers.
        var $copyDiv = this.$el.children("#copyModal");
        (<any>$copyDiv)
            .modal({
                onDeny: function () {
                    toastr.info("Copy operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var newName = $copyDiv.find(".ui.input input").val();
                    var retVal = ServerUtils.copy(thisView.deriv, newName + '.capstxt');
                    var status = retVal[0];
                    var msg = <string>retVal[1];

                    if (status) {
                        toastr.info("Copy successful");
                        thisView.getMainView().onCopySuccess();
                        return true;
                    } else {
                        thisView.msgView.showMsg(msg);
                        return false;
                    }
                }
            })
            .modal('show');

        return this;
    }
}

class DeleteView extends Backbone.View {
    msgView: MessageView;

    constructor(public derivs: string[], public menuView: MenuView, options?) {
        super(options);
        this.tagName = "div";
        this.msgView = new MessageView();
    }

    initialize() {
        this.events = {
            //"input input": "onInputChange" // not working
        };
    }

    getMainView() {
        return this.menuView.mainView;
    }

    render() {
        var thisView = this;
        var selectedDerivs = this.menuView.mainView.tableView.getSelDerivations();
        var selDerivationsUL = 
            html.ul({}, [
                _.map(selectedDerivs, function (x) { return html.li({}, x) })      
            ])

        var template =
            html.div("#deleteModal.small.ui.modal", [
                html.div(".header", "Do you want to delete the following derivations?"),
                html.div(".content", [
                    selDerivationsUL]),
                html.div(".actions", [
                    html.div(".ui.approve.button", "Delete"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString()

        var $template = $(template);

        //Render message view
        $template.append(this.msgView.render().$el);

        this.$el.append($template);

        //bind delete and cancel handlers.
        var $deleteDiv = this.$el.children("#deleteModal");
        (<any>$deleteDiv)
            .modal({
                onDeny: function () {
                    toastr.info("Delete operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var retVal = ServerUtils.deleteDerivs(thisView.derivs);
                    var msg =
                        html.ul({},
                            _.map(_.zip(thisView.derivs, retVal), function (x) {
                                var name = x[0]
                                var status = x[1][0];
                                var msg = x[1][1];

                                var statusMsg = (function () {
                                    if (status) {
                                        return "OK";
                                    } else {
                                        return "Failed: " + msg;
                                    }
                                })();
                                
                                var fullMsg = name + ": " + statusMsg;

                                return html.li({}, fullMsg);
                            })).toString();

                    toastr.info(msg);//TODO: should be a modal popup
                    thisView.getMainView().onDeleteSuccess();
                    return true;
                }
            })
            .modal('show');

        return this;
    }
}

class MessageView extends Backbone.View {

    constructor(options?) {
        super(options);
        this.tagName = "div";
    }

    clear() {
        this.$el.html("");
    }

    render() {
        var template =
            html.div(".ui.negative.message.hidden", [
                //i(cls := "close icon"),
                html.div(".area", "")]).toString();
        var $template = $(template);
        this.clear();
        this.$el.append($template);

        return this;
    }

    public showMsg(msg) {
        this.$el.find('.area').html(msg);
        this.showView();
    }

    showView() {
        this.$el.find('.message').removeClass('hidden');
    }

    public hideView() {
        this.$el.find('.message').addClass('hidden');
    }
}

class TableView extends Backbone.View {

    constructor(public mainView: MainView, options?) {
        super(options);
        this.tagName = "div";
        this.listenTo(mainView, 'rename_successful', $.proxy(this.render, this));
        this.listenTo(mainView, 'delete_successful', $.proxy(this.render, this));
        this.listenTo(mainView, 'copy_successful', $.proxy(this.render, this));
    }

    initialize() {
        this.events = {
            "click .ui.table tbody tr": "onRowClick"
            //"dblclick .ui.table tbody tr": "onRowDblClick"
        };
    }

    onRowClick(e) {
        var $row = $(e.currentTarget);

        if (e.ctrlKey || e.metaKey) {
            $row
                .toggleClass('active')
            ;
        } else if (e.shiftKey) {
            //TODO
        } else {
            $row
                .toggleClass('active')
                .siblings('tr').removeClass('active')
            ;
        }

        var selsLen = this.getSelDerivations().length;
        if (selsLen == 1) {
            this.mainView.onSingleDerivationsSelected();
        } else if (selsLen > 1) {
            this.mainView.onMultipleDerivationsSelected();
        } else {
            this.mainView.onNoDerivationsSelected();
        }
    }

    //onRowDblClick(e) {
    //    var $row = $(e.currentTarget);
    //    var derivName = $row.children('td').html();
    //    alert('Open Derivation not implemented. xxx');
    //    //openDerivation(derivName);//TODO
    //}

    render() {
        var template =
            html.div('#caps-derivations-table', [
                html.h1('.ui.header', "Derivations"),
                html.table('.ui.celled.table', [
                    html.thead(null, [
                        html.tr(null, [html.th(null, "Name")])
                    ]),
                    html.tbody(null,
                        _.map(ServerUtils.getDerivations(), function (d) { 
                            return html.tr(null, [html.td(null, d)])
                        })
                    )
                ]),
            ]).toString();

        this.$el.empty();
        this.$el.append($(template));
        return this;
    }

    public getSelectedDeriv(): string {
        return this.getSelDerivations()[0];
    }

    public getSelDerivations(): string[] {
        var $tbody = (<any>this.$el.find('table.ui.table > tbody')).single();
        var $activeDerivs = $tbody.find("> tr.active > td");

        var selNames = $.map($activeDerivs, function (row) {
            //return $(row).attr('id');
            return $(row).html();
        });
        return selNames;
    }

}


$(document).ready(function () {
    var mainView = new MainView();
    mainView.render();

    $('body').append(mainView.$el);
});