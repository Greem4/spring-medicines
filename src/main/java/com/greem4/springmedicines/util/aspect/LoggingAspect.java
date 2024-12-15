package com.greem4.springmedicines.util.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.greem4.springmedicines.http.controller..*(..)) || execution(public * com.greem4.springmedicines.service..*(..))")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = (MethodSignature) joinPoint.getSignature();
        var className = signature.getDeclaringTypeName();
        var methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("Starting method call: {}.{} with arguments: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            long elapsedTime = System.currentTimeMillis() - startTime;

            log.info("Completed method call: {}.{} in {} ms with result: {}", className, methodName, elapsedTime, result);
            return result;
        } catch (Throwable ex) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            log.error("Exception in method: {}.{} after {} ms. Exception: {}", className, methodName, elapsedTime, ex.toString());
            throw ex;
        }
    }
}