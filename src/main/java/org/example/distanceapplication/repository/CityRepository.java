package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.City;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> getCityInfoByName(@Param("name") String name);

    Optional<City> getCityInfoById(@Param("id") Long id);
}
