package ru.vsu.cs.schedules.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.services.LocomotiveService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocomotiveRestController.class)
class LocomotiveRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LocomotiveService locomotiveService;

    @Test
    @DisplayName("GET /api/locomotives: успешное получение пагинированного списка")
    void list_ShouldReturnPageOfLocomotives() throws Exception {
        Locomotive locomotive = Locomotive.builder().id(1).model("ТЭП70").build();
        Page<Locomotive> page = new PageImpl<>(List.of(locomotive));

        Mockito.when(locomotiveService.list(eq("ТЭП"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/locomotives")
                        .param("q", "ТЭП")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].model").value("ТЭП70"));
    }

    @Test
    @DisplayName("GET /api/locomotives/{id}: успешный возврат локомотива по ID")
    void get_ShouldReturnLocomotive_WhenExists() throws Exception {
        int id = 1;
        Locomotive locomotive = Locomotive.builder().id(id).model("ВЛ80").build();

        Mockito.when(locomotiveService.getById(id)).thenReturn(locomotive);

        mockMvc.perform(get("/api/locomotives/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.model").value("ВЛ80"));
    }

    @Test
    @DisplayName("GET /api/locomotives/{id}: возврат 404 Not Found, если локомотив не найден")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(locomotiveService.getById(id)).thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/api/locomotives/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/locomotives: успешное создание локомотива")
    void create_ShouldReturnCreatedLocomotive() throws Exception {
        Locomotive input = Locomotive.builder().model("Сапсан").build();
        Locomotive saved = Locomotive.builder().id(1).model("Сапсан").build();

        Mockito.when(locomotiveService.create(any(Locomotive.class))).thenReturn(saved);

        mockMvc.perform(post("/api/locomotives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.model").value("Сапсан"));

        verify(locomotiveService).create(any(Locomotive.class));
    }

    @Test
    @DisplayName("PUT /api/locomotives/{id}: успешное обновление данных локомотива")
    void update_ShouldReturnUpdatedLocomotive_WhenExists() throws Exception {
        int id = 1;
        Locomotive input = Locomotive.builder().model("Ласточка").build();
        Locomotive updated = Locomotive.builder().id(id).model("Ласточка").build();

        Mockito.when(locomotiveService.update(eq(id), any(Locomotive.class))).thenReturn(updated);

        mockMvc.perform(put("/api/locomotives/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.model").value("Ласточка"));

        verify(locomotiveService).update(eq(id), any(Locomotive.class));
    }

    @Test
    @DisplayName("PUT /api/locomotives/{id}: возврат 404 Not Found при попытке обновить несуществующий локомотив")
    void update_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Locomotive input = Locomotive.builder().model("Неизвестный").build();

        Mockito.when(locomotiveService.update(eq(id), any(Locomotive.class)))
                .thenThrow(new NotFoundException("Not found"));

        mockMvc.perform(put("/api/locomotives/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/locomotives/{id}: успешное удаление (24 NoContent)")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/locomotives/{id}", id))
                .andExpect(status().isNoContent());

        verify(locomotiveService).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/locomotives/{id}: возврат 404 Not Found при удалении несуществующего локомотива")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Not found")).when(locomotiveService).delete(id);

        mockMvc.perform(delete("/api/locomotives/{id}", id))
                .andExpect(status().isNotFound());
    }
}