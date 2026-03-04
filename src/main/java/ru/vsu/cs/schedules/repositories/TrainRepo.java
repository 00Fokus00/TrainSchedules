package ru.vsu.cs.schedules.repositories;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.Train;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepo extends JpaRepository<Train, Integer> {
    Page<Train> findByNumberContainingIgnoreCase(String number, Pageable pageable);
    Page<Train> findAll(@Nullable Specification<Train> spec, Pageable pageable);
    Optional<Train> findByLocomotiveId(Integer locomotiveId);
    Train findTrainByLocomotiveId(Integer locomotiveId);

    @Query("SELECT DISTINCT t FROM Train t " +
            "JOIN t.schedules s " +
            "JOIN s.stationEntries se " +
            "WHERE se.station.id = :stationId")
    List<Train> findTrainByStationId(@Param("stationId") Integer stationId);
}
