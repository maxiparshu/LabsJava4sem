package org.example.distanceapplication.service.implementation;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class LanguageServiceImpl implements DataService<Language> {
  private final LanguageRepository repository;
  private final LRUCache<Long, Language> cache;
  private static final String DONT_EXIST = " doesn't exist";

  @Override
  public Language create(final Language language) {
    repository.save(language);
    cache.put(language.getId(), language);
    return language;
  }


  @Override
  public List<Language> read() {
    return repository.findAll(Sort.by("id"));
  }

  @Override
  public Language getByName(final String name)
      throws ResourceNotFoundException {
    var optionalLanguage = repository.getByName(name);
    if (optionalLanguage.isPresent()) {
      cache.put(optionalLanguage.get().getId(), optionalLanguage.get());
    } else {
      throw new ResourceNotFoundException(
          "Can't find language because with this name");
    }
    return optionalLanguage.get();
  }

  @Override
  public Language getByID(final Long id) throws ResourceNotFoundException {
    var optionalLanguage = cache.get(id);
    if (optionalLanguage.isEmpty()) {
      optionalLanguage = repository.getLanguageById(id);
      if (optionalLanguage.isPresent()) {
        cache.put(id, optionalLanguage.get());
      } else {
        throw new ResourceNotFoundException(
            "Can't find language with this id = "
                + id + " doesnt exist");
      }
    }
    return optionalLanguage.get();
  }

  @Override
  public void update(final Language language) throws ResourceNotFoundException {
    try {
      getByID(language.getId());
    } catch (ResourceNotFoundException e) {
      throw new ResourceNotFoundException("No info with this id");
    }
    cache.remove(language.getId());
    repository.save(language);
    cache.put(language.getId(), language);
  }

  @Override
  public void delete(final Long id) throws ResourceNotFoundException {
    try {
      Language language = getByID(id);
      List<Country> existingCountries = language.getCountries();
      for (Country country : existingCountries) {
        country.removeLanguage(language);
      }
      cache.remove(id);
      repository.deleteById(language.getId());
    } catch (ResourceNotFoundException e) {
      throw new ResourceNotFoundException(
          "Can't delete language with this id = "
              + id + DONT_EXIST);
    }
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public void update(final LanguageDTO language)
      throws ResourceNotFoundException {
    update(Language.builder().name(language.getName())
        .countries(new ArrayList<>()).id(language.getId()).build());
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public Language create(final LanguageDTO language)
      throws BadRequestException {
    try {
      getByName(language.getName());
      throw new BadRequestException("Language with this id is existed");
    } catch (ResourceNotFoundException e) {
      var newLanguage = Language.builder().name(language.getName())
          .countries(new ArrayList<>()).build();
      return create(newLanguage);
    }
  }


}
