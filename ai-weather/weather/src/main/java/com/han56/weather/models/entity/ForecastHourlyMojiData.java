package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class ForecastHourlyMojiData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "hourly")
    private List<ForecastHourly> forecastHourlyList;


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<ForecastHourly> getForecastHourlyList() {
        return forecastHourlyList;
    }

    public void setForecastHourlyList(List<ForecastHourly> forecastHourlyList) {
        this.forecastHourlyList = forecastHourlyList;
    }
}
