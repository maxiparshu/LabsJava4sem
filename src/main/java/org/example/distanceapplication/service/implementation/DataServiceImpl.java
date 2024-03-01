package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;

import org.example.distanceapplication.entity.CityInfo;
import org.example.distanceapplication.repository.CityRepository;
import org.example.distanceapplication.service.DataService;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class DataServiceImpl implements DataService {
    private final CityRepository repository;

    @Override
    public List<CityInfo> getAll() {
        return repository.findAll();
    }
    @Override
    public CityInfo getCityInfoByName(String name) {
        var optionalCity = repository.getCityInfoByName(name);
        return optionalCity.orElse(null);
    }
    @Override
    public CityInfo addNewCity(CityInfo city) {
        return repository.save(city);
    }
}
