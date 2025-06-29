package com.han56.weather.service;

import com.han56.weather.mapper.PotraitSettingInfoMapper;
import com.han56.weather.models.entity.PotraitSettingInfo;
import com.han56.weather.utils.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotraitSettingInfoService {

    @Autowired
    private PotraitSettingInfoMapper potraitSettingInfoMapper;

    @Autowired
    private CacheManager cacheManager;

    public int insertPotraitSettingInfo(PotraitSettingInfo potraitSettingInfo) {
        return potraitSettingInfoMapper.insertPotraitSettingInfo(potraitSettingInfo);
    }

    public int updatePotraitSettingInfo(PotraitSettingInfo potraitSettingInfo) {
        // 更新用户画像设置
        int result = potraitSettingInfoMapper.updatePotraitSettingInfo(potraitSettingInfo);
        
        // 如果更新成功，清空相关缓存
        if (result > 0 && potraitSettingInfo.getOpenId() != null) {
            // 清空用户相关缓存
            cacheManager.clearUserCache(potraitSettingInfo.getOpenId());
            // 清空该用户的所有AI推荐缓存
            cacheManager.clearUserAiRecommendCache(potraitSettingInfo.getOpenId());
        }
        
        return result;
    }

    public int deletePotraitSettingInfo(String openId) {
        // 删除前先清空缓存
        if (openId != null) {
            cacheManager.clearUserCache(openId);
            cacheManager.clearUserAiRecommendCache(openId);
        }
        
        return potraitSettingInfoMapper.deletePotraitSettingInfo(openId);
    }

    public PotraitSettingInfo selectByOpenId(String openId) {
        return potraitSettingInfoMapper.selectByOpenId(openId);
    }

    public List<PotraitSettingInfo> selectAllPotraitSettingInfo() {
        return potraitSettingInfoMapper.selectAllPotraitSettingInfo();
    }

}
