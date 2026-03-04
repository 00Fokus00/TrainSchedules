package ru.vsu.cs.schedules.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.services.TrainCarriageService;
import ru.vsu.cs.schedules.services.TrainService;
import ru.vsu.cs.schedules.specification.TrainCarriageSpecification;

import java.util.List;

@Controller
@RequestMapping("/carriages")
public class TrainCarriageWebController {

    private final TrainCarriageService trainCarriageService;
    private final TrainService trainService;

    public TrainCarriageWebController(TrainCarriageService trainCarriageService, TrainService trainService) {
        this.trainCarriageService = trainCarriageService;
        this.trainService = trainService;
    }

    @GetMapping
    public String listCarriages(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Integer trainId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;

        if (!"id".equals(sortBy) && !"type".equals(sortBy) &&
                !"train.number".equals(sortBy)) {
            sortBy = "id";
        }

        if (!"asc".equals(direction) && !"desc".equals(direction)) {
            direction = "asc";
        }

        Sort sort = Sort.by(sortBy);
        if ("desc".equals(direction)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Specification<TrainCarriage> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(TrainCarriageSpecification.likeType(search.trim()));
        }

        spec = spec.and(TrainCarriageSpecification.hasTrainId(trainId));

        Page<TrainCarriage> carriagesPage = trainCarriageService.list(spec, pageable);

        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();

        Train selectedTrain = null;
        if (trainId != null) {
            try {
                selectedTrain = trainService.getById(trainId);
            } catch (NotFoundException e) {
            }
        }

        model.addAttribute("carriages", carriagesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", carriagesPage.getTotalPages());
        model.addAttribute("totalItems", carriagesPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("trainId", trainId);
        model.addAttribute("trains", trains);
        model.addAttribute("selectedTrain", selectedTrain);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(carriagesPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "carriages";
    }

    @GetMapping("/{id}")
    public String viewCarriage(@PathVariable Integer id, Model model) {
        TrainCarriage carriage = trainCarriageService.getById(id);
        model.addAttribute("carriage", carriage);
        return "carriage-view";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Integer trainId, Model model) {
        TrainCarriage carriage = new TrainCarriage();

        if (trainId != null) {
            Train train = trainService.getById(trainId);
            carriage.setTrain(train);
        }

        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("carriage", carriage);
        model.addAttribute("trains", trains);
        return "carriage-form";
    }

    @PostMapping
    public String createCarriage(@ModelAttribute TrainCarriage carriage) {
        trainCarriageService.create(carriage);
        return "redirect:/carriages" + (carriage.getTrain() != null ? "?trainId=" + carriage.getTrain().getId() : "");
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        TrainCarriage carriage = trainCarriageService.getById(id);
        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("carriage", carriage);
        model.addAttribute("trains", trains);
        return "carriage-form";
    }

    @PostMapping("/{id}")
    public String updateCarriage(@PathVariable Integer id, @ModelAttribute TrainCarriage carriage) {
        trainCarriageService.update(id, carriage);
        return "redirect:/carriages" + (carriage.getTrain() != null ? "?trainId=" + carriage.getTrain().getId() : "");
    }

    @PostMapping("/{id}/delete")
    public String deleteCarriage(@PathVariable Integer id) {
        TrainCarriage carriage = trainCarriageService.getById(id);
        Integer trainId = carriage.getTrain() != null ? carriage.getTrain().getId() : null;

        trainCarriageService.delete(id);

        return "redirect:/carriages" + (trainId != null ? "?trainId=" + trainId : "");
    }
}