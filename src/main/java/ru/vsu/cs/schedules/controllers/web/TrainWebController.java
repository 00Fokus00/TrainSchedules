package ru.vsu.cs.schedules.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.TrainService;
import ru.vsu.cs.schedules.services.LocomotiveService;
import ru.vsu.cs.schedules.specification.TrainSpecification;

import java.util.List;

@Controller
@RequestMapping("/trains")
public class TrainWebController {

    private final LocomotiveService locomotiveService;

    private final TrainService trainService;

    public TrainWebController(LocomotiveService locomotiveService, TrainService trainService) {
        this.locomotiveService = locomotiveService;
        this.trainService = trainService;
    }

    @GetMapping
    public String listTrains(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") Integer minSpeed,
            @RequestParam(required = false, defaultValue = "500") Integer maxSpeed,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;

        if (!"id".equals(sortBy) && !"number".equals(sortBy) && !"maxSpeed".equals(sortBy)) {
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

//        Page<Train> trainsPage = trainService.list(search.trim(), pageable);

        Specification<Train> spec = Specification.where(null);

        spec = spec.and(TrainSpecification.likeNumber(search.trim()));
        spec = spec.and(TrainSpecification.speedBetween(minSpeed, maxSpeed));

        Page<Train> trainsPage = trainService.list(spec, pageable);

        model.addAttribute("trains", trainsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", trainsPage.getTotalPages());
        model.addAttribute("totalItems", trainsPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("minSpeed", minSpeed);
        model.addAttribute("maxSpeed", maxSpeed);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(trainsPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "trains";
    }

    @GetMapping("/{id}")
    public String viewTrain(@PathVariable Integer id, Model model) {
        Train train = trainService.getById(id);
        model.addAttribute("train", train);
        return "train-view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("train", new Train());

        List<Locomotive> locomotives = locomotiveService.list("", Pageable.unpaged()).getContent();
        model.addAttribute("locomotives", locomotives);
        return "train-form";
    }

    @PostMapping
    public String createTrain(@ModelAttribute Train train) {
        trainService.create(train);
        return "redirect:/trains";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Train train = trainService.getById(id);
        model.addAttribute("train", train);

        List<Locomotive> locomotives = locomotiveService.list("", Pageable.unpaged()).getContent();
        model.addAttribute("locomotives", locomotives);
        return "train-form";
    }

    @PostMapping("/{id}")
    public String updateTrain(@PathVariable Integer id, @ModelAttribute Train train) {
        trainService.update(id, train);
        return "redirect:/trains";
    }

    @PostMapping("/{id}/delete")
    public String deleteTrain(@PathVariable Integer id) {
        trainService.delete(id);
        return "redirect:/trains";
    }
}