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
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.models.TrainCarriage;
import ru.vsu.cs.schedules.repositories.TrainCarriageRepo;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainCarriageServiceTest {

    @Mock
    private TrainCarriageRepo trainCarriageRepo;

    @InjectMocks
    private TrainCarriageService trainCarriageService;


    @Test
    @DisplayName("list с Pageable: должен возвращать страницу вагонов")
    void list_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        TrainCarriage carriage = TrainCarriage.builder().type("Купе").build();
        Page<TrainCarriage> expectedPage = new PageImpl<>(List.of(carriage));

        Mockito.when(trainCarriageRepo.findAll(pageable)).thenReturn(expectedPage);

        Page<TrainCarriage> actualPage = trainCarriageService.list("ignored", pageable);

        assertThat(actualPage).hasSize(1);
        verify(trainCarriageRepo).findAll(pageable);
    }

    @Test
    @DisplayName("list со Specification: должен фильтровать вагоны по спецификации")
    void list_WithSpecification_ShouldReturnFilteredPage() {
        Specification<TrainCarriage> spec = Mockito.mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<TrainCarriage> expectedPage = new PageImpl<>(List.of());

        Mockito.when(trainCarriageRepo.findAll(spec, pageable)).thenReturn(expectedPage);

        Page<TrainCarriage> actualPage = trainCarriageService.list(spec, pageable);

        assertThat(actualPage).isEmpty();
        verify(trainCarriageRepo).findAll(spec, pageable);
    }

    @Test
    @DisplayName("getById: должен возвращать вагон, если он найден")
    void getById_ShouldReturnCarriage_WhenExists() {
        int id = 1;
        TrainCarriage expected = TrainCarriage.builder().id(id).type("Плацкарт").build();
        Mockito.when(trainCarriageRepo.findById(id)).thenReturn(Optional.of(expected));

        TrainCarriage actual = trainCarriageService.getById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getType()).isEqualTo("Плацкарт");
    }

    @Test
    @DisplayName("getById: должен выбрасывать NotFoundException, если вагон не найден")
    void getById_ShouldThrowNotFoundException_WhenNotFound() {
        int id = 99;
        Mockito.when(trainCarriageRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainCarriageService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("TrainCarriage not found: 99");
    }

    @Test
    @DisplayName("create: должен успешно сохранять новый вагон")
    void create_ShouldSaveTrainCarriage() {
        TrainCarriage input = TrainCarriage.builder().type("СВ").build();
        TrainCarriage saved = TrainCarriage.builder().id(5).type("СВ").build();

        Mockito.when(trainCarriageRepo.save(input)).thenReturn(saved);

        TrainCarriage result = trainCarriageService.create(input);

        assertThat(result.getId()).isEqualTo(5);
        verify(trainCarriageRepo).save(input);
    }


    @Test
    @DisplayName("update: должен изменять поля существующего вагона")
    void update_ShouldModifyExistingCarriage() {
        int id = 10;
        Train train = Train.builder().id(2).number("042А").build();
        TrainCarriage existing = TrainCarriage.builder().id(id).type("Плацкарт").build();
        TrainCarriage updateData = TrainCarriage.builder().train(train).type("Люкс").build();

        Mockito.when(trainCarriageRepo.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(trainCarriageRepo.save(existing)).thenReturn(existing);

        TrainCarriage result = trainCarriageService.update(id, updateData);

        assertThat(result.getType()).isEqualTo("Люкс");
        assertThat(result.getTrain()).isEqualTo(train);
        verify(trainCarriageRepo).save(existing);
    }

    @Test
    @DisplayName("update: должен выбрасывать NotFoundException при попытке обновить несуществующий вагон")
    void update_ShouldThrowNotFoundException_WhenCarriageDoesNotExist() {
        int id = 404;
        TrainCarriage updateData = TrainCarriage.builder().type("Общий").build();
        Mockito.when(trainCarriageRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trainCarriageService.update(id, updateData))
                .isInstanceOf(NotFoundException.class);

        verify(trainCarriageRepo, never()).save(any());
    }


    @Test
    @DisplayName("delete: должен успешно удалять существующий вагон")
    void delete_ShouldDelete_WhenCarriageExists() {
        int id = 7;
        Mockito.when(trainCarriageRepo.existsById(id)).thenReturn(true);

        trainCarriageService.delete(id);

        verify(trainCarriageRepo).deleteById(id);
    }

    @Test
    @DisplayName("delete: должен выбрасывать NotFoundException, если вагона нет")
    void delete_ShouldThrowNotFoundException_WhenCarriageDoesNotExist() {
        int id = 88;
        Mockito.when(trainCarriageRepo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> trainCarriageService.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(trainCarriageRepo, never()).deleteById(any());
    }
}