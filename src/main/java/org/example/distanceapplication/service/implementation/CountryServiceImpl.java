package org.example.distanceapplication.service.implementation;

import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements DataService<Country> {
  private final CountryRepository countryRepository;
  private final LanguageRepository languageRepository;
  private final LRUCache<Long, Country> cache;
  private static final String DONT_EXIST = " doesn't exist";
  @Override
  public Country create(final Country country) {
    cache.put(country.getId(), country);
    countryRepository.save(country);
    return country;
  }

  @Override
  public List<Country> read() {
    return countryRepository.findAll(Sort.by("id"));
  }

  @Override
  public Country getByName(final String name)
      throws ResourceNotFoundException {
    var optionalCountry = countryRepository.getByName(name);
    if (optionalCountry.isPresent()) {
      cache.put(optionalCountry.get().getId(), optionalCountry.get());
    } else {
      throw new ResourceNotFoundException(
          "Can't find country because with this name doesn't exist");
    }
    return optionalCountry.get();
  }


  @Override
  public Country getByID(final Long id) throws ResourceNotFoundException {
    var optionalCountry = cache.get(id);
    if (optionalCountry.isEmpty()) {
      optionalCountry = countryRepository.getCountryById(id);
      if (optionalCountry.isPresent()) {
        cache.put(id, optionalCountry.get());
      } else {
        throw new ResourceNotFoundException(
            "Can't create country with id = "
                + id + " already exist");
      }
    }
    return optionalCountry.get();
  }

  @Override
  public void update(final Country country) {
    cache.remove(country.getId());
    countryRepository.save(country);
  }

  @Override
  public void delete(final Long id)
      throws ResourceNotFoundException {
    try {
      getByID(id);
      countryRepository.deleteById(id);
      cache.remove(id);
    } catch (ResourceNotFoundException e) {
      throw new ResourceNotFoundException(
          "Can't delete country with id = " + id + DONT_EXIST);
    }
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public Country create(final CountryDTO countryDto)
      throws BadRequestException {
    try {
      getByName(countryDto.getName());
      cache.remove(countryDto.getId());
      throw new BadRequestException("Country with this name is existed");
    } catch (ResourceNotFoundException e) {
      var listLanguage = new HashSet<Language>();
      if (!countryDto.getLanguages().isEmpty()) {
        for (String ptrLanguage : countryDto.getLanguages()) {
          var language = languageRepository.getByName(ptrLanguage);
          language.ifPresent(listLanguage::add);
        }
      }
      var newCountry = Country.builder().name(countryDto.getName())
          .languages(new HashSet<>()).build();
      for (Language language : listLanguage) {
        newCountry.addLanguage(language);
      }
      return create(newCountry);
    }
  }

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public void update(final CountryDTO country)
      throws ResourceNotFoundException {
    if (countryRepository.getCountryById(country.getId()).isEmpty()) {
      throw new ResourceNotFoundException(
          "Country with this id doesn't exist");
    }
    var newLanguages = new HashSet<Language>();
    if (country.getLanguages() != null) {
      for (String language : country.getLanguages()) {
        var languageTemp = languageRepository.getByName(language);
        languageTemp.ifPresent(newLanguages::add);
      }
    }
    var updatedCountry = Country.builder().name(country.getName())
        .languages(new HashSet<>()).id(country.getId()).build();
    for (Language language : newLanguages) {
      updatedCountry.addLanguage(language);
    }
    update(updatedCountry);
  }


  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public void modifyLanguage(final CountryDTO countryDto,
                             final boolean deleteFlag)
      throws ResourceNotFoundException {
    try {
      var country = getByID(countryDto.getId());
      if (!countryDto.getLanguages().isEmpty()) {
        for (String language : countryDto.getLanguages()) {
          var tempLanguage = languageRepository.getByName(language);
          tempLanguage.ifPresent(!deleteFlag
              ? country::addLanguage : country::removeLanguage);
        }
      }
      cache.remove(country.getId());
      countryRepository.save(country);
    } catch (ResourceNotFoundException e) {
      throw new ResourceNotFoundException("Cant modify language");
    }
  }

  public List<Country> getByLanguage(final Long id)
      throws ResourceNotFoundException {
    var optionalLanguage = languageRepository.getLanguageById(id);
    if (optionalLanguage.isEmpty()) {
      throw new ResourceNotFoundException("No language with this name");
    }
    return countryRepository.findAllCountryWithLanguage(id);
  }
}
