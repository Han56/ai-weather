package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class AQIRealTimeMojiData {

    @JSONField(name = "aqi")
    private AQIRealTime aqiRealTime;

    @JSONField(name = "city")
    private City city;

    public AQIRealTime getAqiRealTime() {
        return aqiRealTime;
    }

    public void setAqiRealTime(AQIRealTime aqiRealTime) {
        this.aqiRealTime = aqiRealTime;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
