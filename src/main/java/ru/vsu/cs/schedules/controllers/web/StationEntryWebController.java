package ru.vsu.cs.schedules.controllers.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.services.ScheduleService;
import ru.vsu.cs.schedules.services.StationEntryService;
import ru.vsu.cs.schedules.services.StationService;
import ru.vsu.cs.schedules.specification.StationEntrySpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/station-entries")
public class StationEntryWebController {

    private final StationEntryService stationEntryService;
    private final ScheduleService scheduleService;
    private final StationService stationService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public StationEntryWebController(StationEntryService stationEntryService,
                                     ScheduleService scheduleService,
                                     StationService stationService) {
        this.stationEntryService = stationEntryService;
        this.scheduleService = scheduleService;
        this.stationService = stationService;
    }

    @GetMapping
    public String listStationEntries(
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime arrivalFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime arrivalTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime departureFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime departureTo,
            @RequestParam(required = false) Integer minOrder,
            @RequestParam(required = false) Integer maxOrder,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;


        if (!"id".equals(sortBy) && !"segmentOrder".equals(sortBy) &&
                !"arrivalTime".equals(sortBy) && !"departureTime".equals(sortBy) &&
                !"schedule.id".equals(sortBy) && !"station.name".equals(sortBy)) {
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

        Specification<StationEntry> spec = Specification.where(null);

        spec = spec.and(StationEntrySpecification.hasScheduleId(scheduleId));
        spec = spec.and(StationEntrySpecification.hasStationId(stationId));
        spec = spec.and(StationEntrySpecification.hasArrivalTimeBetween(arrivalFrom, arrivalTo));
        spec = spec.and(StationEntrySpecification.hasDepartureTimeBetween(departureFrom, departureTo));
        spec = spec.and(StationEntrySpecification.segmentOrderBetween(minOrder, maxOrder));

        Page<StationEntry> entriesPage = stationEntryService.list(spec, pageable);

        // Получаем списки для фильтров
        List<Schedule> schedules = scheduleService.list("", Pageable.unpaged()).getContent();
        List<Station> stations = stationService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("entries", entriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", entriesPage.getTotalPages());
        model.addAttribute("totalItems", entriesPage.getTotalElements());
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("stationId", stationId);
        model.addAttribute("arrivalFrom", arrivalFrom != null ? arrivalFrom.format(dateFormatter) : "");
        model.addAttribute("arrivalTo", arrivalTo != null ? arrivalTo.format(dateFormatter) : "");
        model.addAttribute("departureFrom", departureFrom != null ? departureFrom.format(dateFormatter) : "");
        model.addAttribute("departureTo", departureTo != null ? departureTo.format(dateFormatter) : "");
        model.addAttribute("minOrder", minOrder);
        model.addAttribute("maxOrder", maxOrder);
        model.addAttribute("schedules", schedules);
        model.addAttribute("stations", stations);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(entriesPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "station-entries";
    }

    @GetMapping("/{id}")
    public String viewStationEntry(@PathVariable Integer id, Model model) {
        StationEntry entry = stationEntryService.getById(id);
        model.addAttribute("entry", entry);
        return "station-entry-view";
    }

    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer returnTo,
            Model model) {

        StationEntry entry = new StationEntry();

        if (scheduleId != null) {
            Schedule schedule = scheduleService.getById(scheduleId);
            entry.setSchedule(schedule);
        }

        if (stationId != null) {
            Station station = stationService.getById(stationId);
            entry.setStation(station);
        }

        // Устанавливаем порядок по умолчанию
        entry.setSegmentOrder(1);

        List<Schedule> schedules = scheduleService.list("", Pageable.unpaged()).getContent();
        List<Station> stations = stationService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("entry", entry);
        model.addAttribute("schedules", schedules);
        model.addAttribute("stations", stations);
        model.addAttribute("returnTo", returnTo);

        return "station-entry-form";
    }

//    @PostMapping
//    public String createStationEntry(@ModelAttribute StationEntry entry, @RequestParam(required = false) Integer returnTo) {
//        stationEntryService.create(entry);
//        if (returnTo != null) {
//            return "redirect:/schedules/" + returnTo + "/edit";
//        }
//        return "redirect:/station-entries";
//    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, @RequestParam(required = false) Integer returnTo, Model model) {
        StationEntry entry = stationEntryService.getById(id);
        List<Schedule> schedules = scheduleService.list("", Pageable.unpaged()).getContent();
        List<Station> stations = stationService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("entry", entry);
        model.addAttribute("schedules", schedules);
        model.addAttribute("stations", stations);
        model.addAttribute("returnTo", returnTo);
        return "station-entry-form";
    }

//    @PostMapping("/{id}")
//    public String updateStationEntry(@PathVariable Integer id, @ModelAttribute StationEntry entry, @RequestParam(required = false) Integer returnTo) {
//        stationEntryService.update(id, entry);
//
//        if (returnTo != null) {
//            return "redirect:/schedules/" + returnTo + "/edit";
//        }
//        return "redirect:/station-entries";
//    }

    @PostMapping
    public String createStationEntry(
            @ModelAttribute StationEntry entry,
            @RequestParam Integer scheduleId,
            @RequestParam Integer stationId,
            @RequestParam(required = false) Integer returnTo) {

        // Получаем и устанавливаем Schedule
        Schedule schedule = scheduleService.getById(scheduleId);
        entry.setSchedule(schedule);

        // Получаем и устанавливаем Station
        Station station = stationService.getById(stationId);
        entry.setStation(station);

        stationEntryService.create(entry);

        if (returnTo != null) {
            return "redirect:/schedules/" + returnTo + "/edit";
        }
        return "redirect:/station-entries";
    }

    @PostMapping("/{id}")
    public String updateStationEntry(
            @PathVariable Integer id,
            @ModelAttribute StationEntry entry,
            @RequestParam(required = false) Integer scheduleId,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer returnTo) {

        // Для обновления также нужно установить связанные объекты
        if (scheduleId != null) {
            Schedule schedule = scheduleService.getById(scheduleId);
            entry.setSchedule(schedule);
        }

        if (stationId != null) {
            Station station = stationService.getById(stationId);
            entry.setStation(station);
        }

        stationEntryService.update(id, entry);

        if (returnTo != null) {
            return "redirect:/schedules/" + returnTo + "/edit";
        }
        return "redirect:/station-entries";
    }

    @PostMapping("/{id}/delete")
    public String deleteStationEntry(@PathVariable Integer id) {
        stationEntryService.delete(id);
        return "redirect:/station-entries";
    }
}