package org.zerock.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Aspect
@Log4j
@Component
public class LogAdvice {
	
	/*@Before("execution(* org.zerock.service.SampleService*.*(..))")
	public void logBefore()
	{
		log.info("=========================================");
	}
	
	@Before("execution(* org.zerock.service.SampleService*.doAdd(String, String)) "
			+ "&& args(str1, str2)")
	public void logBeforeWithParam(String str1, String str2) 
	{
		
		log.info("str1: "+ str1);
		log.info("str2: "+ str2);
		
	}
	
	@AfterThrowing(pointcut = "execution(* org.zerock.service.SampleService*.*(..))",
			throwing="exception")
	public void logException(Exception exception) 
	{
		
		log.info("Exception.....!!!!");
		log.info("Exception: " + exception);
		
	}*/
	
	//@Around("execution(* org.zerock.service.*.*(..))")
	@Around("execution(* org.zerock.*.*.*(..))")
	public Object logTime(ProceedingJoinPoint pjp) {
		
		long start = System.currentTimeMillis();
		
		log.info("Target: " + pjp.getTarget());
		log.info("param: " + Arrays.toString(pjp.getArgs()));
		
		// Object는 모든 데이터 타입을 아우를 수 있는 최상위 데이터 타입.
		Object result = null;
		
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		log.info("TIME: " + (end - start));
		
		// Target이 리턴해주는 값을 다시 리턴해줌.
		return result;
	}
	
}





