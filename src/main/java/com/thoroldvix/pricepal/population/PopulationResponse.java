package com.thoroldvix.pricepal.population;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopulationResponse(
        long id,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String serverName,
        int population,
        LocalDateTime updatedAt
)  {

}
