package org.example.distanceapplication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CityDTOTest {

  @Test
  public void checkEquals() {
    CityDTO first = CityDTO.builder()
        .name("Check").build();
    CityDTO second = CityDTO.builder()
        .name("Check").build();
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void checkNotEquals() {
    CityDTO first = CityDTO.builder()
        .name("Check").build();
    CityDTO second = CityDTO.builder()
        .name("Not").build();
    assertNotEquals(first, second);
  }

  @Test
  public void stringCheck() {
    CityDTO first = CityDTO.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(CityDTO.builder().toString().getClass(),
        String.class);
  }

  @Test
  public void settersCheck() {
    var city = CityDTO.builder().build();
    city.setId(1L);
    city.setLongitude(2d);
    city.setLatitude(2d);
    city.setName("paris");
    city.setIdCountry(1L);
    assertEquals(city.getLongitude(), 2d);
    assertEquals(city.getLatitude(), 2d);
    assertEquals(city.getIdCountry(), 1);
    assertEquals(city.getName(), "paris");
    assertEquals(city.getId(), 1);
  }
}
