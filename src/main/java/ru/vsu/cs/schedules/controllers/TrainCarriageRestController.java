package ru.vsu.cs.schedules.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.services.TrainCarriageService;

@RestController
@RequestMapping("/api/train-carriages")
public class TrainCarriageRestController {

    private final TrainCarriageService trainCarriageService;

    public TrainCarriageRestController(TrainCarriageService trainCarriageService) {
        this.trainCarriageService = trainCarriageService;
    }

    @GetMapping
    public Page<TrainCarriage> list(@RequestParam(required=false) String q,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size));
        return trainCarriageService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainCarriage> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(trainCarriageService.getById(id));
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<TrainCarriage> create(@RequestBody TrainCarriage trainCarriage) {
        TrainCarriage tc = trainCarriageService.create(trainCarriage);
        return ResponseEntity.ok(tc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainCarriage> update(@PathVariable Integer id, @RequestBody TrainCarriage trainCarriage) {
        try {
            TrainCarriage updated = trainCarriageService.update(id, trainCarriage);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            trainCarriageService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}