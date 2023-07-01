package com.thoroldvix.economatic.population.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.dto.Filters;
import lombok.Builder;

import java.util.List;

@Builder
public record PopulationsResponse(
        @JsonUnwrapped
        Filters filters,
        List<PopulationResponse> populations
) {
}
