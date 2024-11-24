package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;
import java.util.Map;

/**
 * 生活指数信息
 * */
public class LiveIndex {

    @JSONField(name = "city")
    private City city;

    @JSONField(name = "liveIndex")
    private Map<String,List<LiveIndexEntity>> listMap;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Map<String, List<LiveIndexEntity>> getListMap() {
        return listMap;
    }

    public void setListMap(Map<String, List<LiveIndexEntity>> listMap) {
        this.listMap = listMap;
    }
}
