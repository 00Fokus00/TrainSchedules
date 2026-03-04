package ru.vsu.cs.schedules.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.models.Locomotive;

public class LocomotiveSpecification {

    public static Specification<Locomotive> likeModel(String model) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (model == null || model.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("model")),
                    "%" + model.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Locomotive> powerBetween(Integer minPower, Integer maxPower) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (minPower == null && maxPower == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPower == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("power"), maxPower);
            }
            if (maxPower == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("power"), minPower);
            }
            return criteriaBuilder.between(root.get("power"), minPower, maxPower);
        };
    }

    public static Specification<Locomotive> likeStatus(String status) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (status == null || status.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("status")),
                    "%" + status.toLowerCase() + "%"
            );
        };
    }
}