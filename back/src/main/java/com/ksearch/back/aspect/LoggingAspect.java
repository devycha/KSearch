package com.ksearch.back.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Pointcut("@annotation(Loggable)")
    public void loggableMethods() {}

    @Around("loggableMethods()")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        String arguments = Arrays.toString(joinPoint.getArgs());
        String returnValue = String.valueOf(result);

        LOGGER.info("{}.{}({}) = {} ({}ms)", className, methodName, arguments, returnValue, executionTime);

        // 카프카에 로그 전송
        String logMessage = String.format("%s.%s(%s) = %s (%dms)", className, methodName, arguments, returnValue, executionTime);
        kafkaTemplate.send("logging_topic", logMessage);

        return result;
    }
}





