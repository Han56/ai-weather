package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.ForecastHourlyMojiData;
import com.han56.weather.models.entity.RC;

public class ForecastHourlyMojiResponse {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private ForecastHourlyMojiData forecastHourlyMojiData;

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

    public ForecastHourlyMojiData getForecastHourlyMojiData() {
        return forecastHourlyMojiData;
    }

    public void setForecastHourlyMojiData(ForecastHourlyMojiData forecastHourlyMojiData) {
        this.forecastHourlyMojiData = forecastHourlyMojiData;
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
