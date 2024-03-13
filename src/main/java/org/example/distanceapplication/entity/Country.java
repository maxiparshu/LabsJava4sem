package org.example.distanceapplication.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "country")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "country", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CityInfo> cities = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(name = "language_country",
            joinColumns = {@JoinColumn(name = "id_country")},
            inverseJoinColumns = {@JoinColumn(name = "id_language")})
    @JsonManagedReference
    Set<Language> languages = new HashSet<>();

    public void addLanguage(Language language) {
        languages.add(language);
    }

    public void removeLanguage(Language language) {
        languages.remove(language);
    }
}
