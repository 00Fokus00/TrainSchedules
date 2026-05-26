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
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.services.LocomotiveService;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainWebController.class)
class TrainWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainService trainService;

    @MockitoBean
    private LocomotiveService locomotiveService;

    @Test
    @DisplayName("listTrains: успешный вывод списка поездов со значениями по умолчанию")
    void listTrains_ShouldReturnViewWithAttributes_WhenDefaultsUsed() throws Exception {
        Train train = Train.builder().id(1).number("042А").maxSpeed(140).build();
        Page<Train> trainPage = new PageImpl<>(List.of(train));

        Mockito.when(trainService.list(any(Specification.class), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/trains"))
                .andExpect(status().isOk())
                .andExpect(view().name("trains"))
                .andExpect(model().attributeExists("trains", "currentPage", "totalPages", "totalItems"))
                .andExpect(model().attribute("minSpeed", 0))
                .andExpect(model().attribute("maxSpeed", 500))
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("listTrains: автоматический сброс сортировки при передаче некорректных параметров")
    void listTrains_ShouldFallbackToDefaults_WhenSortParametersAreInvalid() throws Exception {
        Page<Train> trainPage = new PageImpl<>(List.of());
        Mockito.when(trainService.list(any(Specification.class), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/trains")
                        .param("sortBy", "invalid_field")
                        .param("direction", "invalid_direction"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("viewTrain: успешное отображение карточки поезда по ID")
    void viewTrain_ShouldReturnDetailsView() throws Exception {
        int id = 1;
        Train train = Train.builder().id(id).number("102Я").build();
        Mockito.when(trainService.getById(id)).thenReturn(train);

        mockMvc.perform(get("/trains/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("train-view"))
                .andExpect(model().attribute("train", train));
    }

    @Test
    @DisplayName("showCreateForm: инициализация формы создания поезда и загрузка локомотивов")
    void showCreateForm_ShouldReturnFormWithLocomotives() throws Exception {
        Locomotive locomotive = Locomotive.builder().id(1).model("ТЭП70").build();
        Page<Locomotive> locomotivePage = new PageImpl<>(List.of(locomotive));

        Mockito.when(locomotiveService.list(eq(""), any(Pageable.class))).thenReturn(locomotivePage);

        mockMvc.perform(get("/trains/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("train-form"))
                .andExpect(model().attributeExists("train"))
                .andExpect(model().attribute("locomotives", List.of(locomotive)));
    }

    @Test
    @DisplayName("createTrain: обработка отправки формы создания и редирект")
    void createTrain_ShouldSaveAndRedirect() throws Exception {
        mockMvc.perform(post("/trains")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("number", "740Б")
                        .param("maxSpeed", "160")
                        .param("locomotive.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trains"));

        verify(trainService).create(any(Train.class));
    }

    @Test
    @DisplayName("showEditForm: загрузка данных поезда и локомотивов для формы редактирования")
    void showEditForm_ShouldReturnFormWithPrepopulatedData() throws Exception {
        int id = 1;
        Train train = Train.builder().id(id).number("001А").build();
        Page<Locomotive> locomotivePage = new PageImpl<>(List.of());

        Mockito.when(trainService.getById(id)).thenReturn(train);
        Mockito.when(locomotiveService.list(eq(""), any(Pageable.class))).thenReturn(locomotivePage);

        mockMvc.perform(get("/trains/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("train-form"))
                .andExpect(model().attribute("train", train))
                .andExpect(model().attributeExists("locomotives"));
    }

    @Test
    @DisplayName("updateTrain: отправка измененных данных через POST и редирект")
    void updateTrain_ShouldUpdateAndRedirect() throws Exception {
        int id = 1;

        mockMvc.perform(post("/trains/{id}", id)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("number", "001А-Измененный")
                        .param("maxSpeed", "120"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trains"));

        verify(trainService).update(eq(id), any(Train.class));
    }

    @Test
    @DisplayName("deleteTrain: удаление поезда и редирект на общий список")
    void deleteTrain_ShouldDeleteAndRedirect() throws Exception {
        int id = 1;

        mockMvc.perform(post("/trains/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trains"));

        verify(trainService).delete(id);
    }
}