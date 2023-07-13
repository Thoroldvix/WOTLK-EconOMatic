package com.thoroldvix.economatic.goldprice;

import lombok.Builder;

import java.util.List;

@Builder
public record GoldPriceListResponse(
        List<GoldPriceResponse> prices
) {
}
