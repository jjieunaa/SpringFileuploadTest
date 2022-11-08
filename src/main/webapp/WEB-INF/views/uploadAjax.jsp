<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.1/jquery.min.js"></script>
<script>
	$(function() {
		
		var regex = new RegExp("(.*?)\.(exe|sh|jar|msi|com|php|jsp|asp)$");
		var maxSize = 5242880;
		
		function checkExtensions(fileName) {
			if (regex.test(fileName)) {
				alert("해당 종류의 파일은 업로드하실 수 없습니다!");
				return false;
			}
			return true;
		}
		
		function checkFileSize(fileSize) {
			if (fileSize >= maxSize) {
				alert("업로드하실 수 있는 파일 사이즈는 최대 5MB입니다!");
				return false;
			}
			return true;
		}
				
		$("#uploadBtn").on("click", function(e) {
			var formData = new FormData();
			var inputFile = $("input[name='uploadFile']");
			var files = inputFile[0].files;
			var filesLength = files.length;
			
			for (var i=0; i<filesLength; i++) {
				
				if (!checkExtensions(files[i].name)) {
					return false;
				}
				
				if (!checkFileSize(files[i].size)) {
					return false;
				}
				
				formData.append("uploadFile", files[i]);
			}	// for
			
			$.ajax({
				url: '/uploadAjaxAction',	
				processData: false,			// 데이터 처리
				contentType: false,			// MIME 타입 (application/xml ...)
				data: formData,				// 요청 시 전송 데이터
				type: 'POST',					// HTTP Method
				dataType: "json",				// 응답 시 전송 받는 데이터의 타입
				success: function(result) {		// result: 응답 데이터
					console.log(result);
				}
			});	// ajax
		});
	});
</script>
</head>
<body>
	<h1>Upload with AJAX</h1>
	
	<div class="uploadDiv">
		<input type="file" name="uploadFile" multiple>
	</div>
	
	<div>
		<button id="uploadBtn">upload</button>
	</div>
</body>
</html>