package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.LocomotiveService;

@RestController
@RequestMapping("/api/locomotives")
public class LocomotiveRestController {

    private final LocomotiveService locomotiveService;

    public LocomotiveRestController(LocomotiveService locomotiveService) {
        this.locomotiveService = locomotiveService;
    }

    @GetMapping
    public Page<Locomotive> list(@RequestParam(required=false) String q,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return locomotiveService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Locomotive> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(locomotiveService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Locomotive> create(@RequestBody Locomotive locomotive) {
        Locomotive l = locomotiveService.create(locomotive);
        return ResponseEntity.ok(l);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Locomotive> update(@PathVariable Integer id, @RequestBody Locomotive locomotive) {
        try {
            Locomotive updated = locomotiveService.update(id, locomotive);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            locomotiveService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}