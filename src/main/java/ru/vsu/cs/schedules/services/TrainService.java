package ru.vsu.cs.schedules.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.repositories.TrainRepo;

import java.util.List;

@Service
public class TrainService {
    private final TrainRepo trainRepo;

    public TrainService(TrainRepo trainRepo) {
        this.trainRepo = trainRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<Train> list(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return trainRepo.findByNumberContainingIgnoreCase(sanitize(search.trim()), pageable);
        } else {
            return trainRepo.findAll(pageable);
        }
    }

    public List<Train> list(Integer stationId) {
        return trainRepo.findTrainByStationId(stationId);
    }

    public Page<Train> list(Specification<Train> search, Pageable pageable) {
        return trainRepo.findAll(search, pageable);
    }

    public Train getById(Integer id) {
        return trainRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Train not found: " + id));
    }

    public Train getByIdLocomotive(Integer id) {
        try {
            return trainRepo.findTrainByLocomotiveId(id);
        } catch (NotFoundException e) {
            throw new RuntimeException("Train not found: " + id);
        }
    }

    @Transactional
    public Train create(Train train) {
        return trainRepo.save(train);
    }

    @Transactional
    public Train update(Integer id, Train train) {
        Train existing = trainRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Train not found: " + id));

        existing.setCarriages(train.getCarriages());
        existing.setLocomotive(train.getLocomotive());
        existing.setMaxSpeed(train.getMaxSpeed());
        existing.setNumber(train.getNumber());
        existing.setSchedules(train.getSchedules());

        return trainRepo.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        if (!trainRepo.existsById(id)) {
            throw new NotFoundException("Train not found: " + id);
        }
        trainRepo.deleteById(id);
    }
}