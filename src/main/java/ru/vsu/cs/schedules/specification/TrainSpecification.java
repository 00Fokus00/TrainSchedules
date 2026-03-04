package ru.vsu.cs.schedules.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.vsu.cs.schedules.models.Train;

public class TrainSpecification {

    public static Specification<Train> likeNumber(String number) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("number")),
                        "%" + number.toLowerCase() + "%");
    }
    public static Specification<Train> speedBetween(Integer min, Integer max) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.between(root.get("maxSpeed"), min, max);
    }
}
