package org.example.distanceapplication.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountryDTO {
  private Long id;
  private String name;
  private List<String> languages;
}
