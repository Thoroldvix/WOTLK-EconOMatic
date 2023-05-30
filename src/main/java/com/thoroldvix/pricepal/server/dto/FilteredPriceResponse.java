package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
public record FilteredPriceResponse(
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        FilterRequest filters,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ServerPriceResponse avgPrice,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ServerPriceResponse medianPrice,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ServerPriceResponse minPrice,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ServerPriceResponse maxPrice,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ServerPriceResponse> prices

) {
}
