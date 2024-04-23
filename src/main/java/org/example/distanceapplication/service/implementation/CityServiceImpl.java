package org.example.distanceapplication.service.implementation;

import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.example.distanceapplication.cache.LRUCache;
import org.example.distanceapplication.dto.CityDTO;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.entity.Country;
import org.example.distanceapplication.exception.BadRequestException;
import org.example.distanceapplication.exception.ResourceNotFoundException;
import org.example.distanceapplication.repository.CityRepository;
import org.example.distanceapplication.repository.CountryRepository;
import org.example.distanceapplication.service.DataService;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CityServiceImpl implements DataService<City, CityDTO> {
  private final CityRepository repository;
  private final CountryRepository countryRepository;
  private final LRUCache<Long, City> cache;
  private final JdbcTemplate jdbcTemplate;
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
    return i;
  }

  private long findFreeId(final HashSet<Long> usedIndexes) {
    var list = read();
    long i = usedIndexes.isEmpty() ? 1 : usedIndexes
        .iterator().next();
    for (City cityInfo : list) {
      if (cityInfo.getId() != i) {
          return i;
      }
      i++;
    }
    return i + 1;
  }

  public City createWithCountry(final CityDTO city,
                                final Country country)
      throws BadRequestException {
    var newCity = City.builder().build();
    try {
      getByName(city.getName());
      throw new BadRequestException("Can't create because already exist");
    } catch (ResourceNotFoundException e) {
      newCity = City.builder().name(city.getName())
          .latitude(city.getLatitude()).longitude(city.getLongitude())
          .country(country).id(findFreeId()).build();
      return create(newCity);
    }
  }

  @Override
  public City create(final City city) throws BadRequestException {
    repository.save(city);
    cache.put(city.getId(), city);
    return city;
  }

  public void updateWithCountry(final CityDTO city,
                                final Country country)
      throws ResourceNotFoundException {
    try {
      var newCity = getByID(city.getId());
      if (!city.getLongitude().isNaN()) {
        newCity.setLongitude(city.getLongitude());
      }
      if (!city.getLatitude().isNaN()) {
        newCity.setLatitude(city.getLatitude());
      }
      if (!city.getName().isEmpty()) {
        newCity.setName(city.getName());
      }
      newCity.setCountry(country);
      update(newCity);
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
    var optionalCity = repository.getCityByName(name);
    if (optionalCity.isPresent()) {
      cache.put(optionalCity.get().getId(), optionalCity.get());
    } else {
      throw new ResourceNotFoundException(
          "Can't find city because with this name doesn't exist");
    }
    return optionalCity.get();
  }

  @Override
  public City getByID(final Long id) throws ResourceNotFoundException {
    Optional<City> optionalCity = cache.get(id);
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
    cache.remove(city.getId());
    repository.save(city);
    cache.put(city.getId(), city);
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
    oldCity.get().setName(city.getName());
    oldCity.get().setLatitude(city.getLatitude());
    oldCity.get().setLongitude(city.getLongitude());
    update(oldCity.get());
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

  @Transactional
  @Override
  public void createBulk(final List<CityDTO> list)
      throws BadRequestException {
    List<City> cities = list.stream()
        .map(cityDTO -> {
          var country = countryRepository.getCountryById(cityDTO
              .getIdCountry());
          if (country.isPresent()) {
            return City.builder().name(cityDTO.getName()).country(country.get())
                .latitude(cityDTO.getLatitude())
                .longitude(cityDTO.getLongitude()).build();
          }
          return City.builder().build();
        }).filter(city -> city.getCountry() != null).toList();
    String sql = "INSERT into city (name, id, latitude, longitude, id_country)"
        + "VALUES (?, ?, ?, ?, ?)";
    var indexes = new HashSet<Long>();
    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(final PreparedStatement statement,
                            final int i)
          throws SQLException {
        statement.setString(1, cities.get(i).getName());
        long index = findFreeId(indexes);
        indexes.add(index);
        statement.setLong(2, index);
        statement.setDouble(3, cities.get(i).getLatitude());
        statement.setDouble(4, cities.get(i).getLongitude());
        statement.setDouble(5, cities.get(i).getCountry().getId());
      }

      @Override
      public int getBatchSize() {
        return cities.size();
      }
    });
  }

  public List<City> getBetweenLatitudes(final Double first,
                                        final Double second) {
    return repository.findAllCityWithLatitudeBetween(first, second);
  }
}
