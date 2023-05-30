package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record FilterRequest(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String serverName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String uniqueServerName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String region,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String faction,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Integer timeRange
) {
}




