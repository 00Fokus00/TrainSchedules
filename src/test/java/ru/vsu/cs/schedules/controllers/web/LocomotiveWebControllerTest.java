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
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Новый импорт!
import org.springframework.test.web.servlet.MockMvc;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.LocomotiveService;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocomotiveWebController.class)
class LocomotiveWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocomotiveService locomotiveService;

    @MockitoBean
    private TrainService trainService;

    @Test
    @DisplayName("listLocomotives: успешное отображение списка с дефолтными параметрами")
    void listLocomotives_ShouldReturnViewWithAttributes_WhenDefaultsUsed() throws Exception {
        Locomotive loco = Locomotive.builder().id(1).model("ЧС7").status("active").build();
        Page<Locomotive> page = new PageImpl<>(List.of(loco));

        Mockito.when(locomotiveService.list(any(Specification.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/locomotives"))
                .andExpect(status().isOk())
                .andExpect(view().name("locomotives"))
                .andExpect(model().attributeExists("locomotives", "currentPage", "totalPages"))
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("viewLocomotive: отображение карточки локомотива и связанного поезда")
    void viewLocomotive_ShouldReturnViewWithLocoAndTrain() throws Exception {
        int id = 21;
        Locomotive expectedLoco = Locomotive.builder().id(id).model("ЧС7").build();
        Train expectedTrain = Train.builder().id(1).number("042А").build();

        Mockito.when(locomotiveService.getById(id)).thenReturn(expectedLoco);
        Mockito.when(trainService.getByIdLocomotive(id)).thenReturn(expectedTrain);

        mockMvc.perform(get("/locomotives/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("locomotive-view"))
                .andExpect(model().attribute("locomotive", expectedLoco))
                .andExpect(model().attribute("train", expectedTrain));
    }

    @Test
    @DisplayName("createLocomotive: сохранение через POST и редирект")
    void createLocomotive_ShouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/locomotives")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("model", "ЭП20")
                        .param("power", "7200")
                        .param("status", "active"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locomotives"));

        verify(locomotiveService).create(any(Locomotive.class));
    }

    @Test
    @DisplayName("deleteLocomotive: перехват NotFoundException и добавление Flash-ошибки")
    void deleteLocomotive_ShouldFlashErrorMessage_WhenNotFound() throws Exception {
        int id = 99;
        doThrow(new NotFoundException("Locomotive not found")).when(locomotiveService).delete(Mockito.eq(id), any());

        mockMvc.perform(post("/locomotives/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locomotives"))
                .andExpect(flash().attribute("errorMessage", "Locomotive not found"));
    }
}