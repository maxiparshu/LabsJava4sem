package org.example.distanceapplication.service_implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.entity.City;
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
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
  public void shouldReturnAllCity() {
    List<City> expectedCities = new ArrayList<>();
    when(repository.findAll(Sort.by("id")))
        .thenReturn(expectedCities);
    List<City> actualCities = service.read();
    assertEquals(expectedCities, actualCities);
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


}
