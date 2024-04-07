package org.example.distanceapplication.dto;

import java.util.List;
import lombok.Data;

@SuppressWarnings({"checkstyle:AbbreviationAsWordInName",
    "checkstyle:MissingJavadocType"})
@Data
public class CountryDTO {
  private Long id;
  private String name;
  private List<String> languages;
}
