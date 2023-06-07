package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopulationResponse(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String serverName,
        int population,
        LocalDateTime updatedAt
)  {

}
