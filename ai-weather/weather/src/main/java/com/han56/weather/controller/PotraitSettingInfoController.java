package com.han56.weather.controller;

import com.han56.weather.models.entity.PotraitSettingInfo;
import com.han56.weather.service.PotraitSettingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/potraitSettingInfo")
public class PotraitSettingInfoController {

    @Autowired
    private PotraitSettingInfoService potraitSettingInfoService;

    @PostMapping
    public int insertPotraitSettingInfo(@RequestBody PotraitSettingInfo potraitSettingInfo) {
        return potraitSettingInfoService.insertPotraitSettingInfo(potraitSettingInfo);
    }

    @PutMapping
    public int updatePotraitSettingInfo(@RequestBody PotraitSettingInfo potraitSettingInfo) {
        return potraitSettingInfoService.updatePotraitSettingInfo(potraitSettingInfo);
    }

    @DeleteMapping("/{openId}")
    public int deletePotraitSettingInfo(@PathVariable String openId) {
        return potraitSettingInfoService.deletePotraitSettingInfo(openId);
    }

    @GetMapping("/{openId}")
    public PotraitSettingInfo selectByOpenId(@PathVariable String openId) {
        return potraitSettingInfoService.selectByOpenId(openId);
    }

    @GetMapping
    public List<PotraitSettingInfo> selectAllPotraitSettingInfo() {
        return potraitSettingInfoService.selectAllPotraitSettingInfo();
    }

}
