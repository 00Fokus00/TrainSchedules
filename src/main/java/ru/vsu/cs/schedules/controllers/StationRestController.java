package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.StationService;

@RestController
@RequestMapping("/api/stations")
public class StationRestController {

    private final StationService stationService;

    public StationRestController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public Page<Station> list(@RequestParam(required=false) String q,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return stationService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(stationService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Station> create(@RequestBody Station station) {
        Station s = stationService.create(station);
        return ResponseEntity.ok(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Integer id, @RequestBody Station station) {
        try {
            Station updated = stationService.update(id, station);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            stationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}