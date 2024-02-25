package org.example.distanceapplication.entity;

import jakarta.persistence.*;

import lombok.Data;
@Entity(name = "geoposition")
@Table(name = "geoposition")
@Data
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "city_name")
    String name;
    @Column(name = "latitude")
    Double latitude;
    @Column(name = "longitude")
    Double longitude;

    @Override
    public String toString(){
        return name + String.format( " (latitude: %.4f)", latitude) + String.format( " (longitude: %.4f)", longitude);
    }
}
