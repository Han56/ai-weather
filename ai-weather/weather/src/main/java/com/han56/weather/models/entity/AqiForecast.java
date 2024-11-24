package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class AqiForecast {

    //日期
    @JSONField(name = "date")
    private String date;

    @JSONField(name = "publishTime")
    private String publishTime;

    @JSONField(name = "value")
    private int value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
