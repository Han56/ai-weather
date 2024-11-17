package com.han56.weather.controller;

import com.han56.weather.annotation.ResultFormat;
import com.han56.weather.utils.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
    @ResultFormat
    public ServiceResult<String> test(){
        return new ServiceResult<>("返回测试数据");
    }

}
