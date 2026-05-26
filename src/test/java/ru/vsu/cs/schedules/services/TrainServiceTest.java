package ru.vsu.cs.schedules.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.repositories.TrainRepo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainServiceTest {

    @Mock
    private TrainRepo trainRepo;

    @InjectMocks
    private TrainService trainService;


    @Test
    @DisplayName("list: должен вернуть все поезда, если строка поиска пустая")
    void list_ShouldReturnAllTrains_WhenSearchIsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Train> expectedPage = new PageImpl<>(List.of(Train.builder().number("042А").build()));

        Mockito.when(trainRepo.findAll(pageable)).thenReturn(expectedPage);

        Page<Train> actualPage = trainService.list("   ", pageable);

        assertThat(actualPage).hasSize(1);
        verify(trainRepo).findAll(pageable);
        verify(trainRepo, never()).findByNumberContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("list: должен очистить строку от HTML-тегов и искать по номеру")
    void list_ShouldSanitizeAndSearch_WhenSearchIsProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        String dirtySearch = "<span>042А</span>";
        String cleanSearch = "042А";
        Page<Train> expectedPage = new PageImpl<>(List.of(Train.builder().number("042А").build()));

        Mockito.when(trainRepo.findByNumberContainingIgnoreCase(cleanSearch, pageable)).thenReturn(expectedPage);

        Page<Train> actualPage = trainService.list(dirtySearch, pageable);

        assertThat(actualPage).hasSize(1);
        verify(trainRepo).findByNumberContainingIgnoreCase(cleanSearch, pageable);
    }

    @Test
    @DisplayName("list по stationId: должен вернуть список поездов для конкретной станции")
    void list_WithStationId_ShouldReturnList() {
        int stationId = 5;
        List<Train> expectedList = List.of(Train.builder().number("102М").build());
        Mockito.when(trainRepo.findTrainByStationId(stationId)).thenReturn(expectedList);

        List<Train> actualList = trainService.list(stationId);

        assertThat(actualList).hasSize(1);
        verify(trainRepo).findTrainByStationId(stationId);
    }

    @Test
    @DisplayName("list со Specification: должен фильтровать поезда")
    void list_WithSpecification_ShouldReturnFilteredPage() {
        Specification<Train> spec = Mockito.mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Train> expectedPage = new PageImpl<>(Collections.emptyList());

        Mockito.when(trainRepo.findAll(spec, pageable)).thenReturn(expectedPage);

        Page<Train> actualPage = trainService.list(spec, pageable);

        assertThat(actualPage).isEmpty();
    }


    @Test
    @DisplayName("getById: должен возвращать поезд, если он найден")
    void getById_ShouldReturnTrain_WhenExists() {
        int id = 1;
        Train expected = Train.builder().id(id).number("042А").build();
        Mockito.when(trainRepo.findById(id)).thenReturn(Optional.of(expected));

        Train actual = trainService.getById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getByIdLocomotive: должен возвращать поезд по ID его локомотива")
    void getByIdLocomotive_ShouldReturnTrain() {
        int locomotiveId = 10;
        Train expected = Train.builder().id(1).number("042А").build();
        Mockito.when(trainRepo.findTrainByLocomotiveId(locomotiveId)).thenReturn(expected);

        Train actual = trainService.getByIdLocomotive(locomotiveId);

        assertThat(actual.getNumber()).isEqualTo("042А");
    }

    @Test
    @DisplayName("getByIdLocomotive: должен выбрасывать RuntimeException, если репозиторий выбросил NotFoundException")
    void getByIdLocomotive_ShouldThrowRuntimeException_WhenNotFoundExceptionOccurs() {
        int locomotiveId = 10;
        Mockito.when(trainRepo.findTrainByLocomotiveId(locomotiveId))
                .thenThrow(new NotFoundException("Not found"));

        assertThatThrownBy(() -> trainService.getByIdLocomotive(locomotiveId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Train not found: 10");
    }


    @Test
    @DisplayName("create: должен успешно сохранять поезд")
    void create_ShouldSaveTrain() {
        Train input = Train.builder().number("777Х").build();
        Mockito.when(trainRepo.save(input)).thenReturn(input);

        Train result = trainService.create(input);

        verify(trainRepo).save(input);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("update: должен успешно обновлять все поля существующего поезда")
    void update_ShouldModifyExistingTrain() {
        int id = 1;
        Train existing = Train.builder().id(id).number("Старый").maxSpeed(120).build();

        Locomotive newLocomotive = Locomotive.builder().id(2).model("ЭП20").build();
        Train updateData = Train.builder()
                .number("Новый")
                .maxSpeed(160)
                .locomotive(newLocomotive)
                .carriages(List.of())
                .schedules(List.of())
                .build();

        Mockito.when(trainRepo.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(trainRepo.save(existing)).thenReturn(existing);

        Train result = trainService.update(id, updateData);

        assertThat(result.getNumber()).isEqualTo("Новый");
        assertThat(result.getMaxSpeed()).isEqualTo(160);
        assertThat(result.getLocomotive()).isEqualTo(newLocomotive);
        verify(trainRepo).save(existing);
    }

    @Test
    @DisplayName("delete: должен успешно удалять существующий поезд")
    void delete_ShouldDelete_WhenTrainExists() {
        int id = 1;
        Mockito.when(trainRepo.existsById(id)).thenReturn(true);

        trainService.delete(id);

        verify(trainRepo).deleteById(id);
    }

    @Test
    @DisplayName("delete: должен выбрасывать NotFoundException, если поезда нет")
    void delete_ShouldThrowNotFoundException_WhenTrainDoesNotExist() {
        int id = 99;
        Mockito.when(trainRepo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> trainService.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(trainRepo, never()).deleteById(any());
    }
}