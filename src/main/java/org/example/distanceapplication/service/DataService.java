package org.example.distanceapplication.service;

import org.example.distanceapplication.entity.CityInfo;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface DataService {
    List<CityInfo> getAll();

    CityInfo getCityInfoByName(String name);

    CityInfo updateCityInfo(CityInfo city);

}
