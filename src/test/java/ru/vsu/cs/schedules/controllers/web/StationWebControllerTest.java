package ru.vsu.cs.schedules.controllers.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.StationService;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationWebController.class)
class StationWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StationService stationService;

    @MockitoBean
    private TrainService trainService;

    @Test
    @DisplayName("listStations: успешное отображение списка станций с параметрами по умолчанию")
    void listStations_ShouldReturnViewWithAttributes_WhenDefaultsUsed() throws Exception {
        Station station = Station.builder().id(1).name("Воронеж-1").build();
        Page<Station> page = new PageImpl<>(List.of(station));

        Mockito.when(stationService.list(eq(""), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/stations"))
                .andExpect(status().isOk())
                .andExpect(view().name("stations"))
                .andExpect(model().attributeExists("stations", "currentPage", "totalPages", "totalItems", "startPage", "endPage"))
                .andExpect(model().attribute("search", ""))
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("listStations: сброс параметров sortBy и direction на значения по умолчанию при передаче некорректных строк")
    void listStations_ShouldFallbackToDefaults_WhenSortParametersAreInvalid() throws Exception {
        Page<Station> emptyPage = new PageImpl<>(List.of());
        Mockito.when(stationService.list(eq("Центр"), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/stations")
                        .param("search", "Центр")
                        .param("sortBy", "not_exist_field")
                        .param("direction", "wrong_direction"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("viewStation: отображение карточки станции и списка проходящих поездов")
    void viewStation_ShouldReturnViewWithStationAndTrains() throws Exception {
        int id = 1;
        Station station = Station.builder().id(id).name("Лиски").build();
        Train train = Train.builder().id(10).number("042А").build();

        Mockito.when(stationService.getById(id)).thenReturn(station);
        Mockito.when(trainService.list(id)).thenReturn(List.of(train));

        mockMvc.perform(get("/stations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("station-view"))
                .andExpect(model().attribute("station", station))
                .andExpect(model().attribute("trains", List.of(train)));
    }

    @Test
    @DisplayName("showCreateForm: отображение формы добавления новой станции")
    void showCreateForm_ShouldReturnFormWithNewStation() throws Exception {
        mockMvc.perform(get("/stations/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("station-form"))
                .andExpect(model().attributeExists("station"));
    }

    @Test
    @DisplayName("createStation: создание станции через POST и редирект на список")
    void createStation_ShouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/stations")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Придача"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stations"));

        verify(stationService).create(any(Station.class));
    }

    @Test
    @DisplayName("showEditForm: отображение формы изменения существующей станции")
    void showEditForm_ShouldReturnFormWithStation() throws Exception {
        int id = 2;
        Station station = Station.builder().id(id).name("Отрожка").build();
        Mockito.when(stationService.getById(id)).thenReturn(station);

        mockMvc.perform(get("/stations/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("station-form"))
                .andExpect(model().attribute("station", station));
    }

    @Test
    @DisplayName("updateStation: обновление полей станции через POST и редирект")
    void updateStation_ShouldUpdateAndRedirect() throws Exception {
        mockMvc.perform(post("/stations/{id}", 3)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Новое Название"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stations"));

        verify(stationService).update(eq(3), any(Station.class));
    }

    @Test
    @DisplayName("deleteStation: успешное удаление станции")
    void deleteStation_ShouldRedirect_WhenSuccess() throws Exception {
        mockMvc.perform(post("/stations/{id}/delete", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stations"));

        verify(stationService).delete(eq(1), any(RedirectAttributes.class));
    }

    @Test
    @DisplayName("deleteStation: перехват NotFoundException и запись сообщения в Flash-атрибуты")
    void deleteStation_ShouldFlashErrorMessage_WhenNotFound() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Station not found")).when(stationService).delete(eq(id), any(RedirectAttributes.class));

        mockMvc.perform(post("/stations/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stations"))
                .andExpect(flash().attribute("errorMessage", "Station not found"));
    }

    @Test
    @DisplayName("deleteStation: перехват IllegalStateException при нарушении констреинтов зависимостей")
    void deleteStation_ShouldFlashErrorMessage_WhenIllegalState() throws Exception {
        int id = 4;
        doThrow(new IllegalStateException("Cannot delete station with existing active schedules")).when(stationService).delete(eq(id), any(RedirectAttributes.class));

        mockMvc.perform(post("/stations/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stations"))
                .andExpect(flash().attribute("errorMessage", "Cannot delete station with existing active schedules"));
    }
}