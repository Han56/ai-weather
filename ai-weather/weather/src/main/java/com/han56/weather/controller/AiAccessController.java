package com.han56.weather.controller;

import com.han56.weather.service.HourlyWeatherSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/ai")
public class AiAccessController {

    @Autowired
    private HourlyWeatherSummaryService hourlyWeatherSummaryService;

    /**
     * 24小时天气情况AI总结
     * @param cityId
     * */
    @GetMapping("/hourly-weather-summary")
    public DeferredResult<String> hourlyWeatherSummary(@RequestParam(name = "cityId") String cityId){
        DeferredResult<String> result = new DeferredResult<>();
        hourlyWeatherSummaryService.getHourlyWeatherSummary(result, cityId);
        return result;
    }
}
