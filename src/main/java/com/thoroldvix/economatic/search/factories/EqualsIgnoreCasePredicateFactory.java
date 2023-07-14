package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;

class EqualsIgnoreCasePredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "String" -> cb.equal(cb.lower(columnPath.as(String.class)), value.toLowerCase());
            case "Integer" -> cb.equal(columnPath, Integer.parseInt(value));
            case "Long" -> cb.equal(columnPath, Long.parseLong(value));
            case "Double" -> cb.equal(columnPath, Double.parseDouble(value));
            case "LocalDateTime" -> cb.equal(columnPath, LocalDateTime.parse(value, DATE_TIME_FORMATTER));
            default ->
                    throw new IllegalArgumentException("Invalid operation: EQUALS_IGNORE_CASE is only applicable to string, numeric and date-time column types.");
        };
    }
}