package com.han56.weather.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 用于标记需要进行限流控制的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    /**
     * 默认每日最大请求数
     */
    int limit() default 10;

    /**
     * 默认过期时间（小时）
     */
    long expireHours() default 24;

    /**
     * 限流类型：按天、按小时、按分钟
     */
    RateLimitType type() default RateLimitType.DAILY;

    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";

    /**
     * 限流类型枚举
     */
    enum RateLimitType {
        DAILY,    // 按天限流
        HOURLY,   // 按小时限流
        MINUTELY  // 按分钟限流
    }
}
