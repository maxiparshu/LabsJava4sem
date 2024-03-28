package org.example.distanceapplication.service.implementation;

import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
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
    private static final String DONT_EXIST = " doesn't exist";

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

    public void createWithCountry(CityDTO city, Country country)
            throws BadRequestException {
        try {
            getByName(city.getName());
            throw new BadRequestException("Can't create because already exist");
        } catch (ResourceNotFoundException e) {
            var newCity = City.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).country(country).id(findFreeID()).build();
            repository.save(newCity);
            cache.put(newCity.getId(), newCity);
        }
    }

    @Override
    public void create(City city) throws BadRequestException {
        try {
            getByID(city.getId());
            throw new BadRequestException("Can't create city with this id"
                    + city.getId() + " already exist");
        } catch (ResourceNotFoundException e) {
            repository.save(city);
            cache.put(city.getId(), city);

        }
    }

    public void updateWithCountry(CityDTO city, Country country)
            throws ResourceNotFoundException {
        try {
            getByID(city.getId());
            cache.remove(city.getId());
            var newCity = City.builder().name(city.getName()).latitude(city.getLatitude())
                    .longitude(city.getLongitude()).country(country).build();
            repository.save(newCity);
            cache.put(newCity.getId(), newCity);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Can't update city with this id"
                    + city.getId() + DONT_EXIST);
        }
    }

    @Override
    public List<City> read() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    public City getByName(String name) throws ResourceNotFoundException {
        var optionalCountry = repository.getCityByName(name);
        if (optionalCountry.isPresent()) {
            cache.put(optionalCountry.get().getId(), optionalCountry.get());
        } else throw new ResourceNotFoundException("Can't find city because with this name doesn't exist");
        return optionalCountry.get();
    }

    @Override
    public City getByID(Long id) throws ResourceNotFoundException {
        var optionalCity = cache.get(id);
        if (optionalCity.isEmpty()) {
            optionalCity = repository.getCityById(id);
            if (optionalCity.isPresent()) {
                cache.put(id, optionalCity.get());
            } else throw new ResourceNotFoundException("Can't find city with id = "
                    + id + DONT_EXIST);
        }
        return optionalCity.get();
    }

    @Override
    public void update(City city) throws ResourceNotFoundException {
        if (repository.getCityById(city.getId()).isPresent()) {
            cache.remove(city.getId());
            repository.save(city);
            cache.put(city.getId(), city);
        } else throw new ResourceNotFoundException("Can't find city with id = "
                + city.getId() + DONT_EXIST);
    }

    @Override
    public void delete(Long id)
            throws ResourceNotFoundException {
        if (repository.getCityById(id).isPresent()) {
            repository.deleteById(id);
            cache.remove(id);
        } else throw new ResourceNotFoundException("Can't delete city with id = "
                + id + DONT_EXIST);
    }

    public List<City> getBetweenLatitudes
            (Double first, Double second) {
        return repository.findAllCityWithLatitudeBetween(first, second);
    }
}
