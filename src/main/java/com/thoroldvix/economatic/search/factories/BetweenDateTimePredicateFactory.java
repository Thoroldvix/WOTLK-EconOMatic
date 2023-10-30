package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.time.LocalTime;

class BetweenDateTimePredicateFactory extends AbstractBetweenPredicateFactory {

    @Override
    Predicate buildPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.equals(LocalDateTime.class)) {
            return getLocalDateTimeBetweenPredicate(cb, columnPath, lowerBound, upperBound);
        }
        throw new IllegalArgumentException("Invalid operation: BETWEEN_DATE_TIME is only applicable to date-time column types.");
    }

    private static Predicate getLocalDateTimeBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        LocalDateTime start = LocalDateTime.parse(lowerBound, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(upperBound, DATE_TIME_FORMATTER);
        if (end.getHour() == 0 && end.getMinute() == 0 && end.getSecond() == 0) {
            return cb.between(columnPath.as(LocalDateTime.class), start, end.with(LocalTime.MAX));
        }
        return cb.between(columnPath.as(LocalDateTime.class), start, end);
    }
}