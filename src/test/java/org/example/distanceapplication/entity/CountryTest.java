package org.example.distanceapplication.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CountryTest {
  @Test
  public void equalsCheck() {
    var first = Country.builder()
        .name("Test").build();
    var second = Country.builder()
        .name("Test").build();
    assertEquals(first,second);
    assertEquals(first.hashCode(), second.hashCode());
    first.setId(4L);
    assertEquals(4L, first.getId());
    assertNotEquals(first, second);
  }

  @Test
  public void stringCheck() {
    var first = Country.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(Country.builder().toString().getClass(),
        String.class);
  }
  @Test
  public void languageModifyCheck() {
    var country = Country.builder()
        .name("Belarus")
        .languages(new HashSet<>())
        .build();
    var language = Language.builder()
        .name("test").build();
    var actualHashSet = new HashSet<Language>();
    actualHashSet.add(language);
    country.addLanguage(language);
    assertEquals(actualHashSet, country.getLanguages());

    actualHashSet.remove(language);
    country.removeLanguage(language);
    assertEquals(actualHashSet, country.getLanguages());

    actualHashSet.add(language);
    country.setLanguages(actualHashSet);
    assertEquals(actualHashSet, country.getLanguages());
  }

  @Test
  public void citiesCheck() {
    var country = Country.builder()
        .name("Belarus")
        .cities(new ArrayList<>())
        .build();
    var city = City.builder()
        .name("Test").build();
    var citiesList = List.of(city);
    country.setCities(citiesList);
    assertEquals(citiesList, country.getCities());
  }
}
