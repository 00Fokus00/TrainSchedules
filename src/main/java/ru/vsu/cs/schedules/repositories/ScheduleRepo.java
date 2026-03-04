package ru.vsu.cs.schedules.repositories;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.Schedule;

import java.time.LocalDateTime;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {
    Page<Schedule> findAll(@Nullable Specification<Schedule> spec, Pageable pageable);
    Page<Schedule> findByDepartureTimeAfter(LocalDateTime date, Pageable pageable);
    Page<Schedule> findByArrivalTimeBefore(LocalDateTime date, Pageable pageable);
}
