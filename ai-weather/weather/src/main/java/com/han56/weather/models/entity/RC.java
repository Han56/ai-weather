package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class RC {


    @JSONField(name = "c")
    private int c;

    @JSONField(name = "p")
    private String p;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }
}
