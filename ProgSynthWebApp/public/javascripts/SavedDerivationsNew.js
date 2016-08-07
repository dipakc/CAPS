/// <reference path="typings/backbone/backbone.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
/// <reference path="typings/toastr/toastr.d.ts" />
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

    //"toastr" returns toastr object
    var toastr = require('toastr');
    var html = require('pithy');

    var appRoutes = jsRoutes.controllers.Application;
    var authRoutes = jsRoutes.controllers.Authentication;

    exports._dummyExport = undefined;

    //---------------------------------
    var logger = log4javascript.getLogger("webapp.SavedDerivationsNew");

    var ServerUtils;
    (function (ServerUtils) {
        function origin() {
            return window.location.protocol + "//" + window.location.host;
        }

        function getDerivations() {
            var r = appRoutes.getDerivations();

            var derivations = undefined;

            $.ajax({
                async: false,
                url: r.url,
                type: r.method,
                dataType: 'json',
                success: function (derivs) {
                    derivations = derivs;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ logger.trace("ajax request: " + r.url + " failed.");
                    /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
                }
            });

            return derivations;
        }
        ServerUtils.getDerivations = getDerivations;

        function rename(oldName, newName) {
            var r = appRoutes.rename();

            var retVal = undefined;
            $.ajax({
                async: false,
                url: r.url,
                type: r.method,
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify([oldName, newName]),
                dataType: 'json',
                success: function (result) {
                    retVal = result;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ logger.trace("ajax request: " + r.url + " failed.");
                    var errorMsg = errorThrown + ":" + jqXHR.responseText;
                    /**/ logger.trace(textStatus, errorMsg);
                    retVal = [false, errorMsg];
                }
            });
            return retVal;
        }
        ServerUtils.rename = rename;

        function copy(oldName, newName) {
            var r = appRoutes.copy();
            var retVal = undefined;
            $.ajax({
                async: false,
                url: r.url,
                type: r.method,
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify([oldName, newName]),
                dataType: 'json',
                success: function (result) {
                    retVal = result;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ logger.trace("ajax request: " + r.url + " failed.");
                    var errorMsg = errorThrown + ":" + jqXHR.responseText;
                    /**/ logger.trace(textStatus, errorMsg);
                    retVal = [false, errorMsg];
                }
            });
            return retVal;
        }
        ServerUtils.copy = copy;

        function deleteDerivs(derivs) {
            var r = appRoutes.delete();
            var retVal = undefined;
            $.ajax({
                async: false,
                url: r.url,
                type: r.method,
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(derivs),
                dataType: 'json',
                success: function (result) {
                    retVal = result;
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    /**/ logger.trace("ajax request: " + r.url + " failed.");
                    var errorMsg = errorThrown + ":" + jqXHR.responseText;
                    /**/ logger.trace(textStatus, errorMsg);
                    retVal = [false, errorMsg];
                }
            });
            return retVal;
        }
        ServerUtils.deleteDerivs = deleteDerivs;

        function newDerivation() {
            var r = appRoutes.newDerivation();

            //var newDerivationUrl = origin() + "/newDerivation";
            /**/ logger.trace("Sending ajax request: newDerivationUrl = " + r.url);
            $.ajax({
                url: r.url,
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
                    /**/ logger.trace("ajax request: " + r.url + " failed.");
                    /**/ logger.trace(textStatus, errorThrown + ":" + jqXHR.responseText);
                }
            });
        }
        ServerUtils.newDerivation = newDerivation;

        //Refactor: Duplicate code (psMenuBar.ts)
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
        ServerUtils.logout = logout;

        //Refactor: Duplicate code (psMenuBar.ts)
        function login() {
            //window.open('login', '_self', false)
            window.location.href = authRoutes.login().url;
        }

        function openDerivation(derivName) {
            var redirectUrl = appRoutes.derivation(derivName).url;
            /**/ logger.trace("Opening derivation: " + derivName);
            window.location.href = redirectUrl;
        }
        ServerUtils.openDerivation = openDerivation;

        function importDerivation() {
            notImplementedError();
        }

        function notImplementedError() {
            toastr.error('This functionality is not implemented.');
        }
    })(ServerUtils || (ServerUtils = {}));

    var MainView = (function (_super) {
        __extends(MainView, _super);
        function MainView(options) {
            _super.call(this, options);
            this.tagName = "div";
            this.menuView = new MenuView(this);
            this.tableView = new TableView(this);
        }
        MainView.prototype.initialize = function () {
            this.events = {};
        };

        MainView.prototype.render = function () {
            var template = html.div('#caps-derivations-container', []).toString();

            var $template = $(template);

            $template.append(this.menuView.render().$el);
            $template.append(this.tableView.render().$el);

            this.$el.append($template);

            return this;
        };

        MainView.prototype.onRenameSuccess = function () {
            this.trigger("rename_successful");
        };

        MainView.prototype.onCopySuccess = function () {
            this.trigger("copy_successful");
        };

        MainView.prototype.onDeleteSuccess = function () {
            this.trigger("delete_successful");
        };

        MainView.prototype.onNoDerivationsSelected = function () {
            this.trigger("no_derivation_selected");
        };

        MainView.prototype.onMultipleDerivationsSelected = function () {
            this.trigger("multiple_derivation_selected");
        };

        MainView.prototype.onSingleDerivationsSelected = function () {
            this.trigger("single_derivation_selected");
        };
        return MainView;
    })(Backbone.View);

    var MenuView = (function (_super) {
        __extends(MenuView, _super);
        function MenuView(mainView, options) {
            _super.call(this, options);
            this.mainView = mainView;
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
        MenuView.prototype.initialize = function () {
            this.events = {
                "click #caps-menu-new": "onNewDerivation",
                "click #caps-menu-open": "onOpenDerivation",
                "click #caps-menu-rename": "onRenameDerivation",
                "click #caps-menu-copy": "onCopyDerivation",
                "click #caps-menu-delete": "onDeleteDerivations",
                "click #caps-menu-logout": "onLogoutDerivation"
            };
        };

        MenuView.prototype.render = function () {
            var template = html.div('#caps-derivations-menubar', [
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
                ])
            ]).toString();

            this.$el.append($(template));

            this.$el.find(".ui.menu .ui.dropdown").dropdown({
                action: 'hide'
            });

            //this.$el.append($("<div>Menubar tmp Render</div>"));
            return this;
        };

        MenuView.prototype.onNewDerivation = function () {
            ServerUtils.newDerivation();
        };

        MenuView.prototype.onRenameDerivation = function () {
            var deriv = this.mainView.tableView.getSelectedDeriv();
            $('body').append(new RenameView(deriv, this).render().$el);
        };

        MenuView.prototype.onOpenDerivation = function () {
            var deriv = this.mainView.tableView.getSelectedDeriv();
            ServerUtils.openDerivation(deriv);
        };

        MenuView.prototype.onCopyDerivation = function () {
            var deriv = this.mainView.tableView.getSelectedDeriv();
            $('body').append(new CopyView(deriv, this).render().$el);
        };

        MenuView.prototype.onDeleteDerivations = function () {
            var derivs = this.mainView.tableView.getSelDerivations();
            $('body').append(new DeleteView(derivs, this).render().$el);
        };

        MenuView.prototype.onLogoutDerivation = function () {
            ServerUtils.logout();
        };
        MenuView.prototype.enableOpenMenu = function () {
            //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
            $('#caps-menu-open.item').removeClass('disabled');
        };

        MenuView.prototype.disableOpenMenu = function () {
            this.$el.find('#caps-menu-open.item').addClass('disabled');
        };

        MenuView.prototype.enableRenameMenu = function () {
            //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
            $('#caps-menu-rename.item').removeClass('disabled');
        };

        MenuView.prototype.disableRenameMenu = function () {
            this.$el.find('#caps-menu-rename.item').addClass('disabled');
        };

        MenuView.prototype.enableCopyMenu = function () {
            $('#caps-menu-copy.item').removeClass('disabled');
        };

        MenuView.prototype.disableCopyMenu = function () {
            this.$el.find('#caps-menu-copy.item').addClass('disabled');
        };

        MenuView.prototype.enableDeleteMenu = function () {
            //this.$el.find('#caps-menu-rename.item').removeClass('disabled');
            $('#caps-menu-delete.item').removeClass('disabled');
            var x = 5;
        };

        MenuView.prototype.disableDeleteMenu = function () {
            this.$el.find('#caps-menu-delete.item').addClass('disabled');
        };
        return MenuView;
    })(Backbone.View);

    var RenameView = (function (_super) {
        __extends(RenameView, _super);
        function RenameView(deriv, menuView, options) {
            _super.call(this, options);
            this.deriv = deriv;
            this.menuView = menuView;
            this.tagName = "div";
            this.msgView = new MessageView();
        }
        RenameView.prototype.initialize = function () {
            this.events = {};
        };

        RenameView.prototype.getMainView = function () {
            return this.menuView.mainView;
        };

        RenameView.prototype.render = function () {
            var thisView = this;

            var template = html.div("#renameModal.small.ui.modal", [
                html.div(".header", "Rename derivation: Enter new name"),
                html.div(".content", [html.div(".ui.input", [
                        html.input({ type: "text", placeholder: "New derivation name" }, ""),
                        html.span({}, ".capstxt")])]),
                html.div(".actions", [
                    html.div(".ui.approve.button.disabled", "Rename"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString();

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
            $renameDiv.modal({
                onDeny: function () {
                    toastr.info("Rename operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var newName = $renameDiv.find(".ui.input input").val();
                    var retVal = ServerUtils.rename(thisView.deriv, newName + '.capstxt');
                    var status = retVal[0];
                    var msg = retVal[1];

                    if (status) {
                        toastr.info("Rename successful");
                        thisView.getMainView().onRenameSuccess();
                        return true;
                    } else {
                        thisView.msgView.showMsg(msg);
                        return false;
                    }
                }
            }).modal('show');

            return this;
        };
        return RenameView;
    })(Backbone.View);

    var CopyView = (function (_super) {
        __extends(CopyView, _super);
        function CopyView(deriv, menuView, options) {
            _super.call(this, options);
            this.deriv = deriv;
            this.menuView = menuView;
            this.tagName = "div";
            this.msgView = new MessageView();
        }
        CopyView.prototype.initialize = function () {
            this.events = {};
        };

        CopyView.prototype.getMainView = function () {
            return this.menuView.mainView;
        };

        CopyView.prototype.render = function () {
            var thisView = this;

            var template = html.div("#copyModal.small.ui.modal", [
                html.div(".header", "Copy derivation: Enter new name"),
                html.div(".content", [html.div(".ui.input", [
                        html.input({ type: "text", placeholder: "New derivation name" }, ""),
                        html.span({}, ".capstxt")])]),
                html.div(".actions", [
                    html.div(".ui.approve.button.disabled", "Copy"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString();

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
            $copyDiv.modal({
                onDeny: function () {
                    toastr.info("Copy operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var newName = $copyDiv.find(".ui.input input").val();
                    var retVal = ServerUtils.copy(thisView.deriv, newName + '.capstxt');
                    var status = retVal[0];
                    var msg = retVal[1];

                    if (status) {
                        toastr.info("Copy successful");
                        thisView.getMainView().onCopySuccess();
                        return true;
                    } else {
                        thisView.msgView.showMsg(msg);
                        return false;
                    }
                }
            }).modal('show');

            return this;
        };
        return CopyView;
    })(Backbone.View);

    var DeleteView = (function (_super) {
        __extends(DeleteView, _super);
        function DeleteView(derivs, menuView, options) {
            _super.call(this, options);
            this.derivs = derivs;
            this.menuView = menuView;
            this.tagName = "div";
            this.msgView = new MessageView();
        }
        DeleteView.prototype.initialize = function () {
            this.events = {};
        };

        DeleteView.prototype.getMainView = function () {
            return this.menuView.mainView;
        };

        DeleteView.prototype.render = function () {
            var thisView = this;
            var selectedDerivs = this.menuView.mainView.tableView.getSelDerivations();
            var selDerivationsUL = html.ul({}, [
                _.map(selectedDerivs, function (x) {
                    return html.li({}, x);
                })
            ]);

            var template = html.div("#deleteModal.small.ui.modal", [
                html.div(".header", "Do you want to delete the following derivations?"),
                html.div(".content", [selDerivationsUL]),
                html.div(".actions", [
                    html.div(".ui.approve.button", "Delete"),
                    html.div(".ui.cancel.button", "Cancel")])]).toString();

            var $template = $(template);

            //Render message view
            $template.append(this.msgView.render().$el);

            this.$el.append($template);

            //bind delete and cancel handlers.
            var $deleteDiv = this.$el.children("#deleteModal");
            $deleteDiv.modal({
                onDeny: function () {
                    toastr.info("Delete operation cancelled");
                    return true;
                },
                onApprove: function () {
                    var retVal = ServerUtils.deleteDerivs(thisView.derivs);
                    var msg = html.ul({}, _.map(_.zip(thisView.derivs, retVal), function (x) {
                        var name = x[0];
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

                    toastr.info(msg); //TODO: should be a modal popup
                    thisView.getMainView().onDeleteSuccess();
                    return true;
                }
            }).modal('show');

            return this;
        };
        return DeleteView;
    })(Backbone.View);

    var MessageView = (function (_super) {
        __extends(MessageView, _super);
        function MessageView(options) {
            _super.call(this, options);
            this.tagName = "div";
        }
        MessageView.prototype.clear = function () {
            this.$el.html("");
        };

        MessageView.prototype.render = function () {
            var template = html.div(".ui.negative.message.hidden", [html.div(".area", "")]).toString();
            var $template = $(template);
            this.clear();
            this.$el.append($template);

            return this;
        };

        MessageView.prototype.showMsg = function (msg) {
            this.$el.find('.area').html(msg);
            this.showView();
        };

        MessageView.prototype.showView = function () {
            this.$el.find('.message').removeClass('hidden');
        };

        MessageView.prototype.hideView = function () {
            this.$el.find('.message').addClass('hidden');
        };
        return MessageView;
    })(Backbone.View);

    var TableView = (function (_super) {
        __extends(TableView, _super);
        function TableView(mainView, options) {
            _super.call(this, options);
            this.mainView = mainView;
            this.tagName = "div";
            this.listenTo(mainView, 'rename_successful', $.proxy(this.render, this));
            this.listenTo(mainView, 'delete_successful', $.proxy(this.render, this));
            this.listenTo(mainView, 'copy_successful', $.proxy(this.render, this));
        }
        TableView.prototype.initialize = function () {
            this.events = {
                "click .ui.table tbody tr": "onRowClick"
            };
        };

        TableView.prototype.onRowClick = function (e) {
            var $row = $(e.currentTarget);

            if (e.ctrlKey || e.metaKey) {
                $row.toggleClass('active');
            } else if (e.shiftKey) {
                //TODO
            } else {
                $row.toggleClass('active').siblings('tr').removeClass('active');
            }

            var selsLen = this.getSelDerivations().length;
            if (selsLen == 1) {
                this.mainView.onSingleDerivationsSelected();
            } else if (selsLen > 1) {
                this.mainView.onMultipleDerivationsSelected();
            } else {
                this.mainView.onNoDerivationsSelected();
            }
        };

        //onRowDblClick(e) {
        //    var $row = $(e.currentTarget);
        //    var derivName = $row.children('td').html();
        //    alert('Open Derivation not implemented. xxx');
        //    //openDerivation(derivName);//TODO
        //}
        TableView.prototype.render = function () {
            var template = html.div('#caps-derivations-table', [
                html.h1('.ui.header', "Derivations"),
                html.table('.ui.celled.table', [
                    html.thead(null, [
                        html.tr(null, [html.th(null, "Name")])
                    ]),
                    html.tbody(null, _.map(ServerUtils.getDerivations(), function (d) {
                        return html.tr(null, [html.td(null, d)]);
                    }))
                ])
            ]).toString();

            this.$el.empty();
            this.$el.append($(template));
            return this;
        };

        TableView.prototype.getSelectedDeriv = function () {
            return this.getSelDerivations()[0];
        };

        TableView.prototype.getSelDerivations = function () {
            var $tbody = this.$el.find('table.ui.table > tbody').single();
            var $activeDerivs = $tbody.find("> tr.active > td");

            var selNames = $.map($activeDerivs, function (row) {
                //return $(row).attr('id');
                return $(row).html();
            });
            return selNames;
        };
        return TableView;
    })(Backbone.View);

    $(document).ready(function () {
        var mainView = new MainView();
        mainView.render();

        $('body').append(mainView.$el);
    });
});
//# sourceMappingURL=SavedDerivationsNew.js.map
