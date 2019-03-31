package org.zerock.controller;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;
import org.zerock.domain.UserVO;
import org.zerock.dto.LoginDTO;
import org.zerock.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Inject // @Inject와 @AutoWired와의 차이점? 기능은 같다. 
	// @Inject : 자바에서 지원.
	// @AutoWired : 스프링에서 지원.
	private UserService service;
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public void loginGET(@ModelAttribute("dto") LoginDTO dto, Model model) {
		
	}
	
	@RequestMapping(value="/loginPost", method = RequestMethod.POST)
	// 인터셉터에서 세션에 이미 로그인된 정보가 있으면 지우고 왔다.
	public void loginPOST(LoginDTO dto, HttpSession session, Model model) throws Exception {
		
		// 로그인한 유저의 정보를 가지고 와서. 
		UserVO vo = service.login(dto);
		 
		if(vo == null) { // 유저의 정보가 없으면
			return; // 리턴
		}
		
		model.addAttribute("userVO", vo); // 있으면 모델에 넣어서. LoginInterceptor에 있는 postHandle 로 가겠지.
		
		if(dto.isUseCookie()) {
			int amount = 60 * 60 * 24 * 7;
			
			Date sessionLimit = new Date(System.currentTimeMillis()+(1000*amount)); // 현재시간에 일주일을 더해서 sessionLimit 기간을 정함.
			
			service.keepLogin(vo.getUid(), session.getId(), sessionLimit);
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		Object obj = session.getAttribute("login");
		 
		if(obj != null) { // 세션에 로그인 정보가 있으면.
			UserVO vo = (UserVO) obj; // 로그인 정보를 UserVO 객체에 저장
			
			session.removeAttribute("login"); // 세션에서 login 정보를 삭제
			session.invalidate(); // 세션 삭제.
			
			Cookie loginCookie = WebUtils.getCookie(request, "loginCookie"); // request 객체에서 로그인 쿠키를 꺼내옴.
			
			if(loginCookie != null) { // 로그인 쿠키가 있으면.
				loginCookie.setPath("/"); // 어떤 경로에서도 접근이 가능하게 하고.
				loginCookie.setMaxAge(0); // 로그인 쿠키를 없앤다.
				response.addCookie(loginCookie); // loginCookie를 저장.
				service.keepLogin(vo.getUid(), session.getId(), new Date());
				
			}
		}
		
		return "user/logout";
		
		
	}

	
}
