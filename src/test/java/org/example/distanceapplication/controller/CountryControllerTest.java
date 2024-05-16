package org.example.distanceapplication.controller;

import java.util.ArrayList;
import java.util.List;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryControllerTest {
  @Mock
  private CountryServiceImpl countryService;
  @InjectMocks
  private CountryController countryController;

  @Test
  void shouldReturnAll() {
    countryController.getAll();
    verify(countryService, times(1)).read();
  }

  @Test
  void shouldFindById() throws ResourceNotFoundException {
    Long id = 1L;
    var mockCountry = new Country();
    when(countryService.getByID(id))
        .thenReturn(mockCountry);
    var responseEntity = countryController.getCountryById(id);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
  @Test
  void shouldNotFindById() throws ResourceNotFoundException {
    Long id = 1L;
    when(countryService.getByID(id))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> countryController.getCountryById(id));
  }

  @Test
  void shouldFindByName() throws ResourceNotFoundException {
    String name = "Russia";
    var mockCountry = new Country();
    when(countryService.getByName(name))
        .thenReturn(mockCountry);
    var responseEntity = countryController.getCountry(name);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
  @Test
  void shouldNotFindByName() throws ResourceNotFoundException {
    String name = "Russia";
    when(countryService.getByName(name))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> countryController.getCountry(name));
  }

  @Test
  void shouldCreate() {
    CountryDTO countryDTO = CountryDTO.builder()
        .name("Hello").build();
    var status = countryController.create(countryDTO);
    assertEquals(HttpStatus.OK, status);
  }

  @Test
  void shouldNotCreate()
      throws BadRequestException {
    var country = CountryDTO.builder()
        .name("Belarus").build();
    Mockito.doThrow(BadRequestException.class).when(countryService).create(country);
    assertThrows(BadRequestException.class, () -> countryController.create(country));
  }

  @Test
  void shouldDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    HttpStatus httpStatus = countryController.delete(id);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldNotDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    doThrow(ResourceNotFoundException.class).when(countryService).delete(id);
    assertThrows(ResourceNotFoundException.class,() -> countryController.delete(id));
  }


  @Test
  void shouldUpdateCountry() throws ResourceNotFoundException {
    var country = CountryDTO.builder()
        .id(172L)
        .name("Minskk")
        .build();
    HttpStatus httpStatus = countryController.update(country);
    assertEquals(HttpStatus.OK, httpStatus);
  }
  @Test
  void shouldNotUpdateCountry() throws ResourceNotFoundException {
    var country = CountryDTO.builder()
        .id(172L)
        .name("Minskk")
        .build();
    doThrow(ResourceNotFoundException.class).when(countryService).update(country);
    assertThrows(ResourceNotFoundException.class, () -> countryController.update(country));
  }

  @Test
  void shouldAddLanguages()
      throws ResourceNotFoundException {
    var country = CountryDTO.builder().name("Belarus")
            .languages(new ArrayList<>()).build();
    doNothing().when(countryService).modifyLanguage(country, false);
    var responseEntity = countryController
        .addLanguages(country);
    assertEquals(HttpStatus.OK, responseEntity);
  }
  @Test
  void shouldNotAddLanguages()
      throws ResourceNotFoundException {
    var country = CountryDTO.builder().name("Belarus")
        .languages(new ArrayList<>()).build();
    doThrow(ResourceNotFoundException.class).when(countryService)
        .modifyLanguage(country, true);
    assertThrows(ResourceNotFoundException.class, () ->countryController
        .deleteLanguages(country));
  }

  @Test
  void shouldDeleteLanguages() throws ResourceNotFoundException {
    var country = CountryDTO.builder().name("Belarus")
        .languages(new ArrayList<>()).build();
    doNothing().when(countryService).modifyLanguage(country, true);
    var responseEntity = countryController
        .deleteLanguages(country);
    assertEquals(HttpStatus.OK, responseEntity);
  }
  @Test
  void shouldNotDeleteLanguages() throws ResourceNotFoundException {
    var country = CountryDTO.builder().name("Belarus")
        .languages(new ArrayList<>()).build();
    doThrow(ResourceNotFoundException.class).when(countryService)
        .modifyLanguage(country, true);
    assertThrows(ResourceNotFoundException.class, () ->countryController
        .deleteLanguages(country));
  }
  @Test
  void bulkInsert() {
    var country = CountryDTO.builder()
        .name("test")
        .id(2L).build();
    var countries = List.of(country).toArray(new CountryDTO[1]);
    HttpStatus httpStatus = countryController.bulkCreate(countries);
    verify(countryService, times(1)).create(country);
    assertEquals(HttpStatus.OK, httpStatus);
  }
  @Test
  void getCountriesByLanguage() throws ResourceNotFoundException {
    Long id = 22L;
    when(countryService.getByLanguage(id))
        .thenReturn(new ArrayList<>());
    var responseEntity = countryController.getCountriesByLanguage(id);
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
  }
  @Test
  void getCountriesByLanguageNotFindLanguage() throws ResourceNotFoundException {
    Long id = 22L;
    when(countryService.getByLanguage(id))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> countryController.getCountriesByLanguage(id));
  }
}
