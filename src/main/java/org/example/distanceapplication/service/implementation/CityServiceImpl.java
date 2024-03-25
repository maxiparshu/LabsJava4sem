package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;

import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.repository.CityRepository;
import org.example.distanceapplication.service.DataService;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CityServiceImpl implements DataService<City> {
    private final CityRepository repository;
    private final LRUCache<Long, City> cache;
    private long findFreeID() {
        var list = read();
        long i = 1;
        for (City cityInfo : list) {
            if (cityInfo.getId() != i) {
                return i;
            }
            i++;
        }
        return i + 1;
    }

    public boolean createWithCountry(CityDTO city, Country country) {
        if (getByName(city.getName()) == null) {
            var newCity = City.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).country(country).id(findFreeID()).build();
            repository.save(newCity);
            cache.put(newCity.getId(),newCity);
            return true;
        }
        return false;
    }

    @Override
    public boolean create(City city) {
        if (getByID(city.getId()) == null) {
            repository.save(city);
            cache.put(city.getId(), city);
            return true;
        }
        return false;
    }

    public boolean updateWithCountry(CityDTO city, Country country) {
        if (getByID(city.getId()) != null) {
            cache.remove(city.getId());
            var newCity = City.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).country(country).build();
            repository.save(newCity);
            cache.put(newCity.getId(), newCity);
            return true;
        }
        return false;
    }

    @Override
    public List<City> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public City getByName(String name) {
        var optionalCity = repository.getCityInfoByName(name);
        optionalCity.ifPresent(city -> cache.put(city.getId(), city));
        return optionalCity.orElse(null);
    }

    @Override
    public City getByID(Long id) {
        var optionalCity = cache.get(id);
        if (optionalCity.isEmpty())
            optionalCity = repository.getCityInfoById(id);
        optionalCity.ifPresent(city -> cache.put(id, city));
        return optionalCity.orElse(null);
    }

    @Override
    public boolean update(City city) {
        if (getByID(city.getId()) != null) {
            cache.remove(city.getId());
            repository.save(city);
            cache.put(city.getId(), city);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        if (this.getByID(id) != null) {
            repository.deleteById(id);
            cache.remove(id);
            return true;
        }
        return false;
    }

}
