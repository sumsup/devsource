function checkImageType(fileName) { // 확장자를 가지고 이미지 파일인지 아닌지를 판단함.
	var pattern = /jpg|gif|png|jpeg/i;
	
	return fileName.match(pattern);
}

function getFileInfo(fullName) { // 파일이름, 이미지주소, 링크, 풀네임을 돌려줌.
	
	var fileName, imgsrc, getLink;
	
	var fileLink;
	
	if(checkImageType(fullName)) { // 이미지 파일이면
		imgsrc = "/displayFile?fileName="+fullName; // 이미지 소스.
		fileLink = fullName.substr(14); // 링크는 앞에 2018/11/21/s_ 을 제거해 준 부분으로.
		
		// 아래 두개는 썸네일 파일이 아닌 원본 파일용.
		var front = fullName.substr(0,12); 
		var end = fullName.substr(14);
		
		getLink = "/displayFile?fileName="+front+end;
	} else { // 이미지 파일이 아닐 경우
		imgsrc = "/resources/dist/img/file.png";
		fileLink = fullName.substr(12); // 2018/11/21/을 제거해준 걸로.
		getLink = "/displayFile?fileName="+fullName;
	}
	fileName = fileLink.substr(fileLink.indexOf("_")+1); // UUID 빼준거.
	
	return {fileName:fileName, imgsrc:imgsrc, getLink:getLink, fullName: fullName}; // 배열로 리턴함.
	
	
}