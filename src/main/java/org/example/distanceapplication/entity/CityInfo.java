package org.example.distanceapplication.entity;

import jakarta.persistence.*;

import lombok.Data;
@Entity
@Table(name = "city_info")
@Data
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    Long id;
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
