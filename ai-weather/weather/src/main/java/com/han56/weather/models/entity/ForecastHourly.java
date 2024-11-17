package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 24小时天气预报
 * */
public class ForecastHourly {

    //天气状况-例如阴
    @JSONField(name = "condition")
    private String condition;

    //日期
    @JSONField(name = "date")
    private String date;

    //小时单位
    @JSONField(name = "hour")
    private String hour;

    //湿度
    @JSONField(name = "humidity")
    private String humidity;

    //日间图标
    @JSONField(name = "iconDay")
    private String iconDay;

    //夜间图标
    @JSONField(name = "iconNight")
    private String iconNight;

    //气压
    @JSONField(name = "pressure")
    private String pressure;

    //体感温度
    @JSONField(name = "realFeel")
    private String realFeel;

    //气温
    @JSONField(name = "temp")
    private String temp;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIconDay() {
        return iconDay;
    }

    public void setIconDay(String iconDay) {
        this.iconDay = iconDay;
    }

    public String getIconNight() {
        return iconNight;
    }

    public void setIconNight(String iconNight) {
        this.iconNight = iconNight;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
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
