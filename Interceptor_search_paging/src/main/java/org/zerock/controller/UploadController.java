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
	
	@Resource(name = "uploadPath") // ���� ���ؽ�Ʈ���� �������� ��.
	private String uploadPath;
	
	@RequestMapping(value = "/uploadForm", method=RequestMethod.GET)
	public void uploadForm() {
		
	}
	
	@RequestMapping(value = "/uploadForm", method=RequestMethod.POST)
	public String uploadForm(MultipartFile file, Model model) throws Exception { // MultipartFile�� ������ �޾ƿ�.
	
		logger.info("originalName: " + file.getOriginalFilename());
		logger.info("size: " + file.getSize());
		logger.info("contentType: " + file.getContentType());
		
		String savedName = uploadFile(file.getOriginalFilename(), file.getBytes());
		model.addAttribute("savedName", savedName);
		
		return "uploadResult";
		
	}
	
	// ������ �����ϰ� ����� ���� �̸��� ��ȯ.
	private String uploadFile(String originalName, byte[] fileData) throws Exception { 
		
		logger.info("");
		logger.info("------------------ uploadFile() ON -----------------------");

		UUID uid = UUID.randomUUID(); // ���� �ĺ� ���ڿ��� �������.
		
		String savedName = uid.toString() + "_" + originalName;
		
		File target = new File(uploadPath, savedName); // ������ ��ο� ������ �̸��� ������ ����.
		
		FileCopyUtils.copy(fileData, target); // ���� ���� �����͸� target ���Ͽ� ����.
		// FileCopyUtils : ���� ���翡 �ʿ��� ��ɵ��� �����ϴ� �������� ���̺귯��. 
		
		return savedName; // ������ ���� �̸��� ��ȯ��.
	}
	
	@RequestMapping(value = "/uploadAjax", method=RequestMethod.GET)
	public void uploadAjax() {
		
		logger.info("");
		logger.info("------------------ uploadAjax() GET ON -----------------------");
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/uploadAjax", method=RequestMethod.POST, produces="text/plain; charset=UTF-8") // produces �Ӽ� : �ѱ��� ����.
	public ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {
		
		logger.info("");
		logger.info("------------------ uploadAjax() POST ON -----------------------");
		logger.info("originalName: "+ file.getOriginalFilename());
		
		return new ResponseEntity<>(UploadFileUtils.uploadFile(uploadPath, file.getOriginalFilename(), file.getBytes()), HttpStatus.CREATED);
		// HttpStatus : ���ҽ��� ���������� �����Ǿ��ٴ� �ڵ�.
		
	}
	
	@ResponseBody
	@RequestMapping("/displayFile") // ������ �ٽ� �����ִ� ���.
	public ResponseEntity<byte[]> displayFile(String fileName) throws Exception { // ���������� �ޱ⸦ ���ϴ� ���� �̸��� �Ķ�� ����.
		InputStream in = null; // ������ �о�鿩�� �����ֱ� ����.
		ResponseEntity<byte[]> entity = null;
		
		logger.info("");
		logger.info("------------------ displayFile() ON -----------------------");
		logger.info("FILE NAME : "+ fileName);
		
		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // JPG, GIF, PNG ���� �������� ����.
			MediaType mType = MediaUtils.getMediaType(formatName); // 
			HttpHeaders headers = new HttpHeaders();
			
			in = new FileInputStream(uploadPath+fileName); // ��û���� ������ �д´�.
			
			if(mType != null) { // ���� Ȯ���ڰ� �̵�� Ÿ���̸�
				headers.setContentType(mType); // �̵�� Ÿ������ ��������.
			} else { // �ƴϸ�
				fileName = fileName.substring(fileName.indexOf("_")+1); // UUID�� ©�󳻰�.
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // APPLICATION_OCTET_STREAM : �ٿ�ε�� MIME Ÿ��.
				headers.add("Content-Disposition", "attachment; filename=\""+new String(fileName.getBytes("UTF-8"),"ISO-8859-1")+"\"");
			}
			
			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED); 
					// IOUtils.toByteArray(in) : �̰� ������ ������ �о�� ��ƿ.
					// ���� ������ �����Ѵ�.
						
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
		
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // Ȯ���ڸ� ������ �´�.
		MediaType mType = MediaUtils.getMediaType(formatName); // ������ Ȯ���� Ÿ���� �м�.
		
		if(mType != null) { // JPG, GIF, PNG �� �ϳ��϶��� ����Ͽ� �پ��ִ� s �ڸ� ������� �Ѵ�. �������� ���Ÿ� ���ؼ�.
			String front = fileName.substring(0, 12);
			String end = fileName.substring(14);
			new File(uploadPath + (front+end).replace('/', File.separatorChar)).delete(); // ������� s�ڸ� ������ ���� ���� ����.
			// �ڹٿ����� /�� �ν� ���ϰ� ��� \ �� ����Ѵ�. ��� �н��� ����� ��.
		}
		
		new File(uploadPath + fileName.replace('/', File.separatorChar)).delete(); // ���� ���� ����. ����� ���� ����.
		
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/deleteAllFiles", method=RequestMethod.POST)
	public ResponseEntity<String> deleteFile(@RequestParam("files[]") String[] files) {
		// @RequestParam GET����� ��û�� URL���� @files[]=? �� ������ͼ� files�� �����ϴ� ��.
		// ����� @PathVariable�� RESTful ����� ��û���� replies/1/ ���� ��û�� ���� �� 1���� �����ͼ� �����ϱ� ����.
		// �������ڸ� @RequestParam�� GET��� ��û���� @PathVariable�� REST ����� ��û���� ���.
		
		logger.info("");
		logger.info("----------- deleteAllFiles() :	"+ files +" ------------------");
		
		if(files == null || files.length == 0) { // ÷�������� ������? ÷�������� ���µ� ȣ���� ���� �ֳ�?
			return new ResponseEntity<String>("deleted", HttpStatus.OK); // �������ٰ�. 
		}
		
		for(String fileName : files) { // �ϳ��� ����� �ݺ����� �����ش�.
			String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
			
			MediaType mType = MediaUtils.getMediaType(formatName);
			
			if(mType != null) { // �̹��� ������ ���. ����Ͽ� ���̴� s�ڸ� ����� ���������� �������ִ� �۾��� �ؾ���.
				String front = fileName.substring(0, 12);
				String end = fileName.substring(14);
				
				new File(uploadPath+(front+end).replace('/', File.separatorChar)).delete();
			}
			
			new File(uploadPath+fileName.replace('/', File.separatorChar)).delete();			
		}
		
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		
	}
	
	
	
	
	
}
