package com.han56.weather.controller;

import com.han56.weather.annotation.RateLimited;
import com.han56.weather.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 限流测试控制器
 */
@RestController
@RequestMapping("/rateLimit")
@Api(tags = "限流测试接口")
public class RateLimitTestController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/test")
    @ApiOperation("测试限流功能")
    @RateLimited(limit = 5, type = RateLimited.RateLimitType.MINUTELY, message = "测试接口每分钟限流5次")
    public Map<String, Object> testRateLimit(@RequestParam String openId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "限流测试成功");
        result.put("openId", openId);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    @GetMapping("/info")
    @ApiOperation("获取限流信息")
    public Map<String, Object> getRateLimitInfo(@RequestParam String openId, @RequestParam(defaultValue = "DAILY") String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建key
            String key = buildRateLimitKey(openId, type);
            
            // 获取当前次数
            Object currentCount = redisUtil.get(key);
            int count = currentCount == null ? 0 : Integer.parseInt(currentCount.toString());
            
            // 获取过期时间
            long expireTime = redisUtil.getExpire(key);
            
            result.put("success", true);
            result.put("openId", openId);
            result.put("type", type);
            result.put("currentCount", count);
            result.put("expireTime", expireTime);
            result.put("key", key);
            
            // 根据类型设置限制
            int limit = getLimitByType(type);
            result.put("limit", limit);
            result.put("remaining", Math.max(0, limit - count));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取限流信息失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/reset")
    @ApiOperation("重置限流计数")
    public Map<String, Object> resetRateLimit(@RequestParam String openId, @RequestParam(defaultValue = "DAILY") String type) {
        Map<String, Object> result = new HashMap<>();
        try {
            String key = buildRateLimitKey(openId, type);
            redisUtil.del(key);
            
            result.put("success", true);
            result.put("message", "限流计数已重置");
            result.put("openId", openId);
            result.put("type", type);
            result.put("key", key);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置限流计数失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 构建限流key
     */
    private String buildRateLimitKey(String openId, String type) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String timeSuffix;
        
        switch (type.toUpperCase()) {
            case "DAILY":
                timeSuffix = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                break;
            case "HOURLY":
                timeSuffix = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHH"));
                break;
            case "MINUTELY":
                timeSuffix = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                break;
            default:
                timeSuffix = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        
        return String.format("rate_limit:test:%s:%s", openId, timeSuffix);
    }

    /**
     * 根据类型获取限制
     */
    private int getLimitByType(String type) {
        switch (type.toUpperCase()) {
            case "DAILY":
                return 20; // AI推荐接口的每日限制
            case "HOURLY":
                return 5;
            case "MINUTELY":
                return 5;
            default:
                return 20;
        }
    }
} 