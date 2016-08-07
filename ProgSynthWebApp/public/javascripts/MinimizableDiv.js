/// <reference path="typings/jquery/jquery.d.ts" />
/// <reference path="typings/requirejs/require.d.ts" />
"use strict";
define(["require", "exports"], function(require, exports) {
    exports._dummyExport = undefined;

    function minimize($outerDiv) {
        $outerDiv.children('.MaxContent').hide('slow');
        $outerDiv.children('.MinContent').show('slow');
    }
    ;

    function maximize($outerDiv) {
        $outerDiv.children('.MinContent').hide('slow');
        $outerDiv.children('.MaxContent').show('slow');
    }
    ;

    function bindEvents() {
        //MinLink functionality
        $("body").on('click', '.MinLink', function (event) {
            var $outerDiv = $(this).parent().parent();
            minimize($outerDiv);
            return false;
        });

        //MaxLink functionality
        $("body").on('click', '.MaxLink', function (event) {
            var $outerDiv = $(this).parent().parent();
            maximize($outerDiv);
            return false;
        });

        //Show Min Link on mouse enter
        $("body").on('mouseenter', '.Minimizable', function (event) {
            var $this = $(this);
            $this.children('.MaxContent').children('.MinLink').show();
            return false;
        });

        //Hide Min Link on mouse leave
        $("body").on('mouseleave', '.Minimizable', function (event) {
            var $this = $(this);
            $this.children('.MaxContent').children('.MinLink').hide();
            return false;
        });
    }

    exports.init = _.once(bindEvents);
});
//Test html
/*
<div class="Minimizable">
<div class="MinContent"><div>prog</div></div>
<div class="MaxContent">
<div class="Prog">
<div class="Statement">stmt1</div>
<div class="Statement">stmt2</div>
</div>
</div>
</div>
//////////////
<div class="Minimizable">
<div class="MinContent"><div>prog</div></div>
<div class="MaxContent">
<div class="Prog">
<div class="Statement">stmt1</div>
<div class="Statement">
<div class="Minimizable">
<div class="MinContent"><div>prog</div></div>
<div class="MaxContent Prog">
<div class="Statement">stmt3</div>
<div class="Statement">stmt4 </div>
</div>
</div>
</div>
</div>
</div>
</div>
*/
//Test CSS
/*
///////////////////
div {
float:left
}
.Prog div{
}
.Prog {
border: solid;
}
.Statement {
clear:both;
margin: 0.5em;
border: solid;
}
////////////////////
.MinContent {
border:solid;
}
.MaxLink {
margin-left: 1em;
background-color: yellow;
}
.MinLink {
background-color: yellow;
opacity:0.5;
border:solid;
}
///////////////////////
//Essential
.MaxContent {
position:relative;
}
.MinLink {
position:absolute;
right:0%;
top:0;
z-index:2;
}
*/
//# sourceMappingURL=MinimizableDiv.js.map
