package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements DataService<Country> {
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;

    private final LRUCache<Long, Country> cache;

    private long findFreeID() {
        var list = read();
        long i = 1;
        for (Country country : list) {
            if (country.getId() != i) {
                return i;
            }
            i++;
        }
        return i + 1;
    }

    @Override
    public boolean create(Country country) {
        if (getByName(country.getName()) == null) {
            cache.put(country.getId(), country);
            countryRepository.save(country);
            return true;
        }
        return false;
    }

    @Override
    public List<Country> read() {
        return countryRepository.findAll(Sort.by("id"));
    }

    @Override
    public Country getByName(String name) {
        var optionalCountry = countryRepository.getByName(name);
        optionalCountry.ifPresent(country -> cache.put(country.getId(), country));
        return optionalCountry.orElse(null);
    }


    @Override
    public Country getByID(Long id) {
        var optionalCountry = cache.get(id);
        if (optionalCountry.isEmpty())
            optionalCountry = countryRepository.findById(id);
        optionalCountry.ifPresent(country -> cache.put(id, country));
        return optionalCountry.orElse(null);
    }

    @Override
    public boolean update(Country country) {
        if (getByID(country.getId()) != null) {
            cache.remove(country.getId());
            countryRepository.save(country);
            cache.put(country.getId(), country);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (this.getByID(id) != null) {
            countryRepository.deleteById(id);
            cache.remove(id);
            return true;
        }
        return false;
    }

    public boolean create(CountryDTO countryDTO) {
        var listLanguage = new HashSet<Language>();
        for (String ptrLanguage : countryDTO.getLanguages()) {
            var language = languageRepository.getByName(ptrLanguage);
            language.ifPresent(listLanguage::add);
        }
        var newCountry = Country.builder().name(countryDTO.getName()).languages(new HashSet<>()).id(findFreeID()).build();
        for (Language language : listLanguage) {
            newCountry.addLanguage(language);
        }
        return create(newCountry);
    }

    public boolean updateWithExist(CountryDTO country) {
        if (countryRepository.getCountryById(country.getId()).isEmpty())
            return false;
        var newLanguages = new HashSet<Language>();
        for (String language : country.getLanguages()) {
            var languageTemp = languageRepository.getByName(language);
            languageTemp.ifPresent(newLanguages::add);
        }
        var updatedCountry = Country.builder().name(country.getName()).languages(new HashSet<>())
                .id(country.getId()).build();
        for (Language language : newLanguages) {
            updatedCountry.addLanguage(language);
        }
        return update(updatedCountry);
    }

    public boolean modifyLanguage(CountryDTO countryDTO, boolean deleteFlag) {
        var country = this.getByID(countryDTO.getId());
        if (country != null) {
            for (String language : countryDTO.getLanguages()) {
                var tempLanguage = languageRepository.getByName(language);
                tempLanguage.ifPresent(!deleteFlag ? country::addLanguage : country::removeLanguage);
            }
            return update(country);
        }
        return false;
    }

    public List<Country> getByLanguage(Integer languageId) {
        var optionalLanguage = languageRepository.getLanguageById(Long.valueOf(languageId));
        if (optionalLanguage.isEmpty())
            return null;
        return countryRepository.findAllCountryWithLanguage(languageId);
    }
}
