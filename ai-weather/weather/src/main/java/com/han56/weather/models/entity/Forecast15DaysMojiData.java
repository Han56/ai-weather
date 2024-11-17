package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class Forecast15DaysMojiData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "forecast")
    private List<Forecast15Days> forecast15DaysList;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Forecast15Days> getForecast15DaysList() {
        return forecast15DaysList;
    }

    public void setForecast15DaysList(List<Forecast15Days> forecast15DaysList) {
        this.forecast15DaysList = forecast15DaysList;
    }
}
