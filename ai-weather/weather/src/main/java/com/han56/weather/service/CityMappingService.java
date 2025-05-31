package com.han56.weather.service;

import com.han56.weather.mapper.CityMappingMapper;
import com.han56.weather.models.entity.CityMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityMappingService {

    @Autowired
    private CityMappingMapper cityMappingMapper;

    public CityMapping createCityMapping(CityMapping cityMapping) {
        cityMappingMapper.insert(cityMapping);
        return cityMapping;
    }

    public CityMapping getCityMappingById(Integer fid) {
        return cityMappingMapper.selectById(fid);
    }

    public List<CityMapping> getAllCityMappings() {
        return cityMappingMapper.selectAll();
    }

    public List<Integer> getFidsByDivisionCode(Integer divisionCode) {
        return cityMappingMapper.selectFidsByDivisionCode(divisionCode);
    }

    public CityMapping updateCityMapping(CityMapping cityMapping) {
        cityMappingMapper.update(cityMapping);
        return cityMapping;
    }

    public void deleteCityMapping(Integer fid) {
        cityMappingMapper.deleteById(fid);
    }

}
