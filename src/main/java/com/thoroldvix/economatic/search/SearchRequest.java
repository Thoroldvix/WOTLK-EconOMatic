package com.thoroldvix.economatic.search;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Request body for filtering")
public record SearchRequest(
        @Schema(description = "Search criteria list for searching")
        @NotEmpty(message = "Search criteria cannot be null or empty")
        List<SearchCriteria> searchCriteria,

        @Schema(description = "Global operator for combining search criteria", allowableValues = {"AND", "OR", "NOT"})
        GlobalOperator globalOperator
) {

    public enum GlobalOperator {
        AND, OR, NOT
    }
}
