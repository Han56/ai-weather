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


}
