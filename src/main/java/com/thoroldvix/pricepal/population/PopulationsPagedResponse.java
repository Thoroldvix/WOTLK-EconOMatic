package com.thoroldvix.pricepal.population;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;

@Builder
public record PopulationsPagedResponse(
        int page,
        int pageSize,
        int totalPages,
        long totalElements,
        @JsonUnwrapped
        PopulationsResponse populationsResponse
) {
}
