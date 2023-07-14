package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

class BetweenNumericPredicateFactory extends AbstractBetweenPredicateFactory {

    private static Predicate getDoubleBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        double lowerBoundValue = Double.parseDouble(lowerBound);
        double upperDoubleValue = Double.parseDouble(upperBound);
        return cb.between(columnPath.as(Double.class), lowerBoundValue, upperDoubleValue);
    }

    private static Predicate getLongBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        long lowerBoundValue = Long.parseLong(lowerBound);
        long upperBoundValue = Long.parseLong(upperBound);
        return cb.between(columnPath.as(Long.class), lowerBoundValue, upperBoundValue);
    }

    private static Predicate getIntegerBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        int lowerBoundValue = Integer.parseInt(lowerBound);
        int upperBoundValue = Integer.parseInt(upperBound);
        return cb.between(columnPath.as(Integer.class), lowerBoundValue, upperBoundValue);

    }

    @Override
    Predicate buildPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "Integer" -> getIntegerBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case "Long" -> getLongBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case "Double" -> getDoubleBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            default ->
                    throw new IllegalArgumentException("Invalid operation: BETWEEN_NUMERIC is only applicable to numeric column types.");
        };
    }
}
