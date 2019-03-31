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
	
	@Inject // @Inject�� @AutoWired���� ������? ����� ����. 
	// @Inject : �ڹٿ��� ����.
	// @AutoWired : ���������� ����.
	private UserService service;
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public void loginGET(@ModelAttribute("dto") LoginDTO dto, Model model) {
		
	}
	
	@RequestMapping(value="/loginPost", method = RequestMethod.POST)
	// ���ͼ��Ϳ��� ���ǿ� �̹� �α��ε� ������ ������ ����� �Դ�.
	public void loginPOST(LoginDTO dto, HttpSession session, Model model) throws Exception {
		
		// �α����� ������ ������ ������ �ͼ�. 
		UserVO vo = service.login(dto);
		 
		if(vo == null) { // ������ ������ ������
			return; // ����
		}
		
		model.addAttribute("userVO", vo); // ������ �𵨿� �־. LoginInterceptor�� �ִ� postHandle �� ������.
		
		if(dto.isUseCookie()) {
			int amount = 60 * 60 * 24 * 7;
			
			Date sessionLimit = new Date(System.currentTimeMillis()+(1000*amount)); // ����ð��� �������� ���ؼ� sessionLimit �Ⱓ�� ����.
			
			service.keepLogin(vo.getUid(), session.getId(), sessionLimit);
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		
		Object obj = session.getAttribute("login");
		 
		if(obj != null) { // ���ǿ� �α��� ������ ������.
			UserVO vo = (UserVO) obj; // �α��� ������ UserVO ��ü�� ����
			
			session.removeAttribute("login"); // ���ǿ��� login ������ ����
			session.invalidate(); // ���� ����.
			
			Cookie loginCookie = WebUtils.getCookie(request, "loginCookie"); // request ��ü���� �α��� ��Ű�� ������.
			
			if(loginCookie != null) { // �α��� ��Ű�� ������.
				loginCookie.setPath("/"); // � ��ο����� ������ �����ϰ� �ϰ�.
				loginCookie.setMaxAge(0); // �α��� ��Ű�� ���ش�.
				response.addCookie(loginCookie); // loginCookie�� ����.
				service.keepLogin(vo.getUid(), session.getId(), new Date());
				
			}
		}
		
		return "user/logout";
		
		
	}

	
}
