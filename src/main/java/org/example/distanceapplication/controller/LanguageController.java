package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.service.implementation.LanguageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@AllArgsConstructor
public class LanguageController {
    private final LanguageServiceImpl languageService;

    @GetMapping(value = "/all", produces = "application/json")
    private ResponseEntity<List<Language>> getAll() {
        return new ResponseEntity<>(languageService.read(), HttpStatus.OK);
    }

    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity<Language> getLanguage(@RequestParam(name = "language") String name) {
        var language = languageService.getByName(name);
        if (language == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(language, HttpStatus.OK);
    }

    @GetMapping(value = "/find", produces = "application/json")
    public ResponseEntity<Language> getCityInfoById(@RequestParam(name = "id") Long id) {
        var language = languageService.getByID(id);
        if (language == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(language, HttpStatus.OK);
    }
}
