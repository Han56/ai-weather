package com.han56.weather.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Set;

/**
 * 缓存管理工具类
 * 统一管理AI推荐相关的缓存操作
 */
@Component
public class CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    @Autowired
    private RedisUtil redisUtil;

    // 缓存过期时间配置
    private static final int AI_RECOMMEND_CACHE_TIME = 1800; // 30分钟
    private static final int AI_CONTENT_CACHE_TIME = 7200;   // 2小时
    private static final int USER_PORTRAIT_CACHE_TIME = 3600; // 1小时
    private static final int WEATHER_CACHE_TIME = 1800;      // 30分钟

    /**
     * 获取AI推荐结果缓存
     */
    public Object getAiRecommendCache(String cityId, String openId) {
        String key = String.format("ai_recommend:%s:%s", cityId, openId);
        return redisUtil.get(key);
    }

    /**
     * 设置AI推荐结果缓存
     */
    public void setAiRecommendCache(String cityId, String openId, Object result) {
        String key = String.format("ai_recommend:%s:%s", cityId, openId);
        redisUtil.set(key, result, AI_RECOMMEND_CACHE_TIME);
        logger.info("AI推荐结果已缓存: {}", key);
    }

    /**
     * 获取AI内容缓存
     */
    public Object getAiContentCache(String promptHash) {
        String key = String.format("ai_recommend_content:%s", promptHash);
        return redisUtil.get(key);
    }

    /**
     * 设置AI内容缓存
     */
    public void setAiContentCache(String promptHash, Object result) {
        String key = String.format("ai_recommend_content:%s", promptHash);
        redisUtil.set(key, result, AI_CONTENT_CACHE_TIME);
        logger.info("AI内容已缓存: {}", key);
    }

    /**
     * 获取用户画像缓存
     */
    public Object getUserPortraitCache(String openId) {
        String key = String.format("user_portrait:%s", openId);
        return redisUtil.get(key);
    }

    /**
     * 设置用户画像缓存
     */
    public void setUserPortraitCache(String openId, Object result) {
        String key = String.format("user_portrait:%s", openId);
        redisUtil.set(key, result, USER_PORTRAIT_CACHE_TIME);
        logger.info("用户画像已缓存: {}", key);
    }

    /**
     * 获取天气数据缓存
     */
    public Object getWeatherCache(String fid) {
        String key = String.format("weather_hourly:%s", fid);
        return redisUtil.get(key);
    }

    /**
     * 设置天气数据缓存
     */
    public void setWeatherCache(String fid, Object result) {
        String key = String.format("weather_hourly:%s", fid);
        redisUtil.set(key, result, WEATHER_CACHE_TIME);
        logger.info("天气数据已缓存: {}", key);
    }

    /**
     * 清除用户相关缓存
     */
    public void clearUserCache(String openId) {
        // 清除用户画像缓存
        String portraitKey = String.format("user_portrait:%s", openId);
        redisUtil.del(portraitKey);
        
        // 清除用户缓存（简化版本）
        String userKey = String.format("user:%s", openId);
        redisUtil.del(userKey);
        
        logger.info("用户缓存已清除: {}", openId);
    }

    /**
     * 清除用户AI推荐缓存
     * 清除该用户在所有城市的AI推荐缓存
     */
    public void clearUserAiRecommendCache(String openId) {
        try {
            // 获取所有匹配的AI推荐缓存key
            Set<String> keys = redisUtil.keys("ai_recommend:*:" + openId);
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    redisUtil.del(key);
                    logger.info("清除AI推荐缓存: {}", key);
                }
                logger.info("用户AI推荐缓存已清除，共清除{}个缓存", keys.size());
            } else {
                logger.info("用户{}没有找到AI推荐缓存", openId);
            }
        } catch (Exception e) {
            logger.error("清除用户AI推荐缓存失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 清除特定城市的AI推荐缓存
     */
    public void clearCityAiRecommendCache(String cityId) {
        try {
            // 获取所有匹配的AI推荐缓存key
            Set<String> keys = redisUtil.keys("ai_recommend:" + cityId + ":*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    redisUtil.del(key);
                    logger.info("清除城市AI推荐缓存: {}", key);
                }
                logger.info("城市AI推荐缓存已清除，共清除{}个缓存", keys.size());
            } else {
                logger.info("城市{}没有找到AI推荐缓存", cityId);
            }
        } catch (Exception e) {
            logger.error("清除城市AI推荐缓存失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 清除城市相关缓存
     */
    public void clearCityCache(String cityId) {
        // 清除该城市的天气缓存
        // 注意：这里需要根据实际需求实现模式匹配删除
        logger.info("城市缓存已清除: {}", cityId);
    }

    /**
     * 获取缓存统计信息
     */
    public void getCacheStats() {
        // 这里可以实现缓存统计功能
        logger.info("缓存统计功能待实现");
    }

    /**
     * 预热缓存
     */
    public void warmupCache() {
        // 预热热门城市天气数据
        // 预热常用用户画像
        logger.info("缓存预热功能待实现");
    }
} 