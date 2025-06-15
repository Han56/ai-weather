package com.han56.weather.mapper;

import com.han56.weather.models.entity.PotraitSettingInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PotraitSettingInfoMapper {

    int insertPotraitSettingInfo(PotraitSettingInfo potraitSettingInfo);

    int updatePotraitSettingInfo(PotraitSettingInfo potraitSettingInfo);

    int deletePotraitSettingInfo(@Param("openId") String openId);

    PotraitSettingInfo selectByOpenId(@Param("openId") String openId);

    List<PotraitSettingInfo> selectAllPotraitSettingInfo();

}
