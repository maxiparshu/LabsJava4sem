package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.CityInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityInfo, Long> {
    Optional<CityInfo> getCityInfoByName(@Param("name") String name);

}