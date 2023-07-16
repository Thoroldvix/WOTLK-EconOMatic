package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;

class AfterOrEqualsPredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.equals(LocalDateTime.class)) {
            return cb.greaterThanOrEqualTo(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
        }
        throw new IllegalArgumentException("Invalid operation: AFTER_OR_EQUALS is only applicable to date-time column types.");
    }
}
