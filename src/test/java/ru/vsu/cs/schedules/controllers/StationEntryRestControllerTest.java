package ru.vsu.cs.schedules.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.services.StationEntryService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationEntryRestController.class)
@AutoConfigureMockMvc
class StationEntryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StationEntryService stationEntryService;

    @Test
    @DisplayName("GET /api/station-entries: успешное получение пагинированного списка")
    void list_ShouldReturnPageOfStationEntries() throws Exception {
        StationEntry entry = StationEntry.builder().id(1).build();
        Page<StationEntry> page = new PageImpl<>(List.of(entry));

        Mockito.when(stationEntryService.list(eq("Воронеж"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/station-entries")
                        .param("q", "Воронеж")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/station-entries/{id}: успешный возврат записи по ID")
    void get_ShouldReturnStationEntry_WhenExists() throws Exception {
        int id = 1;
        StationEntry entry = StationEntry.builder().id(id).build();

        Mockito.when(stationEntryService.getById(id)).thenReturn(entry);

        mockMvc.perform(get("/api/station-entries/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("GET /api/station-entries/{id}: возврат 404 Not Found, если запись не найдена")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(stationEntryService.getById(id)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/station-entries/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/station-entries/{id}: успешное удаление")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/station-entries/{id}", id))
                .andExpect(status().isNoContent());

        verify(stationEntryService).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/station-entries/{id}: возврат 404 Not Found при удалении несуществующего элемента")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Not found")).when(stationEntryService).delete(id);

        mockMvc.perform(delete("/api/station-entries/{id}", id))
                .andExpect(status().isNotFound());
    }
}