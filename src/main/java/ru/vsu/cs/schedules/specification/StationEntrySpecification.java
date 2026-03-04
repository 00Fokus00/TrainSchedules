package ru.vsu.cs.schedules.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.models.StationEntry;

import java.time.LocalDateTime;

public class StationEntrySpecification {

    public static Specification<StationEntry> hasScheduleId(Integer scheduleId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (scheduleId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("schedule").get("id"), scheduleId);
        };
    }

    public static Specification<StationEntry> hasStationId(Integer stationId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (stationId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("station").get("id"), stationId);
        };
    }

    public static Specification<StationEntry> hasArrivalTimeBetween(LocalDateTime start, LocalDateTime end) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("arrivalTime"), end);
            }
            if (end == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("arrivalTime"), start);
            }
            return criteriaBuilder.between(root.get("arrivalTime"), start, end);
        };
    }

    public static Specification<StationEntry> hasDepartureTimeBetween(LocalDateTime start, LocalDateTime end) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (start == null && end == null) {
                return criteriaBuilder.conjunction();
            }
            if (start == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("departureTime"), end);
            }
            if (end == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("departureTime"), start);
            }
            return criteriaBuilder.between(root.get("departureTime"), start, end);
        };
    }

    public static Specification<StationEntry> segmentOrderBetween(Integer minOrder, Integer maxOrder) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (minOrder == null && maxOrder == null) {
                return criteriaBuilder.conjunction();
            }
            if (minOrder == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("segmentOrder"), maxOrder);
            }
            if (maxOrder == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("segmentOrder"), minOrder);
            }
            return criteriaBuilder.between(root.get("segmentOrder"), minOrder, maxOrder);
        };
    }
}