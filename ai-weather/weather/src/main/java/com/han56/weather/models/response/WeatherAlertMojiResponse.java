package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.RC;
import com.han56.weather.models.entity.WeatherMojiAlertData;

public class WeatherAlertMojiResponse {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private WeatherMojiAlertData weatherMojiAlertData;

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "rc")
    private RC rc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public WeatherMojiAlertData getWeatherMojiAlertData() {
        return weatherMojiAlertData;
    }

    public void setWeatherMojiAlertData(WeatherMojiAlertData weatherMojiAlertData) {
        this.weatherMojiAlertData = weatherMojiAlertData;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RC getRc() {
        return rc;
    }

    public void setRc(RC rc) {
        this.rc = rc;
    }
}
