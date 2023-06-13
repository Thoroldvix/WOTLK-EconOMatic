package com.thoroldvix.pricepal.shared;

import lombok.Builder;

@Builder
public record StatsResponse<T>(
        Number average,
        T minimum,
        T maximum,
        long count
)  {

}
