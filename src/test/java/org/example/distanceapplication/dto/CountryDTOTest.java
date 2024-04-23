package org.example.distanceapplication.dto;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CountryDTOTest {

  @Test
  public void checkEquals() {
    var first = CountryDTO.builder()
        .name("Check").build();
    var second = CountryDTO.builder()
        .name("Check").build();
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
  @Test
  public void checkNotEquals() {
    var first = CountryDTO.builder()
        .name("Check").build();
    var second = CountryDTO.builder()
        .name("Not").build();
    assertNotEquals(first, second);
  }
  @Test
  public void stringCheck() {
    var first = CountryDTO.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(CountryDTO.builder().toString().getClass(),
        String.class);
  }

  @Test
  public void settersCheck() {
    var country = CountryDTO.builder().build();
    country.setId(1L);
    country.setName("paris");
    var list = List.of("English");
    country.setLanguages(list);
    assertEquals(country.getName(), "paris");
    assertEquals(country.getId(), 1);
    assertEquals(country.getLanguages(), list);
  }
}
