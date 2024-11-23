package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class AQIMojiData {

    //空气质量预报信息列表-对应JSON字段 apiForecast
    @JSONField(name = "apiForecast")
    private List<ApiForecast> apiForecast;

    //城市相关信息
    @JSONField(name = "city")
    private City city;

    public List<ApiForecast> getApiForecast() {
        return apiForecast;
    }

    public void setApiForecast(List<ApiForecast> apiForecast) {
        this.apiForecast = apiForecast;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
