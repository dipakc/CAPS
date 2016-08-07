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
        , underscore: 'lib/underscore-min'
        , log4javascript: 'lib/log4javascript'
        , semui: 'lib/Semantic-UI-CSS/semantic.min'
        , tmpderivationsdev: 'tmpderivationsdev'
        , toastr: 'lib/toastr'
        , pithy: 'lib/pithy'
        , backbone: 'lib/backbone'
    }

    ,shim: {
        'underscore': {
            exports: '_'
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
        , 'backbone': {
            deps: ['underscore', 'jquery']
            , exports: 'Backbone'
        }
        , 'toastr': {
            deps: ['jquery']
        }
        , 'SavedDerivationsNew': {
            deps: [ 'jquery', 'underscore', 'backbone', 'log4javascript', 'semui', 'tmpderivationsdev', 'toastr', 'pithy']
        }
        , 'tmpderivationsdev': {
        	deps: []
	    }         
    }
});

require(["SavedDerivationsNew", 'tmpderivationsdev']);
