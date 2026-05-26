package ru.vsu.cs.schedules.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Locomotive;
import ru.vsu.cs.schedules.models.Train;
import ru.vsu.cs.schedules.repositories.LocomotiveRepo;
import ru.vsu.cs.schedules.repositories.TrainRepo;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LocomotiveServiceTest {

    @Mock
    private LocomotiveRepo locomotiveRepo;

    @Mock
    private TrainRepo trainRepo;

    @InjectMocks
    private LocomotiveService locomotiveService;

    @Test
    @DisplayName("getById: локомотив")
    void getById_ShouldReturnLocomotive_WhenExists() {
        Locomotive expected = new Locomotive(1, "ЧС7", 4000, "active");
        Mockito.when(locomotiveRepo.findById(1)).thenReturn(Optional.of(expected));

        Locomotive actual = locomotiveService.getById(1);

        assertThat(actual).isNotNull();
        assertThat(actual.getModel()).isEqualTo("ЧС7");
    }

    @Test
    @DisplayName("getById: NotFoundException")
    void getById_ShouldThrowNotFoundException_WhenDoesNotExist() {
        Mockito.when(locomotiveRepo.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locomotiveService.getById(99))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Locomotive not found: 99");
    }

    @Test
    @DisplayName("delete: должен выбрасывать IllegalStateException, если локомотив привязан к поезду")
    void delete_ShouldThrowException_WhenLocomotiveIsAssignedToTrain() {
        int locomotiveId = 1;

        Locomotive locomotive = Locomotive.builder()
                .id(locomotiveId)
                .model("ЧС7")
                .power(4000)
                .status("active")
                .build();

        Train train = Train.builder()
                .id(10)
                .number("042А")
                .maxSpeed(140)
                .locomotive(locomotive)
                .build();

        Mockito.when(locomotiveRepo.findById(locomotiveId)).thenReturn(Optional.of(locomotive));
        Mockito.when(trainRepo.findByLocomotiveId(locomotiveId)).thenReturn(Optional.of(train));

        assertThatThrownBy(() -> locomotiveService.delete(locomotiveId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete locomotive #1 because it is assigned to train #10 (042А).");

        Mockito.verify(locomotiveRepo, Mockito.never()).delete(any());
    }

    @Test
    @DisplayName("delete: с RedirectAttributes должен успешно удалять локомотив и добавлять сообщение")
    void delete_WithRedirectAttributes_ShouldDeleteSuccessfully() {
        int id = 1;
        Locomotive locomotive = new Locomotive(id, "ЧС7", 4000, "active");
        RedirectAttributes redirectAttributes = Mockito.mock(RedirectAttributes.class);

        Mockito.when(locomotiveRepo.findById(id)).thenReturn(Optional.of(locomotive));
        Mockito.when(trainRepo.findByLocomotiveId(id)).thenReturn(Optional.empty());

        locomotiveService.delete(id, redirectAttributes);

        Mockito.verify(locomotiveRepo).delete(locomotive);

        Mockito.verify(redirectAttributes).addFlashAttribute("successMessage", "Locomotive deleted successfully.");
    }
}