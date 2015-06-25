$(function() {

	$.getJSON("remoteConfig.json", function(remoteConfig) {

		window.setInterval(function() {
			$.ajax({
				dataType: "json",
				url: remoteConfig.requestUrl,
				data: {someProperty: "someValue"},
				success: function(responseData) {
					$("#test").html(
						"request date: " + new Date() + 
						"<br><br>url: " + remoteConfig.requestUrl + 
						"<br><br>response is:<br>" + JSON.stringify(responseData)
					);
				}
			});
		}, 2000);

		
	});

});