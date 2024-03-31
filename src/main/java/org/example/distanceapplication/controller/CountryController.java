package org.example.distanceapplication.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.aspect.AspectAnnotation;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CountryController")
@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountryController {
    private final CountryServiceImpl countryService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<Country>> getAll() {
        return new ResponseEntity<>(countryService.read(), HttpStatus.OK);
    }

    @AspectAnnotation
    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<Country> getCountry(@RequestParam(name = "country") String name)
            throws ResourceNotFoundException {
        var country = countryService.getByName(name);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @AspectAnnotation
    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<Country> getCityInfoById(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        var country = countryService.getByID(id);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @AspectAnnotation
    @PutMapping("/update")
    public HttpStatus update(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.updateWithExist(countryDTO);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @PostMapping("/create")
    public HttpStatus create(@RequestBody CountryDTO countryDTO) throws BadRequestException {
        countryService.create(countryDTO);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @DeleteMapping("/delete")
    public HttpStatus delete(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        countryService.delete(id);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @PutMapping("/add_language")
    public HttpStatus addLanguages(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.modifyLanguage(countryDTO, false);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @PutMapping("/delete_language")
    public HttpStatus deleteLanguages(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.modifyLanguage(countryDTO, true);
        return HttpStatus.OK;
    }

    @AspectAnnotation
    @GetMapping("/get_by_language")
    public ResponseEntity<List<Country>> getCountriesByLanguage(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        var countries = countryService.getByLanguage(id);
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }
}
