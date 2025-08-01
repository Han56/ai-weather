package com.han56.weather.controller;

import com.han56.weather.annotation.RateLimited;
import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.models.response.*;
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

@RestController
@RequestMapping("/weather")
public class WeatherServiceController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * AQI预报5天服务
     * @param cityId
     * */
    @GetMapping("/aqi")
    @ResultFormat
    public ServiceResult<AQIMojiResponse> aqi(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.aqiForeCast5Days(cityId);
    }

    /**
     * 天气实况服务
     * @param cityId
     * */
    @GetMapping("/real_time_weather")
    @ResultFormat
    public ServiceResult<RealTimeMojiWeatherResponse> realTimeMojiWeatherResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.realTimeWeatherCondition(cityId);
    }

    /**
     * 预测15天天气服务
     * @param cityId
     * */
    @GetMapping("/forecast_15days_weather")
    @ResultFormat
    public ServiceResult<Forecast15DaysMojiResponse> forecast15DaysMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.forecast15DaysWeather(cityId);
    }

    /**
     * 24小时天气预报服务
     * @param cityId
     * */
    @GetMapping("/forecast_hourly_weather")
    @ResultFormat
    public ServiceResult<ForecastHourlyMojiResponse> forecastHourlyMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.forecastHourlyWeather(cityId);
    }

    /**
     * 天气预警服务
     * @param cityId
     * */
    @GetMapping("/weather_alert")
    @ResultFormat
    public ServiceResult<WeatherAlertMojiResponse> weatherAlertMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.weatherAlert(cityId);
    }

    /**
     * 生活指数服务
     * */
    @GetMapping("/live_index")
    @ResultFormat
    public ServiceResult<LiveMojiResponse> liveMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.liveIndex(cityId);
    }

    /**
     * 空气指数服务
     * @param cityId
     * */
    @GetMapping("/aqi_real_time")
    @ResultFormat
    public ServiceResult<AQIRealTimeMojiResponse> aqiRealTimeMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.aqiRealTime(cityId);
    }


    /**
     * 限行服务
     * @param cityId
     * */
    @GetMapping("/limit_info")
    @ResultFormat
    public ServiceResult<LimitInfoMojiResponse> limitInfoMojiResponseServiceResult(@RequestParam(name = "cityId") String cityId){
        return weatherForecastService.limitInfo(cityId);
    }

    /**
     * AI推荐
     *
     * @param cityId,openId
     */
    @GetMapping("/ai_recommends")
    @ResultFormat
    @RateLimited(limit = 1000, type = RateLimited.RateLimitType.DAILY, message = "AI推荐服务每日限流10次，请明天再试")
    public ServiceResult<AiClothingRecommendationsResponse> aiClothingRecommendationsServiceResult(@RequestParam(name = "cityId") String cityId,
                                                                                                   @RequestParam(name = "openId") String openId){
        return weatherForecastService.aiClothingRecommendations(cityId, openId);
    }

    /**
     * 查询AI推荐图片状态
     *
     * @param cityId,openId
     */
    @GetMapping("/ai_recommends_status")
    @ResultFormat
    public ServiceResult<Map<String, Object>> aiRecommendStatus(@RequestParam(name = "cityId") String cityId,
                                                                @RequestParam(name = "openId") String openId){
        String cacheKey = String.format("ai_recommend:%s:%s", cityId, openId);
        Object cachedResult = redisUtil.get(cacheKey);
        
        Map<String, Object> status = new HashMap<>();
        if (cachedResult != null) {
            try {
                if (cachedResult instanceof AiClothingRecommendationsResponse) {
                    AiClothingRecommendationsResponse response = (AiClothingRecommendationsResponse) cachedResult;
                    status.put("hasRecommendation", true);
                    status.put("hasImage", response.getImgUrl() != null && !response.getImgUrl().isEmpty());
                    status.put("recommendation", response);
                } else {
                    logger.warn("AI推荐状态查询缓存数据类型不匹配，期望AiClothingRecommendationsResponse，实际: {}, 清除缓存: {}", 
                        cachedResult.getClass().getSimpleName(), cacheKey);
                    redisUtil.del(cacheKey);
                    status.put("hasRecommendation", false);
                    status.put("hasImage", false);
                    status.put("message", "缓存数据格式错误，已清除");
                }
            } catch (Exception e) {
                logger.error("AI推荐状态查询缓存数据转换失败，清除缓存: {}", cacheKey, e);
                redisUtil.del(cacheKey);
                status.put("hasRecommendation", false);
                status.put("hasImage", false);
                status.put("message", "缓存数据转换失败，已清除");
            }
        } else {
            status.put("hasRecommendation", false);
            status.put("hasImage", false);
        }
        
        return ServiceResult.success(status);
    }

}
