package ru.vsu.cs.schedules.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "train")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(name = "max_speed")
    private Integer maxSpeed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "locomotive_id")
    @JsonManagedReference("locomotive-train")
    private Locomotive locomotive;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonManagedReference("train-carriages")
    @Builder.Default
    private List<TrainCarriage> carriages = new ArrayList<>();

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonBackReference("train-schedules")
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();
}
