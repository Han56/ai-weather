package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

public class LimitInfoData {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "limit")
    private List<LimitInfo> limitInfos;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<LimitInfo> getLimitInfos() {
        return limitInfos;
    }

    public void setLimitInfos(List<LimitInfo> limitInfos) {
        this.limitInfos = limitInfos;
    }
}
