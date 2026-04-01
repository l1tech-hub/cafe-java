package com.cafe.aop;

import com.cafe.exception.ServiceExecutionException;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Around("execution(* com.cafe.service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
    long start = System.currentTimeMillis();

    try {
      Object result = joinPoint.proceed();

      if (log.isDebugEnabled()) {
        long duration = System.currentTimeMillis() - start;
        log.debug("Метод {} с аргументами {} выполнялся {} ms",
            joinPoint.getSignature(),
            Arrays.toString(joinPoint.getArgs()),
            duration);
      }

      return result;

    } catch (Throwable ex) {
      String msg = String.format("Ошибка в методе %s с аргументами %s",
          joinPoint.getSignature(),
          Arrays.toString(joinPoint.getArgs()));

      throw new ServiceExecutionException(msg, ex);
    }
  }
}
