/// <reference path="typings/jquery/jquery.d.ts" />
/// <amd-dependency path="jquery" />
"use strict";
define(["require", "exports", "jquery"], function(require, exports) {
    

    function format(str, col) {
        col = typeof col === 'object' ? col : Array.prototype.slice.call(arguments, 1);

        return str.replace(/\{\{|\}\}|\{(\w+)\}/g, function (m, n) {
            if (m == "{{") {
                return "{";
            }
            if (m == "}}") {
                return "}";
            }
            return col[n];
        });
    }
    exports.format = format;
    ;

    //closest descendant
    //http://stackoverflow.com/questions/8961770/similar-to-jquery-closest-but-traversing-descedents
    (function ($) {
        $.fn.closest_descendent = function (filter) {
            var $found = $(), $currentSet = this;
            while ($currentSet.length) {
                $found = $currentSet.filter(filter);
                if ($found.length)
                    break;

                // Get all children of the current set
                $currentSet = $currentSet.children();
            }
            return $found.first();
        };
    })(jQuery);

    $.fn.exists = function () {
        return this.length !== 0;
    };

    jQuery.fn.single = function () {
        if (this.length != 1) {
            console.trace();
            throw new Error("Expected 1 matching element, found " + this.length);
        }

        return this;
    };

    jQuery.fn.nonempty = function () {
        if (this.length <= 0) {
            console.trace();
            throw new Error("Expected at least 1 matching element, found " + this.length);
        }
        return this;
    };

    function extractLastUrlSegment() {
        // url: http://127.0.0.1:9000/caps/intDiv.capstxt
        // output: intDiv.capstxt
        return window.location.pathname.match(/\/([^\/]+)$/)[1];
    }
    exports.extractLastUrlSegment = extractLastUrlSegment;

    //Get application context url
    function getRoolUrl() {
        return jsRoutes.controllers.Application.derivations().url;
    }
    exports.getRoolUrl = getRoolUrl;
});
//# sourceMappingURL=PSJqueryUtils.js.map
