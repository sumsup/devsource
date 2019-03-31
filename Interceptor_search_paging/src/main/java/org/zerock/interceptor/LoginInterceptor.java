package org.zerock.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.zerock.util.LogSessionSaveUtils;

// login 페이지에서 ID와 PW를 입력하고 submit을 하면 loginPost로 이동하기전에 껴드는 인터셉터.
public class LoginInterceptor extends HandlerInterceptorAdapter {
	
	private static final String LOGIN = "login";
	private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws Exception { // handler : 지금 다루고 있는 메서드. ex) SearchBoardController.listPage

		HttpSession session = request.getSession(); //세션 저장소를 가지고와서
		LogSessionSaveUtils.saveDest(request); // 목적지 저장.
		
		if(session.getAttribute(LOGIN) != null) { // 세션에 이미 로그인된 정보가 있으면?
			logger.info("clear login data before");
			session.removeAttribute(LOGIN); // 세션에 로그인 정보를 지운다.
		}
		
		return true;
	}
	
	@Override
	// UserController에서 로그인한 유저의 정보를 가지고 와서 모델에 userVO로 넣어놨다.
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		HttpSession session = request.getSession();
		ModelMap modelMap = modelAndView.getModelMap(); // ModelMap은 뭐야.
		// modelAndView는 뭐야? This interface allows us to pass all the information required by Spring MVC in one return.
		// 쉽게 말해서 modelAndView 객체에 객체도 넣을 수 있고 view 페이지 정보도 넣을 수 있다는 것.
		// modelAndView.addObject와 같은 메서드로.
		
		Object userVO = modelMap.get("userVO");
		
		if(userVO != null) {
			logger.info("new login success");
			session.setAttribute(LOGIN, userVO);
			
			if(request.getParameter("useCookie") != null) { // 쿠키가 있는지 확인하는 작업. 사용자가 useCookie 를 선택했는지를 확인함.
				
				logger.info("remember me..................");
				Cookie loginCookie = new Cookie("loginCookie", session.getId()); // session.getId() : 세션을 가리키는 고유 id값을 String 형으로 변환한다.
				// 쉽게 말해서 현재 세션을 가지고 와서 쿠키에 저장한다는 것.
				loginCookie.setPath("/"); // 모든 경로에서 접근 가능.
				loginCookie.setMaxAge(60*60*24*7); // 쿠키유지기간 7일.
				response.addCookie(loginCookie); // 응답객체에 cookie 저장.
				
			}
			
			Object dest = session.getAttribute("dest"); // 아까 AuthInterceptor에서 session에 넣어놨던 dest를 꺼내와서.
			
			response.sendRedirect(dest != null ? (String)dest : "/sboard/list"); // 목적지가 있으면 목적지로 이동하고. 없으면 홈(/)으로.
		}
	
	}
	
	
	
}
