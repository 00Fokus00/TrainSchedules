package ru.vsu.cs.schedules.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.models.Schedule;

import java.time.LocalDateTime;

public class ScheduleSpecification {

    public static Specification<Schedule> hasTrainId(Integer trainId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (trainId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("train").get("id"), trainId);
        };
    }

    public static Specification<Schedule> departureTimeBetween(LocalDateTime start, LocalDateTime end) {
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

    public static Specification<Schedule> arrivalTimeBetween(LocalDateTime start, LocalDateTime end) {
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
}