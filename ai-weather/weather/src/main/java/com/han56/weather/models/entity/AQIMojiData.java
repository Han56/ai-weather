package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;


public class AQIMojiData {

    //空气质量预报信息列表-对应JSON字段 apiForecast
    @JSONField(name = "aqiForecast")
    private List<AqiForecast> aqiForecast;

    //城市相关信息
    @JSONField(name = "city")
    private City city;

    public List<AqiForecast> getAqiForecast() {
        return aqiForecast;
    }

    public void setAqiForecast(List<AqiForecast> aqiForecast) {
        this.aqiForecast = aqiForecast;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
