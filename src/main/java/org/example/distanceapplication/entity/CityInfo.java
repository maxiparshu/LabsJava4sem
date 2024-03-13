package org.example.distanceapplication.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "city_info")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityInfo {
    @Id
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "latitude")
    Double latitude;
    @Column(name = "longitude")
    Double longitude;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_country")
    @JsonBackReference
    private Country country;

    @Override
    public String toString() {
        return name + "\nlatitude: " + latitude + "\nlongitude: " + longitude + "\nINDEX: "
                + id + "\ncountry " + country.getName() + " id:" + country.getId();

    }
}
