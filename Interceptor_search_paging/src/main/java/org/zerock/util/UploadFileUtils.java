package org.zerock.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

public class UploadFileUtils {
	
	
	private static final Logger logger = LoggerFactory.getLogger(UploadFileUtils.class);
	
	public static String uploadFile(String uploadPath, String originalName, byte[] fileData) throws Exception {
		
		logger.info("");
		logger.info("------------------ static uploadFile() ON -----------------------");
		
		UUID uid = UUID.randomUUID(); // 고유 ID 값 생성.
		
		String savedName = uid.toString() + "_" + originalName; // 파일 이름이랑 합쳐서 저장할 이름 만듬.
		String savedPath = calcPath(uploadPath); // 저장 경로 계산.
		
		File target = new File(uploadPath+savedPath, savedName); // 해당경로에 파일 이름으로 파일 생성
		
		FileCopyUtils.copy(fileData, target); // byte로 받은 파일을 target으로 복사. 이 코드가 파일을 복사하는 코드.
		
		String formatName = originalName.substring(originalName.lastIndexOf(".")+1); // 확장자 알아내기 위해서 . 뒤에있는 확장자를 따옴
		
		String uploadedFileName = null; // 업로드될 파일 이름을 담을 변수
		
		if(MediaUtils.getMediaType(formatName) != null) { // 파일 확장자가 이미지 값이면
			uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName); // 섬네일을 만들고
		} else {
			uploadedFileName = makeIcon(uploadPath, savedPath, savedName); // 아니면 아이콘을 만든다.
		}
		
		return uploadedFileName; // 작업을 다 끝내면 업로드한 파일 이름을 리턴.
		
	}
	
	// 경로를 치환하는 문자열 아이콘이란다.
	private static String makeIcon(String uploadPath, String path, String fileName) throws Exception {
		
		
		logger.info("");
		logger.info("------------------ static makeIcon() ON -----------------------");
		
		String iconName = uploadPath + path + File.separator+fileName; // 업로드하는 전체 파일 네임.
		
		logger.info("iconName =" +iconName);
		
		return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/'); // 
	}
	
	public static String calcPath(String uploadPath) throws Exception {
		// 별도의 데이터가 보관될 필요가 없기 때문에 static으로 설계 되었다고 한다.
		// static은 하나만 생성해놓고 모두가 공유하는 클래스 맴버. 클래스당 1개만 생성 된다. 미리 생성해 놓고 공유함.
		// static 메서드에서는 인스턴스 멤버를 사용할 수 없고 모두 static 메서드를 사용해야함.
		
		logger.info("");
		logger.info("------------------ static calcPath() ON -----------------------");
		
		Calendar cal = Calendar.getInstance();
		
		String yearPath = File.separator+cal.get(Calendar.YEAR); // /2018
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1); 
		// new DecimalFormat 00은 1월일때 01로 만들기 위해서 사용.
		String datePath = monthPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.DATE));
		
		makeDir(uploadPath, yearPath, monthPath, datePath);
		
		logger.info(datePath);
		
		return datePath;
	}
	
	
	private static void makeDir(String uploadPath, String... paths) {
		
		logger.info("");
		logger.info("------------------ makeDir() ON -----------------------");
		
		if(new File(uploadPath + paths[paths.length - 1]).exists()) {
			return;
		}
		
		for (String path: paths) {
			
			File dirPath = new File(uploadPath + path); // yearPath, monthPath, datePath 를 다 더한다.
			
			if (! dirPath.exists()) { // dirPath 가 없을 떄는
				dirPath.mkdir(); //  폴더를 만든다.
			}
			
		}
		
	}
	
	// 썸네일을 만든다. 재료: 업로드경로, 일반경로, 파일이름.
	private static String makeThumbnail(String uploadPath, String path, String fileName) throws Exception {
		
		logger.info("");
		logger.info("------------------ makeThumbnail() ON -----------------------");
		
		BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));
		BufferedImage destImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, 100);
		
		String thumbnailName = uploadPath + path + File.separator + "s_" + fileName; // 경로까지 포함한 썸네일 파일 이름.
		
		File newFile = new File(thumbnailName); // 썸네일 파일 생성.
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // 뒤에서부터 .을 찾아서 확장자를 분리함.
		
		ImageIO.write(destImg, formatName.toUpperCase(), newFile); // 버퍼드 이미지를 포맷네임을 대문자화해서 새로운 파일에 쓴다.
		
		String resultThumbnailName = thumbnailName.substring(uploadPath.length()).replace(File.separatorChar, '/');
		
		return resultThumbnailName;
		
	}
	
}
