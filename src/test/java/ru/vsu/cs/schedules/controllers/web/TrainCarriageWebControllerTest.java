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
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.services.TrainCarriageService;
import ru.vsu.cs.schedules.services.TrainService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainCarriageWebController.class)
class TrainCarriageWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainCarriageService trainCarriageService;

    @MockitoBean
    private TrainService trainService;

    @Test
    @DisplayName("listCarriages: успешное отображение списка вагонов с дефолтными параметрами")
    void listCarriages_ShouldReturnViewWithAttributes_WhenDefaultsUsed() throws Exception {
        TrainCarriage carriage = TrainCarriage.builder().id(1).type("Купе").build();
        Page<TrainCarriage> carriagePage = new PageImpl<>(List.of(carriage));
        Page<Train> trainPage = new PageImpl<>(List.of());

        Mockito.when(trainCarriageService.list(any(Specification.class), any(Pageable.class))).thenReturn(carriagePage);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/carriages"))
                .andExpect(status().isOk())
                .andExpect(view().name("carriages"))
                .andExpect(model().attributeExists("carriages", "trains", "currentPage", "totalPages"))
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("listCarriages: сброс параметров sortBy и direction при передаче невалидных значений")
    void listCarriages_ShouldFallbackToDefaults_WhenSortParametersAreInvalid() throws Exception {
        Page<TrainCarriage> carriagePage = new PageImpl<>(List.of());
        Page<Train> trainPage = new PageImpl<>(List.of());

        Mockito.when(trainCarriageService.list(any(Specification.class), any(Pageable.class))).thenReturn(carriagePage);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/carriages")
                        .param("sortBy", "non_existent_field")
                        .param("direction", "wrong_direction"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortBy", "id"))
                .andExpect(model().attribute("direction", "asc"));
    }

    @Test
    @DisplayName("viewCarriage: отображение карточки конкретного вагона")
    void viewCarriage_ShouldReturnDetailsView() throws Exception {
        int id = 5;
        TrainCarriage carriage = TrainCarriage.builder().id(id).type("Плацкарт").build();
        Mockito.when(trainCarriageService.getById(id)).thenReturn(carriage);

        mockMvc.perform(get("/carriages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("carriage-view"))
                .andExpect(model().attribute("carriage", carriage));
    }

    @Test
    @DisplayName("showCreateForm: инициализация формы создания с привязкой к поезду")
    void showCreateForm_ShouldPrepopulateTrain_WhenTrainIdIsProvided() throws Exception {
        int trainId = 10;
        Train train = Train.builder().id(trainId).number("042А").build();
        Page<Train> trainPage = new PageImpl<>(List.of(train));

        Mockito.when(trainService.getById(trainId)).thenReturn(train);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/carriages/new").param("trainId", String.valueOf(trainId)))
                .andExpect(status().isOk())
                .andExpect(view().name("carriage-form"))
                .andExpect(model().attributeExists("carriage", "trains"));
    }

    @Test
    @DisplayName("showEditForm: отображение формы редактирования вагона")
    void showEditForm_ShouldReturnFormWithCarriageAndTrains() throws Exception {
        int id = 1;
        TrainCarriage carriage = TrainCarriage.builder().id(id).type("Люкс").build();
        Page<Train> trainPage = new PageImpl<>(List.of());

        Mockito.when(trainCarriageService.getById(id)).thenReturn(carriage);
        Mockito.when(trainService.list(eq(""), any(Pageable.class))).thenReturn(trainPage);

        mockMvc.perform(get("/carriages/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("carriage-form"))
                .andExpect(model().attribute("carriage", carriage))
                .andExpect(model().attributeExists("trains"));
    }

    @Test
    @DisplayName("deleteCarriage: удаление вагона и редирект на список с сохранением контекста поезда")
    void deleteCarriage_ShouldDeleteAndRedirectWithTrainId() throws Exception {
        int id = 1;
        Train train = Train.builder().id(12).build();
        TrainCarriage carriage = TrainCarriage.builder().id(id).train(train).build();

        Mockito.when(trainCarriageService.getById(id)).thenReturn(carriage);

        mockMvc.perform(post("/carriages/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/carriages?trainId=12"));

        verify(trainCarriageService).delete(id);
    }
}