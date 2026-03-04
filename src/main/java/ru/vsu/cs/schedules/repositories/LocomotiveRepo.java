package ru.vsu.cs.schedules.repositories;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.Locomotive;

@Repository
public interface LocomotiveRepo extends JpaRepository<Locomotive, Integer> {
    Page<Locomotive> findAll(@Nullable Specification<Locomotive> spec, Pageable pageable);
}
