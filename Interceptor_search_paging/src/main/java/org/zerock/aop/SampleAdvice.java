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

  
  @Before("execution(* org.zerock.service.MessageService*.*(..))") // ���񽺸� �����ϱ� ����
  public void startLog(JoinPoint jp) {

	// �Ʒ��� ���� �α׸� �� ����ش�.
    logger.info("----------AOP �׽�Ʈ �غ���------------------");
    logger.info("----------AOP �׽�Ʈ �غ���------------------");
    logger.info("----------AOP �׽�Ʈ �غ���------------------");
    logger.info("----------AOP �׽�Ʈ �غ���------------------");
    logger.info(Arrays.toString(jp.getArgs())); // pick log that parameters of service method.

  }
  
  @Around("execution(* org.zerock.service.MessageService*.*(..))")
  public Object timeLog(ProceedingJoinPoint pjp) throws Throwable { // ProceedingJoinPoint : �� ��ü�� �޼��带 ����.
	  																// JoinPoint�� aop�� �����ϴ� �޼����.
	  long startTime = System.currentTimeMillis();
	  logger.info(Arrays.toString(pjp.getArgs())); // ���޵Ǵ� ��� �Ķ���͵��� Object�� �迭�� �����´�.
	  
	  Object result = pjp.proceed(); // ��������Ʈ�� �����ؼ� ��� ���� result�� ����.
	  
	  long endTime = System.currentTimeMillis();
	  
	  logger.info(pjp.getSignature().getName()+":"+(endTime - startTime)); 
	  // getSignature().getName() : ������ �޼��� �̸��� ��ȯ. �޼����� �߰��ϸ� addMessage �� ������.
	  
	  logger.info("===========================================");
	  logger.info("================= pjp�� result ���� => " + pjp.toString());
	  
	  return result;
  }
  
  

}