package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class RealTimeMojiWeatherData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "condition")
    private RealTimeWeatherCondition realTimeWeatherCondition;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public RealTimeWeatherCondition getRealTimeWeatherCondition() {
        return realTimeWeatherCondition;
    }

    public void setRealTimeWeatherCondition(RealTimeWeatherCondition realTimeWeatherCondition) {
        this.realTimeWeatherCondition = realTimeWeatherCondition;
    }
}
