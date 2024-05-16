package org.example.distanceapplication.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.aspect.AspectAnnotation;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CountryController")
@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
@CrossOrigin
public class CountryController {
  private final CountryServiceImpl countryService;

  @GetMapping(value = "/all", produces = "application/json")
  public List<Country> getAll() {
    return countryService.read();
  }

  @AspectAnnotation
  @GetMapping(value = "/info", produces = "application/json")
  public ResponseEntity<Country> getCountry(
      final @RequestParam(name = "country") String name)
      throws ResourceNotFoundException {
    var country = countryService.getByName(name);
    return new ResponseEntity<>(country, HttpStatus.OK);
  }

  @AspectAnnotation
  @GetMapping(value = "/find", produces = "application/json")
  public ResponseEntity<Country> getCountryById(
      final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    var country = countryService.getByID(id);
    return new ResponseEntity<>(country, HttpStatus.OK);
  }

  @AspectAnnotation
  @PutMapping("/update")
  public HttpStatus update(final @RequestBody CountryDTO countryDto)
      throws ResourceNotFoundException {
    countryService.update(countryDto);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PostMapping("/create")
  public HttpStatus create(final @RequestBody CountryDTO countryDto)
      throws BadRequestException {
    System.out.println(countryDto);
    countryService.create(countryDto);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @DeleteMapping("/delete")
  public HttpStatus delete(final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    countryService.delete(id);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PutMapping("/add_language")
  public HttpStatus addLanguages(
      final @RequestBody CountryDTO countryDto)
      throws ResourceNotFoundException {
    countryService.modifyLanguage(countryDto, false);
    System.out.println(countryDto.getLanguages());
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PutMapping("/delete_language")
  public HttpStatus deleteLanguages(
      final @RequestBody CountryDTO countryDto)
      throws ResourceNotFoundException {
    System.out.println(countryDto);
    countryService.modifyLanguage(countryDto, true);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @GetMapping("/get_by_language")
  public ResponseEntity<List<Country>> getCountriesByLanguage(
      final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    var countries = countryService.getByLanguage(id);
    return new ResponseEntity<>(countries, HttpStatus.OK);
  }
  @AspectAnnotation
  @PostMapping("/bulkCreate")
  public HttpStatus bulkCreate(
      @RequestBody final CountryDTO[] countries) {
    Arrays.stream(countries).forEach(countryService::create);
    return HttpStatus.OK;
  }
}
