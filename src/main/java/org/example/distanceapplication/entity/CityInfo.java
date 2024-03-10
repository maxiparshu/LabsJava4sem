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
    @Column(name = "name")
    String name;
    @Column(name = "latitude")
    Double latitude;
    @Column(name = "longitude")
    Double longitude;
    @Column(name = "id_country")
    Integer id_country;

}
