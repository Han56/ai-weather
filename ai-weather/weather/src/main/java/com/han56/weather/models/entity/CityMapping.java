package com.han56.weather.models.entity;

public class CityMapping {

    private Integer fid;
    private String province;
    private String secondCity;
    private String cityNameCn;
    private String cityNameEn;
    private String cityNamePy;
    private Integer divisionCode;

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSecondCity() {
        return secondCity;
    }

    public void setSecondCity(String secondCity) {
        this.secondCity = secondCity;
    }

    public String getCityNameCn() {
        return cityNameCn;
    }

    public void setCityNameCn(String cityNameCn) {
        this.cityNameCn = cityNameCn;
    }

    public String getCityNameEn() {
        return cityNameEn;
    }

    public void setCityNameEn(String cityNameEn) {
        this.cityNameEn = cityNameEn;
    }

    public String getCityNamePy() {
        return cityNamePy;
    }

    public void setCityNamePy(String cityNamePy) {
        this.cityNamePy = cityNamePy;
    }

    public Integer getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(Integer divisionCode) {
        this.divisionCode = divisionCode;
    }
}
