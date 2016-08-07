define(function (require) {
	"use strict";
	console.log("in yui_test_main.js before boilerplate require");
	require("tests/yui_boilerplate");
	
	console.log("in yui_test_main.js");
	
	var yui_sample = require('tests/yui_sample');
	console.log("yui_sample" + yui_sample);
	
	//http://developer.yahoo.com/yui/docs/YAHOO.util.Event.html#method_onDOMReady //todo
	//YAHOO.util.Event.onDOMReady(function (){
		console.log("Event Triggered");
    	var logger = new YAHOO.tool.TestLogger();
	    YAHOO.tool.TestRunner.add(yui_sample);
	    YAHOO.tool.TestRunner.run();
	//});
});	
