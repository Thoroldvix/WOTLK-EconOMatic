package com.thoroldvix.pricepal.shared;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.thoroldvix.pricepal.shared.PredicateUtils.getPredicateFromOperation;
import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@Component
public class SearchSpecification<E> {

    public Specification<E> createSearchSpecification(RequestDto.GlobalOperator globalOperator,
                                                      SearchCriteria... searchCriteria) {
        if (searchCriteria == null || searchCriteria.length  == 0) {
            return (root, query, cb) -> cb.isTrue(cb.literal(true));
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (SearchCriteria criteria : searchCriteria) {
                Path<?> columnPath = getColumnPath(root, criteria);
                Predicate predicate = getPredicateFromOperation(cb, criteria, columnPath);
                predicates.add(predicate);
            }
            return getSpecFromPredicates(globalOperator, cb, predicates);
        };
    }

    public Specification<E> getSpecForTimeRange(int timeRangeInDays) {
        if (timeRangeInDays < 0) {
            throw new IllegalArgumentException("Time range must be positive");
        }
        return createSearchSpecification(RequestDto.GlobalOperator.AND,
                getBetweenCriteriaForDays(timeRangeInDays));
    }

    private SearchCriteria getBetweenCriteriaForDays(int timeRangeInDays) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(timeRangeInDays);
        LocalDateTime endDate = LocalDateTime.now();

        String timeRangeString = startDate + "," + endDate;

        return SearchCriteria.builder()
                .column("updatedAt")
                .value(timeRangeString)
                .operation(SearchCriteria.Operation.BETWEEN)
                .build();
    }

    private Predicate getSpecFromPredicates(RequestDto.GlobalOperator globalOperator, CriteriaBuilder cb, List<Predicate> predicates) {
        return switch (globalOperator) {
            case OR -> cb.or(predicates.toArray(new Predicate[0]));
            case NOT -> cb.not(cb.or(predicates.toArray(new Predicate[0])));
            default -> cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<?> getColumnPath(Root<E> root, SearchCriteria searchCriteria) {
        if (searchCriteria == null) {
            throw new IllegalArgumentException("You must provide valid search criteria");
        }
        if (hasText(searchCriteria.joinTable())) {
            return root.join(searchCriteria.joinTable()).get(searchCriteria.column());
        }
        return root.get(searchCriteria.column());
    }


}




