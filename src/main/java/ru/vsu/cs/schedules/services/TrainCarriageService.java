package ru.vsu.cs.schedules.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.repositories.TrainCarriageRepo;

@Service
public class TrainCarriageService {
    private final TrainCarriageRepo trainCarriageRepo;

    public TrainCarriageService(TrainCarriageRepo trainCarriageRepo) {
        this.trainCarriageRepo = trainCarriageRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<TrainCarriage> list(String q, Pageable pageable) {
        return trainCarriageRepo.findAll(pageable);
    }

    public Page<TrainCarriage> list(Specification<TrainCarriage> spec, Pageable pageable) {
        return trainCarriageRepo.findAll(spec, pageable);
    }

    public TrainCarriage getById(Integer id) {
        return trainCarriageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("TrainCarriage not found: " + id));
    }

    @Transactional
    public TrainCarriage create(TrainCarriage trainCarriage) {
        return trainCarriageRepo.save(trainCarriage);
    }

    @Transactional
    public TrainCarriage update(Integer id, TrainCarriage trainCarriage) {
        TrainCarriage existing = trainCarriageRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("TrainCarriage not found: " + id));

        existing.setTrain(trainCarriage.getTrain());
        existing.setType(trainCarriage.getType());

        return trainCarriageRepo.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        if (!trainCarriageRepo.existsById(id)) {
            throw new NotFoundException("TrainCarriage not found: " + id);
        }
        trainCarriageRepo.deleteById(id);
    }
}