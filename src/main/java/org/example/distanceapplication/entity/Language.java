package org.example.distanceapplication.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@SuppressWarnings("checkstyle:MissingJavadocType")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "language")
public class Language {
  @Id
  @Column(name = "id")
  private Long id;
  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "languages",
      cascade = {CascadeType.MERGE, CascadeType.PERSIST},
      fetch = FetchType.LAZY)
  @JsonBackReference
  private List<Country> countries = new ArrayList<>();
}
