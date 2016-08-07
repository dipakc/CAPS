/// <reference path="typings/toastr/toastr.d.ts" />
/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />

"use strict";

export var _dummyExport = undefined;

//---------------------------------
//Foldable div: Two states
//------------------------
//.xyz.unfolded
//	ABC							
//------------------------
//div.folded
//	div.foldicon
//		ooo
//	.xyz.folddata[style="display: none;"]
//		ABC
//------------------------

//toastr.info("foldable div loaded");

function bindFoldUnfoldeHandlers() {
    $("body").on('dblclick', '.unfolded', function (event) {
        var $activeElem = $(this);
        $activeElem
            .removeClass("unfolded").addClass("folddata").hide('slow')
            .wrap("<div class='folded'  style='clear: both;' />").parent()
            .prepend("<div class='foldicon' style='clear: both;'>ooo</div>");

        return false; //Stop event from bubbling
    });

    $("body").on('click', '.foldicon', function (event) {
        var $activeElem = $(this);
        $activeElem
            .siblings(".folddata")
            .unwrap()
            .addClass("unfolded").removeClass("folddata")
            .show('slow');
        $activeElem.remove();
        return false; //Stop event from bubbling
    });
}

export var init = _.once(bindFoldUnfoldeHandlers);



