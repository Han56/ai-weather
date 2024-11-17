package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.AQIRealTimeMojiData;
import com.han56.weather.models.entity.RC;

public class AQIRealTimeMojiResponse {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private AQIRealTimeMojiData aqiRealTimeMojiData;

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

    public AQIRealTimeMojiData getAqiRealTimeMojiData() {
        return aqiRealTimeMojiData;
    }

    public void setAqiRealTimeMojiData(AQIRealTimeMojiData aqiRealTimeMojiData) {
        this.aqiRealTimeMojiData = aqiRealTimeMojiData;
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
