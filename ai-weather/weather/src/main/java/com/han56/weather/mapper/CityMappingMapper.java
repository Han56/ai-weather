package com.han56.weather.mapper;

import com.han56.weather.models.entity.CityMapping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CityMappingMapper {

    int insert(CityMapping cityMapping);

    CityMapping selectById(Integer fid);

    /**
    *  根据行政区划代码查fid
    * */
    List<Integer> selectFidsByDivisionCode(Integer divisionCode);

    List<CityMapping> selectAll();

    int update(CityMapping cityMapping);

    int deleteById(Integer fid);

}
