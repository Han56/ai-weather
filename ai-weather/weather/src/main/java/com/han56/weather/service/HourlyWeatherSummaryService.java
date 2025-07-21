package com.han56.weather.service;

import org.springframework.web.context.request.async.DeferredResult;

public interface HourlyWeatherSummaryService {
    void getHourlyWeatherSummary(DeferredResult<String> result, String cityId);
}
