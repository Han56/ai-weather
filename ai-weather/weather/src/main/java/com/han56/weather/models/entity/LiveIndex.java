package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Map;

/**
 * 生活指数信息
 * */
public class LiveIndex {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "liveIndex")
    private Map<String,LiveIndexEntity> liveIndexEntityMap;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Map<String, LiveIndexEntity> getLiveIndexEntityMap() {
        return liveIndexEntityMap;
    }

    public void setLiveIndexEntityMap(Map<String, LiveIndexEntity> liveIndexEntityMap) {
        this.liveIndexEntityMap = liveIndexEntityMap;
    }
}
