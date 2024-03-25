package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.LanguageDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements DataService<Language> {
    private final LanguageRepository repository;
    private final LRUCache<Long, Language> cache;

    @Override
    public boolean create(Language language) {
        if (getByID(language.getId()) == null) {
            repository.save(language);
            cache.put(language.getId(), language);
            return true;
        }
        return false;
    }

    @Override
    public List<Language> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public Language getByName(String name) {
        var optionalLanguage = repository.getByName(name);
        optionalLanguage.ifPresent(language -> cache.put(language.getId(), language));
        return optionalLanguage.orElse(null);
    }

    @Override
    public Language getByID(Long id) {
        var optionalLanguage = cache.get(id);
        if (optionalLanguage.isEmpty())
            optionalLanguage = repository.getLanguageById(id);
        optionalLanguage.ifPresent(language -> cache.put(id, language));
        return optionalLanguage.orElse(null);
    }

    @Override
    public boolean update(Language language) {
        if (getByID(language.getId()) != null) {
            cache.remove(language.getId());
            repository.save(language);
            cache.put(language.getId(), language);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        Language language;
        if ((language = getByID(id)) != null) {
            List<Country> existingCountries = language.getCountries();
            for (Country country : existingCountries)
                country.removeLanguage(language);
            repository.delete(language);
            cache.remove(id);
            return true;
        }
        return false;
    }

    public boolean update(LanguageDTO language) {
        if (language.getId() == null)
            return false;
        return update(Language.builder().name(language.getName()).countries(new ArrayList<>()).id(language.getId()).build());
    }

    public boolean create(LanguageDTO language) {
        if (repository.getByName(language.getName()).isEmpty()) {
            return create(Language.builder().name(language.getName()).countries(new ArrayList<>()).build());
        }
        return false;
    }

}
