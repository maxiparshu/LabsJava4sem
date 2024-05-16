package org.example.distanceapplication.service_implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.implementation.CountryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryServiceImplTest {
  @Mock
  private CountryRepository countryRepository;
  @Mock
  private LanguageRepository languageRepository;
  @Mock
  private LRUCache<Long, Country> cache;
  @InjectMocks
  private CountryServiceImpl service;

  @Test
  public void shouldReturnAllCountry() {
    List<Country> expectedCountry = new ArrayList<>();
    when(countryRepository.findAll(Sort.by("id")))
        .thenReturn(expectedCountry);
    var actualCities = service.read();
    assertEquals(expectedCountry, actualCities);
  }

  @Test
  public void findCityByIdNotInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    var expectedCountry = Optional.of(new Country());
    when(cache.get(id)).thenReturn(Optional.empty());
    when(countryRepository.getCountryById(id)).thenReturn(expectedCountry);
    Country actualCountry = service.getByID(id);
    assertEquals(expectedCountry.get(), actualCountry);
    verify(cache, times(1))
        .put(id, expectedCountry.get());
  }

  @Test
  public void findCountryByIdNotExist() {
    Long id = 22L;
    when(cache.get(id)).thenReturn(Optional.empty());
    when(countryRepository.getCountryById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByID(id));
    verify(countryRepository, never()).findById(anyLong());
    verify(cache, never())
        .put(anyLong(), any(Country.class));
  }

  @Test
  public void findCountryByIdInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    var expectedCountry = Optional.of(new Country());
    when(cache.get(id)).thenReturn(expectedCountry);
    var actualCity = Optional.of(service.getByID(id));
    assertEquals(expectedCountry, actualCity);
    verify(countryRepository, never()).findById(anyLong());
  }

  @Test
  public void findCountryByNameNotInCache()
      throws ResourceNotFoundException {
    String name = "Japan";
    var expectedCountry = Optional.of(new Country());
    when(countryRepository.getByName(name)).thenReturn(expectedCountry);
    var actualCountry = service.getByName(name);
    assertEquals(expectedCountry.get(), actualCountry);
    verify(cache, times(1)).put(expectedCountry.get().getId(), expectedCountry.get());
  }

  @Test
  public void findCountryByNameNotExist() {
    String name = "Japan";
    when(countryRepository.getByName(name)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByName(name));
    verify(cache, never())
        .put(anyLong(), any(Country.class));
  }

  @Test
  public void createCountrySuccessful() {
    var newCountry = Country.builder()
        .name("Belarus")
        .id(22L).build();
    var actualCountry = service.create(newCountry);
    assertEquals(actualCountry, newCountry);
    verify(countryRepository, times(1)).save(newCountry);
    verify(cache, times(1))
        .put(newCountry.getId(), newCountry);
  }

  @Test
  public void createCountryByDtoSuccessful() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .build();
    when(countryRepository.getByName(newCountry.getName()))
        .thenReturn(Optional.empty());
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    when(countryRepository.findAll(Sort.by("id")))
        .thenReturn(new ArrayList<>());
    var createdCountry = service.create(newCountry);
    assertEquals(createdCountry.getId(), 1);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .put(anyLong(), any(Country.class));
  }

  @Test
  public void createCountryByDtoExistedId() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .id(22L).build();
    when(countryRepository.getByName(newCountry.getName()))
        .thenReturn(Optional.of(new Country()));
    assertThrows(BadRequestException.class, () -> service.create(newCountry));
    verify(countryRepository, never())
        .save(any(Country.class));
    verify(cache, times(1))
        .remove(anyLong());
  }

  @Test
  public void updateCountryByEntity() {
    var newCountry = Country.builder()
        .name("Test")
        .id(22L).build();
    service.update(newCountry);
    verify(countryRepository, times(1)).save(newCountry);
    verify(cache, times(1))
        .remove(newCountry.getId());
  }

  @Test
  public void updateCountryByDto() throws ResourceNotFoundException {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .build();
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.of(new Country()));
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    service.update(newCountry);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .remove(newCountry.getId());
  }

  @Test
  public void updateCountryByDtoWithNotExistedId() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .id(22L).build();
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.update(newCountry));
    verify(countryRepository, never())
        .save(any(Country.class));
    verify(cache, never())
        .remove(anyLong());
  }

  @Test
  public void deleteCountryValidId() throws ResourceNotFoundException {
    Long id = 22L;
    when(countryRepository.getCountryById(id))
        .thenReturn(Optional.of(new Country()));
    when(cache.get(anyLong()))
        .thenReturn(Optional.empty());
    service.delete(id);
    verify(cache, times(1))
        .remove(id);
    verify(countryRepository, times(1))
        .deleteById(id);
  }

  @Test
  public void deleteCountryInvalidId() {
    Long id = 22L;
    when(countryRepository.getCountryById(id))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    verify(countryRepository, never())
        .deleteById(id);
    verify(cache, never())
        .remove(id);
  }

  @Test
  public void deleteLanguageButNotExistedId() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .id(22L).build();
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class
        , () -> service.modifyLanguage(newCountry, true));
    verify(cache, never())
        .put(anyLong(), any(Country.class));
    verify(countryRepository, never())
        .save(any(Country.class));
    verify(languageRepository, never())
        .getByName(anyString());
  }

  @Test
  public void createLanguageButNotExistedId() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .id(22L).build();
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class
        , () -> service.modifyLanguage(newCountry, true));
    verify(cache, never())
        .put(anyLong(), any(Country.class));
    verify(countryRepository, never())
        .save(any(Country.class));
    verify(languageRepository, never())
        .getByName(anyString());
  }

  @Test
  public void deleteLanguageInCountry() throws ResourceNotFoundException {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .id(22L).build();
    var existedCountry = Country.builder()
        .name("Belarus")
        .id(22L).build();
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.of(existedCountry));
    service.modifyLanguage(newCountry, true);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .remove(newCountry.getId());
  }

  @Test
  public void createLanguageInCountry() throws ResourceNotFoundException {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .id(22L).build();
    var existedCountry = Country.builder()
        .name("Belarus")
        .id(22L).build();
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.of(existedCountry));
    service.modifyLanguage(newCountry, false);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .remove(newCountry.getId());
  }

  @Test
  public void getByLanguage() throws ResourceNotFoundException {
    Long id = 22L;
    when(languageRepository.getLanguageById(id))
        .thenReturn(Optional.of(Language.builder().name("English")
            .id(id).build()));
    when(countryRepository.findAllCountryWithLanguage(id))
        .thenReturn(new ArrayList<>());
    assertNotEquals(null, service.getByLanguage(id));
  }

  @Test
  public void getByNotExistedLanguage() {
    Long id = 22L;
    when(languageRepository.getLanguageById(id))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByLanguage(id));
    verify(countryRepository, never())
        .findAllCountryWithLanguage(id);
  }

  @Test
  public void notEmptyRepositoryCreate() {
    var country = CountryDTO.builder()
        .name("Tokyko")
        .languages(List.of("Japan"))
        .build();
    var list = Arrays.asList(Country.builder().id(1L).build()
        , Country.builder().id(2L).build());
    when(countryRepository.findAll(Sort.by("id")))
        .thenReturn(list);
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    var createdCountry = service.create(country);
    assertEquals(createdCountry.getId(), 3);
    assertEquals(createdCountry.getName(), country.getName());
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1)).put(anyLong(), any(Country.class));
  }
  @Test
  public void notEmptyRepositoryWithGapCreate() {
    var country = CountryDTO.builder()
        .name("Tokyko")
        .languages(List.of("Japan"))
        .build();
    var list = Arrays.asList(Country.builder().id(1L).build()
        , Country.builder().id(3L).build());
    when(countryRepository.findAll(Sort.by("id")))
        .thenReturn(list);
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.empty());
    var createdCountry = service.create(country);
    assertEquals(createdCountry.getId(), 2);
    assertEquals(createdCountry.getName(), country.getName());
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1)).put(anyLong(), any(Country.class));
  }
  @Test
  public void updateCountryByDtoWithLanguage() throws ResourceNotFoundException {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .build();
    var language = Language.builder()
            .name("English").build();
    when(countryRepository.getCountryById(newCountry.getId()))
        .thenReturn(Optional.of(new Country()));
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.of(language));
    service.update(newCountry);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .remove(newCountry.getId());
  }
  @Test
  public void createCountryByDtoWithLanguage() {
    var newCountry = CountryDTO.builder()
        .name("Belarus")
        .languages(List.of("English"))
        .build();
    var language = Language.builder()
        .name("English").build();
    when(countryRepository.getByName(newCountry.getName()))
        .thenReturn(Optional.empty());
    when(languageRepository.getByName(anyString()))
        .thenReturn(Optional.of(language));
    when(countryRepository.findAll(Sort.by("id")))
        .thenReturn(new ArrayList<>());
    var createdCountry = service.create(newCountry);
    assertTrue(createdCountry.getLanguages().contains(language));
    assertEquals(createdCountry.getId(), 1);
    verify(countryRepository, times(1))
        .save(any(Country.class));
    verify(cache, times(1))
        .put(anyLong(), any(Country.class));
  }
}
