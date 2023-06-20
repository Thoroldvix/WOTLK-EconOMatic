package com.thoroldvix.pricepal.goldprice;

import lombok.Builder;

import java.util.List;

@Builder
public record GoldPriceServerResponse(
        String server,

        List<GoldPriceResponse> prices
) {
}
