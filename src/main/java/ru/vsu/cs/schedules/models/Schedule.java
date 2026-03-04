package ru.vsu.cs.schedules.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    @JsonManagedReference("train-schedules")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Train train;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    @OrderBy("segmentOrder ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference("schedule-entries")
    @Builder.Default
    private List<StationEntry> stationEntries = new ArrayList<>();
}
