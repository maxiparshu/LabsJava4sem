package org.example.distanceapplication.dto;

import lombok.Data;

@SuppressWarnings({"checkstyle:AbbreviationAsWordInName",
    "checkstyle:MissingJavadocType"})
@Data
public class CityDTO {
  private Long id;
  private String name;
  private Double latitude;
  private Double longitude;
  private Integer idCountry;
}
