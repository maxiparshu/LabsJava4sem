package org.example.distanceapplication.service.implementation;

import java.util.List;
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

@SuppressWarnings("checkstyle:MissingJavadocType")
@Service
@AllArgsConstructor
public class CityServiceImpl implements DataService<City> {
  private final CityRepository repository;
  private final LRUCache<Long, City> cache;
  private static final String DONT_EXIST = " doesn't exist";

  private long findFreeId() {
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

  public void createWithCountry(final CityDTO city,
                                final Country country)
      throws BadRequestException {
    try {
      getByName(city.getName());
      throw new BadRequestException("Can't create because already exist");
    } catch (ResourceNotFoundException e) {
      var newCity = City.builder().name(city.getName())
          .latitude(city.getLatitude()).longitude(city.getLongitude())
          .country(country).id(findFreeId()).build();
      repository.save(newCity);
      cache.put(newCity.getId(), newCity);
    }
  }

  @Override
  public void create(final City city) throws BadRequestException {
    try {
      getByID(city.getId());
      throw new BadRequestException("Can't create city with this id"
          + city.getId() + " already exist");
    } catch (ResourceNotFoundException e) {
      repository.save(city);
      cache.put(city.getId(), city);

    }
  }

  public void updateWithCountry(final CityDTO city,
                                final Country country)
      throws ResourceNotFoundException {
    try {
      getByID(city.getId());
      cache.remove(city.getId());
      var newCity = City.builder().name(city.getName())
          .latitude(city.getLatitude()).longitude(city.getLongitude())
          .country(country).build();
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
  public City getByName(final String name) throws ResourceNotFoundException {
    var optionalCountry = repository.getCityByName(name);
    if (optionalCountry.isPresent()) {
      cache.put(optionalCountry.get().getId(), optionalCountry.get());
    } else {
      throw new ResourceNotFoundException(
          "Can't find city because with this name doesn't exist");
    }
    return optionalCountry.get();
  }

  @Override
  public City getByID(final Long id) throws ResourceNotFoundException {
    var optionalCity = cache.get(id);
    if (optionalCity.isEmpty()) {
      optionalCity = repository.getCityById(id);
      if (optionalCity.isPresent()) {
        cache.put(id, optionalCity.get());
      } else {
        throw new ResourceNotFoundException(
            "Cannot find city with id = " + id + DONT_EXIST);
      }
    }
    return optionalCity.get();
  }


  @Override
  public void update(final City city) throws ResourceNotFoundException {
    if (repository.getCityById(city.getId()).isPresent()) {
      cache.remove(city.getId());
      repository.save(city);
      cache.put(city.getId(), city);
    } else {
      throw new ResourceNotFoundException("Can't find city with id: "
          + city.getId() + DONT_EXIST);
    }
  }

  public void update(final CityDTO city) throws ResourceNotFoundException {

    var oldCity = cache.get(city.getId());
    if (oldCity.isEmpty()) {
      oldCity = repository.getCityById(city.getId());
      if (oldCity.isEmpty()) {
        throw new ResourceNotFoundException("Can't find city with id = "
            + city.getId() + DONT_EXIST);
      }
    }
    cache.remove(city.getId());
    oldCity.get().setName(city.getName());
    oldCity.get().setLatitude(city.getLatitude());
    oldCity.get().setLongitude(city.getLongitude());
    repository.save(oldCity.get());
    cache.put(city.getId(), oldCity.get());
  }

  @Override
  public void delete(final Long id)
      throws ResourceNotFoundException {
    if (repository.getCityById(id).isPresent()) {
      repository.deleteById(id);
      cache.remove(id);
    } else {
      throw new ResourceNotFoundException("Can't delete city with id = "
          + id + DONT_EXIST);
    }
  }

  public List<City> getBetweenLatitudes(final Double first,
                                        final Double second) {
    return repository.findAllCityWithLatitudeBetween(first, second);
  }
}
