<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="../include/header.jsp"%>

<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.1/handlebars.js"></script>
<script type="text/javascript" src="/resources/js/upload.js"></script>
<style type="text/css">
	.popup { position: absolute; }
	.back { 
		background-color: gray; 
		opacity:0.5; width 
		width: 100%;
		height: 300%;
		overflow:hidden;
		z-index: 1101;
	}
	.front {
		z-index:1110; 
		opacity:1; 
		border: 1px; 
		margin: auto;
	}
	.show {
		position: relative;
		max-width: 1200px;
		max-height: 800px;
		overflow: auto;
	}

</style>
<!-- Main content -->
<section class="content">

	<!-- 이미지를 큰 크기로 보여주기 위한 div -->
	<div class='popup back' style="display:none;"></div>
	<div id="popup_front" class='popup front' style="display:none;">
		<img id="popup_img">
	</div>
	
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">MODIFY BOARD</h3>
				</div>
				<!-- /.box-header -->

<form role="form" action="modifyPage" method="post">

	<input type='hidden' name='page' value="${cri.page}"> 
	<input type='hidden' name='perPageNum' value="${cri.perPageNum}">
	<input type='hidden' name='searchType' value="${cri.searchType}">
	<input type='hidden' name='keyword' value="${cri.keyword}">

					<div class="box-body">

						<div class="form-group">
							<label for="exampleInputEmail1">BNO</label> 
							<input type="text" name='bno' class="form-control" value="${boardVO.bno}" readonly="readonly">
						</div>

						<div class="form-group">
							<label for="exampleInputEmail1">Title</label> 
							<input type="text" name='title' class="form-control" value="${boardVO.title}">
						</div>
						<div class="form-group">
							<label for="exampleInputPassword1">Content</label>
							<textarea class="form-control" name="content" rows="3">${boardVO.content}</textarea>
						</div>
						<div class="form-group">
							<label for="exampleInputEmail1">Writer</label> 
							<input type="text" name="writer" class="form-control" value="${boardVO.writer}">
						</div>
					</div>
					<!-- /.box-body -->
				</form>
				<ul class="mailbox-attachments clearfix uploadedList"></ul>
				
				<div class="box-footer">
					<button type="submit" class="btn btn-primary">SAVE</button>
					<button type="submit" class="btn btn-warning">CANCEL</button>
				</div>
				
<!-- span 태그는 div같이 블록을 지정하는 태그인데 구역을 지정하는게 아니라 라인을 지정한다. 스타일링을 위한 태그. -->
<script id="templateAttach" type="text/x-handlebars-template">
<li data-src='{{fullName}}'>
	<span class="mailbox-attachment-icon has-img">
		<img src="{{imgsrc}}" alt="Attachment">
	</span>
	<div class="mailbox-attachment-info">
		<a href="{{getLink}}" class="mailbox-attachment-name">{{fileName}}</a>
	</div>
</li>
</script>

<script>
$(document).ready(
	function() {

		var formObj = $("form[role='form']");

		console.log(formObj);

		$(".btn-warning")
				.on("click",function() {
					self.location = "/sboard/readPage?page=${cri.page}&perPageNum=${cri.perPageNum}&searchType=${cri.searchType}&keyword="
						+"${cri.keyword}&bno=${boardVO.bno}";
				});

		$(".btn-primary").on("click",
				function() {
					formObj.submit();
				});
	});
	
var bno = ${boardVO.bno};
var template = Handlebars.compile($("#templateAttach").html());

$.getJSON("/sboard/getAttach/"+bno, function(list){
	$(list).each(function(){ // 받아온 첨부파일 이름 목록. 반복문 처리
			
		var fileInfo = getFileInfo(this); // 파일 정보들을 가지고 와서. fullName, uploadPath, srcLink, getLink 등등 있잖아..
		var html = template(fileInfo); // 템플릿으로 찍어내서 html 에 저장해서
		
		$(".uploadedList").append(html); // 리스트 파일에 뿌려준다.
		
	});
});

$(".uploadedList").on("click", ".mailbox-attachment-info a", function(event){ // 파일클릭시 화면위에 원본 이미지 파일 출력하는 효과.
	
	// 코드를 잘 모를때는 개발자 도구에서 하나씩 돌려본다.
	var fileLink = $(this).attr("href"); // 링크값을 가져온다.
	
	if(checkImageType(fileLink)) { // 파일타입이 이미지 파일이면.
		event.preventDefault(); // 기본 이벤트를 막고.
		
		var imgTag = $("#popup_img"); //  
		imgTag.attr("src", fileLink);
		
		console.log(imgTag.attr("src"));
		
		$(".popup").show('slow');
		imgTag.addClass("show");
	}
	
});

$("#popup_img").on("click", function(){
	$(".popup").hide('slow');
});


</script>




			</div>
			<!-- /.box -->
		</div>
		<!--/.col (left) -->

	</div>
	<!-- /.row -->
</section>
<!-- /.content -->
</div>
<!-- /.content-wrapper -->

<%@include file="../include/footer.jsp"%>
