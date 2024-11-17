package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class WeatherMojiAlertData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "alert")
    private List<WeatherAlert> weatherAlerts;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<WeatherAlert> getWeatherAlerts() {
        return weatherAlerts;
    }

    public void setWeatherAlerts(List<WeatherAlert> weatherAlerts) {
        this.weatherAlerts = weatherAlerts;
    }
}
