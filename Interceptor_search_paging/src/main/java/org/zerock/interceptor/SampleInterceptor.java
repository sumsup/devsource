package org.zerock.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SampleInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		System.out.println("post handle.................");
		
		Object result = modelAndView.getModel().get("result"); // modelAndView : 뷰와 모델을 같이 사용할 필요가 있을 때 사용.
		
		if(result != null) {
			request.getSession().setAttribute("result", result); //  리퀘스트에서 세션을 가져와서 result를 저장함.
			response.sendRedirect("/doA"); // doA로 넘어간다.
		}
		
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// handler : 방금 호출한 빈 클래스.
		// methodObj : 방금 호출한 메서드명.
		
		System.out.println("pre handle..................");
		
		HandlerMethod method = (HandlerMethod) handler;
		Method methodObj = method.getMethod();
		
		System.out.println("Bean: "+ method.getBean());
		System.out.println("Method: "+ methodObj);
		
		return true;
	}
	
}
