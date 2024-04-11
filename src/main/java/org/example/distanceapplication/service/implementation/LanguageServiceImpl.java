package org.example.distanceapplication.service.implementation;

import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class LanguageServiceImpl implements DataService<Language, LanguageDTO> {
  private final LanguageRepository repository;
  private final LRUCache<Long, Language> cache;
  private final JdbcTemplate jdbcTemplate;
  private static final String DONT_EXIST = " doesn't exist";

  @Override
  public void create(final Language language) {
    try {
      getByID(language.getId());
      throw new BadRequestException("Can't create language with id = "
          + language.getId() + " already exist");
    } catch (ResourceNotFoundException e) {
      repository.save(language);
      cache.put(language.getId(), language);
    }
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
      optionalLanguage = repository.findById(id);
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
      cache.remove(language.getId());
      repository.save(language);
      cache.put(language.getId(), language);
    } catch (ResourceNotFoundException e) {
      throw new ResourceNotFoundException(
          "Can't update language with this id = "
              + language.getId() + DONT_EXIST);
    }
  }

  @Override
  public void delete(final Long id) throws ResourceNotFoundException {
    Language language = getByID(id);
    if (language != null) {
      List<Country> existingCountries = language.getCountries();
      for (Country country : existingCountries) {
        country.removeLanguage(language);
      }
      repository.delete(language);
    } else {
      throw new ResourceNotFoundException(
          "Can't delete language with this id = "
              + id + DONT_EXIST);
    }
  }

  @Transactional
  @Override
  public void createBulk(final List<LanguageDTO> list)
      throws BadRequestException {
    List<Language> languages = list.stream()
        .map(languageDTO -> Language.builder()
            .name(languageDTO.getName()).build()).toList();
    String sql = "INSERT into language (name, id) VALUES (?, ?)";
    var indexes = new HashSet<Long>();
    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(final PreparedStatement statement,
                            final int i)
          throws SQLException {
        long index = findFreeId(indexes);
        indexes.add(index);
        statement.setString(1, languages.get(i).getName());
        statement.setLong(2, index);
      }

      @Override
      public int getBatchSize() {
        return languages.size();
      }
    });
  }
  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public void update(final LanguageDTO language)
      throws ResourceNotFoundException {
    update(Language.builder().name(language.getName())
        .countries(new ArrayList<>()).id(language.getId()).build());
  }

  @SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
  public void create(final LanguageDTO language) throws BadRequestException {
    create(Language.builder().name(language.getName())
        .id(findFreeId()).countries(new ArrayList<>()).build());
  }

  private long findFreeId() {
    var list = read();
    long i = 1;
    for (Language language : list) {
      if (language.getId() != i) {
        return i;
      }
      i++;
    }
    return i + 1;
  }

  private long findFreeId(final HashSet<Long> usedIndexes) {
    var list = read();
    long i = 1;
    for (Language language : list) {
      if (language.getId() != i) {
        if (!usedIndexes.contains(i)) {
          return i;
        } else {
          i = language.getId();
        }
      }
      i++;
    }
    return i + 1;
  }
}
