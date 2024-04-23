package org.example.distanceapplication.service_implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.CityRepository;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.service.implementation.CityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityServiceImplTest {
  @Mock
  private CityRepository repository;
  @Mock
  private CountryRepository countryRepository;
  @Mock
  private LRUCache<Long, City> cache;
  @Mock
  private JdbcTemplate jdbcTemplate;
  @InjectMocks
  private CityServiceImpl service;

  @Test
  public void returnAllCity() {
    List<City> expectedCities = new ArrayList<>();
    when(repository.findAll(Sort.by("id")))
        .thenReturn(expectedCities);
    List<City> actualCities = service.read();
    assertEquals(expectedCities, actualCities);
  }


  @Test
  public void createCityWithCountrySuccess() {
    CityDTO city = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .build();
    when(repository.findAll(Sort.by("id")))
        .thenReturn(new ArrayList<>());
    var createdCity = service.createWithCountry(city, Country.builder().name("Japan").id(103L).build());
    assertEquals(createdCity.getId(), 1);
    assertEquals(createdCity.getName(), city.getName());
    assertEquals(createdCity.getLatitude(), city.getLatitude());
    assertEquals(createdCity.getLongitude(), city.getLongitude());
    assertEquals(createdCity.getCountry().getName(), "Japan");
    verify(repository, times(1)).save(any(City.class));
    verify(cache, times(1)).put(anyLong(), any(City.class));
  }

  @Test
  public void createCityWithCountryWrongName() {
    CityDTO city = CityDTO.builder()
        .name("Tokyo")
        .latitude(13.4543)
        .longitude(12.4445)
        .build();
    var country = Country.builder().name("Japan").id(103L).build();
    when(repository.getCityByName("Tokyo")).thenReturn(Optional.of(new City()));
    assertThrows(BadRequestException.class, () -> service.createWithCountry(city, country));
  }

  @Test
  public void findCityByIdNotInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    Optional<City> expectedCity = Optional.of(new City());
    when(cache.get(id)).thenReturn(Optional.empty());
    when(repository.getCityById(id)).thenReturn(expectedCity);
    City actualCity = service.getByID(id);
    assertEquals(expectedCity.get(), actualCity);
    verify(cache, times(1))
        .put(id, expectedCity.get());
  }

  @Test
  public void findCityByIdNotExist() {
    Long id = 22L;
    when(cache.get(id)).thenReturn(Optional.empty());
    when(repository.getCityById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByID(id));
    verify(cache, never())
        .put(anyLong(), any(City.class));
  }

  @Test
  public void findCityByIdInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    Optional<City> expectedCity = Optional.of(new City());
    when(cache.get(id)).thenReturn(expectedCity);
    var actualCity = Optional.of(service.getByID(id));
    assertEquals(expectedCity, actualCity);
    verify(repository, never()).findById(anyLong());
  }

  @Test
  public void findCityByNameNotInCache()
      throws ResourceNotFoundException {
    String name = "Tokyo";
    Optional<City> expectedCity = Optional.of(new City());
    when(repository.getCityByName(name)).thenReturn(expectedCity);
    City actualCity = service.getByName(name);
    assertEquals(expectedCity.get(), actualCity);
    verify(cache, times(1)).put(expectedCity.get().getId(), expectedCity.get());
  }

  @Test
  public void findCityByNameNotExist() {
    String name = "Tokyo";
    when(repository.getCityByName(name)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByName(name));
    verify(cache, never()).put(anyLong(), any(City.class));
  }

  @Test
  public void createCity() {
    var newCity = City.builder()
        .name("Tokyo").build();
    var createdCity = service.create(newCity);
    assertEquals(createdCity.getName(), newCity.getName());
    verify(cache, times(1))
        .put(newCity.getId(), newCity);
    verify(repository, times(1)).save(newCity);
  }

  @Test
  public void updateCityWithCountry() throws ResourceNotFoundException {
    CityDTO city = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    var expectedCity = City.builder()
        .name("Tokyo")
        .id(666L)
        .build();
    var country = Country.builder().name("Japan").id(103L).build();
    when(repository.getCityById(city.getId()))
        .thenReturn(Optional.of(expectedCity));
    service.updateWithCountry(city, country);
    assertEquals(expectedCity.getCountry().getName(), country.getName());
    assertEquals(expectedCity.getName(), city.getName());
    assertEquals(expectedCity.getLatitude(), city.getLatitude());
    assertEquals(expectedCity.getLongitude(), city.getLongitude());
    verify(cache, times(1)).remove(city.getId());
    verify(repository).save(expectedCity);
  }

  @Test
  public void updateCityWithCountryInvalidId() {
    var city = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    var country = Country.builder().name("Japan").id(103L).build();
    when(repository.getCityById(city.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.updateWithCountry(city, country));
    verify(cache, never()).remove(city.getId());
    verify(cache, never())
        .put(anyLong(), any(City.class));
    verify(repository, never()).save(any(City.class));
  }

  @Test
  public void updateCity() throws ResourceNotFoundException {
    var newCity = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    var expectedCity = City.builder()
        .id(666L)
        .build();
    when(cache.get(newCity.getId())).thenReturn(Optional.empty());
    when(repository.getCityById(newCity.getId()))
        .thenReturn(Optional.of(expectedCity));
    service.update(newCity);
    assertEquals(expectedCity.getName(), newCity.getName());
    assertEquals(expectedCity.getLatitude(), newCity.getLatitude());
    assertEquals(expectedCity.getLongitude(), newCity.getLongitude());
    verify(cache, times(1))
        .remove(newCity.getId());
    verify(repository, times(1)).save(expectedCity);
  }

  @Test
  public void updateCityInCache() throws ResourceNotFoundException {
    CityDTO newCity = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    var expectedCity = City.builder()
        .id(666L)
        .build();
    when(cache.get(newCity.getId())).thenReturn(Optional.of(expectedCity));
    service.update(newCity);
    assertEquals(expectedCity.getName(), newCity.getName());
    assertEquals(expectedCity.getLatitude(), newCity.getLatitude());
    assertEquals(expectedCity.getLongitude(), newCity.getLongitude());
    verify(cache, times(1))
        .remove(newCity.getId());
    verify(repository, times(1)).save(expectedCity);
  }

  @Test
  public void updateCityInvalidId() {
    CityDTO newCity = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    when(cache.get(newCity.getId())).thenReturn(Optional.empty());
    when(repository.getCityById(newCity.getId())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.update(newCity));

  }

  @Test
  public void shouldUpdateCity() throws ResourceNotFoundException {
    var newCity = City.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .id(666L)
        .build();
    service.update(newCity);
    verify(cache, times(1))
        .remove(newCity.getId());
    verify(cache, times(1))
        .put(anyLong(), any(City.class));
    verify(repository, times(1))
        .save(newCity);
  }

  @Test
  public void deleteCityValidId() throws ResourceNotFoundException {
    Long id = 22L;
    when(repository.getCityById(id)).thenReturn(Optional.of(new City()));
    service.delete(id);
    verify(cache, times(1))
        .remove(id);
    verify(repository, times(1))
        .deleteById(id);
  }

  @Test
  public void deleteCityInvalidId() {
    Long id = 22L;
    when(repository.getCityById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    verify(cache, never())
        .remove(id);
    verify(repository, never())
        .deleteById(id);
  }

  @Test
  public void bulkInsert() {
    CityDTO firstCity = CityDTO.builder()
        .name("Minsk")
        .longitude(22.2521)
        .latitude(32.4544)
        .idCountry(1L)
        .build();
    CityDTO secondCity = CityDTO.builder()
        .name("Pinsk")
        .longitude(17.3630)
        .latitude(44.2135)
        .idCountry(1L)
        .build();
    List<CityDTO> cityDTOS = Arrays.asList(firstCity, secondCity);
    Optional<Country> expectedCountry = Optional.of(new Country());
    when(countryRepository.getCountryById(anyLong()))
        .thenReturn(expectedCountry);
    service.createBulk(cityDTOS);
    String sql = "INSERT into city (name, id, latitude, longitude, id_country)"
        + "VALUES (?, ?, ?, ?, ?)";
    verify(jdbcTemplate, times(1))
        .batchUpdate(eq(sql), any(BatchPreparedStatementSetter.class));
  }

  @Test
  public void cityBetweenLatitudes() {
    Double first = 10D;
    Double second = 20D;
    service.getBetweenLatitudes(first, second);
    verify(repository, times(1))
        .findAllCityWithLatitudeBetween(first, second);
  }

  @Test
  public void notEmptyRepositoryCreate() {
    CityDTO city = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .build();
    var list = Arrays.asList(City.builder().id(1L).build()
        , City.builder().id(2L).build());
    when(repository.findAll(Sort.by("id")))
        .thenReturn(list);
    var createdCity = service.createWithCountry(city, Country.builder().name("Japan").id(103L).build());
    assertEquals(createdCity.getId(), 3);
    assertEquals(createdCity.getName(), city.getName());
    assertEquals(createdCity.getLatitude(), city.getLatitude());
    assertEquals(createdCity.getLongitude(), city.getLongitude());
    assertEquals(createdCity.getCountry().getName(), "Japan");
    verify(repository, times(1)).save(any(City.class));
    verify(cache, times(1)).put(anyLong(), any(City.class));
  }
  @Test
  public void notEmptyRepositoryWithGapCreate() {
    CityDTO city = CityDTO.builder()
        .name("Tokyko")
        .latitude(13.4543)
        .longitude(12.4445)
        .build();
    var list = Arrays.asList(City.builder().id(1L).build()
        , City.builder().id(3L).build());
    when(repository.findAll(Sort.by("id")))
        .thenReturn(list);
    var createdCity = service.createWithCountry(city, Country.builder().name("Japan").id(103L).build());
    assertEquals(createdCity.getId(), 2);
    assertEquals(createdCity.getName(), city.getName());
    assertEquals(createdCity.getLatitude(), city.getLatitude());
    assertEquals(createdCity.getLongitude(), city.getLongitude());
    assertEquals(createdCity.getCountry().getName(), "Japan");
    verify(repository, times(1)).save(any(City.class));
    verify(cache, times(1)).put(anyLong(), any(City.class));
  }
}