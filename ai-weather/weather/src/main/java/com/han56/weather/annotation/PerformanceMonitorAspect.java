package com.han56.weather.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 性能监控切面
 */
@Aspect
@Component
public class PerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    @Around("@annotation(performanceMonitor)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint, PerformanceMonitor performanceMonitor) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String description = performanceMonitor.description();
        
        try {
            // 记录方法开始
            if (performanceMonitor.logParams()) {
                logger.info("方法开始执行 - {}: {}, 参数: {}", description, methodName, joinPoint.getArgs());
            } else {
                logger.info("方法开始执行 - {}: {}", description, methodName);
            }
            
            // 执行原方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录执行结果
            if (executionTime > performanceMonitor.slowThreshold()) {
                logger.warn("慢查询警告 - {}: {}, 执行时间: {}ms", description, methodName, executionTime);
            } else {
                logger.info("方法执行完成 - {}: {}, 执行时间: {}ms", description, methodName, executionTime);
            }
            
            // 记录返回值
            if (performanceMonitor.logResult()) {
                logger.debug("方法返回值 - {}: {}, 结果: {}", description, methodName, result);
            }
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("方法执行异常 - {}: {}, 执行时间: {}ms, 异常: {}", 
                description, methodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
} 