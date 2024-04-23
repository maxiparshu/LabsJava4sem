package org.example.distanceapplication.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CityTest {
  @Test
  public void equalsCheck() {
    var first = City.builder()
        .name("Test").build();
    var second = City.builder()
        .name("Test").build();
    assertEquals(first,second);
    assertEquals(first.hashCode(), second.hashCode());
    first.setId(4L);
    assertEquals(4L, first.getId());
    assertNotEquals(first, second);
  }

  @Test
  public void stringCheck() {
    var first = City.builder()
        .name("Check").build();
    assertEquals(String.class, first.toString().getClass());
    assertEquals(City.builder().toString().getClass(),
        String.class);
  }
}
