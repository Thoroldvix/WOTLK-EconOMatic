package com.thoroldvix.economatic.population;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record PopulationPageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,

        List<PopulationResponse> populations
) {
}
