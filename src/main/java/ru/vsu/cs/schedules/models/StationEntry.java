package ru.vsu.cs.schedules.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "station_entry")
public class StationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    @JsonBackReference("schedule-entries")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    @JsonManagedReference("station-entries")
    private Station station;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "segment_order", nullable = false)
    private Integer segmentOrder;
}
