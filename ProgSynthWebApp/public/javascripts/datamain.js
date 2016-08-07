//var jqueryMod = "lib/jquery-1.7.1.min";

//All the relative urls (starting with ./ or ../ ) are with respect to this file.
//All non-relative urls are with respect to the baseUrl
require.config({
	
	// Commenting baseUrl. It is set by data-main attribute in the script tag of the main html file 
	// Refer for additional details (https://github.com/jrburke/requirejs/issues/773)

	//baseUrl: "/assets/javascripts" 
    //,
    paths: {
         jquery : 'lib/jquery-1.7.1.min'
        , jquery_ui: 'lib/jquery-ui-1.10.0.custom'
        , jquery_hotkeys: 'lib/jquery.hotkeys'
        , jquery_layout: 'lib/jquery.layout'
        , mathquill: 'lib/mathquill/mathquill'
        , backbone: 'lib/backbone'
        , underscore: 'lib/underscore-min'
        , handlebars: 'lib/handlebars'
        , hydrate: 'lib/Hydrate'
        , toastr: 'lib/toastr'
        , text: 'lib/text'
        , log4javascript: 'lib/log4javascript'
        , semui: 'lib/Semantic-UI-CSS/semantic.min'
        , tmpdev: 'tmpdev'
    }

    ,shim: {
        'jquery_ui' : {
            deps: ['jquery']
        }
        , 'jquery_hotkeys': {
            deps: ['jquery']
        }
        , 'jquery_layout': {
            deps: ['jquery']
        }
        ,'mathquill' : {
            deps: ['jquery']
        }
        ,'underscore': {
            exports: '_'
        }
        ,'backbone' : {
            deps: ['underscore', 'jquery']
            ,exports: 'Backbone'
        }
        ,'handlebars' : {
            deps: ['jquery']
        }        
        ,'hydrate' : {
            deps: ['jquery']
        }        
        , 'toastr': {
            deps: ['jquery']
        } 
        , 'log4javascript': {            
            exports: 'log4javascript'
            //http://stackoverflow.com/questions/18293471/cannot-get-log4javascript-to-work-with-an-inpageappender-when-using-requirejs
            //, init: function() {
                //log4javascript.setDocumentReady();
            //}
        } 
        , 'semui' : {
	    	deps: ['jquery']
	    }
        , 'PSDerivation': {
            deps: [ 'jquery', 'underscore',  'backbone', 'jquery_ui',
                   'jquery_hotkeys', 'jquery_layout', 'mathquill', 
                   'hydrate', 'handlebars', 'toastr', 'log4javascript', 'semui']
        }
        , 'tmpdev' : {
        	deps: ['PSDerivation']
	    } 
        
    }
});

//require(["PSDerivation", 'tmpdev']);
require(['PSDerivation']);
