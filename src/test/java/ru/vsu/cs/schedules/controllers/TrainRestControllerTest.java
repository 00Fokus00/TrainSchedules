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
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainRestController.class)
@AutoConfigureMockMvc
class TrainRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainService trainService;

    @Test
    @DisplayName("GET /api/trains: успешное получение пагинированного списка поездов")
    void list_ShouldReturnPageOfTrains() throws Exception {
        Train train = Train.builder().id(1).number("042А").build();
        Page<Train> page = new PageImpl<>(List.of(train));

        Mockito.when(trainService.list(eq("042"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/trains")
                        .param("q", "042")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].number").value("042А"));
    }

    @Test
    @DisplayName("GET /api/trains/{id}: успешный возврат поезда по ID")
    void get_ShouldReturnTrain_WhenExists() throws Exception {
        int id = 1;
        Train train = Train.builder().id(id).number("001А").build();

        Mockito.when(trainService.getById(eq(id))).thenReturn(train);

        mockMvc.perform(get("/api/trains/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.number").value("001А"));
    }

    @Test
    @DisplayName("GET /api/trains/{id}: возврат 404 Not Found, если поезд не найден")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(trainService.getById(eq(id))).thenThrow(new NotFoundException("Train not found"));

        mockMvc.perform(get("/api/trains/{id}", id))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("DELETE /api/trains/{id}: успешное удаление поезда")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/trains/{id}", id))
                .andExpect(status().isNoContent());

        verify(trainService).delete(eq(id));
    }

    @Test
    @DisplayName("DELETE /api/trains/{id}: возврат 404 Not Found при удалении несуществующего поезда")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Train not found")).when(trainService).delete(eq(id));

        mockMvc.perform(delete("/api/trains/{id}", id))
                .andExpect(status().isNotFound());
    }
}