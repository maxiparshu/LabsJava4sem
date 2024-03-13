package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> getByName(@Param("name") String name);

    Optional<Country> getCountryById(@Param("id") Long id);
}
