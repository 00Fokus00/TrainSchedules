package ru.vsu.cs.schedules.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.repositories.ScheduleRepo;

import java.time.LocalDateTime;

@Service
public class ScheduleService {
    private final ScheduleRepo scheduleRepo;

    public ScheduleService(ScheduleRepo scheduleRepo) {
        this.scheduleRepo = scheduleRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<Schedule> list(String q, Pageable pageable) {
        return scheduleRepo.findAll(pageable);
    }

    public Page<Schedule> list(Specification<Schedule> spec, Pageable pageable) {
        return scheduleRepo.findAll(spec, pageable);
    }

    @Cacheable(value = "schedule:byId", key="#id")
    public Schedule getById(Integer id) {
        return scheduleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Schedule not found: " + id));
    }

    @Transactional
    public Schedule create(Schedule schedule) {
        return scheduleRepo.save(schedule);
    }

    @CacheEvict(value = "schedule:byId", key = "#id")
    @Transactional
    public Schedule update(Integer id, Schedule schedule) {
        Schedule existing = scheduleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Schedule not found: " + id));

        existing.setTrain(schedule.getTrain());
        existing.setArrivalTime(schedule.getArrivalTime());
        existing.setDepartureTime(schedule.getDepartureTime());

        if (existing.getDepartureTime() != null && existing.getArrivalTime() != null) {
            if (existing.getDepartureTime().isAfter(existing.getArrivalTime())) {
                throw new IllegalArgumentException("Departure time must be before arrival time");
            }
        }
        return scheduleRepo.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        if (!scheduleRepo.existsById(id)) {
            throw new NotFoundException("Schedule not found: " + id);
        }
        scheduleRepo.deleteById(id);
    }
}