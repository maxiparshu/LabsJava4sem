package org.example.distanceapplication.controller;

import java.util.List;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.LanguageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageControllerTest {
  @Mock
  private LanguageServiceImpl service;
  @InjectMocks
  private LanguageController languageController;

  @Test
  void shouldReturnAll() {
    languageController.getAll();
    verify(service, times(1)).read();
  }

  @Test
  void shouldFindById() throws ResourceNotFoundException {
    Long id = 2L;
    var mockLanguage = new Language();
    when(service.getByID(id))
        .thenReturn(mockLanguage);
    var responseEntity = languageController.getLanguageById(id);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldNotFindById() throws ResourceNotFoundException {
    Long id = 2L;
    when(service.getByID(id))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> languageController.getLanguageById(id));
  }

  @Test
  void shouldFindByName() throws ResourceNotFoundException {
    String name = "French";
    var mockCountry = new Language();
    when(service.getByName(name))
        .thenReturn(mockCountry);
    var responseEntity = languageController.getLanguage(name);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldNotFindByName() throws ResourceNotFoundException {
    String name = "French";
    when(service.getByName(name))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> languageController.getLanguage(name));
  }

  @Test
  void shouldCreate() {
    var language = LanguageDTO.builder()
        .name("Hello").build();
    var status = languageController.addLanguage(language);
    assertEquals(HttpStatus.OK, status);
  }

  @Test
  void shouldNotCreate()
      throws BadRequestException {
    var languageDTO = LanguageDTO.builder()
        .name("French").build();
    doThrow(BadRequestException.class).when(service).create(languageDTO);
    assertThrows(BadRequestException.class, () -> languageController.addLanguage(languageDTO));
  }

  @Test
  void shouldDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    HttpStatus httpStatus = languageController.deleteLanguage(id);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldNotDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    doThrow(ResourceNotFoundException.class).when(service).delete(id);
    assertThrows(ResourceNotFoundException.class, () -> languageController.deleteLanguage(id));

  }

  @Test
  void shouldUpdateLanguage() throws ResourceNotFoundException {
    var language = LanguageDTO.builder()
        .id(26L)
        .name("String")
        .build();
    HttpStatus httpStatus = languageController.update(language);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldNotUpdateLanguage() throws ResourceNotFoundException {
    var language = LanguageDTO.builder()
        .id(26L)
        .name("String")
        .build();
    doThrow(ResourceNotFoundException.class).when(service).update(language);
    assertThrows(ResourceNotFoundException.class,
        () -> languageController.update(language));
  }

  @Test
  void bulkInsert() {
    var language = LanguageDTO.builder()
        .name("test")
        .id(2L).build();
    var languages = List.of(language).toArray(new LanguageDTO[1]);
    HttpStatus httpStatus = languageController.bulkCreate(languages);
    verify(service, times(1)).create(language);
    assertEquals(HttpStatus.OK, httpStatus);
  }
}
