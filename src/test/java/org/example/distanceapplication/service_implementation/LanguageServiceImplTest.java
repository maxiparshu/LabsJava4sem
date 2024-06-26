package org.example.distanceapplication.service_implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.implementation.LanguageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceImplTest {
  @Mock
  private LanguageRepository repository;
  @Mock
  private LRUCache<Long, Language> cache;

  @InjectMocks
  private LanguageServiceImpl service;

  @Test
  public void shouldReturnAllLanguage() {
    List<Language> expectedLanguage = new ArrayList<>();
    when(repository.findAll(Sort.by("id")))
        .thenReturn(expectedLanguage);
    var actualCities = service.read();
    assertEquals(expectedLanguage, actualCities);
  }

  @Test
  public void findLanguageByIdNotInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    var expectedLanguage = Language.builder()
        .name("Russian").id(id).build();
    when(cache.get(id)).thenReturn(Optional.empty());
    when(repository.getLanguageById(id))
        .thenReturn(Optional.of(expectedLanguage));
    Language actualLanguage = service.getByID(id);
    assertEquals(expectedLanguage, actualLanguage);
    verify(cache, times(1))
        .put(id, expectedLanguage);
  }

  @Test
  public void findLanguageByIdNotExist() {
    Long id = 22L;
    when(cache.get(id)).thenReturn(Optional.empty());
    when(repository.getLanguageById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByID(id));
    verify(cache, never()).put(anyLong(), any(Language.class));
  }

  @Test
  public void findLanguageByIdInCache()
      throws ResourceNotFoundException {
    Long id = 22L;
    var expectedLanguage = Optional.of(new Language());
    when(cache.get(id)).thenReturn(expectedLanguage);
    var actualCity = Optional.of(service.getByID(id));
    assertEquals(expectedLanguage, actualCity);
    verify(repository, never()).findById(anyLong());
  }

  @Test
  public void findLanguageByNameNotInCache()
      throws ResourceNotFoundException {
    String name = "English";
    var expectedLanguage = Optional.of(new Language());
    when(repository.getByName(name)).thenReturn(expectedLanguage);
    var actualCountry = service.getByName(name);
    assertEquals(expectedLanguage.get(), actualCountry);
    verify(cache, times(1)).put(expectedLanguage.get().getId(), expectedLanguage.get());
  }

  @Test
  public void findLanguageByNameNotExist() {
    String name = "English";
    when(repository.getByName(name)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByName(name));
    verify(cache, never()).put(anyLong(), any(Language.class));
  }

  @Test
  public void createLanguageSuccessful() {
    var language = Language.builder()
        .name("English")
        .id(22L)
        .build();
    var actualLanguage = service.create(language);
    assertEquals(actualLanguage, language);
    verify(repository, times(1)).save(language);
    verify(cache, times(1))
        .put(anyLong(), any(Language.class));
  }


  @Test
  public void createLanguageByDtoSuccessful() {
    var newLanguage = LanguageDTO.builder()
        .name("Russian")
        .build();
    when(repository.getByName(newLanguage.getName()))
        .thenReturn(Optional.empty());
    var createdLanguage = service.create(newLanguage);
    assertEquals(createdLanguage.getName(), newLanguage.getName());
    verify(repository, times(1))
        .save(any(Language.class));
  }

  @Test
  public void createLanguageByDtoExistedId() {
    var newLanguage = LanguageDTO.builder()
        .name("Russian").build();
    when(repository.getByName(newLanguage.getName()))
        .thenReturn(Optional.of(new Language()));
    assertThrows(BadRequestException.class, () -> service.create(newLanguage));
    verify(repository, never())
        .save(any(Language.class));
    verify(cache, never())
        .put(anyLong(), any(Language.class));
  }

  @Test
  public void updateLanguageByEntity() throws ResourceNotFoundException {
    var newLanguage = Language.builder()
        .name("Test")
        .id(22L).build();
    when(repository.getLanguageById(newLanguage.getId()))
        .thenReturn(Optional.of(new Language()));
    service.update(newLanguage);
    verify(repository, times(1)).save(newLanguage);
    verify(cache, times(1))
        .remove(newLanguage.getId());
    verify(cache, times(1))
        .put(newLanguage.getId(), newLanguage);
  }

  @Test
  public void updateLanguageByNotExistedEntity() {
    var newLanguage = Language.builder()
        .name("Test")
        .id(22L).build();
    when(repository.getLanguageById(newLanguage.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.update(newLanguage));
    verify(repository, never()).save(newLanguage);
    verify(cache, never())
        .remove(anyLong());
    verify(cache, never())
        .put(anyLong(), any(Language.class));
  }

  @Test
  public void updateLanguageByDto() throws ResourceNotFoundException {
    var language = LanguageDTO.builder()
        .name("Belarus")
        .id(22L)
        .build();
    when(repository.getLanguageById(language.getId()))
        .thenReturn(Optional.of(new Language()));
    service.update(language);
    verify(repository, times(1))
        .save(any(Language.class));
    verify(cache, times(1))
        .remove(language.getId());
  }

  @Test
  public void updateLanguageByDtoWithNotExistedId() {
    var newLanguage = LanguageDTO.builder()
        .name("Belarus")
        .id(22L).build();
    when(repository.getLanguageById(newLanguage.getId()))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.update(newLanguage));
    verify(repository, never())
        .save(any(Language.class));
    verify(cache, never())
        .remove(anyLong());
  }

  @Test
  public void deleteLanguageValidId() throws ResourceNotFoundException {
    Long id = 22L;
    var language = Language.builder()
        .name("Russian")
        .countries(new ArrayList<>())
        .id(22L).build();
    var country = mock(Country.class);
    language.setCountries(List.of(country));
    when(cache.get(id)).thenReturn(Optional.empty());
    when(repository.getLanguageById(id))
        .thenReturn(Optional.of(language));
    doAnswer(invocationOnMock -> null).when(country)
            .removeLanguage(any(Language.class));
    service.delete(id);
    verify(cache, times(1))
        .remove(id);
    verify(repository, times(1))
        .deleteById(id);
  }

  @Test
  public void deleteLanguageInvalidId() {
    Long id = 22L;
    when(repository.getLanguageById(id))
        .thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    verify(repository, never()).deleteById(id);
  }

  @Test
  public void createLanguageByDtoNotEmptyList() {
    var newLanguage = LanguageDTO.builder()
        .name("Russian")
        .build();
    when(repository.getByName(newLanguage.getName()))
        .thenReturn(Optional.empty());
    var createdLanguage = service.create(newLanguage);
    assertEquals(createdLanguage.getName(), newLanguage.getName());
    verify(repository, times(1))
        .save(any(Language.class));
  }

  @Test
  public void createLanguageByDtoNotEmptyListWithGap() {
    var newLanguage = LanguageDTO.builder()
        .name("Russian")
        .build();
    when(repository.getByName(newLanguage.getName()))
        .thenReturn(Optional.empty());
    var createdLanguage = service.create(newLanguage);
    assertEquals(createdLanguage.getName(), newLanguage.getName());
    verify(repository, times(1))
        .save(any(Language.class));
  }
}
