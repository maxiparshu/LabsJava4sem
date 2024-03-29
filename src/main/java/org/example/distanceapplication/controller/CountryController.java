package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@AllArgsConstructor
public class CountryController {
    private final CountryServiceImpl countryService;

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<Country>> getAll() {
        return new ResponseEntity<>(countryService.read(), HttpStatus.OK);
    }

    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<Country> getCountry(@RequestParam(name = "country") String name)
            throws ResourceNotFoundException {
        var country = countryService.getByName(name);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<Country> getCityInfoById(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        var country = countryService.getByID(id);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @PutMapping("/update")
    public HttpStatus update(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.updateWithExist(countryDTO);
        return HttpStatus.OK;
    }

    @PostMapping("/create")
    public HttpStatus create(@RequestBody CountryDTO countryDTO) throws BadRequestException {
        countryService.create(countryDTO);
        return HttpStatus.OK;
    }

    @DeleteMapping("/delete")
    public HttpStatus delete(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        countryService.delete(id);
        return HttpStatus.OK;
    }

    @PutMapping("/add_language")
    public HttpStatus addLanguages(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.modifyLanguage(countryDTO, false);
        return HttpStatus.OK;
    }

    @PutMapping("/delete_language")
    public HttpStatus deleteLanguages(@RequestBody CountryDTO countryDTO)
            throws ResourceNotFoundException {
        countryService.modifyLanguage(countryDTO, true);
        return HttpStatus.OK;
    }

    @GetMapping("/get_by_language")
    public ResponseEntity<List<Country>> getCountriesByLanguage(@RequestParam(name = "id") Long id)
        throws ResourceNotFoundException{
        var countries = countryService.getByLanguage(id);
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }
}
