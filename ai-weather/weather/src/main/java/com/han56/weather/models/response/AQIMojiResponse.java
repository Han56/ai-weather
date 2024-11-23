package com.han56.weather.models.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.han56.weather.models.entity.AQIMojiData;
import com.han56.weather.models.entity.RC;
import com.han56.weather.models.entity.ToString;

/**
 * 请求墨迹天气接口返回对应实体类
 * */
public class AQIMojiResponse extends ToString {

    //状态码-对应JSON中的code字段
    @JSONField(name = "code")
    private int code;

    //数据部分-对应JSON的data字段
    @JSONField(name = "data")
    private AQIMojiData aqiMojiData;

    //消息内容-对应JSON中的 msg 字段
    @JSONField(name = "msg")
    private String msg;

    //rc字段
    @JSONField(name = "rc")
    private RC rc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AQIMojiData getAqiMojiData() {
        return aqiMojiData;
    }

    public void setAqiMojiData(AQIMojiData aqiMojiData) {
        this.aqiMojiData = aqiMojiData;
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
