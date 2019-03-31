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
		
		UUID uid = UUID.randomUUID(); // ���� ID �� ����.
		
		String savedName = uid.toString() + "_" + originalName; // ���� �̸��̶� ���ļ� ������ �̸� ����.
		String savedPath = calcPath(uploadPath); // ���� ��� ���.
		
		File target = new File(uploadPath+savedPath, savedName); // �ش��ο� ���� �̸����� ���� ����
		
		FileCopyUtils.copy(fileData, target); // byte�� ���� ������ target���� ����. �� �ڵ尡 ������ �����ϴ� �ڵ�.
		
		String formatName = originalName.substring(originalName.lastIndexOf(".")+1); // Ȯ���� �˾Ƴ��� ���ؼ� . �ڿ��ִ� Ȯ���ڸ� ����
		
		String uploadedFileName = null; // ���ε�� ���� �̸��� ���� ����
		
		if(MediaUtils.getMediaType(formatName) != null) { // ���� Ȯ���ڰ� �̹��� ���̸�
			uploadedFileName = makeThumbnail(uploadPath, savedPath, savedName); // �������� �����
		} else {
			uploadedFileName = makeIcon(uploadPath, savedPath, savedName); // �ƴϸ� �������� �����.
		}
		
		return uploadedFileName; // �۾��� �� ������ ���ε��� ���� �̸��� ����.
		
	}
	
	// ��θ� ġȯ�ϴ� ���ڿ� �������̶���.
	private static String makeIcon(String uploadPath, String path, String fileName) throws Exception {
		
		
		logger.info("");
		logger.info("------------------ static makeIcon() ON -----------------------");
		
		String iconName = uploadPath + path + File.separator+fileName; // ���ε��ϴ� ��ü ���� ����.
		
		logger.info("iconName =" +iconName);
		
		return iconName.substring(uploadPath.length()).replace(File.separatorChar, '/'); // 
	}
	
	public static String calcPath(String uploadPath) throws Exception {
		// ������ �����Ͱ� ������ �ʿ䰡 ���� ������ static���� ���� �Ǿ��ٰ� �Ѵ�.
		// static�� �ϳ��� �����س��� ��ΰ� �����ϴ� Ŭ���� �ɹ�. Ŭ������ 1���� ���� �ȴ�. �̸� ������ ���� ������.
		// static �޼��忡���� �ν��Ͻ� ����� ����� �� ���� ��� static �޼��带 ����ؾ���.
		
		logger.info("");
		logger.info("------------------ static calcPath() ON -----------------------");
		
		Calendar cal = Calendar.getInstance();
		
		String yearPath = File.separator+cal.get(Calendar.YEAR); // /2018
		String monthPath = yearPath + File.separator + new DecimalFormat("00").format(cal.get(Calendar.MONTH)+1); 
		// new DecimalFormat 00�� 1���϶� 01�� ����� ���ؼ� ���.
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
			
			File dirPath = new File(uploadPath + path); // yearPath, monthPath, datePath �� �� ���Ѵ�.
			
			if (! dirPath.exists()) { // dirPath �� ���� ����
				dirPath.mkdir(); //  ������ �����.
			}
			
		}
		
	}
	
	// ������� �����. ���: ���ε���, �Ϲݰ��, �����̸�.
	private static String makeThumbnail(String uploadPath, String path, String fileName) throws Exception {
		
		logger.info("");
		logger.info("------------------ makeThumbnail() ON -----------------------");
		
		BufferedImage sourceImg = ImageIO.read(new File(uploadPath + path, fileName));
		BufferedImage destImg = Scalr.resize(sourceImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, 100);
		
		String thumbnailName = uploadPath + path + File.separator + "s_" + fileName; // ��α��� ������ ����� ���� �̸�.
		
		File newFile = new File(thumbnailName); // ����� ���� ����.
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1); // �ڿ������� .�� ã�Ƽ� Ȯ���ڸ� �и���.
		
		ImageIO.write(destImg, formatName.toUpperCase(), newFile); // ���۵� �̹����� ���˳����� �빮��ȭ�ؼ� ���ο� ���Ͽ� ����.
		
		String resultThumbnailName = thumbnailName.substring(uploadPath.length()).replace(File.separatorChar, '/');
		
		return resultThumbnailName;
		
	}
	
}
