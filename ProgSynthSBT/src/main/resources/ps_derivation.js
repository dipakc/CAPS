
function getCurNodeId() {
	return $('.left .curNodeId').html();
}

function getContentFromNodeId(nodeId) {
	//alert('entered in fun' + nodeId);
	var nodeIdElem = $('.nodeid').filter(function(){ return $(this).text().trim() === nodeId;});
	//alert(nodeIdElem.text());
	return nodeIdElem.siblings('.nodeObj').html();
}

function setContent(content) {
	$(".content").html(content);  
}

function selectNextNode() {
	//find the selected .node
	$selectedNode = $('.node.selected');
	//find the next .node
	$nextNode = $selectedNode.children('.node');
	//activate the next .node
	if($nextNode.length > 0)
		activateNodeElem($nextNode);
}

function selectPrevNode() {
	//find the selected .node
	$selectedNode = $('.node.selected');
	//find the prev .node
	$prevNode = $selectedNode.parent('.node');
	//activate the prev .node
	if($prevNode.length > 0)
		activateNodeElem($prevNode);
}

//activates a given .node elem
function activateNodeElem($nodeElem) {
	if(!$nodeElem.hasClass('node')) alert($nodeElem.hasClass('node'));
	//Clean up all the earlier selection
	$('.node').removeClass('selected');
	//alert(nodeElem.child('.nodeid').html());
	$(".content").html($nodeElem.children('.nodeObj').html());
	//Add 'selected' class
	$nodeElem.addClass('selected');
}
$(document).ready(function(){
	$("<style type='text/css'> .selected > .nodeid, .selected > .tactic{ color:#A55; font-weight:bold;} </style>").appendTo("head");
});

//Hilight the selected "node and tactic" in the left pane.
// .node		
	// .nodeid = 3	
	// .tactic = RetVal	
	// .nodeObj	
	// .node	
		// .nodeid = 4
		// .tactic = StepIntoUnknownProgIdx
		// .nodeObj

$(document).ready(function(){
	
	 $(".nodeid,.tactic").click( function(){
		var $nodeElem = $(this).parent('.node');
		activateNodeElem($nodeElem);
	 });
});    	            




$(document).ready(function(){
	$("#toggleIdBtn.topButton").click( function(){
		$(".content .programAnn .displayId").toggle();
		
		$(".content .programAnn").toggleClass('ProgramAnnDisplayIdClass DisplayIdClass');		
		
		$(".content .programAnn .Invariant").toggleClass('InvariantDisplayIdClass DisplayIdClass');
		
		$(".content .programAnn .Pred").toggleClass('PredDisplayIdClass DisplayIdClass');

		$(".content .programAnn .Var").toggleClass('VarDisplayIdClass TermDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Const").toggleClass('ConstDisplayIdClass TermDisplayIdClass DisplayIdClass');
		$(".content .programAnn .ArrSelect").toggleClass('ArrSelectDisplayIdClass TermDisplayIdClass DisplayIdClass');
		$(".content .programAnn .ArrStore").toggleClass('ArrStoreDisplayIdClass TermDisplayIdClass DisplayIdClass');
		$(".content .programAnn .FnApp").toggleClass('FnAppDisplayIdClass TermDisplayIdClass DisplayIdClass');
		
		$(".content .programAnn .True1").toggleClass('True1DisplayIdClass  FormulaDisplayIdClass DisplayIdClass');		
		$(".content .programAnn .False1").toggleClass('False1DisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Not").toggleClass('NotDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .And").toggleClass('AndDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Or").toggleClass('OrDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Impl").toggleClass('ImplDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Iff").toggleClass('IffDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Forall").toggleClass('ForallDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Exists").toggleClass('ExistsDisplayIdClass FormulaDisplayIdClass DisplayIdClass');
		$(".content .programAnn .Unknown").toggleClass('UnknownDisplayIdClass FormulaDisplayIdClass DisplayIdClass');		
		
		$('.right .statusbar .displayIdDiv').html('');
		
	});

	$(".content").live({ 
		mouseover: function(event) {
			var activeElem = $(event.target).closest('.DisplayIdClass');
			var activeElemId = activeElem.attr('displayIdAttr');
			//display the id in status bar
			$('.right .statusbar .displayIdDiv').html("ID: " + activeElemId );
			//hilight the border of the activeElem
			$(".content").find(".DisplayIdClass").removeClass('outline-element');			
			activeElem.addClass('outline-element');
			//activeElem.css('border-width', '7px');
		}
	});
	
});  
///////////////////////////////////////////////////////////////////////////////////////
//Options Handlers
function ShowExprProgHandler() {
	var $theProg = $(".ProgramAnn .program .ExprProg").parent();
	var $theProgramAnn = $theProg.parent();
	$theProgramAnn.siblings("collapse").hide();
	$theProgramAnn.children(".id,.displayId").hide();
	if(localStorage.ShowExprProg !== "checked"){
		$theProgramAnn.children(".pre,.post").hide();
		$theProgramAnn.toggleClass("ProgramAnn", false).css('clear', 'both');
		$theProg.toggleClass("program", false).css('clear', 'both');
	} else{
		$theProgramAnn.children(".pre,.post").show();
		$theProgramAnn.toggleClass("ProgramAnn", true).css('clear', 'both');
		$theProg.toggleClass("program", true).css('clear', 'both');	
	}
}

//PopulateOptions from local storage
function populateOptions($optionsObj) {
	//alert('populate Called');
	if (localStorage) {
        if (localStorage.ShowExprProg === "checked") {
          $("#ShowExprProg").attr("checked", "checked");
        }
	}
}

$(document).ready(function(){	
	//Call all options handlers
	ShowExprProgHandler();

	//Bind show option dialog to the Options button.
	$("#optionsBtn.topButton").click( function(){
		var optionsDialog = '' +
		'<div id="OptionsId">' +
			'<div>Options</div><br>' +
				'<label class="checkbox">' +
					'<input name="ShowExprProg" id="ShowExprProg" type="checkbox">' +
						'Show ExprProg'+
				'</label>'+
		'</div>';
		$('head').append(optionsDialog)
		populateOptions();
		$('head #OptionsId').dialog();
	});

	//UI change hanlders
	$("input[type=checkbox]").live({
		change: function(event){
			$this = $(this);
			localStorage[$this.attr("name")] = $this.attr("checked");
			//call handler
			ShowExprProgHandler();
		}		
	});

	$("input[type=text],select,textarea").live({
		change: function(event){
			//alert('checkbox changed');
			$this = $(this);
			localStorage[$this.attr("name")] = $this.val();
			//call handler
		}		
	});		
	
});

///////////////////////////////////////////////////////////////////////////////////////
//When the document is loaded, activate the current Node.
$(document).ready(function(){
	var curNodeId = getCurNodeId();
	//alert(curNodeId);
	var $nodeIdElem = $('.node').filter(function(){ return $(this).children('.nodeid').text().trim() === curNodeId;});
	activateNodeElem($nodeIdElem);
	//var content = getContentFromNodeId(curNodeId);
	//alert(content);
	//setContent(content);
});

//Foldable div: Two states
/*
------------------------
.xyz.unfolded
	ABC							
------------------------
div.folded
	div.foldicon
		ooo
	.xyz.folddata[style="display: none;"]
		ABC
------------------------
*/
$(document).ready(function(){
	$(".unfolded").live({
		dblclick: function(event){
			var $activeElem = $(this);
			$activeElem
				.removeClass("unfolded").addClass("folddata").hide('slow')
				.wrap("<div class='folded'  style='clear: both;' />").parent()
				.prepend("<div class='foldicon' style='clear: both;'>ooo</div>");

			return false; //Stop event from bubbling
		}		
	});

	
	$(".foldicon").live({
		click : function(event){
			var $activeElem = $(this);
			$activeElem
				.siblings(".folddata")
				.unwrap()
				.addClass("unfolded").removeClass("folddata")
				.show('slow');
			$activeElem.remove();
			return false; //Stop event from bubbling
		}
	});
});

//Key bindings
function ctrlup() {alert("ctrl up pressed");}
function ctrldown(){alert("ctrl  down pressed");}


$(document).bind('keydown', 'ctrl+down', selectNextNode);
$(document).bind('keydown', 'ctrl+up', selectPrevNode);

//Colors
//467058
//76B08F
//E7EBC3
////////////////////////////////////////////////////////////////////
