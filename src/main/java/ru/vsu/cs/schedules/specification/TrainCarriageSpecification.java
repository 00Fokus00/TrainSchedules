package ru.vsu.cs.schedules.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.models.TrainCarriage;

public class TrainCarriageSpecification {

    public static Specification<TrainCarriage> likeType(String type) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (type == null || type.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("type")),
                    "%" + type.toLowerCase() + "%"
            );
        };
    }

    public static Specification<TrainCarriage> hasTrainId(Integer trainId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (trainId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("train").get("id"), trainId);
        };
    }
}