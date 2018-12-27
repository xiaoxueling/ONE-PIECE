var ajax = function(url, data, success, type, dataType) {
	
			url=getUrl(url);
			
			data.r=Math.random();
			type = arguments[3] ? arguments[3] : 'get';
			dataType = arguments[4] ? arguments[4] : 'json';
			$.ajax({
				url: url,
				type: type, //GET
				data: data,
				timeout: 500000, 
				dataType: dataType, //json/xml/html/script/jsonp/text
				beforeSend: function(xhr) {
					console.log(xhr)
					console.log('准备发送请求')
				},
				success: function(data, textStatus, jqXHR) {
					var JData = JSON.parse(jqXHR.responseText);
					console.log(data)
					console.log(textStatus)
					console.log(jqXHR)
					if(dataType == 'json'){
						success(JData);
					}else{
						success(jqXHR.responseText);
					}
				},
				error: function(xhr, textStatus) {
					console.log('失败')
					console.log(xhr)
					console.log(textStatus)
				},
				complete: function() {
					console.log('完成')
				}
			})
		};