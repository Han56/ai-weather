package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.Forecast15DaysMojiData;
import com.han56.weather.models.entity.RC;
import com.han56.weather.models.entity.ToString;

public class Forecast15DaysMojiResponse extends ToString {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private Forecast15DaysMojiData forecast15DaysMojiData;

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

    public Forecast15DaysMojiData getForecast15DaysMojiData() {
        return forecast15DaysMojiData;
    }

    public void setForecast15DaysMojiData(Forecast15DaysMojiData forecast15DaysMojiData) {
        this.forecast15DaysMojiData = forecast15DaysMojiData;
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
