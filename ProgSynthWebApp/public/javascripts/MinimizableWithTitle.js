/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
"use strict";
define(["require", "exports"], function(require, exports) {
    exports._dummyExport = undefined;

    function bindMinWithTitleEvents() {
        //Open the div
        $('body').on("click", ".MinimizableWithTitle .More", function () {
            var $outerDiv = $(this).parent().parent();
            $outerDiv.children(".C").slideDown();
            $outerDiv.children(".T").children('.Less').show();
            $outerDiv.children(".T").children('.More').hide();
            $outerDiv.delay(2000).removeClass("Close");
            $outerDiv.addClass("Open");
            // return false; //Stop event from bubbling
        });

        //Close the div
        $('body').on('click', ".MinimizableWithTitle .Less", function () {
            var $outerDiv = $(this).parent().parent();
            $outerDiv.children(".C").slideUp();
            $outerDiv.children(".T").children('.More').show();
            $outerDiv.children(".T").children('.Less').hide();
            $outerDiv.delay(2000).removeClass("Open");
            $outerDiv.addClass("Close");
            //return false; //Stop event from bubbling
        });
    }

    exports.init = _.once(bindMinWithTitleEvents);
});
//# sourceMappingURL=MinimizableWithTitle.js.map
