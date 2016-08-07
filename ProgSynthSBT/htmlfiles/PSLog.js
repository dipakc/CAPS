$( document ).ready(function() {
	$(".Section").dblclick(function(event){
		event.stopPropagation();
		$(this).closest(".Section").toggleClass("Collapsed");
		//console.log(event.target.outerHTML);
	});



});


/* <div class='Section'>
 *	  <div class='LogInfo'>
 *     	<div id='Logger'> logger </div>
 *      <div id='Level'> level </div>
 *    </div>
 *    <div class='StartDate'>2006-10-20 14:06:49,812</div>
 *    <div class='SectionTitle'> title </div>
 *	  <div class='SectionBody'>
 *    	<div class='Message'>
 *	  		<div class='LogInfo'>
 *     			<div class='Logger'> logger </div>
 *      		<div class='Level'> level </div>
 *    		</div>
 *     	<div class='Date'> date </div>
 *     	<div class='MessageBody'> messageText </div>
 *    </div>
 *    <div class='EndDate'>date</div>
 *    <div class='Duration'>000</div>
 * </div>
 *
 */
