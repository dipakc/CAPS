(function() {
	"use strict";
	var jqueryMod = "lib/jquery-1.7.1.min";
	console.log("in require.config.js");
    var config = {};
	
    config.baseUrl = "/assets/javascripts";
    cofig.waitSeconds = 120;
    
	config.shim = {};
	config.shim['lib/jquery-ui-1.10.0.custom'] = {'deps': [jqueryMod]}; 
	config.shim['lib/jquery.hotkeys'] = {'deps': [jqueryMod]};
	config.shim['lib/jquery.layout'] = {'deps': [jqueryMod]};
	config.shim['lib/mathquill/mathquill'] = {'deps': [jqueryMod]};
	config.shim['lib/yuitest/logger-min'] = {'deps': ['lib/yuitest/yahoo-dom-event']};
	config.shim['lib/yuitest/yuitest-min'] = {'deps': ['lib/yuitest/logger-min', 'lib/yuitest/yahoo-dom-event']};

	require.config(config);
	
	//    config({
	//		baseUrl: "/assets/javascripts",
	//		paths: {
	//		    'yahoo-dom-event' : 'http://yui.yahooapis.com/2.9.0/build/yahoo-dom-event/yahoo-dom-event.js',
	//		    'logger' : 'http://yui.yahooapis.com/2.9.0/build/logger/logger-min.js',
	//		    'yuitest': 'http://yui.yahooapis.com/2.9.0/build/yuitest/yuitest-min.js'
	//		},
	//		shim: {
	//			'jquery-ui-1.10.0.custom' : {
	//				deps: [jqueryMod]
	//			},	    			
	//			'jquery.hotkeys' : {
	//				deps: [jqueryMod]
	//			},	    			
	//			'jquery.layout' : {
	//				deps: [jqueryMod]
	//			},
	//			'mathquill/mathquill' : {
	//				deps: [jqueryMod]
	//			},
	//			'logger' : {
	//				deps: [ydom]
	//			},
	//			'yuitest' : {
	//				deps: [ylog, ydom]
	//			}			
	//		}
	//		
	//	});
}(require))
