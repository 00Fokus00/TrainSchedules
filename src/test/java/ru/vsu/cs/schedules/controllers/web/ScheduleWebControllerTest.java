package ru.vsu.cs.schedules.controllers.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Используем актуальный API
import org.springframework.test.web.servlet.MockMvc;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.ScheduleService;
import ru.vsu.cs.schedules.services.StationService;
import ru.vsu.cs.schedules.services.TrainService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleWebController.class)
class ScheduleWebControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private ScheduleService scheduleService;

    @MockitoBean
    private TrainService trainService;

    @MockitoBean
    private StationService stationService;


    @Test
    @DisplayName("listSchedules: успешный возврат представления со всеми атрибутами фильтрации")
    void listSchedules_ShouldReturnViewWithModelAttributes() throws Exception {
        Schedule schedule = Schedule.builder().id(1).departureTime(LocalDateTime.now()).arrivalTime(LocalDateTime.now().plusHours(2)).build();
        Page<Schedule> page = new PageImpl<>(List.of(schedule));

        Train train = Train.builder().id(10).number("042А").build();
        Page<Train> unpagedTrains = new PageImpl<>(List.of(train));

        Mockito.when(scheduleService.list(any(Specification.class), any(Pageable.class))).thenReturn(page);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(unpagedTrains);
        Mockito.when(trainService.getById(10)).thenReturn(train);

        mockMvc.perform(get("/schedules")
                        .param("trainId", "10")
                        .param("sortBy", "train.number")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedules"))
                .andExpect(model().attributeExists("schedules", "trains", "selectedTrain", "currentPage", "totalPages"))
                .andExpect(model().attribute("sortBy", "train.number"))
                .andExpect(model().attribute("direction", "desc"))
                .andExpect(model().attribute("trainId", 10));
    }

    @Test
    @DisplayName("listSchedules: сброс параметров сортировки при передаче невалидных полей")
    void listSchedules_ShouldFallbackToDefaults_WhenSortIsInvalid() throws Exception {
        Page<Schedule> emptyPage = new PageImpl<>(List.of());
        Page<Train> emptyTrains = new PageImpl<>(List.of());

        Mockito.when(scheduleService.list(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(emptyTrains);

        mockMvc.perform(get("/schedules")
                        .param("sortBy", "malicious_field_click_jacking")
                        .param("direction", "bad_dir"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }


    @Test
    @DisplayName("showCreateForm: инициализация дефолтного времени и списка поездов")
    void showCreateForm_ShouldProvideDefaultScheduleAndTrains() throws Exception {
        Page<Train> trainPage = new PageImpl<>(List.of(Train.builder().id(2).number("102М").build()));
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/schedules/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("schedule-form"))
                .andExpect(model().attributeExists("schedule", "trains"));
    }


    @Test
    @DisplayName("updateSchedule: успешное обновление данных и редирект на карточку")
    void updateSchedule_ShouldUpdateAndRedirect_WhenValid() throws Exception {
        // Given
        int id = 1;
        Schedule existing = Schedule.builder().id(id).build();
        Train train = Train.builder().id(5).number("777Х").build();

        Mockito.when(scheduleService.getById(id)).thenReturn(existing);
        Mockito.when(trainService.getById(5)).thenReturn(train);

        mockMvc.perform(post("/schedules/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trainId", "5")
                        .param("departureTime", "2026-05-25T12:00")
                        .param("arrivalTime", "2026-05-25T15:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/1"))
                .andExpect(flash().attribute("successMessage", "Schedule updated successfully"));

        verify(scheduleService).update(eq(id), any(Schedule.class));
    }

    @Test
    @DisplayName("updateSchedule: перехват NotFoundException и редирект на форму редактирования")
    void updateSchedule_ShouldFlashErrorMessage_WhenEntityNotFound() throws Exception {
        // Given
        int id = 1;
        Mockito.when(scheduleService.getById(id)).thenThrow(new NotFoundException("Schedule not found"));

        mockMvc.perform(post("/schedules/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trainId", "5")
                        .param("departureTime", "2026-05-25T12:00")
                        .param("arrivalTime", "2026-05-25T15:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/1/edit"))
                .andExpect(flash().attribute("errorMessage", "Schedule or Train not found"));
    }

    @Test
    @DisplayName("updateSchedule: перехват IllegalArgumentException (валидация времени)")
    void updateSchedule_ShouldFlashErrorMessage_WhenTimesAreInvalid() throws Exception {
        // Given
        int id = 1;
        Schedule existing = Schedule.builder().id(id).build();
        Train train = Train.builder().id(5).build();

        Mockito.when(scheduleService.getById(id)).thenReturn(existing);
        Mockito.when(trainService.getById(5)).thenReturn(train);

        Mockito.when(scheduleService.update(eq(id), any(Schedule.class)))
                .thenThrow(new IllegalArgumentException("Departure time must be before arrival time"));

        mockMvc.perform(post("/schedules/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("trainId", "5")
                        .param("departureTime", "2026-05-25T16:00")
                        .param("arrivalTime", "2026-05-25T14:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/1/edit"))
                .andExpect(flash().attribute("errorMessage", "Departure time must be before arrival time"));
    }


    @Test
    @DisplayName("deleteSchedule: успешное удаление")
    void deleteSchedule_ShouldRedirectToSchedules() throws Exception {
        mockMvc.perform(post("/schedules/{id}/delete", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules"));

        verify(scheduleService).delete(1);
    }
}