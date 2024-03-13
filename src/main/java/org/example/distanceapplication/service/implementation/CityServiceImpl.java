package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;

import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.CityInfo;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.repository.CityRepository;
import org.example.distanceapplication.service.DataService;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CityServiceImpl implements DataService<CityInfo> {
    private final CityRepository repository;

    private long findFreeID() {
        var list = read();
        long i = 1;
        for (CityInfo cityInfo : list) {
            if (cityInfo.getId() != i) {
                return i;
            }
            i++;
        }
        return i + 1;
    }

    public boolean createWithCountry(CityDTO city, Country country) {
        if (getByName(city.getName()) == null) {
            var newCity = CityInfo.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).country(country).id(findFreeID()).build();
            repository.save(newCity);
            return true;
        }
        return false;
    }

    @Override
    public boolean create(CityInfo city) {
        if (getByID(city.getId()) == null) {
            repository.save(city);
            return true;
        }
        return false;
    }

    public boolean updateWithCountry(CityInfo city, Country country) {
        if (getByID(city.getId()) != null) {
            var newCity = CityInfo.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).id(city.getId()).country(country).build();
            repository.save(newCity);
            return true;
        }
        return false;
    }

    @Override
    public List<CityInfo> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public CityInfo getByName(String name) {
        var optionalCity = repository.getCityInfoByName(name);
        return optionalCity.orElse(null);
    }

    @Override
    public CityInfo getByID(Long id) {
        return repository.getCityInfoById(id).orElse(null);
    }

    @Override
    public boolean update(CityInfo city) {
        if (getByID(city.getId()) != null) {
            repository.save(city);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (this.getByID(id) != null) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

}
