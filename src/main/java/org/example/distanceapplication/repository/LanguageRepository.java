package org.example.distanceapplication.repository;

import java.util.Optional;
import org.example.distanceapplication.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
  Optional<Language> getByName(@Param("name") String name);

  Optional<Language> getLanguageById(@Param("id") Long id);

}
