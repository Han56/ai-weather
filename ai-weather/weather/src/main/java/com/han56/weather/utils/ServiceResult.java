package com.han56.weather.utils;

import com.han56.weather.enums.CommonResultCode;

public class ServiceResult<T> {

    /*
     * 成功标识
     * */
    private boolean success = false;

    /*
     * 结果码
     * */
    private String code = "";

    /*
     * 返回信息
     * */
    private String message = "";

    /*
     * 结果内容
     * */
    private T result;


    /*
     * 构造函数
     * */

    public ServiceResult() {
    }

    public ServiceResult(String errorCode, String errorMsg) {
        this.code = errorCode;
        this.message = errorMsg;
        this.success = false;
    }

    public ServiceResult(boolean success, String code, String message, T result) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public ServiceResult(T data) {
        this.code = CommonResultCode.OK.getCode();
        this.success = true;
        this.result = data;
    }

    /*
     * 成功返回1
     * */
    private static ServiceResult success(){
        ServiceResult result = new ServiceResult<>();
        result.setSuccess(true);
        return result;
    }

    /*
     * 成功返回2
     * */
    private static <T> ServiceResult<T> success(T data){
        return new ServiceResult<T>(data);
    }

    /*
     * 失败返回1
     * */
    private static ServiceResult fail(){
        ServiceResult result = new ServiceResult<>();
        result.setSuccess(true);
        return result;
    }

    /*
     * 失败返回2
     * */
    private static <T> ServiceResult<T> fail(T data){
        return new ServiceResult<T>(data);
    }

    /*
     * getter and setter
     * */
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
