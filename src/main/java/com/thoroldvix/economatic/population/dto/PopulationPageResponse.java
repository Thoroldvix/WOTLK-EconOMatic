package com.thoroldvix.economatic.population.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.economatic.shared.dto.PaginationInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record PopulationPageResponse(
        @JsonUnwrapped
        PaginationInfo paginationInfo,

        List<PopulationResponse> populations
) {
}
