package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.models.response.AQIMojiResponse;
import com.han56.weather.service.WeatherForecastService;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private WeatherForecastService weatherForecastService;

    @GetMapping("/test")
    @ResultFormat
    public ServiceResult<String> test(){
        return new ServiceResult<>("返回测试数据");
    }

    @GetMapping("/aqi")
    @ResultFormat
    public ServiceResult<AQIMojiResponse> aqi(@RequestParam String cityId){
        return weatherForecastService.aqiForeCast5Days(cityId);
    }

}
