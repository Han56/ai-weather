package com.han56.weather.annotation;

import com.han56.weather.utils.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 限流切面
 * 基于Redis实现分布式限流
 */
@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    private RedisUtil redisUtil;

    @Around("@annotation(rateLimited)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 获取openId参数
        String openId = request.getParameter("openId");
        if (openId == null || openId.isEmpty()) {
            openId = request.getParameter("open_id");
        }
        
        if (openId == null || openId.isEmpty()) {
            throw new IllegalArgumentException("缺少openId参数");
        }

        // 构建限流key
        String key = buildRateLimitKey(openId, rateLimited);
        
        // 检查限流
        if (!checkRateLimit(key, rateLimited)) {
            logger.warn("用户 {} 触发限流，接口: {}", openId, request.getRequestURI());
            throw new RuntimeException(rateLimited.message());
        }

        // 执行原方法
        try {
            Object result = joinPoint.proceed();
            logger.info("用户 {} 成功访问接口: {}", openId, request.getRequestURI());
            return result;
        } catch (Exception e) {
            logger.error("用户 {} 访问接口 {} 时发生异常: {}", openId, request.getRequestURI(), e.getMessage());
            throw e;
        }
    }

    /**
     * 构建限流key
     */
    private String buildRateLimitKey(String openId, RateLimited rateLimited) {
        LocalDateTime now = LocalDateTime.now();
        String timeSuffix;
        
        switch (rateLimited.type()) {
            case DAILY:
                timeSuffix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
            case HOURLY:
                timeSuffix = now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
                break;
            case MINUTELY:
                timeSuffix = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            default:
                timeSuffix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        
        return String.format("rate_limit:ai_recommends:%s:%s", openId, timeSuffix);
    }

    /**
     * 检查限流
     */
    private boolean checkRateLimit(String key, RateLimited rateLimited) {
        try {
            // 获取当前请求次数
            Object currentCount = redisUtil.get(key);
            int count = currentCount == null ? 0 : Integer.parseInt(currentCount.toString());
            
            // 检查是否超过限制
            if (count >= rateLimited.limit()) {
                logger.warn("限流触发 - Key: {}, 当前次数: {}, 限制: {}", key, count, rateLimited.limit());
                return false;
            }
            
            // 增加请求次数
            count++;
            long expireSeconds = getExpireSeconds(rateLimited);
            redisUtil.set(key, count, expireSeconds);
            
            logger.debug("限流检查通过 - Key: {}, 当前次数: {}, 限制: {}", key, count, rateLimited.limit());
            return true;
            
        } catch (Exception e) {
            logger.error("限流检查异常: {}", e.getMessage(), e);
            // 发生异常时，为了安全起见，允许请求通过
            return true;
        }
    }

    /**
     * 获取过期时间（秒）
     */
    private long getExpireSeconds(RateLimited rateLimited) {
        switch (rateLimited.type()) {
            case DAILY:
                return 24 * 60 * 60; // 24小时
            case HOURLY:
                return 60 * 60; // 1小时
            case MINUTELY:
                return 60; // 1分钟
            default:
                return rateLimited.expireHours() * 60 * 60;
        }
    }
}
