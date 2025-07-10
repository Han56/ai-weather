package com.han56.weather.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weixin")
public class WeixinAuthController {
    private static final Logger logger = LoggerFactory.getLogger(WeixinAuthController.class);

    private static final String APPID = "wx3e463a85be4880fc";
    private static final String SECRET = "412e616cfaa20f7612aac9012776062b";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String WX_API_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @GetMapping("/getOpenId")
    public Map<String, Object> getOpenId(@RequestParam("js_code") String jsCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=%s",
                    WX_API_URL, APPID, SECRET, jsCode, GRANT_TYPE);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            JSONObject json = JSON.parseObject(body);
            if (json.containsKey("openid")) {
                result.put("success", true);
                result.put("openid", json.getString("openid"));
                result.put("session_key", json.getString("session_key"));
            } else {
                result.put("success", false);
                result.put("error", json.getString("errmsg"));
                result.put("errcode", json.getString("errcode"));
            }
        } catch (Exception e) {
            logger.error("调用微信jscode2session接口异常", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
} 