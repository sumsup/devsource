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
<!-- <section>태그 : 하나의 컨텐츠의 주제로 그룹짓는 태그. -->
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
					<h3 class="box-title">READ BOARD</h3>
				</div>
				<!-- /.box-header -->

				<form role="form" action="modifyPage" method="post">

					<input type='hidden' name='bno' value="${boardVO.bno}"> 
					<input type='hidden' name='page' value="${cri.page}"> 
					<input type='hidden' name='perPageNum' value="${cri.perPageNum}">
					<input type='hidden' name='searchType' value="${cri.searchType}">
					<input type='hidden' name='keyword' value="${cri.keyword}">

				</form>

				<div class="box-body">
					<div class="form-group">
						<label for="exampleInputEmail1">Board Number</label> 
						<input type="text" name='bno' class="form-control" value="${boardVO.bno}" readonly="readonly">
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Title</label> 
						<input type="text" name='title' class="form-control" value="${boardVO.title}" readonly="readonly">
					</div>
					<div class="form-group">
						<label for="exampleInputPassword1">Content</label>
						<textarea class="form-control" name="content" rows="3" readonly="readonly">${boardVO.content}</textarea>
					</div>
					<div class="form-group">
						<label for="exampleInputEmail1">Writer</label> 
						<input type="text" name="writer" class="form-control" value="${boardVO.writer}" readonly="readonly">
					</div>
				</div>
				<!-- /.box-body -->
				
			  <ul class="mailbox-attachments clearfix uploadedList"></ul>

			  <div class="box-footer">
			  	<%-- <h2>login.uid = ${login.uid }</h2>
			  	<h2>boardVO.writer = ${boardVO.writer }</h2> --%>
			  	<c:if test="${login.uid == boardVO.writer}">
			    	<button type="submit" class="btn btn-warning" id="modifyBtn">Modify</button>
			    	<button type="submit" class="btn btn-danger" id="removeBtn">REMOVE</button>
			    </c:if>
			    	<button type="submit" class="btn btn-primary" id="goListBtn">GO LIST </button>
			  </div>



			</div>
			<!-- /.box -->
		</div>
		<!--/.col (left) -->

	</div>
	<!-- /.row -->



	<div class="row">
		<div class="col-md-12">

			<div class="box box-success">
				<div class="box-header">
					<h3 class="box-title">ADD NEW REPLY</h3>
				</div>
				<c:if test="${not empty login }">
					<div class="box-body">
						<label for="exampleInputEmail1">Writer</label> 
						<input class="form-control" type="text" value="${login.uid }" placeholder="USER ID" id="newReplyWriter" readonly> 
						<label for="exampleInputEmail1">Reply Text</label> 
						<input class="form-control" type="text"	placeholder="REPLY TEXT" id="newReplyText">
	
					</div>
				<!-- /.box-body -->
					<div class="box-footer">
						<button type="button" class="btn btn-primary" id="replyAddBtn">ADD REPLY</button>
					</div>
				</c:if>
				
				<c:if test="${empty login }">
					<div class="box-body">
						<div><a href="javascript:goLogin();">Login Please</a></div>
					</div>
				</c:if>
			</div>

		
		<!-- The time line -->
		<ul class="timeline">
		  <!-- timeline time label -->
			<li class="time-label" id="repliesDiv">
		  		<span class="bg-green">
		  			 Replies List <i><b>(Click Here)</b></i><small id='replycntSmall'> [ ${boardVO.replycnt} ] </small>
		  		</span>
	  		</li>
		</ul>
		   
			<div class='text-center'>
				<ul id="pagination" class="pagination pagination-sm no-margin ">

				</ul>
			</div>

		</div>
		<!-- /.col -->
	</div>
	<!-- /.row -->


          
<!-- Modal -->
<div id="modifyModal" class="modal modal-primary fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title"></h4>
      </div>
      <div class="modal-body" data-rno>
        <p><input type="text" id="replytext" class="form-control"></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-info" id="replyModBtn">Modify</button>
        <button type="button" class="btn btn-danger" id="replyDelBtn">DELETE</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>      
	
	
</section>
<!-- /.content -->

<script id="replyTemplate" type="text/x-handlebars-template">
{{#each .}}
<li class="replyLi" data-rno={{rno}}>
<i class="fa fa-comments bg-blue"></i>
 
	<div class="timeline-item" >
  	<span class="time">
    	<i class="fa fa-clock-o"></i>{{prettifyDate regdate}}
  	</span>
  	<h3 class="timeline-header"><strong>{{rno}}</strong> -{{replyer}}</h3>
  	<div class="timeline-body">{{replytext}} </div>
    	<div class="timeline-footer">
			{{#eqReplyer replyer}}
     			<a class="btn btn-primary btn-xs" data-toggle="modal" data-target="#modifyModal">Modify</a>
			{{/eqReplyer}}
    	</div>
  	</div>			
</li>
{{/each}}
</script>

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
	Handlebars.registerHelper("prettifyDate", function(timeValue) {
		var dateObj = new Date(timeValue);
		var year = dateObj.getFullYear();
		var month = dateObj.getMonth() + 1;
		var date = dateObj.getDate();
		return year + "/" + month + "/" + date;
	});
	
	Handlebars.registerHelper("eqReplyer", function(replyer, block) {
		var accum = '';
		if (replyer == '${login.uid}') {
			accum += block.fn(); // block.fn() : 이것도 뭐하는건지 모르겠음. 헨들바 옵션인가.
		}
		return accum;
	});

	var printData = function(replyArr, target, templateObject) {

		var template = Handlebars.compile(templateObject.html());

		var html = template(replyArr);
		$(".replyLi").remove();
		target.after(html);

	}

	var bno = ${boardVO.bno};
	var template = Handlebars.compile($("#templateAttach").html());
	
	var replyPage = 1;
	
	// 
	$.getJSON("/sboard/getAttach/"+bno, function(list){
		$(list).each(function(){ // 받아온 첨부파일 이름 목록. 반복문 처리
 			
			var fileInfo = getFileInfo(this); // 파일 정보들을 가지고 와서. fullName, uploadPath, srcLink, getLink 등등 있잖아..
			var html = template(fileInfo); // 템플릿으로 찍어내서 html 에 저장해서
			
			$(".uploadedList").append(html); // 리스트 파일에 뿌려준다.
			
		});
	});

	function getPage(pageInfo){
		
		$.getJSON(pageInfo,function(data){
			printData(data.list, $("#repliesDiv") ,$('#replyTemplate'));
			printPaging(data.pageMaker, $(".pagination"));
			
			$("#modifyModal").modal('hide');
			$("#replycntSmall").html("[ " + data.pageMaker.totalCount +" ]");
			
		});
	}



	var printPaging = function(pageMaker, target) {

		var str = "";

		if (pageMaker.prev) {
			str += "<li><a href='" + (pageMaker.startPage - 1)
					+ "'> << </a></li>";
		}

		for (var i = pageMaker.startPage, len = pageMaker.endPage; i <= len; i++) {
			var strClass = pageMaker.cri.page == i ? 'class=active' : '';
			str += "<li "+strClass+"><a href='"+i+"'>" + i + "</a></li>";
		}

		if (pageMaker.next) {
			str += "<li><a href='" + (pageMaker.endPage + 1)
					+ "'> >> </a></li>";
		}

		target.html(str);
	};
	
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

	$("#repliesDiv").on("click", function() {

		if ($(".timeline li").size() > 1) {
			return;
		}
		getPage("/replies/" + bno + "/1");

	});
	

	$(".pagination").on("click", "li a", function(event){
		
		event.preventDefault();
		
		replyPage = $(this).attr("href");
		
		getPage("/replies/"+bno+"/"+replyPage);
		
	});
	

	$("#replyAddBtn").on("click",function(){
		 
		 var replyerObj = $("#newReplyWriter");
		 var replytextObj = $("#newReplyText");
		 var replyer = replyerObj.val();
		 var replytext = replytextObj.val();
		
		  
		  $.ajax({
				type:'post',
				url:'/replies/',
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "POST" },
				dataType:'text',
				data: JSON.stringify({bno:bno, replyer:replyer, replytext:replytext}),
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("등록 되었습니다.");
						replyPage = 1;
						getPage("/replies/"+bno+"/"+replyPage );
						replyerObj.val("");
						replytextObj.val("");
					}
			}});
	});


	$(".timeline").on("click", ".replyLi", function(event){
		
		var reply = $(this);
		
		$("#replytext").val(reply.find('.timeline-body').text());
		$(".modal-title").html(reply.attr("data-rno"));
		
	});
	
	

	$("#replyModBtn").on("click",function(){
		  
		  var rno = $(".modal-title").html();
		  var replytext = $("#replytext").val();
		  
		  $.ajax({
				type:'put',
				url:'/replies/'+rno,
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "PUT" },
				data:JSON.stringify({replytext:replytext}), 
				dataType:'text', 
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("수정 되었습니다.");
						getPage("/replies/"+bno+"/"+replyPage );
					}
			}});
	});

	$("#replyDelBtn").on("click",function(){
		  
		  var rno = $(".modal-title").html();
		  var replytext = $("#replytext").val();
		  
		  $.ajax({
				type:'delete',
				url:'/replies/'+rno,
				headers: { 
				      "Content-Type": "application/json",
				      "X-HTTP-Method-Override": "DELETE" },
				dataType:'text', 
				success:function(result){
					console.log("result: " + result);
					if(result == 'SUCCESS'){
						alert("삭제 되었습니다.");
						getPage("/replies/"+bno+"/"+replyPage );
					}
			}});
	});
	
</script>


<script>
$(document).ready(function(){
	
	var formObj = $("form[role='form']");
	
	console.log(formObj);
	
	$("#modifyBtn").on("click", function(){
		formObj.attr("action", "/sboard/modifyPage");
		formObj.attr("method", "get");		
		formObj.submit();
	});
	
	$("#removeBtn").on("click", function(){
		
		var arr = [];
		$(".uploadedList li").each(function(index){
			arr.push($(this).attr("data-src"));
		});
		
		if(arr.length > 0) {
			$.post("/deleteAllFiles", {files:arr}, function(){
				
			});
		}
		
		formObj.attr("action", "/sboard/removePage");
		formObj.submit();
	});
	
	
	$("#goListBtn ").on("click", function(){
		formObj.attr("method", "get");
		formObj.attr("action", "/sboard/list");
		formObj.submit();
	});
	
});
</script>






<%@include file="../include/footer.jsp"%>
