package org.example.distanceapplication.entity;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LanguageTest {
  @Test
  public void equalsCheck() {
    var first = Language.builder()
        .name("Test").build();
    var second = Language.builder()
        .name("Test").build();
    assertEquals(first,second);
    assertEquals(first.hashCode(), second.hashCode());
    first.setId(4L);
    assertEquals(4L, first.getId());
    assertNotEquals(first, second);

    var country = Country.builder()
        .name("Test").build();
    var countriesList = List.of(country);
    first.setCountries(countriesList);
    assertEquals(first.getCountries(), countriesList);
  }

  @Test
  public void stringCheck() {
    var first = Language.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(Language.builder().toString().getClass(),
        String.class);
  }
}
