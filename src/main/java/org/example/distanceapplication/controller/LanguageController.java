package org.example.distanceapplication.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.aspect.AspectAnnotation;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.LanguageServiceImpl;
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

@Tag(name = "LanguageController")
@RestController
@RequestMapping("/api/languages")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://lab7-front.vercel.app"})
public class LanguageController {
  private final LanguageServiceImpl languageService;

  @GetMapping(value = "/all", produces = "application/json")
  public List<Language> getAll() {
    return languageService.read();
  }

  @AspectAnnotation
  @GetMapping(value = "/info", produces = "application/json")
  public ResponseEntity<Language> getLanguage(
      final @RequestParam(name = "language") String name)
      throws ResourceNotFoundException {
    var language = languageService.getByName(name);
    return new ResponseEntity<>(language, HttpStatus.OK);
  }

  @AspectAnnotation
  @GetMapping(value = "/find", produces = "application/json")
  public ResponseEntity<Language> getLanguageById(
      final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    var language = languageService.getByID(id);
    return new ResponseEntity<>(language, HttpStatus.OK);
  }

  @AspectAnnotation
  @PostMapping("/create")
  public HttpStatus addLanguage(final @RequestBody LanguageDTO language)
      throws BadRequestException {
    languageService.create(language);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @DeleteMapping("/delete")
  public HttpStatus deleteLanguage(final @RequestParam(name = "id") Long id)
      throws ResourceNotFoundException {
    languageService.delete(id);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PutMapping("/update")
  public HttpStatus update(final @RequestBody LanguageDTO language)
      throws ResourceNotFoundException {
    languageService.update(language);
    return HttpStatus.OK;
  }

  @AspectAnnotation
  @PostMapping("/bulkCreate")
  public HttpStatus bulkCreate(
      @RequestBody final LanguageDTO[] languageDTOS) {
    Arrays.stream(languageDTOS).forEach(languageService::create);
    return HttpStatus.OK;
  }
}
