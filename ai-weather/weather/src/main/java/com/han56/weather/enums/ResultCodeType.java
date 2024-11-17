package com.han56.weather.enums;

public enum ResultCodeType {

    /*
     * 通用类型结果码
     * */
    COMMON("10","通用类型结果码"),

    /*
     * 业务类型结果码
     * */
    BIZ("11","业务类型结果码")
    ;



    /*
     * 类型代码
     * */
    private String code;

    /*
     * 描述
     * */
    private String desc;

    /*
     * 构造方法
     * */
    ResultCodeType(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    /*
     * getter and setter
     * */

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
