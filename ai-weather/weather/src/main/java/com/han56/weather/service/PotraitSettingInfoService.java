package com.han56.weather.service;

import com.han56.weather.mapper.PotraitSettingInfoMapper;
import com.han56.weather.models.entity.PotraitSettingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotraitSettingInfoService {

    @Autowired
    private PotraitSettingInfoMapper potraitSettingInfoMapper;

    public int insertPotraitSettingInfo(PotraitSettingInfo potraitSettingInfo) {
        return potraitSettingInfoMapper.insertPotraitSettingInfo(potraitSettingInfo);
    }

    public int updatePotraitSettingInfo(PotraitSettingInfo potraitSettingInfo) {
        return potraitSettingInfoMapper.updatePotraitSettingInfo(potraitSettingInfo);
    }

    public int deletePotraitSettingInfo(String openId) {
        return potraitSettingInfoMapper.deletePotraitSettingInfo(openId);
    }

    public PotraitSettingInfo selectByOpenId(String openId) {
        return potraitSettingInfoMapper.selectByOpenId(openId);
    }

    public List<PotraitSettingInfo> selectAllPotraitSettingInfo() {
        return potraitSettingInfoMapper.selectAllPotraitSettingInfo();
    }

}
