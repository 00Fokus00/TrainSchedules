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
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.services.StationService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationRestController.class)
@AutoConfigureMockMvc
class StationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StationService stationService;


    @Test
    @DisplayName("GET /api/stations/{id}: успешный возврат станции по ID")
    void get_ShouldReturnStation_WhenExists() throws Exception {
        int id = 1;
        Station station = Station.builder().id(id).name("Москва Павелецкая").build();

        Mockito.when(stationService.getById(id)).thenReturn(station);

        mockMvc.perform(get("/api/stations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Москва Павелецкая"));
    }

    @Test
    @DisplayName("GET /api/stations/{id}: возврат 404 Not Found, если станция не найдена")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(stationService.getById(id)).thenThrow(new NotFoundException("Station not found"));

        mockMvc.perform(get("/api/stations/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/stations/{id}: успешное удаление станции")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/stations/{id}", id))
                .andExpect(status().isNoContent());

        verify(stationService).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/stations/{id}: возврат 404 Not Found при удалении несуществующей станции")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Station not found")).when(stationService).delete(id);

        mockMvc.perform(delete("/api/stations/{id}", id))
                .andExpect(status().isNotFound());
    }
}