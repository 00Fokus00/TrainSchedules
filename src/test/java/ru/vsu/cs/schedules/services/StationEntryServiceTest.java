package ru.vsu.cs.schedules.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.cs.schedules.exception.ValidationException;
import ru.vsu.cs.schedules.models.Schedule;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.repositories.StationEntryRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationEntryServiceTest {

    @Mock
    private StationEntryRepo stationEntryRepo;

    @InjectMocks
    private StationEntryService stationEntryService;

    private Schedule createTestSchedule(Integer id) {
        return Schedule.builder().id(id).build();
    }

    private Station createTestStation(String name) {
        return Station.builder().name(name).build();
    }

    @Test
    @DisplayName("create: ошибка, если прибытие позже отправления")
    void create_ShouldThrowException_WhenArrivalIsAfterDeparture() {
        LocalDateTime now = LocalDateTime.now();
        StationEntry invalidEntry = StationEntry.builder()
                .arrivalTime(now.plusHours(2))
                .departureTime(now.plusHours(1))
                .segmentOrder(1)
                .schedule(createTestSchedule(1))
                .build();

        assertThatThrownBy(() -> stationEntryService.create(invalidEntry))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Arrival time must be before departure time");

        verify(stationEntryRepo, never()).save(any());
    }

    @Test
    @DisplayName("create: ошибка, если segmentOrder меньше или равен нулю")
    void create_ShouldThrowException_WhenOrderIsZeroOrNegative() {
        // Given
        StationEntry invalidEntry = StationEntry.builder()
                .arrivalTime(LocalDateTime.now())
                .departureTime(LocalDateTime.now().plusHours(1))
                .segmentOrder(0) // Ошибка!
                .schedule(createTestSchedule(1))
                .build();

        assertThatThrownBy(() -> stationEntryService.create(invalidEntry))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Segment order must be positive");
    }

    @Test
    @DisplayName("create: ошибка, если обнаружено пересечение интервалов времени с другой станцией")
    void create_ShouldThrowException_WhenTimeConflictDetected() {
        Schedule schedule = createTestSchedule(1);
        LocalDateTime arrival = LocalDateTime.now();
        LocalDateTime departure = arrival.plusHours(1);

        StationEntry newEntry = StationEntry.builder()
                .arrivalTime(arrival)
                .departureTime(departure)
                .segmentOrder(1)
                .schedule(schedule)
                .build();

        Station conflictingStation = createTestStation("Воронеж-1");
        StationEntry conflictingEntry = StationEntry.builder().station(conflictingStation).build();

        Mockito.when(stationEntryRepo.findTimeConflicts(1, null, arrival, departure))
                .thenReturn(List.of(conflictingEntry));

        assertThatThrownBy(() -> stationEntryService.create(newEntry))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Time conflicts with existing station entry: Воронеж-1");
    }

    @Test
    @DisplayName("create: сдвиг существующих станций вверх при вставке в середину")
    void create_ShouldShiftEntriesUp_WhenInsertedInMiddle() {
        Schedule schedule = createTestSchedule(1);
        LocalDateTime now = LocalDateTime.now();

        StationEntry newEntry = StationEntry.builder()
                .segmentOrder(2)
                .schedule(schedule)
                .arrivalTime(now)
                .departureTime(now.plusHours(1))
                .build();

        StationEntry existing2 = StationEntry.builder().id(10).segmentOrder(2).schedule(schedule).build();
        StationEntry existing3 = StationEntry.builder().id(11).segmentOrder(3).schedule(schedule).build();

        List<StationEntry> existingList = new ArrayList<>(Arrays.asList(existing2, existing3));

        Mockito.when(stationEntryRepo.findByScheduleId(1)).thenReturn(existingList);

        Mockito.when(stationEntryRepo.findTimeConflicts(any(), any(), any(), any())).thenReturn(new ArrayList<>());

        Mockito.when(stationEntryRepo.save(any(StationEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        stationEntryService.create(newEntry);

        assertThat(existing3.getSegmentOrder()).isEqualTo(4);
        assertThat(existing2.getSegmentOrder()).isEqualTo(3);

        Mockito.verify(stationEntryRepo, Mockito.times(3)).save(any(StationEntry.class));
    }

    @Test
    @DisplayName("delete: успешное удаление и сдвиг оставшихся станций вниз")
    void delete_ShouldDeleteEntryAndShiftRemainingEntriesDown() {
        int deleteId = 100;
        Schedule schedule = createTestSchedule(1);
        StationEntry entryToDelete = StationEntry.builder()
                .id(deleteId)
                .segmentOrder(2)
                .schedule(schedule)
                .build();

        StationEntry dynamic3 = StationEntry.builder().id(101).segmentOrder(3).schedule(schedule).build();
        StationEntry dynamic4 = StationEntry.builder().id(102).segmentOrder(4).schedule(schedule).build();

        Mockito.when(stationEntryRepo.findById(deleteId)).thenReturn(Optional.of(entryToDelete));
        Mockito.when(stationEntryRepo.findByScheduleIdAndSegmentOrderGreaterThan(1, 2))
                .thenReturn(Arrays.asList(dynamic3, dynamic4));

        stationEntryService.delete(deleteId);

        verify(stationEntryRepo).deleteById(deleteId);

        assertThat(dynamic3.getSegmentOrder()).isEqualTo(2);
        assertThat(dynamic4.getSegmentOrder()).isEqualTo(3);

        verify(stationEntryRepo).save(dynamic3);
        verify(stationEntryRepo).save(dynamic4);
    }


    @Test
    @DisplayName("validateScheduleTimeSequence: ошибка, если прибытие на текущую станцию раньше отправления с предыдущей")
    void validateScheduleTimeSequence_ShouldThrowException_WhenArrivalIsBeforePreviousDeparture() {
        int scheduleId = 5;
        Station stationA = createTestStation("Станция А");
        Station stationB = createTestStation("Станция Б");

        LocalDateTime time = LocalDateTime.now();

        StationEntry entry1 = StationEntry.builder()
                .station(stationA)
                .segmentOrder(1)
                .arrivalTime(time)
                .departureTime(time.plusHours(2))
                .build();

        StationEntry entry2 = StationEntry.builder()
                .station(stationB)
                .segmentOrder(2)
                .arrivalTime(time.plusHours(1))
                .departureTime(time.plusHours(3))
                .build();

        Mockito.when(stationEntryRepo.findByScheduleId(scheduleId)).thenReturn(Arrays.asList(entry2, entry1));

        assertThatThrownBy(() -> stationEntryService.validateScheduleTimeSequence(scheduleId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Arrival at Станция Б must be after departure from previous station");
    }
}