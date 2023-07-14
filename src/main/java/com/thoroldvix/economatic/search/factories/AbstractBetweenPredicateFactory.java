package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

abstract class AbstractBetweenPredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        String[] split = value.split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid operation: BETWEEN requires 2 values.");
        }
        String lowerBound = split[0];
        String upperBound = split[1];
        return buildPredicate(cb, columnPath, lowerBound, upperBound);
    }

    abstract Predicate buildPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound);

}
