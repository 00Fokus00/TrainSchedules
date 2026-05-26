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
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.services.TrainCarriageService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainCarriageRestController.class)
@AutoConfigureMockMvc
class TrainCarriageRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainCarriageService trainCarriageService;

    @Test
    @DisplayName("GET /api/train-carriages: успешное получение пагинированного списка вагонов")
    void list_ShouldReturnPageOfCarriages() throws Exception {
        TrainCarriage carriage = TrainCarriage.builder().id(1).type("Купе").build();
        Page<TrainCarriage> page = new PageImpl<>(List.of(carriage));

        Mockito.when(trainCarriageService.list(eq("Купе"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/train-carriages")
                        .param("q", "Купе")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].type").value("Купе"));
    }

    @Test
    @DisplayName("GET /api/train-carriages/{id}: успешный возврат вагона по ID")
    void get_ShouldReturnCarriage_WhenExists() throws Exception {
        int id = 1;
        TrainCarriage carriage = TrainCarriage.builder().id(id).type("Плацкарт").build();

        Mockito.when(trainCarriageService.getById(eq(id))).thenReturn(carriage);

        mockMvc.perform(get("/api/train-carriages/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value("Плацкарт"));
    }

    @Test
    @DisplayName("GET /api/train-carriages/{id}: возврат 404 Not Found, если вагон не найден")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(trainCarriageService.getById(eq(id))).thenThrow(new NotFoundException("Carriage not found"));

        mockMvc.perform(get("/api/train-carriages/{id}", id))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("DELETE /api/train-carriages/{id}: успешное удаление вагона")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/train-carriages/{id}", id))
                .andExpect(status().isNoContent());

        verify(trainCarriageService).delete(eq(id));
    }

    @Test
    @DisplayName("DELETE /api/train-carriages/{id}: возврат 404 Not Found при удалении несуществующего вагона")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Carriage not found")).when(trainCarriageService).delete(eq(id));

        mockMvc.perform(delete("/api/train-carriages/{id}", id))
                .andExpect(status().isNotFound());
    }
}