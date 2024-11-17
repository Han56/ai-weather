package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.LimitInfoData;
import com.han56.weather.models.entity.RC;

public class LimitInfoMojiResponse {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private LimitInfoData limitInfoData;

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

    public LimitInfoData getLimitInfoData() {
        return limitInfoData;
    }

    public void setLimitInfoData(LimitInfoData limitInfoData) {
        this.limitInfoData = limitInfoData;
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
