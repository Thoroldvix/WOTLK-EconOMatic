package com.thoroldvix.pricepal.common;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FiltersSpecification<T> {
    public Specification<T> getSearchSpecification(List<SearchCriteria> searchCriteria, RequestDto.GlobalOperator globalOperator) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (SearchCriteria criteria : searchCriteria) {
                Path<?> columnPath = getColumnPath(root, criteria);
                Predicate predicate = getPredicateFromOperation(cb, criteria, columnPath);
                predicates.add(predicate);
            }
            if (globalOperator == null || globalOperator.equals(RequestDto.GlobalOperator.AND)) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }
            else if (globalOperator.equals(RequestDto.GlobalOperator.OR)) {
                return cb.or(predicates.toArray(new Predicate[0]));
            }
            throw new IllegalArgumentException("Invalid global operator: " + globalOperator);
        };
    }

    private Predicate getPredicateFromOperation(CriteriaBuilder cb, SearchCriteria searchCriteria, Path<?> columnPath) {
        String value = searchCriteria.value();
        return switch (searchCriteria.operation()) {
            case EQUALS -> getEqualsPredicate(cb, columnPath, value);
            case NOT_EQUALS -> getNotEqualsPredicate(cb, columnPath, value);
            case LIKE -> cb.like(columnPath.as(String.class), "%" + value + "%");
            case NOT_LIKE -> cb.notLike(columnPath.as(String.class), "%" + value + "%");
            case GREATER_THAN -> getGreaterThanPredicate(cb, columnPath, value);
            case LESS_THAN -> getLessThanPredicate(cb, columnPath, value);
            case IN -> getInPredicate(searchCriteria, columnPath);
            case BETWEEN -> getBetweenPredicate(cb, columnPath, value);
            case EQUALS_IGNORE_CASE -> getEqualsIgnoreCasePredicate(cb, columnPath, value);
        };
    }


    private Predicate getBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        String[] split = value.split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid operation: BETWEEN requires 2 values.");
        }
        String lowerBound = split[0];
        String upperBound = split[1];
        if (Integer.class.isAssignableFrom(columnPath.getJavaType())) {
            Integer lowerIntegerBound = Integer.parseInt(lowerBound);
            Integer upperIntegerBound = Integer.parseInt(upperBound);
            return cb.between(columnPath.as(Integer.class), lowerIntegerBound, upperIntegerBound);
        } else if (Long.class.isAssignableFrom(columnPath.getJavaType())) {
            Long lowerLongBound = Long.parseLong(lowerBound);
            Long upperLongBound = Long.parseLong(upperBound);
            return cb.between(columnPath.as(Long.class), lowerLongBound, upperLongBound);
        } else if (Double.class.isAssignableFrom(columnPath.getJavaType())) {
            Double lowerDoubleBound = Double.parseDouble(lowerBound);
            Double upperDoubleBound = Double.parseDouble(upperBound);
            return cb.between(columnPath.as(Double.class), lowerDoubleBound, upperDoubleBound);
        } else if (LocalDateTime.class.isAssignableFrom(columnPath.getJavaType())) {
            LocalDateTime lowerDateBound = convertToLocalDateTime(lowerBound);
            LocalDateTime upperDateBound = convertToLocalDateTime(upperBound);

            return cb.between(columnPath.as(LocalDateTime.class), lowerDateBound, upperDateBound);
        }
        throw new IllegalArgumentException("Invalid operation: BETWEEN is only applicable to numeric and date-time column types.");
    }

    private LocalDateTime convertToLocalDateTime(String lowerBound) {
        try {
            return LocalDateTime.parse(lowerBound);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid operation: BETWEEN requires 2 values.");
        }
    }

    private Predicate getEqualsIgnoreCasePredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (String.class.isAssignableFrom(columnType)) {
            return cb.equal(cb.lower(columnPath.as(String.class)), value.toLowerCase());
        }
        throw new IllegalArgumentException("Invalid operation: EQUALS_IGNORE_CASE is only applicable to string column types.");
    }

    private Predicate getEqualsPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.isEnum()) {
            Enum<?> searchValue = Enum.valueOf((Class<Enum>) columnType, value.toUpperCase());
            return cb.equal(columnPath, searchValue);
        }
        return cb.equal(columnPath, value);
    }

    private Predicate getNotEqualsPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        if (columnType.isEnum()) {
            Enum<?> searchValue = Enum.valueOf((Class<Enum>) columnType, value.toUpperCase());
            return cb.notEqual(columnPath, searchValue);
        }
        return cb.notEqual(columnPath, value);
    }

    private Predicate getInPredicate(SearchCriteria searchCriteria, Path<?> columnPath) {
        String[] split = searchCriteria.value().split(",");
        return columnPath.in(Arrays.asList(split));
    }

    private Predicate getLessThanPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        if (Integer.class.isAssignableFrom(columnPath.getJavaType())) {
            Integer numericValue = Integer.parseInt(value);
            return cb.lessThan(columnPath.as(Integer.class), numericValue);
        } else if (Long.class.isAssignableFrom(columnPath.getJavaType())) {
            Long numericValue = Long.parseLong(value);
            return cb.lessThan(columnPath.as(Long.class), numericValue);
        } else if (Double.class.isAssignableFrom(columnPath.getJavaType())) {
            Double numericValue = Double.parseDouble(value);
            return cb.lessThan(columnPath.as(Double.class), numericValue);
        }
        throw new IllegalArgumentException("Invalid operation: LESS_THAN is only applicable to numeric  column types.");
    }

    private Predicate getGreaterThanPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        if (Integer.class.isAssignableFrom(columnPath.getJavaType())) {
            Integer numericValue = Integer.parseInt(value);
            return cb.greaterThan(columnPath.as(Integer.class), numericValue);
        } else if (Long.class.isAssignableFrom(columnPath.getJavaType())) {
            Long numericValue = Long.parseLong(value);
            return cb.greaterThan(columnPath.as(Long.class), numericValue);
        } else if (Double.class.isAssignableFrom(columnPath.getJavaType())) {
            Double numericValue = Double.parseDouble(value);
            return cb.greaterThan(columnPath.as(Double.class), numericValue);
        } else if (LocalDateTime.class.isAssignableFrom(columnPath.getJavaType())) {
            LocalDateTime currentDate = LocalDateTime.now();
            int daysAgo = Integer.parseInt(value);
            LocalDateTime targetDate = currentDate.minusDays(daysAgo);
            return cb.greaterThan(columnPath.as(LocalDateTime.class), targetDate);
        }
        throw new IllegalArgumentException("Invalid operation: GREATER_THAN is only applicable to numeric and datetime column types.");
    }

    private Path<?> getColumnPath(Root<T> root, SearchCriteria searchCriteria) {
        if (searchCriteria.joinOperation()) {
            ValidationUtils.validateNonEmptyString(searchCriteria.joinTable(), "You must provide valid join table");
            return root.join(searchCriteria.joinTable()).get(searchCriteria.column());
        }
        return root.get(searchCriteria.column());
    }

}




