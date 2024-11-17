package com.han56.weather.enums;

import com.han56.weather.utils.ResultCode;

public enum CommonResultCode implements ResultCode {

    /*
     * 处理成功
     * */
    OK("200", "OK","处理成功"),
    CREATED("201", "Created","请求创建成功"),
    ACCEPTED("202", "Accepted","请求已接受"),
    NO_CONTENT("204", "No Content","无返回内容"),
    MOVED_PERMANENTLY("301", "Moved Permanently","请求资源已被永久移动到新的位置"),
    NOT_MODIFIED("304", "Not Modified","自从上次请求后，资源未被修改"),
    BAD_REQUEST("400", "Bad Request","请求语法错误"),
    UNAUTHORIZED("401", "Unauthorized","请求未授权"),
    FORBIDDEN("403", "Forbidden","禁止访问"),
    NOT_FOUND("404", "Not Found","为找到请求资源"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error","内部服务器错误"),
    NOT_IMPLEMENTED("501", "Not Implemented","服务器不支持请求的功能"),
    SERVICE_UNAVAILABLE("503", "Service Unavailable","服务不可用")
    ;

    private String code;

    private String desc;

    private String view;

    CommonResultCode(String code, String desc, String view) {
        this.code = code;
        this.desc = desc;
        this.view = view;
    }

    @Override
    public ResultCodeType getType() {
        return com.han56.weather.enums.ResultCodeType.COMMON;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String getView() {
        return view;
    }

}
