package org.example.distanceapplication.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "country")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "country",
      cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JsonManagedReference
  private List<City> cities = new ArrayList<>();
  @ManyToMany(cascade = {CascadeType.MERGE,
      CascadeType.PERSIST}, fetch = FetchType.LAZY)
  @JoinTable(name = "language_country",
      joinColumns = {@JoinColumn(name = "id_country")},
      inverseJoinColumns = {@JoinColumn(name = "id_language")})
  @JsonManagedReference
  private Set<Language> languages = new HashSet<>();

  public void addLanguage(final Language language) {
    languages.add(language);
  }

  public void removeLanguage(final Language language) {
    languages.remove(language);
  }
}
