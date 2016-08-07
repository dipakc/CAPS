/// <reference path="typings/jquery/jquery.d.ts" />
/// <amd-dependency path="jquery" />

"use strict";

//format("i can speak {language} since i was {age}",{language:'javascript',age:10});
//format("i can speak {0} since i was {1}",'javascript',10});
//To enter {, put {{
declare var jsRoutes: any;

export function format(str, col) {
    col = typeof col === 'object' ? col : Array.prototype.slice.call(arguments, 1);

    return str.replace(/\{\{|\}\}|\{(\w+)\}/g, function (m, n) {
        if (m == "{{") { return "{"; }
        if (m == "}}") { return "}"; }
        return col[n];
    });
};

//closest descendant
//http://stackoverflow.com/questions/8961770/similar-to-jquery-closest-but-traversing-descedents
(function ($) {
    $.fn.closest_descendent = function (filter) {
        var $found = $(),
            $currentSet = this; // Current place
        while ($currentSet.length) {
            $found = $currentSet.filter(filter);
            if ($found.length) break;  // At least one match: break loop
            // Get all children of the current set
            $currentSet = $currentSet.children();
        }
        return $found.first(); // Return first match of the collection
    }
})(jQuery);

$.fn.exists = function () {
    return this.length !== 0;
}

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

export function extractLastUrlSegment() {
    // url: http://127.0.0.1:9000/caps/intDiv.capstxt
    // output: intDiv.capstxt
    return window.location.pathname.match(/\/([^\/]+)$/)[1];
}

//Get application context url
export function getRoolUrl() {
    return jsRoutes.controllers.Application.derivations().url;
}