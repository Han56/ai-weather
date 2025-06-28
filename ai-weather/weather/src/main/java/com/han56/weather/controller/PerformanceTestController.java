package com.han56.weather.controller;

import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.RedisUtil;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 性能测试控制器
 */
@RestController
@RequestMapping("/test")
public class PerformanceTestController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 性能测试接口
     */
    @GetMapping("/performance")
    public Map<String, Object> performanceTest(@RequestParam String cityId, @RequestParam String openId) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            ServiceResult<?> response = weatherForecastService.aiClothingRecommendations(cityId, openId);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            result.put("success", true);
            result.put("duration", duration + "ms");
            result.put("response", response);
            
            logger.info("性能测试完成 - 耗时: {}ms, cityId: {}, openId: {}", duration, cityId, openId);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            result.put("success", false);
            result.put("duration", duration + "ms");
            result.put("error", e.getMessage());
            
            logger.error("性能测试失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
        }
        
        return result;
    }

    /**
     * 缓存测试接口
     */
    @GetMapping("/cache")
    public Map<String, Object> cacheTest(@RequestParam String cityId, @RequestParam String openId) {
        Map<String, Object> result = new HashMap<>();
        
        // 第一次请求
        long firstStart = System.currentTimeMillis();
        ServiceResult<?> firstResponse = weatherForecastService.aiClothingRecommendations(cityId, openId);
        long firstEnd = System.currentTimeMillis();
        long firstDuration = firstEnd - firstStart;
        
        // 第二次请求（应该命中缓存）
        long secondStart = System.currentTimeMillis();
        ServiceResult<?> secondResponse = weatherForecastService.aiClothingRecommendations(cityId, openId);
        long secondEnd = System.currentTimeMillis();
        long secondDuration = secondEnd - secondStart;
        
        result.put("first_request", firstDuration + "ms");
        result.put("second_request", secondDuration + "ms");
        result.put("improvement", String.format("%.2f%%", (double)(firstDuration - secondDuration) / firstDuration * 100));
        result.put("first_response", firstResponse);
        result.put("second_response", secondResponse);
        
        logger.info("缓存测试完成 - 首次: {}ms, 缓存: {}ms, 提升: {}%", 
            firstDuration, secondDuration, (double)(firstDuration - secondDuration) / firstDuration * 100);
        
        return result;
    }

    /**
     * 图片生成状态测试
     */
    @GetMapping("/image_status")
    public Map<String, Object> imageStatusTest(@RequestParam String cityId, @RequestParam String openId) {
        Map<String, Object> result = new HashMap<>();
        
        String cacheKey = String.format("ai_recommend:%s:%s", cityId, openId);
        Object cachedResult = redisUtil.get(cacheKey);
        
        if (cachedResult != null) {
            try {
                // 使用反射获取imgUrl字段，避免类型转换问题
                java.lang.reflect.Field imgUrlField = cachedResult.getClass().getDeclaredField("imgUrl");
                imgUrlField.setAccessible(true);
                String imgUrl = (String) imgUrlField.get(cachedResult);
                
                result.put("hasRecommendation", true);
                result.put("hasImage", imgUrl != null && !imgUrl.isEmpty());
                result.put("imageUrl", imgUrl);
                result.put("recommendation", cachedResult);
                
                logger.info("图片状态检查 - 有推荐: {}, 有图片: {}, 图片URL: {}", 
                    true, imgUrl != null && !imgUrl.isEmpty(), imgUrl);
                
            } catch (Exception e) {
                result.put("hasRecommendation", true);
                result.put("hasImage", false);
                result.put("error", "获取图片状态失败: " + e.getMessage());
                logger.error("获取图片状态失败", e);
            }
        } else {
            result.put("hasRecommendation", false);
            result.put("hasImage", false);
            result.put("message", "未找到推荐数据");
        }
        
        return result;
    }

    /**
     * JSON格式测试接口
     */
    @GetMapping("/json_test")
    public Map<String, Object> jsonTest(@RequestParam String cityId, @RequestParam String openId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            ServiceResult<?> response = weatherForecastService.aiClothingRecommendations(cityId, openId);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            result.put("success", true);
            result.put("duration", duration + "ms");
            result.put("response", response);
            
            // 检查响应结构
            if (response != null && response.getResult() != null) {
                Object data = response.getResult();
                try {
                    java.lang.reflect.Field clothingInfoField = data.getClass().getDeclaredField("clothingInfo");
                    clothingInfoField.setAccessible(true);
                    Object clothingInfo = clothingInfoField.get(data);
                    
                    java.lang.reflect.Field detailedField = data.getClass().getDeclaredField("detailedRecommendation");
                    detailedField.setAccessible(true);
                    Object detailed = detailedField.get(data);
                    
                    result.put("hasClothingInfo", clothingInfo != null);
                    result.put("hasDetailedRecommendation", detailed != null);
                    
                    if (clothingInfo != null) {
                        java.lang.reflect.Field topField = clothingInfo.getClass().getDeclaredField("top");
                        topField.setAccessible(true);
                        String top = (String) topField.get(clothingInfo);
                        result.put("top", top);
                    }
                    
                    if (detailed != null) {
                        java.lang.reflect.Field contentField = detailed.getClass().getDeclaredField("content");
                        contentField.setAccessible(true);
                        String content = (String) contentField.get(detailed);
                        result.put("content", content);
                    }
                    
                } catch (Exception e) {
                    result.put("structureError", e.getMessage());
                }
            }
            
            logger.info("JSON格式测试完成 - 耗时: {}ms, 成功: {}", duration, true);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - System.currentTimeMillis();
            
            result.put("success", false);
            result.put("duration", duration + "ms");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            
            logger.error("JSON格式测试失败 - 耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
        }
        
        return result;
    }
} 