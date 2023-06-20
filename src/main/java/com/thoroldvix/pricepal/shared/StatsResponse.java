package com.thoroldvix.pricepal.shared;

import lombok.Builder;

@Builder
public record StatsResponse<T>(
        String server,
        String region,
        String faction,
        Number mean,
        Number median,
        T minimum,
        T maximum,
        long count
)  {

}
