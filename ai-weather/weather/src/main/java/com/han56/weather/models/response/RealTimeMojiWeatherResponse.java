package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.RC;
import com.han56.weather.models.entity.RealTimeMojiWeatherData;
import com.han56.weather.models.entity.ToString;

public class RealTimeMojiWeatherResponse extends ToString {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private RealTimeMojiWeatherData realTimeMojiWeatherData;

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

    public RealTimeMojiWeatherData getRealTimeMojiWeatherData() {
        return realTimeMojiWeatherData;
    }

    public void setRealTimeMojiWeatherData(RealTimeMojiWeatherData realTimeMojiWeatherData) {
        this.realTimeMojiWeatherData = realTimeMojiWeatherData;
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
