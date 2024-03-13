package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.entity.Language;
import org.example.distanceapplication.repository.LanguageRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements DataService<Language> {
    private final LanguageRepository repository;
    @Override
    public boolean create(Language language) {
        if (getByID(language.getId()) == null) {
            repository.save(language);
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
        return repository.getByName(name).orElse(null);
    }

    @Override
    public Language getByID(Long id) {
        return repository.getLanguageById(id).orElse(null);
    }

    @Override
    public boolean update(Language language) {
        if (getByID(language.getId()) != null) {
            repository.save(language);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (getByID(id) != null) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

}
