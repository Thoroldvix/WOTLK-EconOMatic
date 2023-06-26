package com.thoroldvix.economatic.goldprice;

import lombok.Builder;

import java.util.List;

@Builder
public record GoldPricesResponse(
        String faction,
        String server,
        String region,
        List<GoldPriceResponse> prices
) {
}
