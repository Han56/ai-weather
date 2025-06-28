package com.han56.weather.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 性能监控注解
 * 用于监控方法执行时间
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceMonitor {

    /**
     * 方法描述
     */
    String description() default "";

    /**
     * 是否记录参数
     */
    boolean logParams() default false;

    /**
     * 是否记录返回值
     */
    boolean logResult() default false;

    /**
     * 慢查询阈值（毫秒）
     */
    long slowThreshold() default 1000;
} 