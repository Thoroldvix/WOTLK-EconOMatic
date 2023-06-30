package com.thoroldvix.economatic.population;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.Filters;
import lombok.Builder;

import java.util.List;

@Builder
public record PopulationsResponse(
        @JsonUnwrapped
        Filters filters,
        List<PopulationResponse> populations
) {
}
