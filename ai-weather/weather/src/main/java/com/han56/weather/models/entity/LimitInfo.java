package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 限行数据
 */
public class LimitInfo {

    @JSONField(name = "date")
    private String date;

    //限行标签
    @JSONField(name = "prompt")
    private String prompt;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
