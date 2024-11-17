package com.han56.weather.utils;

import com.han56.weather.enums.ResultCodeType;

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
