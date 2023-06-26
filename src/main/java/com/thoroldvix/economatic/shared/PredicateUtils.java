package com.thoroldvix.economatic.shared;

import com.thoroldvix.economatic.item.ItemQuality;
import com.thoroldvix.economatic.item.ItemSlot;
import com.thoroldvix.economatic.item.ItemType;
import com.thoroldvix.economatic.server.Faction;
import com.thoroldvix.economatic.server.Region;
import com.thoroldvix.economatic.server.ServerType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;

public final class PredicateUtils {
    private static final String INTEGER = "Integer";
    private static final String LONG = "Long";
    private static final String DOUBLE = "Double";
    private static final String LOCAL_DATE_TIME = "LocalDateTime";
    private static final String STRING = "String";
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    static {
        DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy")
                .optionalStart()
                .appendPattern(" HH:mm")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .toFormatter();
    }

    private PredicateUtils() {
    }

    public static Predicate getPredicateFromOperation(CriteriaBuilder cb, SearchCriteria searchCriteria, Path<?> columnPath) {

        String value = searchCriteria.value();
        return switch (searchCriteria.operation()) {
            case EQUALS -> getEqualsPredicate(cb, columnPath, value);
            case LIKE -> cb.like(columnPath.as(String.class), "%" + value + "%");
            case GREATER_THAN -> getGreaterThanPredicate(cb, columnPath, value);
            case GREATER_THAN_OR_EQUALS -> getGreaterThanOrEqualsPredicate(cb, columnPath, value);
            case LESS_THAN_OR_EQUALS -> getLessThanOrEqualsPredicate(cb, columnPath, value);
            case LESS_THAN -> getLessThanPredicate(cb, columnPath, value);
            case IN -> getInPredicate(searchCriteria, columnPath);
            case BETWEEN -> getBetweenPredicate(cb, columnPath, value);
            case EQUALS_IGNORE_CASE -> getEqualsIgnoreCasePredicate(cb, columnPath, value);
        };
    }


    public static Predicate getLessThanOrEqualsPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case INTEGER -> cb.lessThanOrEqualTo(columnPath.as(Integer.class), Integer.parseInt(value));
            case LONG -> cb.lessThanOrEqualTo(columnPath.as(Long.class), Long.parseLong(value));
            case DOUBLE -> cb.lessThanOrEqualTo(columnPath.as(Double.class), Double.parseDouble(value));
            case LOCAL_DATE_TIME ->
                    cb.lessThanOrEqualTo(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
            default ->
                    throw new IllegalArgumentException("Invalid operation: LESS_THAN_OR_EQUALS is only applicable to numeric and date-time column types.");
        };
    }

    public static Predicate getGreaterThanOrEqualsPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case INTEGER -> cb.greaterThanOrEqualTo(columnPath.as(Integer.class), Integer.parseInt(value));
            case LONG -> cb.greaterThanOrEqualTo(columnPath.as(Long.class), Long.parseLong(value));
            case DOUBLE -> cb.greaterThanOrEqualTo(columnPath.as(Double.class), Double.parseDouble(value));
            case LOCAL_DATE_TIME ->
                    cb.greaterThanOrEqualTo(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
            default ->
                    throw new IllegalArgumentException("Invalid operation: GREATER_THAN is only applicable to numeric and date-time column types.");
        };

    }

    public static Predicate getBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        String[] split = value.split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid operation: BETWEEN requires 2 values.");
        }
        String lowerBound = split[0];
        String upperBound = split[1];
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case INTEGER -> getIntegerBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case LONG -> getLongBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case DOUBLE -> getDoubleBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case LOCAL_DATE_TIME -> getLocalDateTimeBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            default ->
                    throw new IllegalArgumentException("Invalid operation: BETWEEN is only applicable to numeric and date-time column types.");
        };
    }

    public static Predicate getEqualsIgnoreCasePredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case STRING -> cb.equal(cb.lower(columnPath.as(String.class)), value.toLowerCase());
            case INTEGER -> cb.equal(columnPath, Integer.parseInt(value));
            case LONG -> cb.equal(columnPath, Long.parseLong(value));
            case DOUBLE -> cb.equal(columnPath, Double.parseDouble(value));
            default ->
                    throw new IllegalArgumentException("Invalid operation: EQUALS_IGNORE_CASE is only applicable to string and numeric column types.");
        };
    }

    public static Predicate getEqualsPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.isEnum()) {
            int searchValue = getValueForEnumType(columnType.getSimpleName(), value);
            return cb.equal(columnPath, searchValue);
        }
        return cb.equal(columnPath, value);
    }


    public static Predicate getInPredicate(SearchCriteria searchCriteria, Path<?> columnPath) {
        String[] split = searchCriteria.value().split(",");
        return columnPath.in(Arrays.asList(split));
    }

    public static Predicate getLessThanPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case INTEGER -> cb.lessThan(columnPath.as(Integer.class), Integer.parseInt(value));
            case LONG -> cb.lessThan(columnPath.as(Long.class), Long.parseLong(value));
            case DOUBLE -> cb.lessThan(columnPath.as(Double.class), Double.parseDouble(value));
            case LOCAL_DATE_TIME ->
                    cb.lessThan(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
            default ->
                    throw new IllegalArgumentException("Invalid operation: LESS_THAN is only applicable to numeric and date-time column types.");
        };
    }

    public static Predicate getGreaterThanPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case INTEGER -> cb.greaterThan(columnPath.as(Integer.class), Integer.parseInt(value));
            case LONG -> cb.greaterThan(columnPath.as(Long.class), Long.parseLong(value));
            case DOUBLE -> cb.greaterThan(columnPath.as(Double.class), Double.parseDouble(value));
            case LOCAL_DATE_TIME ->
                    cb.greaterThan(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value, DATE_TIME_FORMATTER));
            default ->
                    throw new IllegalArgumentException("Invalid operation: GREATER_THAN is only applicable to numeric and date-time column types.");
        };
    }

    private static Predicate getLocalDateTimeBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        LocalDateTime start = LocalDateTime.parse(lowerBound, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(upperBound, DATE_TIME_FORMATTER);
        if (end.getHour() == 0 && end.getMinute() == 0 && end.getSecond() == 0) {
            end = end.with(LocalTime.MAX);
        }
        return cb.between(columnPath.as(LocalDateTime.class), start, end);
    }

    private static int getValueForEnumType(String enumType, String value) {
        return switch (enumType) {
            case "Faction" -> Enum.valueOf(Faction.class, value.toUpperCase()).ordinal();
            case "Region" -> Enum.valueOf(Region.class, value.toUpperCase()).ordinal();
            case "ItemType" -> Enum.valueOf(ItemType.class, value.toUpperCase()).ordinal();
            case "ItemQuality" -> Enum.valueOf(ItemQuality.class, value.toUpperCase()).ordinal();
            case "ItemSlot" -> Enum.valueOf(ItemSlot.class, value.toUpperCase()).ordinal();
            case "ServerType" -> Enum.valueOf(ServerType.class, value.toUpperCase()).ordinal();
            default -> throw new IllegalArgumentException("Unsupported enum type");
        };
    }

    private static Predicate getDoubleBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Double.class), Double.parseDouble(lowerBound), Double.parseDouble(upperBound));
    }

    private static Predicate getLongBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Long.class), Long.parseLong(lowerBound), Long.parseLong(upperBound));
    }

    private static Predicate getIntegerBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Integer.class), Integer.parseInt(lowerBound), Integer.parseInt(upperBound));
    }
}
