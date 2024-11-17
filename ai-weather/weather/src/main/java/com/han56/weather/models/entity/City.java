package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class City {

    //城市id
    @JSONField(name = "cityId")
    private int cityId;

    //国家名称
    @JSONField(name = "counname")
    private String counname;

    //时区
    @JSONField(name = "ianatimezone")
    private String ianatimezone;

    //时区代号
    @JSONField(name = "timezone")
    private String timezone;

    //城市名称
    @JSONField(name = "name")
    private String name;

    //上级城市名称
    @JSONField(name = "pname")
    private String pname;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCounname() {
        return counname;
    }

    public void setCounname(String counname) {
        this.counname = counname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getIanatimezone() {
        return ianatimezone;
    }

    public void setIanatimezone(String ianatimezone) {
        this.ianatimezone = ianatimezone;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
