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
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.services.ScheduleService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleRestController.class)
@AutoConfigureMockMvc
class ScheduleRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScheduleService scheduleService;

    @Test
    @DisplayName("GET /api/schedules: успешное получение пагинированного списка расписаний")
    void list_ShouldReturnPageOfSchedules() throws Exception {
        Schedule schedule = Schedule.builder().id(1).build();
        Page<Schedule> page = new PageImpl<>(List.of(schedule));

        Mockito.when(scheduleService.list(eq("Скорый"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/schedules")
                        .param("q", "Скорый")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/schedules/{id}: успешный возврат расписания по ID")
    void get_ShouldReturnSchedule_WhenExists() throws Exception {
        int id = 1;
        Schedule schedule = Schedule.builder().id(id).build();

        Mockito.when(scheduleService.getById(id)).thenReturn(schedule);

        mockMvc.perform(get("/api/schedules/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("GET /api/schedules/{id}: возврат 404 Not Found, если расписание не найдено")
    void get_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        Mockito.when(scheduleService.getById(id)).thenThrow(new NotFoundException("Schedule not found"));

        mockMvc.perform(get("/api/schedules/{id}", id))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("DELETE /api/schedules/{id}: успешное удаление расписания")
    void delete_ShouldReturnNoContent_WhenExists() throws Exception {
        int id = 1;

        mockMvc.perform(delete("/api/schedules/{id}", id))
                .andExpect(status().isNoContent());

        verify(scheduleService).delete(id);
    }

    @Test
    @DisplayName("DELETE /api/schedules/{id}: возврат 404 Not Found при удалении несуществующего расписания")
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Schedule not found")).when(scheduleService).delete(id);

        mockMvc.perform(delete("/api/schedules/{id}", id))
                .andExpect(status().isNotFound());
    }
}