package ru.vsu.cs.schedules.repositories;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StationEntryRepo extends JpaRepository<StationEntry, Integer> {
    Page<StationEntry> findAll(@Nullable Specification<StationEntry> spec, Pageable pageable);
    Page<StationEntry> findStationEntryByScheduleId(Integer scheduleId, Pageable pageable);

    List<StationEntry> findStationEntryByStationId(Integer stationId);

    List<StationEntry> findByScheduleId(Integer scheduleId);

    // Находит остановки с порядковым номером больше указанного
    List<StationEntry> findByScheduleIdAndSegmentOrderGreaterThan(Integer scheduleId, Integer segmentOrder);

    // Проверяет временные конфликты
    @Query("SELECT se FROM StationEntry se " +
            "WHERE se.schedule.id = :scheduleId " +
            "AND (:excludeId IS NULL OR se.id != :excludeId) " +
            "AND se.arrivalTime IS NOT NULL " +
            "AND se.departureTime IS NOT NULL " +
            "AND ((se.arrivalTime <= :arrivalTime AND se.departureTime >= :arrivalTime) OR " +
            "     (se.arrivalTime <= :departureTime AND se.departureTime >= :departureTime) OR " +
            "     (:arrivalTime <= se.arrivalTime AND :departureTime >= se.arrivalTime))")
    List<StationEntry> findTimeConflicts(
            @Param("scheduleId") Integer scheduleId,
            @Param("excludeId") Integer excludeId,
            @Param("arrivalTime") LocalDateTime arrivalTime,
            @Param("departureTime") LocalDateTime departureTime);
}
