package com.thoroldvix.pricepal.shared;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Request body for filtering")
public record RequestDto(
        @Schema(description = "Search criteria")
        SearchCriteria[] searchCriteria,

        @Schema(description = "Global operator for combining search criteria", allowableValues = {"AND", "OR", "NOT"})
        GlobalOperator globalOperator

) {
   public enum GlobalOperator {
        AND, OR, NOT
    }
}
