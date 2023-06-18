package com.thoroldvix.pricepal.shared;

import lombok.Builder;

@Builder
public record StatsResponse<T>(
        Number mean,
        Number median,
        T minimum,
        T maximum,
        long count
)  {

}
