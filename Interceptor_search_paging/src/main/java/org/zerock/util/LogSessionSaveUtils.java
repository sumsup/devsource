package org.zerock.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSessionSaveUtils {
	
	public static final Logger logger = LoggerFactory.getLogger(LogSessionSaveUtils.class);
	
	// ����ڰ� ���� ������ �ߴ� �������� ���ǿ� �����س���.
	public static void saveDest(HttpServletRequest req) { // ���� ��û��ü�� ��û �������� �� �ֱ���.
		String uri = req.getRequestURI(); // ��û URI
		String query = req.getQueryString(); // ��û ����
		
		if (query == null || query.equals("null")) {
			query = ""; // ������ ������ �� String ���� �Ҵ�.
		} else { // ������?
			query = "?"+query; // ? �� query�� ����.
		}
		
		logger.info("******************** dest : "+uri+query+" *****************");
		
		if(req.getMethod().equals("GET")) { // ��û����� GET ��� �̸�?
			logger.info("dest: "+(uri+query)); // ��������?
			req.getSession().setAttribute("dest", uri + query); // ���� ����ҿ� dest �� ������ �ش�.
		}
		
	}
		
}
