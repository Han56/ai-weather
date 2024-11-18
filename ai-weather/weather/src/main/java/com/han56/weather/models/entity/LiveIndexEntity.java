package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class LiveIndexEntity {

    @JSONField(name = "day")
    private String day;

    @JSONField(name = "desc")
    private String desc;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "status")
    private String status;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
