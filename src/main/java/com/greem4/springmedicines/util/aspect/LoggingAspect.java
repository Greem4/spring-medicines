package com.greem4.springmedicines.util.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * com.greem4.springmedicines.http.controller..*(..)) || execution(public * com.greem4.springmedicines.service..*(..))")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = (MethodSignature) joinPoint.getSignature();
        var className = signature.getDeclaringTypeName();
        var methodName = signature.getName();
        var parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StringBuilder argumentsInfo = new StringBuilder();
        IntStream.range(0, args.length).forEach(i -> argumentsInfo.append(parameterNames[i])
                .append("=")
                .append(args[i])
                .append(i < args.length - 1 ? ", " : ""));

        String threadName = Thread.currentThread().getName();
        log.debug("[THREAD: {}] Starting method call: {}.{}({})",
                threadName, className, methodName, argumentsInfo);

        Instant start = Instant.now();

        try {
            Object result = joinPoint.proceed();
            Instant end = Instant.now();
            long elapsedSeconds = Duration.between(start, end).toSeconds();
            log.debug("[THREAD: {}] Completed method call: {}.{} in {} seconds. Result: {}",
                    threadName, className, methodName, elapsedSeconds, result);

            return result;
        } catch (Throwable ex) {
            Instant end = Instant.now();
            long elapsedSeconds = Duration.between(start, end).toSeconds();

            log.error("[THREAD: {}] Exception in method: {}.{} after {} seconds. Exception: {}",
                    threadName, className, methodName, elapsedSeconds, ex.toString());

            throw ex;
        }
    }
}
