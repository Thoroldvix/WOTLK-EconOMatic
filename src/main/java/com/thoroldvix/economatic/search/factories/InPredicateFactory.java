package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.Arrays;

class InPredicateFactory implements PredicateFactory {
    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        String[] split = value.split(",");
        return columnPath.in(Arrays.asList(split));
    }
}
