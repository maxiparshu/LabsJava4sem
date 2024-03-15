package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;

import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.service.DistanceService;

import org.example.distanceapplication.service.implementation.CityServiceImpl;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cities")
@AllArgsConstructor
public class CityController {
    private final CityServiceImpl dataService;
    private final DistanceService distanceService;
    private final CountryServiceImpl countryService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<City>> getAllCity() {
        return new ResponseEntity<>(dataService.read(), HttpStatus.OK);
    }

    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<City> getCityInfo(@RequestParam(name = "city") String cityName) {
        var cityInfo = dataService.getByName(cityName);
        if (cityInfo == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cityInfo, HttpStatus.OK);
    }

    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<City> getCityInfoById(@RequestParam(name = "id") Long id) {
        var cityInfo = dataService.getByID(id);
        if (cityInfo == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(cityInfo, HttpStatus.OK);
    }

    @GetMapping(value = "/distance/{firstCity}+{secondCity}", produces = "application/json")
    public ResponseEntity<?> getDistance(@PathVariable(name = "firstCity") String firstCity, @PathVariable(name = "secondCity") String secondCity) {
        var firstCityInfo = dataService.getByName(firstCity);
        var secondCityInfo = dataService.getByName(secondCity);
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

    @PutMapping("/update/{countryName}")
    private HttpStatus update(@RequestBody City city, @PathVariable(name = "countryName") String countryName) {
        var country = countryService.getByName(countryName);
        if (country == null)
            return HttpStatus.NOT_FOUND;
        if (Boolean.TRUE.equals(dataService.updateWithCountry(city, country)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;
    }

    @PostMapping("/create/{countryName}")
    private HttpStatus create(@RequestBody CityDTO city, @PathVariable(name = "countryName") String countryName) {
        var country = countryService.getByName(countryName);
        if (country == null)
            return HttpStatus.NOT_FOUND;
        if (Boolean.TRUE.equals(dataService.createWithCountry(city, country)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;
    }

    @DeleteMapping("/delete")
    private HttpStatus delete(@RequestParam(name = "id") Long id) {
        if (Boolean.TRUE.equals(dataService.delete(id)))
            return HttpStatus.OK;
        return HttpStatus.NOT_FOUND;
    }
}
