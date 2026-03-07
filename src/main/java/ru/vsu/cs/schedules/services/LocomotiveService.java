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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.repositories.LocomotiveRepo;
import ru.vsu.cs.schedules.repositories.TrainRepo;

import java.util.Optional;

@Service
public class LocomotiveService {
    private LocomotiveRepo locomotiveRepo;
    private TrainRepo trainRepo;

    public LocomotiveService(LocomotiveRepo locomotiveRepo, TrainRepo trainRepo) {
        this.locomotiveRepo = locomotiveRepo;
        this.trainRepo = trainRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<Locomotive> list(String q, Pageable pageable) {
        return locomotiveRepo.findAll(pageable);
    }

    public Page<Locomotive> list(Specification<Locomotive> spec, Pageable pageable) {
        return locomotiveRepo.findAll(spec, pageable);
    }

    @Cacheable(value = "locomotive:byId", key="#id")
    public Locomotive getById(Integer id) {
        return locomotiveRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Locomotive not found: " + id));
    }

    @Transactional
    public Locomotive create(Locomotive locomotive) {
        return locomotiveRepo.save(locomotive);
    }

    @CacheEvict(value = "locomotive:byId", key = "#id")
    @Transactional
    public Locomotive update(Integer id, Locomotive locomotive) {
        Locomotive existing = locomotiveRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Locomotive not found: " + id));

        existing.setModel(locomotive.getModel());
        existing.setPower(locomotive.getPower());
        existing.setStatus(locomotive.getStatus());

        return locomotiveRepo.save(existing);
    }

    @Transactional
    public void delete(Integer id, RedirectAttributes redirectAttributes) {
        Locomotive locomotive = locomotiveRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Locomotive not found: " + id));

        Optional<Train> trainOptional = trainRepo.findByLocomotiveId(id);

        if (trainOptional.isPresent()) {
            Train train = trainOptional.get();

            throw new IllegalStateException("Cannot delete locomotive #" + id +
                    " because it is assigned to train #" + train.getId() +
                    " (" + train.getNumber() + ").");
        }

        locomotiveRepo.delete(locomotive);

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Locomotive deleted successfully.");
        }
    }

    @Transactional
    public void delete(Integer id) {
        Locomotive locomotive = locomotiveRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Locomotive not found: " + id));

        Optional<Train> trainOptional = trainRepo.findByLocomotiveId(id);
        if (trainOptional.isPresent()) {
            Train train = trainOptional.get();
            throw new IllegalStateException("Cannot delete locomotive #" + id +
                    " because it is assigned to train #" + train.getId() +
                    " (" + train.getNumber() + ").");
        }

        locomotiveRepo.delete(locomotive);
    }
}
