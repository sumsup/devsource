package org.zerock.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class SampleAdvice {

  private static final Logger logger = LoggerFactory.getLogger(SampleAdvice.class);

  
  @Before("execution(* org.zerock.service.MessageService*.*(..))") // 서비스를 실행하기 전에
  public void startLog(JoinPoint jp) {

	// 아래와 같이 로그를 딱 찍어준다.
    logger.info("----------AOP 테스트 해본다------------------");
    logger.info("----------AOP 테스트 해본다------------------");
    logger.info("----------AOP 테스트 해본다------------------");
    logger.info("----------AOP 테스트 해본다------------------");
    logger.info(Arrays.toString(jp.getArgs())); // pick log that parameters of service method.

  }
  
  @Around("execution(* org.zerock.service.MessageService*.*(..))")
  public Object timeLog(ProceedingJoinPoint pjp) throws Throwable { // ProceedingJoinPoint : 각 객체의 메서드를 실행.
	  																// JoinPoint란 aop를 적용하는 메서드들.
	  long startTime = System.currentTimeMillis();
	  logger.info(Arrays.toString(pjp.getArgs())); // 전달되는 모든 파라미터들을 Object의 배열로 가져온다.
	  
	  Object result = pjp.proceed(); // 조인포인트를 실행해서 결과 값을 result에 저장.
	  
	  long endTime = System.currentTimeMillis();
	  
	  logger.info(pjp.getSignature().getName()+":"+(endTime - startTime)); 
	  // getSignature().getName() : 실행한 메서드 이름을 반환. 메세지를 추가하면 addMessage 를 리턴함.
	  
	  logger.info("===========================================");
	  logger.info("================= pjp의 result 값은 => " + pjp.toString());
	  
	  return result;
  }
  
  

}