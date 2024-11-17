package com.han56.weather.demos.web.utils;

import com.han56.weather.demos.web.enums.ResultCodeType;

public interface ResultCode {

    /*
     * 获取结果类型
     * */
    ResultCodeType getType();

    /*
     * 获取结果码
     * */
    String getCode();

    /*
     * 获取结果描述
     * */
    String getDesc();

    /*
     * 获取显示结果描述
     * */
    String getView();

}
