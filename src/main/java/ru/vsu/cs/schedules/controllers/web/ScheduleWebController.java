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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.ScheduleService;
import ru.vsu.cs.schedules.services.StationService;
import ru.vsu.cs.schedules.services.TrainService;
import ru.vsu.cs.schedules.specification.ScheduleSpecification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/schedules")
public class ScheduleWebController {

    private final StationService stationService;
    private final ScheduleService scheduleService;
    private final TrainService trainService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public ScheduleWebController(StationService stationService, ScheduleService scheduleService, TrainService trainService) {
        this.stationService = stationService;
        this.scheduleService = scheduleService;
        this.trainService = trainService;
    }

    @GetMapping
    public String listSchedules(
            @RequestParam(required = false) Integer trainId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime departureFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime departureTo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime arrivalFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime arrivalTo,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            Model model) {

        int pageSize = 10;

        if (!"id".equals(sortBy) && !"departureTime".equals(sortBy) &&
                !"arrivalTime".equals(sortBy) && !"train.number".equals(sortBy)) {
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

        Specification<Schedule> spec = Specification.where(null);

        spec = spec.and(ScheduleSpecification.hasTrainId(trainId));
        spec = spec.and(ScheduleSpecification.departureTimeBetween(departureFrom, departureTo));
        spec = spec.and(ScheduleSpecification.arrivalTimeBetween(arrivalFrom, arrivalTo));

        Page<Schedule> schedulesPage = scheduleService.list(spec, pageable);

        // Получаем список поездов для фильтра
        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();

        // Получаем выбранный поезд, если trainId указан
        Train selectedTrain = null;
        if (trainId != null) {
            try {
                selectedTrain = trainService.getById(trainId);
            } catch (Exception e) {
                // Если поезд не найден, оставляем selectedTrain = null
            }
        }

        model.addAttribute("schedules", schedulesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", schedulesPage.getTotalPages());
        model.addAttribute("totalItems", schedulesPage.getTotalElements());
        model.addAttribute("trainId", trainId);
        model.addAttribute("departureFrom", departureFrom != null ? departureFrom.format(dateFormatter) : "");
        model.addAttribute("departureTo", departureTo != null ? departureTo.format(dateFormatter) : "");
        model.addAttribute("arrivalFrom", arrivalFrom != null ? arrivalFrom.format(dateFormatter) : "");
        model.addAttribute("arrivalTo", arrivalTo != null ? arrivalTo.format(dateFormatter) : "");
        model.addAttribute("trains", trains);
        model.addAttribute("selectedTrain", selectedTrain);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(schedulesPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "schedules";
    }

    @GetMapping("/{id}")
    public String viewSchedule(@PathVariable Integer id, Model model) {
        Schedule schedule = scheduleService.getById(id);
        model.addAttribute("schedule", schedule);
        return "schedule-view";
    }

    @GetMapping("/new")
    public String showCreateForm(
            @RequestParam(required = false) Integer trainId,
            Model model) {

        Schedule schedule = new Schedule();

        // Устанавливаем текущее время по умолчанию
        LocalDateTime now = LocalDateTime.now();
        schedule.setDepartureTime(now);
        schedule.setArrivalTime(now.minusHours(1));

        // Если передан trainId, устанавливаем поезд
        if (trainId != null) {
            Train train = trainService.getById(trainId);
            schedule.setTrain(train);
        }

        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();

        model.addAttribute("schedule", schedule);
        model.addAttribute("trains", trains);
        return "schedule-form";
    }

    @PostMapping
    public String createSchedule(@ModelAttribute Schedule schedule) {
        scheduleService.create(schedule);
        return "redirect:/schedules";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Schedule schedule = scheduleService.getById(id);
        List<Train> trains = trainService.list("", Pageable.unpaged()).getContent();
        List<Station> stations = stationService.list("", Pageable.unpaged()).getContent();
        model.addAttribute("stations", stations);
        model.addAttribute("schedule", schedule);
        model.addAttribute("trains", trains);

        model.addAttribute("newEntry", new StationEntry());

//        return "schedule-form";
        return "schedule-edit-with-entries";
    }

//    @PostMapping("/{id}")
//    public String updateSchedule(@PathVariable Integer id, @ModelAttribute Schedule schedule) {
//        scheduleService.update(id, schedule);
//        return "redirect:/schedules";
//    }

    @PostMapping("/{id}")
    public String updateSchedule(
            @PathVariable Integer id,
            @RequestParam Integer trainId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime departureTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime arrivalTime,
            RedirectAttributes redirectAttributes) {

        try {
            Schedule existingSchedule = scheduleService.getById(id);

            Train train = trainService.getById(trainId);
            existingSchedule.setTrain(train);

            existingSchedule.setDepartureTime(departureTime);
            existingSchedule.setArrivalTime(arrivalTime);

            scheduleService.update(id, existingSchedule);

            redirectAttributes.addFlashAttribute("successMessage", "Schedule updated successfully");
            return "redirect:/schedules/" + id;

        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Schedule or Train not found");
            return "redirect:/schedules/" + id + "/edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/schedules/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteSchedule(@PathVariable Integer id) {
        scheduleService.delete(id);
        return "redirect:/schedules";
    }
}