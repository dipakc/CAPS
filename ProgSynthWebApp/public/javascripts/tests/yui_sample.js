define(function (require) {
	"use strict";
	require("tests/yui_boilerplate");
	
	console.log("in yui_sample.js");
	
	var testCase = new YAHOO.tool.TestCase({
		 
	    name: "YUI Sample",
	 
	    // Setup and tear down	 
	    setUp : function () {
	        this.data = { name : "Nicholas", age : 28 };
	    },
	 
	    tearDown : function () {
	        delete this.data;
	    },
	    //---------------------------------------------
	    testName: function () {
	        YAHOO.util.Assert.areEqual("Nicholas", this.data.name, "Name should be 'Nicholas'");
	    },
	 
	    testAge: function () {
	        YAHOO.util.Assert.areEqual(28, this.data.age, "Age should be 28");
	    }    
	});
	return testCase;
});
