package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> getCityByName(@Param("name") String name);

    Optional<City> getCityById(@Param("id") Long id);

    @Query("SELECT city FROM City city WHERE city.latitude > :first" +
            " AND city.latitude < :second ORDER BY city.latitude")
    List<City> findAllCityWithLatitudeBetween(@Param("first") Double first, @Param("second") Double second);
}
