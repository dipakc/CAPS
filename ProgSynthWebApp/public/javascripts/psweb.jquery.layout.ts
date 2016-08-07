/// <reference path="typings/jquery/jquery.d.ts" />

"use strict";
//require("lib/jquery-1.7.1.min");
//var psJqueryUtils = require("PSJqueryUtils");
//var jqueryLayout = require("lib/jquery.layout");

export function dolayout() {

    //Layout Hack: set height of all the parents to OuterLayout to 100%.
    (<any>$("html")).css("height", "100%");
    (<any>$("body")).css("height", "100%");
    (<any>$(".OuterLayout")).css("height", "100%");

    var outerLayout = (<any>$(".OuterLayout")).single().layout(defoptions);
    outerLayout.options.south.enableCursorHotkey = false;
    outerLayout.allowOverflow('north');//allowoverflow for the menubar
    outerLayout.sizePane("south", 200);
    outerLayout.sizePane("west", 250);
    //mainLayout.allowOverflow('west');//allowoverflow for the context menu

    // save selector strings to vars so we don't have to repeat it
    // must prefix paneClass with ".pslayout > " to target ONLY the mainLayout panes
    var westSelector = ".OuterLayout > .ui-layout-west";
    var southSelector = ".OuterLayout > .ui-layout-south";

    (<any>$(westSelector)).single();
    (<any>$(southSelector)).single();

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

var layoutSettings_Outer = {
    name: "mainLayout" // NO FUNCTIONAL USE, but could be used by custom code to 'identify' a layout
    // options.defaults apply to ALL PANES - but overridden by pane-specific settings
	, defaults: {
		size: "auto"
        , minSize: 50
        , paneClass: "ui-layout-pane" 		// default = 'ui-layout-pane'
        , resizerClass: "ui-layout-resizer"	// default = 'ui-layout-resizer'
        , togglerClass: "ui-layout-toggler"	// default = 'ui-layout-toggler'
        , buttonClass: "ui-layout-button"	// default = 'ui-layout-button'
		//,	contentSelector:		".content"	// inner div to auto-size so only it scrolls, not the entire pane!
        , contentIgnoreSelector: "span"		// 'paneSelector' for content to 'ignore' when measuring room for content
        , togglerLength_open: 35			// WIDTH of toggler on north/south edges - HEIGHT on east/west edges
        , togglerLength_closed: 35			// "100%" OR -1 = full height
        , hideTogglerOnSlide: true		// hide the toggler when pane is 'slid open'
        , togglerTip_open: "Close This Pane"
        , togglerTip_closed: "Open This Pane"
        , resizerTip: "Resize This Pane"
        , fxName: "slide"		// none, slide, drop, scale
        , fxSpeed_open: 750
        , fxSpeed_close: 1500
        , fxSettings_open: { easing: "easeInQuint" }
        , fxSettings_close: { easing: "easeOutQuint" }
    }
	, west: {
		size: 250
        , spacing_closed: 21			// wider space when closed
        , togglerLength_closed: 21			// make toggler 'square' - 21x21
        , togglerAlign_closed: "top"		// align to top of resizer
        , togglerLength_open: 0			// NONE - using custom togglers INSIDE west-pane
        , togglerTip_open: "Close West Pane"
        , togglerTip_closed: "Open West Pane"
        , resizerTip_open: "Resize West Pane"
        , slideTrigger_open: "click" 	// default
        , initClosed: false
		//	add 'bounce' option to default 'slide' effect
        , fxSettings_open: { easing: "" }
	}
	, east: {
		size: 250
        , spacing_closed: 21			// wider space when closed
        , togglerLength_closed: 21			// make toggler 'square' - 21x21
        , togglerAlign_closed: "top"		// align to top of resizer
        , togglerLength_open: 0 			// NONE - using custom togglers INSIDE east-pane
        , togglerTip_open: "Close East Pane"
        , togglerTip_closed: "Open East Pane"
        , resizerTip_open: "Resize East Pane"
        , slideTrigger_open: "click"
        , initClosed: false
        , fxName: "drop"
        , fxSpeed: "normal"
        , fxSettings: { easing: "" } // nullify default easing
	}
	, center: {
		paneSelector: "#mainContent" 			// sample: use an ID to select pane instead of a class
        , minWidth: 200
        , minHeight: 200
	}
};

var prefix = "ui-layout-" // prefix for ALL selectors and classNames
var defaults = { //	misc default values
    paneClass: prefix + "pane"		// ui-layout-pane
	, resizerClass: prefix + "resizer"	// ui-layout-resizer
	, togglerClass: prefix + "toggler"	// ui-layout-toggler
	, togglerInnerClass: prefix + ""			// ui-layout-open / ui-layout-closed
	, buttonClass: prefix + "button"		// ui-layout-button
	, contentSelector: "." + prefix + "content"// ui-layout-content
	, contentIgnoreSelector: "." + prefix + "ignore"	// ui-layout-mask 
};

// DEFAULT PANEL OPTIONS - CHANGE IF DESIRED
var defoptions = {
    name: ""			// FUTURE REFERENCE - not used right now
	, scrollToBookmarkOnLoad: true		// after creating a layout, scroll to bookmark in URL (.../page.htm#myBookmark)
	, defaults: { // default options for 'all panes' - will be overridden by 'per-pane settings'
		applyDefaultStyles: false		// apply basic styles directly to resizers & buttons? If not, then stylesheet must handle it
        , closable: true		// pane can open & close
        , resizable: true		// when open, pane can be resized 
        , slidable: true		// when closed, pane can 'slide' open over other panes - closes on mouse-out
		//,	paneSelector:			[ ]			// MUST be pane-specific!
        , contentSelector: defaults.contentSelector	// INNER div/element to auto-size so only it scrolls, not the entire pane!
        , contentIgnoreSelector: defaults.contentIgnoreSelector	// elem(s) to 'ignore' when measuring 'content'
        , paneClass: defaults.paneClass		// border-Pane - default: 'ui-layout-pane'
        , resizerClass: defaults.resizerClass	// Resizer Bar		- default: 'ui-layout-resizer'
        , togglerClass: defaults.togglerClass	// Toggler Button	- default: 'ui-layout-toggler'
        , buttonClass: defaults.buttonClass	// CUSTOM Buttons	- default: 'ui-layout-button-toggle/-open/-close/-pin'
        , resizerDragOpacity: 1			// option for ui.draggable
		//,	resizerCursor:			""			// MUST be pane-specific - cursor when over resizer-bar
        , maskIframesOnResize: true		// true = all iframes OR = iframe-selector(s) - adds masking-div during resizing/dragging
		//,	size:					100			// inital size of pane - defaults are set 'per pane'
        , minSize: 0			// when manually resizing a pane
        , maxSize: 0			// ditto, 0 = no limit
        , spacing_open: 6			// space between pane and adjacent panes - when pane is 'open'
        , spacing_closed: 6			// ditto - when pane is 'closed'
        , togglerLength_open: 35			// Length = WIDTH of toggler button on north/south edges - HEIGHT on east/west edges
        , togglerLength_closed: 35			// 100% OR -1 means 'full height/width of resizer bar' - 0 means 'hidden'
        , togglerAlign_open: "center"	// top/left, bottom/right, center, OR...
        , togglerAlign_closed: "center"	// 1 => nn = offset from top/left, -1 => -nn == offset from bottom/right
        , togglerTip_open: "Close"		// Toggler tool-tip (title)
        , togglerTip_closed: "Open"		// ditto
        , resizerTip: "Resize"	// Resizer tool-tip (title)
        , sliderTip: "Slide Open" // resizer-bar triggers 'sliding' when pane is closed
        , sliderCursor: "pointer"	// cursor when resizer-bar will trigger 'sliding'
        , slideTrigger_open: "click"		// click, dblclick, mouseover
        , slideTrigger_close: "mouseout"	// click, mouseout
        , hideTogglerOnSlide: false		// when pane is slid-open, should the toggler show?
        , togglerContent_open: ""			// text or HTML to put INSIDE the toggler
        , togglerContent_closed: ""			// ditto
        , showOverflowOnHover: false		// will bind allowOverflow() utility to pane.onMouseOver
        , enableCursorHotkey: true		// enabled 'cursor' hotkeys
		//,	customHotkey:			""			// MUST be pane-specific - EITHER a charCode OR a character
        , customHotkeyModifier: "SHIFT"		// either 'SHIFT', 'CTRL' or 'CTRL+SHIFT' - NOT 'ALT'
		//	NOTE: fxSss_open & fxSss_close options (eg: fxName_open) are auto-generated if not passed
        , fxName: "slide" 	// ('none' or blank), slide, drop, scale
        , fxSpeed: null		// slow, normal, fast, 200, nnn - if passed, will OVERRIDE fxSettings.duration
        , fxSettings: {}			// can be passed, eg: { easing: "easeOutBounce", duration: 1500 }
        , initClosed: false		// true = init pane as 'closed'
        , initHidden: false 		// true = init pane as 'hidden' - no resizer or spacing

		/*	callback options do not have to be set - listed here for reference only
		,	onshow_start:			""			// CALLBACK when pane STARTS to Show	- BEFORE onopen/onhide_start
		,	onshow_end:				""			// CALLBACK when pane ENDS being Shown	- AFTER  onopen/onhide_end
		,	onhide_start:			""			// CALLBACK when pane STARTS to Close	- BEFORE onclose_start
		,	onhide_end:				""			// CALLBACK when pane ENDS being Closed	- AFTER  onclose_end
		,	onopen_start:			""			// CALLBACK when pane STARTS to Open
		,	onopen_end:				""			// CALLBACK when pane ENDS being Opened
		,	onclose_start:			""			// CALLBACK when pane STARTS to Close
		,	onclose_end:			""			// CALLBACK when pane ENDS being Closed
		,	onresize_start:			""			// CALLBACK when pane STARTS to be ***MANUALLY*** Resized
		,	onresize_end:			""			// CALLBACK when pane ENDS being Resized ***FOR ANY REASON***
		*/
	}
	, north: {
		paneSelector: "." + prefix + "north" // default = .ui-layout-north
        , size: "auto"
        , resizerCursor: "n-resize"
        , spacing_open: 0
        , spacing_closed: 0
	}
	, south: {
		paneSelector: "." + prefix + "south" // default = .ui-layout-south
        , size: "auto"
        , resizerCursor: "s-resize"
		/////////////////////////////
		//-----------------
        , spacing_closed: 21
        , togglerLength_closed: 21
        , togglerAlign_closed: "left"
        , togglerLength_open: 0
        , togglerTip_open: "Close South Pane"
        , togglerTip_closed: "Open South Pane"
        , resizerTip_open: "Resize South Pane"
        , slideTrigger_open: "click"
        , hideTogglerOnSlide: false

	}
	, east: {
		paneSelector: "." + prefix + "east" // default = .ui-layout-east
        , size: 200
        , resizerCursor: "e-resize"
		//--------------------------
	}
	, west: {
		paneSelector: "." + prefix + "west" // default = .ui-layout-west
        , size: 200
        , resizerCursor: "w-resize"
		//-----------------
        , spacing_closed: 21
        , togglerLength_closed: 21
        , togglerAlign_closed: "top"
        , togglerLength_open: 0
        , togglerTip_open: "Close West Pane"
        , togglerTip_closed: "Open West Pane"
        , resizerTip_open: "Resize West Pane"
        , slideTrigger_open: "click"
        , hideTogglerOnSlide: false

	}
	, center: {
		paneSelector: "." + prefix + "center" // default = .ui-layout-center
	}
};

export var _dummyExport = undefined;