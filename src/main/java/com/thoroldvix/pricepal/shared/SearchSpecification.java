package com.thoroldvix.pricepal.shared;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.thoroldvix.pricepal.shared.PredicateUtils.getPredicateFromOperation;
import static com.thoroldvix.pricepal.shared.ValidationUtils.hasText;

@Component
public class SearchSpecification<E> {

    public Specification<E> create(SearchRequest.GlobalOperator globalOperator,
                                   SearchCriteria... searchCriteria) {
        if (searchCriteria == null || searchCriteria.length == 0) {
            throw new IllegalArgumentException("You must provide valid search criteria");
        }
        return getSpecification(globalOperator, searchCriteria);
    }

    private Specification<E> getSpecification(SearchRequest.GlobalOperator globalOperator, SearchCriteria[] searchCriteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = getPredicates(root, cb, searchCriteria);
            return getSpecFromPredicates(globalOperator, cb, predicates);
        };
    }

    private List<Predicate> getPredicates(Root<E> root, CriteriaBuilder cb, SearchCriteria[] searchCriteria) {
        return Arrays.stream(searchCriteria).map(criteria -> {
            Path<?> columnPath = getColumnPath(root, criteria);
            return getPredicateFromOperation(cb, criteria, columnPath);
        }).toList();
    }

    private Predicate getSpecFromPredicates(SearchRequest.GlobalOperator globalOperator, CriteriaBuilder cb, List<Predicate> predicates) {
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




