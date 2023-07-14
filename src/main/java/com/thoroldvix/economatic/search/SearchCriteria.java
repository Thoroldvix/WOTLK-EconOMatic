package com.thoroldvix.economatic.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Search criteria for filtering")
public record SearchCriteria(
        @Schema(description = "Column name for the search criteria")
        String column,
        @Schema(description = "Value to compare against")
        String value,

        @Schema(description = "Name of table to perform join on")
        String joinTable,

        @Schema(description = "Operation to perform for the search criteria")
        Operation operation
) {

    public enum Operation {
        EQUALS,
        LIKE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUALS,
        LESS_THAN_OR_EQUALS,
        LESS_THAN,
        IN,
        BETWEEN_NUMERIC,
        BETWEEN_DATE_TIME,
        EQUALS_IGNORE_CASE,
        BEFORE,
        AFTER,
        BEFORE_OR_EQUALS,
        AFTER_OR_EQUALS
    }
}

