package org.zerock.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSessionSaveUtils {
	
	public static final Logger logger = LoggerFactory.getLogger(LogSessionSaveUtils.class);
	
	// 사용자가 원래 가려고 했던 목적지를 세션에 저장해놓음.
	public static void saveDest(HttpServletRequest req) { // 아하 요청객체에 요청 정보들이 다 있구나.
		String uri = req.getRequestURI(); // 요청 URI
		String query = req.getQueryString(); // 요청 쿼리
		
		if (query == null || query.equals("null")) {
			query = ""; // 쿼리가 없으면 빈 String 값을 할당.
		} else { // 있으면?
			query = "?"+query; // ? 에 query를 더함.
		}
		
		logger.info("******************** dest : "+uri+query+" *****************");
		
		if(req.getMethod().equals("GET")) { // 요청방식이 GET 방식 이면?
			logger.info("dest: "+(uri+query)); // 목적지는?
			req.getSession().setAttribute("dest", uri + query); // 세션 저장소에 dest 를 저장해 준다.
		}
		
	}
		
}
