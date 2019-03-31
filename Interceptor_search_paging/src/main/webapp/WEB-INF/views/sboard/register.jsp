<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@include file="../include/header.jsp"%>

<script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.1/handlebars.js"></script>
<script type="text/javascript" src="/resources/js/upload.js"></script>

<style>
.fileDrop { 
	width: 80%;
	height: 100px;
	border : 1px dotted gray;
	background-color: lightslategrey;
	margin: auto;
}
</style>

<!-- Main content -->
<section class="content">
	<div class="row">
		<!-- left column -->
		<div class="col-md-12">
			<!-- general form elements -->
			<div class="box box-primary">
				<div class="box-header">
					<h3 class="box-title">REGISTER BOARD</h3>
				</div>
				<!-- /.box-header -->

				<form role="form" id="registerForm" method="post">
					<div class="box-body">
						<div class="form-group">
							<label for="exampleInputEmail1">Title</label> 
							<input type="text" name='title' class="form-control" placeholder="Enter Title">
						</div>
						<div class="form-group">
							<label for="exampleInputPassword1">Content</label>
							<textarea class="form-control" name="content" rows="3" placeholder="Enter ..."></textarea>
						</div>
						<div class="form-group">
							<label for="exampleInputEmail1">Writer</label> 
							<input type="text" name="writer" class="form-control" value="${login.uid }" readonly>
							<!-- JSP에서 사용하는 EL($표시)의 경우 자동으로 session에 있는 login을 찾아서 사용하므로 login.uid와 같은 형태로 사용가능. -->
						</div>
						<div class="form-group">
							<label for="exampleInputEmail1">File DROP Here</label>
							<div class="fileDrop"></div>
						</div>
					</div>
					<!-- /.box-body -->

					<div class="box-footer">
						<div>
							<hr>
						</div>

						<ul class="mailbox-attachments clearfix uploadedList">
						</ul>						
					
						<button type="submit" class="btn btn-primary">Submit</button>
						<button type="button" id="cancelBtn" class="btn btn-warning">Cancel</button>
					</div>
				</form>


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

<!-- alt attribute : The alt attribute specifies an alternate text for an area, if the image cannot be displayed. -->
<!-- 템플릿에 들어갈 소스는? : 1.이미지경로(imgsrc) 2.파일링크주소(getLink) 3.파일이름(fileName) 4.전체파일이름(fullName) -->
<script id="attachTemplate" type="text/x-handlebars-template">
<li>
	<span class="mailbox-attachment-icon has-img"><img src="{{imgsrc}}" alt="Attachment"></span>
	<div class="mailbox-attachment-info">
		<a href="{{getLink}}" class="mailbox-attachment-name">{{fileName}}</a>
		<a href="{{fullName}}" class="btn btn-default btn-xs pull-right delbtn"><i class="fa fa-fw fa-remove"></i></a>
	</div>
</li>
</script>
<script>
	var template = Handlebars.compile($("#attachTemplate").html()); // 템플릿을 굽는다.
	
	$(".fileDrop").on("dragenter dragover", function(event){ // drag enter 하면 사진이 보여지기 때문에 
		event.preventDefault(); // 기본 이벤트 기능을 막는다.
	});
	
	$(".fileDrop").on("drop", function(event){ // 
		event.preventDefault(); // 사진이 보여지는 기본 기능을 막고.
		
		var files = event.originalEvent.dataTransfer.files; // 오리지날 이벤트의 파일을 가져와서
		var file = files[0]; // 파일 배열의 첫번째 파일을 꺼낸다.
		
		var formData = new FormData();
		formData.append("file", file);
		
		$.ajax({
			url: '/uploadAjax',
			data: formData,
			dataType: 'text',
			processData: false,
			contentType: false,
			type: 'POST',
			success: function(data) { // 컨트롤러에서 돌려받는 데이터는 파일 이름이다.
				
				var fileInfo = getFileInfo(data); // 템플릿에 뿌려줄 정보들을 가지고 와서.
				var html = template(fileInfo); // 템플릿에 넣고.
				
				$(".uploadedList").append(html); // 화면에 넣어준다.
				
			}
		});
		
	});
	
	$("#registerForm").submit(function(event){
		event.preventDefault(); // 서밋하면 제출하는 기본 이벤트를 막고.
		
		var that = $(this); // this 에러를 해결하기 위한 방법.
							// 다음에 나오는 each 함수에서 this가 registerForm을 가르키게 하기 위해
							// registerForm 을 의미하는 $('this')를 that 에 담는다.
		var str;
		var filesValue = "";
		
		// 첨부 파일이 있을때만 아래로직을 진행하게.
		$(".uploadedList .delbtn").each(function(index){ // each index 는 배열에 있는 모든 요소에 돌아가면서 적용하는 것. for문과 같음.
			
			str = "";
			filesValue = $(this).attr("href");
			
			// 들어온 파일들을 hidden으로 숨겨서 태그 내부에 추가한다.
			str += "<input type='hidden' name='files["+index+"]' value='"+filesValue+"'>";
			
			that.append(str); // 태그 내부에 추가.
			
			// 아래 코드가 문제인게 업로드 파일이 있을때는 작동을 하는데 없을때는 작동을 하지 않는다. 왜???
			that.get(0).submit(); // jQuery에서 DOM에 바로 접근하기 위한 방법.
								// 여기에서 that은 this인데, this는 registerForm을 의미.
								// 그중에서 get(0)은 리스트중 첫번째 것을 의미.
								// 파일들을 추가한 이후에 submit 함.
		
		});
		
		$(this).get(0).submit();
		
	});
	
	
	$("#cancelBtn").on("click", function(){
		self.location = "/sboard/list?page=${cri.page}&perPageNum=${cri.perPageNum}&searchType=${cri.searchType}"
			+"&keyword=${cri.keyword}&bno=${boardVO.bno}";
	});
	
</script>

<%@include file="../include/footer.jsp"%>