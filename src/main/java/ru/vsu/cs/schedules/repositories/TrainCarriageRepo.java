package ru.vsu.cs.schedules.repositories;

import jakarta.annotation.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.TrainCarriage;

@Repository
public interface TrainCarriageRepo extends JpaRepository<TrainCarriage, Integer> {
    Page<TrainCarriage> findByTypeContainingIgnoreCase(String type, Pageable pageable);
    Page<TrainCarriage> findAll(@Nullable Specification<TrainCarriage> spec, Pageable pageable);
    Page<TrainCarriage> findByTrainId(Integer trainId, Pageable pageable);
}
