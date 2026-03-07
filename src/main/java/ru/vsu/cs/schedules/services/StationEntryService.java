package ru.vsu.cs.schedules.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.exception.ValidationException;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.repositories.StationEntryRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationEntryService {
    private final StationEntryRepo stationEntryRepo;

    public StationEntryService(StationEntryRepo stationEntryRepo) {
        this.stationEntryRepo = stationEntryRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<StationEntry> list(String q, Pageable pageable) {
        return stationEntryRepo.findAll(pageable);
    }

    public Page<StationEntry> list(Specification<StationEntry> spec, Pageable pageable) {
        return stationEntryRepo.findAll(spec, pageable);
    }

    public Page<StationEntry> listByScheduleId(Integer scheduleId, Pageable pageable) {
        return stationEntryRepo.findStationEntryByScheduleId(scheduleId, pageable);
    }

    @Cacheable(value = "stationEntry:byId", key="#id")
    public StationEntry getById(Integer id) {
        return stationEntryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("StationEntry not found: " + id));
    }

    @Transactional
    public StationEntry create(StationEntry stationEntry) {
        validateStationEntry(stationEntry);
        validateTimeConflicts(stationEntry);


        List<StationEntry> existingEntries = stationEntryRepo
                .findByScheduleId(stationEntry.getSchedule().getId());

        // нужно ли сдвигать существующие остановки
        Integer newOrder = stationEntry.getSegmentOrder();
        boolean needsReorder = existingEntries.stream()
                .anyMatch(e -> e.getSegmentOrder() >= newOrder);

        StationEntry savedEntry = stationEntryRepo.save(stationEntry);

        // если нужно сдвигаем существующие остановки
        if (needsReorder) {
            shiftEntriesAfter(existingEntries, newOrder);
        }

        return savedEntry;
    }

    @CacheEvict(value = "stationEntry:byId", key = "#id")
    @Transactional
    public StationEntry update(Integer id, StationEntry stationEntry) {
        StationEntry existing = stationEntryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("StationEntry not found: " + id));

        LocalDateTime oldArrivalTime = existing.getArrivalTime();
        LocalDateTime oldDepartureTime = existing.getDepartureTime();
        Integer oldOrder = existing.getSegmentOrder();
        Integer scheduleId = existing.getSchedule().getId();

        try {
            existing.setStation(stationEntry.getStation());
            existing.setArrivalTime(stationEntry.getArrivalTime());
            existing.setDepartureTime(stationEntry.getDepartureTime());
            existing.setSegmentOrder(stationEntry.getSegmentOrder());

            validateStationEntry(existing);
            validateTimeConflicts(existing, id); // Исключаем текущую запись при проверке

            StationEntry updatedEntry = stationEntryRepo.save(existing);

            // если изменился порядок обновляем нумерацию
            if (!oldOrder.equals(stationEntry.getSegmentOrder())) {
                reorderEntries(scheduleId);
            }

            return updatedEntry;

        } catch (DataIntegrityViolationException e) {
            existing.setArrivalTime(oldArrivalTime);
            existing.setDepartureTime(oldDepartureTime);
            existing.setSegmentOrder(oldOrder);
            throw e;
        }
    }

    @Transactional
    public void delete(Integer id) {
        StationEntry entry = stationEntryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("StationEntry not found: " + id));

        Integer scheduleId = entry.getSchedule().getId();
        Integer deletedOrder = entry.getSegmentOrder();

        stationEntryRepo.deleteById(id);

        // сдвигаем оставшиеся остановки
        shiftEntriesAfterDelete(scheduleId, deletedOrder);
    }

    /**
     * Проверяет корректность времени остановки
     */
    private void validateStationEntry(StationEntry stationEntry) {
        if (stationEntry.getArrivalTime() != null &&
                stationEntry.getDepartureTime() != null) {

            if (!(stationEntry.getArrivalTime().isBefore(stationEntry.getDepartureTime()))) {
                throw new ValidationException(
                        "Arrival time must be before departure time. " +
                                "Arrival: " + stationEntry.getArrivalTime() +
                                ", Departure: " + stationEntry.getDepartureTime()
                );
            }
        }

        if (stationEntry.getSegmentOrder() <= 0) {
            throw new ValidationException("Segment order must be positive");
        }
    }

    /**
     * Проверяет временные конфликты для новой остановки
     */
    private void validateTimeConflicts(StationEntry stationEntry) {
        validateTimeConflicts(stationEntry, null);
    }

    /**
     * Проверяет временные конфликты (с исключением текущей записи при обновлении)
     */
    private void validateTimeConflicts(StationEntry stationEntry, Integer excludeId) {
        if (stationEntry.getArrivalTime() == null || stationEntry.getDepartureTime() == null) {
            return;
        }

        List<StationEntry> conflictingEntries = stationEntryRepo
                .findTimeConflicts(
                        stationEntry.getSchedule().getId(),
                        excludeId,
                        stationEntry.getArrivalTime(),
                        stationEntry.getDepartureTime()
                );

        if (!conflictingEntries.isEmpty()) {
            throw new ValidationException(
                    "Time conflicts with existing station entry: " +
                            conflictingEntries.get(0).getStation().getName()
            );
        }
    }

    /**
     * Сдвигает остановки после добавления новой
     */
    private void shiftEntriesAfter(List<StationEntry> existingEntries, Integer newOrder) {
        List<StationEntry> entriesToShift = existingEntries.stream()
                .filter(e -> e.getSegmentOrder() >= newOrder)
                .sorted((e1, e2) -> e2.getSegmentOrder().compareTo(e1.getSegmentOrder())) // Сортируем по убыванию
                .collect(Collectors.toList());

        for (StationEntry entry : entriesToShift) {
            entry.setSegmentOrder(entry.getSegmentOrder() + 1);
            stationEntryRepo.save(entry);
        }
    }

    /**
     * Сдвигает остановки после удаления
     */
    private void shiftEntriesAfterDelete(Integer scheduleId, Integer deletedOrder) {
        List<StationEntry> entriesToShift = stationEntryRepo
                .findByScheduleIdAndSegmentOrderGreaterThan(scheduleId, deletedOrder);

        for (StationEntry entry : entriesToShift) {
            entry.setSegmentOrder(entry.getSegmentOrder() - 1);
            stationEntryRepo.save(entry);
        }
    }

    /**
     * Перенумеровывает все остановки в расписании
     */
    @Transactional
    public void reorderEntries(Integer scheduleId) {
        List<StationEntry> entries = stationEntryRepo.findByScheduleId(scheduleId);

        // Сортируем по текущему порядку
        entries.sort((e1, e2) -> e1.getSegmentOrder().compareTo(e2.getSegmentOrder()));

        // Присваиваем новый порядок
        int newOrder = 1;
        for (StationEntry entry : entries) {
            if (!entry.getSegmentOrder().equals(newOrder)) {
                entry.setSegmentOrder(newOrder);
                stationEntryRepo.save(entry);
            }
            newOrder++;
        }
    }

    /**
     * Проверяет логическую последовательность времен остановок в расписании
     */
    public void validateScheduleTimeSequence(Integer scheduleId) {
        List<StationEntry> entries = stationEntryRepo.findByScheduleId(scheduleId);

        // Сортируем по порядку
        entries.sort((e1, e2) -> e1.getSegmentOrder().compareTo(e2.getSegmentOrder()));

        LocalDateTime prevDeparture = null;

        for (StationEntry entry : entries) {
            // Если у остановки есть время прибытия и отправления
            if (entry.getArrivalTime() != null && entry.getDepartureTime() != null) {
                // Проверяем, что прибытие раньше отправления
                if (entry.getArrivalTime().isAfter(entry.getDepartureTime())) {
                    throw new ValidationException(
                            "Arrival time must be before departure time for station: " +
                                    entry.getStation().getName()
                    );
                }

                // Проверяем, что прибытие на текущую остановку позже отправления с предыдущей
                if (prevDeparture != null && entry.getArrivalTime().isBefore(prevDeparture)) {
                    throw new ValidationException(
                            "Arrival at " + entry.getStation().getName() +
                                    " must be after departure from previous station"
                    );
                }

                prevDeparture = entry.getDepartureTime();
            }
        }
    }
}