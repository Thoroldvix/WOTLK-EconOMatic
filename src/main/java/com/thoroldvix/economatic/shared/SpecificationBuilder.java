package com.thoroldvix.economatic.shared;

import com.thoroldvix.economatic.error.InvalidSearchCriteriaException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.thoroldvix.economatic.error.ErrorMessages.SEARCH_CRITERIA_CANNOT_BE_NULL_OR_EMPTY;
import static com.thoroldvix.economatic.shared.PredicateUtils.getPredicateFromOperation;
import static com.thoroldvix.economatic.shared.ValidationUtils.isNonEmptyString;
import static com.thoroldvix.economatic.shared.ValidationUtils.notEmpty;



public class SpecificationBuilder {

    private SpecificationBuilder() {}

    public static <E> Specification<E> from(SearchRequest request) {
        notEmpty(request.searchCriteria(),
                () -> new InvalidSearchCriteriaException(SEARCH_CRITERIA_CANNOT_BE_NULL_OR_EMPTY));
        return getSpecification(request.globalOperator(), request.searchCriteria());
    }

    private static <E> Specification<E> getSpecification(SearchRequest.GlobalOperator globalOperator, List<SearchCriteria> searchCriteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = getPredicates(root, cb, searchCriteria);
            return getSpecFromPredicates(globalOperator, cb, predicates);
        };
    }

    private static <E> List<Predicate> getPredicates(Root<E> root, CriteriaBuilder cb, List<SearchCriteria> searchCriteria) {
        return searchCriteria.stream().map(criteria -> {
            Path<?> columnPath = getColumnPath(root, criteria);
            return getPredicateFromOperation(cb, criteria, columnPath);
        }).toList();
    }

    private static Predicate getSpecFromPredicates(SearchRequest.GlobalOperator globalOperator, CriteriaBuilder cb, List<Predicate> predicates) {
        return switch (globalOperator) {
            case OR -> cb.or(predicates.toArray(new Predicate[0]));
            case NOT -> cb.not(cb.or(predicates.toArray(new Predicate[0])));
            default -> cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <E> Path<?> getColumnPath(Root<E> root, SearchCriteria searchCriteria) {
        if (isNonEmptyString(searchCriteria.joinTable())) {
            return root.join(searchCriteria.joinTable()).get(searchCriteria.column());
        }
        return root.get(searchCriteria.column());
    }


}




