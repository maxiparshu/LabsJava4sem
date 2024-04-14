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
import org.example.distanceapplication.exception.ServerException;
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
    return i + 1;
  }

  private long findFreeId(final HashSet<Long> usedIndexes) {
    var list = read();
    long i = 1;
    for (City cityInfo : list) {
      if (cityInfo.getId() != i) {
        if (!usedIndexes.contains(i)) {
          return i;
        } else {
          i = cityInfo.getId();
        }
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
      try {
        repository.save(city);
      } catch (Exception exception) {
      throw new ServerException("Sever problem");
      }
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
    Optional<City> optionalCity = cache.get(id);
    if (optionalCity.isEmpty()) {
      try {
        optionalCity = repository.getCityById(id);
        if (optionalCity.isPresent()) {
          cache.put(id, optionalCity.get());
        } else {
          throw new ResourceNotFoundException(
              "Cannot find city with id = " + id + DONT_EXIST);
        }
      } catch (Exception e) {
        throw new ServerException("Sever problem");
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

  @Transactional
  @Override
  public void createBulk(final List<CityDTO> list)
      throws BadRequestException {
    List<City> cities = list.stream()
        .map(cityDTO -> {
          var country = countryRepository.getCountryById(cityDTO
              .getIdCountry().longValue());
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
