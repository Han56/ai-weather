package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.utils.ServiceResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    @ResultFormat
    public ServiceResult<String> test(){
        return new ServiceResult<>("返回测试数据");
    }

}
