/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jqueryui/jqueryui.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
//---------------------------------
"use strict";
define(["require", "exports"], function(require, exports) {
    exports._dummyExport = undefined;

    function initMenuBar() {
        $('body #caps-menubar .ui.menu .ui.dropdown').nonempty().dropdown({
            action: 'hide'
        });
    }
    exports.initMenuBar = initMenuBar;

    function initSemUI() {
        exports.initMenuBar();
    }
    exports.initSemUI = initSemUI;
});
//# sourceMappingURL=InitSemUI.js.map
