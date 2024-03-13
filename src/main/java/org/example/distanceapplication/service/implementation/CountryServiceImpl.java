package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.dto.CountryDTO;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements DataService<Country> {
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;

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
        return countryRepository.getByName(name).orElse(null);
    }


    @Override
    public Country getByID(Long id) {
        return countryRepository.getCountryById(id).orElse(null);
    }

    @Override
    public boolean update(Country country) {
        if (getByID(country.getId()) != null) {
            countryRepository.save(country);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (this.getByID(id) != null) {
            countryRepository.deleteById(id);
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
            System.out.println(language.getId() + " " + language.getName());
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
            return this.update(country);
        }
        return false;
    }
}
