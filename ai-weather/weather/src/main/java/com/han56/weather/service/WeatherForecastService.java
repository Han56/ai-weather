package com.han56.weather.service;

import com.han56.weather.models.response.AQIMojiResponse;
import com.han56.weather.utils.ServiceResult;

/**
 * 天气预报接口
 * */
public interface WeatherForecastService {

    /**
     * 服务1：空气质量5天预测报告
     * */
    ServiceResult<AQIMojiResponse> aqiForeCast5Days(String cityId);



}
