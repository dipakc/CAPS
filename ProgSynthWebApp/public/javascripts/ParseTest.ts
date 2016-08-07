/// <reference path="typings/jquery/jquery.d.ts" />
/// <amd-dependency path="lib/jquery" />
/// <amd-dependency path="PSJqueryUtils" />

"use strict";

function onParseButtonSubmit() {
	console.log("submit clicked" );
	var ptype = $(".parseTypeCombo option:selected").val();
	console.log("ptype: " + ptype);		
	var mqText = (<any>$(".parseInput.mathquill-editable")).mathquill('latex');
	console.log("mqText: " + mqText);
	var inputData = [ptype, mqText];
	console.log("sending post request to server...");
	var inputDataStr = JSON.stringify(inputData)
	$(".parseSentInput .content").html(inputDataStr)
	$(".parseResult .content").html("<div>Parsing ... </div>")
	$.ajax({
		url: 'parseTestSubmit',
		type: 'POST',
		data: inputDataStr,
		contentType: 'application/json; charset=utf-8',
		dataType: 'html',
		//async: false,
		success: parseTestResponseHandler
	});	
}
	
function parseTestResponseHandler(response) {
	console.log("got response: " + response);
	$(".parseResult .content").html($(response));
}

function bindParseButton() {
    $('#parseSubmit').on('click', onParseButtonSubmit);
}	

$(document).ready(function () {
    bindParseButton();
});

