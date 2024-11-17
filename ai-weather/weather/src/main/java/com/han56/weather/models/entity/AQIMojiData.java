package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class AQIMojiData {

    //空气质量预报信息列表-对应JSON字段 apiForecast
    @JSONField(name = "apiForecast")
    private List<ApiForecast> apiForecasts;

    //城市相关信息
    @JSONField(name = "city")
    private City city;

    public List<ApiForecast> getApiForecasts() {
        return apiForecasts;
    }

    public void setApiForecasts(List<ApiForecast> apiForecasts) {
        this.apiForecasts = apiForecasts;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
