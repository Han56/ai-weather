package com.han56.weather.models.entity;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 天气预警
 * */
public class WeatherAlert {

    @JSONField(name = "content")
    private String content;

    @JSONField(name = "infoid")
    private String infoId;

    @JSONField(name = "level")
    private String level;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "pub_time")
    private String pubTime;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "type")
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubTime() {
        return pubTime;
    }

    public void setPubTime(String pubTime) {
        this.pubTime = pubTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
