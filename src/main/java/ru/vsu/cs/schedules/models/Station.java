package ru.vsu.cs.schedules.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @JsonBackReference("station-entries")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<StationEntry> stationEntries = new ArrayList<>();
}