package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.StationEntryService;

@RestController
@RequestMapping("/api/station-entries")
public class StationEntryRestController {

    private final StationEntryService stationEntryService;

    public StationEntryRestController(StationEntryService stationEntryService) {
        this.stationEntryService = stationEntryService;
    }

    @GetMapping
    public Page<StationEntry> list(@RequestParam(required=false) String q,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return stationEntryService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationEntry> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(stationEntryService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<StationEntry> create(@RequestBody StationEntry stationEntry) {
        StationEntry se = stationEntryService.create(stationEntry);
        return ResponseEntity.ok(se);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationEntry> update(@PathVariable Integer id, @RequestBody StationEntry stationEntry) {
        try {
            StationEntry updated = stationEntryService.update(id, stationEntry);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            stationEntryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}