package com.thoroldvix.pricepal.server.dto;

import lombok.Builder;

@Builder
public record StatsResponse<T>(
        Number average,
        T minimum,
        T maximum,
        long count
)  {

}
