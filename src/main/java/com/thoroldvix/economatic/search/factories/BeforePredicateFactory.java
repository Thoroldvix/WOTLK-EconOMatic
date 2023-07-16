package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;

class BeforePredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.equals(LocalDateTime.class)) {
            return cb.lessThan(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
        }
        throw new IllegalArgumentException("Invalid operation: BEFORE is only applicable to date-time column types.");
    }
}

