package ru.vsu.cs.schedules.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "train_carriage")
public class TrainCarriage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "train_id")
    @JsonBackReference("train-carriages")
    private Train train;

    @Column(nullable = false)
    private String type;
}