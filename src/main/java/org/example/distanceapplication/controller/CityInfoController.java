package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;

import org.example.distanceapplication.entity.CityInfo;
import org.example.distanceapplication.service.DataService;
import org.example.distanceapplication.service.DistanceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cities")
@AllArgsConstructor
public class CityInfoController {
    private final DataService dataService;
    private final DistanceService distanceService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<CityInfo>> getAllCity() {
        return new ResponseEntity<>(dataService.getAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<CityInfo> getCityInfo(@RequestParam(name = "city") String cityName) {
        var cityInfo = dataService.getCityInfoByName(cityName);
        if (cityInfo == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cityInfo, HttpStatus.OK);
    }

    @GetMapping(value = "/distance/{firstCity}+{secondCity}", produces = "application/json")
    public ResponseEntity<?> getDistance(@PathVariable(name = "firstCity") String firstCity, @PathVariable(name = "secondCity") String secondCity) {
        var firstCityInfo = dataService.getCityInfoByName(firstCity);
        var secondCityInfo = dataService.getCityInfoByName(secondCity);
        double distance = distanceService.getDistanceInKilometres(firstCityInfo, secondCityInfo);
        if (distance != -1) {
            var objects = new HashMap<String, String>();
            objects.put("First city info", firstCityInfo.toString());
            objects.put("Second city info", secondCityInfo.toString());
            objects.put("Distance", Double.toString(distance));
            return new ResponseEntity<>(objects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/add")
    public CityInfo addCityInfo(@RequestBody CityInfo newCity) {
        if (dataService.getCityInfoByName(newCity.getName()) != null) return null;
        return dataService.updateCityInfo(newCity);
    }
    @PatchMapping(value = "/update")
    public CityInfo updateCityInfo(@RequestBody CityInfo newCity) {
        if (dataService.getCityInfoByName(newCity.getName()) == null) return null;
        return dataService.updateCityInfo(newCity);
    }
}
