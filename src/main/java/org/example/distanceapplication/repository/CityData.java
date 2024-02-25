package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.CityInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CityData extends JpaRepository<CityInfo, Integer> {
    @Query("SELECT city FROM geoposition city WHERE city.name = ?1")
    Optional<CityInfo> getCityInfoByName(String name);
}
