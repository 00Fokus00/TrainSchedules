package ru.vsu.cs.schedules.services;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.vsu.cs.schedules.exception.NotFoundException;
import ru.vsu.cs.schedules.models.Station;
import ru.vsu.cs.schedules.models.StationEntry;
import ru.vsu.cs.schedules.repositories.StationEntryRepo;
import ru.vsu.cs.schedules.repositories.StationRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepo stationRepo;
    private final StationEntryRepo stationEntryRepo;

    public StationService(StationRepo stationRepo, StationEntryRepo stationEntryRepo) {
        this.stationRepo = stationRepo;
        this.stationEntryRepo = stationEntryRepo;
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.none());
    }

    public Page<Station> list(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return stationRepo.findByNameContainingIgnoreCase(sanitize(search.trim()), pageable);
        } else {
            return stationRepo.findAll(pageable);
        }
    }

    public Station getById(Integer id) {
        return stationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Station not found: " + id));
    }

    @Transactional
    public Station create(Station station) {
        return stationRepo.save(station);
    }

    @Transactional
    public Station update(Integer id, Station station) {
        Station existing = stationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Station not found: " + id));

        existing.setName(sanitize(station.getName()));

        return stationRepo.save(existing);
    }

    @Transactional
    public void delete(Integer id, RedirectAttributes redirectAttributes) {
        if (!stationRepo.existsById(id)) {
            throw new NotFoundException("Station not found: " + id);
        }

        List<StationEntry> stationEntryList = stationEntryRepo.findStationEntryByStationId(id);

        if (!stationEntryList.isEmpty()) {
            // Собираем все ID StationEntry в строку
            String entryIds = stationEntryList.stream()
                    .map(entry -> entry.getId().toString())
                    .collect(Collectors.joining(", #", "#", ""));

            throw new IllegalStateException("Cannot delete station #" + id +
                    " because it is assigned to Station Entries: " + entryIds);
        }

        stationRepo.deleteById(id);

        if (redirectAttributes != null) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Station deleted successfully.");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (!stationRepo.existsById(id)) {
            throw new NotFoundException("Station not found: " + id);
        }

        List<StationEntry> stationEntryList = stationEntryRepo.findStationEntryByStationId(id);

        if (!stationEntryList.isEmpty()) {
            // Собираем все ID StationEntry в строку
            String entryIds = stationEntryList.stream()
                    .map(entry -> entry.getId().toString())
                    .collect(Collectors.joining(", #", "#", ""));

            throw new IllegalStateException("Cannot delete station #" + id +
                    " because it is assigned to Station Entries: " + entryIds);
        }

        stationRepo.deleteById(id);
    }
}