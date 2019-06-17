$(document).ready(function() {
	// $("#detailsDiv").hide();
	// do stuff when DOM is ready
	// $('#ajax-continue').click(function(e) {
	$('#ajax-continue').click(function(e) {
	     // stop normal link click
	     e.preventDefault();
		 url = $('#actionForm').attr('action');
		 productId = $('#actionForm').find('input[name="productId"]').val();
		 headline = $('#actionForm').find('input[name="headline"]').val();
	     $('#detailsDiv').load(url, {ajax: "true", productId: productId, headline: headline}, function(data) {
		       // format and output result
	    	$('#continueButtonDiv').remove();
	    	 $('#phase').val('2');
		 });
	});
 });
