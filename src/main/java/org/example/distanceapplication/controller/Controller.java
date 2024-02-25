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
@RequestMapping("/api")
@AllArgsConstructor
public class Controller {
    private final DataService dataService;
    private final DistanceService distanceService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<CityInfo>> getAllCity(){
        return new ResponseEntity<>(dataService.getAll(), HttpStatus.OK);
    }
    @GetMapping(value = "/info/", produces = "application/json")
    public ResponseEntity<CityInfo> getCityInfo(@RequestParam(name = "city") String cityName){
        return new ResponseEntity<>(dataService.getCityInfoByName(cityName), HttpStatus.OK);
    }
    @GetMapping(value = "/distance/{firstCity}+{secondCity}", produces = "application/json")
    public ResponseEntity<HashMap<String, String>> getDistance(@PathVariable String firstCity, @PathVariable String secondCity){
        var firstCityInfo = dataService.getCityInfoByName(firstCity);
        var secondCityInfo = dataService.getCityInfoByName(secondCity);
        double distance = distanceService.getDistanceInKilometres(firstCityInfo, secondCityInfo);
        var objects = new HashMap<String,String>();
        objects.put("First city info", firstCityInfo.toString());
        objects.put("Second city info", secondCityInfo.toString());
        objects.put("Distance", Double.toString(distance));
        return new ResponseEntity<>(objects,HttpStatus.OK);
    }

}
