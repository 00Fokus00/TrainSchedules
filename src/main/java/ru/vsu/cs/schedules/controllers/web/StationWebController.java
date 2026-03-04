package ru.vsu.cs.schedules.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.StationService;
import org.springframework.data.domain.Sort;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

@Controller
@RequestMapping("/stations")
public class StationWebController {

    private final StationService stationService;
    private final TrainService trainService;

    public StationWebController(StationService stationService, TrainService trainService) {
        this.stationService = stationService;
        this.trainService = trainService;
    }

//    @GetMapping
//    public String listStations(Model model) {
//        Pageable pageable = PageRequest.of(0, 100);
//        Page<Station> stations = stationService.list(null, pageable);
//        model.addAttribute("stations", stations.getContent());
//        return "stations";
//    }

    @GetMapping
    public String listStations(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;

        if (!"id".equals(sortBy) && !"name".equals(sortBy)) {
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

        Page<Station> stationsPage = stationService.list(search.trim(), pageable);

        model.addAttribute("stations", stationsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", stationsPage.getTotalPages());
        model.addAttribute("totalItems", stationsPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(stationsPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "stations";
    }

    @GetMapping("/{id}")
    public String viewStation(@PathVariable Integer id, Model model) {
        Station station = stationService.getById(id);
        model.addAttribute("station", station);

        List<Train> trains = trainService.list(id);

        model.addAttribute("trains", trains);

        return "station-view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("station", new Station());
        return "station-form";
    }

    @PostMapping
    public String createStation(@ModelAttribute Station station) {
        stationService.create(station);
        return "redirect:/stations";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Station station = stationService.getById(id);
        model.addAttribute("station", station);
        return "station-form";
    }

    @PostMapping("/{id}")
    public String updateStation(@PathVariable Integer id, @ModelAttribute Station station) {
        stationService.update(id, station);
        return "redirect:/stations";
    }

    @PostMapping("/{id}/delete")
    public String deleteStation(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            stationService.delete(id, redirectAttributes);
            return "redirect:/stations";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stations";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/stations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting station: " + e.getMessage());
            return "redirect:/stations";
        }

    }
}