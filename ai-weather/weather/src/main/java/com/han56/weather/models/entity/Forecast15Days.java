package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 15日天气预报
 * */
public class Forecast15Days {

    //白天天气状况-例如多云
    @JSONField(name = "conditionDay")
    private String conditionDay;

    //白天天气状况代码-例如多云对应1
    @JSONField(name = "conditionIdDay")
    private String conditionIdDay;

    //黑天天气状况
    @JSONField(name = "condtionNight")
    private String condtionNight;

    //黑天天气状况代码
    @JSONField(name = "conditionIdNight")
    private String condtionIdNight;

    //月相
    @JSONField(name = "moonphase")
    private String moonphase;

    //月升
    @JSONField(name = "moonrise")
    private String moonrise;

    //月落
    @JSONField(name = "moonset")
    private String moonset;

    //预报时间
    @JSONField(name = "predictDate")
    private String predictDate;

    //日出时间
    @JSONField(name = "sunrise")
    private String sunrise;

    //日落时间
    @JSONField(name = "sunset")
    private String sunset;

    //日间温度
    @JSONField(name = "tempDay")
    private String tempDay;

    //夜间温度
    @JSONField(name = "tempNight")
    private String tempNight;

    //更新时间
    @JSONField(name = "updatetime")
    private String updatetime;

    //日间风向
    @JSONField(name = "windDirDay")
    private String windDirDay;

    //日间风力级别
    @JSONField(name = "windLevelDay")
    private String windLevelDay;

    //日间风速
    @JSONField(name = "windSpeedDay")
    private String windSpeedDay;

    //夜间风向
    @JSONField(name = "windDirNight")
    private String windDirNight;

    //夜间风力级别
    @JSONField(name = "windLevelNight")
    private String windLevelNight;

    //夜间风速
    @JSONField(name = "windSpeedNight")
    private String windSpeedNight;

    public String getConditionDay() {
        return conditionDay;
    }

    public void setConditionDay(String conditionDay) {
        this.conditionDay = conditionDay;
    }

    public String getConditionIdDay() {
        return conditionIdDay;
    }

    public void setConditionIdDay(String conditionIdDay) {
        this.conditionIdDay = conditionIdDay;
    }

    public String getCondtionNight() {
        return condtionNight;
    }

    public void setCondtionNight(String condtionNight) {
        this.condtionNight = condtionNight;
    }

    public String getCondtionIdNight() {
        return condtionIdNight;
    }

    public void setCondtionIdNight(String condtionIdNight) {
        this.condtionIdNight = condtionIdNight;
    }

    public String getMoonphase() {
        return moonphase;
    }

    public void setMoonphase(String moonphase) {
        this.moonphase = moonphase;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }

    public String getPredictDate() {
        return predictDate;
    }

    public void setPredictDate(String predictDate) {
        this.predictDate = predictDate;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getTempDay() {
        return tempDay;
    }

    public void setTempDay(String tempDay) {
        this.tempDay = tempDay;
    }

    public String getTempNight() {
        return tempNight;
    }

    public void setTempNight(String tempNight) {
        this.tempNight = tempNight;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWindDirDay() {
        return windDirDay;
    }

    public void setWindDirDay(String windDirDay) {
        this.windDirDay = windDirDay;
    }

    public String getWindLevelDay() {
        return windLevelDay;
    }

    public void setWindLevelDay(String windLevelDay) {
        this.windLevelDay = windLevelDay;
    }

    public String getWindSpeedDay() {
        return windSpeedDay;
    }

    public void setWindSpeedDay(String windSpeedDay) {
        this.windSpeedDay = windSpeedDay;
    }

    public String getWindDirNight() {
        return windDirNight;
    }

    public void setWindDirNight(String windDirNight) {
        this.windDirNight = windDirNight;
    }

    public String getWindLevelNight() {
        return windLevelNight;
    }

    public void setWindLevelNight(String windLevelNight) {
        this.windLevelNight = windLevelNight;
    }

    public String getWindSpeedNight() {
        return windSpeedNight;
    }

    public void setWindSpeedNight(String windSpeedNight) {
        this.windSpeedNight = windSpeedNight;
    }
}
