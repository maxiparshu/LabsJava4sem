package org.example.distanceapplication.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.aspect.AspectAnnotation;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.DistanceService;
import org.example.distanceapplication.service.implementation.CityServiceImpl;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CityController")
@RestController
@RequestMapping("/api/cities")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://lab7-front.vercel.app"})
public class CityController {
  private final CityServiceImpl dataService;
  private final DistanceService distanceService;
  private final CountryServiceImpl countryService;

  @GetMapping(value = "/all", produces = "application/json")
  public List<City> getAllCity() {
    return dataService.read();
  }

  @AspectAnnotation
  @GetMapping(value = "/info", produces = "application/json")
  public ResponseEntity<City> getCityInfo(
      final @RequestParam(name = "city") String cityName)
      throws ResourceNotFoundException {
    var cityInfo = dataService.getByName(cityName);
    return new ResponseEntity<>(cityInfo, HttpStatus.OK);
  }

  @AspectAnnotation
  @GetMapping(value = "/find", produces = "application/json")
  public ResponseEntity<City> getCityInfoById(
      final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    var cityInfo = dataService.getByID(id);
    return new ResponseEntity<>(cityInfo, HttpStatus.OK);
  }

  @AspectAnnotation
  @GetMapping(value = "/distance/{firstCity}+{secondCity}",
      produces = "application/json")
  public ResponseEntity<HashMap<String, String>> getDistance(
      final @PathVariable(name = "firstCity") String firstCity,
      final @PathVariable(name = "secondCity") String secondCity)
      throws ResourceNotFoundException {
    var firstCityInfo = dataService.getByName(firstCity);
    var secondCityInfo = dataService.getByName(secondCity);
    double distance = distanceService.getDistanceInKilometres(
        firstCityInfo, secondCityInfo);
    if (distance != -1) {
      var objects = new HashMap<String, String>();
      objects.put("First city info: ", firstCityInfo.getName() + " "
          + firstCityInfo.getLongitude() + " " + firstCityInfo.getLatitude());
      objects.put("Second city info: ", secondCityInfo.getName() + " "
          + secondCityInfo.getLongitude() + " " + secondCityInfo.getLatitude());
      objects.put("Distance", Double.toString(distance));
      return new ResponseEntity<>(objects, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @AspectAnnotation
  @PutMapping("/update")
  public HttpStatus update(final @RequestBody CityDTO city)
      throws ResourceNotFoundException {
    dataService.update(city);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PutMapping("/update/{countryName}")
  public HttpStatus update(final @RequestBody CityDTO city,
                           final @PathVariable(name = "countryName")
                           String countryName)
      throws ResourceNotFoundException {
    var country = countryService.getByName(countryName);
    dataService.updateWithCountry(city, country);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PostMapping("/create/{countryName}")
  public HttpStatus create(final @RequestBody CityDTO city,
                           final @PathVariable(name = "countryName")
                           String countryName)
      throws ResourceNotFoundException, BadRequestException {
    var country = countryService.getByName(countryName);
    dataService.createWithCountry(city, country);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @DeleteMapping("/delete")
  public HttpStatus delete(final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    dataService.delete(id);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @GetMapping("/getBetweenLatitude")
  public ResponseEntity<List<City>> getCitiesBetween(
      final @RequestParam(name = "first") Double first,
      final @RequestParam(name = "second") Double second) {
    if (first > second) {
      return new ResponseEntity<>(
          dataService.getBetweenLatitudes(second, first),
          HttpStatus.OK);
    }
    return new ResponseEntity<>(
        dataService.getBetweenLatitudes(first, second), HttpStatus.OK);
  }

  @AspectAnnotation
  @PostMapping("/bulkCreate")
  public HttpStatus bulkCreate(
      @RequestBody final CityDTO[] cityDTOS) {
    Arrays.stream(cityDTOS).forEach(city -> {
      try {
        var country = countryService.getByID(city.getIdCountry());
        dataService.createWithCountry(city, country);
      } catch (ResourceNotFoundException ignored) {
      }
    });
    return HttpStatus.OK;
  }
}
