package org.example.distanceapplication.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.distanceapplication.service.CounterService;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {
  @Pointcut("within(org.example.distanceapplication.controller..*)"
      + " || within(org.example.distanceapplication.service..*)"
      + " || within(org.example.distanceapplication.cache..*)")
  public void allMethods() {
  }

  @Pointcut("within(org.example.distanceapplication.service..*)")
  public void serviceMethods() {
  }

  @Pointcut("@annotation(AspectAnnotation)")
  public void methodsWithAnnotation() {
  }
  @Before("serviceMethods()")
  public void loggingCounterAmount(final JoinPoint joinPoint) {
    int counterValue = CounterService.increment();
    log.info("Implements {}.{}."
            + " Current value of counter is {}", joinPoint
            .getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(), counterValue);
  }
  @Around(value = "methodsWithAnnotation()")
  public Object loggingMethods(final ProceedingJoinPoint point)
      throws Throwable {
    log.info("Enter method {}.{} with argument(s): {}",
        point.getSignature().getDeclaringTypeName(),
        point.getSignature().getName(),
        Arrays.toString(point.getArgs()));
    try {
      Object result = point.proceed();
      log.info("After execution method {}.{} with argument(s): {}",
          point.getSignature().getDeclaringTypeName(),
          point.getSignature().getName(),
          Arrays.toString(point.getArgs()));
      return result;
    } catch (Throwable e) {
      log.error("Illegal argument(s) {} in method {}.{}()",
          Arrays.toString(point.getArgs()),
          point.getSignature().getDeclaringTypeName(),
          point.getSignature().getName());
      throw e;
    }
  }

  @AfterThrowing(pointcut = "allMethods()", throwing = "exception")
  public void logsExceptionsFromAnyLocation(final JoinPoint joinPoint,
                                            final Throwable exception) {
    log.error("Exception in {}.{}() - {}",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(), exception.getMessage());
  }
}
