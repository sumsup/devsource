package org.zerock.interceptor;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import org.zerock.domain.UserVO;
import org.zerock.service.UserService;
import org.zerock.util.LogSessionSaveUtils;

public class AuthInterceptor extends HandlerInterceptorAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	
	@Inject
	private UserService service;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		
		if(session.getAttribute("login") == null) { // 세션에 로그인 정보가 없으면?
			logger.info("current user is not logined");
			
			LogSessionSaveUtils.saveDest(request);
			
			Cookie loginCookie = WebUtils.getCookie(request, "loginCookie"); // req객체에서 loginCookie를 가져온다.
			
			if(loginCookie != null) { // 만약 로그인 쿠키가 있으면
				UserVO userVO = service.checkLoginBefore(loginCookie.getValue()); //getValue() : 쿠키에 설정된 값을 가져옴. 가져와서 userVO에 저장.
				
				logger.info("USERVO: "+userVO);
				
				if(userVO != null) { // 쿠키에 유저 정보가 있으면
					session.setAttribute("login", userVO); // 세션으로 유저정보를 옮겨 저장.
					return true; // 그리고 이 메서드를 끝낸다.
				}
			} // 쿠키 정보마저 없다면?
			
			response.sendRedirect("/user/login"); // 로그인 해라.
			return false;
		}
		
		return true;
	}
	
	
	
	
}
