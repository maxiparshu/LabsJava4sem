package org.example.distanceapplication.service;

import org.example.distanceapplication.entity.CityInfo;

import org.springframework.stereotype.Service;
@Service
public interface DistanceService {
    double getDistanceInKilometres(CityInfo to, CityInfo from);
}
