package org.example.distanceapplication.service_implementation;

import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.service.implementation.DistanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DistanceServiceImplTest {

  @InjectMocks
  private DistanceServiceImpl service;

  @Test
  void shouldCalculateDistance() {
    var firstCity = City.builder()
        .name("Minsk")
        .longitude(27.5667)
        .latitude(53.9)
        .build();
    var secondCity = City.builder()
        .name("Pinsk")
        .longitude(26.1031)
        .latitude(52.1153)
        .build();
    assertEquals(221.2667,
        service.getDistanceInKilometres(firstCity, secondCity));

  }
}
