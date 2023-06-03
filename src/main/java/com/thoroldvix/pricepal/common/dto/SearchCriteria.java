package com.thoroldvix.pricepal.common.dto;

import lombok.Builder;

@Builder
public record SearchCriteria(
        String column,
        String value,
        Operation operation,
        String joinTable,
        boolean joinOperation
) {
    public enum Operation {
        EQUALS, NOT_EQUALS, LIKE, GREATER_THAN, LESS_THAN, IN, BETWEEN, NOT_LIKE, EQUALS_IGNORE_CASE
    }
}
