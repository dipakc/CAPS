/// <reference path="typings/jquery/jquery.d.ts" />
"use strict";
define(["require", "exports"], function(require, exports) {
    //require("lib/jquery-1.7.1.min");
    //var psJqueryUtils = require("PSJqueryUtils");
    //var jqueryLayout = require("lib/jquery.layout");
    function dolayout() {
        //Layout Hack: set height of all the parents to OuterLayout to 100%.
        $("html").css("height", "100%");
        $("body").css("height", "100%");
        $(".OuterLayout").css("height", "100%");

        var outerLayout = $(".OuterLayout").single().layout(defoptions);
        outerLayout.options.south.enableCursorHotkey = false;
        outerLayout.allowOverflow('north'); //allowoverflow for the menubar
        outerLayout.sizePane("south", 200);
        outerLayout.sizePane("west", 250);

        //mainLayout.allowOverflow('west');//allowoverflow for the context menu
        // save selector strings to vars so we don't have to repeat it
        // must prefix paneClass with ".pslayout > " to target ONLY the mainLayout panes
        var westSelector = ".OuterLayout > .ui-layout-west";
        var southSelector = ".OuterLayout > .ui-layout-south";

        $(westSelector).single();
        $(southSelector).single();

        /*
        // CREATE SPANs for pin-buttons - using a generic class as identifiers
        $("<span></span>").addClass("pin-button").prependTo( westSelector );
        //$("<span></span>").addClass("pin-button").prependTo( eastSelector );
        $("<span></span>").addClass("pin-button").prependTo( southSelector );
        // BIND events to pin-buttons to make them functional
        mainLayout.addPinBtn( westSelector +" .pin-button", "west");
        //mainLayout.addPinBtn( eastSelector +" .pin-button", "east" );
        mainLayout.addPinBtn( southSelector +" .pin-button", "south" );
        */
        // CREATE SPANs for close-buttons - using unique IDs as identifiers
        $("<span></span>").attr("id", "west-closer").prependTo(westSelector);
        $("<span></span>").attr("id", "south-closer").prependTo(southSelector);

        // BIND layout events to close-buttons to make them functional
        outerLayout.addCloseBtn("#west-closer", "west");
        outerLayout.addCloseBtn("#south-closer", "south");

        // CREATE SPANs for toggler-buttons - using unique IDs as identifiers
        $("<span></span>").attr("id", "west-toggler").prependTo(westSelector);
        $("<span></span>").attr("id", "south-toggler").prependTo(southSelector);

        // BIND layout events to close-buttons to make them functional
        outerLayout.addToggleBtn("#west-toggler", "west");
        outerLayout.addToggleBtn("#south-toggler", "south");

        return outerLayout;
    }
    exports.dolayout = dolayout;

    var layoutSettings_Outer = {
        name: "mainLayout",
        defaults: {
            size: "auto",
            minSize: 50,
            paneClass: "ui-layout-pane",
            resizerClass: "ui-layout-resizer",
            togglerClass: "ui-layout-toggler",
            buttonClass: "ui-layout-button",
            contentIgnoreSelector: "span",
            togglerLength_open: 35,
            togglerLength_closed: 35,
            hideTogglerOnSlide: true,
            togglerTip_open: "Close This Pane",
            togglerTip_closed: "Open This Pane",
            resizerTip: "Resize This Pane",
            fxName: "slide",
            fxSpeed_open: 750,
            fxSpeed_close: 1500,
            fxSettings_open: { easing: "easeInQuint" },
            fxSettings_close: { easing: "easeOutQuint" }
        },
        west: {
            size: 250,
            spacing_closed: 21,
            togglerLength_closed: 21,
            togglerAlign_closed: "top",
            togglerLength_open: 0,
            togglerTip_open: "Close West Pane",
            togglerTip_closed: "Open West Pane",
            resizerTip_open: "Resize West Pane",
            slideTrigger_open: "click",
            initClosed: false,
            fxSettings_open: { easing: "" }
        },
        east: {
            size: 250,
            spacing_closed: 21,
            togglerLength_closed: 21,
            togglerAlign_closed: "top",
            togglerLength_open: 0,
            togglerTip_open: "Close East Pane",
            togglerTip_closed: "Open East Pane",
            resizerTip_open: "Resize East Pane",
            slideTrigger_open: "click",
            initClosed: false,
            fxName: "drop",
            fxSpeed: "normal",
            fxSettings: { easing: "" }
        },
        center: {
            paneSelector: "#mainContent",
            minWidth: 200,
            minHeight: 200
        }
    };

    var prefix = "ui-layout-";
    var defaults = {
        paneClass: prefix + "pane",
        resizerClass: prefix + "resizer",
        togglerClass: prefix + "toggler",
        togglerInnerClass: prefix + "",
        buttonClass: prefix + "button",
        contentSelector: "." + prefix + "content",
        contentIgnoreSelector: "." + prefix + "ignore"
    };

    // DEFAULT PANEL OPTIONS - CHANGE IF DESIRED
    var defoptions = {
        name: "",
        scrollToBookmarkOnLoad: true,
        defaults: {
            applyDefaultStyles: false,
            closable: true,
            resizable: true,
            slidable: true,
            contentSelector: defaults.contentSelector,
            contentIgnoreSelector: defaults.contentIgnoreSelector,
            paneClass: defaults.paneClass,
            resizerClass: defaults.resizerClass,
            togglerClass: defaults.togglerClass,
            buttonClass: defaults.buttonClass,
            resizerDragOpacity: 1,
            maskIframesOnResize: true,
            minSize: 0,
            maxSize: 0,
            spacing_open: 6,
            spacing_closed: 6,
            togglerLength_open: 35,
            togglerLength_closed: 35,
            togglerAlign_open: "center",
            togglerAlign_closed: "center",
            togglerTip_open: "Close",
            togglerTip_closed: "Open",
            resizerTip: "Resize",
            sliderTip: "Slide Open",
            sliderCursor: "pointer",
            slideTrigger_open: "click",
            slideTrigger_close: "mouseout",
            hideTogglerOnSlide: false,
            togglerContent_open: "",
            togglerContent_closed: "",
            showOverflowOnHover: false,
            enableCursorHotkey: true,
            customHotkeyModifier: "SHIFT",
            fxName: "slide",
            fxSpeed: null,
            fxSettings: {},
            initClosed: false,
            initHidden: false
        },
        north: {
            paneSelector: "." + prefix + "north",
            size: "auto",
            resizerCursor: "n-resize",
            spacing_open: 0,
            spacing_closed: 0
        },
        south: {
            paneSelector: "." + prefix + "south",
            size: "auto",
            resizerCursor: "s-resize",
            spacing_closed: 21,
            togglerLength_closed: 21,
            togglerAlign_closed: "left",
            togglerLength_open: 0,
            togglerTip_open: "Close South Pane",
            togglerTip_closed: "Open South Pane",
            resizerTip_open: "Resize South Pane",
            slideTrigger_open: "click",
            hideTogglerOnSlide: false
        },
        east: {
            paneSelector: "." + prefix + "east",
            size: 200,
            resizerCursor: "e-resize"
        },
        west: {
            paneSelector: "." + prefix + "west",
            size: 200,
            resizerCursor: "w-resize",
            spacing_closed: 21,
            togglerLength_closed: 21,
            togglerAlign_closed: "top",
            togglerLength_open: 0,
            togglerTip_open: "Close West Pane",
            togglerTip_closed: "Open West Pane",
            resizerTip_open: "Resize West Pane",
            slideTrigger_open: "click",
            hideTogglerOnSlide: false
        },
        center: {
            paneSelector: "." + prefix + "center"
        }
    };

    exports._dummyExport = undefined;
});
//# sourceMappingURL=psweb.jquery.layout.js.map
