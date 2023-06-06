package com.thoroldvix.pricepal.common.service;

import com.thoroldvix.pricepal.common.dto.RequestDto;
import com.thoroldvix.pricepal.common.dto.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.thoroldvix.pricepal.common.util.ValidationUtils.*;

@Service
public class SearchSpecification<T> {
    public Specification<T> getSearchSpecification(List<SearchCriteria> searchCriteria, RequestDto.GlobalOperator globalOperator,
                                                   String joinTable) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return (root, query, cb) -> cb.isTrue(cb.literal(true));
        }
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (SearchCriteria criteria : searchCriteria) {
                Path<?> columnPath = getColumnPath(root, criteria, joinTable);
                Predicate predicate = getPredicateFromOperation(cb, criteria, columnPath);
                predicates.add(predicate);
            }
            if (globalOperator == null || globalOperator.equals(RequestDto.GlobalOperator.AND)) {
                return cb.and(predicates.toArray(new Predicate[0]));
            } else if (globalOperator.equals(RequestDto.GlobalOperator.OR)) {
                return cb.or(predicates.toArray(new Predicate[0]));
            }
            throw new IllegalArgumentException("Invalid global operator: " + globalOperator);
        };
    }

    public Specification<T> getJoinSpecForServerIdentifier(String serverIdentifier) {
        if (isNumber(serverIdentifier)) {
            int serverId = Integer.parseInt(serverIdentifier);
            validatePositiveInt(serverId, "Server id must be positive");
            return getJoinSpecForServerId(serverId);
        } else {
            return getJoinSpecForServerUniqueName(serverIdentifier);
        }
    }

    public Specification<T> getJoinSpecForServerId(int serverId) {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .column("id")
                .value(String.valueOf(serverId))
                .operation(SearchCriteria.Operation.EQUALS)
                .build();

        return getSearchSpecification(Collections.singletonList(searchCriteria),
                RequestDto.GlobalOperator.AND,
                "server");
    }

    public Specification<T> getJoinSpecForServerUniqueName(String uniqueName) {
        SearchCriteria searchCriteria = SearchCriteria.builder()
                .column("uniqueName")
                .value(uniqueName)
                .operation(SearchCriteria.Operation.EQUALS)
                .build();

        return getSearchSpecification(Collections.singletonList(searchCriteria), RequestDto.GlobalOperator.AND, "server");
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
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "Integer" -> getIntegerBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case "Long" -> getLongBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case "Double" -> getDoubleBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            case "LocalDateTime" -> getLocalDateTimeBetweenPredicate(cb, columnPath, lowerBound, upperBound);
            default ->
                    throw new IllegalArgumentException("Invalid operation: BETWEEN is only applicable to numeric and date-time column types.");
        };
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
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "Integer" -> cb.lessThan(columnPath.as(Integer.class), Integer.parseInt(value));
            case "Long" -> cb.lessThan(columnPath.as(Long.class), Long.parseLong(value));
            case "Double" -> cb.lessThan(columnPath.as(Double.class), Double.parseDouble(value));
            case "LocalDateTime" -> cb.lessThan(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value));
            default ->
                    throw new IllegalArgumentException("Invalid operation: LESS_THAN is only applicable to numeric and date-time column types.");
        };
    }


    private Predicate getGreaterThanPredicate(CriteriaBuilder cb, Path<?> columnPath, String value) {
        Class<?> columnType = columnPath.getJavaType();
        return switch (columnType.getSimpleName()) {
            case "Integer" -> cb.greaterThan(columnPath.as(Integer.class), Integer.parseInt(value));
            case "Long" -> cb.greaterThan(columnPath.as(Long.class), Long.parseLong(value));
            case "Double" -> cb.greaterThan(columnPath.as(Double.class), Double.parseDouble(value));
            case "LocalDateTime" -> cb.greaterThan(columnPath.as(LocalDateTime.class), LocalDateTime.parse(value));
            default ->
                    throw new IllegalArgumentException("Invalid operation: GREATER_THAN is only applicable to numeric and date-time column types.");
        };
    }

    private Path<?> getColumnPath(Root<T> root, SearchCriteria searchCriteria, String joinTable) {
        if (searchCriteria == null) {
            throw new IllegalArgumentException("You must provide valid search criteria");
        }
        if (hasText(joinTable)) {
            return root.join(joinTable).get(searchCriteria.column());
        }
        return root.get(searchCriteria.column());
    }

    private Predicate getLocalDateTimeBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(LocalDateTime.class), LocalDateTime.parse(lowerBound), LocalDateTime.parse(upperBound));
    }

    private Predicate getDoubleBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Double.class), Double.parseDouble(lowerBound), Double.parseDouble(upperBound));
    }

    private Predicate getLongBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Long.class), Long.parseLong(lowerBound), Long.parseLong(upperBound));
    }

    private Predicate getIntegerBetweenPredicate(CriteriaBuilder cb, Path<?> columnPath, String lowerBound, String upperBound) {
        return cb.between(columnPath.as(Integer.class), Integer.parseInt(lowerBound), Integer.parseInt(upperBound));
    }
}




