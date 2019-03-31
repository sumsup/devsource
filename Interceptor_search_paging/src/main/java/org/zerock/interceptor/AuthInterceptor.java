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
		
		if(session.getAttribute("login") == null) { // ���ǿ� �α��� ������ ������?
			logger.info("current user is not logined");
			
			LogSessionSaveUtils.saveDest(request);
			
			Cookie loginCookie = WebUtils.getCookie(request, "loginCookie"); // req��ü���� loginCookie�� �����´�.
			
			if(loginCookie != null) { // ���� �α��� ��Ű�� ������
				UserVO userVO = service.checkLoginBefore(loginCookie.getValue()); //getValue() : ��Ű�� ������ ���� ������. �����ͼ� userVO�� ����.
				
				logger.info("USERVO: "+userVO);
				
				if(userVO != null) { // ��Ű�� ���� ������ ������
					session.setAttribute("login", userVO); // �������� ���������� �Ű� ����.
					return true; // �׸��� �� �޼��带 ������.
				}
			} // ��Ű �������� ���ٸ�?
			
			response.sendRedirect("/user/login"); // �α��� �ض�.
			return false;
		}
		
		return true;
	}
	
	
	
	
}
