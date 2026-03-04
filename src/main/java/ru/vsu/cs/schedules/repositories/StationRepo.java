package ru.vsu.cs.schedules.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.cs.schedules.models.Station;

@Repository
public interface StationRepo extends JpaRepository<Station, Integer> {
    Page<Station> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
