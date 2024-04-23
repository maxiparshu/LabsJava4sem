package org.example.distanceapplication.service_implementation;

import org.example.distanceapplication.service.CounterService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CounterServiceTest {
  @Test
  public void counterCheck() {
    CounterService.increment();
    int i = CounterService.increment();
    assertEquals(i,2);
  }
}
