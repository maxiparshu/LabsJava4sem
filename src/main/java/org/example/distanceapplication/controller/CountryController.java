package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
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
    private ResponseEntity<List<Country>> getAll() {
        return new ResponseEntity<>(countryService.read(), HttpStatus.OK);
    }

    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<Country> getCountry(@RequestParam(name = "country") String name) {
        var country = countryService.getByName(name);
        if (country == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<Country> getCityInfoById(@RequestParam(name = "id") Long id) {
        var country = countryService.getByID(id);
        if (country == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(country, HttpStatus.OK);
    }

    @PutMapping("/update")
    private HttpStatus update(@RequestBody CountryDTO countryDTO) {
        if (Boolean.TRUE.equals(countryService.updateWithExist(countryDTO)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;

    }

    @PostMapping("/create")
    private HttpStatus create(@RequestBody CountryDTO countryDTO) {

        if (Boolean.TRUE.equals(countryService.create(countryDTO)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;
    }

    @DeleteMapping("/delete")
    private HttpStatus delete(@RequestParam(name = "id") Long id) {
        if (Boolean.TRUE.equals(countryService.delete(id)))
            return HttpStatus.OK;
        return HttpStatus.NOT_FOUND;
    }

    @PutMapping("/add_language")
    public HttpStatus addLanguages(@RequestBody CountryDTO countryDTO) {
        if (Boolean.TRUE.equals(countryService.modifyLanguage(countryDTO, false)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;
    }

    @PutMapping("/delete_language")
    public HttpStatus deleteLanguages(@RequestBody CountryDTO countryDTO) {
        if (Boolean.TRUE.equals(countryService.modifyLanguage(countryDTO, true)))
            return HttpStatus.OK;
        return HttpStatus.BAD_REQUEST;
    }
}
