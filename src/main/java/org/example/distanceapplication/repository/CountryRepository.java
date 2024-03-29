package org.example.distanceapplication.repository;

import org.example.distanceapplication.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> getByName(@Param("name") String name);

    Optional<Country> getCountryById(@Param("id") Long id);

    @Query(value = "SELECT country.id, country.name FROM country LEFT JOIN language_country " +
            "ON country.id = language_country.id_country JOIN language " +
            "ON language_country.id_language = language.id WHERE language.id = ?1", nativeQuery = true)
    List<Country> findAllCountryWithLanguage(@Param("1") Integer languageId);
}
