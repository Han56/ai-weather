package com.han56.weather.service;

import com.han56.weather.models.response.*;
import com.han56.weather.utils.ServiceResult;

/**
 * 天气预报接口
 * */
public interface WeatherForecastService {

    /**
     * 服务1：空气质量5天预测报告
     * @param cityId
     * */
    ServiceResult<AQIMojiResponse> aqiForeCast5Days(String cityId);

    /**
     * 服务2：天气实况服务
     * @param cityId
     * */
    ServiceResult<RealTimeMojiWeatherResponse> realTimeWeatherCondition(String cityId);

    /**
     * 服务3：天气预报15天
     * @param cityId
     * */
    ServiceResult<Forecast15DaysMojiResponse> forecast15DaysWeather(String cityId);

    /**
     * 服务4：24小时天气预报服务
     * @param cityId
     * */
    ServiceResult<ForecastHourlyMojiResponse> forecastHourlyWeather(String cityId);

    /**
     * 服务5：天气预警服务
     * @param cityId
     * */
    ServiceResult<WeatherAlertMojiResponse> weatherAlert(String cityId);

    /**
     * 服务6：生活指数服务
     * @param cityId
     * */
    ServiceResult<LiveMojiResponse> liveIndex(String cityId);

    /**
     * 服务7：空气质量指数服务
     * @param cityId
     * */
    ServiceResult<AQIRealTimeMojiResponse> aqiRealTime(String cityId);

    /**
     * 服务8：限行数据
     * @param cityId
     * */
    ServiceResult<LimitInfoMojiResponse> limitInfo(String cityId);


    /**
     * 服务9：AI穿衣推荐服务
     * 优化策略：
     * 1. 用户画像缓存 - 减少数据库查询
     * 2. 天气数据缓存 - 减少API调用
     * 3. AI推荐结果缓存 - 减少AI API调用
     * 4. 异步图片生成 - 提升响应速度
     * @param cityId 城市ID
     * @param openId 用户ID
     * */
    ServiceResult<AiClothingRecommendationsResponse> aiClothingRecommendations(String cityId,String openId);


}
