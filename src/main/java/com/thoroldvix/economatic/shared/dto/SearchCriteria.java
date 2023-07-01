package com.thoroldvix.economatic.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Search criteria for filtering")
public record SearchCriteria(
        @Schema(description = "Column itemName for the search criteria")
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
        BETWEEN,
        EQUALS_IGNORE_CASE
    }
}

