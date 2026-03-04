package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.TrainService;

@RestController
@RequestMapping("/api/trains")
public class TrainRestController {

    private final TrainService trainService;

    public TrainRestController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    public Page<Train> list(@RequestParam(required=false) String q,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return trainService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Train> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(trainService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Train> create(@RequestBody Train train) {
        Train t = trainService.create(train);
        return ResponseEntity.ok(t);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Train> update(@PathVariable Integer id, @RequestBody Train train) {
        try {
            Train updated = trainService.update(id, train);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            trainService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}