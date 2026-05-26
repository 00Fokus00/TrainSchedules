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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.services.ScheduleService;
import ru.vsu.cs.schedules.services.StationEntryService;
import ru.vsu.cs.schedules.services.StationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationEntryWebController.class)
class StationEntryWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StationEntryService stationEntryService;

    @MockitoBean
    private ScheduleService scheduleService;

    @MockitoBean
    private StationService stationService;

    @Test
    @DisplayName("listStationEntries: успешное отображение списка станций в расписании")
    void listStationEntries_ShouldReturnViewWithAttributes() throws Exception {
        StationEntry entry = StationEntry.builder().id(1).segmentOrder(2).build();
        Page<StationEntry> page = new PageImpl<>(List.of(entry));

        Page<Schedule> emptySchedules = new PageImpl<>(List.of());
        Page<Station> emptyStations = new PageImpl<>(List.of());

        Mockito.when(stationEntryService.list(any(Specification.class), any(Pageable.class))).thenReturn(page);
        Mockito.when(scheduleService.list(eq(""), any(Pageable.class))).thenReturn(emptySchedules);
        Mockito.when(stationService.list(eq(""), any(Pageable.class))).thenReturn(emptyStations);

        mockMvc.perform(get("/station-entries")
                        .param("sortBy", "segmentOrder")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("station-entries"))
                .andExpect(model().attributeExists("entries", "schedules", "stations", "currentPage", "totalPages"))
                .andExpect(model().attribute("sortBy", "segmentOrder"))
                .andExpect(model().attribute("direction", "desc"));
    }

    @Test
    @DisplayName("listStationEntries: сброс параметров сортировки при передаче невалидных полей")
    void listStationEntries_ShouldFallbackToDefaults_WhenSortParametersAreInvalid() throws Exception {
        Page<StationEntry> emptyPage = new PageImpl<>(List.of());
        Page<Schedule> emptySchedules = new PageImpl<>(List.of());
        Page<Station> emptyStations = new PageImpl<>(List.of());

        Mockito.when(stationEntryService.list(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);
        Mockito.when(scheduleService.list(eq(""), any(Pageable.class))).thenReturn(emptySchedules);
        Mockito.when(stationService.list(eq(""), any(Pageable.class))).thenReturn(emptyStations);

        mockMvc.perform(get("/station-entries")
                        .param("sortBy", "invalid_field_name")
                        .param("direction", "bad_direction"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("viewStationEntry: успешное отображение отдельной записи")
    void viewStationEntry_ShouldReturnDetailsView() throws Exception {
        int id = 15;
        StationEntry entry = StationEntry.builder().id(id).segmentOrder(3).build();
        Mockito.when(stationEntryService.getById(id)).thenReturn(entry);

        mockMvc.perform(get("/station-entries/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("station-entry-view"))
                .andExpect(model().attribute("entry", entry));
    }

    @Test
    @DisplayName("showCreateForm: инициализация формы создания с предзаполненными параметрами")
    void showCreateForm_ShouldPrepopulateFields() throws Exception {
        Schedule schedule = Schedule.builder().id(5).build();
        Station station = Station.builder().id(10).name("Воронеж-1").build();

        Page<Schedule> emptySchedules = new PageImpl<>(List.of());
        Page<Station> emptyStations = new PageImpl<>(List.of());

        Mockito.when(scheduleService.getById(5)).thenReturn(schedule);
        Mockito.when(stationService.getById(10)).thenReturn(station);
        Mockito.when(scheduleService.list(eq(""), any(Pageable.class))).thenReturn(emptySchedules);
        Mockito.when(stationService.list(eq(""), any(Pageable.class))).thenReturn(emptyStations);

        mockMvc.perform(get("/station-entries/new")
                        .param("scheduleId", "5")
                        .param("stationId", "10")
                        .param("returnTo", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("station-entry-form"))
                .andExpect(model().attributeExists("entry", "schedules", "stations"))
                .andExpect(model().attribute("returnTo", 5));
    }

    @Test
    @DisplayName("createStationEntry: создание сущности и редирект обратно на редактирование расписания")
    void createStationEntry_ShouldRedirectToScheduleEdit_WhenReturnToIsPresent() throws Exception {
        Schedule schedule = Schedule.builder().id(5).build();
        Station station = Station.builder().id(10).build();

        Mockito.when(scheduleService.getById(5)).thenReturn(schedule);
        Mockito.when(stationService.getById(10)).thenReturn(station);

        mockMvc.perform(post("/station-entries")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("scheduleId", "5")
                        .param("stationId", "10")
                        .param("returnTo", "5")
                        .param("segmentOrder", "1")
                        .param("arrivalTime", "2026-05-25T14:00")
                        .param("departureTime", "2026-05-25T14:15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/5/edit"));

        verify(stationEntryService).create(any(StationEntry.class));
    }

    @Test
    @DisplayName("createStationEntry: создание сущности и стандартный редирект на список")
    void createStationEntry_ShouldRedirectToStationEntries_WhenReturnToIsAbsent() throws Exception {
        Schedule schedule = Schedule.builder().id(5).build();
        Station station = Station.builder().id(10).build();

        Mockito.when(scheduleService.getById(5)).thenReturn(schedule);
        Mockito.when(stationService.getById(10)).thenReturn(station);

        mockMvc.perform(post("/station-entries")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("scheduleId", "5")
                        .param("stationId", "10")
                        .param("segmentOrder", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/station-entries"));

        verify(stationEntryService).create(any(StationEntry.class));
    }

    @Test
    @DisplayName("updateStationEntry: успешное обновление со связанными объектами и редиректом")
    void updateStationEntry_ShouldUpdateAndRedirectToScheduleEdit() throws Exception {
        int id = 1;
        Schedule schedule = Schedule.builder().id(5).build();
        Station station = Station.builder().id(10).build();

        Mockito.when(scheduleService.getById(5)).thenReturn(schedule);
        Mockito.when(stationService.getById(10)).thenReturn(station);

        mockMvc.perform(post("/station-entries/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("scheduleId", "5")
                        .param("stationId", "10")
                        .param("returnTo", "5")
                        .param("segmentOrder", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedules/5/edit"));

        verify(stationEntryService).update(eq(id), any(StationEntry.class));
    }

    @Test
    @DisplayName("deleteStationEntry: успешное удаление записи")
    void deleteStationEntry_ShouldDeleteAndRedirect() throws Exception {
        int id = 1;

        mockMvc.perform(post("/station-entries/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/station-entries"));

        verify(stationEntryService).delete(id);
    }
}