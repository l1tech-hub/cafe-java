package com.cafe.aop;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("execution(* com.cafe.service..*(..))")
  public void serviceMethods() {
  }

  // Время выполнения
  @Around("serviceMethods()")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    Object result = joinPoint.proceed(); // исключения НЕ ловим

    if (log.isDebugEnabled()) {
      long duration = System.currentTimeMillis() - start;
      log.debug("Метод {} выполнялся {} ms",
          joinPoint.getSignature().toShortString(),
          duration
      );
    }

    return result;
  }

  // Успешное выполнение
  @AfterReturning("serviceMethods()")
  public void logSuccess(JoinPoint joinPoint) {
    if (log.isDebugEnabled()) {
      log.debug("Метод {} с аргументами {} успешно выполнен",
          joinPoint.getSignature().toShortString(),
          Arrays.toString(joinPoint.getArgs())
      );
    }
  }

  // Ошибки
  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
  public void logError(JoinPoint joinPoint, Throwable ex) {
    if (log.isErrorEnabled()) {
      log.error("Ошибка в методе {} с аргументами {}",
          joinPoint.getSignature().toShortString(),
          Arrays.toString(joinPoint.getArgs()),
          ex
      );
    }
  }
}