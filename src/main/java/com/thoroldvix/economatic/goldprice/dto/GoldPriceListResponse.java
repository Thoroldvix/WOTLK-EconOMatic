package com.thoroldvix.economatic.goldprice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GoldPriceListResponse(
        List<GoldPriceResponse> prices
) {
}
