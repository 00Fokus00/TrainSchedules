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
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.repositories.ScheduleRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepo scheduleRepo;

    @InjectMocks
    private ScheduleService scheduleService;


    @Test
    @DisplayName("list с Pageable: должен возвращать страницу расписаний")
    void list_WithPageable_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Schedule schedule = Schedule.builder().departureTime(LocalDateTime.now()).build();
        Page<Schedule> expectedPage = new PageImpl<>(List.of(schedule));

        Mockito.when(scheduleRepo.findAll(pageable)).thenReturn(expectedPage);

        Page<Schedule> actualPage = scheduleService.list("ignored_query", pageable);

        assertThat(actualPage).hasSize(1);
        verify(scheduleRepo).findAll(pageable);
    }

    @Test
    @DisplayName("list со Specification: должен фильтровать расписания")
    void list_WithSpecification_ShouldReturnFilteredPage() {
        Specification<Schedule> spec = Mockito.mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Schedule> expectedPage = new PageImpl<>(List.of());

        Mockito.when(scheduleRepo.findAll(spec, pageable)).thenReturn(expectedPage);

        Page<Schedule> actualPage = scheduleService.list(spec, pageable);

        assertThat(actualPage).isEmpty();
        verify(scheduleRepo).findAll(spec, pageable);
    }


    @Test
    @DisplayName("getById: должен возвращать расписание, если оно существует")
    void getById_ShouldReturnSchedule_WhenExists() {
        int id = 1;
        Schedule expected = Schedule.builder().id(id).build();
        Mockito.when(scheduleRepo.findById(id)).thenReturn(Optional.of(expected));

        Schedule actual = scheduleService.getById(id);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getById: должен выбрасывать NotFoundException, если расписание не найдено")
    void getById_ShouldThrowNotFoundException_WhenNotFound() {
        int id = 99;
        Mockito.when(scheduleRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleService.getById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Schedule not found: 99");
    }

    @Test
    @DisplayName("create: должен успешно сохранять расписание")
    void create_ShouldSaveSchedule() {
        // Given
        Schedule input = Schedule.builder().departureTime(LocalDateTime.now()).build();
        Schedule saved = Schedule.builder().id(5).departureTime(input.getDepartureTime()).build();

        Mockito.when(scheduleRepo.save(input)).thenReturn(saved);

        Schedule result = scheduleService.create(input);

        assertThat(result.getId()).isEqualTo(5);
        verify(scheduleRepo).save(input);
    }


    @Test
    @DisplayName("update: должен успешно обновлять поля, если время отправления раньше прибытия")
    void update_ShouldModifySchedule_WhenTimesAreValid() {
        int id = 10;
        LocalDateTime departure = LocalDateTime.of(2026, 5, 25, 12, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, 5, 25, 15, 0); // После отправления

        Train train = Train.builder().id(1).number("042А").build();
        Schedule existing = Schedule.builder().id(id).build();

        Schedule updateData = Schedule.builder()
                .train(train)
                .departureTime(departure)
                .arrivalTime(arrival)
                .build();

        Mockito.when(scheduleRepo.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(scheduleRepo.save(existing)).thenReturn(existing);

        Schedule result = scheduleService.update(id, updateData);

        assertThat(result.getTrain()).isEqualTo(train);
        assertThat(result.getDepartureTime()).isEqualTo(departure);
        assertThat(result.getArrivalTime()).isEqualTo(arrival);
        verify(scheduleRepo).save(existing);
    }

    @Test
    @DisplayName("update: должен выбрасывать IllegalArgumentException, если отправление позже прибытия")
    void update_ShouldThrowIllegalArgumentException_WhenDepartureIsAfterArrival() {
        // Given
        int id = 10;
        LocalDateTime departure = LocalDateTime.of(2026, 5, 25, 16, 0);
        LocalDateTime arrival = LocalDateTime.of(2026, 5, 25, 14, 0); // Ошибка! До отправления

        Schedule existing = Schedule.builder().id(id).build();
        Schedule updateData = Schedule.builder()
                .departureTime(departure)
                .arrivalTime(arrival)
                .build();

        Mockito.when(scheduleRepo.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> scheduleService.update(id, updateData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Departure time must be before arrival time");

        verify(scheduleRepo, never()).save(any());
    }


    @Test
    @DisplayName("delete: должен успешно удалять существующее расписание")
    void delete_ShouldDelete_WhenScheduleExists() {
        int id = 7;
        Mockito.when(scheduleRepo.existsById(id)).thenReturn(true);

        scheduleService.delete(id);

        verify(scheduleRepo).deleteById(id);
    }

    @Test
    @DisplayName("delete: должен выбрасывать NotFoundException, если расписания нет")
    void delete_ShouldThrowNotFoundException_WhenScheduleDoesNotExist() {
        int id = 88;
        Mockito.when(scheduleRepo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> scheduleService.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(scheduleRepo, never()).deleteById(any());
    }
}