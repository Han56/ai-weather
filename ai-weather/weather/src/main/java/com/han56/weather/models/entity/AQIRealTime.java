package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 当天空气质量数据
 * */
public class AQIRealTime {

    @JSONField(name = "cityName")
    private String cityName;

    //一氧化碳指数
    @JSONField(name = "co")
    private String co;

    //二氧化氮指数
    @JSONField(name = "no2")
    private String no2;

    //臭氧指数
    @JSONField(name = "o3")
    private String o3;

    //二氧化硫指数
    @JSONField(name = "so2")
    private String so2;

    //PM10指数
    @JSONField(name = "pm10")
    private String pm10;

    //pm25指数
    @JSONField(name = "pm25")
    private String pm25;

    //数据发布时间
    @JSONField(name = "pubtime")
    private String pubtime;

    //城市排名
    @JSONField(name = "rank")
    private String rank;

    //空气质量value
    @JSONField(name = "value")
    private String value;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCo() {
        return co;
    }

    public void setCo(String co) {
        this.co = co;
    }

    public String getNo2() {
        return no2;
    }

    public void setNo2(String no2) {
        this.no2 = no2;
    }

    public String getO3() {
        return o3;
    }

    public void setO3(String o3) {
        this.o3 = o3;
    }

    public String getSo2() {
        return so2;
    }

    public void setSo2(String so2) {
        this.so2 = so2;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getPubtime() {
        return pubtime;
    }

    public void setPubtime(String pubtime) {
        this.pubtime = pubtime;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
