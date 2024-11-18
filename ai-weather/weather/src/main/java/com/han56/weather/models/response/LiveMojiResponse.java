package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.LiveIndex;
import com.han56.weather.models.entity.RC;

/**
 * 生活指数返回映射实体类
 */
public class LiveMojiResponse {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private LiveIndex liveIndex;

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

    public LiveIndex getLiveIndex() {
        return liveIndex;
    }

    public void setLiveIndex(LiveIndex liveIndex) {
        this.liveIndex = liveIndex;
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
