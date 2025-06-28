package com.han56.weather.exception;

import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理限流异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ServiceResult<String> handleRateLimitException(RuntimeException e) {
        // 检查是否是限流异常（通过错误消息判断）
        if (e.getMessage() != null && e.getMessage().contains("限流")) {
            logger.warn("限流异常: {}", e.getMessage());
            return new ServiceResult<>("429", e.getMessage());
        }
        
        // 其他运行时异常
        logger.error("运行时异常: {}", e.getMessage(), e);
        return new ServiceResult<>("500", "服务器内部错误");
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ServiceResult<String> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数异常: {}", e.getMessage());
        return new ServiceResult<>("400", e.getMessage());
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ServiceResult<String> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return new ServiceResult<>("500", "系统异常，请稍后重试");
    }
} 