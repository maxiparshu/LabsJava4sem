package org.example.distanceapplication.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.service.DistanceService;
import org.example.distanceapplication.service.implementation.CityServiceImpl;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityControllerTest {
  @Mock
  private CityServiceImpl cityService;
  @Mock
  private DistanceService distanceService;
  @Mock
  private CountryServiceImpl countryService;
  @InjectMocks
  private CityController cityController;

  @Test
  void shouldReturnOkAll() {
    ResponseEntity<List<City>> responseEntity = cityController.getAllCity();

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldFindById() throws ResourceNotFoundException {
    Long id = 1L;
    var mockCity = new City();
    when(cityService.getByID(id))
        .thenReturn(mockCity);
    ResponseEntity<City> responseEntity = cityController
        .getCityInfoById(id);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldNotFindById() throws ResourceNotFoundException {
    Long id = 1L;
    when(cityService.getByID(id))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class,
        () -> cityController.getCityInfoById(id));
  }

  @Test
  void shouldFindByInfo() throws ResourceNotFoundException {
    String name = "Minsk";
    var mockCity = new City();
    when(cityService.getByName(name))
        .thenReturn(mockCity);
    ResponseEntity<City> responseEntity = cityController
        .getCityInfo(name);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }

  @Test
  void shouldNotFindByInfo() throws ResourceNotFoundException {
    String name = "Minsk";
    when(cityService.getByName(name))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> cityController.getCityInfo(name));
  }

  @Test
  void shouldAddCity()
      throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .build();
    String countryName = "Belarus";
    Country country = new Country();
    City createdCity = new City();
    when(cityService.createWithCountry(cityDTO, country)).thenReturn(createdCity);
    when(countryService.getByName(countryName)).thenReturn(country);
    var responseEntity = cityController.create(cityDTO, countryName);
    assertEquals(HttpStatus.OK, responseEntity);
  }
  @Test
  void shouldNotAddCity() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .build();
    String countryName = "Belarus";
    Country country = new Country();
    when(cityService.createWithCountry(cityDTO, country)).thenThrow(BadRequestException.class);
    when(countryService.getByName(countryName)).thenReturn(country);
    assertThrows(BadRequestException.class, () -> cityController.create(cityDTO, countryName));
  }
  @Test
  void shouldNotAddCityNotExistedCountry() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .build();
    String countryName = "Belarus";
    when(countryService.getByName(countryName)).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> cityController.create(cityDTO, countryName));
  }

  @Test
  void shouldDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    HttpStatus httpStatus = cityController.delete(id);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldNotDeleteWithId() throws ResourceNotFoundException {
    Long id = 1L;
    doThrow(ResourceNotFoundException.class).when(cityService)
        .delete(id);
    assertThrows(ResourceNotFoundException.class, () -> cityController.delete(id));
  }

  @Test
  void shouldUpdateWithCountry() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .id(1542L)
        .build();
    String countryName = "Belarus";
    Country country = Country.builder().name(countryName).build();
    when(countryService.getByName(countryName)).thenReturn(country);
    Mockito.doNothing().when(cityService).updateWithCountry(cityDTO, country);
    var httpStatus = cityController.update(cityDTO, countryName);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldUpdateWithNotExistedCountry() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .id(1542L)
        .build();
    String countryName = "Belarus";
    when(countryService.getByName(countryName)).thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class, () -> cityController.update(cityDTO, countryName));
  }

  @Test
  void shouldNotUpdateWithCountry() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .name("Minsk")
        .latitude(22.4567)
        .longitude(23.2134)
        .id(1542L)
        .build();
    String countryName = "Belarus";
    Country country = Country.builder().name(countryName).build();
    when(countryService.getByName(countryName)).thenReturn(country);
    doThrow(ResourceNotFoundException.class).when(cityService).updateWithCountry(cityDTO, country);
    assertThrows(ResourceNotFoundException.class, () -> cityController.update(cityDTO, countryName));
  }

  @Test
  void shouldUpdate() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .id(1542L)
        .name("Minskk")
        .build();
    HttpStatus httpStatus = cityController.update(cityDTO);
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldNotUpdate() throws ResourceNotFoundException {
    CityDTO cityDTO = CityDTO.builder()
        .id(1542L)
        .name("Minskk")
        .build();
    doThrow(ResourceNotFoundException.class).when(cityService).update(cityDTO);
    assertThrows(ResourceNotFoundException.class, () -> cityController.update(cityDTO));
  }

  @Test
  void bulkInsert() {
    HttpStatus httpStatus = cityController
        .bulkCreate(new ArrayList<>());
    assertEquals(HttpStatus.OK, httpStatus);
  }

  @Test
  void shouldCalculateDistance() throws ResourceNotFoundException {
    var firstCityInfo = new City();
    var secondCityInfo = new City();
    when(cityService.getByName("Minsk")).thenReturn(firstCityInfo);
    when(cityService.getByName("Pinsk")).thenReturn(secondCityInfo);
    when(distanceService.getDistanceInKilometres(firstCityInfo,
        secondCityInfo)).thenReturn(221.2667);
    var responseEntity = cityController.getDistance("Minsk", "Pinsk");
    assertEquals("221.2667", Objects.requireNonNull(responseEntity.getBody()).get("Distance"));
  }
  @Test
  void shouldChangeStatusCalculateDistance()
      throws ResourceNotFoundException {
    var firstCityInfo = new City();
    var secondCityInfo = new City();
    when(cityService.getByName("Minsk")).thenReturn(firstCityInfo);
    when(cityService.getByName("Pinsk")).thenReturn(secondCityInfo);
    when(distanceService.getDistanceInKilometres(firstCityInfo,
        secondCityInfo)).thenReturn(-1d);
    var responseEntity = cityController.getDistance("Minsk", "Pinsk");
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  void shouldNotCalculateDistance() throws ResourceNotFoundException {
    when(cityService.getByName(anyString()))
        .thenThrow(ResourceNotFoundException.class);
    assertThrows(ResourceNotFoundException.class
        , () -> cityController.getDistance("Minsk", "Pinsk"));
  }

  @Test
  void shouldFindCitiesBetweenLatitudes() {
    Double first = 10D;
    Double second = 20D;
    cityController.getCitiesBetween(first, second);
    verify(cityService, times(1))
        .getBetweenLatitudes(first, second);
  }
  @Test
  void shouldFindCitiesBetweenLatitudesFirstBigger() {
    Double first = 20D;
    Double second = 10D;
    cityController.getCitiesBetween(first, second);
    verify(cityService, times(1))
        .getBetweenLatitudes(second, first);
  }
}
