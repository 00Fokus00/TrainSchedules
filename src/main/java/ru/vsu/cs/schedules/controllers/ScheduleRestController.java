package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.ScheduleService;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleRestController {

    private final ScheduleService scheduleService;

    public ScheduleRestController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public Page<Schedule> list(@RequestParam(required=false) String q,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return scheduleService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(scheduleService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Schedule> create(@RequestBody Schedule schedule) {
        Schedule s = scheduleService.create(schedule);
        return ResponseEntity.ok(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> update(@PathVariable Integer id, @RequestBody Schedule schedule) {
        try {
            Schedule updated = scheduleService.update(id, schedule);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            scheduleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}