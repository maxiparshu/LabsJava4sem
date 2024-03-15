package org.example.distanceapplication.service;

import org.example.distanceapplication.entity.City;

import org.springframework.stereotype.Service;

@Service
public interface DistanceService {
    double getDistanceInKilometres(City to, City from);
}
