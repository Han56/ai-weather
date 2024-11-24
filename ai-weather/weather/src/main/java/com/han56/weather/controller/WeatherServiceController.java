package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.models.response.AQIMojiResponse;
import com.han56.weather.models.response.Forecast15DaysMojiResponse;
import com.han56.weather.models.response.RealTimeMojiWeatherResponse;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherServiceController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;

    /**
     * AQI预报5天服务
     * @param cityId
     * */
    @GetMapping("/aqi")
    @ResultFormat
    public ServiceResult<AQIMojiResponse> aqi(@RequestParam String cityId){
        return weatherForecastService.aqiForeCast5Days(cityId);
    }

    /**
     * 天气实况服务
     * @param cityId
     * */
    @GetMapping("/real_time_weather")
    @ResultFormat
    public ServiceResult<RealTimeMojiWeatherResponse> realTimeMojiWeatherResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.realTimeWeatherCondition(cityId);
    }

    /**
     * 预测15天天气服务
     * @param cityId
     * */
    @GetMapping("/forecast_15days_weather")
    @ResultFormat
    public ServiceResult<Forecast15DaysMojiResponse> forecast15DaysMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.forecast15DaysWeather(cityId);
    }



}
