package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

class LessThanPredicateFactory implements PredicateFactory {

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "Integer" -> cb.lessThan(columnPath.as(Integer.class), Integer.parseInt(value));
            case "Long" -> cb.lessThan(columnPath.as(Long.class), Long.parseLong(value));
            case "Double" -> cb.lessThan(columnPath.as(Double.class), Double.parseDouble(value));
            default ->
                    throw new IllegalArgumentException("Invalid operation: LESS_THAN is only applicable to numeric column types.");
        };
    }


}
