package com.thoroldvix.economatic.search.factories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public interface PredicateFactory {

    DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .optionalStart()
            .appendPattern(" HH:mm")
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter();

    Predicate getPredicate(CriteriaBuilder cb, Path<?> columnPath, String value);
}
