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

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void controllerMethods() {
  }

  @Around("serviceMethods()")
  public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long duration = System.currentTimeMillis() - start;
    if (log.isDebugEnabled()) {
      log.debug("Сервисный метод {} выполнен за {} мс", joinPoint.getSignature().toShortString(),
          duration);
    }
    return result;
  }

  @Around("controllerMethods()")
  public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long duration = System.currentTimeMillis() - start;

    String str1 = joinPoint.getSignature().toShortString();
    String str2 = Arrays.toString(joinPoint.getArgs());

    log.info("Контроллерный метод {} выполнен за {} мс, аргументы: {}",
        str1, duration, str2);
    return result;
  }

  @AfterReturning(pointcut = "serviceMethods()", returning = "result")
  public void logServiceSuccess(JoinPoint joinPoint, Object result) {
    if (log.isDebugEnabled()) {
      log.debug("Сервисный метод {} с аргументами {} выполнен успешно",
          joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }
  }

  @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
  public void logServiceError(JoinPoint joinPoint, Throwable ex) {

    String str1 = joinPoint.getSignature().toShortString();
    String str2 = Arrays.toString(joinPoint.getArgs());

    log.error("Ошибка в сервисном методе {} с аргументами {}: {}",
        str1, str2,
        ex.getMessage());

  }


  @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
  public void logControllerError(JoinPoint joinPoint, Throwable ex) {

    String str1 = joinPoint.getSignature().toShortString();
    String str2 = Arrays.toString(joinPoint.getArgs());

    log.error("Ошибка в методе контроллера {} с аргументами {}: {}",
        str1, str2,
        ex.getMessage());
  }
}