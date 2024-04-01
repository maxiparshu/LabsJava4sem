package org.example.distanceapplication.dto;

import lombok.Data;

import java.util.List;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Data
public class CountryDTO {
    private Long id;
    private String name;
    private List<String> languages;
}
