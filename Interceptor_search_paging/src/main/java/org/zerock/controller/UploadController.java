package org.zerock.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.service.BoardService;
import org.zerock.util.MediaUtils;
import org.zerock.util.UploadFileUtils;

@Controller
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Inject
	BoardService service;
	
	@Resource(name = "uploadPath") // 서블릿 컨텍스트에서 가져오는 듯.
	private String uploadPath;
	
	@RequestMapping(value = "/uploadForm", method=RequestMethod.GET)
	public void uploadForm() {
		
	}
	
	@RequestMapping(value = "/uploadForm", method=RequestMethod.POST)
	public String uploadForm(MultipartFile file, Model model) throws Exception { // MultipartFile로 파일을 받아옴.
	
		logger.info("originalName: " + file.getOriginalFilename());
		logger.info("size: " + file.getSize());
		logger.info("contentType: " + file.getContentType());
		
		String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
		model.addAttribute("savedName", savedName);
		
		return "uploadResult";
		
	}
	
	// 파일을 저장하고 저장된 파일 이름을 반환.
	private String uploadFile(String originalName, byte[] fileData) throws Exception { 
		
		logger.info("");
		logger.info("------------------ uploadFile() ON -----------------------");

		UUID uid = UUID.randomUUID(); // 고유 식별 문자열을 만들어줌.
		
		String savedName = uid.toString() + "_" + originalName;
		
		File target = new File(uploadPath, savedName); // 지정된 경로에 지정된 이름의 파일을 만듬.
		
		FileCopyUtils.copy(fileData, target); // 받은 파일 데이터를 target 파일에 저장.
		// FileCopyUtils : 파일 복사에 필요한 기능들을 제공하는 스프링의 라이브러리. 
		
		return savedName; // 저장한 파일 이름을 반환함.
	}
	
	@RequestMapping(value = "/uploadAjax", method=RequestMethod.GET)
	public void uploadAjax() {
		
		logger.info("");
		logger.info("------------------ uploadAjax() GET ON -----------------------");
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/uploadAjax", method=RequestMethod.POST, produces="text/plain; charset=UTF-8") // produces 속성 : 한국어 지원.
	public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
		
		logger.info("");
		logger.info("------------------ uploadAjax() POST ON -----------------------");
		logger.info("originalName: "+ file.getOriginalFilename());
		
		return new ResponseEntity<>(UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()), HttpStatus.CREATED);
		// HttpStatus : 리소스가 정상적으로 생성되었다는 코드.
		
	}
	
	@ResponseBody
	@RequestMapping("/displayFile") // 파일을 다시 보내주는 기능.
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception { // 브라우저에서 받기를 원하는 파일 이름을 파라로 받음.
		InputStream in = null; // 파일을 읽어들여서 보내주기 위함.
		ResponseEntity<byte[]> entity = null;
		
		logger.info("");
		logger.info("------------------ displayFile() ON -----------------------");
		logger.info("FILE NAME : "+ fileName);
		
		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // JPG, GIF, PNG 등을 가려내기 위함.
			MediaType mType = MediaUtils.getMediaType(formatName); // 
			HttpHeaders headers = new HttpHeaders();
			
			in = new FileInputStream(uploadPath+fileName); // 요청받은 파일을 읽는다.
			
			if(mType != null) { // 파일 확장자가 미디어 타입이면
				headers.setContentType(mType); // 미디어 타입으로 설정해줌.
			} else { // 아니면
				fileName = fileName.substring(fileName.indexOf("_")+1); // UUID를 짤라내고.
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // APPLICATION_OCTET_STREAM : 다운로드용 MIME 타입.
				headers.add("Content-Disposition", "attachment; filename=\""+new String(fileName.getBytes("UTF-8"),"ISO-8859-1")+"\"");
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED); 
					// IOUtils.toByteArray(in) : 이게 실제로 파일을 읽어내는 유틸.
					// 실제 파일을 전송한다.
						
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
		} finally {
			in.close();
		}
		
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value="/deleteFile", method=RequestMethod.POST)
	public ResponseEntity<String> deleteFile(String fileName) {
		
		logger.info("");
		logger.info("------------------ deleteFile() POST ON -----------------------");
		logger.info("delete file: "+ fileName);
		
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // 확장자를 가지고 온다.
		MediaType mType = MediaUtils.getMediaType(formatName); // 파일의 확장자 타입을 분석.
		
		if(mType != null) { // JPG, GIF, PNG 중 하나일때는 썸네일에 붙어있는 s 자를 지워줘야 한다. 원본파일 제거를 위해서.
			String front = fileName.substring(0, 12);
			String end = fileName.substring(14);
			new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete(); // 썸네일의 s자를 떼내고 원본 파일 제거.
			// 자바에서는 /를 인식 못하고 대신 \ 를 사용한다. 경로 패스를 명시할 때.
		}
		
		new File(uploadPath + fileName.replace('/', File.separatorChar)).delete(); // 기존 파일 제거. 썸네일 파일 제거.
		
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/deleteAllFiles", method=RequestMethod.POST)
	public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files) {
		// @RequestParam GET방식의 요청에 URL에서 @files[]=? 를 가지고와서 files에 저장하는 것.
		// 참고로 @PathVariable은 RESTful 방식의 요청에서 replies/1/ 등의 요청을 받을 때 1값을 가져와서 저장하기 위함.
		// 정리하자면 @RequestParam은 GET방식 요청에서 @PathVariable은 REST 방식의 요청에서 사용.
		
		logger.info("");
		logger.info("----------- deleteAllFiles() :	"+ files +" ------------------");
		
		if(files == null || files.length == 0) { // 첨부파일이 없으면? 첨부파일이 없는데 호출할 일이 있나?
			return new ResponseEntity<String>("deleted", HttpStatus.OK); // 지워졌다고. 
		}
		
		for(String fileName : files) { // 하나씩 지우는 반복문을 돌려준다.
			String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
			
			MediaType mType = MediaUtils.getMediaType(formatName);
			
			if(mType != null) { // 이미지 파일일 경우. 썸네일에 붙이는 s자를 지우고 원본파일을 삭제해주는 작업을 해야함.
				String front = fileName.substring(0, 12);
				String end = fileName.substring(14);
				
				new File(uploadPath+(front+end).replace('/', File.separatorChar)).delete();
			}
			
			new File(uploadPath+fileName.replace('/', File.separatorChar)).delete();			
		}
		
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		
	}
	
	
	
	
	
}
