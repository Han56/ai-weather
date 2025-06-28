package com.han56.weather.controller;

import com.han56.weather.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Redis测试控制器
 */
@RestController
@RequestMapping("/redis")
@Api(tags = "Redis测试接口")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/set")
    @ApiOperation("设置缓存")
    public Map<String, Object> set(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = redisUtil.set(key, value);
            result.put("success", success);
            result.put("message", success ? "设置成功" : "设置失败");
            result.put("key", key);
            result.put("value", value);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/get")
    @ApiOperation("获取缓存")
    public Map<String, Object> get(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 首先尝试获取字符串值
            String stringValue = redisUtil.getString(key);
            if (stringValue != null) {
                result.put("success", true);
                result.put("key", key);
                result.put("value", stringValue);
                result.put("exists", true);
                result.put("type", "string");
                return result;
            }
            
            // 如果字符串获取失败，尝试获取对象
            Object value = redisUtil.get(key);
            result.put("success", true);
            result.put("key", key);
            result.put("value", value);
            result.put("exists", value != null);
            result.put("type", value != null ? value.getClass().getSimpleName() : "null");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        return result;
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除缓存")
    public Map<String, Object> delete(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            redisUtil.del(key);
            result.put("success", true);
            result.put("message", "删除成功");
            result.put("key", key);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/setWithExpire")
    @ApiOperation("设置缓存并指定过期时间")
    public Map<String, Object> setWithExpire(@RequestParam String key, 
                                           @RequestParam String value, 
                                           @RequestParam long expireSeconds) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = redisUtil.set(key, value, expireSeconds);
            result.put("success", success);
            result.put("message", success ? "设置成功" : "设置失败");
            result.put("key", key);
            result.put("value", value);
            result.put("expireSeconds", expireSeconds);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/hasKey")
    @ApiOperation("检查key是否存在")
    public Map<String, Object> hasKey(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = redisUtil.hasKey(key);
            result.put("success", true);
            result.put("key", key);
            result.put("exists", exists);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/testConnection")
    @ApiOperation("测试Redis连接")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 测试连接
            String testKey = "connection_test_" + System.currentTimeMillis();
            String testValue = "test_value";
            
            // 设置测试值
            boolean setSuccess = redisUtil.set(testKey, testValue, 10); // 10秒过期
            if (!setSuccess) {
                result.put("success", false);
                result.put("message", "Redis连接测试失败：无法设置值");
                return result;
            }
            
            // 获取测试值
            String retrievedValue = redisUtil.getString(testKey);
            if (!testValue.equals(retrievedValue)) {
                result.put("success", false);
                result.put("message", "Redis连接测试失败：获取的值不匹配");
                result.put("expected", testValue);
                result.put("actual", retrievedValue);
                return result;
            }
            
            // 删除测试值
            redisUtil.del(testKey);
            
            result.put("success", true);
            result.put("message", "Redis连接测试成功");
            result.put("timestamp", System.currentTimeMillis());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis连接测试失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
        }
        return result;
    }
} 