package com.han56.weather.controller;

import com.han56.weather.models.entity.CityMapping;
import com.han56.weather.service.CityMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city-mappings")
public class CityMappingController {

    @Autowired
    private CityMappingService cityMappingService;

    @PostMapping
    public CityMapping createCityMapping(@RequestBody CityMapping cityMapping) {
        return cityMappingService.createCityMapping(cityMapping);
    }

    @GetMapping("/{fid}")
    public CityMapping getCityMappingById(@PathVariable Integer fid) {
        return cityMappingService.getCityMappingById(fid);
    }

    @GetMapping
    public List<CityMapping> getAllCityMappings() {
        return cityMappingService.getAllCityMappings();
    }

    @GetMapping("/get_fids")
    public List<Integer> getFidsByDivisionCode(@RequestParam Integer divisionCode) {
        return cityMappingService.getFidsByDivisionCode(divisionCode);
    }

    @PutMapping("/{fid}")
    public CityMapping updateCityMapping(@PathVariable Integer fid, @RequestBody CityMapping cityMapping) {
        cityMapping.setFid(fid);
        return cityMappingService.updateCityMapping(cityMapping);
    }

    @DeleteMapping("/{fid}")
    public void deleteCityMapping(@PathVariable Integer fid) {
        cityMappingService.deleteCityMapping(fid);
    }

}
