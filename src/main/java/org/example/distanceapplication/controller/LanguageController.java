package org.example.distanceapplication.controller;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.LanguageServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Language> getLanguageById(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        var language = languageService.getByID(id);
        return new ResponseEntity<>(language, HttpStatus.OK);
    }

    @PostMapping("/create")
    HttpStatus addLanguage(@RequestBody LanguageDTO language)
            throws BadRequestException {
        languageService.create(language);
        return HttpStatus.OK;
    }

    @DeleteMapping("/delete")
    HttpStatus deleteLanguage(@RequestParam(name = "id") Long id)
            throws ResourceNotFoundException {
        languageService.delete(id);
        return HttpStatus.OK;
    }

    @PutMapping("/update")
    HttpStatus update(@RequestBody LanguageDTO language)
            throws ResourceNotFoundException {
        languageService.update(language);
        return HttpStatus.OK;
    }
}
