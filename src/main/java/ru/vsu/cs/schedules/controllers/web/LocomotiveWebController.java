package ru.vsu.cs.schedules.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.LocomotiveService;
import ru.vsu.cs.schedules.services.TrainService;
import ru.vsu.cs.schedules.specification.LocomotiveSpecification;

@Controller
@RequestMapping("/locomotives")
public class LocomotiveWebController {

    private final LocomotiveService locomotiveService;
    private final TrainService trainService;

    public LocomotiveWebController(LocomotiveService locomotiveService, TrainService trainService) {
        this.locomotiveService = locomotiveService;
        this.trainService = trainService;
    }

    @GetMapping
    public String listLocomotives(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Integer minPower,
            @RequestParam(required = false) Integer maxPower,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;

        if (!"id".equals(sortBy) && !"model".equals(sortBy) && !"power".equals(sortBy) && !"status".equals(sortBy)) {
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

        Specification<Locomotive> spec = Specification.where(null);

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and(LocomotiveSpecification.likeModel(search.trim()));
        }

        spec = spec.and(LocomotiveSpecification.powerBetween(minPower, maxPower));

        if (status != null && !status.trim().isEmpty()) {
            spec = spec.and(LocomotiveSpecification.likeStatus(status.trim()));
        }

        Page<Locomotive> locomotivesPage = locomotiveService.list(spec, pageable);

        model.addAttribute("locomotives", locomotivesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", locomotivesPage.getTotalPages());
        model.addAttribute("totalItems", locomotivesPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("minPower", minPower);
        model.addAttribute("maxPower", maxPower);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(locomotivesPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "locomotives";
    }

    @GetMapping("/{id}")
    public String viewLocomotive(@PathVariable Integer id, Model model) {
        Locomotive locomotive = locomotiveService.getById(id);
        model.addAttribute("locomotive", locomotive);

        Train train = trainService.getByIdLocomotive(locomotive.getId());
        model.addAttribute("train", train);

        return "locomotive-view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("locomotive", new Locomotive());
        return "locomotive-form";
    }

    @PostMapping
    public String createLocomotive(@ModelAttribute Locomotive locomotive) {
        locomotiveService.create(locomotive);
        return "redirect:/locomotives";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Locomotive locomotive = locomotiveService.getById(id);
        model.addAttribute("locomotive", locomotive);
        return "locomotive-form";
    }

    @PostMapping("/{id}")
    public String updateLocomotive(@PathVariable Integer id, @ModelAttribute Locomotive locomotive) {
        locomotiveService.update(id, locomotive);
        return "redirect:/locomotives";
    }

    @PostMapping("/{id}/delete")
    public String deleteLocomotive(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            locomotiveService.delete(id, redirectAttributes);
            return "redirect:/locomotives";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/locomotives";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/locomotives";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting locomotive: " + e.getMessage());
            return "redirect:/locomotives";
        }
    }
}