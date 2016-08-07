/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jqueryui/jqueryui.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />

//---------------------------------

"use strict";
export var _dummyExport = undefined;

export function initMenuBar() {    
    (<any>$('body #caps-menubar .ui.menu .ui.dropdown')).nonempty().dropdown({
        action: 'hide'
    });
}

export function initSemUI() {
    initMenuBar();
}
