package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class RealTimeMojiWeatherData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "condition")
    private RealTimeWeatherCondition realTimeWeatherCondition;



}
