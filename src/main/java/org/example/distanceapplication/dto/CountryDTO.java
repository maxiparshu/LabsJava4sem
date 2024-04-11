package org.example.distanceapplication.dto;

import java.util.List;
import lombok.Data;

@Data
public class CountryDTO {
  private Long id;
  private String name;
  private List<String> languages;
}
