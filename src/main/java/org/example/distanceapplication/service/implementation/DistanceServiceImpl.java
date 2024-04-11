package org.example.distanceapplication.service.implementation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.example.distanceapplication.entity.City;
import org.example.distanceapplication.service.DistanceService;
import org.springframework.stereotype.Service;

@Service
public class DistanceServiceImpl implements DistanceService {

  @Override
  public double getDistanceInKilometres(final City first,
                                        final City second) {
    if (first == null || second == null) {
      return -1.0;
    }
    if (first.getName().equalsIgnoreCase(second.getName())) {
      return 0.0;
    }
    double delta = first.getLongitude() - second.getLongitude();
    double radianValue = Math.sin(Math.toRadians(first.getLatitude()))
        * Math.sin(Math.toRadians(second.getLatitude()))
        + Math.cos(Math.toRadians(first.getLatitude()))
        * Math.cos(Math.toRadians(second.getLatitude()))
        * Math.cos(Math.toRadians(delta));
    double degreeValue = Math.toDegrees(Math.acos(radianValue));
    double distance = degreeValue * 60 * 1.1515 * 1.6093;
    return BigDecimal.valueOf(distance).setScale(4,
        RoundingMode.CEILING).doubleValue();
  }
}
