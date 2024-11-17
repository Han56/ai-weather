package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 天气实况实体类
 * */
public class RealTimeWeatherCondition {

    //天气情况 例如晴
    @JSONField(name = "condition")
    private String condition;

    //天气情况代码 例如晴天对应5
    @JSONField(name = "conditionId")
    private String conditionId;

    //湿度
    @JSONField(name = "humidity")
    private String humidity;

    //图标代号
    @JSONField(name = "icon")
    private String icon;

    //大气压
    @JSONField(name = "pressure")
    private String pressure;

    //体感温度
    @JSONField(name = "realFeel")
    private String realFeel;

    //日出时间
    @JSONField(name = "sunRise")
    private String sunRise;

    //日落时间
    @JSONField(name = "sunSet")
    private String sunSet;

    //实况温度
    @JSONField(name = "temp")
    private String temp;

    //小便签
    @JSONField(name = "tips")
    private String tips;

    //实况天气更新时间
    @JSONField(name = "updatetime")
    private String updatetime;

    //紫外线指数
    @JSONField(name = "uvi")
    private String uvi;

    //风向
    @JSONField(name = "windDir")
    private String windDir;

    //风速
    @JSONField(name = "windSpeed")
    private String windSpeed;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionId() {
        return conditionId;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getRealFeel() {
        return realFeel;
    }

    public void setRealFeel(String realFeel) {
        this.realFeel = realFeel;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUvi() {
        return uvi;
    }

    public void setUvi(String uvi) {
        this.uvi = uvi;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
