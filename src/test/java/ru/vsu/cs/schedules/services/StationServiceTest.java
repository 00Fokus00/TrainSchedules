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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.repositories.StationEntryRepo;
import ru.vsu.cs.schedules.repositories.StationRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepo stationRepo;

    @Mock
    private StationEntryRepo stationEntryRepo;

    @InjectMocks
    private StationService stationService;


    @Test
    @DisplayName("list: возвращает полную страницу, если строка поиска пустая")
    void list_ShouldReturnAllStations_WhenSearchIsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Station> expectedPage = new PageImpl<>(List.of(Station.builder().name("Воронеж-1").build()));

        Mockito.when(stationRepo.findAll(pageable)).thenReturn(expectedPage);

        Page<Station> actualPage = stationService.list("   ", pageable);

        assertThat(actualPage).hasSize(1);
        verify(stationRepo).findAll(pageable);
        verify(stationRepo, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    @DisplayName("list: фильтрует по имени и санитизирует HTML-теги")
    void list_ShouldSanitizeAndSearch_WhenSearchIsProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchInput = "<b>Лиски</b>";
        String sanitizedOutput = "Лиски"; // Jsoup.clean уберет <b>
        Page<Station> expectedPage = new PageImpl<>(List.of(Station.builder().name("Лиски").build()));

        Mockito.when(stationRepo.findByNameContainingIgnoreCase(sanitizedOutput, pageable)).thenReturn(expectedPage);

        Page<Station> actualPage = stationService.list(searchInput, pageable);

        assertThat(actualPage).hasSize(1);
        verify(stationRepo).findByNameContainingIgnoreCase(sanitizedOutput, pageable);
    }


    @Test
    @DisplayName("getById: возвращает станцию, если она найдена")
    void getById_ShouldReturnStation_WhenExists() {
        int id = 1;
        Station station = Station.builder().id(id).name("Отрожка").build();
        Mockito.when(stationRepo.findById(id)).thenReturn(Optional.of(station));

        Station result = stationService.getById(id);

        assertThat(result.getName()).isEqualTo("Отрожка");
    }

    @Test
    @DisplayName("update: успешно обновляет и очищает от HTML имя станции")
    void update_ShouldSanitizeNameAndSave() {
        int id = 2;
        Station existing = Station.builder().id(id).name("Старое название").build();
        Station updateData = Station.builder().name("<script>alert()</script>Новое название").build();
        Station saved = Station.builder().id(id).name("Новое название").build();

        Mockito.when(stationRepo.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(stationRepo.save(any(Station.class))).thenReturn(saved);

        Station result = stationService.update(id, updateData);

        assertThat(result.getName()).isEqualTo("Новое название");
    }


    @Test
    @DisplayName("delete: ошибка NotFoundException, если станции нет в базе")
    void delete_ShouldThrowNotFoundException_WhenStationDoesNotExist() {
        int id = 99;
        Mockito.when(stationRepo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> stationService.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(stationRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete: ошибка IllegalStateException со списком ID, если к станции привязаны расписания")
    void delete_ShouldThrowExceptionWithFormattedIds_WhenAssignedToStationEntries() {
        int stationId = 1;
        Mockito.when(stationRepo.existsById(stationId)).thenReturn(true);

        StationEntry entry1 = StationEntry.builder().id(10).build();
        StationEntry entry2 = StationEntry.builder().id(11).build();
        Mockito.when(stationEntryRepo.findStationEntryByStationId(stationId)).thenReturn(List.of(entry1, entry2));

        assertThatThrownBy(() -> stationService.delete(stationId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete station #1 because it is assigned to Station Entries: #10, #11");

        verify(stationRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete с RedirectAttributes: успешное удаление свободной станции")
    void delete_WithRedirectAttributes_ShouldDeleteSuccessfully() {
        int id = 5;
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        Mockito.when(stationRepo.existsById(id)).thenReturn(true);
        Mockito.when(stationEntryRepo.findStationEntryByStationId(id)).thenReturn(Collections.emptyList());

        stationService.delete(id, redirectAttributes);

        verify(stationRepo).deleteById(id);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Station deleted successfully.");
    }
}