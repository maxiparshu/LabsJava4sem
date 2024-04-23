package org.example.distanceapplication.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LanguageDTOTest {

  @Test
  public void checkEquals() {
    var first = LanguageDTO.builder()
        .name("Check").build();
    var second = LanguageDTO.builder()
        .name("Check").build();
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
  @Test
  public void checkNotEquals() {
    var first = LanguageDTO.builder()
        .name("Check").build();
    var second = LanguageDTO.builder()
        .name("Not").build();
    assertNotEquals(first, second);
  }
  @Test
  public void stringCheck() {
    var first = LanguageDTO.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(LanguageDTO.builder().toString().getClass(),
        String.class);
  }

  @Test
  public void settersCheck() {
    var country = LanguageDTO.builder().build();
    country.setId(1L);
    country.setName("French");
    assertEquals(country.getName(), "French");
    assertEquals(country.getId(), 1);
  }
}
