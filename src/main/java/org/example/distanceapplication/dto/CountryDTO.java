package org.example.distanceapplication.dto;

import lombok.Data;

import java.util.List;

@Data
public class CountryDTO {
    private Long id;
    private String name;
    List<String> languages;
}
