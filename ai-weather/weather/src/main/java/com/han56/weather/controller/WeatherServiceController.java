package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.models.response.*;
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

    /**
     * 24小时天气预报服务
     * @param cityId
     * */
    @GetMapping("/forecast_hourly_weather")
    @ResultFormat
    public ServiceResult<ForecastHourlyMojiResponse> forecastHourlyMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.forecastHourlyWeather(cityId);
    }

    /**
     * 天气预警服务
     * @param cityId
     * */
    @GetMapping("/weather_alert")
    @ResultFormat
    public ServiceResult<WeatherAlertMojiResponse> weatherAlertMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.weatherAlert(cityId);
    }

    /**
     * 生活指数服务
     * */
    @GetMapping("/live_index")
    @ResultFormat
    public ServiceResult<LiveMojiResponse> liveMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.liveIndex(cityId);
    }

    /**
     * 空气指数服务
     * @param cityId
     * */
    @GetMapping("/aqi_real_time")
    @ResultFormat
    public ServiceResult<AQIRealTimeMojiResponse> aqiRealTimeMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.aqiRealTime(cityId);
    }


    /**
     * 限行服务
     * @param cityId
     * */
    @GetMapping("/limit_info")
    @ResultFormat
    public ServiceResult<LimitInfoMojiResponse> limitInfoMojiResponseServiceResult(@RequestParam String cityId){
        return weatherForecastService.limitInfo(cityId);
    }

}
