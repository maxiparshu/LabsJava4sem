package org.example.distanceapplication.service.implementation;

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

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements DataService<Country> {
    private final CountryRepository countryRepository;
    private final LanguageRepository languageRepository;

    private final LRUCache<Long, Country> cache;
    private static final String DONT_EXIST = " doesn't exist";

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
    public void create(Country country) throws BadRequestException {
        try {
            getByID(country.getId());
            throw new BadRequestException("Can't create country with this id = "
                    + country.getId() + " already exist");
        } catch (ResourceNotFoundException e) {
            cache.put(country.getId(), country);
            countryRepository.save(country);

        }
    }

    @Override
    public List<Country> read() {
        return countryRepository.findAll(Sort.by("id"));
    }

    @Override
    public Country getByName(String name) throws ResourceNotFoundException {
        var optionalCountry = countryRepository.getByName(name);
        if (optionalCountry.isPresent()) {
            cache.put(optionalCountry.get().getId(), optionalCountry.get());
        } else throw new ResourceNotFoundException("Can't find country because with this name doesn't exist");
        return optionalCountry.get();
    }


    @Override
    public Country getByID(Long id) throws ResourceNotFoundException {
        var optionalCountry = cache.get(id);
        if (optionalCountry.isEmpty()) {
            optionalCountry = countryRepository.findById(id);
            if (optionalCountry.isPresent()) {
                cache.put(id, optionalCountry.get());
            } else throw new ResourceNotFoundException("Can't create country with this id = "
                    + id + " already exist");
        }
        return optionalCountry.get();
    }

    @Override
    public void update(Country country) throws ResourceNotFoundException {
        try {
            getByID(country.getId());
            cache.remove(country.getId());
            countryRepository.save(country);
            cache.put(country.getId(), country);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Can't update country with this id"
                    + country.getId() + DONT_EXIST);
        }
    }

    @Override
    public void delete(Long id)
            throws ResourceNotFoundException {
        if (getByID(id) != null) {
            countryRepository.deleteById(id);
            cache.remove(id);
        } else throw new ResourceNotFoundException("Can't delete country with this id = "
                + id + DONT_EXIST);
    }

    public void create(CountryDTO countryDTO) throws BadRequestException {
        var listLanguage = new HashSet<Language>();
        for (String ptrLanguage : countryDTO.getLanguages()) {
            var language = languageRepository.getByName(ptrLanguage);
            language.ifPresent(listLanguage::add);
        }
        var newCountry = Country.builder().name(countryDTO.getName()).languages(new HashSet<>()).id(findFreeID()).build();
        for (Language language : listLanguage) {
            newCountry.addLanguage(language);
        }
        create(newCountry);
    }

    public void updateWithExist(CountryDTO country) throws ResourceNotFoundException {
        if (countryRepository.getCountryById(country.getId()).isEmpty())
            throw new ResourceNotFoundException("Country with this id doesn't exist");
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
        update(updatedCountry);
    }

    public void modifyLanguage(CountryDTO countryDTO, boolean deleteFlag)
            throws ResourceNotFoundException {
        var country = getByID(countryDTO.getId());
        if (country != null) {
            for (String language : countryDTO.getLanguages()) {
                var tempLanguage = languageRepository.getByName(language);
                tempLanguage.ifPresent(!deleteFlag ? country::addLanguage : country::removeLanguage);
            }
        }
        assert country != null;
        update(country);
    }

    public List<Country> getByLanguage(Long id)
            throws ResourceNotFoundException {
        var optionalLanguage = languageRepository.getLanguageById(id);
        if (optionalLanguage.isEmpty())
            throw new ResourceNotFoundException("No language with this name");
        return countryRepository.findAllCountryWithLanguage(id);
    }
}
